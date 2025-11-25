package me.eplugins.eglow.event;

import java.util.Iterator;
import java.util.UUID;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.database.EGlowPlayerdataManager;
import me.eplugins.eglow.menu.OldMenu;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.packets.PacketUtil;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EGlowEventListener implements Listener {
   public EGlowEventListener() {
      EGlow.getInstance().getServer().getPluginManager().registerEvents(this, EGlow.getInstance());
      if (ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 13) {
         new EGlowEventListener113AndAbove();
      }

   }

   @EventHandler
   public void PlayerConnectEvent(PlayerJoinEvent event) {
      PlayerConnect(event.getPlayer(), event.getPlayer().getUniqueId());
   }

   @EventHandler
   public void PlayerKickedEvent(PlayerKickEvent event) {
      PlayerDisconnect(event.getPlayer());
   }

   @EventHandler
   public void PlayerDisconnectEvent(PlayerQuitEvent event) {
      PlayerDisconnect(event.getPlayer());
   }

   @EventHandler
   public void onMenuClick(InventoryClickEvent event) {
      InventoryHolder holder = event.getInventory().getHolder();
      if (holder != null) {
         if (holder instanceof OldMenu) {
            event.setCancelled(true);
            Inventory bottomInventory = NMSHook.getBottomInventory(event);
            if (bottomInventory == null || bottomInventory.equals(event.getClickedInventory()) || event.getCurrentItem() == null) {
               return;
            }

            OldMenu menu = (OldMenu)holder;
            menu.handleMenu(event);
         }

      }
   }

   @EventHandler
   public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
      Player player = event.getPlayer();
      EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
      if (eGlowPlayer != null) {
         if (eGlowPlayer.isInBlockedWorld()) {
            if (eGlowPlayer.isGlowing()) {
               eGlowPlayer.disableGlow(false);
               eGlowPlayer.setGlowDisableReason(EnumUtil.GlowDisableReason.BLOCKEDWORLD);
               ChatUtil.sendMsg(player, EGlowMessageConfig.Message.WORLD_BLOCKED.get(), true);
            }
         } else if (eGlowPlayer.getGlowDisableReason().equals(EnumUtil.GlowDisableReason.BLOCKEDWORLD) && eGlowPlayer.setGlowDisableReason(EnumUtil.GlowDisableReason.NONE).equals(EnumUtil.GlowDisableReason.NONE)) {
            eGlowPlayer.activateGlow();
            ChatUtil.sendMsg(player, EGlowMessageConfig.Message.WORLD_ALLOWED.get(), true);
         }
      }

   }

   public static void PlayerConnect(Player player, UUID uuid) {
      if (player.isGlowing()) {
         player.setGlowing(false);
      }

      EGlowPlayer eGlowPlayer = DataManager.addEGlowPlayer(player, uuid.toString());
      PacketUtil.handlePlayerJoin(eGlowPlayer);
      NMSHook.scheduleDelayedTask(true, 2L, () -> {
         EGlowPlayerdataManager.loadPlayerdata(eGlowPlayer);
         eGlowPlayer.updatePlayerTabname();
         if (!EGlow.getInstance().isUpToDate() && EGlowMainConfig.MainConfig.SETTINGS_NOTIFICATIONS_UPDATE.getBoolean() && player.hasPermission("eglow.option.update")) {
            Iterator var2 = EGlowMessageConfig.Message.UPDATE_MESSAGE.getStringList().iterator();

            while(var2.hasNext()) {
               String message = (String)var2.next();
               ChatUtil.sendPlainMsg(player, message.replace("%currentversion%", EGlow.getInstance().getDescription().getVersion()).replace("%latestversion%", EGlow.getInstance().getLatestVersion()), false);
            }
         }

         if (EGlowPlayerdataManager.getMySQL_Failed() && player.hasPermission("eglow.option.update")) {
            ChatUtil.sendPlainMsg(player, "&cMySQL failed to enable properly, have a look at this asap&f.", true);
         }

         if (eGlowPlayer.isGlowOnJoin() && player.hasPermission("eglow.option.glowonjoin")) {
            if (!eGlowPlayer.isActiveOnQuit() && eGlowPlayer.hasNoForceGlow()) {
               sendNoGlowMessage(eGlowPlayer);
            } else if (eGlowPlayer.getForcedEffect() == null && EGlowMainConfig.MainConfig.SETTINGS_JOIN_CHECK_PERMISSION.getBoolean() && !player.hasPermission(eGlowPlayer.getGlowEffect().getPermissionNode())) {
               sendNoGlowMessage(eGlowPlayer);
            } else {
               EnumUtil.GlowDisableReason glowDisableReason = eGlowPlayer.setGlowDisableReason(EnumUtil.GlowDisableReason.NONE);
               switch(glowDisableReason) {
               case BLOCKEDWORLD:
                  ChatUtil.sendMsg(player, EGlowMessageConfig.Message.WORLD_BLOCKED.get(), true);
                  break;
               case INVISIBLE:
                  ChatUtil.sendMsg(player, EGlowMessageConfig.Message.INVISIBILITY_BLOCKED.get(), true);
                  break;
               case ANIMATION:
                  ChatUtil.sendMsg(player, EGlowMessageConfig.Message.ANIMATION_BLOCKED.get(), true);
                  break;
               default:
                  if (eGlowPlayer.getGlowEffect() != null) {
                     eGlowPlayer.activateGlow();
                     if (EGlowMainConfig.MainConfig.SETTINGS_JOIN_MENTION_GLOW_STATE.getBoolean() && player.hasPermission("eglow.option.glowstate")) {
                        ChatUtil.sendMsg(player, EGlowMessageConfig.Message.GLOWING_STATE_ON_JOIN.get(eGlowPlayer.getGlowEffect().getDisplayName()), true);
                     }
                  } else {
                     sendNoGlowMessage(eGlowPlayer);
                  }
               }

            }
         } else {
            sendNoGlowMessage(eGlowPlayer);
         }
      });
      NMSHook.scheduleDelayedTask(true, 30L, () -> {
         PacketUtil.forceUpdateGlow(eGlowPlayer);
      });
   }

   public static void PlayerDisconnect(Player player) {
      EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
      if (eGlowPlayer != null) {
         PacketUtil.handlePlayerQuit(eGlowPlayer);
         NMSHook.scheduleTask(true, () -> {
            eGlowPlayer.setActiveOnQuit(eGlowPlayer.isGlowing());
            EGlowPlayerdataManager.savePlayerdata(eGlowPlayer);
            if (EGlow.getInstance().getAdvancedGlowVisibilityAddon() != null) {
               EGlow.getInstance().getAdvancedGlowVisibilityAddon().uncachePlayer(eGlowPlayer.getUuid());
            }

         });
         DataManager.removeEGlowPlayer(eGlowPlayer.getPlayer());
      }
   }

   public static void PlayerServerShutDown(Player player) {
      EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
      if (eGlowPlayer != null) {
         PacketUtil.handlePlayerQuit(eGlowPlayer);
         eGlowPlayer.setActiveOnQuit(eGlowPlayer.isGlowing());
         EGlowPlayerdataManager.savePlayerdata(eGlowPlayer);
         if (EGlow.getInstance().getAdvancedGlowVisibilityAddon() != null) {
            EGlow.getInstance().getAdvancedGlowVisibilityAddon().uncachePlayer(eGlowPlayer.getUuid());
         }

         DataManager.removeEGlowPlayer(eGlowPlayer.getPlayer());
      }
   }

   private static void sendNoGlowMessage(EGlowPlayer eGlowPlayer) {
      if (EGlowMainConfig.MainConfig.SETTINGS_JOIN_MENTION_GLOW_STATE.getBoolean() && eGlowPlayer.hasPermission("eglow.option.glowstate")) {
         ChatUtil.sendMsg(eGlowPlayer.getPlayer(), EGlowMessageConfig.Message.NON_GLOWING_STATE_ON_JOIN.get(), true);
      }

   }
}

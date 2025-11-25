package me.eplugins.eglow.addon;

import java.util.Iterator;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.addon.tab.TABAddon;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.Dependency;
import me.eplugins.eglow.util.packets.PacketUtil;
import me.eplugins.eglow.util.packets.chat.EnumChatFormat;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.group.GroupDataRecalculateEvent;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.util.Tristate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsAddon extends AbstractAddonBase implements Listener {
   private LuckPerms api;
   private EventSubscription<UserDataRecalculateEvent> luckPermsSub;
   private EventSubscription<GroupDataRecalculateEvent> luckPermsSub2;

   public LuckPermsAddon(EGlow eGlowInstance) {
      super(eGlowInstance);
      RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
      if (provider != null) {
         this.api = (LuckPerms)provider.getProvider();
         EventBus eventBus = ((LuckPerms)provider.getProvider()).getEventBus();
         TABAddon tabAddon = this.getEGlowInstance().getTabAddon();
         VaultAddon vaultAddon = this.getEGlowInstance().getVaultAddon();
         this.luckPermsSub = eventBus.subscribe(UserDataRecalculateEvent.class, (event) -> {
            try {
               if (this.getEGlowInstance() == null || event.getUser().getUsername() == null) {
                  return;
               }

               NMSHook.scheduleDelayedTask(true, 20L, () -> {
                  EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(event.getUser().getUniqueId());
                  if (eGlowPlayer != null) {
                     boolean useTAB = tabAddon != null && tabAddon.isVersionSupported() && tabAddon.blockEGlowPackets();
                     boolean allowEGlowPacket = !Dependency.TAB_BRIDGE.isLoaded();
                     if (useTAB) {
                        tabAddon.updateTABPlayer(eGlowPlayer, eGlowPlayer.getActiveColor());
                     } else {
                        eGlowPlayer.updatePlayerTabname();
                        if (allowEGlowPacket) {
                           PacketUtil.updateScoreboardTeam(eGlowPlayer, eGlowPlayer.getTeamName(), (vaultAddon != null ? vaultAddon.getPlayerTagPrefix(eGlowPlayer) : "") + eGlowPlayer.getActiveColor(), vaultAddon != null ? vaultAddon.getPlayerTagSuffix(eGlowPlayer) : "", EnumChatFormat.valueOf(eGlowPlayer.getActiveColor().name()));
                        }
                     }

                  }
               });
            } catch (IllegalPluginAccessException var5) {
            }

         });
         this.luckPermsSub2 = eventBus.subscribe(GroupDataRecalculateEvent.class, (event) -> {
            try {
               if (EGlow.getInstance() == null) {
                  return;
               }

               NMSHook.scheduleDelayedTask(true, 20L, () -> {
                  boolean useTAB = tabAddon != null && tabAddon.isVersionSupported() && tabAddon.blockEGlowPackets();
                  boolean allowEGlowPacket = !Dependency.TAB_BRIDGE.isLoaded();
                  Iterator var4 = DataManager.getEGlowPlayers().iterator();

                  while(var4.hasNext()) {
                     EGlowPlayer eGlowPlayer = (EGlowPlayer)var4.next();
                     if (useTAB) {
                        tabAddon.updateTABPlayer(eGlowPlayer, eGlowPlayer.getActiveColor());
                     } else if (allowEGlowPacket) {
                        PacketUtil.updateScoreboardTeam(eGlowPlayer, eGlowPlayer.getTeamName(), (vaultAddon != null ? vaultAddon.getPlayerTagPrefix(eGlowPlayer) : "") + eGlowPlayer.getActiveColor(), vaultAddon != null ? vaultAddon.getPlayerTagSuffix(eGlowPlayer) : "", EnumChatFormat.valueOf(eGlowPlayer.getActiveColor().name()));
                     }
                  }

               });
            } catch (IllegalPluginAccessException var4) {
            }

         });
      }
   }

   private boolean hasPermission(Player player, String permission) {
      if (this.api == null) {
         return false;
      } else {
         User user = this.api.getPlayerAdapter(Player.class).getUser(player);
         Tristate result = user.data().contains(Node.builder(permission).build(), NodeEqualityPredicate.EXACT);
         return result.asBoolean();
      }
   }

   public void givePermission(Player player, String permission) {
      if (this.api != null) {
         if (!this.hasPermission(player, permission)) {
            User user = this.api.getPlayerAdapter(Player.class).getUser(player);
            user.data().add(Node.builder(permission).build());
            this.api.getUserManager().saveUser(user);
         }

      }
   }

   public void removePermission(Player player, String permission) {
      if (this.api != null) {
         if (this.hasPermission(player, permission)) {
            User user = this.api.getPlayerAdapter(Player.class).getUser(player);
            user.data().remove(Node.builder(permission).build());
            this.api.getUserManager().saveUser(user);
         }

      }
   }

   public void unload() {
      try {
         this.luckPermsSub.close();
         this.luckPermsSub2.close();
      } catch (NoClassDefFoundError var2) {
      }

   }
}

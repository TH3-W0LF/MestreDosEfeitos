package me.eplugins.eglow.menu.menus;

import java.util.Objects;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowEffect;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.menu.OldMenu;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.packets.PacketUtil;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class EGlowMainMenu extends OldMenu {
   public EGlowMainMenu(EGlowPlayer eGlowPlayer) {
      super(eGlowPlayer.getPlayer());
   }

   public String getMenuName() {
      return ChatUtil.translateColors(EGlowMainConfig.MainConfig.SETTINGS_GUI_ADD_PREFIX.getBoolean() ? EGlowMessageConfig.Message.GUI_TITLE.get() : EGlowMessageConfig.Message.PREFIX.get() + EGlowMessageConfig.Message.GUI_TITLE.get());
   }

   public void handleMenu(InventoryClickEvent event) {
      Player player = (Player)event.getWhoClicked();
      EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
      ClickType clickType = event.getClick();
      int clickedSlot = event.getSlot();
      if (System.currentTimeMillis() - this.getMenuMetadata().getLastClicked() < EGlowMainConfig.MainConfig.SETTINGS_GUIS_INTERACTION_DELAY.getLong()) {
         ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.GUI_COOLDOWN.get());
      } else {
         this.getMenuMetadata().setLastClicked(System.currentTimeMillis());
         switch(clickedSlot) {
         case 0:
            this.enableGlow(player, clickType, "red");
            break;
         case 1:
            this.enableGlow(player, clickType, "darkred");
            break;
         case 2:
            this.enableGlow(player, clickType, "gold");
            break;
         case 3:
            this.enableGlow(player, clickType, "yellow");
            break;
         case 4:
            this.enableGlow(player, clickType, "green");
            break;
         case 5:
            this.enableGlow(player, clickType, "darkgreen");
            break;
         case 6:
            this.enableGlow(player, clickType, "aqua");
            break;
         case 7:
            this.enableGlow(player, clickType, "darkaqua");
            break;
         case 8:
            this.enableGlow(player, clickType, "blue");
            break;
         case 9:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 29:
         case 33:
         default:
            return;
         case 10:
            this.enableGlow(player, clickType, "darkblue");
            break;
         case 11:
            this.enableGlow(player, clickType, "purple");
            break;
         case 12:
            this.enableGlow(player, clickType, "pink");
            break;
         case 13:
            this.enableGlow(player, clickType, "white");
            break;
         case 14:
            this.enableGlow(player, clickType, "gray");
            break;
         case 15:
            this.enableGlow(player, clickType, "darkgray");
            break;
         case 16:
            this.enableGlow(player, clickType, "black");
            break;
         case 22:
            this.enableGlow(player, clickType, "rainbow");
            break;
         case 28:
            if (eGlowPlayer.skipSaveData()) {
               eGlowPlayer.setSaveData(true);
            }

            eGlowPlayer.setGlowOnJoin(!eGlowPlayer.isGlowOnJoin());
            break;
         case 30:
            if (eGlowPlayer.getPlayer().hasPermission("eglow.command.toggle")) {
               if (eGlowPlayer.isGlowing()) {
                  eGlowPlayer.disableGlow(false);
                  ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.DISABLE_GLOW.get());
               } else {
                  if (eGlowPlayer.getGlowEffect() == null || eGlowPlayer.getGlowEffect().getName().equals("none")) {
                     ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_LAST_GLOW.get());
                     return;
                  }

                  switch(eGlowPlayer.getGlowDisableReason()) {
                  case BLOCKEDWORLD:
                     ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.WORLD_BLOCKED.get());
                     return;
                  case INVISIBLE:
                     ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.INVISIBILITY_BLOCKED.get());
                     return;
                  case ANIMATION:
                     ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.ANIMATION_BLOCKED.get());
                     return;
                  default:
                     EGlowEffect currentEGlowEffect = eGlowPlayer.getGlowEffect();
                     if (!eGlowPlayer.hasPermission(currentEGlowEffect.getPermissionNode()) && (!DataManager.isCustomEffect(currentEGlowEffect.getName()) || !eGlowPlayer.hasPermission("eglow.egloweffect.*")) && !eGlowPlayer.isForcedGlow(currentEGlowEffect)) {
                        ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_PERMISSION.get());
                        return;
                     }

                     eGlowPlayer.activateGlow();
                     ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NEW_GLOW.get(eGlowPlayer.getLastGlowName()));
                  }
               }
            } else {
               ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_PERMISSION.get());
            }
            break;
         case 31:
            if (!eGlowPlayer.hasPermission("eglow.command.visibility")) {
               ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_PERMISSION.get());
               return;
            }

            switch(eGlowPlayer.getGlowVisibility()) {
            case ALL:
               eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.OTHER);
               break;
            case OTHER:
               eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.OWN);
               break;
            case OWN:
               eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.NONE);
               break;
            case NONE:
               eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.ALL);
            }

            PacketUtil.forceUpdateGlow(eGlowPlayer);
            ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.VISIBILITY_CHANGE.get(eGlowPlayer.getGlowVisibility().name()));
            break;
         case 32:
            if (this.hasEffect(eGlowPlayer)) {
               this.updateSpeed(eGlowPlayer);
            }
            break;
         case 34:
            if (EGlowMainConfig.MainConfig.SETTINGS_GUI_CUSTOM_EFFECTS.getBoolean()) {
               (new EGlowEffectsMenu(eGlowPlayer.getPlayer())).openInventory();
            }
         }

         this.UpdateMainNavigationBar(eGlowPlayer);
      }
   }

   public void setMenuItems() {
      NMSHook.scheduleTask(true, () -> {
         EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(this.getMenuMetadata().getOwner());
         this.UpdateMainNavigationBar(eGlowPlayer);
         this.getInventory().setItem(0, this.createLeatherColor(eGlowPlayer, "red", 255, 85, 85));
         this.getInventory().setItem(1, this.createLeatherColor(eGlowPlayer, "dark-red", 170, 0, 0));
         this.getInventory().setItem(2, this.createLeatherColor(eGlowPlayer, "gold", 255, 170, 0));
         this.getInventory().setItem(3, this.createLeatherColor(eGlowPlayer, "yellow", 255, 255, 85));
         this.getInventory().setItem(4, this.createLeatherColor(eGlowPlayer, "green", 85, 255, 85));
         this.getInventory().setItem(5, this.createLeatherColor(eGlowPlayer, "dark-green", 0, 170, 0));
         this.getInventory().setItem(6, this.createLeatherColor(eGlowPlayer, "aqua", 85, 255, 255));
         this.getInventory().setItem(7, this.createLeatherColor(eGlowPlayer, "dark-aqua", 0, 170, 170));
         this.getInventory().setItem(8, this.createLeatherColor(eGlowPlayer, "blue", 85, 85, 255));
         this.getInventory().setItem(10, this.createLeatherColor(eGlowPlayer, "dark-blue", 0, 0, 170));
         this.getInventory().setItem(11, this.createLeatherColor(eGlowPlayer, "purple", 170, 0, 170));
         this.getInventory().setItem(12, this.createLeatherColor(eGlowPlayer, "pink", 255, 85, 255));
         this.getInventory().setItem(13, this.createLeatherColor(eGlowPlayer, "white", 255, 255, 255));
         this.getInventory().setItem(14, this.createLeatherColor(eGlowPlayer, "gray", 170, 170, 170));
         this.getInventory().setItem(15, this.createLeatherColor(eGlowPlayer, "dark-gray", 85, 85, 85));
         this.getInventory().setItem(16, this.createLeatherColor(eGlowPlayer, "black", 0, 0, 0));
         this.getInventory().setItem(22, this.createItem(Material.NETHER_STAR, EGlowMessageConfig.Message.GUI_COLOR.get("effect-rainbow"), 0, new String[]{EGlowMessageConfig.Message.GUI_LEFT_CLICK.get() + EGlowMessageConfig.Message.COLOR.get("effect-rainbow"), EGlowMessageConfig.Message.GUI_EFFECT_PERMISSION.get() + (eGlowPlayer.getPlayer().hasPermission(((EGlowEffect)Objects.requireNonNull(DataManager.getEGlowEffect("rainbowslow"), "Unable to retrieve effect from given name")).getPermissionNode()) ? EGlowMessageConfig.Message.GUI_YES.get() : EGlowMessageConfig.Message.GUI_NO.get())}));
         NMSHook.scheduleTask(false, () -> {
            this.getMenuMetadata().getOwner().openInventory(this.getInventory());
         });
      });
   }
}

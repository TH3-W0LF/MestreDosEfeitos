package me.eplugins.eglow.menu.menus;

import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.menu.OldMenu;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class EGlowCustomEffectMenu extends OldMenu {
   public EGlowCustomEffectMenu(Player player) {
      super(player);
   }

   public String getMenuName() {
      return ChatUtil.translateColors(EGlowMainConfig.MainConfig.SETTINGS_GUI_ADD_PREFIX.getBoolean() ? EGlowMessageConfig.Message.GUI_TITLE.get() : EGlowMessageConfig.Message.PREFIX.get() + EGlowMessageConfig.Message.GUI_TITLE.get());
   }

   public void handleMenu(InventoryClickEvent e) {
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
         NMSHook.scheduleTask(false, () -> {
            this.getMenuMetadata().getOwner().openInventory(this.getInventory());
         });
      });
   }
}

package me.eplugins.eglow.menu;

import java.util.Objects;
import lombok.Generated;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.custommenu.menu.manager.MenuManager;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowEffect;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.menu.manager.OldMenuItemManager;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class OldMenu extends OldMenuItemManager implements InventoryHolder {
   protected final MenuManager.MenuMetadata menuMetadata;
   protected Inventory inventory;

   public OldMenu(Player player) {
      this.menuMetadata = this.getMenuMetadata(player);
   }

   public abstract String getMenuName();

   public abstract void handleMenu(InventoryClickEvent var1);

   public abstract void setMenuItems();

   public void openInventory() {
      this.inventory = Bukkit.createInventory(this, 36, this.getMenuName());
      this.setMenuItems();
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   public void enableGlow(Player player, ClickType clickType, String effectName) {
      EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
      EGlowEffect color;
      if (clickType.equals(ClickType.LEFT)) {
         if (DataManager.getEGlowEffect(effectName) != null) {
            color = DataManager.getEGlowEffect(effectName);
            if (color == null) {
               return;
            }

            if (!player.hasPermission(color.getPermissionNode()) && (!DataManager.isCustomEffect(color.getName()) || !((Player)Objects.requireNonNull(player.getPlayer(), "Unable to retrieve player")).hasPermission("eglow.effect.*"))) {
               ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_PERMISSION.get());
               return;
            }

            if (eGlowPlayer.isSameGlow(color)) {
               ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.SAME_GLOW.get());
               return;
            }

            eGlowPlayer.activateGlow(color);
            ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NEW_GLOW.get(color.getDisplayName()));
         } else if (DataManager.getEGlowEffect(effectName + "slow") != null) {
            color = DataManager.getEGlowEffect(effectName + "slow");
            if (!player.hasPermission(((EGlowEffect)Objects.requireNonNull(color, "Unable to retrieve effect from given name")).getPermissionNode())) {
               ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_PERMISSION.get());
               return;
            }

            if (eGlowPlayer.isSameGlow(color)) {
               ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.SAME_GLOW.get());
               return;
            }

            eGlowPlayer.activateGlow(color);
            ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NEW_GLOW.get(color.getDisplayName()));
         }
      } else if (clickType.equals(ClickType.RIGHT)) {
         color = DataManager.getEGlowEffect("blink" + effectName + "slow");
         if (color == null) {
            return;
         }

         if (!player.hasPermission(color.getPermissionNode())) {
            ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_PERMISSION.get());
            return;
         }

         if (eGlowPlayer.isSameGlow(color)) {
            ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.SAME_GLOW.get());
            return;
         }

         eGlowPlayer.activateGlow(color);
         ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NEW_GLOW.get(color.getDisplayName()));
      }

   }

   public void updateSpeed(EGlowPlayer eGlowPlayer) {
      if (eGlowPlayer.getGlowEffect() != null) {
         String effect = eGlowPlayer.getGlowEffect().getName();
         EGlowEffect eGlowEffect = null;
         if (effect.contains("slow")) {
            eGlowEffect = DataManager.getEGlowEffect(effect.replace("slow", "fast"));
         }

         if (effect.contains("fast")) {
            eGlowEffect = DataManager.getEGlowEffect(effect.replace("fast", "slow"));
         }

         eGlowPlayer.activateGlow(eGlowEffect);
         ChatUtil.sendMsgFromGUI(this.getMenuMetadata().getOwner(), EGlowMessageConfig.Message.NEW_GLOW.get(((EGlowEffect)Objects.requireNonNull(eGlowEffect, "Unable to get displayname from effect")).getDisplayName()));
      }

   }

   public void UpdateMainNavigationBar(EGlowPlayer eGlowPlayer) {
      if (EGlowMainConfig.MainConfig.SETTINGS_GUI_ADD_GLASS_PANES.getBoolean()) {
         this.getInventory().setItem(27, this.createItem(Material.valueOf(this.GLASS_PANE), "&f", 5, new String[]{""}));
         this.getInventory().setItem(29, this.createItem(Material.valueOf(this.GLASS_PANE), "&f", 5, new String[]{""}));
         this.getInventory().setItem(32, this.createItem(Material.valueOf(this.GLASS_PANE), "&f", 5, new String[]{""}));
         this.getInventory().setItem(33, this.createItem(Material.valueOf(this.GLASS_PANE), "&f", 5, new String[]{""}));
         this.getInventory().setItem(34, this.createItem(Material.valueOf(this.GLASS_PANE), "&f", 5, new String[]{""}));
         this.getInventory().setItem(35, this.createItem(Material.valueOf(this.GLASS_PANE), "&f", 5, new String[]{""}));
      }

      this.getInventory().setItem(28, this.createPlayerSkull(eGlowPlayer));
      this.getInventory().setItem(30, this.createGlowingStatus(eGlowPlayer));
      this.getInventory().setItem(31, this.createGlowVisibility(eGlowPlayer));
      if (this.hasEffect(eGlowPlayer)) {
         this.getInventory().setItem(32, this.createItem(Material.valueOf(this.CLOCK), EGlowMessageConfig.Message.GUI_SPEED_ITEM_NAME.get(), 0, this.createSpeedLore(eGlowPlayer)));
      }

      if (EGlowMainConfig.MainConfig.SETTINGS_GUI_CUSTOM_EFFECTS.getBoolean()) {
         this.getInventory().setItem(34, this.setItemGlow(this.createItem(Material.BOOK, EGlowMessageConfig.Message.GUI_CUSTOM_EFFECTS_ITEM_NAME.get(), 0, new String[]{EGlowMessageConfig.Message.GUI_CLICK_TO_OPEN.get()})));
      }

   }

   @Generated
   public MenuManager.MenuMetadata getMenuMetadata() {
      return this.menuMetadata;
   }
}

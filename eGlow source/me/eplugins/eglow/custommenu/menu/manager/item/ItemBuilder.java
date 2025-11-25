package me.eplugins.eglow.custommenu.menu.manager.item;

import java.util.List;
import lombok.Generated;
import me.eplugins.eglow.custommenu.menu.manager.item.helper.ItemHelper;
import me.eplugins.eglow.custommenu.menu.manager.item.helper.MetaHelper;
import me.eplugins.eglow.custommenu.menu.manager.item.helper.version.VersionHelper;
import me.eplugins.eglow.custommenu.menu.manager.item.util.MenuItemDataStorage;
import me.eplugins.eglow.util.DebugUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;

public class ItemBuilder {
   private final MenuItemDataStorage menuItemDataStorage;
   private ItemStack item;

   public ItemBuilder(MenuItemDataStorage menuItemDataStorage) {
      this.menuItemDataStorage = menuItemDataStorage;
   }

   public ItemBuilder addItemStack(ItemStack item) {
      this.item = item;
      return this;
   }

   public ItemStack build() {
      this.item = ItemHelper.getItemStackFromMaterial(this.getMenuItemDataStorage().getItemSection().getString("material", "MISSING_MATERIAL"), this.menuItemDataStorage);
      if (this.getItem() == null) {
         return null;
      } else {
         ItemMeta meta = this.getItem().getItemMeta();
         if (meta == null) {
            return this.getItem();
         } else {
            ConfigurationSection itemSection = this.getMenuItemDataStorage().getItemSection();
            String name = MetaHelper.getItemDisplayName(this.getMenuItemDataStorage());
            List<String> lore = MetaHelper.getItemLore(this.getMenuItemDataStorage());
            short data = MetaHelper.getItemData(this.getMenuItemDataStorage());
            int amount = MetaHelper.getItemAmount(this.getMenuItemDataStorage());
            int modeldata = MetaHelper.getLegacyModeldata(this.getMenuItemDataStorage());
            if (!name.isEmpty()) {
               meta.setDisplayName(name);
            }

            if (!lore.isEmpty()) {
               meta.setLore(lore);
            }

            if (DebugUtil.getMainVersion() <= 12 && data != 0) {
               this.getItem().setDurability(data);
            }

            this.getItem().setAmount(amount);
            if (modeldata != 0) {
               meta.setCustomModelData(modeldata);
            }

            this.getItem().setItemMeta(meta);
            if (VersionHelper.isAtLeast(21, 4)) {
               this.getItem().setItemMeta(VersionHelper.getMetaHelper1_21_4().getModeldata(meta, this.getMenuItemDataStorage()));
            }

            if (VersionHelper.getItemHelper1_14() != null && VersionHelper.getItemHelper1_14().isInstanceOfCrossbowMeta(meta)) {
               this.getItem().setItemMeta(VersionHelper.getItemHelper1_14().buildLoadedCrossbowMeta(meta, this.getMenuItemDataStorage()));
            }

            if (VersionHelper.getItemHelper1_20() != null && VersionHelper.getItemHelper1_20().isInstanceOfArmorMeta(meta)) {
               this.getItem().setItemMeta(VersionHelper.getItemHelper1_20().buildArmourTrimMeta(meta, this.getMenuItemDataStorage()));
            }

            if (meta instanceof BannerMeta) {
               this.getItem().setItemMeta(MetaHelper.buildBannerMeta(meta, this.getMenuItemDataStorage()));
            }

            if (meta instanceof LeatherArmorMeta) {
               this.getItem().setItemMeta(MetaHelper.buildLeatherMeta(meta, this.getMenuItemDataStorage()));
            }

            if (meta instanceof PotionMeta) {
               this.getItem().setItemMeta(MetaHelper.buildPotionMeta(meta, this.getMenuItemDataStorage()));
            }

            if (meta instanceof FireworkEffectMeta) {
               this.getItem().setItemMeta(MetaHelper.buildColoredFireworkStarMeta(meta, this.getMenuItemDataStorage()));
            }

            if (!itemSection.getStringList("enchantments").isEmpty()) {
               this.getItem().setItemMeta(MetaHelper.buildEnchantsMeta(meta, this.getMenuItemDataStorage()));
            }

            if (!itemSection.getStringList("item_flags").isEmpty()) {
               this.getItem().setItemMeta(MetaHelper.buildItemFlagsMeta(meta, this.getMenuItemDataStorage()));
            }

            if (itemSection.getBoolean("hide_enchantments")) {
               this.getItem().setItemMeta(MetaHelper.buildHideEnchantmentsMeta(meta));
            }

            if (itemSection.getBoolean("hide_attributes")) {
               this.getItem().setItemMeta(MetaHelper.buildHideAttributesMeta(meta));
            }

            if (itemSection.getBoolean("hide_effects")) {
               this.getItem().setItemMeta(MetaHelper.buildHidePotionEffectsMeta(meta));
            }

            if (itemSection.getBoolean("hide_unbreakable")) {
               this.getItem().setItemMeta(MetaHelper.buildHideUnbreakableMeta(meta));
            }

            if (itemSection.getBoolean("unbreakable")) {
               this.getItem().setItemMeta(MetaHelper.buildUnbreakableMeta(meta));
            }

            return this.getItem();
         }
      }
   }

   public ItemStack update() {
      ItemStack itemToUpdate = this.getItem();
      ItemMeta meta = this.getItem().getItemMeta();
      if (meta == null) {
         return this.getItem();
      } else {
         meta.setDisplayName(MetaHelper.getItemDisplayName(this.getMenuItemDataStorage()));
         meta.setLore(MetaHelper.getItemLore(this.getMenuItemDataStorage()));
         itemToUpdate.setItemMeta(meta);
         return itemToUpdate;
      }
   }

   @Generated
   public MenuItemDataStorage getMenuItemDataStorage() {
      return this.menuItemDataStorage;
   }

   @Generated
   public ItemStack getItem() {
      return this.item;
   }
}

package me.eplugins.eglow.custommenu.menu.manager.item.helper.version;

import me.eplugins.eglow.custommenu.menu.manager.item.util.MenuItemDataStorage;
import me.eplugins.eglow.custommenu.util.TextUtil;
import me.eplugins.eglow.util.DebugUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemHelper1_14 {
   public boolean isInstanceOfCrossbowMeta(ItemMeta meta) {
      return meta instanceof CrossbowMeta;
   }

   public ItemMeta buildLoadedCrossbowMeta(ItemMeta meta, MenuItemDataStorage menuItemDataStorage) {
      String material = menuItemDataStorage.getItemSection().getString("crossbow_ammo");
      if (DebugUtil.getMainVersion() >= 14) {
         CrossbowMeta crossbowMeta = (CrossbowMeta)meta;

         try {
            crossbowMeta.addChargedProjectile(new ItemStack(Material.valueOf(material)));
            return crossbowMeta;
         } catch (IllegalArgumentException var6) {
            TextUtil.sendToConsole("&cInvalid crossbow ammo material&f: &e" + material + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
            return crossbowMeta;
         }
      } else {
         return meta;
      }
   }
}

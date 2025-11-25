package me.eplugins.eglow.custommenu.menu.manager.item.helper.version;

import java.util.Iterator;
import java.util.List;
import me.eplugins.eglow.custommenu.menu.manager.item.util.MenuItemDataStorage;
import me.eplugins.eglow.custommenu.util.TextUtil;
import me.eplugins.eglow.util.DebugUtil;
import org.bukkit.Registry;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class ItemHelper1_20 {
   public boolean isInstanceOfArmorMeta(ItemMeta meta) {
      return meta instanceof ArmorMeta;
   }

   public ItemMeta buildArmourTrimMeta(ItemMeta meta, MenuItemDataStorage menuItemDataStorage) {
      List<String> armorTrimList = menuItemDataStorage.getItemSection().getStringList("amor_trims");
      if (DebugUtil.getMainVersion() >= 20) {
         ArmorMeta armorMeta = (ArmorMeta)meta;
         Iterator var5 = armorTrimList.iterator();

         while(var5.hasNext()) {
            String trim = (String)var5.next();
            String[] trimData = trim.contains(":") ? trim.split(":") : new String[]{"", trim};
            TrimPattern trimPattern = (TrimPattern)Registry.TRIM_PATTERN.match("minecraft:" + trimData[0]);
            TrimMaterial trimMaterial = (TrimMaterial)Registry.TRIM_MATERIAL.match("minecraft:" + trimData[1]);
            if (trimPattern == null) {
               TextUtil.sendToConsole("&cInvalid armor trim pattern&f: &e" + trimData[0] + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
            } else if (trimMaterial == null) {
               TextUtil.sendToConsole("&cInvalid armor trim material&f: &e" + trimData[1] + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
            } else {
               armorMeta.setTrim(new ArmorTrim(trimMaterial, trimPattern));
            }
         }

         return armorMeta;
      } else {
         return meta;
      }
   }
}

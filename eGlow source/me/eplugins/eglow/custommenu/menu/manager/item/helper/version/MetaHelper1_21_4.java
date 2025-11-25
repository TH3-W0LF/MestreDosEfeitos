package me.eplugins.eglow.custommenu.menu.manager.item.helper.version;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.eplugins.eglow.custommenu.menu.manager.item.util.MenuItemDataStorage;
import me.eplugins.eglow.custommenu.util.TextUtil;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

public class MetaHelper1_21_4 {
   public ItemMeta getModeldata(ItemMeta meta, MenuItemDataStorage menuItemDataStorage) {
      ConfigurationSection itemSection = menuItemDataStorage.getItemSection();
      CustomModelDataComponent customModelDataComponent = meta.getCustomModelDataComponent();
      if (!itemSection.isConfigurationSection("model_data")) {
         return meta;
      } else {
         List<Color> colors = new ArrayList();
         List<Boolean> flags = new ArrayList();
         List<Float> floats = new ArrayList();
         List<String> strings = new ArrayList();
         Iterator var9 = itemSection.getStringList("model_data.colors").iterator();

         String rawString;
         while(var9.hasNext()) {
            rawString = (String)var9.next();

            try {
               Color colorValue = this.getRGBColor(rawString, menuItemDataStorage);
               colors.add(colorValue);
            } catch (IllegalArgumentException var14) {
               TextUtil.sendToConsole("&cInvalid color for item modeldata&f: &e" + rawString + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
            }
         }

         var9 = itemSection.getStringList("model_data.flags").iterator();

         while(var9.hasNext()) {
            rawString = (String)var9.next();
            flags.add(Boolean.parseBoolean(rawString));
         }

         var9 = itemSection.getStringList("model_data.floats").iterator();

         while(var9.hasNext()) {
            rawString = (String)var9.next();

            try {
               float floatValue = Float.parseFloat(rawString);
               floats.add(floatValue);
            } catch (NullPointerException | NumberFormatException var13) {
               TextUtil.sendToConsole("&cInvalid float for item modeldata&f: &e" + rawString + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
            }
         }

         var9 = itemSection.getStringList("model_data.strings").iterator();

         while(var9.hasNext()) {
            rawString = (String)var9.next();
            if (rawString != null && !rawString.isEmpty()) {
               strings.add(rawString);
            }
         }

         customModelDataComponent.setColors(colors);
         customModelDataComponent.setFlags(flags);
         customModelDataComponent.setFloats(floats);
         customModelDataComponent.setStrings(strings);
         meta.setCustomModelDataComponent(customModelDataComponent);
         return meta;
      }
   }

   private Color getRGBColor(String rgb, MenuItemDataStorage menuItemDataStorage) {
      String[] rgbValues = rgb.replace(" ", "").split(",");
      if (rgbValues.length != 3) {
         TextUtil.sendToConsole("&cIncomplete rgb for item modeldata&f: &e" + rgb + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
         return Color.fromRGB(0, 0, 0);
      } else {
         int red;
         int green;
         int blue;
         try {
            red = Integer.parseInt(rgbValues[0]);
            green = Integer.parseInt(rgbValues[1]);
            blue = Integer.parseInt(rgbValues[2]);
         } catch (NumberFormatException var10) {
            TextUtil.sendToConsole("&cInvalid rgb for item modeldata&f: &e" + rgb + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
            return Color.fromRGB(0, 0, 0);
         }

         try {
            Color color = Color.fromRGB(red, green, blue);
            return color;
         } catch (IllegalArgumentException var9) {
            TextUtil.sendToConsole("&cInvalid rgb for item modeldata&f: &e" + rgb + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
            return Color.fromRGB(0, 0, 0);
         }
      }
   }
}

package me.eplugins.eglow.custommenu.menu.manager.item.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.eplugins.eglow.custommenu.menu.manager.item.util.MenuItemDataStorage;
import me.eplugins.eglow.custommenu.util.TextUtil;
import org.bukkit.configuration.ConfigurationSection;

public class MenuHelper {
   public static int getPriority(MenuItemDataStorage menuItemDataStorage) {
      return menuItemDataStorage.getItemSection().getInt("priority", Integer.MAX_VALUE);
   }

   public static List<Integer> getSlots(MenuItemDataStorage menuItemDataStorage) {
      ConfigurationSection itemSection = menuItemDataStorage.getItemSection();
      List<Integer> slots = new ArrayList();
      if (itemSection.getInt("slot", -1) != -1) {
         slots.add(itemSection.getInt("slot", -1));
      } else if (!itemSection.getStringList("slots").isEmpty()) {
         Iterator var3 = itemSection.getStringList("slots").iterator();

         while(true) {
            while(var3.hasNext()) {
               String slotArray = (String)var3.next();
               if (!slotArray.contains("-")) {
                  try {
                     slots.add(Integer.valueOf(slotArray));
                  } catch (NumberFormatException var7) {
                     TextUtil.sendToConsole("&cInvalid slot&f: &e" + slotArray + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
                  }
               } else {
                  try {
                     String[] slotRange = slotArray.split("-");

                     for(int i = Integer.parseInt(slotRange[0]); i <= Integer.parseInt(slotRange[1]); ++i) {
                        slots.add(i);
                     }
                  } catch (NumberFormatException var8) {
                     TextUtil.sendToConsole("&cInvalid slot range&f: &e" + slotArray + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
                  }
               }
            }

            return slots;
         }
      }

      return slots;
   }
}

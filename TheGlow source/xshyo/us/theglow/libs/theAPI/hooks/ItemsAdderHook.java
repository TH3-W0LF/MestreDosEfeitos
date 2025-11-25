package xshyo.us.theglow.libs.theAPI.hooks;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ItemsAdderHook implements ItemHook {
   private final boolean isItemsAdderAvailable;

   public ItemsAdderHook() {
      Plugin var1 = Bukkit.getPluginManager().getPlugin("ItemsAdder");
      this.isItemsAdderAvailable = var1 != null && var1.isEnabled();
   }

   public ItemStack getItem(String... var1) {
      if (var1.length != 0 && this.isItemsAdderAvailable) {
         try {
            CustomStack var2 = CustomStack.getInstance(var1[0]);
            return var2 != null ? var2.getItemStack() : new ItemStack(Material.STONE, 1);
         } catch (Exception var3) {
            return new ItemStack(Material.STONE, 1);
         }
      } else {
         return new ItemStack(Material.STONE, 1);
      }
   }

   public String getPrefix() {
      return "itemsadder-";
   }

   public String getPluginName() {
      return "ItemsAdder";
   }
}

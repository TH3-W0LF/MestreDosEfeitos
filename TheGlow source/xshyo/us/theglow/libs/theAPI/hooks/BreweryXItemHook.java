package xshyo.us.theglow.libs.theAPI.hooks;

import com.dre.brewery.api.BreweryApi;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class BreweryXItemHook implements ItemHook {
   private final boolean isBreweryAvailable;

   public BreweryXItemHook() {
      Plugin var1 = Bukkit.getPluginManager().getPlugin("BreweryX");
      this.isBreweryAvailable = var1 != null && var1.isEnabled();
   }

   public ItemStack getItem(String... var1) {
      if (var1.length == 0) {
         return new ItemStack(Material.STONE, 1);
      } else if (!this.isBreweryAvailable) {
         return this.createMissingPluginItem();
      } else {
         try {
            String[] var2 = var1[0].split(":");
            String var3 = var2[0];
            int var4 = var2.length > 1 ? this.parseQuality(var2[1]) : 5;
            ItemStack var5 = BreweryApi.createBrewItem(var3, var4);
            return var5 != null ? var5 : new ItemStack(Material.GLASS_BOTTLE, 1);
         } catch (Exception var6) {
            return new ItemStack(Material.STONE, 1);
         }
      }
   }

   public String getPrefix() {
      return "brewery-";
   }

   public String getPluginName() {
      return "BreweryX";
   }

   private int parseQuality(String var1) {
      try {
         int var2 = Integer.parseInt(var1);
         if (var2 < 1) {
            return 1;
         } else {
            return var2 > 10 ? 10 : var2;
         }
      } catch (NumberFormatException var3) {
         return 5;
      }
   }
}

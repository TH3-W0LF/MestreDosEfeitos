package xshyo.us.theglow.libs.theAPI.hooks;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class HeadDataBaseHook implements ItemHook {
   private final HeadDatabaseAPI api;

   public HeadDataBaseHook() {
      Plugin var1 = Bukkit.getPluginManager().getPlugin("HeadDatabase");
      if (var1 != null && var1.isEnabled()) {
         this.api = new HeadDatabaseAPI();
      } else {
         this.api = null;
      }

   }

   public ItemStack getItem(String... var1) {
      if (var1.length == 0) {
         return new ItemStack(Material.STONE, 1);
      } else if (this.api != null) {
         try {
            ItemStack var2 = this.api.getItemHead(var1[0]);
            return var2 != null ? var2 : new ItemStack(Material.STONE, 1);
         } catch (Exception var3) {
            return new ItemStack(Material.STONE, 1);
         }
      } else {
         return new ItemStack(Material.STONE, 1);
      }
   }

   public String getPrefix() {
      return "hdb-";
   }

   public String getPluginName() {
      return "HeadDatabase";
   }
}

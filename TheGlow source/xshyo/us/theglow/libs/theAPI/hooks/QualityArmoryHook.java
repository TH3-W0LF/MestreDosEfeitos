package xshyo.us.theglow.libs.theAPI.hooks;

import me.zombie_striker.qg.api.QualityArmory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class QualityArmoryHook implements ItemHook {
   private final boolean isQualityArmoryAvailable;

   public QualityArmoryHook() {
      Plugin var1 = Bukkit.getPluginManager().getPlugin("QualityArmory");
      this.isQualityArmoryAvailable = var1 != null && var1.isEnabled();
   }

   public ItemStack getItem(String... var1) {
      if (var1.length != 0 && this.isQualityArmoryAvailable) {
         try {
            ItemStack var2 = QualityArmory.getCustomItemAsItemStack(var1[0]);
            return var2 != null ? var2 : new ItemStack(Material.STONE, 1);
         } catch (Exception var3) {
            return new ItemStack(Material.STONE, 1);
         }
      } else {
         return new ItemStack(Material.STONE, 1);
      }
   }

   public String getPrefix() {
      return "qualityarmory-";
   }

   public String getPluginName() {
      return "QualityArmory";
   }
}

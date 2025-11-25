package xshyo.us.theglow.libs.theAPI.hooks;

import com.google.gson.JsonSyntaxException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NamedHeadHook implements ItemHook {
   public ItemStack getItem(String... var1) {
      if (var1.length == 0) {
         return new ItemStack(Material.STONE, 1);
      } else {
         try {
            return this.getSkullByName(var1[0]).clone();
         } catch (JsonSyntaxException var3) {
            return new ItemStack(Material.STONE, 1);
         }
      }
   }

   public String getPrefix() {
      return "head-";
   }

   public String getPluginName() {
      return "N/A";
   }
}

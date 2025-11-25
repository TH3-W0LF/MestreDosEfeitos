package xshyo.us.theglow.libs.theAPI.hooks;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TextureHeadHook implements ItemHook {
   public ItemStack getItem(String... var1) {
      if (var1.length == 0) {
         return new ItemStack(Material.STONE, 1);
      } else {
         try {
            return this.getSkullByBase64EncodedTextureUrl(this.getEncoded(var1[0])).clone();
         } catch (Exception var3) {
            return new ItemStack(Material.STONE, 1);
         }
      }
   }

   public String getPrefix() {
      return "texture-";
   }

   public String getPluginName() {
      return "N/A";
   }
}

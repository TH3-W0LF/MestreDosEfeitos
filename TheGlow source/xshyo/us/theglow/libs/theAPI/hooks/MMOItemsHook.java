package xshyo.us.theglow.libs.theAPI.hooks;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class MMOItemsHook implements ItemHook {
   private final boolean isMMOItemsAvailable;

   public MMOItemsHook() {
      Plugin var1 = Bukkit.getPluginManager().getPlugin("MMOItems");
      this.isMMOItemsAvailable = var1 != null && var1.isEnabled();
   }

   public ItemStack getItem(String... var1) {
      if (var1.length == 0) {
         return new ItemStack(Material.STONE, 1);
      } else if (!this.isMMOItemsAvailable) {
         return this.createMissingPluginItem();
      } else {
         try {
            String[] var2 = var1[0].split(":");
            String var3 = var2[0].toUpperCase();
            String var4 = var2[1].toUpperCase();
            Type var5 = MMOItems.plugin.getTypes().get(var3);
            if (var5 == null) {
               return new ItemStack(Material.STONE, 1);
            } else {
               ItemStack var6 = MMOItems.plugin.getItem(var5, var4);
               return var6 != null ? var6 : new ItemStack(Material.STONE, 1);
            }
         } catch (Exception var7) {
            return new ItemStack(Material.STONE, 1);
         }
      }
   }

   public String getPrefix() {
      return "mmo-";
   }

   public String getPluginName() {
      return "MMOItems";
   }
}

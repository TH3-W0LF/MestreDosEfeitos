package xshyo.us.theglow.libs.theAPI.hooks;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class OraxenHook implements ItemHook {
   private final boolean isOraxenAvailable;

   public OraxenHook() {
      Plugin var1 = Bukkit.getPluginManager().getPlugin("Oraxen");
      this.isOraxenAvailable = var1 != null && var1.isEnabled();
   }

   public ItemStack getItem(String... var1) {
      if (var1.length != 0 && this.isOraxenAvailable) {
         ItemBuilder var2 = OraxenItems.getItemById(var1[0]);
         return var2 == null ? null : var2.build().clone();
      } else {
         return new ItemStack(Material.STONE, 1);
      }
   }

   public String getPrefix() {
      return "oraxen-";
   }

   public String getPluginName() {
      return "Oraxen";
   }
}

package xshyo.us.theglow.libs.theAPI.hooks;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ExecutableItemsHook implements ItemHook {
   private final boolean isExecutableAvailable;

   public ExecutableItemsHook() {
      Plugin var1 = Bukkit.getPluginManager().getPlugin("ExecutableItems");
      this.isExecutableAvailable = var1 != null && var1.isEnabled();
   }

   public ItemStack getItem(String... var1) {
      if (var1.length != 0 && this.isExecutableAvailable) {
         ItemStack var2 = (ItemStack)ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(var1[0]).map((var0) -> {
            return var0.buildItem(1, Optional.empty());
         }).orElse((Object)null);
         return var2 == null ? new ItemStack(Material.STONE) : var2.clone();
      } else {
         return new ItemStack(Material.STONE, 1);
      }
   }

   public String getPrefix() {
      return "executableitems-";
   }

   public String getPluginName() {
      return "ExecutableItems";
   }
}

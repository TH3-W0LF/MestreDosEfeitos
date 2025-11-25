package xshyo.us.theglow.libs.theAPI.hooks;

import com.ssomar.executableblocks.api.ExecutableBlocksAPI;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ExecutableBlocksHook implements ItemHook {
   private final boolean isExecutableAvailable;

   public ExecutableBlocksHook() {
      Plugin var1 = Bukkit.getPluginManager().getPlugin("ExecutableBlocks");
      this.isExecutableAvailable = var1 != null && var1.isEnabled();
   }

   public ItemStack getItem(String... var1) {
      if (var1.length != 0 && this.isExecutableAvailable) {
         ItemStack var2 = (ItemStack)ExecutableBlocksAPI.getExecutableBlocksManager().getExecutableBlock(var1[0]).map((var0) -> {
            return var0.buildItem(1, Optional.empty());
         }).orElse((Object)null);
         return var2 == null ? new ItemStack(Material.STONE) : var2.clone();
      } else {
         return new ItemStack(Material.STONE, 1);
      }
   }

   public String getPluginName() {
      return "ExecutableBlocks";
   }

   public String getPrefix() {
      return "executableblocks-";
   }
}

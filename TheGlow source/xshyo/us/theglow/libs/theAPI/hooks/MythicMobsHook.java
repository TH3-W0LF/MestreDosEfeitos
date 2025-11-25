package xshyo.us.theglow.libs.theAPI.hooks;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class MythicMobsHook implements ItemHook {
   private final boolean isMythicMobsAvailable;

   public MythicMobsHook() {
      Plugin var1 = Bukkit.getPluginManager().getPlugin("MythicMobs");
      this.isMythicMobsAvailable = var1 != null && var1.isEnabled();
   }

   public ItemStack getItem(String... var1) {
      if (var1.length != 0 && this.isMythicMobsAvailable) {
         try {
            ItemStack var2 = MythicBukkit.inst().getItemManager().getItemStack(var1[0]);
            return var2 != null ? var2 : new ItemStack(Material.STONE, 1);
         } catch (Exception var3) {
            return new ItemStack(Material.STONE, 1);
         }
      } else {
         return new ItemStack(Material.STONE, 1);
      }
   }

   public String getPrefix() {
      return "mythicmobs-";
   }

   public String getPluginName() {
      return "MythicMobs";
   }
}

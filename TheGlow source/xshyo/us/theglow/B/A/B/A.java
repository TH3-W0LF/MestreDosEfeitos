package xshyo.us.theglow.B.A.B;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;
import xshyo.us.theglow.libs.theAPI.utilities.item.ItemBuilder;

public class A extends xshyo.us.theglow.B.A.A {
   private final String N;

   public ItemStack B(Player var1) {
      Section var2 = TheGlow.getInstance().getMenus().getSection(this.N);
      if (var2 == null) {
         Bukkit.getLogger().warning("Configuration section '" + this.N + "' not found.");
         return new ItemStack(Material.BARRIER);
      } else {
         String var3 = var2.getString("material");
         var3 = Utils.setPAPI(var1, var3);
         if (var3 != null && !var3.isEmpty()) {
            String var4 = var2.getString("display_name");
            if (var4 == null) {
               var4 = "Item";
               Bukkit.getLogger().warning("Display name is null in item configuration at path: " + this.N);
            }

            List var5 = var2.getStringList("lore");
            ArrayList var6 = new ArrayList();
            if (var5 != null) {
               Iterator var7 = var5.iterator();

               while(var7.hasNext()) {
                  String var8 = (String)var7.next();
                  if (var8 != null) {
                     var6.add(var8);
                  }
               }
            }

            int var13 = var2.getInt("amount", 1);
            boolean var14 = var2.getBoolean("glowing", false);
            int var9 = var2.getInt("model_data", 0);
            Object var10 = var2.getStringList("item_flags");
            if (var10 == null) {
               var10 = new ArrayList();
            }

            try {
               return (new ItemBuilder(var3)).setName(var4).setLore((List)var6).setAmount(var13).setEnchanted(var14).addFlagsFromConfig(new HashSet((Collection)var10)).setCustomModelData(var9).build();
            } catch (Exception var12) {
               Bukkit.getLogger().severe("Error building button item at path '" + this.N + "': " + var12.getMessage());
               return new ItemStack(Material.BARRIER);
            }
         } else {
            Bukkit.getLogger().warning("Material is null or empty in item configuration at path: " + this.N);
            return new ItemStack(Material.STONE);
         }
      }
   }

   public void A(Player var1, int var2, ClickType var3, int var4) {
   }

   public A(String var1) {
      this.N = var1;
   }
}

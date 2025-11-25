package xshyo.us.theglow.B.A.C;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.MenuData;
import xshyo.us.theglow.data.PlayerGlowData;
import xshyo.us.theglow.enums.Filter;
import xshyo.us.theglow.enums.Order;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;
import xshyo.us.theglow.libs.theAPI.utilities.item.ItemBuilder;

public class C extends xshyo.us.theglow.B.A.A {
   private final TheGlow F = TheGlow.getInstance();
   private final Order H;
   private final Filter G;

   public ItemStack B(Player var1) {
      Section var2 = this.F.getMenus().getSection("inventories.glowing.items.filter");
      if (var2 == null) {
         Bukkit.getLogger().warning("Configuration section 'inventories.glowing.items.filter' not found.");
         return new ItemStack(Material.BARRIER);
      } else {
         String var3 = var2.getString("material");
         var3 = Utils.setPAPI(var1, var3);
         if (var3 != null && !var3.isEmpty()) {
            String var4 = var2.getString("display_name");
            if (var4 == null) {
               var4 = "Filter Item";
               Bukkit.getLogger().warning("Display name is null in filter item configuration.");
            }

            Object var5 = this.A(var2, this.G, var1);
            if (var5 == null) {
               var5 = new ArrayList();
               Bukkit.getLogger().warning("Lore for filter type '" + this.G + "' is null.");
            }

            String var6 = var2.getString("patterns", "");
            int var7 = var2.getInt("amount", 1);
            boolean var8 = var2.getBoolean("glowing", false);
            int var9 = var2.getInt("model_data", 0);
            Object var10 = var2.getStringList("item_flags");
            if (var10 == null) {
               var10 = new ArrayList();
            }

            try {
               return (new ItemBuilder(var3)).setName(var4).setLore((List)var5).setAmount(var7).setEnchanted(var8).addFlagsFromConfig(new HashSet((Collection)var10)).setCustomModelData(var9).build();
            } catch (Exception var12) {
               Bukkit.getLogger().severe("Error building filter button item: " + var12.getMessage());
               return new ItemStack(Material.BARRIER);
            }
         } else {
            Bukkit.getLogger().warning("Material is null or empty in filter item configuration.");
            return new ItemStack(Material.STONE);
         }
      }
   }

   private List<String> A(Section var1, Filter var2, Player var3) {
      String var4 = "lore." + var2.name().toLowerCase();
      List var5 = var1.getStringList("lore.all");
      List var6 = var1.getStringList(var4);
      if (var6.isEmpty()) {
         return var5;
      } else {
         ArrayList var7 = new ArrayList();
         Iterator var8 = var6.iterator();

         while(var8.hasNext()) {
            String var9 = (String)var8.next();
            var9 = Utils.colorize(var9);
            var7.add(var9);
         }

         return var7;
      }
   }

   public void A(Player var1, int var2, ClickType var3, int var4) {
      PlayerGlowData var5 = this.F.getDatabase().B(var1.getUniqueId());
      if (var5 != null && var5.getMenuData() != null) {
         MenuData var6 = var5.getMenuData();
         Filter var7 = var6.getFilter();
         HashMap var8 = new HashMap();
         if (var3 == ClickType.LEFT) {
            var8.put(Filter.ALL, Filter.AVAILABLE);
            var8.put(Filter.AVAILABLE, Filter.UNAVAILABLE);
            var8.put(Filter.UNAVAILABLE, Filter.ALL);
         } else {
            if (var3 != ClickType.RIGHT) {
               return;
            }

            var8.put(Filter.ALL, Filter.UNAVAILABLE);
            var8.put(Filter.AVAILABLE, Filter.ALL);
            var8.put(Filter.UNAVAILABLE, Filter.AVAILABLE);
         }

         Filter var9 = (Filter)var8.getOrDefault(var7, Filter.ALL);
         var6.setFilter(var9);
         this.F.getDatabase().C(var5.getUuid()).thenRun(() -> {
            Bukkit.getScheduler().runTask(this.F, () -> {
               (new xshyo.us.theglow.A.A(var1, this.H, var9, var5)).A(1);
            });
         });
      }
   }

   public C(Order var1, Filter var2) {
      this.H = var1;
      this.G = var2;
   }
}

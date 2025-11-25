package xshyo.us.theglow.B.A.C;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.CurrentGlow;
import xshyo.us.theglow.data.GlowCacheData;
import xshyo.us.theglow.data.PlayerGlowData;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;
import xshyo.us.theglow.libs.theAPI.utilities.item.ItemBuilder;

public class B extends xshyo.us.theglow.B.A.A {
   private final TheGlow C = TheGlow.getInstance();
   private final PlayerGlowData E;
   private final String D;
   private final GlowCacheData B;

   public ItemStack B(Player var1) {
      String var2 = this.B.getPermission();
      Section var3 = this.C.getMenus().getSection("inventories.glowing.items.glow");
      if (var3 == null) {
         Bukkit.getLogger().warning("Configuration section 'inventories.glowing.items.glow' not found.");
         return new ItemStack(Material.BARRIER);
      } else {
         String var4 = null;
         if (this.B.getMaterial() != null && !this.B.getMaterial().isEmpty()) {
            var4 = this.B.getMaterial();
         } else if (var3.contains("material")) {
            var4 = var3.getString("material");
         } else {
            Bukkit.getLogger().warning("Material not found in glow cache data or default configuration.");
            var4 = "STONE";
         }

         var4 = Utils.setPAPI(var1, var4);
         if (var4 != null && !var4.isEmpty()) {
            String var5 = "";
            if (var3.contains("display_name")) {
               var5 = var3.getString("display_name");
               if (var5 != null) {
                  var5 = var5.replace("{name}", this.B.getDisplayName() != null ? this.B.getDisplayName() : "Unknown");
               } else {
                  var5 = "Glow Item";
                  Bukkit.getLogger().warning("Display name is null in configuration.");
               }
            } else {
               var5 = "Glow Item";
               Bukkit.getLogger().warning("Display name not found in configuration.");
            }

            int var6 = 1;
            if (this.B.getAmount() > 0) {
               var6 = this.B.getAmount();
            } else if (var3.contains("amount")) {
               var6 = var3.getInt("amount", 1);
            }

            boolean var7 = this.B.isGlowing();
            int var8 = this.B.getModelData();
            Object var9 = this.B.getItem_flags();
            if (var9 == null) {
               var9 = new ArrayList();
            }

            String var10 = var2 != null && !var2.isEmpty() ? var2 : "none";
            boolean var11 = "none".equals(var10) || var1.hasPermission(var10);
            String var12 = var11 ? "available" : "unavailable";
            Object var13 = this.A(var3, var12, var1);
            if (var13 == null) {
               var13 = new ArrayList();
            }

            try {
               return (new ItemBuilder(var4)).setName(var5).setAmount(var6).setEnchanted(var7).setCustomModelData(var8).setLore((List)var13).addFlagsFromConfig(new HashSet((Collection)var9)).build();
            } catch (Exception var15) {
               Bukkit.getLogger().severe("Error building glow button item: " + var15.getMessage());
               return new ItemStack(Material.BARRIER);
            }
         } else {
            return new ItemStack(Material.STONE);
         }
      }
   }

   private List<String> A(Section var1, String var2, Player var3) {
      String var4 = this.B.getDisplayName() != null && !this.B.getDisplayName().isEmpty() ? this.B.getDisplayName() : "";
      if (this.E != null) {
         CurrentGlow var5 = this.E.getCurrentGlow();
         if (var5 != null && var5.getGlowName().equals(this.D)) {
            var2 = "selected";
         }
      }

      String var10 = "lore." + var2.toLowerCase();
      List var6 = var1.getStringList(var10);
      ArrayList var7 = new ArrayList();
      Iterator var8 = var6.iterator();

      while(var8.hasNext()) {
         String var9 = (String)var8.next();
         var9 = Utils.colorize(var9);
         var9 = Utils.setPAPI(var3, var9);
         var9 = var9.replace("{name}", var4);
         var7.add(var9);
      }

      return var7;
   }

   public void A(Player var1, int var2, ClickType var3, int var4) {
      String var5 = this.B.getDisplayName() != null && !this.B.getDisplayName().isEmpty() ? this.B.getDisplayName() : "";
      List var6 = this.B.getPatterns();
      String var7 = this.B.getPermission();
      String var8 = var7 != null && !var7.isEmpty() ? var7 : "none";
      boolean var9 = "none".equals(var8) || var1.hasPermission(var8);
      if (!var9) {
         xshyo.us.theglow.B.A.A((CommandSender)var1, "MESSAGES.GUI.NO_PERMISSION");
      } else {
         Iterator var10 = var6.iterator();

         String var11;
         do {
            if (!var10.hasNext()) {
               if (this.E == null) {
                  return;
               }

               CurrentGlow var12 = this.E.getCurrentGlow();
               if (var12.getGlowName().equals(this.D)) {
                  xshyo.us.theglow.B.A.A((CommandSender)var1, "MESSAGES.GUI.ALREADY_SELECTED");
                  return;
               }

               var12.setGlowName(this.D);
               var12.setColorList(var6);
               this.C.getDatabase().C(this.E.getUuid()).thenRun(() -> {
                  Bukkit.getScheduler().runTask(this.C, () -> {
                     this.C.getGlowManager().A(var1);
                     this.C.getGlowManager().A(var1, var6, 20L, false);
                     xshyo.us.theglow.B.A.A((CommandSender)var1, "MESSAGES.GUI.SELECTED", var5);
                  });
               });
               this.A(var1);
               return;
            }

            var11 = (String)var10.next();
         } while(xshyo.us.theglow.B.A.A(var11));

         xshyo.us.theglow.B.A.A((CommandSender)var1, "MESSAGES.GUI.INVALID_COLOR");
      }
   }

   public B(PlayerGlowData var1, String var2, GlowCacheData var3) {
      this.E = var1;
      this.D = var2;
      this.B = var3;
   }

   public String B() {
      return this.D;
   }

   public GlowCacheData A() {
      return this.B;
   }
}

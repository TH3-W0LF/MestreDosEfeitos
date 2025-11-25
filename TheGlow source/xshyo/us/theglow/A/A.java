package xshyo.us.theglow.A;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.B.A.C.B;
import xshyo.us.theglow.B.A.C.C;
import xshyo.us.theglow.B.A.C.D;
import xshyo.us.theglow.B.A.C.E;
import xshyo.us.theglow.data.GlowCacheData;
import xshyo.us.theglow.data.PlayerGlowData;
import xshyo.us.theglow.enums.Filter;
import xshyo.us.theglow.enums.Order;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.guis.builder.gui.SimpleBuilder;
import xshyo.us.theglow.libs.guis.guis.Gui;
import xshyo.us.theglow.libs.guis.guis.GuiItem;
import xshyo.us.theglow.libs.kyori.adventure.text.Component;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public class A {
   private final TheGlow C = TheGlow.getInstance();
   private final Gui H;
   private final Player F;
   private final Order D;
   private final Filter B;
   private final PlayerGlowData A;
   private static final String I = "inventories.glowing";
   private int G = 1;
   private int E;

   public A(Player var1, Order var2, Filter var3, PlayerGlowData var4) {
      this.F = var1;
      this.D = var2;
      this.B = var3;
      this.A = var4;
      this.H = this.C();
   }

   private Gui C() {
      int var1 = this.C.getMenus().getInt("inventories.glowing.size", 54);
      int var2;
      if (var1 % 9 == 0 && var1 >= 9 && var1 <= 54) {
         var2 = var1 / 9;
      } else {
         var2 = 6;
      }

      return ((SimpleBuilder)((SimpleBuilder)Gui.gui().title(Component.empty())).rows(var2)).create();
   }

   public int A() {
      String var1 = this.C.getMenus().getString("inventories.glowing.glows.slots");
      if (var1 != null && !var1.isEmpty()) {
         String[] var2 = var1.split(",");
         return var2.length;
      } else {
         return 28;
      }
   }

   public int[] B() {
      String var1 = this.C.getMenus().getString("inventories.glowing.glows.slots");
      int[] var2 = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
      if (var1 != null && !var1.isEmpty()) {
         String[] var3 = var1.split(",");
         int[] var4 = new int[var3.length];

         for(int var5 = 0; var5 < var3.length; ++var5) {
            var4[var5] = Integer.parseInt(var3[var5].trim());
         }

         return var4;
      } else {
         return var2;
      }
   }

   public void A(int var1) {
      this.G = var1;

      for(int var2 = 0; var2 < this.H.getRows() * 9; ++var2) {
         this.H.removeItem(var2);
      }

      HashSet var3 = new HashSet();
      this.D().thenAccept((var3x) -> {
         int var4 = this.A();
         int var5 = (var1 - 1) * var4;
         int var6 = Math.min(var5 + var4, var3x.size());
         int[] var7 = this.B();
         this.E = (int)Math.ceil((double)var3x.size() / (double)var4);
         if (this.E == 0) {
            this.E = 1;
         }

         int var8;
         for(var8 = var5; var8 < var6; ++var8) {
            B var9 = (B)var3x.get(var8);
            GlowCacheData var10 = var9.A();
            int var11 = var10.getSlot();
            if (var11 != -1 && !var3.contains(var11)) {
               GuiItem var12 = new GuiItem(var9.B(this.F), (var2) -> {
                  var9.A(this.F, var2.getSlot(), var2.getClick(), var2.getHotbarButton());
               });
               this.H.setItem(var11, var12);
               var3.add(var11);
            }
         }

         var8 = 0;

         for(int var14 = var5; var14 < var6; ++var14) {
            B var15 = (B)var3x.get(var14);
            GlowCacheData var16 = var15.A();
            int var17 = var16.getSlot();
            if (var17 == -1) {
               while(var8 < var7.length && var3.contains(var7[var8])) {
                  ++var8;
               }

               if (var8 < var7.length) {
                  GuiItem var13 = new GuiItem(var15.B(this.F), (var2) -> {
                     var15.A(this.F, var2.getSlot(), var2.getClick(), var2.getHotbarButton());
                  });
                  this.H.setItem(var7[var8], var13);
                  var3.add(var7[var8]);
                  ++var8;
               }
            }
         }

         if (this.G < this.E) {
            this.A("inventories.glowing.items.buttons.next", this.C, (var0) -> {
               return new xshyo.us.theglow.B.A.B.A("inventories.glowing.items.buttons.next");
            }, this.H.getRows()).forEach((var2, var3xx) -> {
               if (var3xx.B(this.F).getType() != Material.AIR) {
                  this.H.setItem(var2, new GuiItem(var3xx.B(this.F), (var1) -> {
                     this.A(this.G + 1);
                  }));
                  var3.add(var2);
               }

            });
         }

         if (this.G > 1) {
            this.A("inventories.glowing.items.buttons.previous", this.C, (var0) -> {
               return new xshyo.us.theglow.B.A.B.A("inventories.glowing.items.buttons.previous");
            }, this.H.getRows()).forEach((var2, var3xx) -> {
               if (var3xx.B(this.F).getType() != Material.AIR) {
                  this.H.setItem(var2, new GuiItem(var3xx.B(this.F), (var1) -> {
                     this.A(this.G - 1);
                  }));
                  var3.add(var2);
               }

            });
         }

         if (TheGlow.getInstance().getMenus().getBoolean("inventories.glowing.items.unEquip.enabled")) {
            this.A("inventories.glowing.items.unEquip", this.C, (var0) -> {
               return new xshyo.us.theglow.B.A.C.A();
            }, this.H.getRows()).forEach((var2, var3xx) -> {
               if (var3xx.B(this.F).getType() != Material.AIR) {
                  this.H.setItem(var2, new GuiItem(var3xx.B(this.F), (var2x) -> {
                     var3xx.A(this.F, var2x.getSlot(), var2x.getClick(), var2x.getHotbarButton());
                  }));
                  var3.add(var2);
               }

            });
         }

         if (TheGlow.getInstance().getMenus().getBoolean("inventories.glowing.items.filter.enabled")) {
            this.A("inventories.glowing.items.filter", this.C, (var1x) -> {
               return new C(this.D, this.B);
            }, this.H.getRows()).forEach((var2, var3xx) -> {
               if (var3xx.B(this.F).getType() != Material.AIR) {
                  this.H.setItem(var2, new GuiItem(var3xx.B(this.F), (var2x) -> {
                     var3xx.A(this.F, var2x.getSlot(), var2x.getClick(), var2x.getHotbarButton());
                  }));
                  var3.add(var2);
               }

            });
         }

         if (TheGlow.getInstance().getMenus().getBoolean("inventories.glowing.items.order.enabled")) {
            this.A("inventories.glowing.items.order", this.C, (var1x) -> {
               return new E(this.D, this.B);
            }, this.H.getRows()).forEach((var2, var3xx) -> {
               if (var3xx.B(this.F).getType() != Material.AIR) {
                  this.H.setItem(var2, new GuiItem(var3xx.B(this.F), (var2x) -> {
                     var3xx.A(this.F, var2x.getSlot(), var2x.getClick(), var2x.getHotbarButton());
                  }));
                  var3.add(var2);
               }

            });
         }

         this.A(var3);
         this.B(var3);
         this.H.update();
         this.H.setDefaultClickAction((var0) -> {
            var0.setCancelled(true);
         });
         Bukkit.getScheduler().runTask(this.C, () -> {
            this.H.updateTitle(Utils.translate(this.C.getMenus().getString("inventories.glowing.title", "&8☀ Glow Menu").replace("<totalpages>", "" + this.E).replace("<page>", "" + var1)));
            this.H.open(this.F);
         });
      }).exceptionally((var0) -> {
         var0.printStackTrace();
         return null;
      });
   }

   public CompletableFuture<Map<Integer, B>> D() {
      HashMap var1 = new HashMap();
      return CompletableFuture.supplyAsync(() -> {
         ArrayList var2 = new ArrayList();
         ArrayList var3 = new ArrayList();
         Map var4 = this.C.getGlowLoad().C();
         Iterator var5 = var4.entrySet().iterator();

         while(var5.hasNext()) {
            Entry var6 = (Entry)var5.next();
            String var7 = (String)var6.getKey();
            GlowCacheData var8 = (GlowCacheData)var6.getValue();
            String var9 = var8.getPermission();
            boolean var10 = "none".equals(var9) || this.F.hasPermission(var9);
            B var11 = new B(this.A, var7, var8);
            if (var10) {
               var2.add(var11);
            } else {
               var3.add(var11);
            }
         }

         ArrayList var12 = new ArrayList();
         if (this.B == Filter.AVAILABLE) {
            var12.addAll(var2);
         } else if (this.B == Filter.UNAVAILABLE) {
            var12.addAll(var3);
         } else {
            var12.addAll(var2);
            var12.addAll(var3);
         }

         Comparator var13 = Comparator.comparing(B::B);
         if (this.D == Order.Z_A) {
            var13 = var13.reversed();
         }

         var12.sort(var13);
         int var14 = 0;
         Iterator var15 = var12.iterator();

         while(var15.hasNext()) {
            B var16 = (B)var15.next();
            var1.put(var14++, var16);
         }

         return var1;
      }).exceptionally((var0) -> {
         var0.printStackTrace();
         return null;
      });
   }

   private void A(Set<Integer> var1) {
      Map var2 = this.A("inventories.glowing.custom-items", this.C, this.H.getRows());
      var2.forEach((var2x, var3) -> {
         if (!var1.contains(var2x)) {
            this.H.setItem(var2x, new GuiItem(var3.B(this.F), (var2) -> {
               var3.A(this.F, var2.getSlot(), var2.getClick(), var2.getHotbarButton());
            }));
            var1.add(var2x);
         }

      });
   }

   private void B(Set<Integer> var1) {
      for(int var2 = 0; var2 < this.H.getInventory().getSize(); ++var2) {
         if (!var1.contains(var2)) {
            this.H.setItem(var2, new GuiItem(new ItemStack(Material.AIR), (var0) -> {
               var0.setCancelled(true);
            }));
         }
      }

   }

   public <T extends xshyo.us.theglow.B.A.A> Map<Integer, T> A(String var1, TheGlow var2, Function<String, T> var3, int var4) {
      HashMap var5 = new HashMap();
      Section var6 = var2.getMenus().getSection(var1);
      if (var6 == null) {
         return var5;
      } else {
         try {
            String var7 = var6.getString("slot");
            if (var7 != null) {
               this.A((String)var7, var1, (Map)var5, (Function)var3, var4);
               return var5;
            }

            Object var8 = var6.get("slots");
            if (var8 == null) {
               return var5;
            }

            if (var8 instanceof String) {
               this.A((String)var8.toString(), var1, (Map)var5, (Function)var3, var4);
            } else if (var8 instanceof List) {
               List var9 = (List)var8;
               Iterator var10 = var9.iterator();

               while(var10.hasNext()) {
                  Object var11 = var10.next();
                  if (var11 != null) {
                     String var12 = var11.toString().trim();
                     this.A((String)var12, var1, (Map)var5, (Function)var3, var4);
                  }
               }
            }
         } catch (Exception var13) {
            System.out.printf("Error processing item at path %s: %s%n", var1, var13.getMessage());
         }

         return var5;
      }
   }

   private <T extends xshyo.us.theglow.B.A.A> void A(String var1, String var2, Map<Integer, T> var3, Function<String, T> var4, int var5) {
      var1 = var1.replaceAll("\\s+", "");
      if (var1.contains("-")) {
         String[] var6 = var1.split("-");

         try {
            int var7 = Integer.parseInt(var6[0]);
            int var8 = Integer.parseInt(var6[1]);

            for(int var9 = var7; var9 <= var8; ++var9) {
               if (this.A(var9, var5)) {
                  var3.put(var9, (xshyo.us.theglow.B.A.A)var4.apply(var2));
               }
            }
         } catch (ArrayIndexOutOfBoundsException | NumberFormatException var11) {
            System.out.println("Invalid range format: " + var1);
         }
      } else {
         try {
            int var12 = Integer.parseInt(var1);
            if (this.A(var12, var5)) {
               var3.put(var12, (xshyo.us.theglow.B.A.A)var4.apply(var2));
            }
         } catch (NumberFormatException var10) {
            System.out.println("Invalid slot number: " + var1);
         }
      }

   }

   private boolean A(int var1, int var2) {
      return var1 >= 0 && var1 < var2 * 9;
   }

   public Map<Integer, xshyo.us.theglow.B.A.A> A(String var1, TheGlow var2, int var3) {
      HashMap var4 = new HashMap();
      Section var5 = var2.getMenus().getSection(var1);
      if (var5 == null) {
         return var4;
      } else {
         Iterator var6 = var5.getKeys().iterator();

         while(var6.hasNext()) {
            Object var7 = var6.next();
            String var8 = var1 + "." + var7;

            try {
               this.A((Section)var5, var7.toString(), (String)var8, (Map)var4, var3);
            } catch (NumberFormatException var10) {
               System.out.printf("Error processing slots for item %s: %s%n", var7, var10.getMessage());
            }
         }

         return var4;
      }
   }

   private void A(Section var1, String var2, String var3, Map<Integer, xshyo.us.theglow.B.A.A> var4, int var5) {
      String var6 = var1.getString(var2 + ".slot");
      if (var6 != null) {
         try {
            this.A(var4, Integer.parseInt(var6.trim()), var3, var5);
            return;
         } catch (NumberFormatException var11) {
            System.out.println("Invalid single slot format: " + var6);
         }
      }

      Object var7 = var1.get(var2 + ".slots");
      if (var7 != null) {
         if (var7 instanceof String) {
            this.A((String)var7, var4, var3, var5);
         } else if (var7 instanceof List) {
            List var8 = (List)var7;
            Iterator var9 = var8.iterator();

            while(var9.hasNext()) {
               Object var10 = var9.next();
               this.A(var10, var4, var3, var5);
            }
         }

      }
   }

   private void A(String var1, Map<Integer, xshyo.us.theglow.B.A.A> var2, String var3, int var4) {
      var1 = var1.replaceAll("\\s+", "");
      if (var1.contains("-")) {
         String[] var5 = var1.split("-");

         try {
            int var6 = Integer.parseInt(var5[0]);
            int var7 = Integer.parseInt(var5[1]);

            for(int var8 = var6; var8 <= var7; ++var8) {
               this.A(var2, var8, var3, var4);
            }
         } catch (ArrayIndexOutOfBoundsException | NumberFormatException var10) {
            System.out.println("Invalid range format: " + var1);
         }
      } else {
         try {
            this.A(var2, Integer.parseInt(var1), var3, var4);
         } catch (NumberFormatException var9) {
            System.out.println("Invalid slot number: " + var1);
         }
      }

   }

   private void A(Object var1, Map<Integer, xshyo.us.theglow.B.A.A> var2, String var3, int var4) {
      if (var1 instanceof Integer) {
         this.A(var2, (Integer)var1, var3, var4);
      } else if (var1 instanceof String) {
         String var5 = ((String)var1).trim();
         this.A(var5, var2, var3, var4);
      }

   }

   private void A(Map<Integer, xshyo.us.theglow.B.A.A> var1, int var2, String var3, int var4) {
      if (var2 >= 0 && var2 < var4 * 9) {
         var1.put(var2, new D(var3));
      } else {
         System.out.printf("Invalid slot detected: %d (max: %d)%n", var2, var4 * 9);
      }
   }
}

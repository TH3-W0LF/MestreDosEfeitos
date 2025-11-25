package xshyo.us.theglow.E;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.GlowCacheData;

public class A {
   private final TheGlow A = TheGlow.getInstance();
   private final Map<String, GlowCacheData> B = new HashMap();

   public void B() {
      HashSet var1 = new HashSet();
      HashMap var2 = new HashMap();
      this.B.clear();
      List var3 = this.A.getMenus().getMapList("inventories.glowing.glows.colors");
      Iterator var4 = var3.iterator();

      while(true) {
         while(var4.hasNext()) {
            Map var5 = (Map)var4.next();
            String var6 = String.valueOf(var5.get("id"));
            if (var1.contains(var6)) {
               this.A.getLogger().warning("Duplicate ID '" + var6 + "' has already been processed.");
            } else if (var5.containsKey("id") && var5.containsKey("patterns")) {
               try {
                  String var7 = var5.get("permission") instanceof String ? (String)var5.get("permission") : null;
                  String var8 = var5.get("display_name") instanceof String ? (String)var5.get("display_name") : null;
                  String var9 = var5.get("material") instanceof String ? (String)var5.get("material") : null;
                  int var10 = var5.get("amount") instanceof Integer ? (Integer)var5.get("amount") : 1;
                  int var11 = var5.get("slot") instanceof Integer ? (Integer)var5.get("slot") : -1;
                  int var12 = var5.get("page") instanceof Integer ? (Integer)var5.get("page") : -1;
                  if (var11 != -1 && var2.containsKey(var11)) {
                     String var23 = (String)var2.get(var11);
                     this.A.getLogger().warning("Duplicate slot '" + var11 + "' found for IDs '" + var6 + "' and '" + var23 + "'. Skipping ID '" + var6 + "'.");
                  } else {
                     if (var11 != -1) {
                        var2.put(var11, var6);
                     }

                     int var13 = var5.get("model_data") instanceof Integer ? (Integer)var5.get("model_data") : 0;
                     Object var14 = var5.get("item_flags") instanceof List ? (List)var5.get("item_flags") : new ArrayList();
                     boolean var15 = var5.get("glowing") instanceof Boolean ? (Boolean)var5.get("glowing") : false;
                     Object var16 = var5.get("patterns") instanceof List ? (List)var5.get("patterns") : new ArrayList();
                     ArrayList var17 = new ArrayList();
                     Iterator var18 = ((List)var16).iterator();

                     while(var18.hasNext()) {
                        String var19 = (String)var18.next();
                        if (var19 != null && !var19.trim().isEmpty()) {
                           try {
                              ChatColor.valueOf(var19.trim().toUpperCase());
                              var17.add(var19.trim().toUpperCase());
                           } catch (IllegalArgumentException var21) {
                              this.A.getLogger().warning("Invalid pattern '" + var19 + "' in Glow ID '" + var6 + "': " + var21.getMessage());
                           }
                        }
                     }

                     if (var17.isEmpty()) {
                        this.A.getLogger().warning("Glow ID '" + var6 + "' has no valid ChatColor patterns.");
                     }

                     GlowCacheData var24 = new GlowCacheData(var7, var8, var9, var10, var11, var12, var13, (List)var14, var15, (List)var16);
                     this.B.put(var6, var24);
                     var1.add(var6);
                     if (!this.A.getConf().getBoolean("config.minimize-on-loading")) {
                        this.A.getLogger().info("Correctly loaded Glow ID: " + var6 + ".");
                     }
                  }
               } catch (ClassCastException var22) {
                  this.A.getLogger().warning("Error in ID '" + var6 + "' has invalid data types.");
               }
            }
         }

         if (this.A.getConf().getBoolean("config.minimize-on-loading")) {
            this.A.getLogger().info("Correctly loaded total glow: " + this.B.size() + ".");
         }

         return;
      }
   }

   public TheGlow A() {
      return this.A;
   }

   public Map<String, GlowCacheData> C() {
      return this.B;
   }
}

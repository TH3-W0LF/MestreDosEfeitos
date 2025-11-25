package xshyo.us.theglow.J;

import dev.geco.gsit.api.GSitAPI;
import me.libraryaddict.disguise.DisguiseAPI;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.nametag.NameTagManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class A {
   private final boolean A = this.B("TAB");
   private final boolean C = this.B("LibsDisguises") && this.B("LibsDisguises", "1.50");
   private final boolean B = this.B("GSit") && this.B("GSit", "11.0.0");

   public A() {
      this.A();
   }

   private boolean B(String var1, String var2) {
      Plugin var3 = Bukkit.getPluginManager().getPlugin(var1);
      if (var3 == null) {
         return false;
      } else {
         String var4 = var3.getDescription().getVersion();
         return this.A(var4, var2) >= 0;
      }
   }

   private int A(String var1, String var2) {
      String[] var3 = var1.split("\\.");
      String[] var4 = var2.split("\\.");
      int var5 = Math.max(var3.length, var4.length);

      for(int var6 = 0; var6 < var5; ++var6) {
         int var7 = var6 < var3.length ? this.A(var3[var6]) : 0;
         int var8 = var6 < var4.length ? this.A(var4[var6]) : 0;
         if (var7 != var8) {
            return Integer.compare(var7, var8);
         }
      }

      return 0;
   }

   private int A(String var1) {
      try {
         return Integer.parseInt(var1.replaceAll("[^0-9]", ""));
      } catch (NumberFormatException var3) {
         return 0;
      }
   }

   private boolean B(String var1) {
      Plugin var2 = Bukkit.getPluginManager().getPlugin(var1);
      return var2 != null && var2.isEnabled();
   }

   private void A() {
      Bukkit.getLogger().info("[TheGlow] Integrations loaded: TAB=" + this.A + ", LibsDisguises=" + this.C + ", GSit=" + this.B);
   }

   public void A(Player var1, ChatColor var2) {
      if (this.A) {
         try {
            TabPlayer var3 = TabAPI.getInstance().getPlayer(var1.getUniqueId());
            if (var3 == null) {
               return;
            }

            NameTagManager var4 = TabAPI.getInstance().getNameTagManager();
            if (var4 == null) {
               return;
            }

            String var5 = var4.getOriginalPrefix(var3);
            var4.setPrefix(var3, var5 + var2.toString());
         } catch (Exception var6) {
            Bukkit.getLogger().warning("[TheGlow] Error updating TAB nametag: " + var6.getMessage());
         }

      }
   }

   public void B(Player var1) {
      if (this.A) {
         try {
            TabPlayer var2 = TabAPI.getInstance().getPlayer(var1.getUniqueId());
            if (var2 == null) {
               return;
            }

            NameTagManager var3 = TabAPI.getInstance().getNameTagManager();
            if (var3 == null) {
               return;
            }

            String var4 = var3.getOriginalPrefix(var2);
            var3.setPrefix(var2, var4);
         } catch (Exception var5) {
            Bukkit.getLogger().warning("[TheGlow] Error resetting TAB nametag: " + var5.getMessage());
         }

      }
   }

   public boolean A(Player var1) {
      if (!this.C) {
         return false;
      } else {
         try {
            return DisguiseAPI.isDisguised(var1);
         } catch (Exception var3) {
            Bukkit.getLogger().warning("[TheGlow] Error checking disguise: " + var3.getMessage());
            return false;
         }
      }
   }

   public boolean C(Player var1) {
      if (!this.B) {
         return false;
      } else {
         try {
            return GSitAPI.isEntitySitting(var1);
         } catch (Exception var3) {
            Bukkit.getLogger().warning("[TheGlow] Error checking if player is sitting: " + var3.getMessage());
            return false;
         }
      }
   }
}

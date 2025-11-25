package xshyo.us.theglow.E;

import org.bukkit.plugin.java.JavaPlugin;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.enums.DebugLevel;

public class B {
   private static DebugLevel A;

   public static void A(DebugLevel var0) {
      A = var0;
   }

   public static DebugLevel A() {
      return A;
   }

   public static void A(JavaPlugin var0, String var1, DebugLevel var2) {
      if (TheGlow.getInstance().getConf().getBoolean("config.debug") && var2.getPriority() >= A.getPriority()) {
         var0.getLogger().info("[" + var2.name() + "] " + var1);
      }

   }

   static {
      A = DebugLevel.NORMAL;
   }
}

package xshyo.us.theglow.libs.theAPI.libs.universalScheduler.utils;

public class JavaUtil {
   public static boolean classExists(String var0) {
      try {
         Class.forName(var0);
         return true;
      } catch (ClassNotFoundException var2) {
         return false;
      }
   }
}

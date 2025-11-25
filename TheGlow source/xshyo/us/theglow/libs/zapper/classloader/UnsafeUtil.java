package xshyo.us.theglow.libs.zapper.classloader;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.lang.reflect.Field;
import java.util.Objects;
import sun.misc.Unsafe;

final class UnsafeUtil {
   private static final Supplier<Unsafe> Unsafe = Suppliers.memoize(() -> {
      try {
         Field var0 = Unsafe.class.getDeclaredField("theUnsafe");
         var0.setAccessible(true);
         return (Unsafe)var0.get((Object)null);
      } catch (Throwable var1) {
         throw new RuntimeException(var1);
      }
   });
   private static final Supplier<Boolean> isJava8 = Suppliers.memoize(() -> {
      String var0 = System.getProperty("java.version");
      return var0.contains("1.8");
   });

   public static Unsafe getUnsafe() {
      return (Unsafe)Unsafe.get();
   }

   public static boolean isJava8() {
      return (Boolean)isJava8.get();
   }

   public static <T> T getField(Object var0, String var1, Class<?> var2) {
      try {
         Unsafe var3 = (Unsafe)Unsafe.get();
         Field var4 = var2.getDeclaredField(var1);
         long var5 = var3.objectFieldOffset(var4);
         Object var7 = var3.getObject(var0, var5);
         return Objects.requireNonNull(var7, "getField(" + var1 + ") from " + var2);
      } catch (Throwable var8) {
         throw new RuntimeException(var8);
      }
   }
}

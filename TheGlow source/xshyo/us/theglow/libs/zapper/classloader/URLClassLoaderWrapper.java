package xshyo.us.theglow.libs.zapper.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import org.jetbrains.annotations.NotNull;

public abstract class URLClassLoaderWrapper {
   public abstract void addURL(@NotNull URL var1);

   @NotNull
   public static URLClassLoaderWrapper wrap(@NotNull URLClassLoader var0) {
      return new ByUnsafe(var0);
   }
}

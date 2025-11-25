package xshyo.us.theglow.libs.zapper.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;

final class ByUnsafe extends URLClassLoaderWrapper {
   private final Collection<URL> unopenedURLs;
   private final List<URL> pathURLs;

   public ByUnsafe(@NotNull URLClassLoader var1) {
      Object var2 = UnsafeUtil.getField(var1, "ucp", URLClassLoader.class);
      this.unopenedURLs = (Collection)UnsafeUtil.getField(var2, UnsafeUtil.isJava8() ? "urls" : "unopenedUrls", var2.getClass());
      this.pathURLs = (List)UnsafeUtil.getField(var2, "path", var2.getClass());
   }

   public void addURL(@NotNull URL var1) {
      this.unopenedURLs.add(var1);
      this.pathURLs.add(var1);
   }
}

package xshyo.us.theglow.libs.zapper.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import org.jetbrains.annotations.NotNull;

public final class IsolatedClassLoader extends URLClassLoader {
   public IsolatedClassLoader() {
      super(new URL[0]);
   }

   public IsolatedClassLoader(@NotNull URL[] var1) {
      super(var1, ClassLoader.getSystemClassLoader().getParent());
   }

   static {
      ClassLoader.registerAsParallelCapable();
   }
}

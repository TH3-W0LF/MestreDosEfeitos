package xshyo.us.theglow.libs.zapper.relocation;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.zapper.Dependency;
import xshyo.us.theglow.libs.zapper.classloader.IsolatedClassLoader;
import xshyo.us.theglow.libs.zapper.repository.Repository;

public final class Relocator {
   private static boolean initialized = false;
   private static final List<Dependency> dependencies = Arrays.asList(new Dependency("org.ow2.asm", "asm", "9.7.1"), new Dependency("org.ow2.asm", "asm-commons", "9.7.1"), new Dependency("me.lucko", "jar-relocator", "1.7"));
   private static Constructor<?> relocatorConstructor;
   private static Method relocateMethod;

   private Relocator() {
   }

   public static void relocate(@NotNull File var0, @NotNull File var1, @NotNull List<Relocation> var2) {
      if (!initialized) {
         downloadJarRelocator(var0.getParentFile());
         initialized = true;
      }

      try {
         LinkedHashMap var3 = new LinkedHashMap();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            Relocation var5 = (Relocation)var4.next();
            var3.put(var5.getPattern(), var5.getNewPattern());
         }

         Object var7 = relocatorConstructor.newInstance(var0, var1, var3);
         relocateMethod.invoke(var7);
      } catch (Throwable var6) {
         throw new RuntimeException(var6);
      }
   }

   private static void downloadJarRelocator(File var0) {
      try {
         URL[] var1 = new URL[3];
         var0.mkdirs();

         for(int var2 = 0; var2 < dependencies.size(); ++var2) {
            Dependency var3 = (Dependency)dependencies.get(var2);
            File var4 = new File(var0, String.format("%s.%s-%s.jar", var3.getGroupId(), var3.getArtifactId(), var3.getVersion()));
            if (!var4.exists()) {
               var3.download(var4, Repository.mavenCentral());
            }

            var1[var2] = var4.toURI().toURL();
         }

         IsolatedClassLoader var6 = new IsolatedClassLoader(var1);
         Class var7 = var6.loadClass("me.lucko.jarrelocator.JarRelocator");
         relocatorConstructor = var7.getDeclaredConstructor(File.class, File.class, Map.class);
         relocatorConstructor.setAccessible(true);
         relocateMethod = var7.getDeclaredMethod("run");
         relocateMethod.setAccessible(true);
      } catch (Throwable var5) {
         throw new RuntimeException(var5);
      }
   }
}

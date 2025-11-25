package xshyo.us.theglow.libs.zapper.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClassLoaderReader {
   private static final Field description;
   private static final Field dataFolder;
   private static final Class<? extends URLClassLoader> PL_CL_LOADER;

   private ClassLoaderReader() {
   }

   @NotNull
   public static PluginDescriptionFile getDescription(@NotNull Class<?> var0) {
      ClassLoader var1 = var0.getClassLoader();
      if (!PL_CL_LOADER.isAssignableFrom(var1.getClass())) {
         throw new UnsupportedOperationException("Class is not a plugin class");
      } else {
         try {
            return (PluginDescriptionFile)description.get(var1);
         } catch (IllegalAccessException var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   @NotNull
   public static File getDataFolder(@NotNull Class<?> var0) {
      ClassLoader var1 = var0.getClassLoader();
      if (!PL_CL_LOADER.isAssignableFrom(var1.getClass())) {
         throw new UnsupportedOperationException("Class is not a plugin class");
      } else {
         try {
            return (File)dataFolder.get(var1);
         } catch (IllegalAccessException var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   @Contract("null -> fail")
   @Nullable
   public static InputStream getResource(String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("File name cannot be null");
      } else {
         try {
            URL var1 = ClassLoaderReader.class.getClassLoader().getResource(var0);
            if (var1 == null) {
               return null;
            } else {
               URLConnection var2 = var1.openConnection();
               var2.setUseCaches(false);
               return var2.getInputStream();
            }
         } catch (IOException var3) {
            return null;
         }
      }
   }

   static {
      try {
         PL_CL_LOADER = Class.forName("org.bukkit.plugin.java.PluginClassLoader").asSubclass(URLClassLoader.class);
         description = PL_CL_LOADER.getDeclaredField("description");
         description.setAccessible(true);
         dataFolder = PL_CL_LOADER.getDeclaredField("dataFolder");
         dataFolder.setAccessible(true);
      } catch (Throwable var1) {
         throw new RuntimeException(var1);
      }
   }
}

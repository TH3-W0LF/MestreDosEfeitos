package xshyo.us.theglow;

import java.io.File;
import java.net.URLClassLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import xshyo.us.theglow.libs.theAPI.TheAPI;
import xshyo.us.theglow.libs.zapper.DependencyManager;
import xshyo.us.theglow.libs.zapper.ZapperJavaPlugin;
import xshyo.us.theglow.libs.zapper.classloader.URLClassLoaderWrapper;
import xshyo.us.theglow.libs.zapper.relocation.Relocation;
import xshyo.us.theglow.libs.zapper.repository.Repository;
import xshyo.us.theglow.libs.zapper.util.ClassLoaderReader;

public abstract class A extends TheAPI {
   static {
      try {
         File var0 = new File(ClassLoaderReader.getDataFolder(A.class), "libraries");
         if (!var0.exists()) {
            PluginDescriptionFile var1 = ClassLoaderReader.getDescription(ZapperJavaPlugin.class);
            Bukkit.getLogger().info("[" + var1.getName() + "] It appears you're running " + var1.getName() + " for the first time.");
            Bukkit.getLogger().info("[" + var1.getName() + "] Please give me a few seconds to install dependencies. This is a one-time process.");
         }

         DependencyManager var3 = new DependencyManager(var0, URLClassLoaderWrapper.wrap((URLClassLoader)A.class.getClassLoader()));
         var3.repository(Repository.mavenCentral());
         var3.repository(Repository.maven("https://jitpack.io"));
         var3.dependency("com.zaxxer:HikariCP:5.0.1");
         var3.relocate(new Relocation("com{}zaxxer{}hikari".replace("{}", "."), "xshyo.us.theglow.libs.hikari"));
         var3.dependency("net.kyori:adventure-key:4.19.0");
         var3.dependency("net.kyori:adventure-nbt:4.19.0");
         var3.dependency("net.kyori:adventure-text-serializer-gson:4.19.0");
         var3.dependency("net.kyori:adventure-text-serializer-legacy:4.19.0");
         var3.dependency("net.kyori:adventure-text-serializer-json:4.19.0");
         var3.dependency("net.kyori:adventure-text-minimessage:4.19.0");
         var3.dependency("net.kyori:adventure-api:4.19.0");
         var3.dependency("net.kyori:adventure-text-serializer-gson-legacy-impl:4.13.1");
         var3.dependency("net.kyori:adventure-platform-bukkit:4.3.4");
         var3.dependency("net.kyori:adventure-platform-viaversion:4.3.4");
         var3.dependency("net.kyori:adventure-platform-facet:4.3.4");
         var3.dependency("net.kyori:adventure-text-serializer-bungeecord:4.3.4");
         var3.dependency("net.kyori:adventure-platform-api:4.3.4");
         var3.dependency("net.kyori:examination-string:1.3.0");
         var3.dependency("net.kyori:examination-api:1.3.0");
         var3.dependency("net.kyori:option:1.0.0");
         var3.relocate(new Relocation("net{}kyori".replace("{}", "."), "xshyo.us.theglow.libs.kyori"));
         var3.load();
         Bukkit.getLogger().info("[TheGlow] Dependencies loaded correctly");
      } catch (Exception var2) {
         Bukkit.getLogger().severe("[TheGlow] Error loading dependencies: " + var2.getMessage());
         var2.printStackTrace();
      }

   }
}

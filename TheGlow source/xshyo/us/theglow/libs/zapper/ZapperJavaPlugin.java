package xshyo.us.theglow.libs.zapper;

import java.io.File;
import java.net.URLClassLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xshyo.us.theglow.libs.zapper.classloader.URLClassLoaderWrapper;
import xshyo.us.theglow.libs.zapper.meta.MetaReader;

public abstract class ZapperJavaPlugin extends JavaPlugin {
   static {
      MetaReader var0 = MetaReader.create();
      RuntimeLibPluginConfiguration var1 = RuntimeLibPluginConfiguration.parse();
      File var2 = new File(var0.dataFolder(), var1.getLibsFolder());
      if (!var2.exists()) {
         Bukkit.getLogger().info("[" + var0.pluginName() + "] It appears you're running " + var0.pluginName() + " for the first time.");
         Bukkit.getLogger().info("[" + var0.pluginName() + "] Please give me a few seconds to install dependencies. This is a one-time process.");
      }

      DependencyManager var3 = new DependencyManager(var2, URLClassLoaderWrapper.wrap((URLClassLoader)ZapperJavaPlugin.class.getClassLoader()));
      var1.getDependencies().forEach(var3::dependency);
      var1.getRepositories().forEach(var3::repository);
      var1.getRelocations().forEach(var3::relocate);
      var3.load();
   }
}

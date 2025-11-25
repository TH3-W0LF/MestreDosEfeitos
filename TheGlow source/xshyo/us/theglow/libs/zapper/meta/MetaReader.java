package xshyo.us.theglow.libs.zapper.meta;

import java.io.File;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public interface MetaReader {
   @NotNull
   String pluginName();

   @NotNull
   default File dataFolder() {
      return new File(Bukkit.getUpdateFolderFile().getParentFile() + File.separator + this.pluginName());
   }

   @NotNull
   static MetaReader create() {
      try {
         String var0 = MetaReader.class.getClassLoader().getClass().getSimpleName();
         return (MetaReader)(var0.contains("Paper") ? (MetaReader)Class.forName("xshyo.us.theglow.libs.zapper.meta.PaperMetaReader").asSubclass(MetaReader.class).newInstance() : new BukkitMetaReader());
      } catch (Throwable var1) {
         throw var1;
      }
   }
}

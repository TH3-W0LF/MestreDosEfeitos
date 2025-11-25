package xshyo.us.theglow.libs.zapper.meta;

import java.io.File;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.zapper.util.ClassLoaderReader;

final class BukkitMetaReader implements MetaReader {
   @NotNull
   public String pluginName() {
      return ClassLoaderReader.getDescription(BukkitMetaReader.class).getName();
   }

   @NotNull
   public File dataFolder() {
      return ClassLoaderReader.getDataFolder(BukkitMetaReader.class);
   }
}

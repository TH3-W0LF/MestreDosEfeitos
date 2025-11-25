package xshyo.us.theglow.libs.config.updater;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.config.settings.general.GeneralSettings;
import xshyo.us.theglow.libs.config.settings.updater.UpdaterSettings;
import xshyo.us.theglow.libs.config.updater.operators.Merger;

public class Updater {
   public static void update(@NotNull Section var0, @NotNull Section var1, @NotNull UpdaterSettings var2, @NotNull GeneralSettings var3) throws IOException {
      if (!VersionedOperations.run(var0, var1, var2, var3.getRouteSeparator())) {
         Merger.merge(var0, var1, var2);
         if (var2.getVersioning() != null) {
            var2.getVersioning().updateVersionID(var0, var1);
         }

         if (var2.isAutoSave()) {
            var0.getRoot().save();
         }

      }
   }
}

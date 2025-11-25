package xshyo.us.theglow.libs.config.updater;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.config.dvs.Version;
import xshyo.us.theglow.libs.config.dvs.versioning.Versioning;
import xshyo.us.theglow.libs.config.settings.updater.UpdaterSettings;
import xshyo.us.theglow.libs.config.updater.operators.Mapper;
import xshyo.us.theglow.libs.config.updater.operators.Relocator;

public class VersionedOperations {
   public static boolean run(@NotNull Section var0, @NotNull Section var1, @NotNull UpdaterSettings var2, char var3) {
      Versioning var4 = var2.getVersioning();
      if (var4 == null) {
         return false;
      } else {
         Version var5 = var4.getDocumentVersion(var0, false);
         Version var6 = (Version)Objects.requireNonNull(var4.getDocumentVersion(var1, true), "Version ID of the defaults cannot be null! Is it malformed or not specified?");
         int var7 = var5 != null ? var5.compareTo(var6) : -1;
         if (var7 > 0 && !var2.isEnableDowngrading()) {
            throw new UnsupportedOperationException(String.format("Downgrading is not enabled (%s > %s)!", var6.asID(), var5.asID()));
         } else if (var7 == 0) {
            return true;
         } else {
            if (var7 < 0) {
               iterate(var0, var5 != null ? var5 : var4.getFirstVersion(), var6, var2, var3);
            }

            var2.getIgnoredRoutes(var6.asID(), var3).forEach((var1x) -> {
               var0.getOptionalBlock(var1x).ifPresent((var0x) -> {
                  var0x.setIgnored(true);
               });
            });
            return false;
         }
      }
   }

   private static void iterate(@NotNull Section var0, @NotNull Version var1, @NotNull Version var2, @NotNull UpdaterSettings var3, char var4) {
      Version var5 = var1.copy();

      while(var5.compareTo(var2) <= 0) {
         var5.next();
         Relocator.apply(var0, var3.getRelocations(var5.asID(), var4));
         Mapper.apply(var0, var3.getMappers(var5.asID(), var4));
         var3.getCustomLogic(var5.asID()).forEach((var1x) -> {
            var1x.accept(var0.getRoot());
         });
      }

   }
}

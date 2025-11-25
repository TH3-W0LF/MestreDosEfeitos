package xshyo.us.theglow.libs.config.dvs.versioning;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.config.dvs.Version;

public interface Versioning {
   @Nullable
   Version getDocumentVersion(@NotNull Section var1, boolean var2);

   @NotNull
   Version getFirstVersion();

   default void updateVersionID(@NotNull Section var1, @NotNull Section var2) {
   }
}

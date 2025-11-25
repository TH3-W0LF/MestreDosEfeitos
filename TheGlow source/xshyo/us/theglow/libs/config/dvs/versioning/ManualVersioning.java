package xshyo.us.theglow.libs.config.dvs.versioning;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.config.dvs.Pattern;
import xshyo.us.theglow.libs.config.dvs.Version;

public class ManualVersioning implements Versioning {
   private final Version documentVersion;
   private final Version defaultsVersion;

   public ManualVersioning(@NotNull Pattern var1, @Nullable String var2, @NotNull String var3) {
      this.documentVersion = var2 == null ? null : var1.getVersion(var2);
      this.defaultsVersion = var1.getVersion(var3);
   }

   @Nullable
   public Version getDocumentVersion(@NotNull Section var1, boolean var2) {
      return var2 ? this.defaultsVersion : this.documentVersion;
   }

   @NotNull
   public Version getFirstVersion() {
      return this.defaultsVersion.getPattern().getFirstVersion();
   }

   public String toString() {
      return "ManualVersioning{documentVersion=" + this.documentVersion + ", defaultsVersion=" + this.defaultsVersion + '}';
   }
}

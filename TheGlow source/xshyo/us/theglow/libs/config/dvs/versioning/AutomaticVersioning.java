package xshyo.us.theglow.libs.config.dvs.versioning;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.config.dvs.Pattern;
import xshyo.us.theglow.libs.config.dvs.Version;
import xshyo.us.theglow.libs.config.route.Route;

public class AutomaticVersioning implements Versioning {
   private final Pattern pattern;
   private final Route route;
   private final String strRoute;

   public AutomaticVersioning(@NotNull Pattern var1, @NotNull Route var2) {
      this.pattern = var1;
      this.route = var2;
      this.strRoute = null;
   }

   public AutomaticVersioning(@NotNull Pattern var1, @NotNull String var2) {
      this.pattern = var1;
      this.route = null;
      this.strRoute = var2;
   }

   @Nullable
   public Version getDocumentVersion(@NotNull Section var1, boolean var2) {
      Optional var10000 = this.route != null ? var1.getOptionalString(this.route) : var1.getOptionalString(this.strRoute);
      Pattern var10001 = this.pattern;
      var10001.getClass();
      return (Version)var10000.map(var10001::getVersion).orElse((Object)null);
   }

   @NotNull
   public Version getFirstVersion() {
      return this.pattern.getFirstVersion();
   }

   public void updateVersionID(@NotNull Section var1, @NotNull Section var2) {
      if (this.route != null) {
         var1.set((Route)this.route, var2.getString(this.route));
      } else {
         var1.set((String)this.strRoute, var2.getString(this.strRoute));
      }

   }

   public String toString() {
      return "AutomaticVersioning{pattern=" + this.pattern + ", route='" + (this.route == null ? this.strRoute : this.route) + '\'' + '}';
   }
}

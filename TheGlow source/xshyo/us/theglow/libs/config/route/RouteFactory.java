package xshyo.us.theglow.libs.config.route;

import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.config.settings.general.GeneralSettings;

public class RouteFactory {
   private final char separator;
   private final String escapedSeparator;

   public RouteFactory(@NotNull GeneralSettings var1) {
      this.separator = var1.getRouteSeparator();
      this.escapedSeparator = var1.getEscapedSeparator();
   }

   public RouteFactory(char var1) {
      this.separator = var1;
      this.escapedSeparator = Pattern.quote(String.valueOf(var1));
   }

   public RouteFactory() {
      this.separator = '.';
      this.escapedSeparator = GeneralSettings.DEFAULT_ESCAPED_SEPARATOR;
   }

   @NotNull
   public Route create(String var1) {
      return Route.fromString(var1, this);
   }

   public char getSeparator() {
      return this.separator;
   }

   @NotNull
   public String getEscapedSeparator() {
      return this.escapedSeparator;
   }
}

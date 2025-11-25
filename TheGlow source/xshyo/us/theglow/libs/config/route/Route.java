package xshyo.us.theglow.libs.config.route;

import java.util.Objects;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.route.implementation.MultiKeyRoute;
import xshyo.us.theglow.libs.config.route.implementation.SingleKeyRoute;

public interface Route {
   @NotNull
   static Route from(@NotNull Object... var0) {
      if (((Object[])Objects.requireNonNull(var0, "Route array cannot be null!")).length == 0) {
         throw new IllegalArgumentException("Empty routes are not allowed!");
      } else {
         return (Route)(var0.length == 1 ? new SingleKeyRoute(var0[0]) : new MultiKeyRoute(var0));
      }
   }

   @NotNull
   static Route from(@NotNull Object var0) {
      return new SingleKeyRoute(var0);
   }

   @NotNull
   static Route fromSingleKey(@NotNull Object var0) {
      return new SingleKeyRoute(var0);
   }

   @NotNull
   static Route fromString(@NotNull String var0) {
      return fromString(var0, '.');
   }

   @NotNull
   static Route fromString(@NotNull String var0, char var1) {
      return (Route)(var0.indexOf(var1) != -1 ? new MultiKeyRoute((Object[])var0.split(Pattern.quote(String.valueOf(var1)))) : new SingleKeyRoute(var0));
   }

   @NotNull
   static Route fromString(@NotNull String var0, @NotNull RouteFactory var1) {
      return (Route)(var0.indexOf(var1.getSeparator()) != -1 ? new MultiKeyRoute((Object[])var0.split(var1.getEscapedSeparator())) : new SingleKeyRoute(var0));
   }

   @NotNull
   static Route addTo(@Nullable Route var0, @NotNull Object var1) {
      return var0 == null ? fromSingleKey(var1) : var0.add(var1);
   }

   @NotNull
   String join(char var1);

   int length();

   @NotNull
   Object get(int var1);

   @NotNull
   Route add(@NotNull Object var1);

   @NotNull
   Route parent();

   boolean equals(Object var1);

   int hashCode();
}

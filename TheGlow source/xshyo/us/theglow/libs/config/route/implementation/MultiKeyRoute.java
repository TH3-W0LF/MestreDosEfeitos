package xshyo.us.theglow.libs.config.route.implementation;

import java.util.Arrays;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.config.route.Route;

public class MultiKeyRoute implements Route {
   private final Object[] route;

   public MultiKeyRoute(@NotNull Object... var1) {
      if (((Object[])Objects.requireNonNull(var1, "Route array cannot be null!")).length == 0) {
         throw new IllegalArgumentException("Empty routes are not allowed!");
      } else {
         Object[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Object var5 = var2[var4];
            Objects.requireNonNull(var5, "Route cannot contain null keys!");
         }

         this.route = var1;
      }
   }

   @NotNull
   public String join(char var1) {
      StringBuilder var2 = new StringBuilder();

      for(int var3 = 0; var3 < this.length(); ++var3) {
         var2.append(this.get(var3)).append(var3 + 1 < this.length() ? var1 : "");
      }

      return var2.toString();
   }

   public int length() {
      return this.route.length;
   }

   @NotNull
   public Object get(int var1) {
      return this.route[var1];
   }

   @NotNull
   public Route add(@NotNull Object var1) {
      Object[] var2 = Arrays.copyOf(this.route, this.route.length + 1);
      var2[var2.length - 1] = Objects.requireNonNull(var1, "Route cannot contain null keys!");
      return new MultiKeyRoute(var2);
   }

   @NotNull
   public Route parent() {
      return this.route.length == 2 ? Route.from(this.route[0]) : Route.from(Arrays.copyOf(this.route, this.route.length - 1));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Route)) {
         return false;
      } else {
         Route var2 = (Route)var1;
         if (this.length() != var2.length()) {
            return false;
         } else if (this.length() == 1 && var2.length() == 1) {
            return Objects.equals(this.get(0), var2.get(0));
         } else {
            return !(var2 instanceof MultiKeyRoute) ? false : Arrays.equals(this.route, ((MultiKeyRoute)var2).route);
         }
      }
   }

   public int hashCode() {
      return this.length() > 1 ? Arrays.hashCode(this.route) : Objects.hashCode(this.route[0]);
   }

   public String toString() {
      return "MultiKeyRoute{route=" + Arrays.toString(this.route) + '}';
   }
}

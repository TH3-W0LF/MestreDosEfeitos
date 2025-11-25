package xshyo.us.theglow.libs.config.route.implementation;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.config.route.Route;

public class SingleKeyRoute implements Route {
   private final Object key;

   public SingleKeyRoute(@NotNull Object var1) {
      this.key = Objects.requireNonNull(var1, "Route cannot contain null keys!");
   }

   @NotNull
   public String join(char var1) {
      return this.key.toString();
   }

   public int length() {
      return 1;
   }

   @NotNull
   public Object get(int var1) {
      if (var1 != 0) {
         throw new ArrayIndexOutOfBoundsException("Index " + var1 + " for single key route!");
      } else {
         return this.key;
      }
   }

   @NotNull
   public Route parent() {
      throw new IllegalArgumentException("Empty routes are not allowed!");
   }

   @NotNull
   public Route add(@NotNull Object var1) {
      return Route.from(this.key, Objects.requireNonNull(var1, "Route cannot contain null keys!"));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Route)) {
         return false;
      } else {
         Route var2 = (Route)var1;
         return var2.length() != 1 ? false : Objects.equals(this.key, var2.get(0));
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.key});
   }

   public String toString() {
      return "SingleKeyRoute{key=" + this.key + '}';
   }
}

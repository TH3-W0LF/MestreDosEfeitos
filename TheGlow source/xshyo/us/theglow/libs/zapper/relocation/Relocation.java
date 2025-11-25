package xshyo.us.theglow.libs.zapper.relocation;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class Relocation {
   private final String pattern;
   private final String newPattern;

   public Relocation(@NotNull String var1, @NotNull String var2) {
      this.pattern = var1;
      this.newPattern = var2;
   }

   public String getPattern() {
      return this.pattern;
   }

   public String getNewPattern() {
      return this.newPattern;
   }

   public String toString() {
      return String.format("Relocation{pattern='%s', newPattern='%s'}", this.pattern, this.newPattern);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Relocation)) {
         return false;
      } else {
         Relocation var2 = (Relocation)var1;
         return Objects.equals(this.pattern, var2.pattern) && Objects.equals(this.newPattern, var2.newPattern);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.pattern, this.newPattern});
   }
}

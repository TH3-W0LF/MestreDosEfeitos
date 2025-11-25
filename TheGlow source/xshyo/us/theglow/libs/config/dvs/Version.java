package xshyo.us.theglow.libs.config.dvs;

import java.util.Arrays;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Version implements Comparable<Version> {
   private final Pattern pattern;
   private final int[] cursors;
   private String id;

   Version(@Nullable String var1, @NotNull Pattern var2, int[] var3) {
      this.id = var1;
      this.pattern = var2;
      this.cursors = var3;
      if (var1 == null) {
         this.buildID();
      }

   }

   public int compareTo(Version var1) {
      if (!this.pattern.equals(var1.pattern)) {
         throw new ClassCastException("Compared versions are not defined by the same pattern!");
      } else {
         for(int var2 = 0; var2 < this.cursors.length; ++var2) {
            int var3 = Integer.compare(this.cursors[var2], var1.cursors[var2]);
            if (var3 != 0) {
               return var3;
            }
         }

         return 0;
      }
   }

   public int getCursor(int var1) {
      return this.cursors[var1];
   }

   public void next() {
      for(int var1 = this.cursors.length - 1; var1 >= 0; --var1) {
         int var2 = this.cursors[var1];
         if (var2 + 1 < this.pattern.getSegment(var1).length()) {
            this.cursors[var1] = var2 + 1;
            break;
         }

         this.cursors[var1] = 0;
      }

      this.buildID();
   }

   private void buildID() {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < this.cursors.length; ++var2) {
         var1.append(this.pattern.getSegment(var2).getElement(this.cursors[var2]));
      }

      this.id = var1.toString();
   }

   public String asID() {
      return this.id;
   }

   public Version copy() {
      return new Version(this.id, this.pattern, Arrays.copyOf(this.cursors, this.cursors.length));
   }

   public Pattern getPattern() {
      return this.pattern;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Version)) {
         return false;
      } else {
         Version var2 = (Version)var1;
         return this.pattern.equals(var2.pattern) && Arrays.equals(this.cursors, var2.cursors);
      }
   }

   public int hashCode() {
      int var1 = Objects.hash(new Object[]{this.pattern});
      var1 = 31 * var1 + Arrays.hashCode(this.cursors);
      return var1;
   }

   public String toString() {
      return "Version{pattern=" + this.pattern + ", cursors=" + Arrays.toString(this.cursors) + ", id='" + this.id + '\'' + '}';
   }
}

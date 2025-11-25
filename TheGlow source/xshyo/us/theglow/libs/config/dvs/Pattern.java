package xshyo.us.theglow.libs.config.dvs;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.dvs.segment.Segment;

public class Pattern {
   private final Segment[] segments;

   public Pattern(@NotNull Segment... var1) {
      this.segments = var1;
   }

   @Deprecated
   @NotNull
   public Segment getPart(int var1) {
      return this.segments[var1];
   }

   @NotNull
   public Segment getSegment(int var1) {
      return this.segments[var1];
   }

   @Nullable
   public Version getVersion(@NotNull String var1) {
      int[] var2 = new int[this.segments.length];
      int var3 = 0;

      for(int var4 = 0; var4 < this.segments.length; ++var4) {
         int var5 = this.segments[var4].parse(var1, var3);
         if (var5 == -1) {
            return null;
         }

         var2[var4] = var5;
         var3 += this.segments[var4].getElementLength(var5);
      }

      return new Version(var1, this, var2);
   }

   public Version getFirstVersion() {
      return new Version((String)null, this, new int[this.segments.length]);
   }

   public String toString() {
      return "Pattern{segments=" + Arrays.toString(this.segments) + '}';
   }
}

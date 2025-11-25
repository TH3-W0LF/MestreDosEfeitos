package xshyo.us.theglow.libs.config.dvs.segment;

import java.util.Arrays;

public class LiteralSegment implements Segment {
   private final String[] elements;

   public LiteralSegment(String... var1) {
      this.elements = var1;
   }

   public int parse(String var1, int var2) {
      for(int var3 = 0; var3 < this.elements.length; ++var3) {
         if (var1.startsWith(this.elements[var3], var2)) {
            return var3;
         }
      }

      return -1;
   }

   public String getElement(int var1) {
      return this.elements[var1];
   }

   public int getElementLength(int var1) {
      return this.elements[var1].length();
   }

   public int length() {
      return this.elements.length;
   }

   public String toString() {
      return "LiteralSegment{elements=" + Arrays.toString(this.elements) + '}';
   }
}

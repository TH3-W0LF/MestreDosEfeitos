package xshyo.us.theglow.libs.config.dvs.segment;

import java.util.Arrays;

public class RangeSegment implements Segment {
   private final int start;
   private final int end;
   private final int step;
   private final int minStringLength;
   private final int maxStringLength;
   private final int fill;
   private final int length;

   public RangeSegment(int var1, int var2, int var3, int var4) {
      this.start = var1;
      this.end = var2;
      this.step = var3;
      this.fill = var4;
      if (var3 == 0) {
         throw new IllegalArgumentException("Step cannot be zero!");
      } else if (var1 < var2 && var3 < 0 || var1 > var2 && var3 > 0) {
         throw new IllegalArgumentException(String.format("Invalid step for the given range! start=%d end=%d step=%d", var1, var2, var3));
      } else if (var1 == var2) {
         throw new IllegalArgumentException(String.format("Parameters define an empty range, start=end! start=%d end=%d", var1, var2));
      } else {
         this.length = (int)Math.ceil((double)Math.abs(var1 - var2) / (double)Math.abs(var3));
         int var5 = var1 + var3 * (this.length - 1);
         if (var1 < 0 || var2 < 0 && var5 < 0) {
            throw new IllegalArgumentException(String.format("Range contains negative integers! start=%d end=%d step=%d", var1, var2, var3));
         } else if (var4 > 0 && !this.validateFill(var4, Math.max(var1, var5))) {
            throw new IllegalArgumentException(String.format("Some integer from the range exceeds maximum length defined by the filling parameter! start=%d end=%d last=%d fill=%d", var1, var2, var5, var4));
         } else {
            this.maxStringLength = var4 > 0 ? var4 : this.countDigits(var3 > 0 ? var2 : var1);
            this.minStringLength = var4 > 0 ? var4 : this.countDigits(var3 > 0 ? var1 : var2);
         }
      }
   }

   @Deprecated
   public RangeSegment(int var1, int var2) {
      this(var1, var2, var1 < var2 ? 1 : -1, 0);
   }

   @Deprecated
   public RangeSegment(int var1, int var2, int var3) {
      this(var1, var2, var3, 0);
   }

   private boolean validateFill(int var1, int var2) {
      int var3 = 9;

      for(int var4 = 0; var4 < var1; ++var4) {
         if (var3 >= var2) {
            return true;
         }

         var3 *= 10;
         var3 += 9;
      }

      return false;
   }

   public int parse(String var1, int var2) {
      if (this.fill > 0) {
         if (this.fill > var1.length() - var2) {
            return -1;
         } else {
            try {
               return this.getRangeIndex(Integer.parseInt(var1.substring(var2, this.fill)));
            } catch (NumberFormatException var7) {
               return -1;
            }
         }
      } else if (var1.length() <= var2) {
         return -1;
      } else {
         int var3 = 0;
         int var4 = 0;

         int var5;
         for(var5 = 0; var5 < this.maxStringLength && var5 < var1.length() - var2 && (var5 != 1 || var3 != 0 || var4 != 1); ++var5) {
            int var6 = Character.digit(var1.charAt(var2 + var5), 10);
            if (var6 == -1) {
               break;
            }

            var3 *= 10;
            var3 += var6;
            ++var4;
         }

         if (var4 == 0) {
            return -1;
         } else if (var3 == 0) {
            return this.getRangeIndex(0);
         } else {
            while(var3 > 0 && var4 >= this.minStringLength) {
               var5 = this.getRangeIndex(var3);
               if (var5 != -1) {
                  return var5;
               }

               var3 /= 10;
               --var4;
            }

            return -1;
         }
      }
   }

   private int countDigits(int var1) {
      if (var1 == 0) {
         return 1;
      } else {
         int var2;
         for(var2 = 0; var1 > 0; ++var2) {
            var1 /= 10;
         }

         return var2;
      }
   }

   private int getRangeIndex(int var1) {
      if (this.step > 0) {
         if (this.start > var1 || this.end <= var1) {
            return -1;
         }
      } else if (this.start < var1 || this.end >= var1) {
         return -1;
      }

      int var2 = Math.abs(var1 - this.start);
      return var1 >= 0 && var2 % this.step == 0 ? var2 / Math.abs(this.step) : -1;
   }

   public String getElement(int var1) {
      if (var1 >= this.length) {
         throw new IndexOutOfBoundsException(String.format("Index out of bounds! i=%d length=%d", var1, this.length));
      } else {
         String var2 = Integer.toString(this.start + this.step * var1, 10);
         if (this.fill > 0 && var2.length() != this.fill) {
            char[] var3 = new char[this.fill - var2.length()];
            Arrays.fill(var3, '0');
            return (new StringBuilder(var2)).insert(0, var3).toString();
         } else {
            return var2;
         }
      }
   }

   public int getElementLength(int var1) {
      if (var1 >= this.length) {
         throw new IndexOutOfBoundsException(String.format("Index out of bounds! i=%d length=%d", var1, this.length));
      } else {
         return this.fill > 0 ? this.fill : this.countDigits(this.start + this.step * var1);
      }
   }

   public int length() {
      return this.length;
   }

   public String toString() {
      return "RangeSegment{start=" + this.start + ", end=" + this.end + ", step=" + this.step + ", fill=" + this.fill + ", length=" + this.length + '}';
   }
}

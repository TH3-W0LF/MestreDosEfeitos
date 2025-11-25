package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions;

import java.io.Serializable;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.CharConstants;

public final class Mark implements Serializable {
   private final String name;
   private final int index;
   private final int line;
   private final int column;
   private final int[] buffer;
   private final int pointer;

   public Mark(String var1, int var2, int var3, int var4, int[] var5, int var6) {
      this.name = var1;
      this.index = var2;
      this.line = var3;
      this.column = var4;
      this.buffer = var5;
      this.pointer = var6;
   }

   public Mark(String var1, int var2, int var3, int var4, char[] var5, int var6) {
      this(var1, var2, var3, var4, toCodePoints(var5), var6);
   }

   private static int[] toCodePoints(char[] var0) {
      int[] var1 = new int[Character.codePointCount(var0, 0, var0.length)];
      int var2 = 0;

      for(int var3 = 0; var2 < var0.length; ++var3) {
         int var4 = Character.codePointAt(var0, var2);
         var1[var3] = var4;
         var2 += Character.charCount(var4);
      }

      return var1;
   }

   private boolean isLineBreak(int var1) {
      return CharConstants.NULL_OR_LINEBR.has(var1);
   }

   public String createSnippet(int var1, int var2) {
      float var3 = (float)var2 / 2.0F - 1.0F;
      int var4 = this.pointer;
      String var5 = "";

      while(var4 > 0 && !this.isLineBreak(this.buffer[var4 - 1])) {
         --var4;
         if ((float)(this.pointer - var4) > var3) {
            var5 = " ... ";
            var4 += 5;
            break;
         }
      }

      String var6 = "";
      int var7 = this.pointer;

      while(var7 < this.buffer.length && !this.isLineBreak(this.buffer[var7])) {
         ++var7;
         if ((float)(var7 - this.pointer) > var3) {
            var6 = " ... ";
            var7 -= 5;
            break;
         }
      }

      StringBuilder var8 = new StringBuilder();

      int var9;
      for(var9 = 0; var9 < var1; ++var9) {
         var8.append(" ");
      }

      var8.append(var5);

      for(var9 = var4; var9 < var7; ++var9) {
         var8.appendCodePoint(this.buffer[var9]);
      }

      var8.append(var6);
      var8.append("\n");

      for(var9 = 0; var9 < var1 + this.pointer - var4 + var5.length(); ++var9) {
         var8.append(" ");
      }

      var8.append("^");
      return var8.toString();
   }

   public String createSnippet() {
      return this.createSnippet(4, 75);
   }

   public String toString() {
      String var1 = this.createSnippet();
      return " in " + this.name + ", line " + (this.line + 1) + ", column " + (this.column + 1) + ":\n" + var1;
   }

   public String getName() {
      return this.name;
   }

   public int getLine() {
      return this.line;
   }

   public int getColumn() {
      return this.column;
   }

   public int getIndex() {
      return this.index;
   }

   public int[] getBuffer() {
      return this.buffer;
   }

   public int getPointer() {
      return this.pointer;
   }
}

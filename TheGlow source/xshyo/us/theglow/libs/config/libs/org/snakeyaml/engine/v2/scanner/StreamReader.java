package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.scanner;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.CharConstants;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.ReaderException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;

public final class StreamReader {
   private final String name;
   private final Reader stream;
   private final int bufferSize;
   private final char[] buffer;
   private final boolean useMarks;
   private int[] codePointsWindow;
   private int dataLength;
   private int pointer;
   private boolean eof;
   private int index;
   private int documentIndex;
   private int line;
   private int column;

   @Deprecated
   public StreamReader(Reader var1, LoadSettings var2) {
      this(var2, var1);
   }

   public StreamReader(LoadSettings var1, Reader var2) {
      this.pointer = 0;
      this.index = 0;
      this.documentIndex = 0;
      this.line = 0;
      this.column = 0;
      this.name = var1.getLabel();
      this.codePointsWindow = new int[0];
      this.dataLength = 0;
      this.stream = var2;
      this.eof = false;
      this.bufferSize = var1.getBufferSize();
      this.buffer = new char[this.bufferSize];
      this.useMarks = var1.getUseMarks();
   }

   @Deprecated
   public StreamReader(String var1, LoadSettings var2) {
      this((LoadSettings)var2, (Reader)(new StringReader(var1)));
   }

   public StreamReader(LoadSettings var1, String var2) {
      this((LoadSettings)var1, (Reader)(new StringReader(var2)));
   }

   public static boolean isPrintable(String var0) {
      int var1 = var0.length();

      int var3;
      for(int var2 = 0; var2 < var1; var2 += Character.charCount(var3)) {
         var3 = var0.codePointAt(var2);
         if (!isPrintable(var3)) {
            return false;
         }
      }

      return true;
   }

   public static boolean isPrintable(int var0) {
      return var0 >= 32 && var0 <= 126 || var0 == 9 || var0 == 10 || var0 == 13 || var0 == 133 || var0 >= 160 && var0 <= 55295 || var0 >= 57344 && var0 <= 65533 || var0 >= 65536 && var0 <= 1114111;
   }

   public Optional<Mark> getMark() {
      return this.useMarks ? Optional.of(new Mark(this.name, this.index, this.line, this.column, this.codePointsWindow, this.pointer)) : Optional.empty();
   }

   public void forward() {
      this.forward(1);
   }

   public void forward(int var1) {
      for(int var2 = 0; var2 < var1 && this.ensureEnoughData(); ++var2) {
         int var3 = this.codePointsWindow[this.pointer++];
         this.moveIndices(1);
         if (CharConstants.LINEBR.has(var3) || var3 == 13 && this.ensureEnoughData() && this.codePointsWindow[this.pointer] != 10) {
            ++this.line;
            this.column = 0;
         } else if (var3 != 65279) {
            ++this.column;
         }
      }

   }

   public int peek() {
      return this.ensureEnoughData() ? this.codePointsWindow[this.pointer] : 0;
   }

   public int peek(int var1) {
      return this.ensureEnoughData(var1) ? this.codePointsWindow[this.pointer + var1] : 0;
   }

   public String prefix(int var1) {
      if (var1 == 0) {
         return "";
      } else {
         return this.ensureEnoughData(var1) ? new String(this.codePointsWindow, this.pointer, var1) : new String(this.codePointsWindow, this.pointer, Math.min(var1, this.dataLength - this.pointer));
      }
   }

   public String prefixForward(int var1) {
      String var2 = this.prefix(var1);
      this.pointer += var1;
      this.moveIndices(var1);
      this.column += var1;
      return var2;
   }

   private boolean ensureEnoughData() {
      return this.ensureEnoughData(0);
   }

   private boolean ensureEnoughData(int var1) {
      if (!this.eof && this.pointer + var1 >= this.dataLength) {
         this.update();
      }

      return this.pointer + var1 < this.dataLength;
   }

   private void update() {
      try {
         int var1 = this.stream.read(this.buffer, 0, this.bufferSize);
         if (var1 > 0) {
            int var2 = this.dataLength - this.pointer;
            this.codePointsWindow = Arrays.copyOfRange(this.codePointsWindow, this.pointer, this.dataLength + var1);
            if (Character.isHighSurrogate(this.buffer[var1 - 1])) {
               if (this.stream.read(this.buffer, var1, 1) == -1) {
                  this.eof = true;
               } else {
                  ++var1;
               }
            }

            Optional var3 = Optional.empty();

            for(int var4 = 0; var4 < var1; ++var2) {
               int var5 = Character.codePointAt(this.buffer, var4);
               this.codePointsWindow[var2] = var5;
               if (isPrintable(var5)) {
                  var4 += Character.charCount(var5);
               } else {
                  var3 = Optional.of(var5);
                  var4 = var1;
               }
            }

            this.dataLength = var2;
            this.pointer = 0;
            if (var3.isPresent()) {
               throw new ReaderException(this.name, var2 - 1, (Integer)var3.get(), "special characters are not allowed");
            }
         } else {
            this.eof = true;
         }

      } catch (IOException var6) {
         throw new YamlEngineException(var6);
      }
   }

   public int getColumn() {
      return this.column;
   }

   private void moveIndices(int var1) {
      this.index += var1;
      this.documentIndex += var1;
   }

   public int getDocumentIndex() {
      return this.documentIndex;
   }

   public void resetDocumentIndex() {
      this.documentIndex = 0;
   }

   public int getIndex() {
      return this.index;
   }

   public int getLine() {
      return this.line;
   }
}

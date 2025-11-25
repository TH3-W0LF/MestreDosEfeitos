package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

public class YamlUnicodeReader extends Reader {
   private static final Charset UTF8;
   private static final Charset UTF16BE;
   private static final Charset UTF16LE;
   private static final Charset UTF32BE;
   private static final Charset UTF32LE;
   private static final int BOM_SIZE = 4;
   PushbackInputStream internalIn;
   InputStreamReader internalIn2 = null;
   Charset encoding;

   public YamlUnicodeReader(InputStream var1) {
      this.encoding = UTF8;
      this.internalIn = new PushbackInputStream(var1, 4);
   }

   public Charset getEncoding() {
      return this.encoding;
   }

   protected void init() throws IOException {
      if (this.internalIn2 == null) {
         byte[] var1 = new byte[4];
         int var2 = this.internalIn.read(var1, 0, var1.length);
         int var3;
         if (var1[0] == 0 && var1[1] == 0 && var1[2] == -2 && var1[3] == -1) {
            this.encoding = UTF32BE;
            var3 = var2 - 4;
         } else if (var1[0] == -1 && var1[1] == -2 && var1[2] == 0 && var1[3] == 0) {
            this.encoding = UTF32LE;
            var3 = var2 - 4;
         } else if (var1[0] == -17 && var1[1] == -69 && var1[2] == -65) {
            this.encoding = UTF8;
            var3 = var2 - 3;
         } else if (var1[0] == -2 && var1[1] == -1) {
            this.encoding = UTF16BE;
            var3 = var2 - 2;
         } else if (var1[0] == -1 && var1[1] == -2) {
            this.encoding = UTF16LE;
            var3 = var2 - 2;
         } else {
            this.encoding = UTF8;
            var3 = var2;
         }

         if (var3 > 0) {
            this.internalIn.unread(var1, var2 - var3, var3);
         }

         CharsetDecoder var4 = this.encoding.newDecoder().onUnmappableCharacter(CodingErrorAction.REPORT);
         this.internalIn2 = new InputStreamReader(this.internalIn, var4);
      }
   }

   public void close() throws IOException {
      this.init();
      this.internalIn2.close();
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      this.init();
      return this.internalIn2.read(var1, var2, var3);
   }

   static {
      UTF8 = StandardCharsets.UTF_8;
      UTF16BE = StandardCharsets.UTF_16BE;
      UTF16LE = StandardCharsets.UTF_16LE;
      UTF32BE = Charset.forName("UTF-32BE");
      UTF32LE = Charset.forName("UTF-32LE");
   }
}

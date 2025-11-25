package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class CharConstants {
   private static final String ALPHA_S = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
   private static final String LINEBR_S = "\n";
   private static final String FULL_LINEBR_S = "\r\n";
   private static final String NULL_OR_LINEBR_S = "\u0000\r\n";
   private static final String NULL_BL_LINEBR_S = " \u0000\r\n";
   private static final String NULL_BL_T_LINEBR_S = "\t \u0000\r\n";
   private static final String NULL_BL_T_S = "\u0000 \t";
   private static final String URI_CHARS_SUFFIX_S = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_-;/?:@&=+$_.!~*'()%";
   public static final CharConstants LINEBR = new CharConstants("\n");
   public static final CharConstants NULL_OR_LINEBR = new CharConstants("\u0000\r\n");
   public static final CharConstants NULL_BL_LINEBR = new CharConstants(" \u0000\r\n");
   public static final CharConstants NULL_BL_T_LINEBR = new CharConstants("\t \u0000\r\n");
   public static final CharConstants NULL_BL_T = new CharConstants("\u0000 \t");
   public static final CharConstants URI_CHARS_FOR_TAG_PREFIX = new CharConstants("abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_-;/?:@&=+$_.!~*'()%,[]");
   public static final CharConstants URI_CHARS_FOR_TAG_SUFFIX = new CharConstants("abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_-;/?:@&=+$_.!~*'()%");
   public static final CharConstants ALPHA = new CharConstants("abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_");
   private static final int ASCII_SIZE = 128;
   boolean[] contains = new boolean[128];
   public static final Map<Character, String> ESCAPE_REPLACEMENTS;
   public static final Map<Character, Integer> ESCAPE_CODES;

   private CharConstants(String var1) {
      Arrays.fill(this.contains, false);

      for(int var2 = 0; var2 < var1.length(); ++var2) {
         int var3 = var1.codePointAt(var2);
         this.contains[var3] = true;
      }

   }

   public boolean has(int var1) {
      return var1 < 128 && this.contains[var1];
   }

   public boolean hasNo(int var1) {
      return !this.has(var1);
   }

   public boolean has(int var1, String var2) {
      return this.has(var1) || var2.indexOf(var1) != -1;
   }

   public boolean hasNo(int var1, String var2) {
      return !this.has(var1, var2);
   }

   public static String escapeChar(String var0) {
      Iterator var1 = ESCAPE_REPLACEMENTS.keySet().iterator();

      Character var2;
      String var3;
      do {
         if (!var1.hasNext()) {
            return var0;
         }

         var2 = (Character)var1.next();
         var3 = (String)ESCAPE_REPLACEMENTS.get(var2);
      } while(" ".equals(var3) || "/".equals(var3) || "\"".equals(var3) || !var3.equals(var0));

      return "\\" + var2;
   }

   static {
      HashMap var0 = new HashMap();
      var0.put('0', "\u0000");
      var0.put('a', "\u0007");
      var0.put('b', "\b");
      var0.put('t', "\t");
      var0.put('n', "\n");
      var0.put('v', "\u000b");
      var0.put('f', "\f");
      var0.put('r', "\r");
      var0.put('e', "\u001b");
      var0.put(' ', " ");
      var0.put('"', "\"");
      var0.put('/', "/");
      var0.put('\\', "\\");
      var0.put('N', "\u0085");
      var0.put('_', " ");
      ESCAPE_REPLACEMENTS = Collections.unmodifiableMap(var0);
      HashMap var1 = new HashMap();
      var1.put('x', 2);
      var1.put('u', 4);
      var1.put('U', 8);
      ESCAPE_CODES = Collections.unmodifiableMap(var1);
   }
}

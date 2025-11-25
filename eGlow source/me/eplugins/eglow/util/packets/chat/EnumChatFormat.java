package me.eplugins.eglow.util.packets.chat;

import lombok.Generated;
import me.eplugins.eglow.util.packets.chat.rgb.RGBUtils;

public enum EnumChatFormat {
   BLACK('0', "000000"),
   DARK_BLUE('1', "0000AA"),
   DARK_GREEN('2', "00AA00"),
   DARK_AQUA('3', "00AAAA"),
   DARK_RED('4', "AA0000"),
   DARK_PURPLE('5', "AA00AA"),
   GOLD('6', "FFAA00"),
   GRAY('7', "AAAAAA"),
   DARK_GRAY('8', "555555"),
   BLUE('9', "5555FF"),
   GREEN('a', "55FF55"),
   AQUA('b', "55FFFF"),
   RED('c', "FF5555"),
   LIGHT_PURPLE('d', "FF55FF"),
   YELLOW('e', "FFFF55"),
   WHITE('f', "FFFFFF"),
   OBFUSCATED('k'),
   BOLD('l'),
   STRIKETHROUGH('m'),
   UNDERLINE('n'),
   ITALIC('o'),
   RESET('r');

   public static final EnumChatFormat[] VALUES = values();
   public static final char COLOR_CHAR = '§';
   private final char character;
   private final short red;
   private final short green;
   private final short blue;
   private final String hexCode;
   private final String chatFormat;

   private EnumChatFormat(char character, String hexCode) {
      this.character = character;
      this.chatFormat = String.valueOf('§') + character;
      this.hexCode = hexCode;
      int hexColor = Integer.parseInt(hexCode, 16);
      this.red = (short)(hexColor >> 16 & 255);
      this.green = (short)(hexColor >> 8 & 255);
      this.blue = (short)(hexColor & 255);
   }

   private EnumChatFormat(char character) {
      this.character = character;
      this.chatFormat = String.valueOf('§') + character;
      this.red = 0;
      this.green = 0;
      this.blue = 0;
      this.hexCode = null;
   }

   public static EnumChatFormat getByChar(char c) {
      EnumChatFormat[] var1 = VALUES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EnumChatFormat format = var1[var3];
         if (format.character == c) {
            return format;
         }
      }

      return null;
   }

   public static EnumChatFormat lastColorsOf(String string) {
      if (string != null && !string.isEmpty()) {
         String legacyText = RGBUtils.getInstance().convertRGBtoLegacy(string);
         String last = getLastColors(legacyText);
         if (!last.isEmpty()) {
            char c = last.toCharArray()[1];
            EnumChatFormat[] var4 = VALUES;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               EnumChatFormat e = var4[var6];
               if (e.character == c) {
                  return e;
               }
            }
         }

         return WHITE;
      } else {
         return WHITE;
      }
   }

   public String getFormat() {
      return this.chatFormat;
   }

   public static EnumChatFormat fromRGBExact(int red, int green, int blue) {
      EnumChatFormat[] var3 = VALUES;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumChatFormat format = var3[var5];
         if (format.red == red && format.green == green && format.blue == blue) {
            return format;
         }
      }

      return null;
   }

   public static String color(String textToTranslate) {
      if (textToTranslate == null) {
         return null;
      } else if (!textToTranslate.contains("&")) {
         return textToTranslate;
      } else {
         char[] b = textToTranslate.toCharArray();

         for(int i = 0; i < b.length - 1; ++i) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
               b[i] = 167;
               b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
         }

         return new String(b);
      }
   }

   public static String getLastColors(String input) {
      if (input == null) {
         return "";
      } else {
         StringBuilder result = new StringBuilder();
         int length = input.length();

         for(int index = length - 1; index > -1; --index) {
            char section = input.charAt(index);
            if ((section == 167 || section == '&') && index < length - 1) {
               char c = input.charAt(index + 1);
               if ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".contains(String.valueOf(c))) {
                  result.insert(0, '§');
                  result.insert(1, c);
                  if ("0123456789AaBbCcDdEeFfRr".contains(String.valueOf(c))) {
                     break;
                  }
               }
            }
         }

         return result.toString();
      }
   }

   @Generated
   public char getCharacter() {
      return this.character;
   }

   @Generated
   public short getRed() {
      return this.red;
   }

   @Generated
   public short getGreen() {
      return this.green;
   }

   @Generated
   public short getBlue() {
      return this.blue;
   }

   @Generated
   public String getHexCode() {
      return this.hexCode;
   }

   // $FF: synthetic method
   private static EnumChatFormat[] $values() {
      return new EnumChatFormat[]{BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE, OBFUSCATED, BOLD, STRIKETHROUGH, UNDERLINE, ITALIC, RESET};
   }
}

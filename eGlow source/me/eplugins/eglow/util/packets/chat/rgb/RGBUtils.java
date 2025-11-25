package me.eplugins.eglow.util.packets.chat.rgb;

import java.util.regex.Pattern;
import lombok.Generated;
import me.eplugins.eglow.util.packets.chat.EnumChatFormat;
import me.eplugins.eglow.util.packets.chat.TextColor;
import me.eplugins.eglow.util.packets.chat.rgb.format.BukkitFormat;
import me.eplugins.eglow.util.packets.chat.rgb.format.CMIFormat;
import me.eplugins.eglow.util.packets.chat.rgb.format.HtmlFormat;
import me.eplugins.eglow.util.packets.chat.rgb.format.KyoriFormat;
import me.eplugins.eglow.util.packets.chat.rgb.format.RGBFormatter;
import me.eplugins.eglow.util.packets.chat.rgb.format.UnnamedFormat1;
import me.eplugins.eglow.util.packets.chat.rgb.gradient.CMIGradient;
import me.eplugins.eglow.util.packets.chat.rgb.gradient.CommonGradient;
import me.eplugins.eglow.util.packets.chat.rgb.gradient.GradientPattern;
import me.eplugins.eglow.util.packets.chat.rgb.gradient.KyoriGradient;

public class RGBUtils {
   private static final RGBUtils instance = new RGBUtils();
   private final RGBFormatter[] formats = new RGBFormatter[]{new BukkitFormat(), new CMIFormat(), new UnnamedFormat1(), new HtmlFormat(), new KyoriFormat()};
   private final GradientPattern[] gradients = new GradientPattern[]{new CMIGradient(), new CommonGradient(Pattern.compile("<#[0-9a-fA-F]{6}>[^<]*</#[0-9a-fA-F]{6}>"), Pattern.compile("<#[0-9a-fA-F]{6}\\|.>[^<]*</#[0-9a-fA-F]{6}>"), "<#", 9, 2, 9, 7), new CommonGradient(Pattern.compile("<\\$#[0-9a-fA-F]{6}>[^<]*<\\$#[0-9a-fA-F]{6}>"), Pattern.compile("<\\$#[0-9a-fA-F]{6}\\|.>[^<]*<\\$#[0-9a-fA-F]{6}>"), "<$", 10, 3, 10, 7), new KyoriGradient()};

   public String applyFormats(String text) {
      if (text == null) {
         return "";
      } else {
         String replaced = text;
         GradientPattern[] var3 = this.gradients;
         int var4 = var3.length;

         int var5;
         for(var5 = 0; var5 < var4; ++var5) {
            GradientPattern pattern = var3[var5];
            replaced = pattern.applyPattern(replaced, false);
         }

         RGBFormatter[] var7 = this.formats;
         var4 = var7.length;

         for(var5 = 0; var5 < var4; ++var5) {
            RGBFormatter formatter = var7[var5];
            replaced = formatter.reformat(replaced);
         }

         return replaced;
      }
   }

   public String convertRGBtoLegacy(String text) {
      if (text == null) {
         return null;
      } else if (!text.contains("#")) {
         return EnumChatFormat.color(text);
      } else {
         String applied = this.applyFormats(text);
         StringBuilder sb = new StringBuilder();

         for(int i = 0; i < applied.length(); ++i) {
            char c = applied.charAt(i);
            if (c == '#' && applied.length() > i + 6) {
               String hexCode = applied.substring(i + 1, i + 7);
               if (this.isHexCode(hexCode)) {
                  if (containsLegacyCode(applied, i)) {
                     sb.append((new TextColor(hexCode, EnumChatFormat.getByChar(applied.charAt(i + 8)))).getLegacyColor().getFormat());
                     i += 8;
                  } else {
                     sb.append((new TextColor(hexCode)).getLegacyColor().getFormat());
                     i += 6;
                  }
               } else {
                  sb.append(c);
               }
            } else {
               sb.append(c);
            }
         }

         return sb.toString();
      }
   }

   public boolean isHexCode(String string) {
      if (string == null) {
         return false;
      } else if (string.length() != 6) {
         return false;
      } else {
         for(int i = 0; i < 6; ++i) {
            char c = string.charAt(i);
            if (c < '0' || c > '9' && c < 'A' || c > 'F' && c < 'a' || c > 'f') {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean containsLegacyCode(String text, int i) {
      if (text.length() - i >= 9 && text.charAt(i + 7) == '|') {
         return EnumChatFormat.getByChar(text.charAt(i + 8)) != null;
      } else {
         return false;
      }
   }

   @Generated
   public static RGBUtils getInstance() {
      return instance;
   }
}

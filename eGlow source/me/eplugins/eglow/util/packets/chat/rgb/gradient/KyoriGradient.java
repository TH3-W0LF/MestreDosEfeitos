package me.eplugins.eglow.util.packets.chat.rgb.gradient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.eplugins.eglow.util.packets.chat.EnumChatFormat;
import me.eplugins.eglow.util.packets.chat.TextColor;

public class KyoriGradient implements GradientPattern {
   private final Pattern pattern = Pattern.compile("<gradient:#[0-9a-fA-F]{6}:#[0-9a-fA-F]{6}>[^<]*</gradient>");
   private final Pattern patternLegacy = Pattern.compile("<gradient:#[0-9a-fA-F]{6}\\|.:#[0-9a-fA-F]{6}>[^<]*</gradient>");

   public String applyPattern(String text, boolean ignorePlaceholders) {
      if (!text.contains("<grad")) {
         return text;
      } else {
         String replaced = text;
         Matcher m = this.patternLegacy.matcher(text);

         while(true) {
            String format;
            EnumChatFormat legacyColor;
            do {
               if (!m.find()) {
                  m = this.pattern.matcher(replaced);

                  while(true) {
                     do {
                        if (!m.find()) {
                           return replaced;
                        }

                        format = m.group();
                     } while(ignorePlaceholders && format.contains("%"));

                     TextColor start = new TextColor(format.substring(11, 17));
                     String message = format.substring(26, format.length() - 11);
                     TextColor end = new TextColor(format.substring(19, 25));
                     String applied = this.asGradient(start, message, end);
                     replaced = replaced.replace(format, applied);
                  }
               }

               format = m.group();
               legacyColor = EnumChatFormat.getByChar(format.charAt(18));
            } while(ignorePlaceholders && format.contains("%"));

            if (legacyColor != null) {
               TextColor start = new TextColor(format.substring(11, 17), legacyColor);
               String message = format.substring(28, format.length() - 11);
               TextColor end = new TextColor(format.substring(21, 27));
               String applied = this.asGradient(start, message, end);
               replaced = replaced.replace(format, applied);
            }
         }
      }
   }
}

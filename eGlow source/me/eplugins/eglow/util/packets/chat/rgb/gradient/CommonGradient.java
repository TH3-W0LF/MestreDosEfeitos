package me.eplugins.eglow.util.packets.chat.rgb.gradient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.eplugins.eglow.util.packets.chat.EnumChatFormat;
import me.eplugins.eglow.util.packets.chat.TextColor;

public class CommonGradient implements GradientPattern {
   private final Pattern pattern;
   private final Pattern legacyPattern;
   private final String containCheck;
   private final int legacyCharPosition;
   private final int startColorStart;
   private final int messageStart;
   private final int endColorStartSub;

   public CommonGradient(Pattern pattern, Pattern legacyPattern, String containCheck, int legacyCharPosition, int startColorStart, int messageStart, int endColorStartSub) {
      this.pattern = pattern;
      this.legacyPattern = legacyPattern;
      this.containCheck = containCheck;
      this.legacyCharPosition = legacyCharPosition;
      this.startColorStart = startColorStart;
      this.messageStart = messageStart;
      this.endColorStartSub = endColorStartSub;
   }

   public String applyPattern(String text, boolean ignorePlaceholders) {
      if (!text.contains(this.containCheck)) {
         return text;
      } else {
         String replaced = text;
         Matcher m = this.legacyPattern.matcher(text);

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

                     TextColor start = new TextColor(format.substring(this.startColorStart, this.startColorStart + 6));
                     String message = format.substring(this.messageStart, format.length() - 10);
                     TextColor end = new TextColor(format.substring(format.length() - this.endColorStartSub, format.length() - this.endColorStartSub + 6));
                     String applied = this.asGradient(start, message, end);
                     replaced = replaced.replace(format, applied);
                  }
               }

               format = m.group();
               legacyColor = EnumChatFormat.getByChar(format.charAt(this.legacyCharPosition));
            } while(ignorePlaceholders && format.contains("%"));

            if (legacyColor != null) {
               TextColor start = new TextColor(format.substring(this.startColorStart, this.startColorStart + 6), legacyColor);
               String message = format.substring(this.messageStart + 2, format.length() - 10);
               TextColor end = new TextColor(format.substring(format.length() - this.endColorStartSub, format.length() - this.endColorStartSub + 6));
               String applied = this.asGradient(start, message, end);
               replaced = replaced.replace(format, applied);
            }
         }
      }
   }
}

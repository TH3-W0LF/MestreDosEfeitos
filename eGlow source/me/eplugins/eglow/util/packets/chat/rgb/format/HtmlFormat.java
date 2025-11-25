package me.eplugins.eglow.util.packets.chat.rgb.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlFormat implements RGBFormatter {
   private final Pattern pattern = Pattern.compile("#<[0-9a-fA-F]{6}>");

   public String reformat(String text) {
      if (!text.contains("#<")) {
         return text;
      } else {
         Matcher m = this.pattern.matcher(text);

         String replaced;
         String hexCode;
         String fixed;
         for(replaced = text; m.find(); replaced = replaced.replace(hexCode, "#" + fixed)) {
            hexCode = m.group();
            fixed = hexCode.substring(2, 8);
         }

         return replaced;
      }
   }
}

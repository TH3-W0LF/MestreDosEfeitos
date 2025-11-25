package me.eplugins.eglow.util.packets.chat.rgb.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.eplugins.eglow.util.packets.chat.EnumChatFormat;

public class BukkitFormat implements RGBFormatter {
   private final Pattern pattern = Pattern.compile("[§&]x[§&\\p{XDigit}]{12}");

   public String reformat(String text) {
      if (!text.contains("&x") && !text.contains("§x")) {
         return text;
      } else {
         String replaced = text;

         String hexCode;
         String fixed;
         for(Matcher m = this.pattern.matcher(text); m.find(); replaced = replaced.replace(hexCode, EnumChatFormat.color(fixed))) {
            hexCode = m.group();
            fixed = new String(new char[]{'#', hexCode.charAt(3), hexCode.charAt(5), hexCode.charAt(7), hexCode.charAt(9), hexCode.charAt(11), hexCode.charAt(13)});
         }

         return replaced;
      }
   }
}

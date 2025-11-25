package me.eplugins.eglow.util.packets.chat.rgb.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CMIFormat implements RGBFormatter {
   private final Pattern pattern = Pattern.compile("\\{#[0-9a-fA-F]{6}}");

   public String reformat(String text) {
      if (!text.contains("{#")) {
         return text;
      } else {
         String replaced = text;

         String hexCode;
         String fixed;
         for(Matcher m = this.pattern.matcher(text); m.find(); replaced = replaced.replace(hexCode, "#" + fixed)) {
            hexCode = m.group();
            fixed = hexCode.substring(2, 8);
         }

         return replaced;
      }
   }
}

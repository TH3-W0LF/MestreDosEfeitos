package me.eplugins.eglow.util.packets.chat.rgb.gradient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CMIGradient extends CommonGradient {
   private final Pattern shortcutPattern = Pattern.compile("\\{#[0-9a-fA-F]{6}<>}");

   public CMIGradient() {
      super(Pattern.compile("\\{#[0-9a-fA-F]{6}>}[^{]*\\{#[0-9a-fA-F]{6}<}"), Pattern.compile("\\{#[0-9a-fA-F]{6}\\|.>}[^{]*\\{#[0-9a-fA-F]{6}<}"), "{#", 9, 2, 10, 8);
   }

   public String applyPattern(String text, boolean ignorePlaceholders) {
      String replaced = text;
      String format;
      String code;
      if (text.contains("<>}")) {
         for(Matcher m = this.shortcutPattern.matcher(text); m.find(); replaced = replaced.replace(format, "{#" + code + "<}{#" + code + ">}")) {
            format = m.group();
            code = format.substring(2, 8);
         }
      }

      return super.applyPattern(replaced, ignorePlaceholders);
   }
}

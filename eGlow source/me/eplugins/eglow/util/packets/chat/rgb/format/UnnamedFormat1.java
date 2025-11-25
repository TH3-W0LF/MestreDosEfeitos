package me.eplugins.eglow.util.packets.chat.rgb.format;

public class UnnamedFormat1 implements RGBFormatter {
   public String reformat(String text) {
      return text.contains("&#") ? text.replace("&#", "#") : text;
   }
}

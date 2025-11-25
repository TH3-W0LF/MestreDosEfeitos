package me.eplugins.eglow.util.packets.chat.rgb.gradient;

import me.eplugins.eglow.util.packets.chat.EnumChatFormat;
import me.eplugins.eglow.util.packets.chat.TextColor;

public interface GradientPattern {
   String applyPattern(String var1, boolean var2);

   default String asGradient(TextColor start, String text, TextColor end) {
      String magicCodes = EnumChatFormat.getLastColors(text);
      String deColorized = text.substring(magicCodes.length());
      StringBuilder sb = new StringBuilder();
      int length = deColorized.length();
      if (length == 1) {
         sb.append("#");
         sb.append((new TextColor(start.getRed(), start.getGreen(), start.getBlue())).getHexCode());
         if (start.isLegacyColorForced()) {
            sb.append("|").append(start.getLegacyColor().getCharacter());
         }

         sb.append(magicCodes);
         sb.append(deColorized);
         return sb.toString();
      } else {
         for(int i = 0; i < length; ++i) {
            int red = (int)((float)start.getRed() + (float)(end.getRed() - start.getRed()) / (float)(length - 1) * (float)i);
            int green = (int)((float)start.getGreen() + (float)(end.getGreen() - start.getGreen()) / (float)(length - 1) * (float)i);
            int blue = (int)((float)start.getBlue() + (float)(end.getBlue() - start.getBlue()) / (float)(length - 1) * (float)i);
            sb.append("#");
            sb.append((new TextColor(red, green, blue)).getHexCode());
            if (start.isLegacyColorForced()) {
               sb.append("|").append(start.getLegacyColor().getCharacter());
            }

            sb.append(magicCodes);
            sb.append(deColorized.charAt(i));
         }

         return sb.toString();
      }
   }
}

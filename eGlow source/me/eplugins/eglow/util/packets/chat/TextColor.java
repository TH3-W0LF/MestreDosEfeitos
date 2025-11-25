package me.eplugins.eglow.util.packets.chat;

import lombok.Generated;

public class TextColor {
   private int rgb = -1;
   private EnumChatFormat legacyColor;
   private String hexCode;
   private boolean legacyColorForced;

   public TextColor(TextColor color) {
      Preconditions.checkNotNull(color, "color");
      this.rgb = color.rgb;
      this.legacyColor = color.legacyColor;
      this.hexCode = color.hexCode;
      this.legacyColorForced = color.legacyColorForced;
   }

   public TextColor(String hexCode) {
      Preconditions.checkNotNull(hexCode, "hex code");
      this.hexCode = hexCode;
   }

   public TextColor(String hexCode, EnumChatFormat legacyColor) {
      Preconditions.checkNotNull(hexCode, "hex code");
      Preconditions.checkNotNull(legacyColor, "legacy color");
      this.hexCode = hexCode;
      this.legacyColorForced = true;
      this.legacyColor = legacyColor;
   }

   public TextColor(EnumChatFormat legacyColor) {
      Preconditions.checkNotNull(legacyColor, "legacy color");
      this.rgb = (legacyColor.getRed() << 16) + (legacyColor.getGreen() << 8) + legacyColor.getBlue();
      this.hexCode = legacyColor.getHexCode();
   }

   public TextColor(int red, int green, int blue) {
      Preconditions.checkRange(red, 0, 255, "red");
      Preconditions.checkRange(green, 0, 255, "green");
      Preconditions.checkRange(blue, 0, 255, "blue");
      this.rgb = (red << 16) + (green << 8) + blue;
   }

   private EnumChatFormat loadClosestColor() {
      double minMaxDist = 9999.0D;
      EnumChatFormat closestColor = EnumChatFormat.WHITE;
      EnumChatFormat[] var6 = EnumChatFormat.VALUES;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         EnumChatFormat color = var6[var8];
         int rDiff = Math.abs(color.getRed() - this.getRed());
         int gDiff = Math.abs(color.getGreen() - this.getGreen());
         int bDiff = Math.abs(color.getBlue() - this.getBlue());
         double maxDist = (double)rDiff;
         if ((double)gDiff > maxDist) {
            maxDist = (double)gDiff;
         }

         if ((double)bDiff > maxDist) {
            maxDist = (double)bDiff;
         }

         if (maxDist < minMaxDist) {
            minMaxDist = maxDist;
            closestColor = color;
         }
      }

      return closestColor;
   }

   public int getRed() {
      if (this.rgb == -1) {
         this.rgb = Integer.parseInt(this.hexCode, 16);
      }

      return this.rgb >> 16 & 255;
   }

   public int getGreen() {
      if (this.rgb == -1) {
         this.rgb = Integer.parseInt(this.hexCode, 16);
      }

      return this.rgb >> 8 & 255;
   }

   public int getBlue() {
      if (this.rgb == -1) {
         this.rgb = Integer.parseInt(this.hexCode, 16);
      }

      return this.rgb & 255;
   }

   public EnumChatFormat getLegacyColor() {
      if (this.legacyColor == null) {
         this.legacyColor = this.loadClosestColor();
      }

      return this.legacyColor;
   }

   public String getHexCode() {
      if (this.hexCode == null) {
         this.hexCode = String.format("%06X", this.rgb);
      }

      return this.hexCode;
   }

   public String toString() {
      EnumChatFormat legacyEquivalent = EnumChatFormat.fromRGBExact(this.getRed(), this.getGreen(), this.getBlue());
      return legacyEquivalent != null ? legacyEquivalent.toString().toLowerCase() : "#" + this.getHexCode();
   }

   public static TextColor fromString(String string) {
      if (string == null) {
         return null;
      } else {
         return string.startsWith("#") ? new TextColor(string.substring(1)) : new TextColor(EnumChatFormat.valueOf(string.toUpperCase()));
      }
   }

   @Generated
   public boolean isLegacyColorForced() {
      return this.legacyColorForced;
   }
}

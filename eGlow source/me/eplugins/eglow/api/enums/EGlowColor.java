package me.eplugins.eglow.api.enums;

public enum EGlowColor {
   RED,
   DARK_RED,
   GOLD,
   YELLOW,
   GREEN,
   DARK_GREEN,
   AQUA,
   DARK_AQUA,
   BLUE,
   DARK_BLUE,
   PURPLE,
   PINK,
   WHITE,
   GRAY,
   DARK_GRAY,
   BLACK,
   NONE;

   public String toString() {
      return super.toString().toLowerCase().replace("_", "");
   }

   // $FF: synthetic method
   private static EGlowColor[] $values() {
      return new EGlowColor[]{RED, DARK_RED, GOLD, YELLOW, GREEN, DARK_GREEN, AQUA, DARK_AQUA, BLUE, DARK_BLUE, PURPLE, PINK, WHITE, GRAY, DARK_GRAY, BLACK, NONE};
   }
}

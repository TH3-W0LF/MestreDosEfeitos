package me.eplugins.eglow.api.enums;

public enum EGlowEffect {
   RAINBOW_SLOW,
   RAINBOW_FAST;

   public String toString() {
      return super.toString().toLowerCase().replace("_", "");
   }

   // $FF: synthetic method
   private static EGlowEffect[] $values() {
      return new EGlowEffect[]{RAINBOW_SLOW, RAINBOW_FAST};
   }
}

package me.eplugins.eglow.util.packets.chat;

import java.awt.Color;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ChatColor {
   public static final char COLOR_CHAR = '§';
   public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-ORX]");
   private final String toString;

   private ChatColor(String toString) {
      this.toString = toString;
   }

   public int hashCode() {
      int hash = 7;
      int hash = 53 * hash + Objects.hashCode(this.toString);
      return hash;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         ChatColor other = (ChatColor)obj;
         return Objects.equals(this.toString, other.toString);
      } else {
         return false;
      }
   }

   public String toString() {
      return this.toString;
   }

   public static String stripColor(String input) {
      return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
   }

   public static ChatColor of(Color color) {
      return of("#" + String.format("%08x", color.getRGB()).substring(2));
   }

   public static ChatColor of(String string) {
      Preconditions.checkNotNull(string, "ChatColor text");
      if (string.startsWith("#") && string.length() == 7) {
         try {
            Integer.parseInt(string.substring(1), 16);
         } catch (NumberFormatException var6) {
            throw new IllegalArgumentException("Illegal hex string " + string);
         }

         StringBuilder magic = new StringBuilder("§x");
         char[] var2 = string.substring(1).toCharArray();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            char c = var2[var4];
            magic.append('§').append(c);
         }

         return new ChatColor(magic.toString());
      } else {
         throw new IllegalArgumentException("Could not parse ChatColor " + string);
      }
   }
}

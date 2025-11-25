package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.EmitterException;

public class Anchor {
   private static final Set<Character> INVALID_ANCHOR = new HashSet();
   private static final Pattern SPACES_PATTERN = Pattern.compile("\\s");
   private final String value;

   public Anchor(String var1) {
      Objects.requireNonNull(var1);
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("Empty anchor.");
      } else {
         for(int var2 = 0; var2 < var1.length(); ++var2) {
            char var3 = var1.charAt(var2);
            if (INVALID_ANCHOR.contains(var3)) {
               throw new EmitterException("Invalid character '" + var3 + "' in the anchor: " + var1);
            }
         }

         Matcher var4 = SPACES_PATTERN.matcher(var1);
         if (var4.find()) {
            throw new EmitterException("Anchor may not contain spaces: " + var1);
         } else {
            this.value = var1;
         }
      }
   }

   public String getValue() {
      return this.value;
   }

   public String toString() {
      return this.value;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Anchor var2 = (Anchor)var1;
         return Objects.equals(this.value, var2.value);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.value});
   }

   static {
      INVALID_ANCHOR.add('[');
      INVALID_ANCHOR.add(']');
      INVALID_ANCHOR.add('{');
      INVALID_ANCHOR.add('}');
      INVALID_ANCHOR.add(',');
      INVALID_ANCHOR.add('*');
      INVALID_ANCHOR.add('&');
   }
}

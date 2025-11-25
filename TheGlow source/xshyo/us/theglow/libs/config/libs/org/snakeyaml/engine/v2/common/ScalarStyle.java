package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common;

import java.util.Optional;

public enum ScalarStyle {
   DOUBLE_QUOTED(Optional.of('"')),
   SINGLE_QUOTED(Optional.of('\'')),
   LITERAL(Optional.of('|')),
   FOLDED(Optional.of('>')),
   JSON_SCALAR_STYLE(Optional.of('J')),
   PLAIN(Optional.empty());

   private final Optional<Character> styleOpt;

   private ScalarStyle(Optional<Character> var3) {
      this.styleOpt = var3;
   }

   public String toString() {
      return String.valueOf(this.styleOpt.orElse(':'));
   }

   // $FF: synthetic method
   private static ScalarStyle[] $values() {
      return new ScalarStyle[]{DOUBLE_QUOTED, SINGLE_QUOTED, LITERAL, FOLDED, JSON_SCALAR_STYLE, PLAIN};
   }
}

package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens;

import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public final class ScalarToken extends Token {
   private final String value;
   private final boolean plain;
   private final ScalarStyle style;

   public ScalarToken(String var1, boolean var2, Optional<Mark> var3, Optional<Mark> var4) {
      this(var1, var2, ScalarStyle.PLAIN, var3, var4);
   }

   public ScalarToken(String var1, boolean var2, ScalarStyle var3, Optional<Mark> var4, Optional<Mark> var5) {
      super(var4, var5);
      Objects.requireNonNull(var1);
      this.value = var1;
      this.plain = var2;
      Objects.requireNonNull(var3);
      this.style = var3;
   }

   public boolean isPlain() {
      return this.plain;
   }

   public String getValue() {
      return this.value;
   }

   public ScalarStyle getStyle() {
      return this.style;
   }

   public Token.ID getTokenId() {
      return Token.ID.Scalar;
   }

   public String toString() {
      return this.getTokenId().toString() + " plain=" + this.plain + " style=" + this.style + " value=" + this.value;
   }
}

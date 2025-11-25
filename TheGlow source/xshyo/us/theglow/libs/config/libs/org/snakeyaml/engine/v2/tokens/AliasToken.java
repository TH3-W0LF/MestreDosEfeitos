package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens;

import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.Anchor;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public final class AliasToken extends Token {
   private final Anchor value;

   public AliasToken(Anchor var1, Optional<Mark> var2, Optional<Mark> var3) {
      super(var2, var3);
      Objects.requireNonNull(var1);
      this.value = var1;
   }

   public Anchor getValue() {
      return this.value;
   }

   public Token.ID getTokenId() {
      return Token.ID.Alias;
   }
}

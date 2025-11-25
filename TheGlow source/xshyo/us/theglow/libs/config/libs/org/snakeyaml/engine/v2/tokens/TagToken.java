package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens;

import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public final class TagToken extends Token {
   private final TagTuple value;

   public TagToken(TagTuple var1, Optional<Mark> var2, Optional<Mark> var3) {
      super(var2, var3);
      Objects.requireNonNull(var1);
      this.value = var1;
   }

   public TagTuple getValue() {
      return this.value;
   }

   public Token.ID getTokenId() {
      return Token.ID.Tag;
   }
}

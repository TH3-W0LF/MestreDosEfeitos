package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens;

import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public final class FlowSequenceEndToken extends Token {
   public FlowSequenceEndToken(Optional<Mark> var1, Optional<Mark> var2) {
      super(var1, var2);
   }

   public Token.ID getTokenId() {
      return Token.ID.FlowSequenceEnd;
   }
}

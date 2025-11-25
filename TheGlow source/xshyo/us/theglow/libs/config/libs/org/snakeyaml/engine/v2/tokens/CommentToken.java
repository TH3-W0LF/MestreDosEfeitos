package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens;

import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentType;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public final class CommentToken extends Token {
   private final CommentType type;
   private final String value;

   public CommentToken(CommentType var1, String var2, Optional<Mark> var3, Optional<Mark> var4) {
      super(var3, var4);
      Objects.requireNonNull(var1);
      this.type = var1;
      Objects.requireNonNull(var2);
      this.value = var2;
   }

   public CommentType getCommentType() {
      return this.type;
   }

   public String getValue() {
      return this.value;
   }

   public Token.ID getTokenId() {
      return Token.ID.Comment;
   }
}

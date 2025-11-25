package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events;

import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentType;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public final class CommentEvent extends Event {
   private final CommentType type;
   private final String value;

   public CommentEvent(CommentType var1, String var2, Optional<Mark> var3, Optional<Mark> var4) {
      super(var3, var4);
      Objects.requireNonNull(var1);
      this.type = var1;
      Objects.requireNonNull(var2);
      this.value = var2;
   }

   public String getValue() {
      return this.value;
   }

   public CommentType getCommentType() {
      return this.type;
   }

   public Event.ID getEventId() {
      return Event.ID.Comment;
   }

   public String toString() {
      String var1 = "=COM " + this.type + " " + this.value;
      return var1;
   }
}

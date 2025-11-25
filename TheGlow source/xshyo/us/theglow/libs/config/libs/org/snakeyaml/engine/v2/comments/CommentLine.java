package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments;

import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.CommentEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public class CommentLine {
   private final Optional<Mark> startMark;
   private final Optional<Mark> endMark;
   private final String value;
   private final CommentType commentType;

   public CommentLine(CommentEvent var1) {
      this(var1.getStartMark(), var1.getEndMark(), var1.getValue(), var1.getCommentType());
   }

   public CommentLine(Optional<Mark> var1, Optional<Mark> var2, String var3, CommentType var4) {
      Objects.requireNonNull(var1);
      this.startMark = var1;
      Objects.requireNonNull(var2);
      this.endMark = var2;
      Objects.requireNonNull(var3);
      this.value = var3;
      Objects.requireNonNull(var4);
      this.commentType = var4;
   }

   public Optional<Mark> getEndMark() {
      return this.endMark;
   }

   public Optional<Mark> getStartMark() {
      return this.startMark;
   }

   public CommentType getCommentType() {
      return this.commentType;
   }

   public String getValue() {
      return this.value;
   }

   public String toString() {
      return "<" + this.getClass().getName() + " (type=" + this.getCommentType() + ", value=" + this.getValue() + ")>";
   }
}

package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events;

import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public abstract class Event {
   private final Optional<Mark> startMark;
   private final Optional<Mark> endMark;

   public Event(Optional<Mark> var1, Optional<Mark> var2) {
      if ((!var1.isPresent() || var2.isPresent()) && (var1.isPresent() || !var2.isPresent())) {
         this.startMark = var1;
         this.endMark = var2;
      } else {
         throw new NullPointerException("Both marks must be either present or absent.");
      }
   }

   public Event() {
      this(Optional.empty(), Optional.empty());
   }

   public Optional<Mark> getStartMark() {
      return this.startMark;
   }

   public Optional<Mark> getEndMark() {
      return this.endMark;
   }

   public abstract Event.ID getEventId();

   public static enum ID {
      Alias,
      Comment,
      DocumentEnd,
      DocumentStart,
      MappingEnd,
      MappingStart,
      Scalar,
      SequenceEnd,
      SequenceStart,
      StreamEnd,
      StreamStart;

      // $FF: synthetic method
      private static Event.ID[] $values() {
         return new Event.ID[]{Alias, Comment, DocumentEnd, DocumentStart, MappingEnd, MappingStart, Scalar, SequenceEnd, SequenceStart, StreamEnd, StreamStart};
      }
   }
}

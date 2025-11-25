package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events;

import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public final class StreamStartEvent extends Event {
   public StreamStartEvent(Optional<Mark> var1, Optional<Mark> var2) {
      super(var1, var2);
   }

   public StreamStartEvent() {
   }

   public Event.ID getEventId() {
      return Event.ID.StreamStart;
   }

   public String toString() {
      return "+STR";
   }
}

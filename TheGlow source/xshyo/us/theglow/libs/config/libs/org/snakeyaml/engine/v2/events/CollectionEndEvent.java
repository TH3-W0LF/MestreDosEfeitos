package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events;

import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public abstract class CollectionEndEvent extends Event {
   public CollectionEndEvent(Optional<Mark> var1, Optional<Mark> var2) {
      super(var1, var2);
   }

   public CollectionEndEvent() {
   }
}

package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events;

import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public final class DocumentEndEvent extends Event {
   private final boolean explicit;

   public DocumentEndEvent(boolean var1, Optional<Mark> var2, Optional<Mark> var3) {
      super(var2, var3);
      this.explicit = var1;
   }

   public DocumentEndEvent(boolean var1) {
      this(var1, Optional.empty(), Optional.empty());
   }

   public boolean isExplicit() {
      return this.explicit;
   }

   public Event.ID getEventId() {
      return Event.ID.DocumentEnd;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("-DOC");
      if (this.isExplicit()) {
         var1.append(" ...");
      }

      return var1.toString();
   }
}

package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events;

import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.Anchor;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public final class AliasEvent extends NodeEvent {
   private final Anchor alias;

   public AliasEvent(Optional<Anchor> var1, Optional<Mark> var2, Optional<Mark> var3) {
      super(var1, var2, var3);
      this.alias = (Anchor)var1.orElseThrow(() -> {
         return new NullPointerException("Anchor is required in AliasEvent");
      });
   }

   public AliasEvent(Optional<Anchor> var1) {
      this(var1, Optional.empty(), Optional.empty());
   }

   public Event.ID getEventId() {
      return Event.ID.Alias;
   }

   public String toString() {
      return "=ALI *" + this.alias;
   }

   public Anchor getAlias() {
      return this.alias;
   }
}

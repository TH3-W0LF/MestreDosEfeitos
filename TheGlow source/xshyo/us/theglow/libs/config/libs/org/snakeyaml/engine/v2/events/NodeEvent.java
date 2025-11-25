package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events;

import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.Anchor;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public abstract class NodeEvent extends Event {
   private final Optional<Anchor> anchor;

   public NodeEvent(Optional<Anchor> var1, Optional<Mark> var2, Optional<Mark> var3) {
      super(var2, var3);
      Objects.requireNonNull(var1);
      this.anchor = var1;
   }

   public Optional<Anchor> getAnchor() {
      return this.anchor;
   }
}

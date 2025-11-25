package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events;

import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.Anchor;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public final class MappingStartEvent extends CollectionStartEvent {
   public MappingStartEvent(Optional<Anchor> var1, Optional<String> var2, boolean var3, FlowStyle var4, Optional<Mark> var5, Optional<Mark> var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public MappingStartEvent(Optional<Anchor> var1, Optional<String> var2, boolean var3, FlowStyle var4) {
      this(var1, var2, var3, var4, Optional.empty(), Optional.empty());
   }

   public Event.ID getEventId() {
      return Event.ID.MappingStart;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("+MAP");
      if (this.getFlowStyle() == FlowStyle.FLOW) {
         var1.append(" {}");
      }

      var1.append(super.toString());
      return var1.toString();
   }
}

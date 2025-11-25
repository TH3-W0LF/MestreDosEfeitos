package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public abstract class CollectionNode<T> extends Node {
   private FlowStyle flowStyle;

   public CollectionNode(Tag var1, FlowStyle var2, Optional<Mark> var3, Optional<Mark> var4) {
      super(var1, var3, var4);
      this.setFlowStyle(var2);
   }

   public abstract List<T> getValue();

   public FlowStyle getFlowStyle() {
      return this.flowStyle;
   }

   public void setFlowStyle(FlowStyle var1) {
      Objects.requireNonNull(var1, "Flow style must be provided.");
      this.flowStyle = var1;
   }

   public void setEndMark(Optional<Mark> var1) {
      this.endMark = var1;
   }
}

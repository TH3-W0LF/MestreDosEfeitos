package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events;

import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.Anchor;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public abstract class CollectionStartEvent extends NodeEvent {
   private final Optional<String> tag;
   private final boolean implicit;
   private final FlowStyle flowStyle;

   public CollectionStartEvent(Optional<Anchor> var1, Optional<String> var2, boolean var3, FlowStyle var4, Optional<Mark> var5, Optional<Mark> var6) {
      super(var1, var5, var6);
      Objects.requireNonNull(var2);
      this.tag = var2;
      this.implicit = var3;
      Objects.requireNonNull(var4);
      this.flowStyle = var4;
   }

   public Optional<String> getTag() {
      return this.tag;
   }

   public boolean isImplicit() {
      return this.implicit;
   }

   public FlowStyle getFlowStyle() {
      return this.flowStyle;
   }

   public boolean isFlow() {
      return FlowStyle.FLOW == this.flowStyle;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      this.getAnchor().ifPresent((var1x) -> {
         var1.append(" &" + var1x);
      });
      if (!this.implicit) {
         this.getTag().ifPresent((var1x) -> {
            var1.append(" <" + var1x + ">");
         });
      }

      return var1.toString();
   }
}

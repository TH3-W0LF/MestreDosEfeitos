package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public class MappingNode extends CollectionNode<NodeTuple> {
   private List<NodeTuple> value;

   public MappingNode(Tag var1, boolean var2, List<NodeTuple> var3, FlowStyle var4, Optional<Mark> var5, Optional<Mark> var6) {
      super(var1, var4, var5, var6);
      Objects.requireNonNull(var3);
      this.value = var3;
      this.resolved = var2;
   }

   public MappingNode(Tag var1, List<NodeTuple> var2, FlowStyle var3) {
      this(var1, true, var2, var3, Optional.empty(), Optional.empty());
   }

   public NodeType getNodeType() {
      return NodeType.MAPPING;
   }

   public List<NodeTuple> getValue() {
      return this.value;
   }

   public void setValue(List<NodeTuple> var1) {
      Objects.requireNonNull(var1);
      this.value = var1;
   }

   public String toString() {
      StringBuilder var2 = new StringBuilder();

      for(Iterator var3 = this.getValue().iterator(); var3.hasNext(); var2.append(" }")) {
         NodeTuple var4 = (NodeTuple)var3.next();
         var2.append("{ key=");
         var2.append(var4.getKeyNode());
         var2.append("; value=");
         if (var4.getValueNode() instanceof CollectionNode) {
            var2.append(System.identityHashCode(var4.getValueNode()));
         } else {
            var2.append(var4);
         }
      }

      String var1 = var2.toString();
      return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", values=" + var1 + ")>";
   }
}

package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public class SequenceNode extends CollectionNode<Node> {
   private final List<Node> value;

   public SequenceNode(Tag var1, boolean var2, List<Node> var3, FlowStyle var4, Optional<Mark> var5, Optional<Mark> var6) {
      super(var1, var4, var5, var6);
      Objects.requireNonNull(var3, "value in a Node is required.");
      this.value = var3;
      this.resolved = var2;
   }

   public SequenceNode(Tag var1, List<Node> var2, FlowStyle var3) {
      this(var1, true, var2, var3, Optional.empty(), Optional.empty());
   }

   public NodeType getNodeType() {
      return NodeType.SEQUENCE;
   }

   public List<Node> getValue() {
      return this.value;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();

      for(Iterator var2 = this.getValue().iterator(); var2.hasNext(); var1.append(",")) {
         Node var3 = (Node)var2.next();
         if (var3 instanceof CollectionNode) {
            var1.append(System.identityHashCode(var3));
         } else {
            var1.append(var3.toString());
         }
      }

      if (var1.length() > 0) {
         var1.deleteCharAt(var1.length() - 1);
      }

      return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", value=[" + var1 + "])>";
   }
}

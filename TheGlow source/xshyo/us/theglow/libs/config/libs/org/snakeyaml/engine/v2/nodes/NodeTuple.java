package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes;

import java.util.Objects;

public final class NodeTuple {
   private final Node keyNode;
   private final Node valueNode;

   public NodeTuple(Node var1, Node var2) {
      Objects.requireNonNull(var1, "keyNode must be provided.");
      Objects.requireNonNull(var2, "value Node must be provided");
      this.keyNode = var1;
      this.valueNode = var2;
   }

   public Node getKeyNode() {
      return this.keyNode;
   }

   public Node getValueNode() {
      return this.valueNode;
   }

   public String toString() {
      return "<NodeTuple keyNode=" + this.keyNode + "; valueNode=" + this.valueNode + ">";
   }
}

package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes;

public class AnchorNode extends Node {
   private final Node realNode;

   public AnchorNode(Node var1) {
      super(var1.getTag(), var1.getStartMark(), var1.getEndMark());
      this.realNode = var1;
   }

   public NodeType getNodeType() {
      return NodeType.ANCHOR;
   }

   public Node getRealNode() {
      return this.realNode;
   }
}

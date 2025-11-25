package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes;

public enum NodeType {
   SCALAR,
   SEQUENCE,
   MAPPING,
   ANCHOR;

   // $FF: synthetic method
   private static NodeType[] $values() {
      return new NodeType[]{SCALAR, SEQUENCE, MAPPING, ANCHOR};
   }
}

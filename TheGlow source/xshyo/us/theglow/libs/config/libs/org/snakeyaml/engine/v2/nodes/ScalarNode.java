package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes;

import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public class ScalarNode extends Node {
   private final ScalarStyle style;
   private final String value;

   public ScalarNode(Tag var1, boolean var2, String var3, ScalarStyle var4, Optional<Mark> var5, Optional<Mark> var6) {
      super(var1, var5, var6);
      Objects.requireNonNull(var3, "value in a Node is required.");
      this.value = var3;
      Objects.requireNonNull(var4, "Scalar style must be provided.");
      this.style = var4;
      this.resolved = var2;
   }

   public ScalarNode(Tag var1, String var2, ScalarStyle var3) {
      this(var1, true, var2, var3, Optional.empty(), Optional.empty());
   }

   public ScalarStyle getScalarStyle() {
      return this.style;
   }

   public NodeType getNodeType() {
      return NodeType.SCALAR;
   }

   public String getValue() {
      return this.value;
   }

   public String toString() {
      return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", value=" + this.getValue() + ")>";
   }

   public boolean isPlain() {
      return this.style == ScalarStyle.PLAIN;
   }
}

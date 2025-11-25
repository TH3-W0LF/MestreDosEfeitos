package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.json;

import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.ConstructScalar;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.ConstructorException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.NodeType;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.resolver.ScalarResolver;

public class ConstructOptionalClass extends ConstructScalar {
   private final ScalarResolver scalarResolver;

   public ConstructOptionalClass(ScalarResolver var1) {
      this.scalarResolver = var1;
   }

   public Object construct(Node var1) {
      if (var1.getNodeType() != NodeType.SCALAR) {
         throw new ConstructorException("while constructing Optional", Optional.empty(), "found non scalar node", var1.getStartMark());
      } else {
         String var2 = this.constructScalar(var1);
         Tag var3 = this.scalarResolver.resolve(var2, true);
         return var3.equals(Tag.NULL) ? Optional.empty() : Optional.of(var2);
      }
   }
}

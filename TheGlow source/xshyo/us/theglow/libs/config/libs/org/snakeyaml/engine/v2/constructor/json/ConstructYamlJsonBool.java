package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.json;

import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.ConstructScalar;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;

public class ConstructYamlJsonBool extends ConstructScalar {
   public Object construct(Node var1) {
      String var2 = this.constructScalar(var1);
      return BOOL_VALUES.get(var2);
   }
}

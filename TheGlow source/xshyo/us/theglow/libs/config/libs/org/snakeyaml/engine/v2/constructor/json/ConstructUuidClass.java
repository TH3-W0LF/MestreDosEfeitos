package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.json;

import java.util.UUID;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.ConstructScalar;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;

public class ConstructUuidClass extends ConstructScalar {
   public Object construct(Node var1) {
      String var2 = this.constructScalar(var1);
      return UUID.fromString(var2);
   }
}

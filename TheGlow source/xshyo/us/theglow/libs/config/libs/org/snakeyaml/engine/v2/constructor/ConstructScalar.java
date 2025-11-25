package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor;

import java.util.HashMap;
import java.util.Map;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;

public abstract class ConstructScalar implements ConstructNode {
   protected static final Map<String, Boolean> BOOL_VALUES = new HashMap();

   protected String constructScalar(Node var1) {
      return ((ScalarNode)var1).getValue();
   }

   static {
      BOOL_VALUES.put("true", Boolean.TRUE);
      BOOL_VALUES.put("false", Boolean.FALSE);
   }
}

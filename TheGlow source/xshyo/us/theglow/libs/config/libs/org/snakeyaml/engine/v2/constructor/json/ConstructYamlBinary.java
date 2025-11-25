package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.json;

import java.util.Base64;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.ConstructScalar;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;

public class ConstructYamlBinary extends ConstructScalar {
   public Object construct(Node var1) {
      String var2 = this.constructScalar(var1).replaceAll("\\s", "");
      return Base64.getDecoder().decode(var2);
   }
}

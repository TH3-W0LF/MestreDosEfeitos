package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.json;

import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.ConstructScalar;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;

public class ConstructYamlJsonFloat extends ConstructScalar {
   public Object construct(Node var1) {
      String var2 = this.constructScalar(var1);
      if (".inf".equals(var2)) {
         return Double.POSITIVE_INFINITY;
      } else if ("-.inf".equals(var2)) {
         return Double.NEGATIVE_INFINITY;
      } else {
         return ".nan".equals(var2) ? Double.NaN : this.constructFromString(var2);
      }
   }

   protected Object constructFromString(String var1) {
      byte var2 = 1;
      char var3 = var1.charAt(0);
      if (var3 == '-') {
         var2 = -1;
         var1 = var1.substring(1);
      } else if (var3 == '+') {
         var1 = var1.substring(1);
      }

      double var4 = Double.valueOf(var1);
      return var4 * (double)var2;
   }
}

package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.json;

import java.math.BigInteger;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.ConstructScalar;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;

public class ConstructYamlJsonInt extends ConstructScalar {
   public Object construct(Node var1) {
      String var2 = this.constructScalar(var1);
      return this.createIntNumber(var2);
   }

   protected Number createIntNumber(String var1) {
      Object var2;
      try {
         var2 = Integer.valueOf(var1);
      } catch (NumberFormatException var6) {
         try {
            var2 = Long.valueOf(var1);
         } catch (NumberFormatException var5) {
            var2 = new BigInteger(var1);
         }
      }

      return (Number)var2;
   }
}

package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api;

import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;

public interface ConstructNode {
   Object construct(Node var1);

   default void constructRecursive(Node var1, Object var2) {
      if (var1.isRecursive()) {
         throw new IllegalStateException("Not implemented in " + this.getClass().getName());
      } else {
         throw new YamlEngineException("Unexpected recursive structure for Node: " + var1);
      }
   }
}

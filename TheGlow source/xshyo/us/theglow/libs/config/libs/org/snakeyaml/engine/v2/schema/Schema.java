package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.schema;

import java.util.Map;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.resolver.ScalarResolver;

public interface Schema {
   ScalarResolver getScalarResolver();

   Map<Tag, ConstructNode> getSchemaTagConstructors();
}

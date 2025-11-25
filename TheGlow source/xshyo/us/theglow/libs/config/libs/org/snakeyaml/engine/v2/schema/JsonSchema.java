package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.schema;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.ConstructYamlNull;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.json.ConstructOptionalClass;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.json.ConstructUuidClass;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.json.ConstructYamlBinary;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.json.ConstructYamlJsonBool;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.json.ConstructYamlJsonFloat;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.json.ConstructYamlJsonInt;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.resolver.JsonScalarResolver;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.resolver.ScalarResolver;

public class JsonSchema implements Schema {
   private final Map<Tag, ConstructNode> tagConstructors = new HashMap();
   private final ScalarResolver scalarResolver = new JsonScalarResolver();

   public JsonSchema() {
      this.tagConstructors.put(Tag.NULL, new ConstructYamlNull());
      this.tagConstructors.put(Tag.BOOL, new ConstructYamlJsonBool());
      this.tagConstructors.put(Tag.INT, new ConstructYamlJsonInt());
      this.tagConstructors.put(Tag.FLOAT, new ConstructYamlJsonFloat());
      this.tagConstructors.put(Tag.BINARY, new ConstructYamlBinary());
      this.tagConstructors.put(new Tag(UUID.class), new ConstructUuidClass());
      this.tagConstructors.put(new Tag(Optional.class), new ConstructOptionalClass(this.getScalarResolver()));
   }

   public ScalarResolver getScalarResolver() {
      return this.scalarResolver;
   }

   public Map<Tag, ConstructNode> getSchemaTagConstructors() {
      return this.tagConstructors;
   }
}

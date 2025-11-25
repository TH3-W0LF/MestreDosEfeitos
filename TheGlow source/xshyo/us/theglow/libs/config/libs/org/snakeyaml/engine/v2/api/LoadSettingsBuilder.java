package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.env.EnvConfig;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlVersionException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.schema.JsonSchema;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.schema.Schema;

public final class LoadSettingsBuilder {
   private final Map<SettingKey, Object> customProperties = new HashMap();
   private String label = "reader";
   private Map<Tag, ConstructNode> tagConstructors = new HashMap();
   private IntFunction<List<Object>> defaultList = ArrayList::new;
   private IntFunction<Set<Object>> defaultSet = LinkedHashSet::new;
   private IntFunction<Map<Object, Object>> defaultMap = LinkedHashMap::new;
   private UnaryOperator<SpecVersion> versionFunction = (var0) -> {
      if (var0.getMajor() != 1) {
         throw new YamlVersionException(var0);
      } else {
         return var0;
      }
   };
   private Integer bufferSize = 1024;
   private boolean allowDuplicateKeys = false;
   private boolean allowRecursiveKeys = false;
   private boolean parseComments = false;
   private int maxAliasesForCollections = 50;
   private boolean useMarks = true;
   private Optional<EnvConfig> envConfig = Optional.empty();
   private int codePointLimit = 3145728;
   private Schema schema = new JsonSchema();

   LoadSettingsBuilder() {
   }

   public LoadSettingsBuilder setLabel(String var1) {
      Objects.requireNonNull(var1, "label cannot be null");
      this.label = var1;
      return this;
   }

   public LoadSettingsBuilder setTagConstructors(Map<Tag, ConstructNode> var1) {
      this.tagConstructors = var1;
      return this;
   }

   public LoadSettingsBuilder setDefaultList(IntFunction<List<Object>> var1) {
      Objects.requireNonNull(var1, "defaultList cannot be null");
      this.defaultList = var1;
      return this;
   }

   public LoadSettingsBuilder setDefaultSet(IntFunction<Set<Object>> var1) {
      Objects.requireNonNull(var1, "defaultSet cannot be null");
      this.defaultSet = var1;
      return this;
   }

   public LoadSettingsBuilder setDefaultMap(IntFunction<Map<Object, Object>> var1) {
      Objects.requireNonNull(var1, "defaultMap cannot be null");
      this.defaultMap = var1;
      return this;
   }

   public LoadSettingsBuilder setBufferSize(Integer var1) {
      this.bufferSize = var1;
      return this;
   }

   public LoadSettingsBuilder setAllowDuplicateKeys(boolean var1) {
      this.allowDuplicateKeys = var1;
      return this;
   }

   public LoadSettingsBuilder setAllowRecursiveKeys(boolean var1) {
      this.allowRecursiveKeys = var1;
      return this;
   }

   public LoadSettingsBuilder setMaxAliasesForCollections(int var1) {
      this.maxAliasesForCollections = var1;
      return this;
   }

   public LoadSettingsBuilder setUseMarks(boolean var1) {
      this.useMarks = var1;
      return this;
   }

   public LoadSettingsBuilder setVersionFunction(UnaryOperator<SpecVersion> var1) {
      Objects.requireNonNull(var1, "versionFunction cannot be null");
      this.versionFunction = var1;
      return this;
   }

   public LoadSettingsBuilder setEnvConfig(Optional<EnvConfig> var1) {
      this.envConfig = var1;
      return this;
   }

   public LoadSettingsBuilder setCustomProperty(SettingKey var1, Object var2) {
      this.customProperties.put(var1, var2);
      return this;
   }

   public LoadSettingsBuilder setParseComments(boolean var1) {
      this.parseComments = var1;
      return this;
   }

   public LoadSettingsBuilder setCodePointLimit(int var1) {
      this.codePointLimit = var1;
      return this;
   }

   public LoadSettingsBuilder setSchema(Schema var1) {
      this.schema = var1;
      return this;
   }

   public LoadSettings build() {
      return new LoadSettings(this.label, this.tagConstructors, this.defaultList, this.defaultSet, this.defaultMap, this.versionFunction, this.bufferSize, this.allowDuplicateKeys, this.allowRecursiveKeys, this.maxAliasesForCollections, this.useMarks, this.customProperties, this.envConfig, this.parseComments, this.codePointLimit, this.schema);
   }
}

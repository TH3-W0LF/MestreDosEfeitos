package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.env.EnvConfig;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.schema.Schema;

public final class LoadSettings {
   private final String label;
   private final Map<Tag, ConstructNode> tagConstructors;
   private final IntFunction<List<Object>> defaultList;
   private final IntFunction<Set<Object>> defaultSet;
   private final IntFunction<Map<Object, Object>> defaultMap;
   private final UnaryOperator<SpecVersion> versionFunction;
   private final Integer bufferSize;
   private final boolean allowDuplicateKeys;
   private final boolean allowRecursiveKeys;
   private final boolean parseComments;
   private final int maxAliasesForCollections;
   private final boolean useMarks;
   private final Optional<EnvConfig> envConfig;
   private final int codePointLimit;
   private final Schema schema;
   private final Map<SettingKey, Object> customProperties;

   LoadSettings(String var1, Map<Tag, ConstructNode> var2, IntFunction<List<Object>> var3, IntFunction<Set<Object>> var4, IntFunction<Map<Object, Object>> var5, UnaryOperator<SpecVersion> var6, Integer var7, boolean var8, boolean var9, int var10, boolean var11, Map<SettingKey, Object> var12, Optional<EnvConfig> var13, boolean var14, int var15, Schema var16) {
      this.label = var1;
      this.tagConstructors = var2;
      this.defaultList = var3;
      this.defaultSet = var4;
      this.defaultMap = var5;
      this.versionFunction = var6;
      this.bufferSize = var7;
      this.allowDuplicateKeys = var8;
      this.allowRecursiveKeys = var9;
      this.parseComments = var14;
      this.maxAliasesForCollections = var10;
      this.useMarks = var11;
      this.customProperties = var12;
      this.envConfig = var13;
      this.codePointLimit = var15;
      this.schema = var16;
   }

   public static LoadSettingsBuilder builder() {
      return new LoadSettingsBuilder();
   }

   public String getLabel() {
      return this.label;
   }

   public Map<Tag, ConstructNode> getTagConstructors() {
      return this.tagConstructors;
   }

   public IntFunction<List<Object>> getDefaultList() {
      return this.defaultList;
   }

   public IntFunction<Set<Object>> getDefaultSet() {
      return this.defaultSet;
   }

   public IntFunction<Map<Object, Object>> getDefaultMap() {
      return this.defaultMap;
   }

   public Integer getBufferSize() {
      return this.bufferSize;
   }

   public boolean getAllowDuplicateKeys() {
      return this.allowDuplicateKeys;
   }

   public boolean getAllowRecursiveKeys() {
      return this.allowRecursiveKeys;
   }

   public boolean getUseMarks() {
      return this.useMarks;
   }

   public Function<SpecVersion, SpecVersion> getVersionFunction() {
      return this.versionFunction;
   }

   public Object getCustomProperty(SettingKey var1) {
      return this.customProperties.get(var1);
   }

   public int getMaxAliasesForCollections() {
      return this.maxAliasesForCollections;
   }

   public Optional<EnvConfig> getEnvConfig() {
      return this.envConfig;
   }

   public boolean getParseComments() {
      return this.parseComments;
   }

   public int getCodePointLimit() {
      return this.codePointLimit;
   }

   public Schema getSchema() {
      return this.schema;
   }
}

package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.NonPrintableStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.EmitterException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.schema.JsonSchema;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.schema.Schema;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.serializer.AnchorGenerator;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.serializer.NumberAnchorGenerator;

public final class DumpSettingsBuilder {
   Map<SettingKey, Object> customProperties = new HashMap();
   private boolean explicitStart = false;
   private boolean explicitEnd = false;
   private NonPrintableStyle nonPrintableStyle;
   private Optional<Tag> explicitRootTag = Optional.empty();
   private AnchorGenerator anchorGenerator = new NumberAnchorGenerator(0);
   private Optional<SpecVersion> yamlDirective = Optional.empty();
   private Map<String, String> tagDirective = new HashMap();
   private FlowStyle defaultFlowStyle;
   private ScalarStyle defaultScalarStyle;
   private boolean dereferenceAliases;
   private boolean canonical = false;
   private boolean multiLineFlow;
   private boolean useUnicodeEncoding = true;
   private int indent = 2;
   private int indicatorIndent = 0;
   private int width = 80;
   private String bestLineBreak = "\n";
   private boolean splitLines = true;
   private int maxSimpleKeyLength;
   private boolean indentWithIndicator;
   private boolean dumpComments;
   private Schema schema;

   DumpSettingsBuilder() {
      this.defaultFlowStyle = FlowStyle.AUTO;
      this.defaultScalarStyle = ScalarStyle.PLAIN;
      this.nonPrintableStyle = NonPrintableStyle.ESCAPE;
      this.maxSimpleKeyLength = 128;
      this.indentWithIndicator = false;
      this.dumpComments = false;
      this.schema = new JsonSchema();
      this.dereferenceAliases = false;
   }

   public DumpSettingsBuilder setDefaultFlowStyle(FlowStyle var1) {
      this.defaultFlowStyle = var1;
      return this;
   }

   public DumpSettingsBuilder setDefaultScalarStyle(ScalarStyle var1) {
      this.defaultScalarStyle = var1;
      return this;
   }

   public DumpSettingsBuilder setExplicitStart(boolean var1) {
      this.explicitStart = var1;
      return this;
   }

   public DumpSettingsBuilder setAnchorGenerator(AnchorGenerator var1) {
      Objects.requireNonNull(var1, "anchorGenerator cannot be null");
      this.anchorGenerator = var1;
      return this;
   }

   public DumpSettingsBuilder setExplicitRootTag(Optional<Tag> var1) {
      Objects.requireNonNull(var1, "explicitRootTag cannot be null");
      this.explicitRootTag = var1;
      return this;
   }

   public DumpSettingsBuilder setExplicitEnd(boolean var1) {
      this.explicitEnd = var1;
      return this;
   }

   public DumpSettingsBuilder setYamlDirective(Optional<SpecVersion> var1) {
      Objects.requireNonNull(var1, "yamlDirective cannot be null");
      this.yamlDirective = var1;
      return this;
   }

   public DumpSettingsBuilder setTagDirective(Map<String, String> var1) {
      Objects.requireNonNull(var1, "tagDirective cannot be null");
      this.tagDirective = var1;
      return this;
   }

   public DumpSettingsBuilder setCanonical(boolean var1) {
      this.canonical = var1;
      return this;
   }

   public DumpSettingsBuilder setMultiLineFlow(boolean var1) {
      this.multiLineFlow = var1;
      return this;
   }

   public DumpSettingsBuilder setUseUnicodeEncoding(boolean var1) {
      this.useUnicodeEncoding = var1;
      return this;
   }

   public DumpSettingsBuilder setIndent(int var1) {
      if (var1 < 1) {
         throw new EmitterException("Indent must be at least 1");
      } else if (var1 > 10) {
         throw new EmitterException("Indent must be at most 10");
      } else {
         this.indent = var1;
         return this;
      }
   }

   public DumpSettingsBuilder setIndicatorIndent(int var1) {
      if (var1 < 0) {
         throw new EmitterException("Indicator indent must be non-negative");
      } else if (var1 > 9) {
         throw new EmitterException("Indicator indent must be at most Emitter.MAX_INDENT-1: 9");
      } else {
         this.indicatorIndent = var1;
         return this;
      }
   }

   public DumpSettingsBuilder setWidth(int var1) {
      this.width = var1;
      return this;
   }

   public DumpSettingsBuilder setBestLineBreak(String var1) {
      Objects.requireNonNull(var1, "bestLineBreak cannot be null");
      this.bestLineBreak = var1;
      return this;
   }

   public DumpSettingsBuilder setSplitLines(boolean var1) {
      this.splitLines = var1;
      return this;
   }

   public DumpSettingsBuilder setMaxSimpleKeyLength(int var1) {
      if (var1 > 1024) {
         throw new YamlEngineException("The simple key must not span more than 1024 stream characters. See https://yaml.org/spec/1.2/spec.html#id2798057");
      } else {
         this.maxSimpleKeyLength = var1;
         return this;
      }
   }

   public DumpSettingsBuilder setNonPrintableStyle(NonPrintableStyle var1) {
      this.nonPrintableStyle = var1;
      return this;
   }

   public DumpSettingsBuilder setCustomProperty(SettingKey var1, Object var2) {
      this.customProperties.put(var1, var2);
      return this;
   }

   public DumpSettingsBuilder setIndentWithIndicator(boolean var1) {
      this.indentWithIndicator = var1;
      return this;
   }

   public DumpSettingsBuilder setDumpComments(boolean var1) {
      this.dumpComments = var1;
      return this;
   }

   public DumpSettingsBuilder setSchema(Schema var1) {
      this.schema = var1;
      return this;
   }

   public DumpSettingsBuilder setDereferenceAliases(Boolean var1) {
      this.dereferenceAliases = var1;
      return this;
   }

   public DumpSettings build() {
      return new DumpSettings(this.explicitStart, this.explicitEnd, this.explicitRootTag, this.anchorGenerator, this.yamlDirective, this.tagDirective, this.defaultFlowStyle, this.defaultScalarStyle, this.nonPrintableStyle, this.schema, this.dereferenceAliases, this.canonical, this.multiLineFlow, this.useUnicodeEncoding, this.indent, this.indicatorIndent, this.width, this.bestLineBreak, this.splitLines, this.maxSimpleKeyLength, this.customProperties, this.indentWithIndicator, this.dumpComments);
   }
}

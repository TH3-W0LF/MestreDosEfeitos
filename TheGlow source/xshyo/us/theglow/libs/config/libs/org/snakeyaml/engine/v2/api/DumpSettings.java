package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api;

import java.util.Map;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.NonPrintableStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.schema.Schema;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.serializer.AnchorGenerator;

public final class DumpSettings {
   private final boolean explicitStart;
   private final boolean explicitEnd;
   private final NonPrintableStyle nonPrintableStyle;
   private final Optional<Tag> explicitRootTag;
   private final AnchorGenerator anchorGenerator;
   private final Optional<SpecVersion> yamlDirective;
   private final Map<String, String> tagDirective;
   private final FlowStyle defaultFlowStyle;
   private final ScalarStyle defaultScalarStyle;
   private final Boolean dereferenceAliases;
   private final boolean canonical;
   private final boolean multiLineFlow;
   private final boolean useUnicodeEncoding;
   private final int indent;
   private final int indicatorIndent;
   private final int width;
   private final String bestLineBreak;
   private final boolean splitLines;
   private final int maxSimpleKeyLength;
   private final boolean indentWithIndicator;
   private final boolean dumpComments;
   private final Schema schema;
   private final Map<SettingKey, Object> customProperties;

   DumpSettings(boolean var1, boolean var2, Optional<Tag> var3, AnchorGenerator var4, Optional<SpecVersion> var5, Map<String, String> var6, FlowStyle var7, ScalarStyle var8, NonPrintableStyle var9, Schema var10, Boolean var11, boolean var12, boolean var13, boolean var14, int var15, int var16, int var17, String var18, boolean var19, int var20, Map<SettingKey, Object> var21, boolean var22, boolean var23) {
      this.explicitStart = var1;
      this.explicitEnd = var2;
      this.nonPrintableStyle = var9;
      this.explicitRootTag = var3;
      this.anchorGenerator = var4;
      this.yamlDirective = var5;
      this.tagDirective = var6;
      this.defaultFlowStyle = var7;
      this.defaultScalarStyle = var8;
      this.schema = var10;
      this.canonical = var12;
      this.multiLineFlow = var13;
      this.useUnicodeEncoding = var14;
      this.indent = var15;
      this.indicatorIndent = var16;
      this.width = var17;
      this.bestLineBreak = var18;
      this.splitLines = var19;
      this.maxSimpleKeyLength = var20;
      this.customProperties = var21;
      this.indentWithIndicator = var22;
      this.dumpComments = var23;
      this.dereferenceAliases = var11;
   }

   public static DumpSettingsBuilder builder() {
      return new DumpSettingsBuilder();
   }

   public FlowStyle getDefaultFlowStyle() {
      return this.defaultFlowStyle;
   }

   public ScalarStyle getDefaultScalarStyle() {
      return this.defaultScalarStyle;
   }

   public boolean isExplicitStart() {
      return this.explicitStart;
   }

   public AnchorGenerator getAnchorGenerator() {
      return this.anchorGenerator;
   }

   public boolean isExplicitEnd() {
      return this.explicitEnd;
   }

   public Optional<Tag> getExplicitRootTag() {
      return this.explicitRootTag;
   }

   public Optional<SpecVersion> getYamlDirective() {
      return this.yamlDirective;
   }

   public Map<String, String> getTagDirective() {
      return this.tagDirective;
   }

   public boolean isCanonical() {
      return this.canonical;
   }

   public boolean isMultiLineFlow() {
      return this.multiLineFlow;
   }

   public boolean isUseUnicodeEncoding() {
      return this.useUnicodeEncoding;
   }

   public int getIndent() {
      return this.indent;
   }

   public int getIndicatorIndent() {
      return this.indicatorIndent;
   }

   public int getWidth() {
      return this.width;
   }

   public String getBestLineBreak() {
      return this.bestLineBreak;
   }

   public boolean isSplitLines() {
      return this.splitLines;
   }

   public int getMaxSimpleKeyLength() {
      return this.maxSimpleKeyLength;
   }

   public NonPrintableStyle getNonPrintableStyle() {
      return this.nonPrintableStyle;
   }

   public Object getCustomProperty(SettingKey var1) {
      return this.customProperties.get(var1);
   }

   public boolean getIndentWithIndicator() {
      return this.indentWithIndicator;
   }

   public boolean getDumpComments() {
      return this.dumpComments;
   }

   public Schema getSchema() {
      return this.schema;
   }

   public Boolean isDereferenceAliases() {
      return this.dereferenceAliases;
   }
}

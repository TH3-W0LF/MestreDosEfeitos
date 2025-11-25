package xshyo.us.theglow.libs.config.settings.dumper;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.DumpSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.DumpSettingsBuilder;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.NonPrintableStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.schema.Schema;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.serializer.AnchorGenerator;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.serializer.NumberAnchorGenerator;
import xshyo.us.theglow.libs.config.settings.Settings;
import xshyo.us.theglow.libs.config.utils.format.Formatter;

public class DumperSettings implements Settings {
   public static final DumperSettings DEFAULT = builder().build();
   private final DumpSettingsBuilder builder;
   private final Supplier<AnchorGenerator> generatorSupplier;
   private final ScalarStyle stringStyle;
   private final Formatter<ScalarStyle, String> scalarFormatter;
   private final Formatter<FlowStyle, Iterable<?>> sequenceFormatter;
   private final Formatter<FlowStyle, Map<?, ?>> mappingFormatter;

   private DumperSettings(DumperSettings.Builder var1) {
      this.builder = var1.builder;
      this.generatorSupplier = var1.anchorGeneratorSupplier;
      this.scalarFormatter = var1.scalarFormatter;
      this.sequenceFormatter = var1.sequenceFormatter;
      this.mappingFormatter = var1.mappingFormatter;
      this.stringStyle = var1.stringStyle;
   }

   public DumpSettings buildEngineSettings() {
      return this.builder.setAnchorGenerator((AnchorGenerator)this.generatorSupplier.get()).setDumpComments(true).build();
   }

   public ScalarStyle getStringStyle() {
      return this.stringStyle;
   }

   public Formatter<ScalarStyle, String> getScalarFormatter() {
      return this.scalarFormatter;
   }

   public Formatter<FlowStyle, Iterable<?>> getSequenceFormatter() {
      return this.sequenceFormatter;
   }

   public Formatter<FlowStyle, Map<?, ?>> getMappingFormatter() {
      return this.mappingFormatter;
   }

   public static DumperSettings.Builder builder() {
      return new DumperSettings.Builder();
   }

   public static DumperSettings.Builder builder(DumpSettingsBuilder var0) {
      return new DumperSettings.Builder(var0);
   }

   public static DumperSettings.Builder builder(DumperSettings var0) {
      return builder(var0.builder).setAnchorGenerator(var0.generatorSupplier);
   }

   // $FF: synthetic method
   DumperSettings(DumperSettings.Builder var1, Object var2) {
      this(var1);
   }

   public static class Builder {
      public static final Supplier<AnchorGenerator> DEFAULT_ANCHOR_GENERATOR = () -> {
         return new NumberAnchorGenerator(0);
      };
      public static final FlowStyle DEFAULT_FLOW_STYLE;
      public static final ScalarStyle DEFAULT_SCALAR_STYLE;
      public static final Formatter<ScalarStyle, String> DEFAULT_SCALAR_FORMATTER;
      public static final Formatter<FlowStyle, Iterable<?>> DEFAULT_SEQUENCE_FORMATTER;
      public static final Formatter<FlowStyle, Map<?, ?>> DEFAULT_MAPPING_FORMATTER;
      public static final ScalarStyle DEFAULT_STRING_STYLE;
      public static final boolean DEFAULT_START_MARKER = false;
      public static final boolean DEFAULT_END_MARKER = false;
      public static final Tag DEFAULT_ROOT_TAG;
      public static final boolean DEFAULT_CANONICAL = false;
      public static final boolean DEFAULT_MULTILINE_FORMAT = false;
      public static final DumperSettings.Encoding DEFAULT_ENCODING;
      public static final int DEFAULT_INDENTATION = 2;
      public static final int DEFAULT_INDICATOR_INDENTATION = 0;
      public static final int DEFAULT_MAX_LINE_WIDTH = 0;
      public static final int DEFAULT_MAX_SIMPLE_KEY_LENGTH = 0;
      public static final boolean DEFAULT_ESCAPE_UNPRINTABLE = true;
      private final DumpSettingsBuilder builder;
      private Supplier<AnchorGenerator> anchorGeneratorSupplier;
      private Formatter<ScalarStyle, String> scalarFormatter;
      private Formatter<FlowStyle, Iterable<?>> sequenceFormatter;
      private Formatter<FlowStyle, Map<?, ?>> mappingFormatter;
      private ScalarStyle stringStyle;

      private Builder(DumpSettingsBuilder var1) {
         this.anchorGeneratorSupplier = DEFAULT_ANCHOR_GENERATOR;
         this.scalarFormatter = DEFAULT_SCALAR_FORMATTER;
         this.sequenceFormatter = DEFAULT_SEQUENCE_FORMATTER;
         this.mappingFormatter = DEFAULT_MAPPING_FORMATTER;
         this.stringStyle = DEFAULT_STRING_STYLE;
         this.builder = var1;
      }

      private Builder() {
         this.anchorGeneratorSupplier = DEFAULT_ANCHOR_GENERATOR;
         this.scalarFormatter = DEFAULT_SCALAR_FORMATTER;
         this.sequenceFormatter = DEFAULT_SEQUENCE_FORMATTER;
         this.mappingFormatter = DEFAULT_MAPPING_FORMATTER;
         this.stringStyle = DEFAULT_STRING_STYLE;
         this.builder = DumpSettings.builder();
         this.setFlowStyle(DEFAULT_FLOW_STYLE);
         this.setScalarStyle(DEFAULT_SCALAR_STYLE);
         this.setStringStyle(DEFAULT_STRING_STYLE);
         this.setStartMarker(false);
         this.setEndMarker(false);
         this.setRootTag(DEFAULT_ROOT_TAG);
         this.setCanonicalForm(false);
         this.setMultilineStyle(false);
         this.setEncoding(DEFAULT_ENCODING);
         this.setIndentation(2);
         this.setIndicatorIndentation(0);
         this.setLineWidth(0);
         this.setMaxSimpleKeyLength(0);
         this.setEscapeUnprintable(true);
      }

      public DumperSettings.Builder setAnchorGenerator(@NotNull Supplier<AnchorGenerator> var1) {
         this.anchorGeneratorSupplier = var1;
         return this;
      }

      public DumperSettings.Builder setFlowStyle(@NotNull FlowStyle var1) {
         this.builder.setDefaultFlowStyle(var1);
         return this;
      }

      public DumperSettings.Builder setScalarStyle(@NotNull ScalarStyle var1) {
         this.builder.setDefaultScalarStyle(var1);
         return this;
      }

      public DumperSettings.Builder setScalarFormatter(@NotNull Formatter<ScalarStyle, String> var1) {
         this.scalarFormatter = var1;
         return this;
      }

      public DumperSettings.Builder setSequenceFormatter(@NotNull Formatter<FlowStyle, Iterable<?>> var1) {
         this.sequenceFormatter = var1;
         return this;
      }

      public DumperSettings.Builder setMappingFormatter(@NotNull Formatter<FlowStyle, Map<?, ?>> var1) {
         this.mappingFormatter = var1;
         return this;
      }

      @Deprecated
      public DumperSettings.Builder setStringStyle(@NotNull ScalarStyle var1) {
         this.stringStyle = var1;
         return this;
      }

      public DumperSettings.Builder setStartMarker(boolean var1) {
         this.builder.setExplicitStart(var1);
         return this;
      }

      public DumperSettings.Builder setEndMarker(boolean var1) {
         this.builder.setExplicitEnd(var1);
         return this;
      }

      public DumperSettings.Builder setSchema(@NotNull Schema var1) {
         this.builder.setSchema(var1);
         return this;
      }

      public DumperSettings.Builder setRootTag(@Nullable Tag var1) {
         this.builder.setExplicitRootTag(Optional.ofNullable(var1));
         return this;
      }

      public DumperSettings.Builder setYamlDirective(@Nullable SpecVersion var1) {
         this.builder.setYamlDirective(Optional.ofNullable(var1));
         return this;
      }

      public DumperSettings.Builder setTagDirectives(@NotNull Map<String, String> var1) {
         this.builder.setTagDirective(var1);
         return this;
      }

      public DumperSettings.Builder setCanonicalForm(boolean var1) {
         this.builder.setCanonical(var1);
         return this;
      }

      public DumperSettings.Builder setMultilineStyle(boolean var1) {
         this.builder.setMultiLineFlow(var1);
         return this;
      }

      public DumperSettings.Builder setEncoding(@NotNull DumperSettings.Encoding var1) {
         this.builder.setUseUnicodeEncoding(var1.isUnicode());
         return this;
      }

      public DumperSettings.Builder setIndentation(int var1) {
         this.builder.setIndent(var1);
         return this;
      }

      public DumperSettings.Builder setIndicatorIndentation(int var1) {
         this.builder.setIndentWithIndicator(var1 > 0);
         this.builder.setIndicatorIndent(Math.max(var1, 0));
         return this;
      }

      public DumperSettings.Builder setLineWidth(int var1) {
         this.builder.setWidth(var1 <= 0 ? Integer.MAX_VALUE : var1);
         return this;
      }

      public DumperSettings.Builder setLineBreak(@NotNull String var1) {
         this.builder.setBestLineBreak(var1);
         return this;
      }

      public DumperSettings.Builder setMaxSimpleKeyLength(int var1) {
         if (var1 > 1018) {
            throw new IllegalArgumentException("Maximum simple key length is limited to 1018!");
         } else {
            this.builder.setMaxSimpleKeyLength(var1 <= 0 ? 1024 : var1 + 6);
            return this;
         }
      }

      public DumperSettings.Builder setEscapeUnprintable(boolean var1) {
         return this.setUnprintableStyle(var1 ? NonPrintableStyle.ESCAPE : NonPrintableStyle.BINARY);
      }

      public DumperSettings.Builder setUnprintableStyle(@NotNull NonPrintableStyle var1) {
         this.builder.setNonPrintableStyle(var1);
         return this;
      }

      public DumperSettings build() {
         return new DumperSettings(this);
      }

      // $FF: synthetic method
      Builder(Object var1) {
         this();
      }

      // $FF: synthetic method
      Builder(DumpSettingsBuilder var1, Object var2) {
         this(var1);
      }

      static {
         DEFAULT_FLOW_STYLE = FlowStyle.BLOCK;
         DEFAULT_SCALAR_STYLE = ScalarStyle.PLAIN;
         DEFAULT_SCALAR_FORMATTER = Formatter.identity();
         DEFAULT_SEQUENCE_FORMATTER = Formatter.identity();
         DEFAULT_MAPPING_FORMATTER = Formatter.identity();
         DEFAULT_STRING_STYLE = ScalarStyle.PLAIN;
         DEFAULT_ROOT_TAG = null;
         DEFAULT_ENCODING = DumperSettings.Encoding.UNICODE;
      }
   }

   public static enum Encoding {
      UNICODE,
      ASCII;

      boolean isUnicode() {
         return this == UNICODE;
      }
   }
}

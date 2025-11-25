package xshyo.us.theglow.libs.config.settings.general;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.serialization.YamlSerializer;
import xshyo.us.theglow.libs.config.serialization.standard.StandardSerializer;
import xshyo.us.theglow.libs.config.settings.Settings;
import xshyo.us.theglow.libs.config.utils.supplier.ListSupplier;
import xshyo.us.theglow.libs.config.utils.supplier.MapSupplier;
import xshyo.us.theglow.libs.config.utils.supplier.SetSupplier;

public class GeneralSettings implements Settings {
   public static final char DEFAULT_ROUTE_SEPARATOR = '.';
   public static final String DEFAULT_ESCAPED_SEPARATOR = Pattern.quote(String.valueOf('.'));
   public static final GeneralSettings.KeyFormat DEFAULT_KEY_FORMATTING;
   public static final YamlSerializer DEFAULT_SERIALIZER;
   public static final boolean DEFAULT_USE_DEFAULTS = true;
   public static final Object DEFAULT_OBJECT;
   public static final Number DEFAULT_NUMBER;
   public static final String DEFAULT_STRING;
   public static final Character DEFAULT_CHAR;
   public static final Boolean DEFAULT_BOOLEAN;
   public static final ListSupplier DEFAULT_LIST;
   public static final SetSupplier DEFAULT_SET;
   public static final MapSupplier DEFAULT_MAP;
   public static final GeneralSettings DEFAULT;
   private final GeneralSettings.KeyFormat keyFormat;
   private final char separator;
   private final String escapedSeparator;
   private final YamlSerializer serializer;
   private final boolean useDefaults;
   private final Object defaultObject;
   private final Number defaultNumber;
   private final String defaultString;
   private final Character defaultChar;
   private final Boolean defaultBoolean;
   private final ListSupplier defaultList;
   private final SetSupplier defaultSet;
   private final MapSupplier defaultMap;

   private GeneralSettings(GeneralSettings.Builder var1) {
      this.keyFormat = var1.keyFormat;
      this.separator = var1.routeSeparator;
      this.escapedSeparator = Pattern.quote(String.valueOf(this.separator));
      this.serializer = var1.serializer;
      this.defaultObject = var1.defaultObject;
      this.defaultNumber = var1.defaultNumber;
      this.defaultString = var1.defaultString;
      this.defaultChar = var1.defaultChar;
      this.defaultBoolean = var1.defaultBoolean;
      this.defaultList = var1.defaultList;
      this.defaultSet = var1.defaultSet;
      this.defaultMap = var1.defaultMap;
      this.useDefaults = var1.useDefaults;
   }

   public GeneralSettings.KeyFormat getKeyFormat() {
      return this.keyFormat;
   }

   public char getRouteSeparator() {
      return this.separator;
   }

   public String getEscapedSeparator() {
      return this.escapedSeparator;
   }

   public YamlSerializer getSerializer() {
      return this.serializer;
   }

   public boolean isUseDefaults() {
      return this.useDefaults;
   }

   public Object getDefaultObject() {
      return this.defaultObject;
   }

   public String getDefaultString() {
      return this.defaultString;
   }

   public Character getDefaultChar() {
      return this.defaultChar;
   }

   public Number getDefaultNumber() {
      return this.defaultNumber;
   }

   public Boolean getDefaultBoolean() {
      return this.defaultBoolean;
   }

   public <T> List<T> getDefaultList(int var1) {
      return this.defaultList.supply(var1);
   }

   public <T> List<T> getDefaultList() {
      return this.getDefaultList(0);
   }

   public <T> Set<T> getDefaultSet(int var1) {
      return this.defaultSet.supply(var1);
   }

   public <T> Set<T> getDefaultSet() {
      return this.getDefaultSet(0);
   }

   public <K, V> Map<K, V> getDefaultMap(int var1) {
      return this.defaultMap.supply(var1);
   }

   public <K, V> Map<K, V> getDefaultMap() {
      return this.getDefaultMap(0);
   }

   public MapSupplier getDefaultMapSupplier() {
      return this.defaultMap;
   }

   public static GeneralSettings.Builder builder() {
      return new GeneralSettings.Builder();
   }

   public static GeneralSettings.Builder builder(GeneralSettings var0) {
      return builder().setKeyFormat(var0.keyFormat).setRouteSeparator(var0.separator).setSerializer(var0.serializer).setUseDefaults(var0.useDefaults).setDefaultObject(var0.defaultObject).setDefaultNumber(var0.defaultNumber).setDefaultString(var0.defaultString).setDefaultChar(var0.defaultChar).setDefaultBoolean(var0.defaultBoolean).setDefaultList(var0.defaultList).setDefaultSet(var0.defaultSet).setDefaultMap(var0.defaultMap);
   }

   // $FF: synthetic method
   GeneralSettings(GeneralSettings.Builder var1, Object var2) {
      this(var1);
   }

   static {
      DEFAULT_KEY_FORMATTING = GeneralSettings.KeyFormat.STRING;
      DEFAULT_SERIALIZER = StandardSerializer.getDefault();
      DEFAULT_OBJECT = null;
      DEFAULT_NUMBER = 0;
      DEFAULT_STRING = null;
      DEFAULT_CHAR = ' ';
      DEFAULT_BOOLEAN = false;
      DEFAULT_LIST = ArrayList::new;
      DEFAULT_SET = LinkedHashSet::new;
      DEFAULT_MAP = LinkedHashMap::new;
      DEFAULT = builder().build();
   }

   public static class Builder {
      private GeneralSettings.KeyFormat keyFormat;
      private char routeSeparator;
      private YamlSerializer serializer;
      private boolean useDefaults;
      private Object defaultObject;
      private Number defaultNumber;
      private String defaultString;
      private Character defaultChar;
      private Boolean defaultBoolean;
      private ListSupplier defaultList;
      private SetSupplier defaultSet;
      private MapSupplier defaultMap;

      private Builder() {
         this.keyFormat = GeneralSettings.DEFAULT_KEY_FORMATTING;
         this.routeSeparator = '.';
         this.serializer = GeneralSettings.DEFAULT_SERIALIZER;
         this.useDefaults = true;
         this.defaultObject = GeneralSettings.DEFAULT_OBJECT;
         this.defaultNumber = GeneralSettings.DEFAULT_NUMBER;
         this.defaultString = GeneralSettings.DEFAULT_STRING;
         this.defaultChar = GeneralSettings.DEFAULT_CHAR;
         this.defaultBoolean = GeneralSettings.DEFAULT_BOOLEAN;
         this.defaultList = GeneralSettings.DEFAULT_LIST;
         this.defaultSet = GeneralSettings.DEFAULT_SET;
         this.defaultMap = GeneralSettings.DEFAULT_MAP;
      }

      public GeneralSettings.Builder setKeyFormat(@NotNull GeneralSettings.KeyFormat var1) {
         this.keyFormat = var1;
         return this;
      }

      public GeneralSettings.Builder setRouteSeparator(char var1) {
         this.routeSeparator = var1;
         return this;
      }

      public GeneralSettings.Builder setSerializer(@NotNull YamlSerializer var1) {
         this.serializer = var1;
         return this;
      }

      public GeneralSettings.Builder setUseDefaults(boolean var1) {
         this.useDefaults = var1;
         return this;
      }

      public GeneralSettings.Builder setDefaultObject(@Nullable Object var1) {
         this.defaultObject = var1;
         return this;
      }

      public GeneralSettings.Builder setDefaultNumber(@NotNull Number var1) {
         this.defaultNumber = var1;
         return this;
      }

      public GeneralSettings.Builder setDefaultString(@Nullable String var1) {
         this.defaultString = var1;
         return this;
      }

      public GeneralSettings.Builder setDefaultChar(@Nullable Character var1) {
         this.defaultChar = var1;
         return this;
      }

      public GeneralSettings.Builder setDefaultBoolean(@Nullable Boolean var1) {
         this.defaultBoolean = var1;
         return this;
      }

      public GeneralSettings.Builder setDefaultList(@NotNull ListSupplier var1) {
         this.defaultList = var1;
         return this;
      }

      public GeneralSettings.Builder setDefaultSet(@NotNull SetSupplier var1) {
         this.defaultSet = var1;
         return this;
      }

      public GeneralSettings.Builder setDefaultMap(@NotNull MapSupplier var1) {
         this.defaultMap = var1;
         return this;
      }

      public GeneralSettings build() {
         return new GeneralSettings(this);
      }

      // $FF: synthetic method
      Builder(Object var1) {
         this();
      }
   }

   public static enum KeyFormat {
      STRING,
      OBJECT;
   }
}

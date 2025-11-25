package xshyo.us.theglow.libs.config.settings.loader;

import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.LoadSettingsBuilder;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.env.EnvConfig;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.schema.Schema;
import xshyo.us.theglow.libs.config.settings.Settings;
import xshyo.us.theglow.libs.config.settings.general.GeneralSettings;

public class LoaderSettings implements Settings {
   public static final LoaderSettings DEFAULT = builder().build();
   private final LoadSettingsBuilder builder;
   private final boolean createFileIfAbsent;
   private final boolean autoUpdate;

   private LoaderSettings(LoaderSettings.Builder var1) {
      this.builder = var1.builder;
      this.autoUpdate = var1.autoUpdate;
      this.createFileIfAbsent = var1.createFileIfAbsent;
   }

   public boolean isAutoUpdate() {
      return this.autoUpdate;
   }

   public boolean isCreateFileIfAbsent() {
      return this.createFileIfAbsent;
   }

   public LoadSettings buildEngineSettings(GeneralSettings var1) {
      LoadSettingsBuilder var10000 = this.builder.setParseComments(true);
      var1.getClass();
      var10000 = var10000.setDefaultList(var1::getDefaultList);
      var1.getClass();
      var10000 = var10000.setDefaultSet(var1::getDefaultSet);
      var1.getClass();
      return var10000.setDefaultMap(var1::getDefaultMap).build();
   }

   public static LoaderSettings.Builder builder() {
      return new LoaderSettings.Builder();
   }

   public static LoaderSettings.Builder builder(LoadSettingsBuilder var0) {
      return new LoaderSettings.Builder(var0);
   }

   public static LoaderSettings.Builder builder(LoaderSettings var0) {
      return builder(var0.builder).setAutoUpdate(var0.autoUpdate).setCreateFileIfAbsent(var0.createFileIfAbsent);
   }

   // $FF: synthetic method
   LoaderSettings(LoaderSettings.Builder var1, Object var2) {
      this(var1);
   }

   public static class Builder {
      public static final boolean DEFAULT_CREATE_FILE_IF_ABSENT = true;
      public static final boolean DEFAULT_AUTO_UPDATE = false;
      public static final boolean DEFAULT_DETAILED_ERRORS = true;
      public static final boolean DEFAULT_ALLOW_DUPLICATE_KEYS = true;
      private final LoadSettingsBuilder builder;
      private boolean autoUpdate;
      private boolean createFileIfAbsent;

      private Builder(LoadSettingsBuilder var1) {
         this.autoUpdate = false;
         this.createFileIfAbsent = true;
         this.builder = var1;
      }

      private Builder() {
         this.autoUpdate = false;
         this.createFileIfAbsent = true;
         this.builder = LoadSettings.builder();
         this.setDetailedErrors(true);
         this.setAllowDuplicateKeys(true);
      }

      public LoaderSettings.Builder setCreateFileIfAbsent(boolean var1) {
         this.createFileIfAbsent = var1;
         return this;
      }

      public LoaderSettings.Builder setAutoUpdate(boolean var1) {
         this.autoUpdate = var1;
         return this;
      }

      public LoaderSettings.Builder setErrorLabel(@NotNull String var1) {
         this.builder.setLabel(var1);
         return this;
      }

      public LoaderSettings.Builder setDetailedErrors(boolean var1) {
         this.builder.setUseMarks(var1);
         return this;
      }

      public LoaderSettings.Builder setAllowDuplicateKeys(boolean var1) {
         this.builder.setAllowDuplicateKeys(var1);
         return this;
      }

      public LoaderSettings.Builder setMaxCollectionAliases(int var1) {
         this.builder.setMaxAliasesForCollections(var1);
         return this;
      }

      public LoaderSettings.Builder setTagConstructors(@NotNull Map<Tag, ConstructNode> var1) {
         this.builder.setTagConstructors(var1);
         return this;
      }

      public LoaderSettings.Builder setSchema(@NotNull Schema var1) {
         this.builder.setSchema(var1);
         return this;
      }

      public LoaderSettings.Builder setEnvironmentConfig(@Nullable EnvConfig var1) {
         this.builder.setEnvConfig(Optional.ofNullable(var1));
         return this;
      }

      public LoaderSettings build() {
         return new LoaderSettings(this);
      }

      // $FF: synthetic method
      Builder(Object var1) {
         this();
      }

      // $FF: synthetic method
      Builder(LoadSettingsBuilder var1, Object var2) {
         this(var1);
      }
   }
}

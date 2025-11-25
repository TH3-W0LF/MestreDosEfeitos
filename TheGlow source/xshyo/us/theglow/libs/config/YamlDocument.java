package xshyo.us.theglow.libs.config;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.config.engine.ExtendedConstructor;
import xshyo.us.theglow.libs.config.engine.ExtendedRepresenter;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.DumpSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.StreamDataWriter;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.YamlUnicodeReader;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.composer.Composer;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.emitter.Emitter;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.parser.ParserImpl;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.scanner.StreamReader;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.serializer.Serializer;
import xshyo.us.theglow.libs.config.settings.Settings;
import xshyo.us.theglow.libs.config.settings.dumper.DumperSettings;
import xshyo.us.theglow.libs.config.settings.general.GeneralSettings;
import xshyo.us.theglow.libs.config.settings.loader.LoaderSettings;
import xshyo.us.theglow.libs.config.settings.updater.UpdaterSettings;
import xshyo.us.theglow.libs.config.updater.Updater;

public class YamlDocument extends Section {
   private final File file;
   private final YamlDocument defaults;
   private GeneralSettings generalSettings;
   private LoaderSettings loaderSettings;
   private DumperSettings dumperSettings;
   private UpdaterSettings updaterSettings;

   protected YamlDocument(@NotNull InputStream var1, @Nullable InputStream var2, @NotNull Settings... var3) throws IOException {
      super(Collections.emptyMap());
      this.setSettingsInternal(var3);
      this.setValue(this.generalSettings.getDefaultMap());
      this.file = null;
      this.defaults = var2 == null ? null : new YamlDocument(var2, (InputStream)null, var3);
      this.reload(var1);
   }

   protected YamlDocument(@NotNull File var1, @Nullable InputStream var2, @NotNull Settings... var3) throws IOException {
      super(Collections.emptyMap());
      this.setSettingsInternal(var3);
      this.setValue(this.generalSettings.getDefaultMap());
      this.file = var1;
      this.defaults = var2 == null ? null : new YamlDocument(var2, (InputStream)null, new Settings[]{this.generalSettings, this.loaderSettings, this.dumperSettings, this.updaterSettings});
      this.reload();
   }

   private void setSettingsInternal(@NotNull Settings... var1) {
      Settings[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Settings var5 = var2[var4];
         if (var5 instanceof GeneralSettings) {
            if (this.generalSettings != null && this.generalSettings.getKeyFormat() != ((GeneralSettings)var5).getKeyFormat()) {
               throw new IllegalArgumentException("Cannot change the key format! Recreate the file if needed to do so.");
            }

            this.generalSettings = (GeneralSettings)var5;
         } else if (var5 instanceof LoaderSettings) {
            this.loaderSettings = (LoaderSettings)var5;
         } else if (var5 instanceof DumperSettings) {
            this.dumperSettings = (DumperSettings)var5;
         } else {
            if (!(var5 instanceof UpdaterSettings)) {
               throw new IllegalArgumentException("Unknown settings object!");
            }

            this.updaterSettings = (UpdaterSettings)var5;
         }
      }

      this.generalSettings = this.generalSettings == null ? GeneralSettings.DEFAULT : this.generalSettings;
      this.loaderSettings = this.loaderSettings == null ? LoaderSettings.DEFAULT : this.loaderSettings;
      this.dumperSettings = this.dumperSettings == null ? DumperSettings.DEFAULT : this.dumperSettings;
      this.updaterSettings = this.updaterSettings == null ? UpdaterSettings.DEFAULT : this.updaterSettings;
   }

   public boolean reload() throws IOException {
      if (this.file == null) {
         return false;
      } else {
         this.reload(this.file);
         return true;
      }
   }

   private void reload(@NotNull File var1) throws IOException {
      this.clear();
      if (((File)Objects.requireNonNull(var1, "File cannot be null!")).exists()) {
         this.reload((InputStream)(new BufferedInputStream(new FileInputStream(var1))));
      } else {
         if (this.loaderSettings.isCreateFileIfAbsent()) {
            if (var1.getParentFile() != null) {
               var1.getParentFile().mkdirs();
            }

            var1.createNewFile();
         }

         if (this.defaults == null) {
            this.initEmpty(this);
         } else {
            String var2 = this.defaults.dump();
            BufferedWriter var3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(var1, false), StandardCharsets.UTF_8));
            Throwable var4 = null;

            try {
               var3.write(var2);
            } catch (Throwable var13) {
               var4 = var13;
               throw var13;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var12) {
                        var4.addSuppressed(var12);
                     }
                  } else {
                     var3.close();
                  }
               }

            }

            this.reload((InputStream)(new BufferedInputStream(new ByteArrayInputStream(var2.getBytes(StandardCharsets.UTF_8)))));
         }
      }
   }

   public void reload(@NotNull InputStream var1) throws IOException {
      this.reload(var1, this.loaderSettings);
   }

   public void reload(@NotNull InputStream var1, @NotNull LoaderSettings var2) throws IOException {
      this.clear();
      LoadSettings var3 = ((LoaderSettings)Objects.requireNonNull(var2, "Loader settings cannot be null!")).buildEngineSettings(this.generalSettings);
      ExtendedConstructor var4 = new ExtendedConstructor(var3, this.generalSettings.getSerializer());
      ParserImpl var5 = new ParserImpl(var3, new StreamReader(var3, new YamlUnicodeReader((InputStream)Objects.requireNonNull(var1, "Input stream cannot be null!"))));
      Composer var6 = new Composer(var3, var5);
      if (var6.hasNext()) {
         Node var7 = var6.next();
         if (var6.hasNext()) {
            throw new InvalidObjectException("Multiple documents are not supported!");
         }

         if (!(var7 instanceof MappingNode)) {
            throw new IllegalArgumentException(String.format("Top level object is not a map! Parsed node: %s", var7.toString()));
         }

         var4.constructSingleDocument(Optional.of(var7));
         this.init(this, (Node)null, (MappingNode)var7, var4);
         var4.clear();
      } else {
         this.initEmpty(this);
      }

      if (this.file != null && var2.isCreateFileIfAbsent() && !this.file.exists()) {
         if (this.file.getParentFile() != null) {
            this.file.getParentFile().mkdirs();
         }

         this.file.createNewFile();
         this.save();
      }

      if (this.defaults != null && var2.isAutoUpdate()) {
         Updater.update(this, this.defaults, this.updaterSettings, this.generalSettings);
      }

   }

   public boolean update() throws IOException {
      return this.update(this.updaterSettings);
   }

   public boolean update(@NotNull UpdaterSettings var1) throws IOException {
      if (this.defaults == null) {
         return false;
      } else {
         Updater.update(this, this.defaults, (UpdaterSettings)Objects.requireNonNull(var1, "Updater settings cannot be null!"), this.generalSettings);
         return true;
      }
   }

   public void update(@NotNull InputStream var1) throws IOException {
      this.update(var1, this.updaterSettings);
   }

   public void update(@NotNull InputStream var1, @NotNull UpdaterSettings var2) throws IOException {
      Updater.update(this, create((InputStream)Objects.requireNonNull(var1, "Defaults cannot be null!"), this.generalSettings, this.loaderSettings, this.dumperSettings, UpdaterSettings.DEFAULT), (UpdaterSettings)Objects.requireNonNull(var2, "Updater settings cannot be null!"), this.generalSettings);
   }

   public boolean save() throws IOException {
      if (this.file == null) {
         return false;
      } else {
         this.save(this.file);
         return true;
      }
   }

   public void save(@NotNull File var1) throws IOException {
      BufferedWriter var2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(var1, false), StandardCharsets.UTF_8));
      Throwable var3 = null;

      try {
         var2.write(this.dump());
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   public void save(@NotNull OutputStream var1, Charset var2) throws IOException {
      var1.write(this.dump().getBytes(var2));
   }

   public void save(@NotNull OutputStreamWriter var1) throws IOException {
      var1.write(this.dump());
   }

   public String dump() {
      return this.dump(this.dumperSettings);
   }

   public String dump(@NotNull DumperSettings var1) {
      DumpSettings var2 = var1.buildEngineSettings();
      YamlDocument.SerializedStream var3 = new YamlDocument.SerializedStream();
      ExtendedRepresenter var4 = new ExtendedRepresenter(this.generalSettings, var1, var2);
      Serializer var5 = new Serializer(var2, new Emitter(var2, var3));
      var5.emitStreamStart();
      var5.serializeDocument(var4.represent(this));
      var5.emitStreamEnd();
      return var3.toString();
   }

   public void setSettings(@NotNull Settings... var1) {
      this.setSettingsInternal(var1);
   }

   @Deprecated
   public void setLoaderSettings(@NotNull LoaderSettings var1) {
      this.loaderSettings = var1;
   }

   public void setDumperSettings(@NotNull DumperSettings var1) {
      this.dumperSettings = var1;
   }

   public void setGeneralSettings(@NotNull GeneralSettings var1) {
      if (var1.getKeyFormat() != this.generalSettings.getKeyFormat()) {
         throw new IllegalArgumentException("Cannot change key format! Recreate the file if needed to do so.");
      } else {
         this.generalSettings = var1;
      }
   }

   public void setUpdaterSettings(@NotNull UpdaterSettings var1) {
      this.updaterSettings = var1;
   }

   @Nullable
   public YamlDocument getDefaults() {
      return this.defaults;
   }

   @NotNull
   public GeneralSettings getGeneralSettings() {
      return this.generalSettings;
   }

   @NotNull
   public DumperSettings getDumperSettings() {
      return this.dumperSettings;
   }

   @NotNull
   public UpdaterSettings getUpdaterSettings() {
      return this.updaterSettings;
   }

   @NotNull
   public LoaderSettings getLoaderSettings() {
      return this.loaderSettings;
   }

   @Nullable
   public File getFile() {
      return this.file;
   }

   public boolean isRoot() {
      return true;
   }

   public static YamlDocument create(@NotNull File var0, @NotNull InputStream var1, @NotNull Settings... var2) throws IOException {
      return new YamlDocument(var0, var1, var2);
   }

   public static YamlDocument create(@NotNull InputStream var0, @NotNull InputStream var1, @NotNull Settings... var2) throws IOException {
      return new YamlDocument(var0, var1, var2);
   }

   public static YamlDocument create(@NotNull File var0, @NotNull Settings... var1) throws IOException {
      return new YamlDocument(var0, (InputStream)null, var1);
   }

   public static YamlDocument create(@NotNull InputStream var0, @NotNull Settings... var1) throws IOException {
      return new YamlDocument(var0, (InputStream)null, var1);
   }

   private static class SerializedStream extends StringWriter implements StreamDataWriter {
      private SerializedStream() {
      }

      // $FF: synthetic method
      SerializedStream(Object var1) {
         this();
      }
   }
}

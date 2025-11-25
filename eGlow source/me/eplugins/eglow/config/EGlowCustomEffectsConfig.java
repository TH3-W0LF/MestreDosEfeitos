package me.eplugins.eglow.config;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class EGlowCustomEffectsConfig {
   private static YamlConfiguration config;
   private static File configFile;

   public static void initialize() {
      File oldConfigFile = new File(EGlow.getInstance().getDataFolder(), "CustomEffects.yml");
      configFile = new File(EGlow.getInstance().getDataFolder(), "customeffects.yml");

      try {
         if (oldConfigFile.exists()) {
            oldConfigFile.renameTo(configFile);
         }

         if (!configFile.exists()) {
            ChatUtil.sendToConsole("&f[&eeGlow&f]: &4customeffects.yml not found&f! &eCreating&f...", false);
            configFile.getParentFile().mkdirs();
            EGlow.getInstance().saveResource("customeffects.yml", false);
         } else {
            ChatUtil.sendToConsole("&f[&eeGlow&f]: &aLoading customeffects config&f.", false);
         }

         config = new YamlConfiguration();
         config.load(configFile);
      } catch (Exception var2) {
         ChatUtil.printException("Failed to initialize custom effects config", var2);
      }

   }

   public static boolean reloadConfig() {
      YamlConfiguration configBackup = config;
      File configFileBackup = configFile;

      try {
         config = null;
         configFile = null;
         configFile = new File(EGlow.getInstance().getDataFolder(), "customeffects.yml");
         config = new YamlConfiguration();
         config.load(configFile);
         return true;
      } catch (Exception var3) {
         config = configBackup;
         configFile = configFileBackup;
         ChatUtil.printException("Failed to reload custom effects config", var3);
         return false;
      }
   }

   public static enum Effect {
      GET_ALL_EFFECTS("Effects"),
      GET_DISPLAYNAME("Effects.%effect%.Displayname"),
      GET_DELAY("Effects.%effect%.Delay"),
      GET_COLORS("Effects.%effect%.Colors"),
      GET_MATERIAL("Effects.%effect%.GUI.Material"),
      GET_META("Effects.%effect%.GUI.Meta"),
      GET_MODEL_ID("Effects.%effect%.GUI.Model"),
      GET_NAME("Effects.%effect%.GUI.Name"),
      GET_LORES("Effects.%effect%.GUI.Lores");

      private final EGlowCustomEffectsConfig.Effect effect = this;
      private final String configPath;

      private Effect(String configPath) {
         this.configPath = configPath;
      }

      public Set<String> get() {
         try {
            return ((ConfigurationSection)Objects.requireNonNull(EGlowCustomEffectsConfig.config.getConfigurationSection(this.effect.getConfigPath()), this.effect.getConfigPath() + " isn't a valid path")).getKeys(false);
         } catch (NullPointerException var2) {
            return Collections.emptySet();
         }
      }

      public int getInt(String value) {
         return this.effect == GET_MODEL_ID && !EGlowCustomEffectsConfig.config.contains(this.effect.getConfigPath().replace("%effect%", value)) ? -1 : EGlowCustomEffectsConfig.config.getInt(this.effect.getConfigPath().replace("%effect%", value));
      }

      public double getDouble(String value) {
         return EGlowCustomEffectsConfig.config.getDouble(this.effect.getConfigPath().replace("%effect%", value), 1.0D);
      }

      public String getString(String value) {
         return EGlowCustomEffectsConfig.config.getString(this.effect.getConfigPath().replace("%effect%", value));
      }

      public List<String> getList(String value) {
         return EGlowCustomEffectsConfig.config.getStringList(this.effect.getConfigPath().replace("%effect%", value));
      }

      @Generated
      public String getConfigPath() {
         return this.configPath;
      }

      // $FF: synthetic method
      private static EGlowCustomEffectsConfig.Effect[] $values() {
         return new EGlowCustomEffectsConfig.Effect[]{GET_ALL_EFFECTS, GET_DISPLAYNAME, GET_DELAY, GET_COLORS, GET_MATERIAL, GET_META, GET_MODEL_ID, GET_NAME, GET_LORES};
      }
   }
}

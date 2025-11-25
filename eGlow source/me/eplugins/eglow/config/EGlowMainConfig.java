package me.eplugins.eglow.config;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;

public class EGlowMainConfig {
   private static YamlConfiguration config;
   private static File configFile;

   public static void initialize() {
      File oldConfigFile = new File(EGlow.getInstance().getDataFolder(), "Config.yml");
      configFile = new File(EGlow.getInstance().getDataFolder(), "config.yml");

      try {
         if (!EGlow.getInstance().getDataFolder().exists()) {
            EGlow.getInstance().getDataFolder().mkdirs();
         }

         if (oldConfigFile.exists()) {
            oldConfigFile.renameTo(configFile);
         }

         if (!configFile.exists()) {
            ChatUtil.sendToConsole("&f[&eeGlow&f]: &4config.yml not found&f! &eCreating&f...", false);
            configFile.getParentFile().mkdirs();
            EGlow.getInstance().saveResource("config.yml", false);
         } else {
            ChatUtil.sendToConsole("&f[&eeGlow&f]: &aLoading main config&f.", false);
         }

         config = new YamlConfiguration();
         config.load(configFile);
         registerCustomPermissions();
         repairConfig();
      } catch (Exception var2) {
         ChatUtil.printException("Failed to initialize main config", var2);
      }

   }

   public static boolean reloadConfig() {
      YamlConfiguration configBackup = config;
      File configFileBackup = configFile;

      try {
         config = null;
         configFile = null;
         configFile = new File(EGlow.getInstance().getDataFolder(), "config.yml");
         config = new YamlConfiguration();
         config.load(configFile);
         registerCustomPermissions();
         return true;
      } catch (Exception var3) {
         config = configBackup;
         configFile = configFileBackup;
         ChatUtil.printException("Failed to reload main config", var3);
         return false;
      }
   }

   private static void repairConfig() {
      InputStream resource = EGlow.getInstance().getResource("config.yml");
      YamlConfiguration tempConfig = YamlConfiguration.loadConfiguration(new InputStreamReader((InputStream)Objects.requireNonNull(resource)));
      Iterator var2 = ((ConfigurationSection)Objects.requireNonNull(tempConfig.getConfigurationSection(""))).getKeys(true).iterator();

      while(var2.hasNext()) {
         String path = (String)var2.next();
         if (!path.contains("Settings.join.force-glows.glows") && !config.contains(path)) {
            config.set(path, tempConfig.get(path));
         }
      }

      try {
         config.save(configFile);
      } catch (Exception var4) {
         ChatUtil.printException("Failed to repair main config", var4);
      }

   }

   private static void registerCustomPermissions() {
      if (EGlowMainConfig.MainConfig.SETTINGS_JOIN_FORCE_GLOWS_ENABLE.getBoolean()) {
         Iterator var0 = EGlowMainConfig.MainConfig.SETTINGS_JOIN_FORCE_GLOWS_LIST.getConfigSection().iterator();

         while(var0.hasNext()) {
            String name = (String)var0.next();

            try {
               EGlow.getInstance().getServer().getPluginManager().addPermission(new Permission("eglow.force." + name.toLowerCase()));
            } catch (Exception var3) {
            }
         }

      }
   }

   public static enum MainConfig {
      ENABLE_BETA("beta"),
      ACTIONBARS_ENABLE("Actionbars.enable"),
      ACTIONBARS_IN_GUI("Actionbars.use-in-GUI"),
      COMMAND_ALIAS_ENABLE("Command-alias.enable"),
      COMMAND_ALIAS("Command-alias.alias"),
      DELAY_SLOW("Delays.slow"),
      DELAY_FAST("Delays.fast"),
      FORMATTING_TABLIST_ENABLE("Formatting.tablist.enable"),
      FORMATTING_TABLIST_FORMAT("Formatting.tablist.format"),
      FORMATTING_TAGNAME_ENABLE("Formatting.tagname.enable"),
      FORMATTING_TAGNAME_PREFIX("Formatting.tagname.prefix"),
      FORMATTING_TAGNAME_SUFFIX("Formatting.tagname.suffix"),
      MYSQL_ENABLE("MySQL.enable"),
      MYSQL_HOST("MySQL.host"),
      MYSQL_PORT("MySQL.port"),
      MYSQL_DBNAME("MySQL.DBName"),
      MYSQL_USERNAME("MySQL.username"),
      MYSQL_PASSWORD("MySQL.password"),
      WORLD_ENABLE("World.enable"),
      WORLD_ACTION("World.action"),
      WORLD_LIST("World.worlds"),
      SETTINGS_DISABLE_GLOW_WHEN_INVISIBLE("Settings.disable-glow-when-invisible"),
      SETTINGS_SMART_TAB_NAMETAG_HANDLER("Settings.smart-TAB-nametag-handler"),
      SETTINGS_GUI_DISABLE_GUI("Settings.gui.disable-gui"),
      SETTINGS_GUI_RENDER_SKULLS("Settings.gui.render-skulls"),
      SETTINGS_GUI_ADD_GLASS_PANES("Settings.gui.add-glass-panes"),
      SETTINGS_GUI_ADD_PREFIX("Settings.gui.add-prefix-to-title"),
      SETTINGS_GUI_CUSTOM_EFFECTS("Settings.gui.custom-effects-in-gui"),
      SETTINGS_GUI_COLOR_FOR_MESSAGES("Settings.gui.use-gui-color-for-messages"),
      SETTINGS_GUIS_INTERACTION_DELAY("Settings.gui.interaction-delay"),
      SETTINGS_JOIN_CHECK_PERMISSION("Settings.join.check-glow-permission"),
      SETTINGS_JOIN_DEFAULT_GLOW_ON_JOIN_VALUE("Settings.join.default-glow-on-join-value"),
      SETTINGS_JOIN_MENTION_GLOW_STATE("Settings.join.mention-glow-state"),
      SETTINGS_JOIN_FORCE_GLOWS_ENABLE("Settings.join.force-glows.enable"),
      SETTINGS_JOIN_FORCE_GLOWS_BYPASS_BLOCKED_WORLDS("Settings.join.force-glows.bypass-blocked-worlds"),
      SETTINGS_JOIN_FORCE_GLOWS_LIST("Settings.join.force-glows.glows"),
      SETTINGS_NOTIFICATIONS_UPDATE("Settings.notifications.plugin-update"),
      SETTINGS_NOTIFICATIONS_INVISIBILITY("Settings.notifications.invisibility-change"),
      SETTINGS_NOTIFICATIONS_TARGET_COMMAND("Settings.notifications.target-set-unset-command"),
      ADVANCED_VELOCITAB_MESSAGING("Advanced.use-velocitab-plugin-messaging"),
      ADVANCED_VELOCITY_MESSAGING("Advanced.use-velocity-plugin-messaging"),
      ADVANCED_FORCE_DISABLE_PROXY_MESSAGING("Advanced.force-disable-proxy-messaging"),
      ADVANCED_FORCE_DISABLE_TAB_INTEGRATION("Advanced.force-disable-tab-integration"),
      ADVANCED_GLOW_VISIBILITY_ENABLE("Advanced.glow-visibility.enable"),
      ADVANCED_GLOW_VISIBILITY_DELAY("Advanced.glow-visibility.delay"),
      ADVANCED_MYSQL_USESSL("Advanced.MySQL.useSSL"),
      ADVANCED_TEAMS_ENTITY_COLLISION("Advanced.teams.entity-collision"),
      ADVANCED_TEAMS_NAMETAG_VISIBILITY("Advanced.teams.nametag-visibility"),
      ADVANCED_TEAMS_REMOVE_ON_JOIN("Advanced.teams.remove-teams-on-join"),
      ADVANCED_TEAMS_SEND_PACKETS("Advanced.teams.send-eGlow-team-packets"),
      ADVANCED_PACKETS_SMART_BLOCKER("Advanced.packets.smart-packet-blocker"),
      ADVANCED_PACKETS_DELAYED_ENTITYMETA("Advanced.packets.send-delayed-entitymetadata");

      private final EGlowMainConfig.MainConfig main = this;
      private final String configPath;

      private MainConfig(String configPath) {
         this.configPath = configPath;
      }

      public String getString() {
         return EGlowMainConfig.config.getString(this.main.getConfigPath());
      }

      public String getString(String name) {
         return EGlowMainConfig.config.getString(this.main.getConfigPath() + "." + name);
      }

      public List<String> getStringList() {
         List<String> worldNames = new ArrayList();
         Iterator var2 = EGlowMainConfig.config.getStringList(this.main.getConfigPath()).iterator();

         while(var2.hasNext()) {
            String worldName = (String)var2.next();
            worldNames.add(worldName.toLowerCase());
         }

         return worldNames;
      }

      public int getInt() {
         switch(this.main) {
         case DELAY_SLOW:
         case DELAY_FAST:
         case ADVANCED_GLOW_VISIBILITY_DELAY:
            return (int)(EGlowMainConfig.config.getDouble(this.main.getConfigPath()) * 20.0D);
         case MYSQL_PORT:
            if (EGlowMainConfig.config.getInt(this.main.getConfigPath()) == 0) {
               return Integer.parseInt(EGlowMainConfig.config.getString(this.main.getConfigPath(), "-1"));
            }
         default:
            return EGlowMainConfig.config.getInt(this.main.getConfigPath());
         }
      }

      public long getLong() {
         return (long)(EGlowMainConfig.config.getDouble(this.main.getConfigPath()) * 1000.0D);
      }

      public Boolean getBoolean() {
         return EGlowMainConfig.config.getBoolean(this.main.getConfigPath());
      }

      public Set<String> getConfigSection() {
         return ((ConfigurationSection)Objects.requireNonNull(EGlowMainConfig.config.getConfigurationSection(this.main.getConfigPath()), this.main.getConfigPath() + " isn't a valid path")).getKeys(false);
      }

      @Generated
      public String getConfigPath() {
         return this.configPath;
      }

      // $FF: synthetic method
      private static EGlowMainConfig.MainConfig[] $values() {
         return new EGlowMainConfig.MainConfig[]{ENABLE_BETA, ACTIONBARS_ENABLE, ACTIONBARS_IN_GUI, COMMAND_ALIAS_ENABLE, COMMAND_ALIAS, DELAY_SLOW, DELAY_FAST, FORMATTING_TABLIST_ENABLE, FORMATTING_TABLIST_FORMAT, FORMATTING_TAGNAME_ENABLE, FORMATTING_TAGNAME_PREFIX, FORMATTING_TAGNAME_SUFFIX, MYSQL_ENABLE, MYSQL_HOST, MYSQL_PORT, MYSQL_DBNAME, MYSQL_USERNAME, MYSQL_PASSWORD, WORLD_ENABLE, WORLD_ACTION, WORLD_LIST, SETTINGS_DISABLE_GLOW_WHEN_INVISIBLE, SETTINGS_SMART_TAB_NAMETAG_HANDLER, SETTINGS_GUI_DISABLE_GUI, SETTINGS_GUI_RENDER_SKULLS, SETTINGS_GUI_ADD_GLASS_PANES, SETTINGS_GUI_ADD_PREFIX, SETTINGS_GUI_CUSTOM_EFFECTS, SETTINGS_GUI_COLOR_FOR_MESSAGES, SETTINGS_GUIS_INTERACTION_DELAY, SETTINGS_JOIN_CHECK_PERMISSION, SETTINGS_JOIN_DEFAULT_GLOW_ON_JOIN_VALUE, SETTINGS_JOIN_MENTION_GLOW_STATE, SETTINGS_JOIN_FORCE_GLOWS_ENABLE, SETTINGS_JOIN_FORCE_GLOWS_BYPASS_BLOCKED_WORLDS, SETTINGS_JOIN_FORCE_GLOWS_LIST, SETTINGS_NOTIFICATIONS_UPDATE, SETTINGS_NOTIFICATIONS_INVISIBILITY, SETTINGS_NOTIFICATIONS_TARGET_COMMAND, ADVANCED_VELOCITAB_MESSAGING, ADVANCED_VELOCITY_MESSAGING, ADVANCED_FORCE_DISABLE_PROXY_MESSAGING, ADVANCED_FORCE_DISABLE_TAB_INTEGRATION, ADVANCED_GLOW_VISIBILITY_ENABLE, ADVANCED_GLOW_VISIBILITY_DELAY, ADVANCED_MYSQL_USESSL, ADVANCED_TEAMS_ENTITY_COLLISION, ADVANCED_TEAMS_NAMETAG_VISIBILITY, ADVANCED_TEAMS_REMOVE_ON_JOIN, ADVANCED_TEAMS_SEND_PACKETS, ADVANCED_PACKETS_SMART_BLOCKER, ADVANCED_PACKETS_DELAYED_ENTITYMETA};
      }
   }
}

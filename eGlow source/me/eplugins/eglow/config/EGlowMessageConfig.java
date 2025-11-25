package me.eplugins.eglow.config;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class EGlowMessageConfig {
   private static YamlConfiguration config;
   private static File configFile;

   public static void initialize() {
      File oldConfigFile = new File(EGlow.getInstance().getDataFolder(), "Messages.yml");
      configFile = new File(EGlow.getInstance().getDataFolder(), "messages.yml");

      try {
         if (oldConfigFile.exists()) {
            oldConfigFile.renameTo(configFile);
         }

         if (!configFile.exists()) {
            ChatUtil.sendToConsole("&f[&eeGlow&f]: &4messages.yml not found&f! &eCreating&f...", false);
            configFile.getParentFile().mkdirs();
            EGlow.getInstance().saveResource("messages.yml", false);
         } else {
            ChatUtil.sendToConsole("&f[&eeGlow&f]: &aLoading messages config&f.", false);
         }

         config = new YamlConfiguration();
         config.load(configFile);
         repairConfig();
      } catch (Exception var2) {
         ChatUtil.printException("Failed to initialize message config", var2);
      }

   }

   public static boolean reloadConfig() {
      YamlConfiguration configBackup = config;
      File configFileBackup = configFile;

      try {
         config = null;
         configFile = null;
         configFile = new File(EGlow.getInstance().getDataFolder(), "messages.yml");
         config = new YamlConfiguration();
         config.load(configFile);
         return true;
      } catch (Exception var3) {
         config = configBackup;
         configFile = configFileBackup;
         ChatUtil.printException("Failed to reload message config", var3);
         return false;
      }
   }

   private static void repairConfig() {
      InputStream resource = EGlow.getInstance().getResource("messages.yml");
      YamlConfiguration tempConfig = YamlConfiguration.loadConfiguration(new InputStreamReader((InputStream)Objects.requireNonNull(resource)));
      Iterator var2 = ((ConfigurationSection)Objects.requireNonNull(tempConfig.getConfigurationSection(""))).getKeys(true).iterator();

      while(var2.hasNext()) {
         String path = (String)var2.next();
         if (!config.contains(path)) {
            config.set(path, tempConfig.get(path));
         }
      }

      try {
         config.save(configFile);
      } catch (Exception var4) {
         ChatUtil.printException("Failed to repair message config", var4);
      }

   }

   public static enum Message {
      PREFIX("main.prefix"),
      NO_PERMISSION("main.no-permission"),
      COLOR("main.color-"),
      NEW_GLOW("main.glow-new"),
      SAME_GLOW("main.glow-same"),
      DISABLE_GLOW("main.glow-disable"),
      GLOWONJOIN_TOGGLE("main.glow-glowonjoin-toggle"),
      VISIBILITY_CHANGE("main.glow-visibility-change"),
      VISIBILITY_ALL("main.glow-visibility-all"),
      VISIBILITY_OTHER("main.glow-visibility-other"),
      VISIBILITY_OWN("main.glow-visibility-own"),
      VISIBILITY_NONE("main.glow-visibility-none"),
      VISIBILITY_UNSUPPORTED("main.glow-visibility-unsupported-version"),
      UNSUPPORTED_GLOW("main.glow-unsupported"),
      OTHER_CONFIRM("main.other-glow-player-confirm"),
      OTHER_CONFIRM_OFF("main.other-glow-player-confirm-off"),
      OTHER_GLOW_ON_JOIN_CONFIRM("main.other-glow-on-join-confirm"),
      OTHER_PLAYER_IN_DISABLED_WORLD("main.other-glow-player-disabled-world"),
      OTHER_PLAYER_INVISIBLE("main.other-glow-player-invisible"),
      OTHER_PLAYER_ANIMATION("main.other-glow-player-animation"),
      TARGET_NOTIFICATION_PREFIX("main.other-glow-target-notification-prefix"),
      RELOAD_SUCCESS("main.reload-success"),
      RELOAD_FAIL("main.reload-fail"),
      RELOAD_GLOW_ALLOWED("main.reload-glow-allowed"),
      RELOAD_GLOW_BLOCKED("main.reload-glow-blocked"),
      INFO_CUSTOM_LINES("main.info-custom-lines"),
      INFO_PLAYER_INFO("main.info-player-info"),
      INFO_PLAYER_NAME("main.info-player-name"),
      INFO_LAST_GLOWEFFECT("main.info-last-gloweffect"),
      INFO_GLOW_VISIBILITY("main.info-glow-visibility"),
      INFO_GLOW_ON_JOIN("main.info-glow-on-join"),
      INFO_FORCED_GLOW("main.info-forced-glow"),
      INFO_GLOW_BLOCKED_REASON("main.info-glow-blocked-reason"),
      INFO_GLOW_INFO("main.info-eglow-info"),
      INFO_EGLOW_VERSION("main.info-eglow-version"),
      INFO_DATABASE_TYPE("main.info-database-type"),
      INFO_LOADED_PLUGINHOOKS("main.info-loaded-pluginhooks"),
      INFO_UPDATE_PLUGIN("main.info-update-plugin"),
      GLOWING_STATE_ON_JOIN("main.glowing-state-on-join"),
      NON_GLOWING_STATE_ON_JOIN("main.non-glowing-state-on-join"),
      NO_LAST_GLOW("main.argument-no-last-glow"),
      INCORRECT_USAGE("main.argument-incorrect-usage"),
      PLAYER_NOT_FOUND("main.argument-player-not-found"),
      COMMAND_LIST("main.command-list"),
      PLAYER_ONLY("main.command-player-only"),
      UPDATE_MESSAGE("main.update-message"),
      INVISIBILITY_BLOCKED("main.invisibility-glow-blocked"),
      INVISIBILITY_ALLOWED("main.invisibility-glow-allowed"),
      WORLD_BLOCKED("main.world-glow-blocked"),
      WORLD_ALLOWED("main.world-glow-allowed"),
      ANIMATION_BLOCKED("main.animation-glow-blocked"),
      CITIZENS_NPC_NOT_FOUND("main.citizens-npc-not-found"),
      CUSTOM_MENU_RELOAD_BLOCKED("custom-menu.reload-blocked"),
      COMMANDS_USER_HELP("commands.user.help"),
      COMMANDS_USER_TOGGLE("commands.user.toggle"),
      COMMANDS_USER_TOGGLEGLOWONJOIN("commands.user.toggleglowonjoin"),
      COMMANDS_USER_VISIBILITY_VISIBILITY("commands.user.visibility.visibility"),
      COMMANDS_USER_VISIBILITY_ALL("commands.user.visibility.all"),
      COMMANDS_USER_VISIBILITY_OTHER("commands.user.visibility.other"),
      COMMANDS_USER_VISIBILITY_OWN("commands.user.visibility.own"),
      COMMANDS_USER_VISIBILITY_NONE("commands.user.visibility.none"),
      COMMANDS_USER_LIST("commands.user.list"),
      COMMANDS_ADMIN_SET("commands.admin.set"),
      COMMANDS_ADMIN_UNSET("commands.admin.unset"),
      COMMANDS_ADMIN_GLOWONJOIN_GLOWONJOIN("commands.admin.glowonjoin.glowonjoin"),
      COMMANDS_ADMIN_GLOWONJOIN_TRUE("commands.admin.glowonjoin.true"),
      COMMANDS_ADMIN_GLOWONJOIN_FALSE("commands.admin.glowonjoin.false"),
      COMMANDS_ADMIN_DEBUG("commands.admin.debug"),
      COMMANDS_ADMIN_INFO("commands.admin.info"),
      COMMANDS_ADMIN_RELOAD("commands.admin.reload"),
      COMMANDS_EFFECTS_SOLID_RED("commands.effects.solid.red"),
      COMMANDS_EFFECTS_SOLID_DARKRED("commands.effects.solid.darkred"),
      COMMANDS_EFFECTS_SOLID_GOLD("commands.effects.solid.gold"),
      COMMANDS_EFFECTS_SOLID_YELLOW("commands.effects.solid.yellow"),
      COMMANDS_EFFECTS_SOLID_GREEN("commands.effects.solid.green"),
      COMMANDS_EFFECTS_SOLID_DARKGREEN("commands.effects.solid.darkgreen"),
      COMMANDS_EFFECTS_SOLID_AQUA("commands.effects.solid.aqua"),
      COMMANDS_EFFECTS_SOLID_DARKAQUA("commands.effects.solid.darkaqua"),
      COMMANDS_EFFECTS_SOLID_BLUE("commands.effects.solid.blue"),
      COMMANDS_EFFECTS_SOLID_DARKBLUE("commands.effects.solid.darkblue"),
      COMMANDS_EFFECTS_SOLID_PURPLE("commands.effects.solid.purple"),
      COMMANDS_EFFECTS_SOLID_PINK("commands.effects.solid.pink"),
      COMMANDS_EFFECTS_SOLID_WHITE("commands.effects.solid.white"),
      COMMANDS_EFFECTS_SOLID_GRAY("commands.effects.solid.gray"),
      COMMANDS_EFFECTS_SOLID_DARKGRAY("commands.effects.solid.darkgray"),
      COMMANDS_EFFECTS_SOLID_BLACK("commands.effects.solid.black"),
      COMMANDS_EFFECTS_EFFECT_BLINK("commands.effects.effect.blink"),
      COMMANDS_EFFECTS_EFFECT_RAINBOW("commands.effects.effect.rainbow"),
      COMMANDS_EFFECTS_EFFECT_SPEED_SLOW("commands.effects.effect.effect-speed-slow"),
      COMMANDS_EFFECTS_EFFECT_SPEED_FAST("commands.effects.effect.effect-speed-fast"),
      GUI_DISABLED("gui.gui-disabled"),
      GUI_TITLE("gui.title"),
      GUI_COLOR("gui.color-"),
      GUI_YES("gui.misc-yes"),
      GUI_NO("gui.misc-no"),
      GUI_NOT_AVAILABLE("gui.misc-not-available"),
      GUI_LEFT_CLICK("gui.misc-left-click"),
      GUI_RIGHT_CLICK("gui.misc-right-click"),
      GUI_CLICK_TO_TOGGLE("gui.misc-click-to-toggle"),
      GUI_CLICK_TO_CYCLE("gui.misc-click-to-cycle"),
      GUI_CLICK_TO_OPEN("gui.misc-click-to-open"),
      GUI_PREVIOUS_PAGE("gui.misc-previous-page"),
      GUI_NEXT_PAGE("gui.misc-next-page"),
      GUI_PAGE_LORE("gui.misc-page-lore"),
      GUI_MAIN_MENU("gui.misc-main-menu"),
      GUI_COOLDOWN("gui.misc-interaction-cooldown"),
      GUI_COLOR_PERMISSION("gui.color-colorpermission"),
      GUI_BLINK_PERMISSION("gui.color-blinkpermission"),
      GUI_EFFECT_PERMISSION("gui.color-effectpermission"),
      GUI_SETTINGS_NAME("gui.setting-item-name"),
      GUI_GLOW_ON_JOIN("gui.setting-glow-on-join"),
      GUI_LAST_GLOW("gui.setting-last-glow"),
      GUI_GLOW_ITEM_NAME("gui.glow-item-name"),
      GUI_GLOWING("gui.glow-glowing"),
      GLOW_VISIBILITY_ITEM_NAME("gui.glow-visibility-item-name"),
      GLOW_VISIBILITY_INDICATOR("gui.glow-visibility-indicator"),
      GUI_SPEED_ITEM_NAME("gui.speed-item-name"),
      GUI_SPEED("gui.speed-speed"),
      GUI_CUSTOM_EFFECTS_ITEM_NAME("gui.custom-effect-item-name");

      private final EGlowMessageConfig.Message msg = this;
      private final String configPath;

      private Message(String configPath) {
         this.configPath = configPath;
      }

      public String get() {
         return this.getColorValue(this.getConfigPath());
      }

      public String getStrippedLowercase() {
         return EGlowMessageConfig.config.getString(this.msg.getConfigPath());
      }

      public String get(String value) {
         switch(this.msg) {
         case COLOR:
            return this.getColorValue(this.msg.getConfigPath() + value);
         case GUI_PAGE_LORE:
            return this.getColorValue(this.msg.getConfigPath(), "%page%", value);
         case GUI_COLOR:
            if (EGlowMessageConfig.config.contains(this.msg.getConfigPath() + value)) {
               return this.getColorValue(this.msg.getConfigPath() + value);
            }

            return this.getColorValue(COLOR.getConfigPath() + value);
         case GLOWONJOIN_TOGGLE:
            return this.getColorValue(this.msg.getConfigPath(), "%value%", value);
         case VISIBILITY_CHANGE:
            return value.toUpperCase().equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT.toString()) ? this.getColorValue(this.msg.getConfigPath(), "%value%", VISIBILITY_UNSUPPORTED.get()) : this.getColorValue(this.msg.getConfigPath(), "%value%", valueOf("VISIBILITY_" + value).get());
         case INCORRECT_USAGE:
            return this.getColorValue(this.msg.getConfigPath(), "%command%", value);
         case NEW_GLOW:
         case GLOWING_STATE_ON_JOIN:
            return this.getColorValue(this.msg.getConfigPath(), "%glowname%", value);
         case RELOAD_GLOW_ALLOWED:
         case RELOAD_GLOW_BLOCKED:
            return this.getColorValue(this.msg.getConfigPath(), "%reason%", value);
         default:
            return "Incorrect handled message for: " + this.msg;
         }
      }

      public String get(EGlowPlayer eGlowTarget, String value) {
         switch(this.msg) {
         case OTHER_CONFIRM:
            return this.getColorValue(this.msg.getConfigPath(), eGlowTarget, "%glowname%", value);
         case OTHER_GLOW_ON_JOIN_CONFIRM:
            return this.getColorValue(this.msg.getConfigPath(), eGlowTarget, "%value%", value);
         default:
            return "Incorrect handled message for: " + this.msg;
         }
      }

      public String get(EGlowPlayer eGlowTarget) {
         return this.getColorValue(this.msg.getConfigPath(), "%target%", eGlowTarget.getDisplayName());
      }

      public List<String> getStringList() {
         return EGlowMessageConfig.config.getStringList(this.msg.getConfigPath());
      }

      private String getColorValue(String path) {
         String text = EGlowMessageConfig.config.getString(path);
         return text == null ? "&cFailed to get text for&f: '&e" + path + "'" : ChatUtil.translateColors(text);
      }

      private String getColorValue(String path, String textToReplace, String replacement) {
         String text = EGlowMessageConfig.config.getString(path);
         if (text == null) {
            return "&cFailed to get text for&f: '&e" + path + "'";
         } else {
            return replacement == null ? "&cInvalid effectname&f." : ChatUtil.translateColors(text.replace(textToReplace, replacement));
         }
      }

      private String getColorValue(String path, EGlowPlayer eGlowPlayer, String textToReplace, String replacement) {
         String text = EGlowMessageConfig.config.getString(path);
         String name = "NULL";
         if (text == null) {
            return "&cFailed to get text for&f: '&e" + path + "'";
         } else {
            if (replacement == null) {
               replacement = "NULL";
            }

            if (eGlowPlayer != null) {
               name = eGlowPlayer.getDisplayName();
            }

            return ChatUtil.translateColors(text.replace(textToReplace, replacement).replace("%target%", name));
         }
      }

      @Generated
      public String getConfigPath() {
         return this.configPath;
      }

      // $FF: synthetic method
      private static EGlowMessageConfig.Message[] $values() {
         return new EGlowMessageConfig.Message[]{PREFIX, NO_PERMISSION, COLOR, NEW_GLOW, SAME_GLOW, DISABLE_GLOW, GLOWONJOIN_TOGGLE, VISIBILITY_CHANGE, VISIBILITY_ALL, VISIBILITY_OTHER, VISIBILITY_OWN, VISIBILITY_NONE, VISIBILITY_UNSUPPORTED, UNSUPPORTED_GLOW, OTHER_CONFIRM, OTHER_CONFIRM_OFF, OTHER_GLOW_ON_JOIN_CONFIRM, OTHER_PLAYER_IN_DISABLED_WORLD, OTHER_PLAYER_INVISIBLE, OTHER_PLAYER_ANIMATION, TARGET_NOTIFICATION_PREFIX, RELOAD_SUCCESS, RELOAD_FAIL, RELOAD_GLOW_ALLOWED, RELOAD_GLOW_BLOCKED, INFO_CUSTOM_LINES, INFO_PLAYER_INFO, INFO_PLAYER_NAME, INFO_LAST_GLOWEFFECT, INFO_GLOW_VISIBILITY, INFO_GLOW_ON_JOIN, INFO_FORCED_GLOW, INFO_GLOW_BLOCKED_REASON, INFO_GLOW_INFO, INFO_EGLOW_VERSION, INFO_DATABASE_TYPE, INFO_LOADED_PLUGINHOOKS, INFO_UPDATE_PLUGIN, GLOWING_STATE_ON_JOIN, NON_GLOWING_STATE_ON_JOIN, NO_LAST_GLOW, INCORRECT_USAGE, PLAYER_NOT_FOUND, COMMAND_LIST, PLAYER_ONLY, UPDATE_MESSAGE, INVISIBILITY_BLOCKED, INVISIBILITY_ALLOWED, WORLD_BLOCKED, WORLD_ALLOWED, ANIMATION_BLOCKED, CITIZENS_NPC_NOT_FOUND, CUSTOM_MENU_RELOAD_BLOCKED, COMMANDS_USER_HELP, COMMANDS_USER_TOGGLE, COMMANDS_USER_TOGGLEGLOWONJOIN, COMMANDS_USER_VISIBILITY_VISIBILITY, COMMANDS_USER_VISIBILITY_ALL, COMMANDS_USER_VISIBILITY_OTHER, COMMANDS_USER_VISIBILITY_OWN, COMMANDS_USER_VISIBILITY_NONE, COMMANDS_USER_LIST, COMMANDS_ADMIN_SET, COMMANDS_ADMIN_UNSET, COMMANDS_ADMIN_GLOWONJOIN_GLOWONJOIN, COMMANDS_ADMIN_GLOWONJOIN_TRUE, COMMANDS_ADMIN_GLOWONJOIN_FALSE, COMMANDS_ADMIN_DEBUG, COMMANDS_ADMIN_INFO, COMMANDS_ADMIN_RELOAD, COMMANDS_EFFECTS_SOLID_RED, COMMANDS_EFFECTS_SOLID_DARKRED, COMMANDS_EFFECTS_SOLID_GOLD, COMMANDS_EFFECTS_SOLID_YELLOW, COMMANDS_EFFECTS_SOLID_GREEN, COMMANDS_EFFECTS_SOLID_DARKGREEN, COMMANDS_EFFECTS_SOLID_AQUA, COMMANDS_EFFECTS_SOLID_DARKAQUA, COMMANDS_EFFECTS_SOLID_BLUE, COMMANDS_EFFECTS_SOLID_DARKBLUE, COMMANDS_EFFECTS_SOLID_PURPLE, COMMANDS_EFFECTS_SOLID_PINK, COMMANDS_EFFECTS_SOLID_WHITE, COMMANDS_EFFECTS_SOLID_GRAY, COMMANDS_EFFECTS_SOLID_DARKGRAY, COMMANDS_EFFECTS_SOLID_BLACK, COMMANDS_EFFECTS_EFFECT_BLINK, COMMANDS_EFFECTS_EFFECT_RAINBOW, COMMANDS_EFFECTS_EFFECT_SPEED_SLOW, COMMANDS_EFFECTS_EFFECT_SPEED_FAST, GUI_DISABLED, GUI_TITLE, GUI_COLOR, GUI_YES, GUI_NO, GUI_NOT_AVAILABLE, GUI_LEFT_CLICK, GUI_RIGHT_CLICK, GUI_CLICK_TO_TOGGLE, GUI_CLICK_TO_CYCLE, GUI_CLICK_TO_OPEN, GUI_PREVIOUS_PAGE, GUI_NEXT_PAGE, GUI_PAGE_LORE, GUI_MAIN_MENU, GUI_COOLDOWN, GUI_COLOR_PERMISSION, GUI_BLINK_PERMISSION, GUI_EFFECT_PERMISSION, GUI_SETTINGS_NAME, GUI_GLOW_ON_JOIN, GUI_LAST_GLOW, GUI_GLOW_ITEM_NAME, GUI_GLOWING, GLOW_VISIBILITY_ITEM_NAME, GLOW_VISIBILITY_INDICATOR, GUI_SPEED_ITEM_NAME, GUI_SPEED, GUI_CUSTOM_EFFECTS_ITEM_NAME};
      }
   }
}

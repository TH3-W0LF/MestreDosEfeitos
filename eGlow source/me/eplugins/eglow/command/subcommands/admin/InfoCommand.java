package me.eplugins.eglow.command.subcommands.admin;

import java.util.Iterator;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.command.SubCommand;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.Dependency;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class InfoCommand extends SubCommand {
   public String getName() {
      return "info";
   }

   public String getPermission() {
      return "eglow.command.info";
   }

   public String[] getSyntax() {
      return new String[]{"/eGlow info <player>"};
   }

   public boolean isPlayerCmd() {
      return false;
   }

   public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
      if (args.length >= 2) {
         Player player = Bukkit.getPlayer(args[1]);
         if (player != null) {
            eGlowPlayer = DataManager.getEGlowPlayer(player);
         }
      }

      ChatUtil.sendPlainMsg(sender, "&f&m                             &r &eeGlow &finfo &f&m                               ", false);
      String version;
      if (!EGlowMessageConfig.Message.INFO_CUSTOM_LINES.getStringList().isEmpty()) {
         Iterator var6 = EGlowMessageConfig.Message.INFO_CUSTOM_LINES.getStringList().iterator();

         while(var6.hasNext()) {
            version = (String)var6.next();
            ChatUtil.sendPlainMsg(sender, version, false);
         }

         ChatUtil.sendPlainMsg(sender, "&f", false);
      }

      if (eGlowPlayer != null) {
         ChatUtil.sendPlainMsg(sender, EGlowMessageConfig.Message.INFO_PLAYER_INFO.get(), false);
         ChatUtil.sendPlainMsg(sender, "  " + EGlowMessageConfig.Message.INFO_PLAYER_NAME.get() + eGlowPlayer.getDisplayName(), false);
         ChatUtil.sendPlainMsg(sender, "  " + EGlowMessageConfig.Message.INFO_LAST_GLOWEFFECT.get() + eGlowPlayer.getLastGlowName(), false);
         ChatUtil.sendPlainMsg(sender, "  " + EGlowMessageConfig.Message.INFO_GLOW_VISIBILITY.get() + eGlowPlayer.getGlowVisibility().name(), false);
         ChatUtil.sendPlainMsg(sender, "  " + EGlowMessageConfig.Message.INFO_GLOW_ON_JOIN.get() + (eGlowPlayer.isGlowOnJoin() ? "true" : "false"), false);
         ChatUtil.sendPlainMsg(sender, "  " + EGlowMessageConfig.Message.INFO_FORCED_GLOW.get() + (eGlowPlayer.getForcedEffect() == null ? "None" : eGlowPlayer.getForcedEffect().getName()), false);
         ChatUtil.sendPlainMsg(sender, "  " + EGlowMessageConfig.Message.INFO_GLOW_BLOCKED_REASON.get() + eGlowPlayer.getGlowDisableReason(), false);
      }

      ChatUtil.sendPlainMsg(sender, "&f", false);
      ChatUtil.sendPlainMsg(sender, EGlowMessageConfig.Message.INFO_GLOW_INFO.get(), false);
      ChatUtil.sendPlainMsg(sender, "  " + EGlowMessageConfig.Message.INFO_EGLOW_VERSION.get() + EGlow.getInstance().getDescription().getVersion() + (!EGlow.getInstance().isUpToDate() ? EGlowMessageConfig.Message.INFO_UPDATE_PLUGIN.get() : ""), false);
      ChatUtil.sendPlainMsg(sender, "  " + EGlowMessageConfig.Message.INFO_DATABASE_TYPE.get() + (EGlowMainConfig.MainConfig.MYSQL_ENABLE.getBoolean() ? "MySQL" : "SQLite"), false);
      ChatUtil.sendPlainMsg(sender, "  " + EGlowMessageConfig.Message.INFO_LOADED_PLUGINHOOKS.get(), false);
      Plugin tabPlugin = Dependency.TAB.getPlugin();
      if (tabPlugin != null) {
         version = Dependency.TAB.getVersion();
         ChatUtil.sendPlainMsg(sender, "    &f- &eTAB " + (this.getInstance().getTabAddon().isVersionSupported() ? "" : version + EGlowMessageConfig.Message.INFO_UPDATE_PLUGIN.get()), false);
      }

      if (Dependency.PLACEHOLDER_API.isLoaded()) {
         ChatUtil.sendPlainMsg(sender, "    &f- &ePlaceholderAPI ", false);
      }

      if (this.getInstance().getCitizensAddon() != null) {
         ChatUtil.sendPlainMsg(sender, "    &f- &eCitizens", false);
      }

      if (this.getInstance().getLpAddon() != null) {
         ChatUtil.sendPlainMsg(sender, "    &f- &eLuckPerms", false);
      }

      if (this.getInstance().getVaultAddon() != null) {
         ChatUtil.sendPlainMsg(sender, "    &f- &eVault", false);
      }

      if (this.getInstance().getGsitAddon() != null || Dependency.GSIT.isLoaded()) {
         version = Dependency.TAB.getVersion();
         ChatUtil.sendPlainMsg(sender, "    &f- &eGSit " + (Integer.parseInt(Dependency.GSIT.getVersion().replaceAll("[^\\d]", "")) >= 200 ? "" : version + EGlowMessageConfig.Message.INFO_UPDATE_PLUGIN.get()), false);
      }

      ChatUtil.sendPlainMsg(sender, "&f&m                                                                          ", false);
   }
}

package me.eplugins.eglow.command.subcommands.admin;

import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.command.SubCommand;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.DebugUtil;
import me.eplugins.eglow.util.enums.Dependency;
import me.eplugins.eglow.util.packets.PacketUtil;
import me.eplugins.eglow.util.packets.PipelineInjector;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class DebugCommand extends SubCommand {
   public String getName() {
      return "debug";
   }

   public String getPermission() {
      return "eglow.command.debug";
   }

   public String[] getSyntax() {
      return new String[]{"/eGlow debug <player>"};
   }

   public boolean isPlayerCmd() {
      return false;
   }

   public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
      ChatUtil.sendPlainMsg(sender, "&f&m                             &r &fDebug info for &eeGlow: &f&m                               ", false);
      if (args.length >= 2) {
         Player player = Bukkit.getPlayer(args[1]);
         if (player != null) {
            eGlowPlayer = DataManager.getEGlowPlayer(player);
         }
      }

      StringBuilder plugins = new StringBuilder(" ");
      if (eGlowPlayer != null) {
         ChatUtil.sendPlainMsg(sender, "&fPlayer info (&e" + eGlowPlayer.getDisplayName() + "&f)", false);
         ChatUtil.sendPlainMsg(sender, "  &fTeamname: &e" + eGlowPlayer.getTeamName(), false);
         ChatUtil.sendPlainMsg(sender, "  &fClient version: &e" + eGlowPlayer.getVersion().getFriendlyName(), false);
         ChatUtil.sendPlainMsg(sender, "  &f", false);
         ChatUtil.sendPlainMsg(sender, "  &fLast gloweffect: " + eGlowPlayer.getLastGlowName(), false);
         ChatUtil.sendPlainMsg(sender, "  &fGlow visibility: &e" + eGlowPlayer.getGlowVisibility().name(), false);
         ChatUtil.sendPlainMsg(sender, "  &fGlow on join: " + (eGlowPlayer.isGlowOnJoin() ? "&aTrue" : "&cFalse"), false);
         ChatUtil.sendPlainMsg(sender, "  &fForced glow: " + (eGlowPlayer.getForcedEffect() == null ? "&eNone" : eGlowPlayer.getForcedEffect().getName()), false);
         ChatUtil.sendPlainMsg(sender, "  &fGlow blocked reason: &e" + eGlowPlayer.getGlowDisableReason(), false);
      }

      ChatUtil.sendPlainMsg(sender, "&f&m                                                                               ", false);
      ChatUtil.sendPlainMsg(sender, "&fServer version: &e" + DebugUtil.getServerVersion(), false);
      ChatUtil.sendPlainMsg(sender, "&fDatabase type: &e" + (EGlowMainConfig.MainConfig.MYSQL_ENABLE.getBoolean() ? "MySQL" : "SQLite"), false);
      ChatUtil.sendPlainMsg(sender, "&fEGlow team packets: &e" + PacketUtil.getSendTeamPackets(), false);
      ChatUtil.sendPlainMsg(sender, "&fEGlow packet modifier: &e" + PipelineInjector.blockPackets(), false);
      ChatUtil.sendPlainMsg(sender, "Plugins:", false);
      Plugin[] var5 = Bukkit.getPluginManager().getPlugins();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Plugin plugin = var5[var7];
         String pluginName = plugin.getDescription().getName();
         if (plugin.isEnabled()) {
            String pluginText = getPluginText(plugin, pluginName);
            plugins.append(pluginText);
         } else {
            plugins.append("&c").append(pluginName).append("&f, ");
         }
      }

      ChatUtil.sendPlainMsg(sender, ChatUtil.translateColors(plugins.substring(0, plugins.length() - 2)), false);
      if (EGlow.getInstance().getTabAddon() != null && !EGlow.getInstance().getTabAddon().isVersionSupported() && Dependency.TAB.isLoaded()) {
         ChatUtil.sendPlainMsg(sender, ChatUtil.translateColors("&cYour TAB version seems incompatible with this eGlow version&f!"), false);
      }

      ChatUtil.sendPlainMsg(sender, "&f&m                                                                               ", false);
   }

   private static String getPluginText(Plugin plugin, String pluginName) {
      String pluginText = pluginName.equalsIgnoreCase("eGlow") ? "&6" + pluginName + " &f(" + plugin.getDescription().getVersion() + "), " : "&a" + pluginName + "&f, ";
      if (pluginName.equalsIgnoreCase("TAB")) {
         String secretCode = EGlow.getInstance().getTabAddon().isVersionSupported() ? (EGlow.getInstance().getTabAddon().isSettingNametagPrefixSuffixEnabled() ? "&a" : "&c") + "F" + (EGlow.getInstance().getTabAddon().isSettingTeamPacketBlockingEnabled() ? "&a" : "&c") + "B" : "&c!";
         pluginText = "&6" + pluginName + " &f(" + plugin.getDescription().getVersion() + ") (" + secretCode + "&f), ";
      }

      if (pluginName.equalsIgnoreCase("GSit") && Integer.parseInt(Dependency.GSIT.getVersion().replaceAll("[^\\d]", "")) < 200) {
         pluginText = "&6" + pluginName + " &f(" + plugin.getDescription().getVersion() + "), ";
      }

      return pluginText;
   }
}

package me.eplugins.eglow.command.subcommands;

import me.eplugins.eglow.command.SubCommand;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.text.ChatUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand extends SubCommand {
   public String getName() {
      return "help";
   }

   public String getPermission() {
      return "eglow.command.help";
   }

   public String[] getSyntax() {
      return new String[]{"/eGlow help"};
   }

   public boolean isPlayerCmd() {
      return false;
   }

   public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
      String prefix = "&f- /&eeglow &f";
      ChatUtil.sendPlainMsg(sender, "&f&m                 &r &fCommands for &eeGlow &r&f&m                 ", false);
      ChatUtil.sendPlainMsg(sender, "&fUser commands:", false);
      this.sendClickableText(sender, prefix + "(&eOpens GUI&f)", "/eglow");
      this.sendClickableText(sender, prefix + "help", "/eglow help");
      this.sendClickableText(sender, prefix + "toggle", "/eglow toggle");
      this.sendClickableText(sender, prefix + "toggleglowonjoin", "/eglow toggleonjoin");
      this.sendClickableText(sender, prefix + "visibility <&eall&f/&eother&f/&eown&f/&enone&f>", "/eglow visibility ");
      this.sendClickableText(sender, prefix + "list", "/eglow list");
      this.sendClickableText(sender, prefix + "<&eColor&f>", "/eglow ");
      this.sendClickableText(sender, prefix + "<&eBlink&f> <&eColor&f> <&eSpeed&f>", "/eglow blink ");
      this.sendClickableText(sender, prefix + "<&eEffect&f> <&eSpeed&f>", "/eglow ");
      ChatUtil.sendPlainMsg(sender, "&fAdmin commands:", false);
      this.sendClickableText(sender, prefix + "set <&ePlayer&f/&eNPC*&f> <&eColor&f>", "/eglow set ");
      this.sendClickableText(sender, prefix + "set <&ePlayer&f/&eNPC*&f> <&eBlink&f> <&eColor&f> <&eSpeed&f>", "/eglow set ");
      this.sendClickableText(sender, prefix + "set <&ePlayer&f/&eNPC*&f> <&eEffect&f> <&eSpeed>", "/eglow set ");
      this.sendClickableText(sender, prefix + "set <&ePlayer&f> glowonjoin <&eTrue&f/&eFalse&f>", "/eglow set ");
      this.sendClickableText(sender, prefix + "unset <&ePlayer&f/&eNPC*&f>", "/eglow unset ");
      this.sendClickableText(sender, prefix + "info", "/eglow info ");
      this.sendClickableText(sender, prefix + "debug", "/eglow debug ");
      this.sendClickableText(sender, prefix + "reload", "/eglow reload");
      ChatUtil.sendPlainMsg(sender, "&f*&enpc:s&f, &enpc:sel&f, &enpc:selected&f, &enpc:<ID>", false);
      ChatUtil.sendPlainMsg(sender, "&f&m                                                             ", false);
   }

   private void sendClickableText(CommandSender sender, String text, String command) {
      if (sender instanceof Player) {
         try {
            Player player = (Player)sender;
            TextComponent message = new TextComponent(ChatUtil.translateColors(text));
            message.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, command));
            message.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatUtil.translateColors("&fClick to use this command."))).create()));
            player.spigot().sendMessage(message);
         } catch (Exception var6) {
            ChatUtil.sendPlainMsg(sender, text, false);
         }
      } else {
         ChatUtil.sendPlainMsg(sender, text, false);
      }

   }
}

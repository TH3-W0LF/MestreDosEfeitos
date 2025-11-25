package me.eplugins.eglow.command.subcommands;

import me.eplugins.eglow.command.SubCommand;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.command.CommandSender;

public class ToggleGlowOnJoinCommand extends SubCommand {
   public String getName() {
      return "toggleglowonjoin";
   }

   public String getPermission() {
      return "eglow.command.toggleglowonjoin";
   }

   public String[] getSyntax() {
      return new String[]{"/eGlow toggleglowonjoin"};
   }

   public boolean isPlayerCmd() {
      return true;
   }

   public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
      eGlowPlayer.setGlowOnJoin(!eGlowPlayer.isGlowOnJoin());
      ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.GLOWONJOIN_TOGGLE.get(String.valueOf(eGlowPlayer.isGlowOnJoin())), true);
   }
}

package me.eplugins.eglow.command.subcommands;

import me.eplugins.eglow.command.SubCommand;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.packets.PacketUtil;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.command.CommandSender;

public class VisibilityCommand extends SubCommand {
   public String getName() {
      return "visibility";
   }

   public String getPermission() {
      return "eglow.command.visibility";
   }

   public String[] getSyntax() {
      return new String[]{"/eGlow visibility <all/other/own/none>"};
   }

   public boolean isPlayerCmd() {
      return true;
   }

   public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
      if (args.length >= 2) {
         if (eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT)) {
            ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.UNSUPPORTED_GLOW.get(), true);
            return;
         }

         String var4 = args[1].toLowerCase();
         byte var5 = -1;
         switch(var4.hashCode()) {
         case 96673:
            if (var4.equals("all")) {
               var5 = 0;
            }
            break;
         case 110470:
            if (var4.equals("own")) {
               var5 = 2;
            }
            break;
         case 3387192:
            if (var4.equals("none")) {
               var5 = 3;
            }
            break;
         case 106069776:
            if (var4.equals("other")) {
               var5 = 1;
            }
         }

         switch(var5) {
         case 0:
         case 1:
         case 2:
         case 3:
            EnumUtil.GlowVisibility oldVisibility = eGlowPlayer.getGlowVisibility();
            EnumUtil.GlowVisibility newVisibility = EnumUtil.GlowVisibility.valueOf(args[1].toUpperCase());
            if (!oldVisibility.equals(newVisibility)) {
               eGlowPlayer.setGlowVisibility(newVisibility);
               PacketUtil.forceUpdateGlow(eGlowPlayer);
            }

            ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.VISIBILITY_CHANGE.get(newVisibility.name()), true);
            break;
         default:
            this.sendSyntax(sender);
            return;
         }
      } else {
         switch(eGlowPlayer.getGlowVisibility()) {
         case ALL:
            eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.OTHER);
            break;
         case OTHER:
            eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.OWN);
            break;
         case OWN:
            eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.NONE);
            break;
         case NONE:
            eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.ALL);
         }

         PacketUtil.forceUpdateGlow(eGlowPlayer);
         ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.VISIBILITY_CHANGE.get(eGlowPlayer.getGlowVisibility().name()), true);
      }

   }
}

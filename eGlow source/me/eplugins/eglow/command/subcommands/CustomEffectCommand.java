package me.eplugins.eglow.command.subcommands;

import me.eplugins.eglow.command.SubCommand;
import me.eplugins.eglow.data.EGlowPlayer;
import org.bukkit.command.CommandSender;

public class CustomEffectCommand extends SubCommand {
   public String getName() {
      return "custom";
   }

   public String getPermission() {
      return "eglow.effect.custom";
   }

   public String[] getSyntax() {
      return new String[]{"/eGlow custom <delay> <color> <color>..."};
   }

   public boolean isPlayerCmd() {
      return true;
   }

   public void perform(CommandSender sender, EGlowPlayer ePlayer, String[] args) {
      if (args.length <= 3) {
         this.sendSyntax(sender);
      } else {
         try {
            int var4 = (int)(20.0D * Double.parseDouble(args[1]));
         } catch (NumberFormatException var7) {
         }

      }
   }
}

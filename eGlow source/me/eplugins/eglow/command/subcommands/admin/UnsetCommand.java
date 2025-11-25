package me.eplugins.eglow.command.subcommands.admin;

import java.util.Iterator;
import java.util.Set;
import me.eplugins.eglow.command.SubCommand;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.command.CommandSender;

public class UnsetCommand extends SubCommand {
   public String getName() {
      return "unset";
   }

   public String getPermission() {
      return "eglow.command.unset";
   }

   public String[] getSyntax() {
      return new String[]{"/eGlow unset <player/npc>"};
   }

   public boolean isPlayerCmd() {
      return false;
   }

   public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
      Set<EGlowPlayer> eGlowTargets = this.getTarget(sender, args);
      if (eGlowTargets.isEmpty()) {
         this.sendSyntax(sender);
      } else {
         Iterator var5 = eGlowTargets.iterator();

         while(var5.hasNext()) {
            EGlowPlayer eTarget = (EGlowPlayer)var5.next();
            if (eTarget != null) {
               if (eTarget.isGlowing()) {
                  eTarget.disableGlow(false);
                  if (eTarget.getEntityType().equals(EnumUtil.EntityType.PLAYER) && EGlowMainConfig.MainConfig.SETTINGS_NOTIFICATIONS_TARGET_COMMAND.getBoolean()) {
                     ChatUtil.sendMsg(eTarget.getPlayer(), EGlowMessageConfig.Message.TARGET_NOTIFICATION_PREFIX.get() + EGlowMessageConfig.Message.DISABLE_GLOW.get(), true);
                  }
               }

               if (!args[args.length - 1].equalsIgnoreCase("-s")) {
                  ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.OTHER_CONFIRM_OFF.get(eTarget), true);
               }
            }
         }

      }
   }
}

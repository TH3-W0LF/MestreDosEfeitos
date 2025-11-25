package me.eplugins.eglow.command.subcommands.admin;

import java.util.Iterator;
import java.util.Set;
import me.eplugins.eglow.command.SubCommand;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowEffect;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.command.CommandSender;

public class SetCommand extends SubCommand {
   public String getName() {
      return "set";
   }

   public String getPermission() {
      return "eglow.command.set";
   }

   public String[] getSyntax() {
      return new String[]{"/eGlow set <player/npc> <color>", "/eGlow set <player/npc> blink <color> <speed>", "/eGlow set <player/npc> <effect> <speed>", "/eGlow set <player/npc> glowonjoin <true/false>"};
   }

   public boolean isPlayerCmd() {
      return false;
   }

   public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
      Set<EGlowPlayer> eGlowTargets = this.getTarget(sender, args);
      boolean isSilent = args[args.length - 1].equalsIgnoreCase("-s");
      if (eGlowTargets.isEmpty()) {
         this.sendSyntax(sender);
      } else {
         Iterator var6 = eGlowTargets.iterator();

         while(true) {
            while(true) {
               EGlowPlayer eGlowTarget;
               EGlowEffect eGlowEffect;
               do {
                  if (!var6.hasNext()) {
                     return;
                  }

                  eGlowTarget = (EGlowPlayer)var6.next();
                  eGlowEffect = null;
               } while(eGlowTarget == null);

               if (!isSilent) {
                  switch(args.length) {
                  case 3:
                     eGlowEffect = DataManager.getEGlowEffect(args[2].toLowerCase().replace("off", "none").replace("disable", "none"));
                     break;
                  case 4:
                     if (args[2].equalsIgnoreCase("glowonjoin")) {
                        eGlowTarget.setGlowOnJoin(Boolean.parseBoolean(args[3].toLowerCase()));
                        ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.OTHER_GLOW_ON_JOIN_CONFIRM.get(eGlowTarget, args[3].toLowerCase()), true);
                        continue;
                     }

                     eGlowEffect = DataManager.getEGlowEffect(args[2] + args[3]);
                     break;
                  case 5:
                     eGlowEffect = DataManager.getEGlowEffect(args[2] + args[3] + args[4]);
                  }
               } else {
                  switch(args.length) {
                  case 4:
                     eGlowEffect = DataManager.getEGlowEffect(args[2].toLowerCase().replace("off", "none").replace("disable", "none"));
                     break;
                  case 5:
                     if (args[2].equalsIgnoreCase("glowonjoin")) {
                        eGlowTarget.setGlowOnJoin(Boolean.parseBoolean(args[3].toLowerCase()));
                        ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.OTHER_GLOW_ON_JOIN_CONFIRM.get(eGlowTarget, args[3].toLowerCase()), true);
                        continue;
                     }

                     eGlowEffect = DataManager.getEGlowEffect(args[2] + args[3]);
                     break;
                  case 6:
                     eGlowEffect = DataManager.getEGlowEffect(args[2] + args[3] + args[4]);
                  }
               }

               if (eGlowEffect == null) {
                  this.sendSyntax(sender);
                  return;
               }

               if (eGlowEffect.getName().equals("none")) {
                  if (eGlowTarget.isGlowing()) {
                     eGlowTarget.disableGlow(false);
                     if (eGlowTarget.getEntityType().equals(EnumUtil.EntityType.PLAYER) && EGlowMainConfig.MainConfig.SETTINGS_NOTIFICATIONS_TARGET_COMMAND.getBoolean()) {
                        ChatUtil.sendMsg(eGlowTarget.getPlayer(), EGlowMessageConfig.Message.TARGET_NOTIFICATION_PREFIX.get() + EGlowMessageConfig.Message.DISABLE_GLOW.get(), true);
                     }
                  }

                  ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.OTHER_CONFIRM_OFF.get(eGlowTarget), true);
               } else {
                  if (!eGlowTarget.isSameGlow(eGlowEffect)) {
                     eGlowTarget.activateGlow(eGlowEffect);
                     if (eGlowTarget.getEntityType().equals(EnumUtil.EntityType.PLAYER) && EGlowMainConfig.MainConfig.SETTINGS_NOTIFICATIONS_TARGET_COMMAND.getBoolean()) {
                        ChatUtil.sendMsg(eGlowTarget.getPlayer(), EGlowMessageConfig.Message.TARGET_NOTIFICATION_PREFIX.get() + EGlowMessageConfig.Message.NEW_GLOW.get(eGlowEffect.getDisplayName()), true);
                     }
                  }

                  if (!args[args.length - 1].equalsIgnoreCase("-s")) {
                     ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.OTHER_CONFIRM.get(eGlowTarget, eGlowEffect.getDisplayName()), true);
                  }
               }
            }
         }
      }
   }
}

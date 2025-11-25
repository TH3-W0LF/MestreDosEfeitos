package me.eplugins.eglow.command.subcommands;

import me.eplugins.eglow.command.SubCommand;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowEffect;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.command.CommandSender;

public class EffectCommand extends SubCommand {
   public String getName() {
      return "effect";
   }

   public String getPermission() {
      return "";
   }

   public String[] getSyntax() {
      return new String[]{"/eGlow <color>", "/eGlow blink <color> <speed>", "/eGlow <effect> <speed>"};
   }

   public boolean isPlayerCmd() {
      return true;
   }

   public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
      switch(eGlowPlayer.getGlowDisableReason()) {
      case BLOCKEDWORLD:
         ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.WORLD_BLOCKED.get(), true);
         return;
      case INVISIBLE:
         ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.INVISIBILITY_BLOCKED.get(), true);
         return;
      case ANIMATION:
         ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.ANIMATION_BLOCKED.get(), true);
         return;
      default:
         EGlowEffect eGlowEffect = null;
         EGlowEffect currentEGlowEffect = eGlowPlayer.getGlowEffect();
         switch(args.length) {
         case 1:
            eGlowEffect = DataManager.getEGlowEffect(args[0].replace("off", "none").replace("disable", "none"));
            if (eGlowEffect == null && currentEGlowEffect != null) {
               if (currentEGlowEffect.getName().contains(args[0].toLowerCase())) {
                  eGlowEffect = this.switchEffectSpeed(currentEGlowEffect);
               } else {
                  eGlowEffect = DataManager.getEGlowEffect(args[0].toLowerCase() + currentEGlowEffect.getName() + "slow");
               }
            }
            break;
         case 2:
            eGlowEffect = DataManager.getEGlowEffect(args[0] + args[1]);
            if (eGlowEffect == null && currentEGlowEffect != null && currentEGlowEffect.getName().contains((args[0] + args[1]).toLowerCase())) {
               eGlowEffect = this.switchEffectSpeed(currentEGlowEffect);
            }
            break;
         case 3:
            eGlowEffect = DataManager.getEGlowEffect(args[0] + args[1] + args[2]);
         }

         if (eGlowEffect == null) {
            this.sendSyntax(sender);
         } else {
            if (!eGlowPlayer.hasPermission(eGlowEffect.getPermissionNode()) && (!DataManager.isCustomEffect(eGlowEffect.getName()) || !eGlowPlayer.hasPermission("eglow.egloweffect.*"))) {
               ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.NO_PERMISSION.get(), true);
            } else if (eGlowEffect.getName().equals("none")) {
               eGlowPlayer.disableGlow(false);
               ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.DISABLE_GLOW.get(), true);
            } else {
               if (eGlowPlayer.isSameGlow(eGlowEffect)) {
                  ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.SAME_GLOW.get(), true);
                  return;
               }

               eGlowPlayer.activateGlow(eGlowEffect);
               ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.NEW_GLOW.get(eGlowEffect.getDisplayName()), true);
               if (eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT)) {
                  ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.UNSUPPORTED_GLOW.get(), true);
               }
            }

         }
      }
   }

   private EGlowEffect switchEffectSpeed(EGlowEffect eGlowEffect) {
      String effectName = eGlowEffect.getName();
      return effectName.contains("slow") ? DataManager.getEGlowEffect(effectName.replace("slow", "fast")) : DataManager.getEGlowEffect(effectName.replace("fast", "slow"));
   }
}

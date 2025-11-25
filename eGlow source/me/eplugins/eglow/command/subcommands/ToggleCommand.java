package me.eplugins.eglow.command.subcommands;

import me.eplugins.eglow.command.SubCommand;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowEffect;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.command.CommandSender;

public class ToggleCommand extends SubCommand {
   public String getName() {
      return "toggle";
   }

   public String getPermission() {
      return "eglow.command.toggle";
   }

   public String[] getSyntax() {
      return new String[]{"/eGlow toggle"};
   }

   public boolean isPlayerCmd() {
      return true;
   }

   public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
      if (eGlowPlayer.isGlowing()) {
         eGlowPlayer.disableGlow(false);
         ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.DISABLE_GLOW.get(), true);
      } else {
         if (eGlowPlayer.getGlowEffect() == null || eGlowPlayer.getGlowEffect().getName().equals("none")) {
            ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.NO_LAST_GLOW.get(), true);
            return;
         }

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
         }

         EGlowEffect currentEGlowEffect = eGlowPlayer.getGlowEffect();
         if (!eGlowPlayer.hasPermission(currentEGlowEffect.getPermissionNode()) && (!DataManager.isCustomEffect(currentEGlowEffect.getName()) || !eGlowPlayer.hasPermission("eglow.egloweffect.*")) && !eGlowPlayer.isForcedGlow(currentEGlowEffect)) {
            ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.NO_PERMISSION.get(), true);
            return;
         }

         eGlowPlayer.activateGlow();
         ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.NEW_GLOW.get(eGlowPlayer.getLastGlowName()), true);
      }

   }
}

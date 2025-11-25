package me.eplugins.eglow.command.subcommands.admin;

import java.util.Iterator;
import me.eplugins.eglow.addon.internal.AdvancedGlowVisibilityAddon;
import me.eplugins.eglow.command.SubCommand;
import me.eplugins.eglow.config.EGlowCustomEffectsConfig;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.custommenu.config.EGlowCustomMenuConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.database.EGlowPlayerdataManager;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {
   public String getName() {
      return "reload";
   }

   public String getPermission() {
      return "eglow.command.reload";
   }

   public String[] getSyntax() {
      return new String[]{"/eGlow reload"};
   }

   public boolean isPlayerCmd() {
      return false;
   }

   public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
      if (EGlowMainConfig.reloadConfig() && EGlowMessageConfig.reloadConfig() && EGlowCustomEffectsConfig.reloadConfig()) {
         if (this.getInstance().isBeta()) {
            EGlowCustomMenuConfig.reloadConfig();
         }

         EGlowPlayerdataManager.setMysql_Failed(false);
         NMSHook.registerCommandAlias();
         DataManager.addEGlowEffects();
         this.getInstance().getTablistFormattingAddon().onReload();
         boolean advancedGlowVisibilityEnabled = EGlowMainConfig.MainConfig.ADVANCED_GLOW_VISIBILITY_ENABLE.getBoolean();
         if (advancedGlowVisibilityEnabled && this.getInstance().getAdvancedGlowVisibilityAddon() == null) {
            this.getInstance().setAdvancedGlowVisibilityAddon(new AdvancedGlowVisibilityAddon());
         } else if (!advancedGlowVisibilityEnabled && this.getInstance().getAdvancedGlowVisibilityAddon() != null) {
            this.getInstance().getAdvancedGlowVisibilityAddon().shutdown();
         }

         Iterator var5 = DataManager.getEGlowPlayers().iterator();

         while(var5.hasNext()) {
            EGlowPlayer eGlowTarget = (EGlowPlayer)var5.next();
            if (eGlowTarget != null) {
               eGlowTarget.setupForceGlows();
               eGlowTarget.updatePlayerTabname();
               EnumUtil.GlowDisableReason oldGlowDisableReason = eGlowTarget.getGlowDisableReason();
               EnumUtil.GlowDisableReason newGlowDisableReason = eGlowTarget.setGlowDisableReason(EnumUtil.GlowDisableReason.NONE);
               if (!oldGlowDisableReason.equals(newGlowDisableReason)) {
                  if (oldGlowDisableReason.equals(EnumUtil.GlowDisableReason.NONE)) {
                     if (eGlowTarget.isGlowing()) {
                        eGlowTarget.disableGlow(false);
                     }
                  } else {
                     eGlowTarget.activateGlow();
                  }
               }
            }
         }

         ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.RELOAD_SUCCESS.get(), true);
      } else {
         ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.RELOAD_FAIL.get(), true);
      }

   }
}

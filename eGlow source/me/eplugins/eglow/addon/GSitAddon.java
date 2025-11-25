package me.eplugins.eglow.addon;

import dev.geco.gsit.api.GSitAPI;
import dev.geco.gsit.api.event.PlayerPoseEvent;
import dev.geco.gsit.api.event.PlayerStopPoseEvent;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.api.event.GlowColorChangeEvent;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GSitAddon extends AbstractAddonBase implements Listener {
   public GSitAddon(EGlow eGlowInstance) {
      super(eGlowInstance);
   }

   @EventHandler
   public void playerPoseEvent(PlayerPoseEvent event) {
      Player player = event.getPlayer();
      EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
      this.poseGlowCheck(eGlowPlayer, true);
   }

   @EventHandler
   public void playerUnposeEvent(PlayerStopPoseEvent event) {
      Player player = event.getPlayer();
      EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
      this.poseGlowCheck(eGlowPlayer, false);
   }

   @EventHandler
   public void onGlowChange(GlowColorChangeEvent event) {
      Player player = event.getPlayer();
      EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
      NMSHook.scheduleDelayedTask(false, 5L, () -> {
         this.poseGlowCheck(eGlowPlayer, GSitAPI.isPlayerPosing(player));
      });
   }

   private void poseGlowCheck(EGlowPlayer eGlowPlayer, boolean isPosing) {
      if (eGlowPlayer != null) {
         if (isPosing) {
            if (eGlowPlayer.isGlowing()) {
               eGlowPlayer.setGlowDisableReason(EnumUtil.GlowDisableReason.ANIMATION);
               eGlowPlayer.disableGlow(false);
            }
         } else if (eGlowPlayer.getGlowDisableReason().equals(EnumUtil.GlowDisableReason.ANIMATION) && !eGlowPlayer.isGlowing()) {
            eGlowPlayer.setGlowDisableReason(EnumUtil.GlowDisableReason.NONE);
            eGlowPlayer.activateGlow();
         }

      }
   }
}

package me.eplugins.eglow.api;

import java.util.List;
import java.util.UUID;
import me.eplugins.eglow.api.enums.EGlowBlink;
import me.eplugins.eglow.api.enums.EGlowColor;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowEffect;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.packets.PacketUtil;
import me.eplugins.eglow.util.packets.PipelineInjector;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EGlowAPI {
   public EGlowPlayer getEGlowPlayer(Player player) {
      return DataManager.getEGlowPlayer(player);
   }

   public EGlowPlayer getEGlowPlayer(UUID uuid) {
      Player player = Bukkit.getPlayer(uuid);
      return player != null ? DataManager.getEGlowPlayer(player) : null;
   }

   public EGlowEffect getEGlowEffect(String name) {
      EGlowEffect eGlowEffect = DataManager.getEGlowEffect(name);
      if (eGlowEffect == null) {
         ChatUtil.sendToConsole("(API) Unable to find effect for name: " + name, true);
      }

      return eGlowEffect;
   }

   public String getGlowColor(EGlowPlayer eGlowPlayer) {
      return eGlowPlayer != null && eGlowPlayer.isGlowing() ? String.valueOf(eGlowPlayer.getActiveColor()) : "";
   }

   public void enableGlow(EGlowPlayer eGlowPlayer, EGlowEffect eGlowEffect) {
      NMSHook.scheduleDelayedTask(true, 1L, () -> {
         if (eGlowPlayer != null) {
            eGlowPlayer.activateGlow(eGlowEffect);
         }
      });
   }

   public void enableGlow(EGlowPlayer eGlowPlayer, EGlowColor color) {
      NMSHook.scheduleDelayedTask(true, 1L, () -> {
         if (eGlowPlayer != null) {
            EGlowEffect eGlowEffect = DataManager.getEGlowEffect(color.toString());
            eGlowPlayer.activateGlow(eGlowEffect);
         }
      });
   }

   public void enableGlow(EGlowPlayer eGlowPlayer, EGlowBlink blink) {
      NMSHook.scheduleDelayedTask(true, 1L, () -> {
         if (eGlowPlayer != null) {
            EGlowEffect eGlowEffect = DataManager.getEGlowEffect(blink.toString());
            eGlowPlayer.activateGlow(eGlowEffect);
         }
      });
   }

   public void enableGlow(EGlowPlayer eGlowPlayer, me.eplugins.eglow.api.enums.EGlowEffect effect) {
      NMSHook.scheduleDelayedTask(true, 1L, () -> {
         if (eGlowPlayer != null) {
            EGlowEffect eGlowEffect = DataManager.getEGlowEffect(effect.toString());
            eGlowPlayer.activateGlow(eGlowEffect);
         }
      });
   }

   public void disableGlow(EGlowPlayer eGlowPlayer) {
      NMSHook.scheduleDelayedTask(true, 1L, () -> {
         if (eGlowPlayer != null) {
            eGlowPlayer.disableGlow(true);
         }
      });
   }

   public void addCustomGlowReceiver(EGlowPlayer eGlowPlayerSender, Player playerReceiver) {
      if (eGlowPlayerSender != null) {
         eGlowPlayerSender.addGlowTarget(playerReceiver);
         PacketUtil.forceUpdateGlow(eGlowPlayerSender);
      }
   }

   public void removeCustomGlowReceiver(EGlowPlayer eGlowPlayerSender, Player playerReceiver) {
      if (eGlowPlayerSender != null) {
         eGlowPlayerSender.removeGlowTarget(playerReceiver);
         PacketUtil.forceUpdateGlow(eGlowPlayerSender);
      }
   }

   public void setCustomGlowReceivers(EGlowPlayer eGlowPlayerSender, List<Player> playerReceiverList) {
      if (eGlowPlayerSender != null) {
         eGlowPlayerSender.setGlowTargets(playerReceiverList);
         PacketUtil.forceUpdateGlow(eGlowPlayerSender);
      }
   }

   public void resetCustomGlowReceivers(EGlowPlayer eGlowPlayer) {
      if (eGlowPlayer != null) {
         eGlowPlayer.resetGlowTargets();
         PacketUtil.forceUpdateGlow(eGlowPlayer);
      }
   }

   public void setSendTeamPackets(boolean status) {
      PacketUtil.setSendTeamPackets(status);
   }

   public void setPacketBlockerStatus(boolean status) {
      PipelineInjector.setBlockPackets(status);
   }
}

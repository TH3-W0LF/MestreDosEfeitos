package me.eplugins.eglow.event;

import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;

public class EGlowEventListener113AndAbove implements Listener {
   public EGlowEventListener113AndAbove() {
      EGlow.getInstance().getServer().getPluginManager().registerEvents(this, EGlow.getInstance());
   }

   @EventHandler
   public void PlayerPotionEvent(EntityPotionEffectEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof Player) {
         EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer((Player)entity);
         NMSHook.scheduleDelayedTask(true, 1L, () -> {
            if (eGlowPlayer != null) {
               if (!EGlowMainConfig.MainConfig.SETTINGS_DISABLE_GLOW_WHEN_INVISIBLE.getBoolean()) {
                  if (eGlowPlayer.getGlowDisableReason().equals(EnumUtil.GlowDisableReason.INVISIBLE)) {
                     eGlowPlayer.setGlowDisableReason(EnumUtil.GlowDisableReason.NONE);
                  }

               } else if (event.getNewEffect() != null && event.getNewEffect().getType().equals(PotionEffectType.INVISIBILITY) && eGlowPlayer.isGlowing()) {
                  eGlowPlayer.disableGlow(false);
                  eGlowPlayer.setGlowDisableReason(EnumUtil.GlowDisableReason.INVISIBLE);
                  if (EGlowMainConfig.MainConfig.SETTINGS_NOTIFICATIONS_INVISIBILITY.getBoolean()) {
                     ChatUtil.sendMsg(eGlowPlayer.getPlayer(), EGlowMessageConfig.Message.INVISIBILITY_BLOCKED.get(), true);
                  }

               } else {
                  if (event.getNewEffect() == null && event.getOldEffect() != null && event.getOldEffect().getType().equals(PotionEffectType.INVISIBILITY) && eGlowPlayer.getGlowDisableReason().equals(EnumUtil.GlowDisableReason.INVISIBLE) && eGlowPlayer.setGlowDisableReason(EnumUtil.GlowDisableReason.NONE).equals(EnumUtil.GlowDisableReason.NONE)) {
                     eGlowPlayer.activateGlow();
                     if (EGlowMainConfig.MainConfig.SETTINGS_NOTIFICATIONS_INVISIBILITY.getBoolean()) {
                        ChatUtil.sendMsg(eGlowPlayer.getPlayer(), EGlowMessageConfig.Message.INVISIBILITY_ALLOWED.get(), true);
                     }
                  }

               }
            }
         });
      }

   }
}

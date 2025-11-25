package me.eplugins.eglow.addon.tab;

import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.api.event.GlowColorChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class TABAddonEvents implements Listener {
   @EventHandler
   public void onColorChange(GlowColorChangeEvent event) {
      EGlow.getInstance().getTabAddon().requestTABPlayerUpdate(event.getPlayer());
   }

   @EventHandler
   public void onWorldChange(PlayerChangedWorldEvent event) {
      EGlow.getInstance().getTabAddon().requestTABPlayerUpdate(event.getPlayer());
   }
}

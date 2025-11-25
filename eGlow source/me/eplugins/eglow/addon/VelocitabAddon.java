package me.eplugins.eglow.addon;

import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.config.EGlowMainConfig;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VelocitabAddon extends AbstractAddonBase {
   public VelocitabAddon(EGlow eGlowInstance) {
      super(eGlowInstance);
      this.getEGlowInstance().getServer().getMessenger().registerOutgoingPluginChannel(this.getEGlowInstance(), "velocitab:update_team_color");
   }

   public void VelocitabUpdateRequest(Player player, ChatColor chatcolor) {
      if (EGlowMainConfig.MainConfig.ADVANCED_VELOCITAB_MESSAGING.getBoolean()) {
         player.sendPluginMessage(EGlow.getInstance(), "velocitab:update_team_color", String.valueOf(chatcolor.getChar()).getBytes());
      }
   }
}

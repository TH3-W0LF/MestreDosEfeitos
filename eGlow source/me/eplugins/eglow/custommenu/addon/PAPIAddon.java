package me.eplugins.eglow.custommenu.addon;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PAPIAddon {
   public String translatePlaceholders(Player player, String text) {
      return !text.contains("%") ? text : PlaceholderAPI.setPlaceholders(player, text);
   }
}

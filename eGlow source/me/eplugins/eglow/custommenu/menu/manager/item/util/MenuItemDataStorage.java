package me.eplugins.eglow.custommenu.menu.manager.item.util;

import java.util.Map;
import lombok.Generated;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class MenuItemDataStorage {
   private final ConfigurationSection itemSection;
   private final String filePath;
   private final Player player;
   private final Map<String, String> arguments;

   public MenuItemDataStorage(ConfigurationSection itemSection, String filePath, Player player, Map<String, String> arguments) {
      this.itemSection = itemSection;
      this.filePath = filePath;
      this.player = player;
      this.arguments = arguments;
   }

   @Generated
   public ConfigurationSection getItemSection() {
      return this.itemSection;
   }

   @Generated
   public String getFilePath() {
      return this.filePath;
   }

   @Generated
   public Player getPlayer() {
      return this.player;
   }

   @Generated
   public Map<String, String> getArguments() {
      return this.arguments;
   }
}

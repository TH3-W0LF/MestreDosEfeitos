package me.eplugins.eglow.custommenu.menu.handler;

import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.bukkit.entity.Player;

public class ActionDataStorage {
   private final List<String> actionCommands;
   private final String menuName;
   private final Player player;
   private final Map<String, String> arguments;

   public ActionDataStorage(List<String> actionCommands, String menuName, Player player, Map<String, String> arguments) {
      this.actionCommands = actionCommands;
      this.menuName = menuName;
      this.player = player;
      this.arguments = arguments;
   }

   @Generated
   public List<String> getActionCommands() {
      return this.actionCommands;
   }

   @Generated
   public String getMenuName() {
      return this.menuName;
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

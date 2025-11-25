package me.eplugins.eglow.custommenu.menu.manager;

import lombok.Generated;
import org.bukkit.entity.Player;

public class MenuManager {
   public MenuManager.MenuMetadata getMenuMetadata(Player player) {
      return new MenuManager.MenuMetadata(player);
   }

   public static class MenuMetadata {
      private final Player owner;
      private long lastClicked;

      public MenuMetadata(Player player) {
         this.owner = player;
         this.lastClicked = System.currentTimeMillis();
      }

      @Generated
      public Player getOwner() {
         return this.owner;
      }

      @Generated
      public long getLastClicked() {
         return this.lastClicked;
      }

      @Generated
      public void setLastClicked(long lastClicked) {
         this.lastClicked = lastClicked;
      }
   }
}

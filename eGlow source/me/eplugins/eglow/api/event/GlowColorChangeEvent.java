package me.eplugins.eglow.api.event;

import java.util.UUID;
import lombok.Generated;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GlowColorChangeEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private final Player player;
   private final UUID UUID;
   private final ChatColor color;
   private final boolean isGlowing;

   public GlowColorChangeEvent(Player player, UUID uuid, ChatColor color, boolean isGlowing) {
      this.player = player;
      this.UUID = uuid;
      this.color = color;
      this.isGlowing = isGlowing;
   }

   public UUID getPlayerUUID() {
      return this.UUID;
   }

   public String getColor() {
      return this.color != null && !this.color.equals(ChatColor.RESET) && this.isGlowing ? String.valueOf(this.color) : "";
   }

   public String getColorChar() {
      return this.color != null && !this.color.equals(ChatColor.RESET) && this.isGlowing ? String.valueOf(this.color.getChar()) : "";
   }

   public ChatColor getChatColor() {
      return this.color == null ? ChatColor.RESET : this.color;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   @Generated
   public Player getPlayer() {
      return this.player;
   }
}

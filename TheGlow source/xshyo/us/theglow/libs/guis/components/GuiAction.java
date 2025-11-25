package xshyo.us.theglow.libs.guis.components;

import org.bukkit.event.Event;

@FunctionalInterface
public interface GuiAction<T extends Event> {
   void execute(T var1);
}

package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.parser;

import java.util.Iterator;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.Event;

public interface Parser extends Iterator<Event> {
   boolean checkEvent(Event.ID var1);

   Event peekEvent();

   Event next();
}

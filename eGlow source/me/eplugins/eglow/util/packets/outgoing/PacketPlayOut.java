package me.eplugins.eglow.util.packets.outgoing;

import java.lang.reflect.Field;
import me.eplugins.eglow.util.packets.ProtocolVersion;

public abstract class PacketPlayOut {
   public abstract Object toNMS(ProtocolVersion var1) throws Exception;

   public String cutTo(String string, int length) {
      if (string != null && string.length() > length) {
         return string.charAt(length - 1) == 167 ? string.substring(0, length - 1) : string.substring(0, length);
      } else {
         return string;
      }
   }

   public static Object getField(Object packet, String field) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
      Field f = packet.getClass().getDeclaredField(field);
      f.setAccessible(true);
      Object value = f.get(packet);
      f.setAccessible(false);
      return value;
   }
}

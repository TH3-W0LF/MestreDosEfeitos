package me.eplugins.eglow.util.packets.outgoing;

import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.packets.chat.IChatBaseComponent;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.packets.nms.NMSStorage;

public class PacketPlayOutActionBar extends PacketPlayOut {
   private final IChatBaseComponent message;

   public PacketPlayOutActionBar(IChatBaseComponent message) {
      this.message = message;
   }

   public Object toNMS(ProtocolVersion clientVersion) throws Exception {
      NMSStorage nms = NMSHook.nms;
      Object component = NMSHook.stringToComponent(this.message.toString(clientVersion));
      return nms.newPlayOutPacketActionBar.newInstance(component);
   }
}

package me.eplugins.eglow.util.packets.outgoing;

import java.util.UUID;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.packets.chat.IChatBaseComponent;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.packets.nms.NMSStorage;

public class PacketPlayOutChat extends PacketPlayOut {
   private final IChatBaseComponent message;
   private final PacketPlayOutChat.ChatMessageType type;

   public PacketPlayOutChat(IChatBaseComponent message, PacketPlayOutChat.ChatMessageType type) {
      this.message = message;
      this.type = type;
   }

   public Object toNMS(ProtocolVersion clientVersion) throws Exception {
      NMSStorage nms = NMSHook.nms;
      Object component = NMSHook.stringToComponent(this.message.toString(clientVersion));
      if (nms.minorVersion >= 19) {
         return nms.newPacketPlayOutChat.newInstance(component, this.type.ordinal());
      } else if (nms.minorVersion >= 16) {
         return nms.newPacketPlayOutChat.newInstance(component, nms.ChatMessageType_values[this.type.ordinal()], UUID.randomUUID());
      } else if (nms.minorVersion >= 12) {
         return nms.newPacketPlayOutChat.newInstance(component, Enum.valueOf(nms.ChatMessageType, this.type.toString()));
      } else {
         return nms.minorVersion >= 8 ? nms.newPacketPlayOutChat.newInstance(component, (byte)this.type.ordinal()) : null;
      }
   }

   public static enum ChatMessageType {
      CHAT,
      SYSTEM,
      GAME_INFO;

      // $FF: synthetic method
      private static PacketPlayOutChat.ChatMessageType[] $values() {
         return new PacketPlayOutChat.ChatMessageType[]{CHAT, SYSTEM, GAME_INFO};
      }
   }
}

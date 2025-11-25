package me.eplugins.eglow.custommenu.addon;

import java.lang.reflect.Method;
import me.eplugins.eglow.custommenu.util.TextUtil;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.packets.nms.NMSStorage;

public class MiniMessagesAddon {
   Class<?> component;
   Class<?> miniMessage;
   Class<?> legacyComponentSerializer;
   Method miniMessage_getMiniMessage;
   Method legacyComponentSerializer_legacyAmpersand;
   Method legacyComponentSerializer_deserialize;
   Method miniMessage_serialize;

   public MiniMessagesAddon() {
      NMSStorage nms = NMSHook.nms;

      try {
         this.component = nms.getNMSClass("net.kyori.adventure.text.Component");
         this.miniMessage = nms.getNMSClass("net.kyori.adventure.text.minimessage.MiniMessage");
         this.legacyComponentSerializer = nms.getNMSClass("net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer");
         this.miniMessage_getMiniMessage = nms.getMethod(this.miniMessage, "miniMessage");
         this.miniMessage_serialize = nms.getMethod(this.miniMessage, new String[]{"serialize"}, this.component);
         this.legacyComponentSerializer_legacyAmpersand = nms.getMethod(this.legacyComponentSerializer, "legacyAmpersand");
         this.legacyComponentSerializer_deserialize = nms.getMethod(this.legacyComponentSerializer, new String[]{"deserialize"}, String.class);
      } catch (Exception var3) {
         TextUtil.sendException("&cFailed to setup reflection for MiniMessage support!", var3);
      }

   }

   public String translateMM(String text) {
      return text;
   }
}

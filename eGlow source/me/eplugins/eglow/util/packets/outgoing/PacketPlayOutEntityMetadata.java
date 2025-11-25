package me.eplugins.eglow.util.packets.outgoing;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.packets.datawatcher.DataWatcher;
import me.eplugins.eglow.util.packets.nms.NMSHook;

public class PacketPlayOutEntityMetadata extends PacketPlayOut {
   private final int entityId;
   private final DataWatcher dataWatcher;

   public PacketPlayOutEntityMetadata(int entityId, DataWatcher dataWatcher) {
      this.entityId = entityId;
      this.dataWatcher = dataWatcher;
   }

   public Object toNMS(ProtocolVersion clientVersion) throws Exception {
      if (NMSHook.nms.newPacketPlayOutEntityMetadata.getParameterCount() != 2) {
         return NMSHook.nms.newPacketPlayOutEntityMetadata.newInstance(this.entityId, this.dataWatcher.toNMS(), true);
      } else {
         List<Object> items = new ArrayList();
         if (NMSHook.nms.is1_20_5OrAbove) {
            items = new ArrayList((Collection)Collections.singletonList((List)this.dataWatcher.toNMS()).get(0));
         } else {
            ObjectIterator var3 = ((Int2ObjectMap)NMSHook.nms.DataWatcherItems.get(this.dataWatcher.toNMS())).values().iterator();

            while(var3.hasNext()) {
               Object object = var3.next();
               items.add(NMSHook.nms.DataWatcherItemToData.invoke(object));
            }
         }

         return NMSHook.nms.newPacketPlayOutEntityMetadata.newInstance(this.entityId, items);
      }
   }
}

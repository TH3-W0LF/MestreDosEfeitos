package me.eplugins.eglow.util.packets.datawatcher;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.packets.nms.NMSStorage;

public class DataWatcher {
   private final Map<Integer, DataWatcherItem> dataValues = new HashMap();

   public void setValue(DataWatcherObject type, Object value) {
      this.dataValues.put(type.position, new DataWatcherItem(type, value));
   }

   public DataWatcherItem getItem(int position) {
      return (DataWatcherItem)this.dataValues.get(position);
   }

   public Object toNMS() throws Exception {
      NMSStorage nms = NMSHook.nms;
      if (!nms.is1_20_5OrAbove) {
         Object nmsWatcher;
         if (nms.newDataWatcher.getParameterCount() == 1) {
            nmsWatcher = nms.newDataWatcher.newInstance(null);
         } else {
            nmsWatcher = nms.newDataWatcher.newInstance();
         }

         DataWatcherItem item;
         Object position;
         for(Iterator var6 = this.dataValues.values().iterator(); var6.hasNext(); nms.DataWatcher_REGISTER.invoke(nmsWatcher, position, item.value)) {
            item = (DataWatcherItem)var6.next();
            if (nms.minorVersion >= 9) {
               position = nms.newDataWatcherObject.newInstance(item.type.position, item.type.classType);
            } else {
               position = item.type.position;
            }
         }

         return nmsWatcher;
      } else {
         List<Object> items = new ArrayList();
         Iterator var4 = this.dataValues.values().iterator();

         while(var4.hasNext()) {
            DataWatcherItem item = (DataWatcherItem)var4.next();
            items.add(nms.newDataWatcherItem.newInstance(item.type.position, item.type.classType, item.value));
         }

         return items;
      }
   }

   public static DataWatcher fromNMS(Object nmsWatcher) throws Exception {
      DataWatcher watcher = new DataWatcher();
      NMSStorage nms = NMSHook.nms;
      Object items;
      if (nms.is1_20_5OrAbove) {
         items = new ArrayList(Arrays.asList((Object[])nms.DataWatcherItems.get(nmsWatcher)));
      } else if (nms.is1_19_3OrAbove) {
         items = new ArrayList(((Int2ObjectMap)nms.DataWatcherItems.get(nmsWatcher)).values());
      } else {
         items = ProtocolVersion.SERVER_VERSION.getMinorVersion() == 17 ? (List)nmsWatcher.getClass().getMethod("getAll").invoke(nmsWatcher) : (List)nmsWatcher.getClass().getMethod("c").invoke(nmsWatcher);
      }

      Iterator var4 = ((List)items).iterator();

      while(var4.hasNext()) {
         Object watchableObject = var4.next();
         DataWatcherItem w = DataWatcherItem.fromNMS(watchableObject);
         watcher.setValue(w.type, w.value);
      }

      return watcher;
   }

   public static DataWatcher fromNMSPacket(Object nmsPacket) throws Exception {
      DataWatcher watcher = new DataWatcher();
      List<Object> items = new ArrayList((List)NMSHook.nms.PacketPlayOutEntityMetadata_LIST.get(nmsPacket));

      DataWatcherItem w;
      for(Iterator var3 = items.iterator(); var3.hasNext(); watcher.setValue(w.type, w.value)) {
         Object watchableObject = var3.next();
         if (NMSHook.nms.is1_19_3OrAbove) {
            w = DataWatcherItem.fromPacketNMS(watchableObject);
         } else {
            w = DataWatcherItem.fromNMS(watchableObject);
         }
      }

      return watcher;
   }
}

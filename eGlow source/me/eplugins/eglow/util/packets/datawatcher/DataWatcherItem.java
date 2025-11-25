package me.eplugins.eglow.util.packets.datawatcher;

import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.packets.nms.NMSStorage;

public class DataWatcherItem {
   public final DataWatcherObject type;
   public final Object value;

   public DataWatcherItem(DataWatcherObject type, Object value) {
      this.type = type;
      this.value = value;
   }

   public static DataWatcherItem fromNMS(Object nmsItem) throws Exception {
      NMSStorage nms = NMSHook.nms;
      if (nms.minorVersion >= 9) {
         Object nmsObject = nms.DataWatcherItem_TYPE.get(nmsItem);
         return new DataWatcherItem(new DataWatcherObject(nms.DataWatcherObject_SLOT.getInt(nmsObject), nms.DataWatcherObject_SERIALIZER.get(nmsObject)), nms.DataWatcherItem_VALUE.get(nmsItem));
      } else {
         return new DataWatcherItem(new DataWatcherObject(nms.DataWatcherItem_TYPE.getInt(nmsItem), (Object)null), nms.DataWatcherItem_VALUE.get(nmsItem));
      }
   }

   public static DataWatcherItem fromPacketNMS(Object nmsItem) throws Exception {
      NMSStorage nms = NMSHook.nms;
      return nms.minorVersion >= 9 ? new DataWatcherItem(new DataWatcherObject((Integer)nms.DataWatcherB_INT.invoke(nmsItem), nms.DataWatcherB_Serializer.invoke(nmsItem)), nms.DataWatcherB_VALUE.invoke(nmsItem)) : new DataWatcherItem(new DataWatcherObject(nms.DataWatcherItem_TYPE.getInt(nmsItem), (Object)null), nms.DataWatcherItem_VALUE.get(nmsItem));
   }
}

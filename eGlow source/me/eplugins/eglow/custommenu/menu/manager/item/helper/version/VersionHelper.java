package me.eplugins.eglow.custommenu.menu.manager.item.helper.version;

import lombok.Generated;
import me.eplugins.eglow.util.DebugUtil;

public class VersionHelper {
   private static ItemHelper1_14 itemHelper1_14;
   private static ItemHelper1_20 itemHelper1_20;
   private static MetaHelper1_21_4 metaHelper1_21_4;

   public static void initialize() {
      if (DebugUtil.getMainVersion() >= 15) {
         itemHelper1_14 = new ItemHelper1_14();
      }

      if (DebugUtil.getMainVersion() >= 20) {
         itemHelper1_20 = new ItemHelper1_20();
      }

      if (isAtLeast(21, 4)) {
         metaHelper1_21_4 = new MetaHelper1_21_4();
      }

   }

   public static boolean isAtLeast(int major, int minor) {
      return DebugUtil.getMainVersion() > major || DebugUtil.getMainVersion() == major && DebugUtil.getMinorVersion() >= minor;
   }

   @Generated
   public static ItemHelper1_14 getItemHelper1_14() {
      return itemHelper1_14;
   }

   @Generated
   public static ItemHelper1_20 getItemHelper1_20() {
      return itemHelper1_20;
   }

   @Generated
   public static MetaHelper1_21_4 getMetaHelper1_21_4() {
      return metaHelper1_21_4;
   }
}

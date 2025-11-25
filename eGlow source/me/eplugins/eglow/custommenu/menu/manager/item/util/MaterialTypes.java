package me.eplugins.eglow.custommenu.menu.manager.item.util;

import java.util.Arrays;

public class MaterialTypes {
   public static enum PlaceholderItemType {
      MAIN_HAND,
      OFF_HAND,
      ARMOR_HELMET,
      ARMOR_CHESTPLATE,
      ARMOR_LEGGINGS,
      ARMOR_BOOTS,
      WATER_BOTTLE;

      public static MaterialTypes.PlaceholderItemType fromString(String key) {
         return (MaterialTypes.PlaceholderItemType)Arrays.stream(values()).filter((type) -> {
            return type.name().equalsIgnoreCase(key);
         }).findFirst().orElse((Object)null);
      }

      // $FF: synthetic method
      private static MaterialTypes.PlaceholderItemType[] $values() {
         return new MaterialTypes.PlaceholderItemType[]{MAIN_HAND, OFF_HAND, ARMOR_HELMET, ARMOR_CHESTPLATE, ARMOR_LEGGINGS, ARMOR_BOOTS, WATER_BOTTLE};
      }
   }

   public static enum CustomItemType {
      HEAD,
      BASEHEAD,
      TEXTURE,
      HDB,
      ITEMSADDER,
      ORAXEN,
      PLACEHOLDER;

      public static MaterialTypes.CustomItemType fromString(String key) {
         return (MaterialTypes.CustomItemType)Arrays.stream(values()).filter((type) -> {
            return type.name().equalsIgnoreCase(key);
         }).findFirst().orElse((Object)null);
      }

      // $FF: synthetic method
      private static MaterialTypes.CustomItemType[] $values() {
         return new MaterialTypes.CustomItemType[]{HEAD, BASEHEAD, TEXTURE, HDB, ITEMSADDER, ORAXEN, PLACEHOLDER};
      }
   }
}

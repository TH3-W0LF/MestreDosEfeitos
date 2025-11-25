package xshyo.us.theglow.libs.guis.components.util;

import xshyo.us.theglow.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class Legacy {
   public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();

   private Legacy() {
      throw new UnsupportedOperationException("Class should not be instantiated!");
   }
}

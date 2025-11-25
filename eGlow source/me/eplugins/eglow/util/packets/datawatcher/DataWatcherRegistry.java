package me.eplugins.eglow.util.packets.datawatcher;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.packets.nms.NMSStorage;
import me.eplugins.eglow.util.text.ChatUtil;

public class DataWatcherRegistry {
   public final Object Byte;
   public final Object Integer;
   public final Object Float;
   public final Object String;
   public Object Optional_IChatBaseComponent;
   public Object Boolean;

   public DataWatcherRegistry(NMSStorage nms) {
      if (ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 9) {
         Map<String, Object> fields = this.getStaticFields(nms.DataWatcherRegistry);
         if (fields.containsKey("a")) {
            this.Byte = fields.get("a");
            this.Integer = fields.get("b");
            this.Float = fields.get("c");
            this.String = fields.get("d");
         } else {
            this.Byte = fields.get("BYTE");
            this.Integer = fields.get("INT");
            this.Float = fields.get("FLOAT");
            this.String = fields.get("STRING");
         }

         if (ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 13) {
            if (fields.containsKey("f")) {
               this.Optional_IChatBaseComponent = fields.get("f");
               this.Boolean = fields.get("i");
            } else {
               this.Optional_IChatBaseComponent = fields.get("COMPONENT");
               this.Boolean = fields.get("BOOLEAN");
            }
         } else {
            this.Boolean = fields.get("h");
         }
      } else {
         this.Byte = 0;
         this.Integer = 2;
         this.Float = 3;
         this.String = 4;
      }

   }

   private Map<String, Object> getStaticFields(Class<?> clazz) {
      Map<String, Object> fields = new HashMap();
      if (clazz == null) {
         return fields;
      } else {
         Field[] var3 = clazz.getDeclaredFields();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Field field = var3[var5];
            field.setAccessible(true);
            if (Modifier.isStatic(field.getModifiers())) {
               try {
                  fields.put(field.getName(), field.get((Object)null));
               } catch (Exception var8) {
                  ChatUtil.printException("Failed to get DataWatcherRegistry fields", var8);
               }
            }
         }

         return fields;
      }
   }
}

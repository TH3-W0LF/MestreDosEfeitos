package me.eplugins.eglow.util.packets.chat;

import java.util.Iterator;
import java.util.List;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.text.ChatUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DeserializedChatComponent extends IChatBaseComponent {
   private final String json;
   private boolean deserialized;
   private boolean modified;

   public DeserializedChatComponent(String json) {
      Preconditions.checkNotNull(json, "json");
      this.json = json;
   }

   public String toString() {
      return this.modified ? super.toString() : this.json;
   }

   public String toString(ProtocolVersion clientVersion) {
      return this.modified ? super.toString(clientVersion) : this.json;
   }

   public List<IChatBaseComponent> getExtra() {
      if (!this.deserialized) {
         this.deserialize();
      }

      return super.getExtra();
   }

   public String getText() {
      if (!this.deserialized) {
         this.deserialize();
      }

      return super.getText();
   }

   public ChatModifier getModifier() {
      if (!this.deserialized) {
         this.deserialize();
      }

      return super.getModifier();
   }

   public IChatBaseComponent setExtra(List<IChatBaseComponent> components) {
      if (!this.deserialized) {
         this.deserialize();
      }

      this.modified = true;
      return super.setExtra(components);
   }

   public void addExtra(IChatBaseComponent child) {
      if (!this.deserialized) {
         this.deserialize();
      }

      this.modified = true;
      super.addExtra(child);
   }

   public void setModifier(ChatModifier modifier) {
      if (!this.deserialized) {
         this.deserialize();
      }

      this.modified = true;
      super.setModifier(modifier);
   }

   void deserialize() {
      this.deserialized = true;
      if (this.json.startsWith("\"") && this.json.endsWith("\"") && this.json.length() > 1) {
         this.setText(this.json.substring(1, this.json.length() - 1));
      } else {
         JSONObject jsonObject;
         try {
            jsonObject = (JSONObject)(new JSONParser()).parse(this.json);
         } catch (ParseException var6) {
            ChatUtil.printException("Failed to deserialize IChatBaseComponent", var6);
            return;
         }

         this.setText((String)jsonObject.get("text"));
         this.getModifier().setBold(getBoolean(jsonObject, "bold"));
         this.getModifier().setItalic(getBoolean(jsonObject, "italic"));
         this.getModifier().setUnderlined(getBoolean(jsonObject, "underlined"));
         this.getModifier().setStrikethrough(getBoolean(jsonObject, "strikethrough"));
         this.getModifier().setObfuscated(getBoolean(jsonObject, "obfuscated"));
         this.getModifier().setColor(TextColor.fromString((String)jsonObject.get("color")));
         if (jsonObject.containsKey("extra")) {
            List<Object> list = (List)jsonObject.get("extra");

            String string;
            for(Iterator var3 = list.iterator(); var3.hasNext(); this.addExtra(IChatBaseComponent.deserialize(string))) {
               Object extra = var3.next();
               string = extra.toString();
               if (!string.startsWith("{")) {
                  string = "\"" + string + "\"";
               }
            }
         }

      }
   }

   private static Boolean getBoolean(JSONObject jsonObject, String key) {
      Preconditions.checkNotNull(jsonObject, "json object");
      Preconditions.checkNotNull(key, "key");
      String value = String.valueOf(jsonObject.getOrDefault(key, (Object)null));
      return "null".equals(value) ? null : Boolean.parseBoolean(value);
   }
}

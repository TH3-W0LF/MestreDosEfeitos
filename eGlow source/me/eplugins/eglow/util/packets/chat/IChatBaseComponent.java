package me.eplugins.eglow.util.packets.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.packets.chat.rgb.RGBUtils;
import org.json.simple.JSONObject;

public class IChatBaseComponent {
   private static final Map<String, IChatBaseComponent> componentCache = new HashMap();
   private static final Map<IChatBaseComponent, String> serializeCacheModern = new HashMap();
   private static final Map<IChatBaseComponent, String> serializeCacheLegacy = new HashMap();
   private String text;
   private ChatModifier modifier = new ChatModifier();
   private List<IChatBaseComponent> extra;

   public IChatBaseComponent() {
   }

   public IChatBaseComponent(IChatBaseComponent component) {
      Preconditions.checkNotNull(component, "component");
      this.text = component.text;
      this.modifier = new ChatModifier(component.modifier);
      Iterator var2 = component.getExtra().iterator();

      while(var2.hasNext()) {
         IChatBaseComponent child = (IChatBaseComponent)var2.next();
         this.addExtra(new IChatBaseComponent(child));
      }

   }

   public IChatBaseComponent(String text) {
      this.text = text;
   }

   public List<IChatBaseComponent> getExtra() {
      return this.extra == null ? Collections.emptyList() : this.extra;
   }

   public IChatBaseComponent setExtra(List<IChatBaseComponent> components) {
      if (components.isEmpty()) {
         throw new IllegalArgumentException("Unexpected empty array of components");
      } else {
         this.extra = components;
         return this;
      }
   }

   public void addExtra(IChatBaseComponent child) {
      Preconditions.checkNotNull(child, "extra");
      if (this.extra == null) {
         this.extra = new ArrayList();
      }

      this.extra.add(child);
   }

   public void setModifier(ChatModifier modifier) {
      Preconditions.checkNotNull(modifier, "modifier");
      this.modifier = modifier;
   }

   public static IChatBaseComponent deserialize(String json) {
      return json == null ? null : new DeserializedChatComponent(json);
   }

   public String toString() {
      JSONObject json = new JSONObject();
      if (this.text != null) {
         json.put("text", this.text);
      }

      if (this.modifier.getTargetVersion() == null) {
         this.modifier.setTargetVersion(ProtocolVersion.SERVER_VERSION);
      }

      json.putAll(this.modifier.serialize());
      if (this.extra != null) {
         json.put("extra", this.extra);
      }

      return json.toString();
   }

   public String toString(ProtocolVersion clientVersion) {
      return this.toString(clientVersion, false);
   }

   public String toString(ProtocolVersion clientVersion, boolean sendTranslatableIfEmpty) {
      if (this.extra == null) {
         if (this.text == null) {
            return null;
         }

         if (this.text.isEmpty()) {
            if (sendTranslatableIfEmpty) {
               return "{\"translate\":\"\"}";
            }

            return "{\"text\":\"\"}";
         }
      }

      this.modifier.setTargetVersion(clientVersion);
      Iterator var3 = this.getExtra().iterator();

      while(var3.hasNext()) {
         IChatBaseComponent child = (IChatBaseComponent)var3.next();
         child.modifier.setTargetVersion(clientVersion);
      }

      String string;
      if (clientVersion.getMinorVersion() >= 16) {
         if (serializeCacheModern.containsKey(this)) {
            return (String)serializeCacheModern.get(this);
         }

         string = this.toString();
         if (serializeCacheModern.size() > 10000) {
            serializeCacheModern.clear();
         }

         serializeCacheModern.put(this, string);
      } else {
         if (serializeCacheLegacy.containsKey(this)) {
            return (String)serializeCacheLegacy.get(this);
         }

         string = this.toString();
         if (serializeCacheLegacy.size() > 10000) {
            serializeCacheLegacy.clear();
         }

         serializeCacheLegacy.put(this, string);
      }

      return string;
   }

   public static IChatBaseComponent fromColoredText(String originalText) {
      Preconditions.checkNotNull(originalText, "text");
      String text = RGBUtils.getInstance().applyFormats(EnumChatFormat.color(originalText));
      List<IChatBaseComponent> components = new ArrayList();
      StringBuilder builder = new StringBuilder();
      IChatBaseComponent component = new IChatBaseComponent();

      for(int i = 0; i < text.length(); ++i) {
         char c = text.charAt(i);
         if (c == 167) {
            ++i;
            if (i >= text.length()) {
               break;
            }

            c = text.charAt(i);
            if (c >= 'A' && c <= 'Z') {
               c = (char)(c + 32);
            }

            EnumChatFormat format = EnumChatFormat.getByChar(c);
            if (format != null) {
               if (builder.length() > 0) {
                  component.setText(builder.toString());
                  components.add(component);
                  component = new IChatBaseComponent(component);
                  component.text = null;
                  builder = new StringBuilder();
               }

               switch(format) {
               case BOLD:
                  component.modifier.setBold(true);
                  break;
               case ITALIC:
                  component.modifier.setItalic(true);
                  break;
               case UNDERLINE:
                  component.modifier.setUnderlined(true);
                  break;
               case STRIKETHROUGH:
                  component.modifier.setStrikethrough(true);
                  break;
               case OBFUSCATED:
                  component.modifier.setObfuscated(true);
                  break;
               case RESET:
                  component = new IChatBaseComponent();
                  component.modifier.setColor(new TextColor(EnumChatFormat.WHITE));
                  break;
               default:
                  component = new IChatBaseComponent();
                  component.modifier.setColor(new TextColor(format));
               }
            }
         } else if (c == '#' && text.length() > i + 6) {
            String hex = text.substring(i + 1, i + 7);
            if (RGBUtils.getInstance().isHexCode(hex)) {
               TextColor color;
               if (containsLegacyCode(text, i)) {
                  color = new TextColor(hex, EnumChatFormat.getByChar(text.charAt(i + 8)));
                  i += 8;
               } else {
                  color = new TextColor(hex);
                  i += 6;
               }

               if (builder.length() > 0) {
                  component.setText(builder.toString());
                  components.add(component);
                  builder = new StringBuilder();
               }

               component = new IChatBaseComponent();
               component.modifier.setColor(color);
            } else {
               builder.append(c);
            }
         } else {
            builder.append(c);
         }
      }

      component.setText(builder.toString());
      components.add(component);
      return (new IChatBaseComponent("")).setExtra(components);
   }

   private static boolean containsLegacyCode(String text, int i) {
      if (text.length() - i >= 9 && text.charAt(i + 7) == '|') {
         return EnumChatFormat.getByChar(text.charAt(i + 8)) != null;
      } else {
         return false;
      }
   }

   public static IChatBaseComponent optimizedComponent(String text) {
      if (text == null) {
         return null;
      } else if (componentCache.containsKey(text)) {
         return (IChatBaseComponent)componentCache.get(text);
      } else {
         IChatBaseComponent component;
         if (!text.contains("#") && !text.contains("&x") && !text.contains("§x")) {
            component = new IChatBaseComponent(text);
         } else {
            component = fromColoredText(text);
         }

         if (componentCache.size() > 10000) {
            componentCache.clear();
         }

         componentCache.put(text, component);
         return component;
      }
   }

   @Generated
   public void setText(String text) {
      this.text = text;
   }

   @Generated
   public String getText() {
      return this.text;
   }

   @Generated
   public ChatModifier getModifier() {
      return this.modifier;
   }
}

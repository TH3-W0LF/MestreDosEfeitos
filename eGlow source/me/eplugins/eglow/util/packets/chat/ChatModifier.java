package me.eplugins.eglow.util.packets.chat;

import lombok.Generated;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import org.json.simple.JSONObject;

public class ChatModifier {
   private TextColor color;
   private Boolean bold;
   private Boolean italic;
   private Boolean underlined;
   private Boolean strikethrough;
   private Boolean obfuscated;
   private String font;
   private ProtocolVersion targetVersion;

   public ChatModifier() {
   }

   public ChatModifier(ChatModifier modifier) {
      Preconditions.checkNotNull(modifier, "modifier");
      this.color = modifier.color == null ? null : new TextColor(modifier.color);
      this.bold = modifier.bold;
      this.italic = modifier.italic;
      this.underlined = modifier.underlined;
      this.strikethrough = modifier.strikethrough;
      this.obfuscated = modifier.obfuscated;
      this.font = modifier.font;
      this.targetVersion = modifier.targetVersion;
   }

   public JSONObject serialize() {
      JSONObject json = new JSONObject();
      if (this.color != null) {
         json.put("color", this.targetVersion.getMinorVersion() >= 16 ? this.color.toString() : this.color.getLegacyColor().toString().toLowerCase());
      }

      if (this.bold != null) {
         json.put("bold", this.bold);
      }

      if (this.italic != null) {
         json.put("italic", this.italic);
      }

      if (this.underlined != null) {
         json.put("underlined", this.underlined);
      }

      if (this.strikethrough != null) {
         json.put("strikethrough", this.strikethrough);
      }

      if (this.obfuscated != null) {
         json.put("obfuscated", this.obfuscated);
      }

      if (this.font != null) {
         json.put("font", this.font);
      }

      return json;
   }

   @Generated
   public void setColor(TextColor color) {
      this.color = color;
   }

   @Generated
   public TextColor getColor() {
      return this.color;
   }

   @Generated
   public void setBold(Boolean bold) {
      this.bold = bold;
   }

   @Generated
   public void setItalic(Boolean italic) {
      this.italic = italic;
   }

   @Generated
   public void setUnderlined(Boolean underlined) {
      this.underlined = underlined;
   }

   @Generated
   public void setStrikethrough(Boolean strikethrough) {
      this.strikethrough = strikethrough;
   }

   @Generated
   public void setObfuscated(Boolean obfuscated) {
      this.obfuscated = obfuscated;
   }

   @Generated
   public ProtocolVersion getTargetVersion() {
      return this.targetVersion;
   }

   @Generated
   public void setTargetVersion(ProtocolVersion targetVersion) {
      this.targetVersion = targetVersion;
   }
}

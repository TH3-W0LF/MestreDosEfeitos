package me.eplugins.eglow.util.enums;

import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public enum Dependency {
   VIA_VERSION("ViaVersion"),
   PROTOCOL_SUPPORT("ProtocolSupport"),
   TAB_BRIDGE("TAB-Bridge"),
   TAB("TAB"),
   PLACEHOLDER_API("PlaceholderAPI"),
   VAULT("Vault"),
   CITIZENS("Citizens"),
   GSIT("GSit"),
   LUCKPERMS("LuckPerms"),
   HEADDATABASE("HeadDatabase"),
   ITEMSADDER("ItemsAdder"),
   ORAXEN("Oraxen");

   private final String pluginName;

   public boolean isLoaded() {
      Plugin plugin = Bukkit.getPluginManager().getPlugin(this.pluginName);
      return plugin != null && plugin.isEnabled();
   }

   public Plugin getPlugin() {
      return Bukkit.getPluginManager().getPlugin(this.pluginName);
   }

   public String getVersion() {
      Plugin plugin = Bukkit.getPluginManager().getPlugin(this.pluginName);
      return plugin == null ? "0" : plugin.getDescription().getVersion();
   }

   @Generated
   public String getPluginName() {
      return this.pluginName;
   }

   @Generated
   private Dependency(String pluginName) {
      this.pluginName = pluginName;
   }

   // $FF: synthetic method
   private static Dependency[] $values() {
      return new Dependency[]{VIA_VERSION, PROTOCOL_SUPPORT, TAB_BRIDGE, TAB, PLACEHOLDER_API, VAULT, CITIZENS, GSIT, LUCKPERMS, HEADDATABASE, ITEMSADDER, ORAXEN};
   }
}

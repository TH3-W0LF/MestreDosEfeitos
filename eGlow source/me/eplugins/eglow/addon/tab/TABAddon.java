package me.eplugins.eglow.addon.tab;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.addon.AbstractAddonBase;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.DebugUtil;
import me.eplugins.eglow.util.enums.Dependency;
import me.eplugins.eglow.util.packets.chat.EnumChatFormat;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.event.EventBus;
import me.neznamy.tab.api.event.plugin.TabLoadEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TABAddon extends AbstractAddonBase {
   private final boolean versionSupported;
   private boolean settingNametagPrefixSuffixEnabled = false;
   private boolean settingTeamPacketBlockingEnabled = false;

   public TABAddon(EGlow eGlowInstance) {
      super(eGlowInstance);
      Plugin tabPlugin = Dependency.TAB.getPlugin();
      if (tabPlugin != null) {
         if (!tabPlugin.getClass().getName().startsWith("me.neznamy.tab")) {
            this.versionSupported = false;
         } else {
            int tabVersion = Integer.parseInt(tabPlugin.getDescription().getVersion().replaceAll("[^\\d]", ""));
            this.versionSupported = tabVersion >= 522;
            if (!this.isVersionSupported()) {
               ChatUtil.sendToConsole("&cWarning&f! &cThis version of eGlow requires a higher TAB version&f!", true);
            } else {
               this.loadTABSettings();
               this.getEGlowInstance().getServer().getPluginManager().registerEvents(new TABAddonEvents(), this.getEGlowInstance());
               ((EventBus)Objects.requireNonNull(TabAPI.getInstance().getEventBus())).register(TabLoadEvent.class, (event) -> {
                  NMSHook.scheduleTask(true, () -> {
                     try {
                        TABAddon tabAddon = EGlow.getInstance().getTabAddon();
                        tabAddon.loadTABSettings();
                        if (tabAddon.blockEGlowPackets()) {
                           Iterator var1 = DataManager.getEGlowPlayers().iterator();

                           while(var1.hasNext()) {
                              EGlowPlayer ePlayer = (EGlowPlayer)var1.next();
                              if (ePlayer.isGlowing()) {
                                 tabAddon.updateTABPlayer(ePlayer, ePlayer.getActiveColor());
                              }
                           }
                        }
                     } catch (Exception var3) {
                        ChatUtil.printException("Failed to modify player data in TAB through API", var3);
                     }

                  });
               });
            }
         }
      } else {
         this.versionSupported = false;
         if (DebugUtil.onBungee() || DebugUtil.onVelocity()) {
            this.getEGlowInstance().getServer().getPluginManager().registerEvents(new TABAddonEvents(), this.getEGlowInstance());
         }

      }
   }

   public void loadTABSettings() {
      try {
         Plugin tabPlugin = Dependency.TAB.getPlugin();
         int tabVersion = Integer.parseInt(tabPlugin.getDescription().getVersion().replaceAll("[^\\d]", ""));
         List<String> lines = Files.readAllLines(Paths.get(Dependency.TAB.getPlugin().getDataFolder() + "/config.yml"));
         boolean scoreboardTeamsSection = false;
         if (tabVersion >= 522) {
            this.settingTeamPacketBlockingEnabled = true;
         }

         Iterator var5 = lines.iterator();

         while(var5.hasNext()) {
            String line = (String)var5.next();
            if (scoreboardTeamsSection) {
               if (line.contains("enabled:")) {
                  this.settingNametagPrefixSuffixEnabled = Boolean.parseBoolean(line.replace("enabled:", "").replace(" ", ""));
                  return;
               }
            } else if (line.contains("scoreboard-teams:")) {
               scoreboardTeamsSection = true;
            }
         }
      } catch (IOException var7) {
         ChatUtil.printException("Failed to load TAB config", var7);
      }

   }

   public void updateTABPlayer(EGlowPlayer eGlowPlayer, ChatColor glowColor) {
      if (this.isVersionSupported() && this.blockEGlowPackets() && !EGlowMainConfig.MainConfig.ADVANCED_FORCE_DISABLE_TAB_INTEGRATION.getBoolean()) {
         TabPlayer tabPlayer = this.getTABPlayer(eGlowPlayer.getUuid());
         if (tabPlayer != null && TabAPI.getInstance().getNameTagManager() != null) {
            String color = glowColor.equals(ChatColor.RESET) ? "" : String.valueOf(glowColor);

            String tagPrefix;
            try {
               tagPrefix = TabAPI.getInstance().getNameTagManager().getOriginalPrefix(tabPlayer);
            } catch (Exception var11) {
               tagPrefix = color;
            }

            if (tagPrefix.contains("<")) {
               EnumChatFormat[] var6 = EnumChatFormat.values();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  EnumChatFormat colorName = var6[var8];
                  if (tagPrefix.toLowerCase().contains("<" + colorName.toString().toLowerCase())) {
                     color = "<" + glowColor.name().toLowerCase() + ">";
                     break;
                  }
               }
            }

            try {
               TabAPI.getInstance().getNameTagManager().setPrefix(tabPlayer, tagPrefix + color);
            } catch (NullPointerException | IllegalStateException var10) {
            }

         }
      }
   }

   public void requestTABPlayerUpdate(Player player) {
      NMSHook.scheduleTask(true, () -> {
         try {
            TABAddon tabAddon = this.getEGlowInstance().getTabAddon();
            EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
            if (eGlowPlayer == null) {
               return;
            }

            if (tabAddon.isVersionSupported() && tabAddon.blockEGlowPackets()) {
               tabAddon.updateTABPlayer(eGlowPlayer, eGlowPlayer.getActiveColor());
            } else if (DebugUtil.onBungee() || DebugUtil.onVelocity()) {
               DataManager.TABProxyUpdateRequest(player, eGlowPlayer.getActiveColor().name().replace("ı", "i"));
            }
         } catch (ConcurrentModificationException var4) {
         } catch (Exception var5) {
            ChatUtil.printException("Failed to modify player data in TAB through API", var5);
         }

      });
   }

   private TabPlayer getTABPlayer(UUID uuid) {
      return TabAPI.getInstance().getPlayer(uuid);
   }

   public boolean blockEGlowPackets() {
      return this.isSettingNametagPrefixSuffixEnabled() && this.isSettingTeamPacketBlockingEnabled();
   }

   @Generated
   public boolean isVersionSupported() {
      return this.versionSupported;
   }

   @Generated
   public boolean isSettingNametagPrefixSuffixEnabled() {
      return this.settingNametagPrefixSuffixEnabled;
   }

   @Generated
   public boolean isSettingTeamPacketBlockingEnabled() {
      return this.settingTeamPacketBlockingEnabled;
   }
}

package me.eplugins.eglow.addon.internal;

import java.util.Iterator;
import lombok.Generated;
import me.clip.placeholderapi.PlaceholderAPI;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.addon.AbstractAddonBase;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.Dependency;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.scheduler.BukkitTask;

public class TablistFormattingAddon extends AbstractAddonBase {
   String format;
   Object bukkitTask;
   boolean usingPAPIPlaceholder;

   public TablistFormattingAddon(EGlow eGlowInstance) {
      super(eGlowInstance);
      this.format = EGlowMainConfig.MainConfig.FORMATTING_TABLIST_FORMAT.getString();
      if (EGlowMainConfig.MainConfig.FORMATTING_TABLIST_ENABLE.getBoolean()) {
         String stripped = this.getFormat().replaceAll("%prefix%|%name%|%suffix%", "");
         if (Dependency.PLACEHOLDER_API.isLoaded() && PlaceholderAPI.containsPlaceholders(stripped)) {
            this.usingPAPIPlaceholder = true;
            this.startTabListUpdater();
         }

      }
   }

   public void updateTablistFormat(EGlowPlayer ePlayer) {
      if (EGlowMainConfig.MainConfig.FORMATTING_TABLIST_ENABLE.getBoolean() && !this.usingPAPIPlaceholder) {
         if (ePlayer.getPlayer() != null) {
            String updatedFormat = this.getFormat();
            if (updatedFormat.contains("%name%")) {
               updatedFormat = updatedFormat.replace("%name%", ePlayer.getDisplayName());
            }

            if (updatedFormat.contains("%prefix%") || updatedFormat.contains("%suffix%")) {
               String prefix = EGlow.getInstance().getVaultAddon() != null ? EGlow.getInstance().getVaultAddon().getPlayerPrefix(ePlayer) : "";
               String suffix = EGlow.getInstance().getVaultAddon() != null ? EGlow.getInstance().getVaultAddon().getPlayerSuffix(ePlayer) : "";
               updatedFormat = updatedFormat.replace("%prefix%", prefix).replace("%suffix%", suffix);
            }

            if (Dependency.PLACEHOLDER_API.isLoaded()) {
               updatedFormat = PlaceholderAPI.setPlaceholders(ePlayer.getPlayer(), updatedFormat);
            }

            ePlayer.getPlayer().setPlayerListName(ChatUtil.translateColors(updatedFormat));
         }
      }
   }

   public void onReload() {
      this.format = EGlowMainConfig.MainConfig.FORMATTING_TABLIST_FORMAT.getString();
      String stripped = this.getFormat().replaceAll("%prefix%|%name%|%suffix%", "");
      if (!EGlowMainConfig.MainConfig.FORMATTING_TABLIST_ENABLE.getBoolean() && this.usingPAPIPlaceholder) {
         this.stopTabListUpdater();
      } else {
         if (Dependency.PLACEHOLDER_API.isLoaded()) {
            if (PlaceholderAPI.containsPlaceholders(stripped)) {
               if (this.usingPAPIPlaceholder) {
                  return;
               }

               this.usingPAPIPlaceholder = true;
               this.startTabListUpdater();
            } else {
               if (this.usingPAPIPlaceholder) {
                  this.stopTabListUpdater();
               }

               this.usingPAPIPlaceholder = false;
            }
         }

      }
   }

   public void startTabListUpdater() {
      this.bukkitTask = NMSHook.scheduleTimerTask(false, 1L, 45L, () -> {
         Iterator var1 = DataManager.getEGlowPlayers().iterator();

         while(var1.hasNext()) {
            EGlowPlayer ePlayer = (EGlowPlayer)var1.next();
            if (ePlayer.getPlayer() == null) {
               return;
            }

            String updatedFormat = this.getFormat();
            if (updatedFormat.contains("%name%")) {
               updatedFormat = updatedFormat.replace("%name%", ePlayer.getDisplayName());
            }

            if (updatedFormat.contains("%prefix%") || updatedFormat.contains("%suffix%")) {
               String prefix = EGlow.getInstance().getVaultAddon() != null ? EGlow.getInstance().getVaultAddon().getPlayerPrefix(ePlayer) : "";
               String suffix = EGlow.getInstance().getVaultAddon() != null ? EGlow.getInstance().getVaultAddon().getPlayerSuffix(ePlayer) : "";
               updatedFormat = updatedFormat.replace("%prefix%", prefix).replace("%suffix%", suffix);
            }

            updatedFormat = PlaceholderAPI.setPlaceholders(ePlayer.getPlayer(), updatedFormat);
            ePlayer.getPlayer().setPlayerListName(ChatUtil.translateColors(updatedFormat));
         }

      });
   }

   public void stopTabListUpdater() {
      if (this.getBukkitTask() != null) {
         if (this.getBukkitTask() instanceof BukkitTask) {
            BukkitTask task = (BukkitTask)this.getBukkitTask();
            task.cancel();
         } else {
            try {
               NMSHook.nms.cancelScheduledTask.invoke(this.getBukkitTask());
            } catch (Exception var2) {
               ChatUtil.printException("Failed to cancel Tablist updater.", var2);
            }
         }
      }

   }

   @Generated
   public String getFormat() {
      return this.format;
   }

   @Generated
   public Object getBukkitTask() {
      return this.bukkitTask;
   }

   @Generated
   public boolean isUsingPAPIPlaceholder() {
      return this.usingPAPIPlaceholder;
   }
}

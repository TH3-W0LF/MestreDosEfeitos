package me.eplugins.eglow.addon;

import lombok.Generated;
import me.clip.placeholderapi.PlaceholderAPI;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.Dependency;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.text.ChatUtil;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultAddon extends AbstractAddonBase {
   private Chat chat;
   private Economy economy;
   private Permission permission;

   public VaultAddon(EGlow eGlowInstance) {
      super(eGlowInstance);
      RegisteredServiceProvider<Chat> chat_rsp = this.getEGlowInstance().getServer().getServicesManager().getRegistration(Chat.class);
      RegisteredServiceProvider<Economy> economy_rsp = this.getEGlowInstance().getServer().getServicesManager().getRegistration(Economy.class);
      RegisteredServiceProvider<Permission> permission_rsp = this.getEGlowInstance().getServer().getServicesManager().getRegistration(Permission.class);
      if (chat_rsp != null) {
         this.chat = (Chat)chat_rsp.getProvider();
      }

      if (economy_rsp != null) {
         this.economy = (Economy)economy_rsp.getProvider();
      }

      if (permission_rsp != null) {
         this.permission = (Permission)permission_rsp.getProvider();
      }

   }

   public String getPlayerTagPrefix(EGlowPlayer eGlowPlayer) {
      if (!EGlowMainConfig.MainConfig.FORMATTING_TAGNAME_ENABLE.getBoolean()) {
         return "";
      } else {
         Player player = eGlowPlayer.getPlayer();
         String prefix = EGlowMainConfig.MainConfig.FORMATTING_TAGNAME_PREFIX.getString().replace("%prefix%", this.getPlayerPrefix(eGlowPlayer));
         if (Dependency.PLACEHOLDER_API.isLoaded()) {
            prefix = PlaceholderAPI.setPlaceholders(player, prefix);
         }

         if (prefix.length() > 14 && ProtocolVersion.SERVER_VERSION.getMinorVersion() <= 12) {
            prefix = prefix.substring(0, 14);
         }

         return !prefix.isEmpty() ? ChatUtil.translateColors(prefix) : prefix;
      }
   }

   public String getPlayerTagSuffix(EGlowPlayer eGlowPlayer) {
      if (!EGlowMainConfig.MainConfig.FORMATTING_TAGNAME_ENABLE.getBoolean()) {
         return "";
      } else {
         Player player = eGlowPlayer.getPlayer();
         String suffix = EGlowMainConfig.MainConfig.FORMATTING_TAGNAME_SUFFIX.getString();
         if (suffix.contains("%suffix%")) {
            suffix = suffix.replace("%suffix%", this.getPlayerSuffix(eGlowPlayer));
         }

         if (Dependency.PLACEHOLDER_API.isLoaded()) {
            suffix = PlaceholderAPI.setPlaceholders(player, suffix);
         }

         return !suffix.isEmpty() ? ChatUtil.translateColors(suffix) : "";
      }
   }

   public String getPlayerPrefix(EGlowPlayer eGlowPlayer) {
      if (EGlow.getInstance().getVaultAddon() != null && this.getChat() != null) {
         Player player = eGlowPlayer.getPlayer();
         String prefix = this.getChat().getPlayerPrefix(player);
         if (prefix != null && !prefix.isEmpty()) {
            return ProtocolVersion.SERVER_VERSION.getMinorVersion() <= 12 && prefix.length() > 14 ? (eGlowPlayer.getActiveColor().equals(ChatColor.RESET) ? (prefix.length() > 16 ? prefix.substring(0, 16) : prefix) : prefix.substring(0, 14) + eGlowPlayer.getActiveColor()) : prefix;
         } else {
            return "";
         }
      } else {
         return "";
      }
   }

   public String getPlayerSuffix(EGlowPlayer eGlowPlayer) {
      if (EGlow.getInstance().getVaultAddon() != null && this.getChat() != null) {
         Player player = eGlowPlayer.getPlayer();
         String suffix = this.getChat().getPlayerSuffix(player);
         if (suffix != null && !suffix.isEmpty()) {
            return ProtocolVersion.SERVER_VERSION.getMinorVersion() <= 12 && suffix.length() > 16 ? suffix.substring(0, 16) : suffix;
         } else {
            return "";
         }
      } else {
         return "";
      }
   }

   public boolean hasBalance(Player player, double amount) {
      if (EGlow.getInstance().getVaultAddon() != null && this.getEconomy() != null) {
         return this.getEconomy().hasAccount(player, player.getWorld().getName()) ? this.getEconomy().has(player, amount) : false;
      } else {
         return false;
      }
   }

   public void depositBalance(Player player, double amount) {
      if (EGlow.getInstance().getVaultAddon() != null && this.getEconomy() != null) {
         this.getEconomy().depositPlayer(player, player.getWorld().getName(), amount);
      }
   }

   public void withdrawBalance(Player player, double amount) {
      if (EGlow.getInstance().getVaultAddon() != null && this.getEconomy() != null) {
         this.getEconomy().withdrawPlayer(player, player.getWorld().getName(), amount);
      }
   }

   public boolean hasPermission(Player player, String permission) {
      return EGlow.getInstance().getVaultAddon() != null && this.getPermission() != null ? this.getPermission().has(player, permission) : false;
   }

   public void givePermission(Player player, String permission) {
      if (EGlow.getInstance().getVaultAddon() != null && this.getPermission() != null) {
         this.getPermission().playerAdd(player, permission);
      }
   }

   public void removePermission(Player player, String permission) {
      if (EGlow.getInstance().getVaultAddon() != null && this.getPermission() != null) {
         this.getPermission().playerRemove(player, permission);
      }
   }

   @Generated
   public Chat getChat() {
      return this.chat;
   }

   @Generated
   public Economy getEconomy() {
      return this.economy;
   }

   @Generated
   public Permission getPermission() {
      return this.permission;
   }
}

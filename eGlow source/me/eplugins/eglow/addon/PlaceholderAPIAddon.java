package me.eplugins.eglow.addon;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowEffect;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.packets.chat.ChatColor;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.entity.Player;

public class PlaceholderAPIAddon extends PlaceholderExpansion {
   public PlaceholderAPIAddon() {
      this.register();
   }

   public String getAuthor() {
      return EGlow.getInstance().getDescription().getAuthors().toString();
   }

   public String getVersion() {
      return EGlow.getInstance().getDescription().getVersion();
   }

   public String getIdentifier() {
      return "eglow";
   }

   public String getRequiredPlugin() {
      return "eGlow";
   }

   public boolean canRegister() {
      return EGlow.getInstance() != null;
   }

   public boolean register() {
      return !this.canRegister() ? false : super.register();
   }

   public boolean persist() {
      return true;
   }

   public String onPlaceholderRequest(Player player, String identifier) {
      if (player == null) {
         return "";
      } else {
         EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
         if (eGlowPlayer == null) {
            return "";
         } else {
            String var4 = identifier.toLowerCase();
            byte var5 = -1;
            switch(var4.hashCode()) {
            case -1893502957:
               if (var4.equals("glowstatus_join_raw")) {
                  var5 = 14;
               }
               break;
            case -1692304247:
               if (var4.equals("glowcolor_mm")) {
                  var5 = 2;
               }
               break;
            case -1506231196:
               if (var4.equals("client_version")) {
                  var5 = 0;
               }
               break;
            case -1458844381:
               if (var4.equals("lastglow")) {
                  var5 = 6;
               }
               break;
            case -708103644:
               if (var4.equals("glow_speed_type")) {
                  var5 = 10;
               }
               break;
            case -414446258:
               if (var4.equals("glow_visibility_next")) {
                  var5 = 20;
               }
               break;
            case -414436973:
               if (var4.equals("glow_visibility_none")) {
                  var5 = 19;
               }
               break;
            case -151928858:
               if (var4.equals("glow_visibility_all")) {
                  var5 = 16;
               }
               break;
            case -151915061:
               if (var4.equals("glow_visibility_own")) {
                  var5 = 18;
               }
               break;
            case 38422549:
               if (var4.equals("glow_visibility_other")) {
                  var5 = 17;
               }
               break;
            case 447092648:
               if (var4.equals("glowstatus_raw")) {
                  var5 = 12;
               }
               break;
            case 548977429:
               if (var4.equals("glow_speed")) {
                  var5 = 8;
               }
               break;
            case 600914180:
               if (var4.equals("glow_visibility")) {
                  var5 = 15;
               }
               break;
            case 974745002:
               if (var4.equals("glowstatus_join")) {
                  var5 = 13;
               }
               break;
            case 991111670:
               if (var4.equals("glowcolor")) {
                  var5 = 1;
               }
               break;
            case 1122052031:
               if (var4.equals("glowstatus")) {
                  var5 = 11;
               }
               break;
            case 1362628606:
               if (var4.equals("glow_speed_raw")) {
                  var5 = 9;
               }
               break;
            case 1505290108:
               if (var4.equals("activeglow_raw")) {
                  var5 = 5;
               }
               break;
            case 1981372729:
               if (var4.equals("colorchar")) {
                  var5 = 3;
               }
               break;
            case 1987537676:
               if (var4.equals("lastglow_raw")) {
                  var5 = 7;
               }
               break;
            case 2044526995:
               if (var4.equals("activeglow")) {
                  var5 = 4;
               }
            }

            switch(var5) {
            case 0:
               return eGlowPlayer.getVersion().getFriendlyName();
            case 1:
               return eGlowPlayer.getGlowStatus() && !eGlowPlayer.isFakeGlowStatus() ? eGlowPlayer.getActiveColor().toString() : "";
            case 2:
               return eGlowPlayer.getGlowStatus() && !eGlowPlayer.isFakeGlowStatus() ? "<" + eGlowPlayer.getActiveColor().name().toLowerCase() + ">" : "";
            case 3:
               return eGlowPlayer.getGlowStatus() && !eGlowPlayer.isFakeGlowStatus() ? String.valueOf(eGlowPlayer.getActiveColor().getChar()) : "r";
            case 4:
               return eGlowPlayer.isGlowing() ? ChatUtil.getEffectChatName(eGlowPlayer) : EGlowMessageConfig.Message.COLOR.get("none");
            case 5:
               return eGlowPlayer.isGlowing() ? this.setToBasicName(ChatUtil.getEffectChatName(eGlowPlayer)) : this.setToBasicName(EGlowMessageConfig.Message.COLOR.get("none"));
            case 6:
               return eGlowPlayer.getLastGlowName();
            case 7:
               return this.setToBasicName(eGlowPlayer.getLastGlow());
            case 8:
               return this.getSpeedFromEffect(eGlowPlayer.getGlowEffect(), false);
            case 9:
               return this.getSpeedFromEffect(eGlowPlayer.getGlowEffect(), true);
            case 10:
               String effectName = eGlowPlayer.getGlowEffect().getName();
               if (effectName.startsWith("blink")) {
                  return "blink";
               } else {
                  if (effectName.startsWith("rainbow")) {
                     return "rainbow";
                  }

                  return "";
               }
            case 11:
               return eGlowPlayer.getGlowStatus() && !eGlowPlayer.isFakeGlowStatus() ? EGlowMessageConfig.Message.GUI_YES.get() : EGlowMessageConfig.Message.GUI_NO.get();
            case 12:
               return eGlowPlayer.getGlowStatus() && !eGlowPlayer.isFakeGlowStatus() ? "true" : "false";
            case 13:
               return eGlowPlayer.isGlowOnJoin() ? EGlowMessageConfig.Message.GUI_YES.get() : EGlowMessageConfig.Message.GUI_NO.get();
            case 14:
               return eGlowPlayer.isGlowOnJoin() ? "true" : "false";
            case 15:
               return eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT) ? EGlowMessageConfig.Message.VISIBILITY_UNSUPPORTED.get() : EGlowMessageConfig.Message.valueOf("VISIBILITY_" + eGlowPlayer.getGlowVisibility()).get();
            case 16:
               return (eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.ALL) ? EGlowMessageConfig.Message.GLOW_VISIBILITY_INDICATOR.get() : "") + EGlowMessageConfig.Message.VISIBILITY_ALL.get();
            case 17:
               return (eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.OTHER) ? EGlowMessageConfig.Message.GLOW_VISIBILITY_INDICATOR.get() : "") + EGlowMessageConfig.Message.VISIBILITY_OTHER.get();
            case 18:
               return (eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.OWN) ? EGlowMessageConfig.Message.GLOW_VISIBILITY_INDICATOR.get() : "") + EGlowMessageConfig.Message.VISIBILITY_OWN.get();
            case 19:
               return (eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.NONE) ? EGlowMessageConfig.Message.GLOW_VISIBILITY_INDICATOR.get() : "") + EGlowMessageConfig.Message.VISIBILITY_NONE.get();
            case 20:
               switch(eGlowPlayer.getGlowVisibility()) {
               case ALL:
                  return "other";
               case OTHER:
                  return "own";
               case OWN:
               case UNSUPPORTEDCLIENT:
                  return "none";
               case NONE:
                  return "all";
               }
            default:
               boolean raw = identifier.toLowerCase().endsWith("_raw");
               if (identifier.toLowerCase().contains("has_permission_")) {
                  EGlowEffect effect = DataManager.getEGlowEffect(identifier.toLowerCase().replace("has_permission_", "").replace("_raw", ""));
                  if (effect != null) {
                     if (!player.hasPermission(effect.getPermissionNode()) && !player.hasPermission("eglow.effect.*")) {
                        return raw ? "false" : EGlowMessageConfig.Message.GUI_NO.get();
                     } else {
                        return raw ? "true" : EGlowMessageConfig.Message.GUI_YES.get();
                     }
                  } else {
                     return "Invalid effect";
                  }
               } else {
                  return null;
               }
            }
         }
      }
   }

   private String setToBasicName(String effect) {
      return ChatColor.stripColor(effect).toLowerCase().replaceAll("(slow|fast|[()\\s])", "");
   }

   private String getSpeedFromEffect(EGlowEffect effect, boolean raw) {
      if (effect == null) {
         return raw ? "none" : EGlowMessageConfig.Message.COLOR.get("none");
      } else {
         String effectName = effect.getName();
         if (effectName.contains("slow")) {
            return raw ? "slow" : EGlowMessageConfig.Message.COLOR.get("slow");
         } else if (effectName.contains("fast")) {
            return raw ? "fast" : EGlowMessageConfig.Message.COLOR.get("fast");
         } else {
            return raw ? "none" : EGlowMessageConfig.Message.COLOR.get("none");
         }
      }
   }
}

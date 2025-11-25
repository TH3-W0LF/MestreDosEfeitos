package me.eplugins.eglow.util.text;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.packets.PacketUtil;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.packets.chat.ChatColor;
import me.eplugins.eglow.util.packets.chat.rgb.RGBUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtil {
   private static final Pattern rgb = Pattern.compile("#[0-9a-fA-F]{6}");

   public static String translateColors(String text) {
      if (text != null && !text.isEmpty()) {
         try {
            if (ProtocolVersion.SERVER_VERSION.getMinorVersion() <= 15) {
               text = RGBUtils.getInstance().convertRGBtoLegacy(text);
               return text.replace("&", "§");
            }
         } catch (NullPointerException var3) {
            return text.replace("&", "§");
         }

         text = RGBUtils.getInstance().applyFormats(text);

         for(Matcher match = rgb.matcher(text); match.find(); match = rgb.matcher(text)) {
            String color = text.substring(match.start(), match.end());
            text = text.replace(color, String.valueOf(ChatColor.of(color)));
         }

         return text.replace("&", "§");
      } else {
         return "";
      }
   }

   public static void sendMsg(Object sender, String message, boolean withPrefix) {
      if (!message.isEmpty()) {
         message = translateColors((withPrefix ? EGlowMessageConfig.Message.PREFIX.get() : "") + message);
         if (sender instanceof Player) {
            if (EGlowMainConfig.MainConfig.ACTIONBARS_ENABLE.getBoolean()) {
               sendActionbar((Player)sender, message);
            } else {
               ((Player)sender).sendMessage(message);
            }
         } else {
            ((CommandSender)sender).sendMessage(message);
         }
      }

   }

   public static void sendPlainMsg(Object sender, String message, boolean withPrefix) {
      if (!message.isEmpty()) {
         message = translateColors((withPrefix ? EGlowMessageConfig.Message.PREFIX.get() : "") + message);
         if (sender instanceof Player) {
            ((Player)sender).sendMessage(message);
         } else {
            ((CommandSender)sender).sendMessage(message);
         }
      }

   }

   public static void sendMsgFromGUI(Player player, String message) {
      if (EGlowMainConfig.MainConfig.ACTIONBARS_ENABLE.getBoolean() && EGlowMainConfig.MainConfig.ACTIONBARS_IN_GUI.getBoolean()) {
         sendMsg(player, message, true);
      } else {
         sendPlainMsg(player, message, true);
      }

   }

   private static void sendActionbar(Player player, String message) {
      EGlowPlayer ePlayer = DataManager.getEGlowPlayer(player);
      if (ePlayer != null) {
         if (ePlayer.getVersion().getMinorVersion() < 9) {
            sendPlainMsg(player, message, false);
         } else {
            PacketUtil.sendActionbar(ePlayer, message);
         }

      }
   }

   public static void sendToConsole(String message, boolean withPrefix) {
      Bukkit.getConsoleSender().sendMessage(translateColors((withPrefix ? EGlowMessageConfig.Message.PREFIX.get() : "") + message));
   }

   public static void printException(String message, Throwable exception) {
      EGlow.getInstance().getLogger().log(Level.WARNING, translateColors(message), exception);
   }

   public static String getEffectChatName(EGlowPlayer eGlowPlayer) {
      return eGlowPlayer.getGlowEffect() == null ? EGlowMessageConfig.Message.GUI_NOT_AVAILABLE.get() : eGlowPlayer.getGlowEffect().getDisplayName();
   }
}

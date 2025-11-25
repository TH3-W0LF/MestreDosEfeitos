package me.eplugins.eglow.custommenu.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.custommenu.CustomMenus;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.entity.Player;

public class TextUtil {
   public static final String customMenuPrefix = "&f[&eeGlow-CustomMenu&f] ";

   public static String translateText(String text, Player player, boolean color, boolean papi, boolean mm, Map<String, String> args) {
      CustomMenus customMenus = EGlow.getInstance().getCustomMenus();
      Entry arg;
      if (args != null && text.contains("{")) {
         for(Iterator var7 = args.entrySet().iterator(); var7.hasNext(); text = text.replace((CharSequence)arg.getKey(), (CharSequence)arg.getValue())) {
            arg = (Entry)var7.next();
         }
      }

      if (papi && customMenus.getPapiAddon() != null) {
         text = customMenus.getPapiAddon().translatePlaceholders(player, text);
      }

      if (color) {
         text = ChatUtil.translateColors(text);
      } else if (mm && customMenus.getMmAddon() != null) {
         text = customMenus.getMmAddon().translateMM(text);
      }

      return text;
   }

   public static void sendToPlayerWithoutPrefix(Player player, String message) {
      ChatUtil.sendPlainMsg(player, message, false);
   }

   public static void sendToPlayer(Player player, String message) {
      ChatUtil.sendPlainMsg(player, "&f[&eeGlow-CustomMenu&f] " + message, false);
   }

   public static void sendToConsole(String message) {
      ChatUtil.sendToConsole("&f[&eeGlow-CustomMenu&f] " + message, false);
   }

   public static void sendException(String message, Throwable throwable) {
      ChatUtil.printException("&f[&eeGlow-CustomMenu&f] " + message, throwable);
   }
}

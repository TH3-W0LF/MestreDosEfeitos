package xshyo.us.theglow.libs.theAPI.utilities;

import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xshyo.us.theglow.libs.theAPI.TheAPI;
import xshyo.us.theglow.libs.theAPI.requirements.Requirement;
import xshyo.us.theglow.libs.theAPI.requirements.RequirementManager;
import xshyo.us.theglow.libs.theAPI.requirements.RequirementResult;

public final class Utils {
   private static final Random random = new Random();
   private static final Gson gson = new Gson();
   public static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})|#([A-Fa-f0-9]{6})");
   public static int[] targetSlots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

   public static boolean isLocked(List<Map<String, Object>> var0, Player var1) {
      if (var0 != null && !var0.isEmpty()) {
         try {
            ArrayList var2 = new ArrayList();
            Iterator var3 = var0.iterator();

            while(var3.hasNext()) {
               Object var4 = var3.next();
               if (var4 instanceof Map) {
                  Map var5 = (Map)var4;
                  Requirement var6 = createRequirement(var5);
                  if (var6 != null) {
                     var2.add(var6);
                  }
               }
            }

            if (var2.isEmpty()) {
               return true;
            } else {
               List var8 = TheAPI.getInstance().getRequirementManager().checkRequirements(var1, var2);
               return var8.stream().allMatch(RequirementResult::isMeets);
            }
         } catch (Exception var7) {
            TheAPI.getInstance().getLogger().warning("Error when verifying requirements: " + var7.getMessage());
            return false;
         }
      } else {
         return true;
      }
   }

   public static List<String> getRequirementsLore(List<Map<String, Object>> var0, Player var1, boolean var2, boolean var3, String var4) {
      if (var0 == null) {
         return new ArrayList();
      } else {
         var4 = translate(var4);
         ArrayList var5 = new ArrayList();
         List var6 = (List)var0.stream().map(Utils::createRequirement).collect(Collectors.toList());
         RequirementManager var7 = TheAPI.getInstance().getRequirementManager();
         if (var7.getProviders() == null) {
            var5.add(translate("&c[!] Requirement system is not available."));
            var5.add(translate("&7Please install &ePlaceholderAPI &7or remove this requirement."));
            return var5;
         } else {
            List var8 = var7.checkRequirements(var1, var6);
            boolean var9 = false;
            Iterator var10 = var8.iterator();

            while(true) {
               while(true) {
                  RequirementResult var11;
                  String var12;
                  do {
                     if (!var10.hasNext()) {
                        return var5;
                     }

                     var11 = (RequirementResult)var10.next();
                     var12 = var11.getMessage();
                  } while(var2 && (var12 == null || var12.trim().isEmpty()));

                  if (var3 && var9) {
                     var5.add(var4);
                  } else {
                     var5.add(setPAPI(var1, var12));
                     if (!var11.isMeets()) {
                        var9 = true;
                     }
                  }
               }
            }
         }
      }
   }

   public static Requirement createRequirement(Map<String, Object> var0) {
      return new Requirement((String)var0.get("placeholder"), (String)var0.get("condition"), var0.get("value"), (String)var0.get("success"), (String)var0.get("fail"));
   }

   public static boolean hasPermission(CommandSender var0, String var1, String var2, String var3, String var4) {
      if (!var0.isOp() && !var0.hasPermission(var3) && !var0.hasPermission(var4)) {
         var0.sendMessage(translate(var1.replace("{1}", var0.getName()).replace("{2}", var3)));
         Bukkit.getConsoleSender().sendMessage(translate(var2.replace("{1}", var0.getName()).replace("{2}", var3)));
         return false;
      } else {
         return true;
      }
   }

   public static void sendRawMessage(Player var0, String var1, String var2, String var3, Object... var4) {
      if (var1 != null && !var1.isEmpty()) {
         if (var4.length > 0) {
            for(int var5 = 0; var5 < var4.length; ++var5) {
               String var6 = "{" + (var5 + 1) + "}";
               var1 = var1.replace(var6, var4[var5].toString());
            }
         }

         var1 = var1.replace("{cmd}", var2);
         var1 = var1.replace("{shortenedcmd}", var3);
         var0.sendRawMessage(translate(var1));
      }

   }

   public static void sendMessage(CommandSender var0, String var1, String var2, String var3, Object... var4) {
      if (var1 != null && !var1.isEmpty()) {
         if (var4.length > 0) {
            for(int var5 = 0; var5 < var4.length; ++var5) {
               String var6 = "{" + (var5 + 1) + "}";
               var1 = var1.replace(var6, var4[var5].toString());
            }
         }

         var1 = var1.replace("{cmd}", var2);
         var1 = var1.replace("{shortenedcmd}", var3);
         var0.sendMessage(translate(var1));
      }

   }

   public static void sendMessageWhitPath(CommandSender var0, String var1, String var2, String var3, Object... var4) {
      if (var1 != null && !var1.isEmpty()) {
         if (var4.length > 0) {
            for(int var5 = 0; var5 < var4.length; ++var5) {
               String var6 = "{" + (var5 + 1) + "}";
               var1 = var1.replace(var6, var4[var5].toString());
            }
         }

         var1 = var1.replace("{cmd}", var2);
         var1 = var1.replace("{shortenedcmd}", var3);
         var0.sendMessage(translate(var1));
      }

   }

   public static boolean passCondition(Player var0, String var1) {
      String[] var2 = var1.split(" ");
      String var3 = setPAPI(var0, var2[0]);
      String var4 = var2[1];

      try {
         double var5 = Double.parseDouble(var2[2]);
         double var12 = Double.parseDouble(var3);
         byte var10 = -1;
         switch(var4.hashCode()) {
         case 60:
            if (var4.equals("<")) {
               var10 = 5;
            }
            break;
         case 62:
            if (var4.equals(">")) {
               var10 = 4;
            }
            break;
         case 1084:
            if (var4.equals("!=")) {
               var10 = 3;
            }
            break;
         case 1921:
            if (var4.equals("<=")) {
               var10 = 1;
            }
            break;
         case 1952:
            if (var4.equals("==")) {
               var10 = 2;
            }
            break;
         case 1983:
            if (var4.equals(">=")) {
               var10 = 0;
            }
         }

         switch(var10) {
         case 0:
            return var12 >= var5;
         case 1:
            return var12 <= var5;
         case 2:
            return var3.equals(var2[2]);
         case 3:
            return !var3.equals(var2[2]);
         case 4:
            return var12 > var5;
         case 5:
            return var12 < var5;
         default:
            return false;
         }
      } catch (ArrayIndexOutOfBoundsException | NumberFormatException var11) {
         String var6 = var2[1];
         byte var7 = -1;
         switch(var6.hashCode()) {
         case 61:
            if (var6.equals("=")) {
               var7 = 2;
            }
            break;
         case 1084:
            if (var6.equals("!=")) {
               var7 = 1;
            }
            break;
         case 1519:
            if (var6.equals("-|")) {
               var7 = 4;
            }
            break;
         case 1905:
            if (var6.equals("<-")) {
               var7 = 0;
            }
            break;
         case 3889:
            if (var6.equals("|-")) {
               var7 = 3;
            }
         }

         switch(var7) {
         case 0:
            return var3.contains(var2[2]);
         case 1:
            return !var3.equals(var2[2]);
         case 2:
            return var3.equals(var2[2]);
         case 3:
            return var3.startsWith(var2[2]);
         case 4:
            return var3.endsWith(var2[2]);
         default:
            return false;
         }
      }
   }

   public static void sendMessageWhitPath(CommandSender var0, String var1, Object... var2) {
      if (var1 != null && !var1.isEmpty()) {
         if (var2.length > 0) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               String var4 = "{" + (var3 + 1) + "}";
               var1 = var1.replace(var4, var2[var3].toString());
            }
         }

         var0.sendMessage(translate(var1));
      }

   }

   public static void sendActionbar(Player var0, String var1) {
      var0.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(setPAPI(var0, var1)));
   }

   public static boolean shouldExecuteAction(String var0) {
      int var1 = Integer.parseInt(var0.substring(8, var0.length() - 1));
      return random.nextInt(100) < var1;
   }

   public static String removeHexFormats(String var0) {
      return HEX_PATTERN.matcher(var0).replaceAll("");
   }

   private static String translateHexColorCodes(String var0) {
      boolean var1 = true;
      Matcher var2 = HEX_PATTERN.matcher(var0);
      StringBuffer var3 = new StringBuffer(var0.length() + 32);

      while(var2.find()) {
         String var4 = var2.group(1) != null ? var2.group(1) : var2.group(2);
         char var10002 = var4.charAt(0);
         var2.appendReplacement(var3, "§x§" + var10002 + "§" + var4.charAt(1) + "§" + var4.charAt(2) + "§" + var4.charAt(3) + "§" + var4.charAt(4) + "§" + var4.charAt(5));
      }

      return var2.appendTail(var3).toString();
   }

   public static String colorize(String var0) {
      return ChatColor.translateAlternateColorCodes('&', var0);
   }

   public static String translate(String var0) {
      return translateHexColorCodes(colorize(var0));
   }

   public static String[] translate(String[] var0) {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         var0[var1] = translate(var0[var1]);
      }

      return var0;
   }

   public static List<String> translate(List<String> var0) {
      return (List)var0.stream().map(Utils::translate).collect(Collectors.toList());
   }

   public static String setPAPI(Player var0, String var1) {
      return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") ? PlaceholderAPI.setPlaceholders(var0, var1) : var1;
   }

   public static int getCurrentVersion() {
      Matcher var0 = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?").matcher(Bukkit.getBukkitVersion());
      StringBuilder var1 = new StringBuilder();
      if (var0.find()) {
         var1.append(var0.group("version").replace(".", ""));
         String var2 = var0.group("patch");
         if (var2 == null) {
            var1.append("0");
         } else {
            var1.append(var2.replace(".", ""));
         }
      }

      Integer var3 = Ints.tryParse(var1.toString());
      if (var3 == null) {
         throw new RuntimeException("Could not retrieve server version!");
      } else {
         return var3;
      }
   }

   public static String millisToLongDHMS(long var0) {
      StringBuilder var2 = new StringBuilder();
      boolean var5 = false;
      if (var0 >= 1000L) {
         long var3 = var0 / 86400000L;
         if (var3 > 0L) {
            var5 = true;
            var0 -= var3 * 86400000L;
            var2.append(var3).append("d");
         }

         if (var5) {
            var2.append(" ");
            var5 = false;
         }

         var3 = var0 / 3600000L;
         if (var3 > 0L) {
            var5 = true;
            var0 -= var3 * 3600000L;
            var2.append(var3).append("h");
         }

         if (var5) {
            var2.append(" ");
            var5 = false;
         }

         var3 = var0 / 60000L;
         if (var3 > 0L) {
            var5 = true;
            var0 -= var3 * 60000L;
            var2.append(var3).append("m");
         }

         var3 = var0 / 1000L;
         if (var3 > 0L) {
            if (var5) {
               var2.append(" ");
            }

            var2.append(var3).append(var3 > 1L ? "s" : "");
         }

         return var2.toString();
      } else {
         return "0";
      }
   }

   public static void createPrompt(JavaPlugin var0, Player var1, boolean var2, int var3, StringPrompt var4, Map<Object, Object> var5) {
      Conversation var6 = (new ConversationFactory(var0)).withFirstPrompt(var4).withInitialSessionData(var5).withLocalEcho(var2).withTimeout(var3).buildConversation(var1);
      var6.begin();
   }

   private Utils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static Gson getGson() {
      return gson;
   }
}

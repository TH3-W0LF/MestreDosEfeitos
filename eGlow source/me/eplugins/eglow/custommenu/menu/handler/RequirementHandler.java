package me.eplugins.eglow.custommenu.menu.handler;

import java.util.Iterator;
import java.util.Map;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.addon.VaultAddon;
import me.eplugins.eglow.custommenu.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class RequirementHandler {
   public static boolean failedRequirements(YamlConfiguration config, Player player, String menuName, String requirementsType, Map<String, String> parsedArgs) {
      try {
         ConfigurationSection reqs = config.getConfigurationSection(requirementsType + ".requirements");
         if (reqs == null) {
            return false;
         } else if (reqs.getKeys(false).isEmpty()) {
            return false;
         } else {
            boolean stopAtSuccess = config.getBoolean(requirementsType + ".stop_at_success");
            int minReqs = config.getInt(requirementsType + ".minimum_requirements", reqs.getKeys(false).size());
            int reqsMet = 0;
            Iterator var9 = reqs.getKeys(false).iterator();

            do {
               if (!var9.hasNext()) {
                  return true;
               }

               String reqName = (String)var9.next();
               boolean optional = reqs.getBoolean(reqName + ".optional", false);
               String rawType = reqs.getString(reqName + ".type", "").toLowerCase();
               boolean negated = rawType.startsWith("!") && !rawType.equals("!=");
               String type = negated ? rawType.substring(1) : rawType;
               boolean passed = checkRequirement(reqs, reqName, type, player, parsedArgs);
               passed = negated != passed;
               if (hasPassedRequirement(passed, minReqs, optional)) {
                  ++reqsMet;
                  ActionHandler.runCommandsFromConfig(config, menuName, requirementsType + ".requirements." + reqName + ".success_commands", player, parsedArgs);
               } else {
                  ActionHandler.runCommandsFromConfig(config, menuName, requirementsType + ".requirements." + reqName + ".deny_commands", player, parsedArgs);
               }

               if (stopAtSuccess && reqsMet > 0) {
                  return false;
               }
            } while(minReqs <= 0 || reqsMet < minReqs);

            return false;
         }
      } catch (Exception var16) {
         TextUtil.sendException("Failed to check requirements for custom menu", var16);
         return true;
      }
   }

   private static boolean checkRequirement(ConfigurationSection reqs, String reqName, String type, Player player, Map<String, String> parsedArgs) {
      byte var6 = -1;
      switch(type.hashCode()) {
      case -1738871878:
         if (type.equals("has money")) {
            var6 = 1;
         }
         break;
      case -1364181836:
         if (type.equals("string equals ignorecase")) {
            var6 = 5;
         }
         break;
      case -1093502642:
         if (type.equals("string contains")) {
            var6 = 6;
         }
         break;
      case -236813650:
         if (type.equals("string equals")) {
            var6 = 4;
         }
         break;
      case 60:
         if (type.equals("<")) {
            var6 = 12;
         }
         break;
      case 62:
         if (type.equals(">")) {
            var6 = 11;
         }
         break;
      case 1084:
         if (type.equals("!=")) {
            var6 = 8;
         }
         break;
      case 1921:
         if (type.equals("<=")) {
            var6 = 10;
         }
         break;
      case 1952:
         if (type.equals("==")) {
            var6 = 7;
         }
         break;
      case 1983:
         if (type.equals(">=")) {
            var6 = 9;
         }
         break;
      case 340313877:
         if (type.equals("has permission")) {
            var6 = 0;
         }
         break;
      case 695389079:
         if (type.equals("has exp")) {
            var6 = 2;
         }
         break;
      case 2023888222:
         if (type.equals("is near")) {
            var6 = 3;
         }
      }

      switch(var6) {
      case 0:
         return checkPermission(reqs, reqName, player);
      case 1:
         return checkMoney(reqs, reqName, player, parsedArgs);
      case 2:
         return checkExp(reqs, reqName, player, parsedArgs);
      case 3:
         return checkIsNear(reqs, reqName, player);
      case 4:
         return checkStringEquals(reqs, reqName, player, false, parsedArgs);
      case 5:
         return checkStringEquals(reqs, reqName, player, true, parsedArgs);
      case 6:
         return checkStringContains(reqs, reqName, player, parsedArgs);
      case 7:
         return checkNumberCompare(reqs, reqName, player, (a, b) -> {
            return a == b;
         }, parsedArgs);
      case 8:
         return checkNumberCompare(reqs, reqName, player, (a, b) -> {
            return a != b;
         }, parsedArgs);
      case 9:
         return checkNumberCompare(reqs, reqName, player, (a, b) -> {
            return a >= b;
         }, parsedArgs);
      case 10:
         return checkNumberCompare(reqs, reqName, player, (a, b) -> {
            return a <= b;
         }, parsedArgs);
      case 11:
         return checkNumberCompare(reqs, reqName, player, (a, b) -> {
            return a > b;
         }, parsedArgs);
      case 12:
         return checkNumberCompare(reqs, reqName, player, (a, b) -> {
            return a < b;
         }, parsedArgs);
      default:
         return false;
      }
   }

   private static boolean checkPermission(ConfigurationSection reqs, String reqName, Player player) {
      String permissionRaw = reqs.getString(reqName + ".permission", "");
      String permission = TextUtil.translateText(permissionRaw, player, false, true, false, (Map)null);
      return !permission.isEmpty() && player.hasPermission(permission);
   }

   private static boolean checkMoney(ConfigurationSection reqs, String reqName, Player player, Map<String, String> parsedArgs) {
      VaultAddon vaultAddon = EGlow.getInstance().getVaultAddon();
      if (vaultAddon == null) {
         return false;
      } else {
         double amount = getDoubleFromReq(reqs, reqName, "amount", player, parsedArgs);
         return vaultAddon.hasBalance(player, amount);
      }
   }

   private static boolean checkExp(ConfigurationSection reqs, String reqName, Player player, Map<String, String> parsedArgs) {
      boolean checkLevel = reqs.getBoolean(reqName + ".level", false);
      double amount = getDoubleFromReq(reqs, reqName, "amount", player, parsedArgs);
      if (checkLevel) {
         return (double)player.getLevel() >= amount;
      } else {
         return (double)player.getTotalExperience() >= amount;
      }
   }

   private static boolean checkIsNear(ConfigurationSection reqs, String reqName, Player player) {
      String locRaw = reqs.getString(reqName + ".location", "").replace(" ", "");
      double distance = reqs.getDouble(reqName + ".distance", 0.0D);
      if (!locRaw.isEmpty() && !(distance <= 0.0D)) {
         String[] locationValues = locRaw.split(",");
         if (locationValues.length != 4) {
            return true;
         } else {
            World world = Bukkit.getWorld(locationValues[0]);
            if (world == null) {
               return true;
            } else {
               try {
                  double x = Double.parseDouble(locationValues[1]);
                  double y = Double.parseDouble(locationValues[2]);
                  double z = Double.parseDouble(locationValues[3]);
                  Location loc = new Location(world, x, y, z);
                  double distanceSquared = distance * distance;
                  return player.getLocation().distanceSquared(loc) <= distanceSquared;
               } catch (NumberFormatException var17) {
                  return true;
               }
            }
         }
      } else {
         return true;
      }
   }

   private static boolean checkStringEquals(ConfigurationSection reqs, String reqName, Player player, boolean ignoreCase, Map<String, String> parsedArgs) {
      String input = TextUtil.translateText(reqs.getString(reqName + ".input", ""), player, true, true, false, parsedArgs);
      String output = TextUtil.translateText(reqs.getString(reqName + ".output", ""), player, true, true, false, parsedArgs);
      return ignoreCase ? input.equalsIgnoreCase(output) : input.equals(output);
   }

   private static boolean checkStringContains(ConfigurationSection reqs, String reqName, Player player, Map<String, String> parsedArgs) {
      String input = TextUtil.translateText(reqs.getString(reqName + ".input", ""), player, true, true, false, parsedArgs);
      String output = TextUtil.translateText(reqs.getString(reqName + ".output", ""), player, true, true, false, parsedArgs);
      return input.contains(output);
   }

   private static boolean checkNumberCompare(ConfigurationSection reqs, String reqName, Player player, RequirementHandler.DoubleComparator comparator, Map<String, String> parsedArgs) {
      double input = getDoubleFromReq(reqs, reqName, "input", player, parsedArgs);
      double output = getDoubleFromReq(reqs, reqName, "output", player, parsedArgs);
      return comparator.compare(input, output);
   }

   private static double getDoubleFromReq(ConfigurationSection reqs, String reqName, String key, Player player, Map<String, String> parsedArgs) {
      String raw = reqs.getString(reqName + "." + key, "");
      if (raw.contains("%")) {
         try {
            return Double.parseDouble(TextUtil.translateText(raw, player, false, true, false, parsedArgs));
         } catch (NumberFormatException var7) {
            return 0.0D;
         }
      } else {
         return reqs.getDouble(reqName + "." + key, 0.0D);
      }
   }

   private static boolean hasPassedRequirement(boolean passed, int minReqs, boolean optional) {
      if (minReqs == 0) {
         return passed;
      } else {
         return passed && !optional;
      }
   }

   @FunctionalInterface
   private interface DoubleComparator {
      boolean compare(double var1, double var3);
   }
}

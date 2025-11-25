package me.eplugins.eglow.database;

import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.text.ChatUtil;

public class EGlowPlayerdataManager {
   private static EGlowPlayerdataSQLite sqlite;
   private static EGlowPlayerdataMySQL mysql;
   private static boolean mysql_Failed = false;

   public static void initialize() {
      switch(EGlowMainConfig.MainConfig.MYSQL_ENABLE.getBoolean() ? EnumUtil.ConfigType.MYSQL : EnumUtil.ConfigType.SQLITE) {
      case SQLITE:
         sqlite = new EGlowPlayerdataSQLite();
         break;
      case MYSQL:
         mysql = new EGlowPlayerdataMySQL();
      }

   }

   public static void loadPlayerdata(EGlowPlayer eGlowPlayer) {
      switch(EGlowMainConfig.MainConfig.MYSQL_ENABLE.getBoolean() ? EnumUtil.ConfigType.MYSQL : EnumUtil.ConfigType.SQLITE) {
      case SQLITE:
         if (sqlite == null) {
            return;
         }

         sqlite.loadPlayerdata(eGlowPlayer);
         break;
      case MYSQL:
         if (mysql == null) {
            return;
         }

         mysql.loadPlayerdata(eGlowPlayer);
      }

      eGlowPlayer.setSaveData(false);
   }

   public static void savePlayerdata(EGlowPlayer eGlowPlayer) {
      if (!eGlowPlayer.skipSaveData()) {
         switch(EGlowMainConfig.MainConfig.MYSQL_ENABLE.getBoolean() ? EnumUtil.ConfigType.MYSQL : EnumUtil.ConfigType.SQLITE) {
         case SQLITE:
            if (sqlite == null) {
               return;
            }

            sqlite.savePlayerdata(eGlowPlayer);
            break;
         case MYSQL:
            if (mysql == null) {
               return;
            }

            mysql.savePlayerdata(eGlowPlayer);
         }

      }
   }

   public static boolean getMySQL_Failed() {
      return mysql_Failed;
   }

   public static void setMysql_Failed(boolean state) {
      if (mysql_Failed != state) {
         mysql_Failed = state;
         if (!state) {
            ChatUtil.sendToConsole("&6trying to reestablishing MySQL connection&f.", true);
            initialize();
         }

      }
   }

   public static void setDefaultValues(EGlowPlayer ePlayer) {
      ePlayer.setActiveOnQuit(false);
      ePlayer.setDataFromLastGlow("none");
      ePlayer.setGlowOnJoin(EGlowMainConfig.MainConfig.SETTINGS_JOIN_DEFAULT_GLOW_ON_JOIN_VALUE.getBoolean());
   }
}

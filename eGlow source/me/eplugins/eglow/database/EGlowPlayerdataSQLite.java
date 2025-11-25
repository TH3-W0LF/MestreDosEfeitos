package me.eplugins.eglow.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.sqlite.SQLiteDataSource;

public class EGlowPlayerdataSQLite {
   private final ConcurrentHashMap<String, String> savingQueue = new ConcurrentHashMap();
   private SQLiteDataSource sqlite;
   private boolean isActive = false;

   public EGlowPlayerdataSQLite() {
      if (this.setupSQLiteConnection()) {
         ChatUtil.sendToConsole("&aSuccessfully loaded Playerdata database.", true);
         this.startSavingQueueHandler();
      } else {
         ChatUtil.sendToConsole("&cFailed to load Playerdata database!.", true);
      }

   }

   public void loadPlayerdata(EGlowPlayer eGlowPlayer) {
      String playerUUID = eGlowPlayer.getUuid().toString();
      if (this.getSavingQueue().containsKey(playerUUID)) {
         String[] data = ((String)this.getSavingQueue().get(playerUUID)).split(",");
         this.getSavingQueue().remove(playerUUID);
         eGlowPlayer.setDataFromLastGlow(data[0]);
         eGlowPlayer.setGlowOnJoin(Boolean.parseBoolean(data[1]));
         eGlowPlayer.setActiveOnQuit(Boolean.parseBoolean(data[2]));
         eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.valueOf(data[3]));
         eGlowPlayer.setForcedGlowDisableReason(EnumUtil.GlowDisableReason.valueOf(data[4]));
      } else {
         Connection connection = null;
         PreparedStatement preparedStatement = null;
         ResultSet resultSet = null;
         String statement = "SELECT * FROM eglow WHERE UUID='" + playerUUID + "'";

         try {
            connection = this.getSqlite().getConnection();
            preparedStatement = connection.prepareStatement(statement);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
               try {
                  eGlowPlayer.setGlowOnJoin(resultSet.getBoolean("glowOnJoin"));
                  eGlowPlayer.setActiveOnQuit(resultSet.getBoolean("activeOnQuit"));
                  eGlowPlayer.setDataFromLastGlow(resultSet.getString("lastGlowData"));
                  eGlowPlayer.setGlowVisibility(resultSet.getString("glowVisibility").equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT.name()) ? eGlowPlayer.getGlowVisibility() : EnumUtil.GlowVisibility.valueOf(resultSet.getString("glowVisibility")));
                  eGlowPlayer.setForcedGlowDisableReason(EnumUtil.GlowDisableReason.valueOf(resultSet.getString("glowDisableReason")));
               } catch (IllegalArgumentException | NullPointerException var12) {
                  ChatUtil.sendToConsole("Playerdata of player: " + eGlowPlayer.getDisplayName() + " has been reset due to a corrupted value.", true);
                  EGlowPlayerdataManager.setDefaultValues(eGlowPlayer);
               }
            } else {
               EGlowPlayerdataManager.setDefaultValues(eGlowPlayer);
            }
         } catch (SQLException var13) {
            ChatUtil.printException("Failed to load SQLite player data", var13);
         } finally {
            this.closeMySQLConnection(connection, preparedStatement, resultSet);
         }

      }
   }

   public void savePlayerdata(EGlowPlayer eGlowPlayer) {
      String values = eGlowPlayer.getLastGlow() + "," + eGlowPlayer.isGlowOnJoin() + "," + eGlowPlayer.isActiveOnQuit() + "," + eGlowPlayer.getGlowVisibility().name() + "," + eGlowPlayer.getGlowDisableReason().name();
      if (this.getSavingQueue().containsKey(eGlowPlayer.getUuid().toString())) {
         this.getSavingQueue().replace(eGlowPlayer.getUuid().toString(), values);
      } else {
         this.getSavingQueue().put(eGlowPlayer.getUuid().toString(), values);
      }

   }

   private void startSavingQueueHandler() {
      NMSHook.scheduleTimerTask(true, 1L, 20L, () -> {
         if (!this.isActive && !this.getSavingQueue().isEmpty()) {
            this.isActive = true;
            this.processSavingQueue();
         }

      });
   }

   private void processSavingQueue() {
      Iterator var1 = this.getSavingQueue().entrySet().iterator();

      while(var1.hasNext()) {
         Entry<String, String> entry = (Entry)var1.next();
         String statement = "INSERT OR REPLACE INTO eglow (UUID, glowOnJoin, activeOnQuit, lastGlowData, glowVisibility, glowDisableReason) VALUES(?,?,?,?,?,?)";
         String playerUUID = (String)entry.getKey();
         String values = (String)entry.getValue();
         String[] splitValues = values.split(",");
         Connection connection = null;
         PreparedStatement preparedStatement = null;

         try {
            connection = this.getSqlite().getConnection();
            preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, playerUUID);
            preparedStatement.setBoolean(2, Boolean.parseBoolean(splitValues[1]));
            preparedStatement.setBoolean(3, Boolean.parseBoolean(splitValues[2]));
            preparedStatement.setString(4, splitValues[0]);
            preparedStatement.setString(5, splitValues[3]);
            preparedStatement.setString(6, splitValues[4]);
            preparedStatement.executeUpdate();
            this.getSavingQueue().remove(playerUUID);
            continue;
         } catch (Exception var13) {
            if (!var13.getMessage().startsWith("[SQLITE_BUSY]")) {
               ChatUtil.printException("Failed to process SQLite queue", var13);
            }

            this.isActive = false;
         } finally {
            this.closeMySQLConnection(connection, preparedStatement, (ResultSet)null);
         }

         return;
      }

      this.isActive = false;
   }

   private boolean setupSQLiteConnection() {
      this.sqlite = new SQLiteDataSource();
      this.getSqlite().setUrl("jdbc:sqlite:" + EGlow.getInstance().getDataFolder() + File.separator + "Playerdata.db");
      Connection connection = null;
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;
      String statement = "";

      boolean var6;
      try {
         connection = this.getSqlite().getConnection();
         DatabaseMetaData dbm = connection.getMetaData();
         resultSet = dbm.getTables((String)null, (String)null, "eglow", (String[])null);
         if (!resultSet.next()) {
            statement = "CREATE TABLE eglow (UUID VARCHAR(255) NOT NULL, glowOnJoin BOOLEAN, activeOnQuit BOOLEAN, lastGlowData VARCHAR(255), glowVisibility VARCHAR(255), glowDisableReason VARCHAR(255), PRIMARY KEY (UUID))";
         }

         if (!statement.isEmpty()) {
            try {
               preparedStatement = connection.prepareStatement(statement);
               preparedStatement.executeUpdate();
            } catch (Exception var11) {
            }

            var6 = true;
            return var6;
         }

         var6 = true;
         return var6;
      } catch (SQLException var12) {
         ChatUtil.printException("Failed to setup SQLite connect", var12);
         var6 = false;
      } finally {
         this.closeMySQLConnection(connection, preparedStatement, resultSet);
      }

      return var6;
   }

   private void closeMySQLConnection(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
      try {
         if (connection != null) {
            connection.close();
         }

         if (preparedStatement != null) {
            preparedStatement.close();
         }

         if (resultSet != null) {
            resultSet.close();
         }
      } catch (SQLException var5) {
      }

   }

   @Generated
   public ConcurrentHashMap<String, String> getSavingQueue() {
      return this.savingQueue;
   }

   @Generated
   public SQLiteDataSource getSqlite() {
      return this.sqlite;
   }

   @Generated
   public boolean isActive() {
      return this.isActive;
   }
}

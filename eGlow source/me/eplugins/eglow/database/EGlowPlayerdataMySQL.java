package me.eplugins.eglow.database;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import lombok.Generated;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;

public class EGlowPlayerdataMySQL {
   Object mysql;

   public EGlowPlayerdataMySQL() {
      if (this.setupMySQLConnection()) {
         ChatUtil.sendToConsole("&aSuccessfully loaded MySQL.", true);
      } else {
         EGlowPlayerdataManager.setMysql_Failed(true);
         ChatUtil.sendToConsole("&cFailed to load MySQL.", true);
      }

   }

   public void loadPlayerdata(EGlowPlayer eGlowPlayer) {
      Connection connection = null;
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;
      String statement = "SELECT * FROM eglow WHERE UUID='" + eGlowPlayer.getUuid().toString() + "'";

      try {
         connection = this.getConnection();
         preparedStatement = ((Connection)Objects.requireNonNull(connection, "Failed to retrieve MySQL connection")).prepareStatement(statement);
         resultSet = preparedStatement.executeQuery();
         if (resultSet.next()) {
            try {
               eGlowPlayer.setGlowOnJoin(resultSet.getBoolean("glowOnJoin"));
               eGlowPlayer.setActiveOnQuit(resultSet.getBoolean("activeOnQuit"));
               eGlowPlayer.setDataFromLastGlow(resultSet.getString("lastGlowData"));
               eGlowPlayer.setGlowVisibility(resultSet.getString("glowVisibility").equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT.name()) ? eGlowPlayer.getGlowVisibility() : EnumUtil.GlowVisibility.valueOf(resultSet.getString("glowVisibility")));
               eGlowPlayer.setForcedGlowDisableReason(EnumUtil.GlowDisableReason.valueOf(resultSet.getString("glowDisableReason")));
            } catch (IllegalArgumentException | NullPointerException var11) {
               ChatUtil.sendToConsole("Playerdata of player: " + eGlowPlayer.getDisplayName() + " has been reset due to a corrupted value.", true);
               EGlowPlayerdataManager.setDefaultValues(eGlowPlayer);
            }
         } else {
            EGlowPlayerdataManager.setDefaultValues(eGlowPlayer);
         }
      } catch (SQLException var12) {
         ChatUtil.printException("Failed to load playerdata", var12);
      } finally {
         this.closeMySQLConnection(connection, preparedStatement, resultSet);
      }

   }

   public void savePlayerdata(EGlowPlayer eGlowPlayer) {
      Connection connection = null;
      PreparedStatement preparedStatement = null;
      String statement = "INSERT INTO eglow (UUID, glowOnJoin, activeOnQuit, lastGlowData, glowVisibility, glowDisableReason) VALUES(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE UUID=?, glowonJoin=?, activeOnQuit=?, lastGlowData=?, glowVisibility= ?, glowDisableReason=?";
      String lastGlowData = eGlowPlayer.getLastGlow();
      boolean glowOnJoin = eGlowPlayer.isGlowOnJoin();
      boolean activeOnQuit = eGlowPlayer.isActiveOnQuit();
      String glowVisibility = eGlowPlayer.getGlowVisibility().name();
      String glowDisableReason = eGlowPlayer.getGlowDisableReason().name();

      try {
         connection = this.getConnection();
         preparedStatement = ((Connection)Objects.requireNonNull(connection, "Failed to retrieve MySQL connection")).prepareStatement(statement);
         preparedStatement.setString(1, eGlowPlayer.getUuid().toString());
         preparedStatement.setBoolean(2, glowOnJoin);
         preparedStatement.setBoolean(3, activeOnQuit);
         preparedStatement.setString(4, lastGlowData);
         preparedStatement.setString(5, glowVisibility);
         preparedStatement.setString(6, glowDisableReason);
         preparedStatement.setString(7, eGlowPlayer.getUuid().toString());
         preparedStatement.setBoolean(8, glowOnJoin);
         preparedStatement.setBoolean(9, activeOnQuit);
         preparedStatement.setString(10, lastGlowData);
         preparedStatement.setString(11, glowVisibility);
         preparedStatement.setString(12, glowDisableReason);
         preparedStatement.executeUpdate();
      } catch (SQLException var14) {
         ChatUtil.printException("Failed to save playerdata", var14);
      } finally {
         this.closeMySQLConnection(connection, preparedStatement, (ResultSet)null);
      }

   }

   private boolean setupMySQLConnection() {
      this.mysql = this.getMySQLDataSource();
      this.setServerName(EGlowMainConfig.MainConfig.MYSQL_HOST.getString());
      this.setPort(EGlowMainConfig.MainConfig.MYSQL_PORT.getInt());
      this.setDatabaseName(!EGlowMainConfig.MainConfig.ADVANCED_MYSQL_USESSL.getBoolean() ? "?useSSL=false" : "");
      this.setUser(EGlowMainConfig.MainConfig.MYSQL_USERNAME.getString());
      this.setPassword(EGlowMainConfig.MainConfig.MYSQL_PASSWORD.getString());
      Connection connection = null;
      PreparedStatement preparedStatement = null;
      String statement = "CREATE DATABASE IF NOT EXISTS " + (!EGlowMainConfig.MainConfig.MYSQL_DBNAME.getString().isEmpty() ? "`" + EGlowMainConfig.MainConfig.MYSQL_DBNAME.getString() + "`" : "eglow");

      try {
         connection = this.getConnection();
         preparedStatement = ((Connection)Objects.requireNonNull(connection, "Failed to retrieve MySQL connection")).prepareStatement(statement);
         preparedStatement.executeUpdate();
      } catch (SQLException var18) {
         ChatUtil.printException("Failed to check/setup MySQL database", var18);
      } finally {
         this.closeMySQLConnection(connection, preparedStatement, (ResultSet)null);
      }

      this.setDatabaseName((!EGlowMainConfig.MainConfig.MYSQL_DBNAME.getString().isEmpty() ? EGlowMainConfig.MainConfig.MYSQL_DBNAME.getString() : "eglow") + (!EGlowMainConfig.MainConfig.ADVANCED_MYSQL_USESSL.getBoolean() ? "?useSSL=false" : ""));

      boolean var5;
      try {
         connection = this.getConnection();
         statement = "CREATE TABLE IF NOT EXISTS eglow (UUID VARCHAR(190) NOT NULL, glowOnJoin BOOLEAN, activeOnQuit BOOLEAN, lastGlowData VARCHAR(190), glowVisibility VARCHAR(190), glowDisableReason VARCHAR(190), PRIMARY KEY (UUID))";
         preparedStatement = connection.prepareStatement(statement);

         try {
            preparedStatement.executeUpdate();
         } catch (Exception var17) {
         }

         boolean var4 = true;
         return var4;
      } catch (SQLException var20) {
         ChatUtil.printException("Failed to check/setup MySQL table", var20);
         var5 = false;
      } finally {
         this.closeMySQLConnection(connection, preparedStatement, (ResultSet)null);
      }

      return var5;
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

   private Object getMySQLDataSource() {
      try {
         return NMSHook.nms.newMySQLDataSource.newInstance();
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException var2) {
         ChatUtil.printException("Failed to retrieve MySQL datasource", var2);
         return null;
      }
   }

   private Connection getConnection() {
      try {
         return (Connection)NMSHook.nms.MySQL_getConnection.invoke(this.getMysql());
      } catch (InvocationTargetException | IllegalAccessException var2) {
         ChatUtil.printException("Failed to retrieve MySQL connection", var2);
         return null;
      }
   }

   private void setServerName(String serverName) {
      try {
         NMSHook.nms.MySQL_setServerName.invoke(this.getMysql(), serverName);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         ChatUtil.printException("Failed to set MySQL server name", var3);
      }

   }

   private void setPort(int port) {
      try {
         NMSHook.nms.MySQL_setPort.invoke(this.getMysql(), port);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         ChatUtil.printException("Failed to set MySQL port", var3);
      }

   }

   private void setDatabaseName(String databaseName) {
      try {
         NMSHook.nms.MySQL_setDatabaseName.invoke(this.getMysql(), databaseName);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         ChatUtil.printException("Failed to set MySQL database name", var3);
      }

   }

   private void setUser(String user) {
      try {
         NMSHook.nms.MySQL_setUser.invoke(this.getMysql(), user);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         ChatUtil.printException("Failed to set MySQL user", var3);
      }

   }

   private void setPassword(String password) {
      try {
         NMSHook.nms.MySQL_setPassword.invoke(this.getMysql(), password);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         ChatUtil.printException("Failed to set MySQL password", var3);
      }

   }

   @Generated
   public Object getMysql() {
      return this.mysql;
   }
}

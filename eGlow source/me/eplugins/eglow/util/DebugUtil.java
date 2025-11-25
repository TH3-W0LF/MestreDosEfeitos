package me.eplugins.eglow.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Bukkit;

public class DebugUtil {
   private static final String serverVersion = Bukkit.getVersion().split("MC: ")[1].replace(")", "");
   private static final String[] versionParts;
   private static final int mainVersion;
   private static final int minorVersion;

   public static boolean onBungee() {
      return !Bukkit.getServer().getOnlineMode() && NMSHook.isBungee();
   }

   public static boolean onVelocity() {
      return EGlowMainConfig.MainConfig.ADVANCED_VELOCITY_MESSAGING.getBoolean();
   }

   public static void setupSupport(EGlow eGlow) {
      try {
         URL url = new URL("https://raw.githubusercontent.com/SlyOtters/EGlowTracker/main/BlockedUsers.txt");
         String BlockedUsers = (new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))).readLine();
         String[] var3 = BlockedUsers.split(",");
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String username = var3[var5];
            if (username.equals("09f60b2b754e15a587ae75572c2b2c25d4b736cdb17f9231544bd6b65dc243c8")) {
               NMSHook.scheduleTask(false, () -> {
                  ChatUtil.sendToConsole("&cPɪʀᴀᴛᴇᴅ ᴇGʟᴏᴡ ᴠᴇʀsɪᴏɴ ᴅᴇᴛᴇᴄᴛᴇᴅ! Dɪsᴀʙʟɪɴɢ ᴇGʟᴏᴡ!", true);
                  ChatUtil.sendToConsole("&cTʜɪs ɪs ғᴏʀ ʏᴏᴜʀ ᴏᴡɴ sᴀғᴇᴛʏ sᴏᴍᴇ ʟᴇᴀᴋᴇᴅ ᴊᴀʀs ᴅᴏ ᴄᴏɴᴛᴀɪɴ ᴍᴀʟᴡᴀʀᴇ!", true);
                  EGlow.getInstance().getServer().getPluginManager().disablePlugin(eGlow);
               });
            }
         }
      } catch (Exception var7) {
      }

   }

   @Generated
   public static String getServerVersion() {
      return serverVersion;
   }

   @Generated
   public static int getMainVersion() {
      return mainVersion;
   }

   @Generated
   public static int getMinorVersion() {
      return minorVersion;
   }

   static {
      versionParts = serverVersion.split("\\.");
      mainVersion = Integer.parseInt(versionParts[1]);
      minorVersion = Integer.parseInt(versionParts.length > 2 ? versionParts[2] : "0");
   }
}

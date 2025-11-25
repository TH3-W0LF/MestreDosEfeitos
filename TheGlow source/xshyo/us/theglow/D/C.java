package xshyo.us.theglow.D;

import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.libs.kyori.adventure.text.minimessage.MiniMessage;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public class C implements Listener {
   private final TheGlow A = TheGlow.getInstance();
   private final JavaPlugin B;
   private final String C = "xShyo-Plugins";
   private final String D = "TheGlow";

   public C(JavaPlugin var1) {
      this.B = var1;
   }

   public void getReleaseInfo(String var1, Consumer<String> var2) {
      if (var2 == null) {
         this.B.getLogger().info("No consumer provided for update checking.");
      } else {
         TheGlow.getInstance().getScheduler().runTaskAsynchronously(() -> {
            try {
               String var1 = "https://api.github.com/repos/xShyo-Plugins/TheGlow/releases/latest";
               HttpURLConnection var2 = this.A(var1);
               if (var2 != null) {
                  this.A(var2);
               }
            } catch (JsonSyntaxException | NullPointerException | IOException var4) {
               this.B.getLogger().log(Level.WARNING, "Failed to check for updates: ", var4);
            }

         });
      }
   }

   @EventHandler
   public void PlayerJoinEvent(PlayerJoinEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = Utils.translate("<blue><bold><hover:show_text:'<blue>Click to open</blue>'><click:open_url:'https://polymart.org/resource/6519'>[POLYMART]</click></hover></bold></blue>");
      String var4 = Utils.translate("<aqua><bold><hover:show_text:'<aqua>Click to open</aqua>'><click:open_url:'https://builtbybit.com/resources/51697'>[BUILTBYBIT]</click></hover></bold></aqua>");
      String var5 = Utils.translate("<gold><bold><hover:show_text:'<gold>Click to open</gold>'><click:open_url:'https://www.spigotmc.org/resources/119556/'>[SPIGOT]</click></hover></bold></gold>");
      if (this.A.isUpdateAvailable() && var2.hasPermission("theglow.updatechecker")) {
         var2.sendMessage("");
         this.A.getAdventure().player(var2).sendMessage(MiniMessage.miniMessage().deserialize(" <white>A newer version of <dark_red><bold>TheGlow</bold> <white>is available. download it from: " + var5 + " <dark_gray>» " + var4 + " <dark_gray>» " + var3));
         var2.sendMessage("");
         var2.sendMessage(Utils.translate("&8» &7" + this.A.getUpdateDescription()));
      }

   }

   private HttpURLConnection A(String var1) throws IOException {
      HttpURLConnection var2 = (HttpURLConnection)(new URL(var1)).openConnection();
      var2.setConnectTimeout(5000);
      var2.setReadTimeout(5000);
      int var3 = var2.getResponseCode();
      if (var3 != 200) {
         this.B.getLogger().info("Update check failed. Server responded with code: " + var3);
         var2.disconnect();
         return null;
      } else {
         return var2;
      }
   }

   private String A(HttpURLConnection var1) throws IOException {
      String var5;
      try {
         BufferedReader var2 = new BufferedReader(new InputStreamReader(var1.getInputStream()));

         try {
            StringBuilder var3 = new StringBuilder();

            String var4;
            while((var4 = var2.readLine()) != null) {
               var3.append(var4);
            }

            var5 = var3.toString();
         } catch (Throwable var11) {
            try {
               var2.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }

            throw var11;
         }

         var2.close();
      } finally {
         var1.disconnect();
      }

      return var5;
   }
}

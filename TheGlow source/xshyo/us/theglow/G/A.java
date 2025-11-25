package xshyo.us.theglow.G;

import java.util.List;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.CurrentGlow;
import xshyo.us.theglow.data.PlayerGlowData;

public class A extends PlaceholderExpansion {
   private final TheGlow A = TheGlow.getInstance();

   public boolean persist() {
      return true;
   }

   public boolean canRegister() {
      return true;
   }

   @NotNull
   public String getIdentifier() {
      return "theglow";
   }

   @NotNull
   public String getAuthor() {
      return "xShyo_";
   }

   @NotNull
   public String getVersion() {
      PluginDescriptionFile var1 = this.A.getDescription();
      return var1.getVersion();
   }

   public String onPlaceholderRequest(Player var1, @NotNull String var2) {
      if (var1 != null && !var2.isEmpty()) {
         PlayerGlowData var3 = this.A.getDatabase().B(var1.getUniqueId());
         if (var2.equals("toggle_state")) {
            if (var3 == null) {
               return "false";
            } else if (var3.getCurrentGlow() == null) {
               return "false";
            } else {
               return var3.getCurrentGlow().getEnable() ? "true" : "false";
            }
         } else {
            CurrentGlow var5;
            if (var2.equals("glow_state")) {
               if (var3 == null) {
                  return "false";
               } else {
                  var5 = var3.getCurrentGlow();
                  return var5.getGlowName().isEmpty() ? "false" : "true";
               }
            } else if (var2.equals("glow_id")) {
               if (var3 == null) {
                  return "";
               } else {
                  var5 = var3.getCurrentGlow();
                  return var5.getGlowName();
               }
            } else if (var2.contains("glow_colors")) {
               if (var3 == null) {
                  return "Loading...";
               } else {
                  List var4 = var3.getCurrentGlow().getColorList();
                  return String.join(", ", var4);
               }
            } else if (var2.contains("glow_color")) {
               if (var3 == null) {
                  return "Loading...";
               } else {
                  return xshyo.us.theglow.B.A.A(var1) == null ? "" : "" + xshyo.us.theglow.B.A.A(var1);
               }
            } else {
               return "";
            }
         }
      } else {
         return "";
      }
   }
}

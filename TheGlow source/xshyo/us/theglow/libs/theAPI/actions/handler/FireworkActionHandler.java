package xshyo.us.theglow.libs.theAPI.actions.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import xshyo.us.theglow.libs.theAPI.TheAPI;
import xshyo.us.theglow.libs.theAPI.actions.ActionHandler;

public class FireworkActionHandler implements ActionHandler {
   public void execute(Player var1, String var2, int var3) {
      if (var1 != null && var1.isOnline()) {
         if (var3 > 0) {
            TheAPI.getInstance().getScheduler().runTaskLater(() -> {
               this.spawnFirework(var1, var2);
            }, (long)var3);
         } else {
            TheAPI.getInstance().getScheduler().runTask(() -> {
               this.spawnFirework(var1, var2);
            });
         }

      }
   }

   private void spawnFirework(Player var1, String var2) {
      try {
         FireworkActionHandler.FireworkOptions var3 = this.parseOptions(var2);
         Firework var4 = (Firework)var1.getWorld().spawn(var1.getLocation(), Firework.class);
         FireworkMeta var5 = var4.getFireworkMeta();
         Builder var6 = FireworkEffect.builder();
         Iterator var7;
         Color var8;
         if (!var3.colors.isEmpty()) {
            var7 = var3.colors.iterator();

            while(var7.hasNext()) {
               var8 = (Color)var7.next();
               var6.withColor(var8);
            }
         } else {
            var6.withColor(Color.RED);
         }

         if (!var3.fadeColors.isEmpty()) {
            var7 = var3.fadeColors.iterator();

            while(var7.hasNext()) {
               var8 = (Color)var7.next();
               var6.withFade(var8);
            }
         }

         if (var3.type != null) {
            var6.with(var3.type);
         } else {
            var6.with(Type.BALL);
         }

         if (var3.flicker) {
            var6.withFlicker();
         }

         if (var3.trail) {
            var6.withTrail();
         }

         FireworkEffect var10 = var6.build();
         var5.addEffect(var10);
         var5.setPower(var3.power);
         var4.setFireworkMeta(var5);
         Logger var10000 = Bukkit.getLogger();
         String var10001 = var1.getName();
         var10000.info("[TheAPI] Spawned firework for player " + var10001 + " with options: " + String.valueOf(var3));
      } catch (Exception var9) {
         Bukkit.getLogger().warning("[TheAPI] Failed to spawn firework: " + var9.getMessage());
         this.spawnDefaultFirework(var1);
      }

   }

   private void spawnDefaultFirework(Player var1) {
      Firework var2 = (Firework)var1.getWorld().spawn(var1.getLocation(), Firework.class);
      FireworkMeta var3 = var2.getFireworkMeta();
      FireworkEffect var4 = FireworkEffect.builder().withColor(Color.RED).withColor(Color.BLUE).with(Type.BALL).build();
      var3.addEffect(var4);
      var3.setPower(1);
      var2.setFireworkMeta(var3);
      Bukkit.getLogger().info("[TheAPI] Spawned default firework for player " + var1.getName());
   }

   private FireworkActionHandler.FireworkOptions parseOptions(String var1) {
      FireworkActionHandler.FireworkOptions var2 = new FireworkActionHandler.FireworkOptions();
      if (var1 != null && !var1.trim().isEmpty()) {
         String[] var3 = var1.split(";");
         String[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            var7 = var7.trim();
            if (var7.startsWith("colors=")) {
               var2.colors = this.parseColors(var7.substring("colors=".length()));
            } else if (var7.startsWith("fade=")) {
               var2.fadeColors = this.parseColors(var7.substring("fade=".length()));
            } else if (var7.startsWith("type=")) {
               String var8 = var7.substring("type=".length()).toUpperCase();

               try {
                  var2.type = Type.valueOf(var8);
               } catch (IllegalArgumentException var11) {
                  Bukkit.getLogger().warning("[TheAPI] Invalid firework type: " + var8);
               }
            } else if (var7.startsWith("power=")) {
               try {
                  int var12 = Integer.parseInt(var7.substring("power=".length()));
                  var2.power = Math.max(0, Math.min(4, var12));
               } catch (NumberFormatException var10) {
                  Bukkit.getLogger().warning("[TheAPI] Invalid power value: " + var7);
               }
            } else if (var7.startsWith("flicker=")) {
               var2.flicker = Boolean.parseBoolean(var7.substring("flicker=".length()));
            } else if (var7.startsWith("trail=")) {
               var2.trail = Boolean.parseBoolean(var7.substring("trail=".length()));
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   private List<Color> parseColors(String var1) {
      ArrayList var2 = new ArrayList();
      String[] var3 = var1.split(",");
      String[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         var7 = var7.trim().toUpperCase();
         if (var7.matches("^\\d+,\\d+,\\d+$")) {
            String[] var14 = var7.split(",");

            try {
               int var9 = Integer.parseInt(var14[0].trim());
               int var15 = Integer.parseInt(var14[1].trim());
               int var11 = Integer.parseInt(var14[2].trim());
               var9 = Math.max(0, Math.min(255, var9));
               var15 = Math.max(0, Math.min(255, var15));
               var11 = Math.max(0, Math.min(255, var11));
               var2.add(Color.fromRGB(var9, var15, var11));
            } catch (NumberFormatException var12) {
               Bukkit.getLogger().warning("[TheAPI] Invalid RGB color: " + var7);
            }
         } else {
            try {
               java.awt.Color var8 = (java.awt.Color)java.awt.Color.class.getField(var7).get((Object)null);
               var2.add(Color.fromRGB(var8.getRed(), var8.getGreen(), var8.getBlue()));
            } catch (Exception var13) {
               byte var10 = -1;
               switch(var7.hashCode()) {
               case -2027972496:
                  if (var7.equals("MAROON")) {
                     var10 = 18;
                  }
                  break;
               case -1955522002:
                  if (var7.equals("ORANGE")) {
                     var10 = 5;
                  }
                  break;
               case -1923613764:
                  if (var7.equals("PURPLE")) {
                     var10 = 4;
                  }
                  break;
               case -1848981747:
                  if (var7.equals("SILVER")) {
                     var10 = 15;
                  }
                  break;
               case -1680910220:
                  if (var7.equals("YELLOW")) {
                     var10 = 3;
                  }
                  break;
               case 81009:
                  if (var7.equals("RED")) {
                     var10 = 0;
                  }
                  break;
               case 2016956:
                  if (var7.equals("AQUA")) {
                     var10 = 10;
                  }
                  break;
               case 2041946:
                  if (var7.equals("BLUE")) {
                     var10 = 1;
                  }
                  break;
               case 2083619:
                  if (var7.equals("CYAN")) {
                     var10 = 11;
                  }
                  break;
               case 2196067:
                  if (var7.equals("GRAY")) {
                     var10 = 8;
                  }
                  break;
               case 2196191:
                  if (var7.equals("GREY")) {
                     var10 = 9;
                  }
                  break;
               case 2336725:
                  if (var7.equals("LIME")) {
                     var10 = 14;
                  }
                  break;
               case 2388918:
                  if (var7.equals("NAVY")) {
                     var10 = 16;
                  }
                  break;
               case 2455926:
                  if (var7.equals("PINK")) {
                     var10 = 12;
                  }
                  break;
               case 2570844:
                  if (var7.equals("TEAL")) {
                     var10 = 19;
                  }
                  break;
               case 63281119:
                  if (var7.equals("BLACK")) {
                     var10 = 6;
                  }
                  break;
               case 68081379:
                  if (var7.equals("GREEN")) {
                     var10 = 2;
                  }
                  break;
               case 75295163:
                  if (var7.equals("OLIVE")) {
                     var10 = 17;
                  }
                  break;
               case 82564105:
                  if (var7.equals("WHITE")) {
                     var10 = 7;
                  }
                  break;
               case 198329015:
                  if (var7.equals("FUCHSIA")) {
                     var10 = 13;
                  }
               }

               switch(var10) {
               case 0:
                  var2.add(Color.RED);
                  break;
               case 1:
                  var2.add(Color.BLUE);
                  break;
               case 2:
                  var2.add(Color.GREEN);
                  break;
               case 3:
                  var2.add(Color.YELLOW);
                  break;
               case 4:
                  var2.add(Color.PURPLE);
                  break;
               case 5:
                  var2.add(Color.ORANGE);
                  break;
               case 6:
                  var2.add(Color.BLACK);
                  break;
               case 7:
                  var2.add(Color.WHITE);
                  break;
               case 8:
               case 9:
                  var2.add(Color.GRAY);
                  break;
               case 10:
               case 11:
                  var2.add(Color.AQUA);
                  break;
               case 12:
               case 13:
                  var2.add(Color.fromRGB(255, 105, 180));
                  break;
               case 14:
                  var2.add(Color.LIME);
                  break;
               case 15:
                  var2.add(Color.SILVER);
                  break;
               case 16:
                  var2.add(Color.NAVY);
                  break;
               case 17:
                  var2.add(Color.OLIVE);
                  break;
               case 18:
                  var2.add(Color.MAROON);
                  break;
               case 19:
                  var2.add(Color.TEAL);
                  break;
               default:
                  Bukkit.getLogger().warning("[TheAPI] Unknown color: " + var7);
               }
            }
         }
      }

      return var2;
   }

   private static class FireworkOptions {
      List<Color> colors = new ArrayList();
      List<Color> fadeColors = new ArrayList();
      Type type;
      int power;
      boolean flicker;
      boolean trail;

      private FireworkOptions() {
         this.type = Type.BALL;
         this.power = 1;
         this.flicker = false;
         this.trail = false;
      }

      public String toString() {
         int var10000 = this.colors.size();
         return "colors=" + var10000 + ", fadeColors=" + this.fadeColors.size() + ", type=" + String.valueOf(this.type) + ", power=" + this.power + ", flicker=" + this.flicker + ", trail=" + this.trail;
      }
   }
}

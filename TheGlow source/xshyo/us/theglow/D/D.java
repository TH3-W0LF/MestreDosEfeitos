package xshyo.us.theglow.D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.CurrentGlow;
import xshyo.us.theglow.data.GlowCacheData;
import xshyo.us.theglow.data.PlayerGlowData;
import xshyo.us.theglow.enums.DebugLevel;
import xshyo.us.theglow.libs.config.block.implementation.Section;

public class D implements Listener {
   private final TheGlow A = TheGlow.getInstance();

   @EventHandler
   public void onEntityPotionEffectChange(EntityPotionEffectEvent var1) {
      if (var1.getEntity() instanceof Player) {
         if (!this.A.getConf().getBoolean("config.glow.check-invisibility")) {
            return;
         }

         Player var2 = (Player)var1.getEntity();
         PlayerGlowData var3 = this.A.getDatabase().B(var2.getUniqueId());
         if (var3 == null) {
            return;
         }

         if (var3.getCurrentGlow().getGlowName().isEmpty()) {
            return;
         }

         if (var1.getNewEffect() != null && var1.getNewEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
            this.A.getGlowManager().A(var2);
         }

         if (var1.getNewEffect() == null && var1.getOldEffect() != null && var1.getOldEffect().getType().equals(PotionEffectType.INVISIBILITY) && var3.getCurrentGlow().getEnable()) {
            this.A.getGlowManager().A(var2, var3.getCurrentGlow().getColorList(), 20L, true);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onCommandPreprocess(PlayerCommandPreprocessEvent var1) {
      String var2 = var1.getMessage();
      if (var2.equalsIgnoreCase("/tab reload")) {
         if (var1.getPlayer().hasPermission("tab.reload") || var1.getPlayer().hasPermission("tab.admin")) {
            this.A.getScheduler().runTaskLater(() -> {
               Iterator var2 = Bukkit.getOnlinePlayers().iterator();

               while(var2.hasNext()) {
                  Player var3 = (Player)var2.next();
                  if (this.A.getGlowManager().B(var1.getPlayer())) {
                     this.A.getPluginIntegrationManager().A(var3, xshyo.us.theglow.B.A.A(var3));
                     xshyo.us.theglow.E.B.A(this.A, "Update " + var3.getName(), DebugLevel.NORMAL);
                  }
               }

            }, 20L);
         }
      }
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent var1) {
      Player var2 = var1.getPlayer();
      this.A.getDatabase().B(var2.getUniqueId(), var2.getName()).thenAccept((var2x) -> {
         PlayerGlowData var3 = this.A.getDatabase().B(var2.getUniqueId());
         if (!var2x) {
            boolean var4 = false;
            if (var3 == null) {
               return;
            }

            this.A.getDatabase().B(var3.getUuid().toString()).thenAccept((var3x) -> {
               if (!var2.getName().equals(var3x)) {
                  this.A.getDatabase().A(var3.getUuid().toString(), var2.getName()).exceptionally((var0) -> {
                     var0.printStackTrace();
                     return null;
                  });
               }

            }).exceptionally((var0) -> {
               var0.printStackTrace();
               return null;
            });
            boolean var5 = false;
            Map var6 = this.A.getGlowLoad().C();
            String var7 = var3.getCurrentGlow().getGlowName();
            if (var6.containsKey(var7)) {
               var5 = true;
               GlowCacheData var8 = (GlowCacheData)var6.get(var7);
               String var9 = var8.getPermission();
               boolean var10 = "none".equals(var9) || var2.hasPermission(var9);
               if (this.A.getConf().getBoolean("config.glow.check-glow-permission") && !var10) {
                  CurrentGlow var11 = var3.getCurrentGlow();
                  var11.setColorList(Collections.emptyList());
                  var11.setGlowName("");
                  var2.setGlowing(false);
                  var4 = true;
               }
            }

            if (this.A.getConf().getBoolean("config.glow.check-glow-exist") && !var5) {
               CurrentGlow var12 = var3.getCurrentGlow();
               var12.setColorList(Collections.emptyList());
               var12.setGlowName("");
               var4 = true;
            }

            if (var3.getCurrentGlow().getEnable()) {
               this.A.getGlowManager().A(var2, var3.getCurrentGlow().getColorList(), 20L, true);
            }

            if (var4) {
               this.A.getDatabase().C(var3.getUuid());
            }

            this.A(var2, var3);
         }

      }).exceptionally((var0) -> {
         var0.printStackTrace();
         return null;
      });
   }

   private void A(Player var1, PlayerGlowData var2) {
      Section var3 = this.A.getConf().getSection("config.glow.auto-glow-on-join");
      if (var3 != null && var3.getBoolean("enabled")) {
         boolean var4 = var3.getBoolean("override-existing");
         CurrentGlow var5 = var2.getCurrentGlow();
         boolean var6 = var5.getEnable() && var5.getGlowName() != null && !var5.getGlowName().trim().isEmpty() && var5.getColorList() != null && !var5.getColorList().isEmpty();
         if (var6 && !var4) {
            if (this.A.getConf().getBoolean("config.debug")) {
               this.A.getLogger().info("Player " + var1.getName() + " already has active glow '" + var5.getGlowName() + "' and override is disabled.");
            }

         } else {
            if (!var6 && var5.getEnable() && this.A.getConf().getBoolean("config.debug")) {
               this.A.getLogger().info("Cleaning corrupted glow data for player " + var1.getName() + " (enabled=true but no valid glow data)");
            }

            Section var7 = var3.getSection("groups");
            if (var7 != null) {
               Map var8 = this.A.getGlowLoad().C();
               ArrayList var9 = new ArrayList();
               Iterator var10 = var7.getKeys().iterator();

               while(var10.hasNext()) {
                  Object var11 = var10.next();
                  Section var12 = var7.getSection(var11.toString());
                  String var13 = var12.getString("permission");
                  String var14 = var12.getString("glow");
                  int var15 = var12.getInt("priority", 0);
                  if (var13 != null && var1.hasPermission(var13) && var14 != null) {
                     if ("none".equalsIgnoreCase(var14)) {
                        var9.add(new D._A(var11.toString(), var14, (GlowCacheData)null, var15));
                     } else if (var8.containsKey(var14)) {
                        GlowCacheData var16 = (GlowCacheData)var8.get(var14);
                        var9.add(new D._A(var11.toString(), var14, var16, var15));
                     } else {
                        this.A.getLogger().warning("Auto-glow ID '" + var14 + "' for group '" + var11 + "' not found in cached glows. Player: " + var1.getName());
                     }
                  }
               }

               if (var9.isEmpty()) {
                  if (this.A.getConf().getBoolean("config.debug")) {
                     this.A.getLogger().info("No valid auto-glow groups found for player " + var1.getName());
                  }

               } else {
                  var9.sort((var0, var1x) -> {
                     return Integer.compare(var1x.B, var0.B);
                  });
                  D._A var17 = (D._A)var9.get(0);
                  if ("none".equalsIgnoreCase(var17.A)) {
                     if (this.A.getConf().getBoolean("config.debug")) {
                        this.A.getLogger().info("Top priority auto-glow is 'none' for group '" + var17 + "', skipping glow application for player " + var1.getName());
                     }

                  } else {
                     if (this.A.getConf().getBoolean("config.debug")) {
                        if (var6) {
                           this.A.getLogger().info("Overriding existing glow '" + var5.getGlowName() + "' with auto-glow '" + var17.A + "' (priority: " + var17.B + ") for player " + var1.getName());
                        } else {
                           this.A.getLogger().info("Applying auto-glow '" + var17.A + "' (priority: " + var17.B + ") to player " + var1.getName() + " (no active glow found)");
                        }

                        if (var9.size() > 1) {
                           StringBuilder var18 = new StringBuilder("Other valid groups: ");

                           for(int var20 = 1; var20 < var9.size(); ++var20) {
                              D._A var21 = (D._A)var9.get(var20);
                              var18.append(var21.A).append("(").append(var21.B).append(")");
                              if (var20 < var9.size() - 1) {
                                 var18.append(", ");
                              }
                           }

                           this.A.getLogger().info(var18.toString());
                        }
                     }

                     List var19 = var17.C.getPatterns();
                     var5.setEnable(true);
                     var5.setGlowName(var17.A);
                     var5.setColorList(var19);
                     this.A.getGlowManager().A(var1, var19, 20L, true);
                     this.A.getDatabase().C(var2.getUuid());
                     if (!this.A.getConf().getBoolean("config.debug")) {
                        this.A.getLogger().info("Applied auto-glow '" + var17.A + "' (priority: " + var17.B + ") to player " + var1.getName());
                     }

                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.A.getGlowManager().B(var2)) {
         this.A.getGlowManager().A(var2);
      }

      this.A.getDatabase().A(var2.getUniqueId());
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onWorldChange(PlayerChangedWorldEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerGlowData var3 = this.A.getDatabase().B(var2.getUniqueId());
      if (var3 != null) {
         if (!var3.getCurrentGlow().getGlowName().isEmpty()) {
            if (var3.getCurrentGlow().getEnable()) {
               this.A.getGlowManager().A(var2, var3.getCurrentGlow().getColorList(), 20L, true);
            }

         }
      }
   }

   @EventHandler
   public void onGamemodeChange(PlayerGameModeChangeEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerGlowData var3 = this.A.getDatabase().B(var2.getUniqueId());
      if (var3 != null) {
         if (!var3.getCurrentGlow().getGlowName().isEmpty()) {
            if (var1.getNewGameMode().equals(GameMode.SPECTATOR)) {
               this.A.getGlowManager().A(var2);
            } else if (var3.getCurrentGlow().getEnable()) {
               this.A.getGlowManager().A(var2, var3.getCurrentGlow().getColorList(), 20L, true);
            }

         }
      }
   }

   private static class _A {
      final String D;
      final String A;
      final GlowCacheData C;
      final int B;

      _A(String var1, String var2, GlowCacheData var3, int var4) {
         this.D = var1;
         this.A = var2;
         this.C = var3;
         this.B = var4;
      }
   }
}

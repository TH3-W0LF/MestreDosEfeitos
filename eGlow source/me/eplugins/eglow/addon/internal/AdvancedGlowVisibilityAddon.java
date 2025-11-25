package me.eplugins.eglow.addon.internal;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.addon.internal.raytrace.FoliaRaytrace;
import me.eplugins.eglow.addon.internal.raytrace.Raytrace;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class AdvancedGlowVisibilityAddon {
   private final Object bukkitTask;
   private final Set<Material> ignoredBlocks = EnumSet.noneOf(Material.class);
   private final Map<UUID, Location> cache = Collections.synchronizedMap(new HashMap());

   public AdvancedGlowVisibilityAddon() {
      InputStream resource = EGlow.getInstance().getResource("internal/advanced-visibility-ignored-blocks.yml");
      YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader((InputStream)Objects.requireNonNull(resource)));
      List<Material> materials = (List)config.getStringList("ignored-blocks").stream().map(Material::matchMaterial).filter(Objects::nonNull).collect(Collectors.toList());
      this.getIgnoredBlocks().addAll(materials);
      this.bukkitTask = NMSHook.scheduleTimerTask(true, 1L, (long)Math.max(EGlowMainConfig.MainConfig.ADVANCED_GLOW_VISIBILITY_DELAY.getInt(), 10), () -> {
         Collection<EGlowPlayer> eGlowPlayers = DataManager.getEGlowPlayers();
         List<BiPair<UUID, UUID>> checkedPlayers = new ArrayList(eGlowPlayers.size());
         Iterator var3 = eGlowPlayers.iterator();

         label74:
         while(true) {
            EGlowPlayer eGlowPlayer;
            Player player;
            Location playerLoc;
            boolean playerIsGlowing;
            do {
               if (!var3.hasNext()) {
                  return;
               }

               eGlowPlayer = (EGlowPlayer)var3.next();
               player = eGlowPlayer.getPlayer();
               playerLoc = player.getEyeLocation();
               playerIsGlowing = eGlowPlayer.isGlowing();
            } while(this.checkLocationCache(eGlowPlayer, playerLoc));

            Iterator var8 = ((World)Objects.requireNonNull(playerLoc.getWorld())).getPlayers().iterator();

            while(true) {
               while(true) {
                  EGlowPlayer ePlayerNearby;
                  BiPair pair;
                  do {
                     boolean nearbyIsGlowing;
                     do {
                        do {
                           Player player1;
                           do {
                              do {
                                 do {
                                    if (!var8.hasNext()) {
                                       continue label74;
                                    }

                                    player1 = (Player)var8.next();
                                 } while(player1 == player);
                              } while(this.distance(player1.getEyeLocation(), playerLoc) >= 50);
                           } while(!player1.getWorld().equals(playerLoc.getWorld()));

                           ePlayerNearby = DataManager.getEGlowPlayer(player1);
                        } while(ePlayerNearby == null);

                        nearbyIsGlowing = ePlayerNearby.isGlowing();
                     } while(!playerIsGlowing && !nearbyIsGlowing);

                     pair = new BiPair(eGlowPlayer.getUuid(), ePlayerNearby.getUuid());
                  } while(checkedPlayers.contains(pair));

                  Location nearbyLoc = ePlayerNearby.getPlayer().getEyeLocation();
                  if (this.isOutsideView(playerLoc, nearbyLoc) && this.isOutsideView(nearbyLoc, playerLoc)) {
                     this.toggleGlow(eGlowPlayer, ePlayerNearby, false);
                  } else {
                     if (!NMSHook.nms.isFolia) {
                        Raytrace trace = new Raytrace(playerLoc, nearbyLoc);
                        boolean hasLineOfSight = trace.hasLineOfSight();
                        this.toggleGlow(eGlowPlayer, ePlayerNearby, hasLineOfSight);
                     } else {
                        FoliaRaytrace foliaRaytrace = new FoliaRaytrace(playerLoc, nearbyLoc);
                        foliaRaytrace.hasLineOfSightAsync((result) -> {
                           this.toggleGlow(eGlowPlayer, ePlayerNearby, result);
                        });
                     }

                     checkedPlayers.add(pair);
                  }
               }
            }
         }
      });
   }

   private boolean isOutsideView(Location location1, Location location2) {
      Vector vector = location1.toVector().subtract(location2.toVector());
      Vector direction = location1.getDirection();
      double delta = vector.dot(direction);
      return delta > 0.0D;
   }

   private boolean checkLocationCache(EGlowPlayer eGlowPlayer, Location playerLocation) {
      UUID uuid = eGlowPlayer.getUuid();
      Location cached = (Location)this.getCache().get(uuid);
      if (cached == null) {
         this.getCache().put(uuid, playerLocation);
      } else {
         if (cached.equals(playerLocation)) {
            return true;
         }

         this.getCache().replace(uuid, playerLocation);
      }

      return false;
   }

   private void toggleGlow(EGlowPlayer eGlowPlayer1, EGlowPlayer eGlowPlayer2, boolean toggle) {
      if (toggle) {
         eGlowPlayer1.addGlowTarget(eGlowPlayer2.getPlayer());
         eGlowPlayer2.addGlowTarget(eGlowPlayer1.getPlayer());
      } else {
         eGlowPlayer1.removeGlowTarget(eGlowPlayer2.getPlayer());
         eGlowPlayer2.removeGlowTarget(eGlowPlayer1.getPlayer());
      }

   }

   public void uncachePlayer(UUID uuid) {
      this.getCache().remove(uuid);
   }

   private int distance(Location start, Location end) {
      return (int)Math.floor(Math.sqrt(Math.pow(start.getX() - end.getX(), 2.0D) + Math.pow(start.getY() - end.getY(), 2.0D) + Math.pow(start.getZ() - end.getZ(), 2.0D)));
   }

   public void shutdown() {
      if (this.getBukkitTask() != null) {
         if (this.getBukkitTask() instanceof BukkitTask) {
            BukkitTask task = (BukkitTask)this.getBukkitTask();
            task.cancel();
         } else {
            try {
               NMSHook.nms.cancelScheduledTask.invoke(this.getBukkitTask());
            } catch (Exception var2) {
               ChatUtil.printException("Failed to cancel advanced glow visibility addon", var2);
            }
         }
      }

      EGlow.getInstance().setAdvancedGlowVisibilityAddon((AdvancedGlowVisibilityAddon)null);
   }

   @Generated
   public Object getBukkitTask() {
      return this.bukkitTask;
   }

   @Generated
   public Set<Material> getIgnoredBlocks() {
      return this.ignoredBlocks;
   }

   @Generated
   public Map<UUID, Location> getCache() {
      return this.cache;
   }
}

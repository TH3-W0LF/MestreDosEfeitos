package me.eplugins.eglow.addon.internal.raytrace;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class FoliaRaytrace {
   private final Location origin;
   private final Location target;
   private final Vector direction;

   public FoliaRaytrace(Location origin, Location target) {
      this.origin = origin;
      this.target = target;
      this.direction = target.clone().toVector().subtract(origin.toVector()).normalize();
   }

   public void hasLineOfSightAsync(final Consumer<Boolean> resultCallback) {
      final int distance = this.distance();
      if (distance <= 1) {
         resultCallback.accept(true);
      } else {
         final World world = this.getOrigin().getWorld();
         final AtomicInteger step = new AtomicInteger(1);
         Runnable raytraceStep = new Runnable() {
            public void run() {
               if (step.get() > Math.min(distance, 50)) {
                  resultCallback.accept(true);
               } else {
                  Vector offset = FoliaRaytrace.this.getDirection().clone().multiply(step.getAndIncrement());
                  Location newLocation = FoliaRaytrace.this.getOrigin().clone().add(offset);
                  int x = newLocation.getBlockX();
                  int y = newLocation.getBlockY();
                  int z = newLocation.getBlockZ();
                  int chunkX = x >> 4;
                  int chunkZ = z >> 4;
                  NMSHook.scheduleRegionTask(world, chunkX, chunkZ, () -> {
                     Block block = world.getBlockAt(x, y, z);
                     if (!block.isPassable() && !block.isLiquid() && !EGlow.getInstance().getAdvancedGlowVisibilityAddon().getIgnoredBlocks().contains(block.getType())) {
                        resultCallback.accept(false);
                     } else {
                        this.run();
                     }

                  });
               }
            }
         };
         raytraceStep.run();
      }
   }

   private int distance() {
      return (int)Math.floor(Math.sqrt(Math.pow(this.getOrigin().getX() - this.getTarget().getX(), 2.0D) + Math.pow(this.getOrigin().getY() - this.getTarget().getY(), 2.0D) + Math.pow(this.getOrigin().getZ() - this.getTarget().getZ(), 2.0D)));
   }

   @Generated
   public Location getOrigin() {
      return this.origin;
   }

   @Generated
   public Location getTarget() {
      return this.target;
   }

   @Generated
   public Vector getDirection() {
      return this.direction;
   }
}

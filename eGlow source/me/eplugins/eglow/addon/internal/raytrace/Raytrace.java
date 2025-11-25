package me.eplugins.eglow.addon.internal.raytrace;

import java.util.Objects;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class Raytrace {
   private final Location origin;
   private final Location target;
   private final Vector direction;

   public Raytrace(Location origin, Location target) {
      this.origin = origin;
      this.target = target;
      this.direction = target.clone().toVector().subtract(origin.clone().toVector()).normalize();
   }

   public boolean hasLineOfSight() {
      int distance = this.distance();
      if (this.distance() <= 1) {
         return true;
      } else {
         BlockIterator blocks = new BlockIterator((World)Objects.requireNonNull(this.getOrigin().getWorld()), this.getOrigin().toVector(), this.getDirection(), Math.min(distance, 50));

         Block block;
         do {
            if (!blocks.hasNext()) {
               return true;
            }

            block = blocks.next();
         } while(block.isLiquid() || block.isPassable() || EGlow.getInstance().getAdvancedGlowVisibilityAddon().getIgnoredBlocks().contains(block.getType()));

         return false;
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

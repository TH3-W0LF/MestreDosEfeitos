package me.eplugins.eglow.addon.internal.raytrace;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class BlockIterator implements Iterator<Block> {
   private final int maxDistance;
   private static final int gridSize = 16777216;
   private final Block[] blockQueue = new Block[3];
   private int currentBlock = 0;
   private int currentDistance;
   private int maxDistanceInt;
   private int secondError;
   private int thirdError;
   private final int secondStep;
   private final int thirdStep;
   private BlockFace mainFace;
   private BlockFace secondFace;
   private BlockFace thirdFace;

   public BlockIterator(World world, Vector start, Vector direction, int maxDistance) {
      this.maxDistance = maxDistance;
      this.currentDistance = 0;
      double mainDirection = 0.0D;
      double secondDirection = 0.0D;
      double thirdDirection = 0.0D;
      double mainPosition = 0.0D;
      double secondPosition = 0.0D;
      double thirdPosition = 0.0D;
      Block startBlock = world.getBlockAt((int)Math.floor(start.getX()), (int)Math.floor(start.getY()), (int)Math.floor(start.getZ()));
      if (this.getXLength(direction) > mainDirection) {
         this.mainFace = this.getXFace(direction);
         mainDirection = this.getXLength(direction);
         mainPosition = this.getXPosition(direction, start, startBlock);
         this.secondFace = this.getYFace(direction);
         secondDirection = this.getYLength(direction);
         secondPosition = this.getYPosition(direction, start, startBlock);
         this.thirdFace = this.getZFace(direction);
         thirdDirection = this.getZLength(direction);
         thirdPosition = this.getZPosition(direction, start, startBlock);
      }

      if (this.getYLength(direction) > mainDirection) {
         this.mainFace = this.getYFace(direction);
         mainDirection = this.getYLength(direction);
         mainPosition = this.getYPosition(direction, start, startBlock);
         this.secondFace = this.getZFace(direction);
         secondDirection = this.getZLength(direction);
         secondPosition = this.getZPosition(direction, start, startBlock);
         this.thirdFace = this.getXFace(direction);
         thirdDirection = this.getXLength(direction);
         thirdPosition = this.getXPosition(direction, start, startBlock);
      }

      if (this.getZLength(direction) > mainDirection) {
         this.mainFace = this.getZFace(direction);
         mainDirection = this.getZLength(direction);
         mainPosition = this.getZPosition(direction, start, startBlock);
         this.secondFace = this.getXFace(direction);
         secondDirection = this.getXLength(direction);
         secondPosition = this.getXPosition(direction, start, startBlock);
         this.thirdFace = this.getYFace(direction);
         thirdDirection = this.getYLength(direction);
         thirdPosition = this.getYPosition(direction, start, startBlock);
      }

      double d = mainPosition / mainDirection;
      double secondd = secondPosition - secondDirection * d;
      double thirdd = thirdPosition - thirdDirection * d;
      this.secondError = NumberConversions.floor(secondd * 1.6777216E7D);
      this.secondStep = NumberConversions.round(secondDirection / mainDirection * 1.6777216E7D);
      this.thirdError = NumberConversions.floor(thirdd * 1.6777216E7D);
      this.thirdStep = NumberConversions.round(thirdDirection / mainDirection * 1.6777216E7D);
      if (this.secondError + this.secondStep <= 0) {
         this.secondError = -this.secondStep + 1;
      }

      if (this.thirdError + this.thirdStep <= 0) {
         this.thirdError = -this.thirdStep + 1;
      }

      Block lastBlock = startBlock.getRelative(this.mainFace.getOppositeFace());
      if (this.secondError < 0) {
         this.secondError += 16777216;
         lastBlock = lastBlock.getRelative(this.secondFace.getOppositeFace());
      }

      if (this.thirdError < 0) {
         this.thirdError += 16777216;
         lastBlock = lastBlock.getRelative(this.thirdFace.getOppositeFace());
      }

      this.secondError -= 16777216;
      this.thirdError -= 16777216;
      this.blockQueue[0] = lastBlock;
      this.currentBlock = -1;
      this.scan();
      boolean startBlockFound = false;

      for(int cnt = this.currentBlock; cnt >= 0; --cnt) {
         if (this.blockEquals(this.blockQueue[cnt], startBlock)) {
            this.currentBlock = cnt;
            startBlockFound = true;
            break;
         }
      }

      if (startBlockFound) {
         this.maxDistanceInt = NumberConversions.round((double)maxDistance / Math.sqrt(mainDirection * mainDirection + secondDirection * secondDirection + thirdDirection * thirdDirection) / mainDirection);
      }
   }

   private boolean blockEquals(Block a, Block b) {
      return a.getLocation().equals(b.getLocation());
   }

   private BlockFace getXFace(Vector direction) {
      return direction.getX() > 0.0D ? BlockFace.EAST : BlockFace.WEST;
   }

   private BlockFace getYFace(Vector direction) {
      return direction.getY() > 0.0D ? BlockFace.UP : BlockFace.DOWN;
   }

   private BlockFace getZFace(Vector direction) {
      return direction.getZ() > 0.0D ? BlockFace.SOUTH : BlockFace.NORTH;
   }

   private double getXLength(Vector direction) {
      return Math.abs(direction.getX());
   }

   private double getYLength(Vector direction) {
      return Math.abs(direction.getY());
   }

   private double getZLength(Vector direction) {
      return Math.abs(direction.getZ());
   }

   private double getPosition(double direction, double position, int blockPosition) {
      return direction > 0.0D ? position - (double)blockPosition : (double)(blockPosition + 1) - position;
   }

   private double getXPosition(Vector direction, Vector position, Block block) {
      return this.getPosition(direction.getX(), position.getX(), block.getX());
   }

   private double getYPosition(Vector direction, Vector position, Block block) {
      return this.getPosition(direction.getY(), position.getY(), block.getY());
   }

   private double getZPosition(Vector direction, Vector position, Block block) {
      return this.getPosition(direction.getZ(), position.getZ(), block.getZ());
   }

   public boolean hasNext() {
      this.scan();
      return this.currentBlock != -1;
   }

   public Block next() throws NoSuchElementException {
      this.scan();
      if (this.currentBlock <= -1) {
         throw new NoSuchElementException();
      } else {
         return this.blockQueue[this.currentBlock--];
      }
   }

   private void scan() {
      if (this.currentBlock < 0) {
         if (this.maxDistance == 0 || this.currentDistance <= this.maxDistanceInt) {
            ++this.currentDistance;
            this.secondError += this.secondStep;
            this.thirdError += this.thirdStep;
            if (this.secondError > 0 && this.thirdError > 0) {
               this.blockQueue[2] = this.blockQueue[0].getRelative(this.mainFace);
               if (this.secondStep * this.thirdError < this.thirdStep * this.secondError) {
                  this.blockQueue[1] = this.blockQueue[2].getRelative(this.secondFace);
                  this.blockQueue[0] = this.blockQueue[1].getRelative(this.thirdFace);
               } else {
                  this.blockQueue[1] = this.blockQueue[2].getRelative(this.thirdFace);
                  this.blockQueue[0] = this.blockQueue[1].getRelative(this.secondFace);
               }

               this.thirdError -= 16777216;
               this.secondError -= 16777216;
               this.currentBlock = 2;
            } else if (this.secondError > 0) {
               this.blockQueue[1] = this.blockQueue[0].getRelative(this.mainFace);
               this.blockQueue[0] = this.blockQueue[1].getRelative(this.secondFace);
               this.secondError -= 16777216;
               this.currentBlock = 1;
            } else if (this.thirdError > 0) {
               this.blockQueue[1] = this.blockQueue[0].getRelative(this.mainFace);
               this.blockQueue[0] = this.blockQueue[1].getRelative(this.thirdFace);
               this.thirdError -= 16777216;
               this.currentBlock = 1;
            } else {
               this.blockQueue[0] = this.blockQueue[0].getRelative(this.mainFace);
               this.currentBlock = 0;
            }
         }
      }
   }
}

package xshyo.us.theglow.D;

import dev.geco.gsit.api.event.EntityStopSitEvent;
import dev.geco.gsit.api.event.PreEntitySitEvent;
import dev.geco.gsit.api.event.PrePlayerCrawlEvent;
import dev.geco.gsit.api.event.PrePlayerPlayerSitEvent;
import dev.geco.gsit.api.event.PrePlayerPoseEvent;
import dev.geco.gsit.api.event.PrePlayerStopCrawlEvent;
import dev.geco.gsit.api.event.PrePlayerStopPlayerSitEvent;
import dev.geco.gsit.api.event.PrePlayerStopPoseEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.PlayerGlowData;

public class A implements Listener {
   private final TheGlow A = TheGlow.getInstance();

   private PlayerGlowData A(Player var1) {
      PlayerGlowData var2 = this.A.getDatabase().B(var1.getUniqueId());
      return var2 != null && !var2.getCurrentGlow().getGlowName().isEmpty() ? var2 : null;
   }

   private void B(Player var1) {
      this.A.getGlowManager().A(var1);
   }

   private void A(Player var1, PlayerGlowData var2) {
      this.A.getGlowManager().A(var1, var2.getCurrentGlow().getColorList(), 20L, true);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPreEntitySit(PreEntitySitEvent var1) {
      if (var1.getEntity() instanceof Player) {
         Player var2 = (Player)var1.getEntity();
         if (this.A(var2) != null) {
            this.B(var2);
         }
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPreEntityGetUpSit(EntityStopSitEvent var1) {
      if (var1.getEntity() instanceof Player) {
         Player var2 = (Player)var1.getEntity();
         PlayerGlowData var3 = this.A(var2);
         if (var3 != null) {
            this.A(var2, var3);
         }
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPrePlayerPose(PrePlayerPoseEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.A(var2) != null) {
         this.B(var2);
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPrePlayerGetUpPose(PrePlayerStopPoseEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerGlowData var3 = this.A(var2);
      if (var3 != null) {
         this.A(var2, var3);
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPrePlayerSit(PrePlayerPlayerSitEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.A(var2) != null) {
         this.B(var2);
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPrePlayerGetUpSit(PrePlayerStopPlayerSitEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerGlowData var3 = this.A(var2);
      if (var3 != null) {
         this.A(var2, var3);
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPrePlayerCrawl(PrePlayerCrawlEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.A(var2) != null) {
         this.B(var2);
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPrePlayerGetUpCrawl(PrePlayerStopCrawlEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerGlowData var3 = this.A(var2);
      if (var3 != null) {
         this.A(var2, var3);
      }

   }
}

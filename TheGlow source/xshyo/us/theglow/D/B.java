package xshyo.us.theglow.D;

import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.events.UndisguiseEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.PlayerGlowData;

public class B implements Listener {
   private final TheGlow A = TheGlow.getInstance();

   private PlayerGlowData B(Player var1) {
      PlayerGlowData var2 = this.A.getDatabase().B(var1.getUniqueId());
      return var2 != null && !var2.getCurrentGlow().getGlowName().isEmpty() ? var2 : null;
   }

   private void A(Player var1) {
      this.A.getGlowManager().A(var1);
   }

   private void A(Player var1, PlayerGlowData var2) {
      this.A.getGlowManager().A(var1, var2.getCurrentGlow().getColorList(), 20L, true);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void DisguiseEvent(DisguiseEvent var1) {
      if (var1.getEntity() instanceof Player) {
         Player var2 = (Player)var1.getEntity();
         if (this.B(var2) != null) {
            this.A(var2);
         }
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void disguiseEvent(UndisguiseEvent var1) {
      if (var1.getEntity() instanceof Player) {
         Player var2 = (Player)var1.getEntity();
         PlayerGlowData var3 = this.A.getDatabase().B(var2.getUniqueId());
         if (var3 == null) {
            return;
         }

         if (var3.getCurrentGlow().getGlowName().isEmpty()) {
            return;
         }

         this.A.getGlowManager().A(var2, var3.getCurrentGlow().getColorList(), 20L, false);
      }

   }
}

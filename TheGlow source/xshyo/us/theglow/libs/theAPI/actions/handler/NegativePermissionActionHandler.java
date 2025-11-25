package xshyo.us.theglow.libs.theAPI.actions.handler;

import java.util.logging.Logger;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import xshyo.us.theglow.libs.theAPI.TheAPI;
import xshyo.us.theglow.libs.theAPI.actions.ActionHandler;

public class NegativePermissionActionHandler implements ActionHandler {
   public void execute(Player var1, String var2, int var3) {
      if (var1 != null && var1.isOnline()) {
         if (!this.isVaultAvailable()) {
            Bukkit.getLogger().warning("[TheAPI] Could not execute remove permission action: Vault is not available");
         } else {
            if (var3 > 0) {
               TheAPI.getInstance().getScheduler().runTaskLater(() -> {
                  this.removePermission(var1, var2);
               }, (long)var3);
            } else {
               TheAPI.getInstance().getScheduler().runTask(() -> {
                  this.removePermission(var1, var2);
               });
            }

         }
      }
   }

   private void removePermission(Player var1, String var2) {
      Permission var3 = this.getVaultPermission();
      if (var3 != null) {
         Bukkit.getLogger().info("[TheAPI] Attempting to remove permission '" + var2 + "' from player " + var1.getName());
         boolean var4 = var3.playerHas(var1, var2);
         Logger var10000;
         String var10001;
         if (var4) {
            var10000 = Bukkit.getLogger();
            var10001 = var1.getName();
            var10000.info("[TheAPI] Player " + var10001 + " had permission '" + var2 + "' before removing it");
         } else {
            var10000 = Bukkit.getLogger();
            var10001 = var1.getName();
            var10000.info("[TheAPI] Player " + var10001 + " did NOT have permission '" + var2 + "' before attempting to remove it");
         }

         boolean var5 = var3.playerRemove(var1, var2);
         if (var5) {
            Bukkit.getLogger().info("[TheAPI] Permission '" + var2 + "' successfully removed from player " + var1.getName());
         } else {
            Bukkit.getLogger().warning("[TheAPI] Could not remove permission '" + var2 + "' from player " + var1.getName() + ". Operation failed.");
         }

         if (!var3.playerHas(var1, var2)) {
            var10000 = Bukkit.getLogger();
            var10001 = var1.getName();
            var10000.info("[TheAPI] Verified: Player " + var10001 + " no longer has permission '" + var2 + "'");
         } else {
            var10000 = Bukkit.getLogger();
            var10001 = var1.getName();
            var10000.warning("[TheAPI] Verification failed: Player " + var10001 + " still has permission '" + var2 + "' after attempting to remove it");
         }

         Bukkit.getLogger().info("[TheAPI] Permission provider used: " + var3.getName());
      } else {
         Bukkit.getLogger().warning("[TheAPI] Could not remove permission: Vault is not available");
      }

   }

   private Permission getVaultPermission() {
      RegisteredServiceProvider var1 = Bukkit.getServicesManager().getRegistration(Permission.class);
      return var1 != null ? (Permission)var1.getProvider() : null;
   }

   private boolean isVaultAvailable() {
      Plugin var1 = Bukkit.getPluginManager().getPlugin("Vault");
      if (var1 != null && var1.isEnabled()) {
         RegisteredServiceProvider var2 = Bukkit.getServicesManager().getRegistration(Permission.class);
         if (var2 != null && var2.getProvider() != null) {
            Bukkit.getLogger().info("[TheAPI] Vault available with provider: " + ((Permission)var2.getProvider()).getName());
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}

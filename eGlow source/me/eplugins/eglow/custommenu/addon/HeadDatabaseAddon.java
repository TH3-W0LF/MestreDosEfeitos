package me.eplugins.eglow.custommenu.addon;

import lombok.Generated;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.inventory.ItemStack;

public class HeadDatabaseAddon {
   private final HeadDatabaseAPI api = new HeadDatabaseAPI();

   public ItemStack getHeadDatabaseSkull(String id) {
      try {
         return this.getApi().getItemHead(id);
      } catch (NullPointerException var3) {
         return null;
      }
   }

   @Generated
   public HeadDatabaseAPI getApi() {
      return this.api;
   }
}

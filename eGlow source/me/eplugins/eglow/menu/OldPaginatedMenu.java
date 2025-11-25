package me.eplugins.eglow.menu;

import java.util.Objects;
import lombok.Generated;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class OldPaginatedMenu extends OldMenu {
   protected int page = 1;
   protected boolean hasNextPage = false;
   protected final int maxItemsPerPage = 26;

   public OldPaginatedMenu(Player player) {
      super(player);
   }

   public boolean hasNextPage() {
      return this.hasNextPage;
   }

   public void UpdateMainEffectsNavigationBar(EGlowPlayer p) {
      if (EGlowMainConfig.MainConfig.SETTINGS_GUI_ADD_GLASS_PANES.getBoolean()) {
         this.getInventory().setItem(27, this.createItem(Material.valueOf(this.GLASS_PANE), "&f", 5, new String[]{""}));
         this.getInventory().setItem(30, this.createItem(Material.valueOf(this.GLASS_PANE), "&f", 5, new String[]{""}));
         this.getInventory().setItem(31, this.createItem(Material.valueOf(this.GLASS_PANE), "&f", 5, new String[]{""}));
         this.getInventory().setItem(32, this.createItem(Material.valueOf(this.GLASS_PANE), "&f", 5, new String[]{""}));
         this.getInventory().setItem(35, this.createItem(Material.valueOf(this.GLASS_PANE), "&f", 5, new String[]{""}));
      }

      this.getInventory().setItem(28, this.createPlayerSkull(p));
      this.getInventory().setItem(29, this.createGlowingStatus(p));
      this.getInventory().setItem(33, this.createItem(ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 14 ? Material.valueOf("OAK_SIGN") : Material.valueOf("SIGN"), EGlowMessageConfig.Message.GUI_PREVIOUS_PAGE.get(), 0, new String[]{EGlowMessageConfig.Message.GUI_PAGE_LORE.get(this.page == 1 ? EGlowMessageConfig.Message.GUI_MAIN_MENU.get() : String.valueOf(this.page - 1))}));
      this.getInventory().setItem(34, this.createItem(ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 14 ? Material.valueOf("OAK_SIGN") : Material.valueOf("SIGN"), EGlowMessageConfig.Message.GUI_NEXT_PAGE.get(), 0, new String[]{EGlowMessageConfig.Message.GUI_PAGE_LORE.get(!this.hasNextPage() ? EGlowMessageConfig.Message.GUI_NOT_AVAILABLE.get() : String.valueOf(this.page + 1))}));
   }

   @Generated
   public int getPage() {
      return this.page;
   }

   @Generated
   public boolean isHasNextPage() {
      return this.hasNextPage;
   }

   @Generated
   public void setHasNextPage(boolean hasNextPage) {
      this.hasNextPage = hasNextPage;
   }

   @Generated
   public int getMaxItemsPerPage() {
      Objects.requireNonNull(this);
      return 26;
   }
}

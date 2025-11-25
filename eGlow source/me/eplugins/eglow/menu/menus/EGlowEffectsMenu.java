package me.eplugins.eglow.menu.menus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import me.eplugins.eglow.config.EGlowCustomEffectsConfig;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowEffect;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.menu.OldPaginatedMenu;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class EGlowEffectsMenu extends OldPaginatedMenu {
   private ConcurrentHashMap<Integer, String> effects = new ConcurrentHashMap();

   public EGlowEffectsMenu(Player player) {
      super(player);
   }

   public String getMenuName() {
      return ChatUtil.translateColors(EGlowMainConfig.MainConfig.SETTINGS_GUI_ADD_PREFIX.getBoolean() ? EGlowMessageConfig.Message.GUI_TITLE.get() : EGlowMessageConfig.Message.PREFIX.get() + EGlowMessageConfig.Message.GUI_TITLE.get());
   }

   public void handleMenu(InventoryClickEvent event) {
      Player player = (Player)event.getWhoClicked();
      EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
      int clickedSlot = event.getSlot();
      if (System.currentTimeMillis() - this.getMenuMetadata().getLastClicked() < EGlowMainConfig.MainConfig.SETTINGS_GUIS_INTERACTION_DELAY.getLong()) {
         ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.GUI_COOLDOWN.get());
      } else {
         this.getMenuMetadata().setLastClicked(System.currentTimeMillis());
         switch(clickedSlot) {
         case 28:
            if (eGlowPlayer.skipSaveData()) {
               eGlowPlayer.setSaveData(true);
            }

            eGlowPlayer.setGlowOnJoin(!eGlowPlayer.isGlowOnJoin());
            break;
         case 29:
            if (eGlowPlayer.getPlayer().hasPermission("eglow.command.toggle")) {
               if (eGlowPlayer.isGlowing()) {
                  eGlowPlayer.disableGlow(false);
                  ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.DISABLE_GLOW.get());
                  break;
               }

               if (eGlowPlayer.getGlowEffect() != null && !eGlowPlayer.getGlowEffect().getName().equals("none")) {
                  switch(eGlowPlayer.getGlowDisableReason()) {
                  case BLOCKEDWORLD:
                     ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.WORLD_BLOCKED.get());
                     return;
                  case INVISIBLE:
                     ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.INVISIBILITY_BLOCKED.get());
                     return;
                  case ANIMATION:
                     ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.ANIMATION_BLOCKED.get());
                     return;
                  }

                  EGlowEffect currentEGlowEffect = eGlowPlayer.getGlowEffect();
                  if (!eGlowPlayer.hasPermission(currentEGlowEffect.getPermissionNode()) && (!DataManager.isCustomEffect(currentEGlowEffect.getName()) || !eGlowPlayer.hasPermission("eglow.egloweffect.*")) && !eGlowPlayer.isForcedGlow(currentEGlowEffect)) {
                     ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_PERMISSION.get());
                     return;
                  }

                  eGlowPlayer.activateGlow();
                  ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NEW_GLOW.get(eGlowPlayer.getLastGlowName()));
                  break;
               }

               ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_LAST_GLOW.get());
               return;
            }

            ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_PERMISSION.get());
            break;
         case 30:
         case 31:
         case 32:
         default:
            if (this.getEffects().containsKey(clickedSlot)) {
               String effect = (String)this.getEffects().get(clickedSlot);
               this.enableGlow(eGlowPlayer.getPlayer(), ClickType.LEFT, effect);
            }
            break;
         case 33:
            if (this.getPage() == 1) {
               (new EGlowMainMenu(eGlowPlayer)).openInventory();
            } else {
               --this.page;
               super.openInventory();
            }
            break;
         case 34:
            if (this.hasNextPage()) {
               ++this.page;
               super.openInventory();
            }
         }

         this.UpdateMainEffectsNavigationBar(eGlowPlayer);
      }
   }

   public void setMenuItems() {
      Player player = this.getMenuMetadata().getOwner();
      EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
      this.effects = new ConcurrentHashMap();
      this.UpdateMainEffectsNavigationBar(eGlowPlayer);
      this.setHasNextPage(false);
      int slot = 0;
      int currentEffectSlot = 0;
      int nextEffectSlot = 26 * (this.getPage() - 1) + (this.getPage() > 1 ? 1 : 0);
      Iterator var6 = EGlowCustomEffectsConfig.Effect.GET_ALL_EFFECTS.get().iterator();

      while(true) {
         while(true) {
            String effect;
            EGlowEffect eGlowEffect;
            do {
               do {
                  if (!var6.hasNext()) {
                     NMSHook.scheduleTask(false, () -> {
                        this.getMenuMetadata().getOwner().openInventory(this.getInventory());
                     });
                     return;
                  }

                  effect = (String)var6.next();
                  eGlowEffect = DataManager.getEGlowEffect(effect.toLowerCase());
               } while(eGlowEffect == null);
            } while(!player.hasPermission(eGlowEffect.getPermissionNode()) && !player.hasPermission("eglow.effect.*"));

            if (currentEffectSlot != nextEffectSlot) {
               ++currentEffectSlot;
            } else {
               if (slot > this.getMaxItemsPerPage()) {
                  this.setHasNextPage(true);
                  this.UpdateMainEffectsNavigationBar(eGlowPlayer);
                  return;
               }

               Material material = this.getMaterial(effect);
               String name = this.getName(effect);
               int meta = this.getMeta(effect);
               int model = this.getModelID(effect);
               ArrayList<String> lores = new ArrayList();
               Iterator var14 = EGlowCustomEffectsConfig.Effect.GET_LORES.getList(effect).iterator();

               while(var14.hasNext()) {
                  String lore = (String)var14.next();
                  lore = ChatUtil.translateColors(lore.replace("%effect_name%", eGlowEffect.getDisplayName()).replace("%effect_has_permission%", this.hasPermission(eGlowPlayer, eGlowEffect.getPermissionNode())));
                  lores.add(lore);
               }

               this.getInventory().setItem(slot, this.createItem(material, name, meta, lores, model));
               if (!this.getEffects().containsKey(slot)) {
                  this.getEffects().put(slot, eGlowEffect.getName());
               }

               ++slot;
            }
         }
      }
   }

   private Material getMaterial(String effect) {
      String material = EGlowCustomEffectsConfig.Effect.GET_MATERIAL.getString(effect).toUpperCase();

      try {
         if (ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 13) {
            byte var4 = -1;
            switch(material.hashCode()) {
            case -1709492552:
               if (material.equals("SAPLING")) {
                  var4 = 0;
               }
               break;
            case 492897096:
               if (material.equals("PUMPKIN")) {
                  var4 = 1;
               }
            }

            switch(var4) {
            case 0:
               material = "SPRUCE_SAPLING";
               break;
            case 1:
               material = "CARVED_PUMPKIN";
            }
         }

         return Material.valueOf(material);
      } catch (NullPointerException | IllegalArgumentException var5) {
         ChatUtil.sendToConsole("Material: " + material + " for effect " + effect + "is not valid.", true);
         return Material.valueOf("DIRT");
      }
   }

   private String getName(String effect) {
      return EGlowCustomEffectsConfig.Effect.GET_NAME.getString(effect);
   }

   private int getMeta(String effect) {
      return EGlowCustomEffectsConfig.Effect.GET_META.getInt(effect);
   }

   private int getModelID(String effect) {
      return ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 14 ? EGlowCustomEffectsConfig.Effect.GET_MODEL_ID.getInt(effect) : -1;
   }

   @Generated
   public ConcurrentHashMap<Integer, String> getEffects() {
      return this.effects;
   }
}

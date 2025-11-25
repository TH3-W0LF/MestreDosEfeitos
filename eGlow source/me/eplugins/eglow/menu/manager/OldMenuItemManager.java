package me.eplugins.eglow.menu.manager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.custommenu.menu.manager.MenuManager;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowEffect;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class OldMenuItemManager extends MenuManager {
   public final String GLASS_PANE;
   private final String GUNPOWDER;
   private final String PLAYER_HEAD;
   public final String CLOCK;
   public final String ENDER_EYE;

   public OldMenuItemManager() {
      this.GLASS_PANE = ProtocolVersion.SERVER_VERSION.getMinorVersion() <= 12 ? "STAINED_GLASS_PANE" : "CYAN_STAINED_GLASS_PANE";
      this.GUNPOWDER = ProtocolVersion.SERVER_VERSION.getMinorVersion() <= 12 ? "SULPHUR" : "GUNPOWDER";
      this.PLAYER_HEAD = ProtocolVersion.SERVER_VERSION.getMinorVersion() <= 12 ? "SKULL_ITEM" : "PLAYER_HEAD";
      this.CLOCK = ProtocolVersion.SERVER_VERSION.getMinorVersion() <= 12 ? "WATCH" : "CLOCK";
      this.ENDER_EYE = ProtocolVersion.SERVER_VERSION.getMinorVersion() <= 12 ? "EYE_OF_ENDER" : "ENDER_EYE";
   }

   public ItemStack createItem(Material material, String name, int numb, String... lores) {
      ArrayList<String> lore = new ArrayList();
      String[] var6 = lores;
      int var7 = lores.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String text = var6[var8];
         if (!text.isEmpty()) {
            lore.add(text);
         }
      }

      return this.createItem(material, name, numb, lore, -1);
   }

   public ItemStack createItem(Material material, String name, int numb, List<String> lores, int model) {
      ItemStack item = ProtocolVersion.SERVER_VERSION.getMinorVersion() <= 12 && numb != 0 ? this.createLegacyItemStack(material, (short)numb) : new ItemStack(material);
      ItemMeta meta = item.getItemMeta();
      ((ItemMeta)Objects.requireNonNull(meta, "Unable to set item name because ItemMeta is null")).setDisplayName(ChatUtil.translateColors(name));
      if (model > 0) {
         meta.setCustomModelData(model);
      }

      if (!lores.isEmpty()) {
         meta.setLore(lores);
      }

      item.setItemMeta(meta);
      return item;
   }

   public ItemStack setItemGlow(ItemStack item) {
      ItemMeta meta = item.getItemMeta();
      ((ItemMeta)Objects.requireNonNull(meta, "Unable to set item enchantment because ItemMeta is null")).addEnchant(Enchantment.MENDING, 1, false);
      meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
      item.setItemMeta(meta);
      return item;
   }

   public boolean hasEffect(EGlowPlayer eGlowPlayer) {
      return eGlowPlayer.getGlowEffect() != null && eGlowPlayer.isGlowing() && (eGlowPlayer.getGlowEffect().getName().contains("slow") || eGlowPlayer.getGlowEffect().getName().contains("fast"));
   }

   public String hasPermission(EGlowPlayer eGlowPlayer, String permission) {
      Player player = eGlowPlayer.getPlayer();
      return !player.hasPermission(permission) && !player.hasPermission("eglow.effect.*") && !player.isOp() ? EGlowMessageConfig.Message.GUI_NO.get() : EGlowMessageConfig.Message.GUI_YES.get();
   }

   private ItemStack createLegacyItemStack(Material material, short numb) {
      try {
         return (ItemStack)NMSHook.nms.getItemStack.newInstance(material, 1, numb);
      } catch (IllegalAccessException | InvocationTargetException | InstantiationException var4) {
         ChatUtil.printException("Failed to create legacy itemstack", var4);
         return new ItemStack(Material.AIR);
      }
   }

   public ItemStack createPlayerSkull(EGlowPlayer eGlowPlayer) {
      ItemStack item = this.createItem(Material.valueOf(this.PLAYER_HEAD), EGlowMessageConfig.Message.GUI_SETTINGS_NAME.get(), 3, this.createInfoLore(eGlowPlayer));
      if (!EGlowMainConfig.MainConfig.SETTINGS_GUI_RENDER_SKULLS.getBoolean()) {
         return item;
      } else {
         try {
            SkullMeta meta = (SkullMeta)item.getItemMeta();
            if (ProtocolVersion.SERVER_VERSION.getMinorVersion() <= 12) {
               NMSHook.setOwningPlayer(meta, eGlowPlayer.getDisplayName());
            } else {
               ((SkullMeta)Objects.requireNonNull(meta, "Unable to set skull owner because ItemMeta is null")).setOwningPlayer(eGlowPlayer.getPlayer());
            }

            item.setItemMeta(meta);
            return item;
         } catch (ConcurrentModificationException var4) {
            return item;
         }
      }
   }

   public ItemStack createLeatherColor(EGlowPlayer eGlowPlayer, String color, int red, int green, int blue) {
      ItemStack item = this.createItem(Material.LEATHER_CHESTPLATE, EGlowMessageConfig.Message.GUI_COLOR.get(color), 0, this.createColorLore(eGlowPlayer, color));
      LeatherArmorMeta meta = (LeatherArmorMeta)item.getItemMeta();
      ((LeatherArmorMeta)Objects.requireNonNull(meta, "Unable to set item color because ItemMeta is null")).setColor(Color.fromRGB(red, green, blue));
      meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
      if (ProtocolVersion.SERVER_VERSION.getNetworkId() > 751) {
         meta.addItemFlags(new ItemFlag[]{ItemFlag.valueOf("HIDE_DYE")});
      }

      item.setItemMeta(meta);
      return item;
   }

   public ItemStack createLeatherColorCustom(EGlowPlayer eGlowPlayer, String color, int red, int green, int blue) {
      ItemStack item = this.createItem(Material.LEATHER_CHESTPLATE, EGlowMessageConfig.Message.GUI_COLOR.get(color), 0, this.createColorLore(eGlowPlayer, color));
      LeatherArmorMeta meta = (LeatherArmorMeta)item.getItemMeta();
      ((LeatherArmorMeta)Objects.requireNonNull(meta, "Unable to set item color because ItemMeta is null")).setColor(Color.fromRGB(red, green, blue));
      meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
      if (ProtocolVersion.SERVER_VERSION.getNetworkId() > 751) {
         meta.addItemFlags(new ItemFlag[]{ItemFlag.valueOf("HIDE_DYE")});
      }

      item.setItemMeta(meta);
      return item;
   }

   public ItemStack createGlowingStatus(EGlowPlayer eGlowPlayer) {
      List<String> prelores = new ArrayList();
      prelores.add(EGlowMessageConfig.Message.GUI_GLOWING.get() + (eGlowPlayer.isGlowing() ? EGlowMessageConfig.Message.GUI_YES.get() : EGlowMessageConfig.Message.GUI_NO.get()));
      prelores.add(EGlowMessageConfig.Message.GUI_LAST_GLOW.get() + (eGlowPlayer.getGlowEffect() == null ? EGlowMessageConfig.Message.GUI_NOT_AVAILABLE.get() : eGlowPlayer.getGlowEffect().getDisplayName()));
      prelores.add(EGlowMessageConfig.Message.GUI_CLICK_TO_TOGGLE.get());
      String[] lores = new String[prelores.size()];
      return eGlowPlayer.isGlowing() ? this.createItem(Material.GLOWSTONE_DUST, EGlowMessageConfig.Message.GUI_GLOW_ITEM_NAME.get(), 0, (String[])prelores.toArray(lores)) : this.createItem(Material.valueOf(this.GUNPOWDER), EGlowMessageConfig.Message.GUI_GLOW_ITEM_NAME.get(), 0, (String[])prelores.toArray(lores));
   }

   public ItemStack createGlowVisibility(EGlowPlayer eGlowPlayer) {
      List<String> prelores = new ArrayList();
      EnumUtil.GlowVisibility glowVisibility = eGlowPlayer.getGlowVisibility();
      prelores.add(ChatUtil.translateColors("&f") + EGlowMessageConfig.Message.VISIBILITY_ALL.get());
      prelores.add(ChatUtil.translateColors("&f") + EGlowMessageConfig.Message.VISIBILITY_OTHER.get());
      prelores.add(ChatUtil.translateColors("&f") + EGlowMessageConfig.Message.VISIBILITY_OWN.get());
      prelores.add(ChatUtil.translateColors("&f") + EGlowMessageConfig.Message.VISIBILITY_NONE.get());
      prelores.add(EGlowMessageConfig.Message.GUI_CLICK_TO_CYCLE.get());
      switch(glowVisibility) {
      case ALL:
         prelores.set(0, EGlowMessageConfig.Message.GLOW_VISIBILITY_INDICATOR.get() + (String)prelores.get(0));
         break;
      case OTHER:
         prelores.set(1, EGlowMessageConfig.Message.GLOW_VISIBILITY_INDICATOR.get() + (String)prelores.get(1));
         break;
      case OWN:
         prelores.set(2, EGlowMessageConfig.Message.GLOW_VISIBILITY_INDICATOR.get() + (String)prelores.get(2));
         break;
      case NONE:
         prelores.set(3, EGlowMessageConfig.Message.GLOW_VISIBILITY_INDICATOR.get() + (String)prelores.get(3));
      }

      return this.createItem(Material.valueOf(this.ENDER_EYE), EGlowMessageConfig.Message.GLOW_VISIBILITY_ITEM_NAME.get(), 0, prelores, 0);
   }

   private String[] createColorLore(EGlowPlayer eGlowPlayer, String color) {
      List<String> prelores = new ArrayList();
      EGlowEffect eglowColor = DataManager.getEGlowEffect(color.replace("-", ""));
      EGlowEffect eglowEffect = DataManager.getEGlowEffect("blink" + color.replace("-", "") + "slow");
      prelores.add(EGlowMessageConfig.Message.GUI_LEFT_CLICK.get() + EGlowMessageConfig.Message.COLOR.get(color));
      prelores.add(EGlowMessageConfig.Message.GUI_COLOR_PERMISSION.get() + this.hasPermission(eGlowPlayer, ((EGlowEffect)Objects.requireNonNull(eglowColor, "Unable to retrieve permission from effect")).getPermissionNode()));
      prelores.add(EGlowMessageConfig.Message.GUI_RIGHT_CLICK.get() + EGlowMessageConfig.Message.COLOR.get("effect-blink") + " " + EGlowMessageConfig.Message.COLOR.get(color));
      prelores.add(EGlowMessageConfig.Message.GUI_BLINK_PERMISSION.get() + this.hasPermission(eGlowPlayer, ((EGlowEffect)Objects.requireNonNull(eglowEffect, "Unable to retrieve permission from effect")).getPermissionNode()));
      String[] lores = new String[prelores.size()];
      return (String[])prelores.toArray(lores);
   }

   private String[] createCustomColorLore(EGlowPlayer eGlowPlayer, String color) {
      List<String> prelores = new ArrayList();
      EGlowEffect eglowColor = DataManager.getEGlowEffect(color.replace("-", ""));
      EGlowEffect eglowEffect = DataManager.getEGlowEffect("blink" + color.replace("-", "") + "slow");
      prelores.add(EGlowMessageConfig.Message.GUI_LEFT_CLICK.get() + EGlowMessageConfig.Message.COLOR.get(color));
      prelores.add(EGlowMessageConfig.Message.GUI_COLOR_PERMISSION.get() + this.hasPermission(eGlowPlayer, ((EGlowEffect)Objects.requireNonNull(eglowColor, "Unable to retrieve permission from effect")).getPermissionNode()));
      prelores.add(EGlowMessageConfig.Message.GUI_RIGHT_CLICK.get() + EGlowMessageConfig.Message.COLOR.get("effect-blink") + " " + EGlowMessageConfig.Message.COLOR.get(color));
      prelores.add(EGlowMessageConfig.Message.GUI_BLINK_PERMISSION.get() + this.hasPermission(eGlowPlayer, ((EGlowEffect)Objects.requireNonNull(eglowEffect, "Unable to retrieve permission from effect")).getPermissionNode()));
      String[] lores = new String[prelores.size()];
      return (String[])prelores.toArray(lores);
   }

   private String[] createInfoLore(EGlowPlayer eGlowPlayer) {
      List<String> prelores = new ArrayList();
      prelores.add(EGlowMessageConfig.Message.GUI_LAST_GLOW.get() + ChatUtil.getEffectChatName(eGlowPlayer));
      prelores.add(EGlowMessageConfig.Message.GUI_GLOW_ON_JOIN.get() + (eGlowPlayer.isGlowOnJoin() ? EGlowMessageConfig.Message.GUI_YES.get() : EGlowMessageConfig.Message.GUI_NO.get()));
      prelores.add(EGlowMessageConfig.Message.GUI_CLICK_TO_TOGGLE.get());
      String[] lores = new String[prelores.size()];
      return (String[])prelores.toArray(lores);
   }

   public String[] createSpeedLore(EGlowPlayer eGlowPlayer) {
      List<String> prelores = new ArrayList();
      if (eGlowPlayer.getGlowEffect() != null) {
         String effect = eGlowPlayer.getGlowEffect().getName();
         if (effect.contains("slow")) {
            prelores.add(EGlowMessageConfig.Message.GUI_SPEED.get() + EGlowMessageConfig.Message.COLOR.get("slow"));
         }

         if (effect.contains("fast")) {
            prelores.add(EGlowMessageConfig.Message.GUI_SPEED.get() + EGlowMessageConfig.Message.COLOR.get("fast"));
         }
      }

      String[] lores = new String[prelores.size()];
      return (String[])prelores.toArray(lores);
   }
}

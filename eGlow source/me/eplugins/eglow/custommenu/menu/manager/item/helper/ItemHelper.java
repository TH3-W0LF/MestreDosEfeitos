package me.eplugins.eglow.custommenu.menu.manager.item.helper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.custommenu.menu.manager.item.util.MaterialTypes;
import me.eplugins.eglow.custommenu.menu.manager.item.util.MenuItemDataStorage;
import me.eplugins.eglow.custommenu.util.TextUtil;
import me.eplugins.eglow.util.DebugUtil;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;

public class ItemHelper {
   public static ItemStack getItemStackFromMaterial(String material, MenuItemDataStorage menuItemDataStorage) {
      if (material.isEmpty()) {
         return null;
      } else {
         ItemStack item = getSpecialItem(material, menuItemDataStorage);
         if (item == null) {
            item = getPlaceholderItem(material, menuItemDataStorage);
            if (item != null) {
               if (item.getType().equals(Material.AIR)) {
                  return null;
               }

               if (item.getType().equals(Material.POTION)) {
                  return item;
               }
            } else {
               try {
                  item = new ItemStack(Material.valueOf(material.toUpperCase()));
               } catch (IllegalArgumentException var4) {
                  TextUtil.sendToConsole("&cInvalid Material for string&f: &e" + material + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
                  return null;
               }
            }
         }

         return item;
      }
   }

   private static ItemStack getSpecialItem(String material, MenuItemDataStorage menuItemDataStorage) {
      if (material.contains("-")) {
         ItemStack item = null;
         String[] splitMaterial = material.split("-");
         MaterialTypes.CustomItemType materialTypes = MaterialTypes.CustomItemType.fromString(splitMaterial[0]);
         if (materialTypes == null) {
            return null;
         }

         splitMaterial[1] = TextUtil.translateText(splitMaterial[1], menuItemDataStorage.getPlayer(), true, false, false, menuItemDataStorage.getArguments());
         String profileName;
         SkullMeta meta;
         switch(materialTypes) {
         case HEAD:
            item = new ItemStack(Material.valueOf(DebugUtil.getMainVersion() < 13 ? "SKULL_ITEM" : "PLAYER_HEAD"));
            meta = (SkullMeta)item.getItemMeta();
            if (meta != null) {
               profileName = TextUtil.translateText(splitMaterial[1], menuItemDataStorage.getPlayer(), false, true, false, menuItemDataStorage.getArguments());
               if (DebugUtil.getMainVersion() < 13) {
                  NMSHook.setOwningPlayer(meta, profileName);
               } else {
                  try {
                     OfflinePlayer offlinePlayer = Bukkit.getPlayer(profileName);
                     if (offlinePlayer != null) {
                        meta.setOwningPlayer(offlinePlayer);
                     }
                  } catch (Throwable var9) {
                  }
               }

               item.setItemMeta(meta);
               return item;
            }
            break;
         case BASEHEAD:
            item = new ItemStack(Material.valueOf(DebugUtil.getMainVersion() < 13 ? "SKULL_ITEM" : "PLAYER_HEAD"));
            meta = (SkullMeta)item.getItemMeta();
            NMSHook.setSkullTexture(meta, splitMaterial[1]);
            item.setItemMeta(meta);
            return item;
         case TEXTURE:
            item = new ItemStack(Material.valueOf(DebugUtil.getMainVersion() < 13 ? "SKULL_ITEM" : "PLAYER_HEAD"));
            meta = (SkullMeta)item.getItemMeta();
            profileName = "https://textures.minecraft.net/texture/" + splitMaterial[1];
            String json = "{ \"textures\": { \"SKIN\": { \"url\": \"" + profileName + "\" } } }";
            String base64 = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            NMSHook.setSkullTexture(meta, base64);
            item.setItemMeta(meta);
            return item;
         case HDB:
            if (EGlow.getInstance().getCustomMenus().getHdbAddon() != null) {
               item = EGlow.getInstance().getCustomMenus().getHdbAddon().getHeadDatabaseSkull(splitMaterial[1]);
               if (item == null) {
                  TextUtil.sendToConsole("&cInvalid Head Database id&f: &e" + splitMaterial[1] + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
               }
            }

            return item;
         case ITEMSADDER:
            if (EGlow.getInstance().getCustomMenus().getItemsAdderAddon() != null) {
               item = EGlow.getInstance().getCustomMenus().getItemsAdderAddon().getItemsAdderItem(splitMaterial[1]);
               if (item == null) {
                  TextUtil.sendToConsole("&cInvalid ItemsAdder item for name&f: &e" + splitMaterial[1] + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
               }
            }
         case ORAXEN:
            if (EGlow.getInstance().getCustomMenus().getOraxenAddon() != null) {
               item = EGlow.getInstance().getCustomMenus().getOraxenAddon().getOraxenItem(splitMaterial[1]);
               if (item == null) {
                  TextUtil.sendToConsole("&cInvalid Oraxen item for name&f: &e" + splitMaterial[1] + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
               }
            }
         case PLACEHOLDER:
            try {
               Material itemMaterial = Material.valueOf(TextUtil.translateText(splitMaterial[1], menuItemDataStorage.getPlayer(), false, true, false, menuItemDataStorage.getArguments()));
               return new ItemStack(itemMaterial);
            } catch (Exception var10) {
               TextUtil.sendToConsole("&cInvalid material for placeholder&&f: &e" + splitMaterial[1] + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
            }
         }
      }

      return null;
   }

   public static ItemStack getPlaceholderItem(String material, MenuItemDataStorage menuItemDataStorage) {
      MaterialTypes.PlaceholderItemType placeholderItemType = MaterialTypes.PlaceholderItemType.fromString(material);
      if (placeholderItemType == null) {
         return null;
      } else {
         ItemStack item;
         switch(placeholderItemType) {
         case MAIN_HAND:
            return menuItemDataStorage.getPlayer().getInventory().getItemInMainHand();
         case OFF_HAND:
            return menuItemDataStorage.getPlayer().getInventory().getItemInOffHand();
         case ARMOR_HELMET:
            item = menuItemDataStorage.getPlayer().getInventory().getHelmet();
            if (item == null) {
               return new ItemStack(Material.AIR);
            }

            return item;
         case ARMOR_CHESTPLATE:
            item = menuItemDataStorage.getPlayer().getInventory().getChestplate();
            if (item == null) {
               return new ItemStack(Material.AIR);
            }

            return item;
         case ARMOR_LEGGINGS:
            item = menuItemDataStorage.getPlayer().getInventory().getLeggings();
            if (item == null) {
               return new ItemStack(Material.AIR);
            }

            return item;
         case ARMOR_BOOTS:
            item = menuItemDataStorage.getPlayer().getInventory().getBoots();
            if (item == null) {
               return new ItemStack(Material.AIR);
            }

            return item;
         case WATER_BOTTLE:
            item = new ItemStack(Material.POTION);
            PotionMeta potionMeta = (PotionMeta)item.getItemMeta();
            if (potionMeta != null) {
               potionMeta.setBasePotionType(PotionType.WATER);
            }

            item.setItemMeta(potionMeta);
            return item;
         default:
            return null;
         }
      }
   }
}

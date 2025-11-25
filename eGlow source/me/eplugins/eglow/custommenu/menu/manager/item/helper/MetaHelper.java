package me.eplugins.eglow.custommenu.menu.manager.item.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.eplugins.eglow.custommenu.menu.manager.item.util.MenuItemDataStorage;
import me.eplugins.eglow.custommenu.util.TextUtil;
import me.eplugins.eglow.util.DebugUtil;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class MetaHelper {
   public static String getItemDisplayName(MenuItemDataStorage menuItemDataStorage) {
      return TextUtil.translateText(menuItemDataStorage.getItemSection().getString("display_name", ""), menuItemDataStorage.getPlayer(), true, true, false, menuItemDataStorage.getArguments());
   }

   public static List<String> getItemLore(MenuItemDataStorage menuItemDataStorage) {
      List<String> rawLores = menuItemDataStorage.getItemSection().getStringList("lore");
      List<String> lores = new ArrayList();
      Iterator var3 = rawLores.iterator();

      while(var3.hasNext()) {
         String rawLore = (String)var3.next();
         lores.add(TextUtil.translateText(rawLore, menuItemDataStorage.getPlayer(), true, true, false, menuItemDataStorage.getArguments()));
      }

      return lores;
   }

   public static int getItemAmount(MenuItemDataStorage menuItemDataStorage) {
      ConfigurationSection itemSection = menuItemDataStorage.getItemSection();
      String rawDynamicAmount;
      int dynamicAmount;
      if (itemSection.isSet("amount")) {
         rawDynamicAmount = itemSection.getString("amount", "1");

         try {
            dynamicAmount = Integer.parseInt(rawDynamicAmount);
            if (dynamicAmount > 64) {
               TextUtil.sendToConsole("&cItem amount exceeds 64!&f: &e" + rawDynamicAmount + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
               dynamicAmount = 64;
            }

            return dynamicAmount;
         } catch (NumberFormatException var5) {
            TextUtil.sendToConsole("&cInvalid item amount&f: &e" + rawDynamicAmount + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
            return 1;
         }
      } else if (itemSection.isSet("dynamic_amount")) {
         rawDynamicAmount = itemSection.getString("dynamic_amount", "1");

         try {
            dynamicAmount = Integer.parseInt(rawDynamicAmount);
            if (dynamicAmount > 64) {
               TextUtil.sendToConsole("&cItem amount exceeds 64!&f: &e" + rawDynamicAmount + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
               dynamicAmount = 64;
            }

            return dynamicAmount;
         } catch (NumberFormatException var6) {
            TextUtil.sendToConsole("&cInvalid dynamic item amount&f: &e" + rawDynamicAmount + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
            return 1;
         }
      } else {
         return 1;
      }
   }

   public static int getLegacyModeldata(MenuItemDataStorage menuItemDataStorage) {
      String rawModeldata = menuItemDataStorage.getItemSection().getString("model_data", "0");

      try {
         int modeldata = Integer.parseInt(rawModeldata);
         return modeldata;
      } catch (NumberFormatException var4) {
         TextUtil.sendToConsole("&cInvalid item modeldata&f: &e" + rawModeldata + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
         return 0;
      }
   }

   public static short getItemData(MenuItemDataStorage menuItemDataStorage) {
      String rawData = menuItemDataStorage.getItemSection().getString("data", "0");
      short data = 0;

      try {
         data = Short.parseShort(rawData);
      } catch (NumberFormatException var4) {
         TextUtil.sendToConsole("&cInvalid item data&f: &e" + rawData + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
      }

      return data;
   }

   public static ItemMeta buildEnchantsMeta(ItemMeta meta, MenuItemDataStorage menuItemDataStorage) {
      List<String> enchantmentList = menuItemDataStorage.getItemSection().getStringList("enchantments");
      String[] enchantInfo = new String[0];
      Iterator var4 = enchantmentList.iterator();

      while(var4.hasNext()) {
         String enchantmentInfo = (String)var4.next();

         try {
            if (!enchantmentList.isEmpty()) {
               enchantInfo = enchantmentInfo.split(";");
               int level = enchantInfo.length < 2 ? 1 : Integer.parseInt(enchantInfo[1]);
               Enchantment enchant;
               if (DebugUtil.getMainVersion() < 15) {
                  enchant = Enchantment.getByName(enchantInfo[0].toUpperCase());
               } else {
                  enchant = (Enchantment)Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantInfo[0].toLowerCase()));
               }

               if (enchant != null) {
                  meta.addEnchant(enchant, level, true);
               }
            }
         } catch (NumberFormatException var8) {
            TextUtil.sendToConsole("&cInvalid enchantment level&f: &e" + enchantInfo[1] + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
         } catch (NullPointerException var9) {
            TextUtil.sendToConsole("&cInvalid enchantment&f: &e" + enchantInfo[0] + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
         }
      }

      return meta;
   }

   public static ItemMeta buildBannerMeta(ItemMeta meta, MenuItemDataStorage menuItemDataStorage) {
      String baseColor = menuItemDataStorage.getItemSection().getString("base_color", "");
      List<String> patternList = menuItemDataStorage.getItemSection().getStringList("banner_meta");
      BannerMeta bannerMeta = (BannerMeta)meta;
      if (DebugUtil.getMainVersion() < 13 && !baseColor.isEmpty()) {
         try {
            DyeColor color = DyeColor.valueOf(baseColor.toUpperCase());
            NMSHook.setBannerBaseColor(bannerMeta, color);
         } catch (IllegalArgumentException var11) {
            TextUtil.sendToConsole("&cInvalid banner base color&f: &e" + baseColor + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
         }
      }

      if (!patternList.isEmpty()) {
         Iterator var14 = patternList.iterator();

         while(true) {
            while(var14.hasNext()) {
               String bannerInfo = (String)var14.next();
               if (!bannerInfo.contains(";")) {
                  TextUtil.sendToConsole("&cIncomplete pattern info&f: &e" + bannerInfo + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
               } else {
                  String[] patternInfo = bannerInfo.split(";");

                  DyeColor dyeColor;
                  try {
                     dyeColor = DyeColor.valueOf(patternInfo[0]);
                  } catch (NullPointerException var13) {
                     TextUtil.sendToConsole("&cInvalid pattern color&f: &e" + patternInfo[0] + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
                     continue;
                  }

                  PatternType patternType;
                  try {
                     if (DebugUtil.getMainVersion() < 20) {
                        patternType = PatternType.valueOf(patternInfo[1]);
                     } else {
                        patternType = (PatternType)Registry.BANNER_PATTERN.get(NamespacedKey.minecraft(patternInfo[1].toLowerCase()));
                     }
                  } catch (Exception var12) {
                     TextUtil.sendToConsole("&cInvalid banner pattern&f: &e" + patternInfo[1] + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
                     continue;
                  }

                  if (patternType != null && bannerMeta != null) {
                     bannerMeta.addPattern(new Pattern(dyeColor, patternType));
                  }
               }
            }

            return bannerMeta;
         }
      } else {
         return bannerMeta;
      }
   }

   public static ItemMeta buildItemFlagsMeta(ItemMeta meta, MenuItemDataStorage menuItemDataStorage) {
      List<String> flags = menuItemDataStorage.getItemSection().getStringList("item_flags");
      Iterator var3 = flags.iterator();

      while(var3.hasNext()) {
         String flag = (String)var3.next();

         ItemFlag itemFlag;
         try {
            itemFlag = ItemFlag.valueOf(flag.toUpperCase());
         } catch (Exception var7) {
            TextUtil.sendToConsole("&cInvalid item flag&f: &e" + flag + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
            continue;
         }

         meta.addItemFlags(new ItemFlag[]{itemFlag});
      }

      return meta;
   }

   public static ItemMeta buildPotionMeta(ItemMeta meta, MenuItemDataStorage menuItemDataStorage) {
      String basePotion = menuItemDataStorage.getItemSection().getString("potion_base", "");
      List<String> potionList = menuItemDataStorage.getItemSection().getStringList("potion_effects");
      PotionMeta potionMeta = (PotionMeta)meta;
      if (!basePotion.isEmpty()) {
         try {
            PotionType potionType = PotionType.valueOf(basePotion);
            potionMeta.setBasePotionType(potionType);
         } catch (Exception var12) {
            TextUtil.sendToConsole("&cInvalid base potion&f: &e" + basePotion + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
         }
      }

      if (!potionList.isEmpty()) {
         Iterator var16 = potionList.iterator();

         label72:
         while(true) {
            while(true) {
               String potionData;
               do {
                  if (!var16.hasNext()) {
                     break label72;
                  }

                  potionData = (String)var16.next();
               } while(potionData.isEmpty());

               String[] potionInfo = potionData.split(";");
               if (potionInfo.length < 3) {
                  TextUtil.sendToConsole("&cIncomplete potion effect&f: &e" + potionData + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
               } else {
                  int duration;
                  try {
                     duration = Integer.parseInt(potionInfo[1]);
                  } catch (NumberFormatException var14) {
                     TextUtil.sendToConsole("&cInvalid potion duration&f: &e" + potionInfo[1] + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
                     continue;
                  }

                  int amplifier;
                  try {
                     amplifier = Integer.parseInt(potionInfo[2]);
                  } catch (NumberFormatException var13) {
                     TextUtil.sendToConsole("&cInvalid potion duration&f: &e" + potionInfo[1] + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
                     continue;
                  }

                  PotionEffectType potionEffectType;
                  try {
                     if (DebugUtil.getMainVersion() <= 20 && (DebugUtil.getMainVersion() != 20 || DebugUtil.getMinorVersion() <= 2)) {
                        potionEffectType = PotionEffectType.getByName(potionInfo[0].toUpperCase());
                     } else {
                        potionEffectType = (PotionEffectType)Registry.EFFECT.get(NamespacedKey.minecraft(potionInfo[0].toLowerCase()));
                     }
                  } catch (NullPointerException var15) {
                     TextUtil.sendToConsole("&cInvalid potion effect&f: &e" + potionInfo[0] + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
                     continue;
                  }

                  if (potionEffectType != null) {
                     potionMeta.addCustomEffect(new PotionEffect(potionEffectType, duration * 20, amplifier), true);
                  }
               }
            }
         }
      }

      if (DebugUtil.getMainVersion() > 10 && !menuItemDataStorage.getItemSection().getString("rgb", "").isEmpty()) {
         potionMeta.setColor(getRGBColor(menuItemDataStorage));
      }

      return potionMeta;
   }

   public static ItemMeta buildLeatherMeta(ItemMeta meta, MenuItemDataStorage menuItemDataStorage) {
      LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)meta;
      if (!menuItemDataStorage.getItemSection().getString("rgb", "").isEmpty()) {
         leatherArmorMeta.setColor(getRGBColor(menuItemDataStorage));
      }

      return leatherArmorMeta;
   }

   public static ItemMeta buildColoredFireworkStarMeta(ItemMeta meta, MenuItemDataStorage menuItemDataStorage) {
      FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta)meta;

      try {
         fireworkEffectMeta.setEffect(FireworkEffect.builder().withColor(getRGBColor(menuItemDataStorage)).build());
         return fireworkEffectMeta;
      } catch (IllegalArgumentException var4) {
         return meta;
      }
   }

   public static ItemMeta buildHideAttributesMeta(ItemMeta meta) {
      meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
      return meta;
   }

   public static ItemMeta buildHideEnchantmentsMeta(ItemMeta meta) {
      meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
      return meta;
   }

   public static ItemMeta buildHidePotionEffectsMeta(ItemMeta meta) {
      if (DebugUtil.getMainVersion() > 20 || DebugUtil.getMainVersion() == 20 && DebugUtil.getMinorVersion() <= 4) {
         meta.addItemFlags(new ItemFlag[]{ItemFlag.valueOf("HIDE_POTION_EFFECTS")});
      }

      return meta;
   }

   public static ItemMeta buildHideUnbreakableMeta(ItemMeta meta) {
      meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE});
      return meta;
   }

   public static ItemMeta buildUnbreakableMeta(ItemMeta meta) {
      meta.setUnbreakable(true);
      return meta;
   }

   private static Color getRGBColor(MenuItemDataStorage menuItemDataStorage) {
      String rgb = menuItemDataStorage.getItemSection().getString("rgb", "");
      String[] rgbValues = rgb.replace(" ", "").split(",");
      if (rgbValues.length != 3) {
         TextUtil.sendToConsole("&cIncomplete rgb&f: &e" + rgb + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
         return Color.fromRGB(0, 0, 0);
      } else {
         int red;
         int green;
         int blue;
         try {
            red = Integer.parseInt(rgbValues[0]);
            green = Integer.parseInt(rgbValues[1]);
            blue = Integer.parseInt(rgbValues[2]);
         } catch (NumberFormatException var9) {
            TextUtil.sendToConsole("&cInvalid rgb&f: &e" + rgb + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
            return Color.fromRGB(0, 0, 0);
         }

         try {
            Color color = Color.fromRGB(red, green, blue);
            return color;
         } catch (IllegalArgumentException var8) {
            TextUtil.sendToConsole("&cInvalid rgb&f: &e" + rgb + " &cin menu&f: &e" + menuItemDataStorage.getFilePath());
            return Color.fromRGB(0, 0, 0);
         }
      }
   }
}

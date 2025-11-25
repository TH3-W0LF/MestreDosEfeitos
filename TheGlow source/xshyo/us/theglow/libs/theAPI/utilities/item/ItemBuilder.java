package xshyo.us.theglow.libs.theAPI.utilities.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Registry;
import org.bukkit.block.Banner;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Axolotl.Variant;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import xshyo.us.theglow.libs.theAPI.hooks.BaseHeadHook;
import xshyo.us.theglow.libs.theAPI.hooks.BreweryXItemHook;
import xshyo.us.theglow.libs.theAPI.hooks.ExecutableBlocksHook;
import xshyo.us.theglow.libs.theAPI.hooks.ExecutableItemsHook;
import xshyo.us.theglow.libs.theAPI.hooks.HeadDataBaseHook;
import xshyo.us.theglow.libs.theAPI.hooks.ItemHook;
import xshyo.us.theglow.libs.theAPI.hooks.ItemsAdderHook;
import xshyo.us.theglow.libs.theAPI.hooks.MMOItemsHook;
import xshyo.us.theglow.libs.theAPI.hooks.MythicMobsHook;
import xshyo.us.theglow.libs.theAPI.hooks.NamedHeadHook;
import xshyo.us.theglow.libs.theAPI.hooks.OraxenHook;
import xshyo.us.theglow.libs.theAPI.hooks.QualityArmoryHook;
import xshyo.us.theglow.libs.theAPI.hooks.TextureHeadHook;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public class ItemBuilder {
   private final ItemStack itemStack;
   private ItemMeta itemMeta;
   private static final List<ItemHook> HANDLERS = Arrays.asList(new BaseHeadHook(), new HeadDataBaseHook(), new ItemsAdderHook(), new NamedHeadHook(), new MMOItemsHook(), new ExecutableBlocksHook(), new ExecutableItemsHook(), new BreweryXItemHook(), new OraxenHook(), new TextureHeadHook(), new QualityArmoryHook(), new MythicMobsHook());

   public ItemBuilder(ItemStack var1) {
      this.itemStack = var1;
      this.itemMeta = var1.getItemMeta();
   }

   public ItemBuilder(Material var1) {
      this.itemStack = new ItemStack(var1, 1);
      this.itemMeta = this.itemStack.getItemMeta();
   }

   public ItemBuilder(Material var1, int var2) {
      this.itemStack = new ItemStack(var1, var2);
      this.itemMeta = this.itemStack.getItemMeta();
   }

   public ItemBuilder(String var1) {
      ItemHook var2 = this.getHandlerForPrefix(var1);
      if (var2 != null) {
         this.itemStack = var2.getItem(var1.substring(var2.getPrefix().length()));
      } else {
         Material var3 = Material.matchMaterial(var1);
         this.itemStack = new ItemStack((Material)Objects.requireNonNullElse(var3, Material.STONE), 1);
      }

      this.itemMeta = this.itemStack.getItemMeta();
   }

   public ItemBuilder(String var1, int var2) {
      ItemHook var3 = this.getHandlerForPrefix(var1);
      if (var3 != null) {
         this.itemStack = var3.getItem(var1.substring(var3.getPrefix().length()));
         this.itemStack.setAmount(var2);
      } else {
         Material var4 = Material.matchMaterial(var1);
         this.itemStack = new ItemStack((Material)Objects.requireNonNullElse(var4, Material.STONE), var2);
      }

      this.itemMeta = this.itemStack.getItemMeta();
   }

   private ItemHook getHandlerForPrefix(String var1) {
      Iterator var2 = HANDLERS.iterator();

      ItemHook var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (ItemHook)var2.next();
      } while(!var1.startsWith(var3.getPrefix()));

      return var3;
   }

   public ItemBuilder(Material var1, int var2, int var3) {
      this.itemStack = new ItemStack(var1, var2, (short)var3);
      this.itemMeta = this.itemStack.getItemMeta();
   }

   public ItemBuilder setData(int var1) {
      this.itemStack.setDurability((short)var1);
      return this;
   }

   public ItemBuilder setAmount(int var1) {
      this.itemStack.setAmount(var1);
      return this;
   }

   public ItemBuilder addAmount(int var1) {
      this.itemStack.setAmount(this.itemStack.getAmount() + var1);
      return this;
   }

   public ItemBuilder setName(String var1) {
      if (this.itemMeta != null) {
         this.itemMeta.setDisplayName(Utils.translate(var1));
      }

      return this;
   }

   public ItemBuilder setSkullOwner(String var1) {
      if (var1 != null && !var1.isEmpty() && this.itemMeta != null) {
         if (this.itemMeta instanceof SkullMeta) {
            SkullMeta var2 = (SkullMeta)this.itemMeta;
            var2.setOwner(var1);
         }

         return this;
      } else {
         return this;
      }
   }

   public ItemBuilder setSkullOwnerOffline(OfflinePlayer var1) {
      if (var1 != null && this.itemMeta != null) {
         if (!this.itemStack.getType().toString().endsWith("SKULL_ITEM") && !this.itemStack.getType().toString().endsWith("PLAYER_HEAD")) {
            return this;
         } else {
            try {
               if (!(this.itemMeta instanceof SkullMeta)) {
                  return this;
               }

               SkullMeta var2 = (SkullMeta)this.itemMeta;
               var2.setOwningPlayer(var1);
               this.itemMeta = var2;
            } catch (Exception var3) {
               System.out.println(var3.getMessage());
            }

            return this;
         }
      } else {
         return this;
      }
   }

   public ItemBuilder setNBTData(String var1) {
      return var1 != null && !var1.isEmpty() && this.itemMeta != null ? this : this;
   }

   public ItemBuilder addNBTData(String var1) {
      return var1 != null && !var1.isEmpty() ? this : this;
   }

   public ItemBuilder setLore(List<String> var1) {
      if (this.itemMeta != null) {
         this.itemMeta.setLore(Utils.translate(var1));
      }

      return this;
   }

   public ItemBuilder setLore(String... var1) {
      if (this.itemMeta != null) {
         this.itemMeta.setLore(Utils.translate(Arrays.asList(var1)));
      }

      return this;
   }

   public ItemBuilder addLoreLine(String var1) {
      if (this.itemMeta != null) {
         Object var2 = this.itemMeta.getLore();
         if (var2 == null) {
            var2 = new ArrayList();
         }

         ((List)var2).add(Utils.translate(var1));
         this.itemMeta.setLore((List)var2);
      }

      return this;
   }

   public ItemBuilder setEnchanted(boolean var1) {
      if (var1 && this.itemMeta != null) {
         this.itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
         this.itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
         this.itemStack.setItemMeta(this.itemMeta);
      }

      return this;
   }

   public ItemBuilder addEnchantment() {
      try {
         if (this.itemMeta != null) {
            this.itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
            this.itemStack.setItemMeta(this.itemMeta);
         }
      } catch (IllegalArgumentException var2) {
         System.out.println("Error when adding enchantment: " + var2.getMessage());
      }

      return this;
   }

   public ItemBuilder addEnchantment(Enchantment var1, int var2) {
      try {
         if (this.itemMeta != null) {
            this.itemMeta.addEnchant(var1, var2, false);
         }
      } catch (IllegalArgumentException var4) {
         PrintStream var10000 = System.out;
         String var10001 = String.valueOf(var1.getKey());
         var10000.println("Error when adding enchantment " + var10001 + ": " + var4.getMessage());
      }

      return this;
   }

   public ItemBuilder addUnsafeEnchantment(Enchantment var1, int var2) {
      try {
         this.itemMeta.addEnchant(var1, var2, true);
      } catch (Exception var4) {
         PrintStream var10000 = System.out;
         String var10001 = String.valueOf(var1.getKey());
         var10000.println("Error adding unsafe enchantment " + var10001 + ": " + var4.getMessage());
      }

      return this;
   }

   public boolean hasCustomModelData() {
      return this.itemMeta != null && this.itemMeta.hasCustomModelData();
   }

   public ItemBuilder setCustomModelData(int var1) {
      if (this.itemMeta != null && !this.itemMeta.hasCustomModelData()) {
         this.itemMeta.setCustomModelData(var1);
      }

      return this;
   }

   public ItemBuilder setShieldPattern(DyeColor var1, List<Pattern> var2) {
      if (this.itemStack.getType() == Material.SHIELD && this.itemMeta != null) {
         try {
            BlockStateMeta var3 = (BlockStateMeta)this.itemMeta;
            Banner var4 = (Banner)var3.getBlockState();
            if (var1 != null) {
               var4.setBaseColor(var1);
            }

            if (var2 != null && !var2.isEmpty()) {
               var4.setPatterns(var2);
            }

            var4.update();
            var3.setBlockState(var4);
            this.itemMeta = var3;
         } catch (Exception var5) {
            System.out.println("Error applying shield pattern: " + var5.getMessage());
         }

         return this;
      } else {
         return this;
      }
   }

   public ItemBuilder setShieldFromJson(String var1) {
      if (var1 != null && !var1.isEmpty() && this.itemMeta != null && this.itemStack.getType() == Material.SHIELD) {
         try {
            BlockStateMeta var2 = (BlockStateMeta)this.itemMeta;
            Banner var3 = (Banner)var2.getBlockState();
            JsonObject var4 = (JsonObject)Utils.getGson().fromJson(var1, JsonObject.class);
            JsonObject var5 = var4.getAsJsonObject("BlockEntityTag");
            if (var5 != null) {
               if (var5.has("Base")) {
                  int var6 = var5.get("Base").getAsInt();
                  DyeColor var7 = DyeColor.getByDyeData((byte)var6);
                  if (var7 != null) {
                     var3.setBaseColor(var7);
                  }
               }

               JsonArray var16 = var5.getAsJsonArray("Patterns");
               if (var16 != null) {
                  ArrayList var17 = new ArrayList();

                  for(int var8 = 0; var8 < var16.size(); ++var8) {
                     JsonObject var9 = var16.get(var8).getAsJsonObject();
                     int var10 = var9.get("Color").getAsInt();
                     String var11 = var9.get("Pattern").getAsString();
                     DyeColor var12 = DyeColor.getByDyeData((byte)var10);
                     PatternType var13 = PatternType.getByIdentifier(var11);
                     if (var12 != null && var13 != null) {
                        Pattern var14 = new Pattern(var12, var13);
                        var17.add(var14);
                     }
                  }

                  var3.setPatterns(var17);
               }
            }

            var3.update();
            var2.setBlockState(var3);
            this.itemMeta = var2;
         } catch (Exception var15) {
            System.out.println("Error processing shield JSON: " + var15.getMessage());
         }

         return this;
      } else {
         return this;
      }
   }

   public ItemBuilder setBannerFromJson(String var1) {
      if (var1 != null && !var1.isEmpty() && this.itemMeta != null) {
         if (this.itemMeta instanceof BannerMeta) {
            BannerMeta var2 = (BannerMeta)this.itemMeta;

            try {
               JsonObject var3 = (JsonObject)Utils.getGson().fromJson(var1, JsonObject.class);
               JsonObject var4 = var3.getAsJsonObject("BlockEntityTag");
               if (var4 != null) {
                  JsonArray var5 = var4.getAsJsonArray("Patterns");
                  if (var5 != null) {
                     for(int var6 = 0; var6 < var5.size(); ++var6) {
                        JsonObject var7 = var5.get(var6).getAsJsonObject();
                        int var8 = var7.get("Color").getAsInt();
                        String var9 = var7.get("Pattern").getAsString();
                        DyeColor var10 = DyeColor.getByDyeData((byte)var8);
                        PatternType var11 = PatternType.getByIdentifier(var9);
                        if (var10 != null && var11 != null) {
                           Pattern var12 = new Pattern(var10, var11);
                           var2.addPattern(var12);
                        }
                     }
                  }
               }

               this.itemMeta = var2;
            } catch (Exception var13) {
               System.out.println("Error processing banner JSON: " + var13.getMessage());
            }
         }

         return this;
      } else {
         return this;
      }
   }

   public ItemBuilder addFlagsFromConfig(Set<String> var1) {
      if (var1 != null && !var1.isEmpty() && this.itemMeta != null) {
         boolean var2 = false;
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();

            try {
               ItemFlag var5 = ItemFlag.valueOf(var4.toUpperCase());
               if (var5 == ItemFlag.HIDE_ATTRIBUTES && Utils.getCurrentVersion() >= 1206 && !var2) {
                  this.itemMeta.setAttributeModifiers(ImmutableMultimap.of());
                  var2 = true;
               }

               this.itemMeta.addItemFlags(new ItemFlag[]{var5});
            } catch (IllegalArgumentException var6) {
               System.out.println("Warning: Invalid ItemFlag found in configuration: " + var4);
            }
         }
      }

      return this;
   }

   public ItemBuilder addItemFlag(ItemFlag var1) {
      if (this.itemMeta != null) {
         this.itemMeta.addItemFlags(new ItemFlag[]{var1});
      }

      return this;
   }

   public ItemBuilder setGoatHornData(MusicInstrument var1) {
      if (this.itemStack.getType() == Material.GOAT_HORN && var1 != null) {
         try {
            if (this.itemMeta instanceof MusicInstrumentMeta) {
               MusicInstrumentMeta var2 = (MusicInstrumentMeta)this.itemMeta;
               var2.setInstrument(var1);
               this.itemStack.setItemMeta(var2);
            } else {
               System.out.println("Instrumento no encontrado o tipo de ItemMeta incorrecto.");
            }
         } catch (Exception var3) {
            System.out.println("Error: " + var3.getMessage());
         }
      }

      return this;
   }

   public ItemBuilder setGoatHornData(NamespacedKey var1) {
      if (this.itemStack.getType() == Material.GOAT_HORN && var1 != null) {
         try {
            ItemMeta var3 = this.itemMeta;
            if (var3 instanceof MusicInstrumentMeta) {
               MusicInstrumentMeta var2 = (MusicInstrumentMeta)var3;
               var2.setInstrument(MusicInstrument.getByKey(var1));
               this.itemStack.setItemMeta(var2);
            } else {
               System.out.println("Instrumento no encontrado o tipo de ItemMeta incorrecto.");
            }
         } catch (Exception var4) {
            System.out.println("Error setting goat horn with NamespacedKey: " + var4.getMessage());
         }
      }

      return this;
   }

   public ItemBuilder setGoatHornData(String var1) {
      if (this.itemStack.getType() == Material.GOAT_HORN && var1 != null) {
         try {
            if (Utils.getCurrentVersion() < 1201) {
               NamespacedKey var4 = NamespacedKey.minecraft(var1.toLowerCase());
               return this.setGoatHornData(var4);
            }

            MusicInstrument var2 = (MusicInstrument)Registry.INSTRUMENT.get(NamespacedKey.minecraft(var1.toLowerCase()));
            if (var2 != null) {
               return this.setGoatHornData(var2);
            }

            System.out.println("Invalid music instrument: " + var1);
         } catch (Exception var3) {
            System.out.println("Error setting goat horn data: " + var3.getMessage());
         }
      }

      return this;
   }

   public ItemBuilder setLeatherArmorColor(String var1) {
      if (this.itemMeta == null) {
         return this;
      } else {
         if (this.itemStack.getType() == Material.LEATHER_HELMET || this.itemStack.getType() == Material.LEATHER_CHESTPLATE || this.itemStack.getType() == Material.LEATHER_LEGGINGS || this.itemStack.getType() == Material.LEATHER_BOOTS) {
            try {
               int var2 = 160;
               int var3 = 101;
               int var4 = 64;
               if (var1 != null && var1.length() > 2) {
                  String var5 = var1.replaceAll("\\s+", "");
                  String[] var6 = var5.split(",");
                  if (var6.length >= 3) {
                     var2 = Integer.parseInt(var6[0]);
                     var3 = Integer.parseInt(var6[1]);
                     var4 = Integer.parseInt(var6[2]);
                  }
               }

               Color var8 = Color.fromRGB(var2, var3, var4);
               if (this.itemMeta instanceof LeatherArmorMeta) {
                  LeatherArmorMeta var9 = (LeatherArmorMeta)this.itemMeta;
                  var9.setColor(var8);
                  this.itemMeta = var9;
               }
            } catch (Exception var7) {
               System.out.println("Error when setting the color of the armor: " + var7.getMessage());
            }
         }

         return this;
      }
   }

   public ItemBuilder setBasePotionData(PotionData var1) {
      if (this.itemMeta != null && this.itemMeta instanceof PotionMeta) {
         PotionMeta var2 = (PotionMeta)this.itemMeta;
         var2.setBasePotionData(var1);
         this.itemMeta = var2;
      }

      return this;
   }

   public ItemBuilder addCustomEffect(PotionEffect var1, boolean var2) {
      if (this.itemMeta != null && this.itemMeta instanceof PotionMeta) {
         PotionMeta var3 = (PotionMeta)this.itemMeta;
         var3.addCustomEffect(var1, var2);
         this.itemMeta = var3;
      }

      return this;
   }

   public ItemBuilder setBannerBaseColor(DyeColor var1) {
      if (this.itemMeta != null && this.itemMeta instanceof BannerMeta) {
         BannerMeta var2 = (BannerMeta)this.itemMeta;

         try {
            var2.setBaseColor(var1);
         } catch (NoSuchMethodError var4) {
            System.out.println("setBaseColor is not available in this version.");
         }

         this.itemMeta = var2;
      }

      return this;
   }

   public ItemBuilder addBannerPattern(Pattern var1) {
      if (this.itemMeta != null && this.itemMeta instanceof BannerMeta) {
         BannerMeta var2 = (BannerMeta)this.itemMeta;
         var2.addPattern(var1);
         this.itemMeta = var2;
      }

      return this;
   }

   public ItemBuilder setFireworkPower(int var1) {
      if (this.itemMeta != null && this.itemMeta instanceof FireworkMeta) {
         FireworkMeta var2 = (FireworkMeta)this.itemMeta;
         var2.setPower(var1);
         this.itemMeta = var2;
      }

      return this;
   }

   public ItemBuilder addFireworkEffect(FireworkEffect var1) {
      if (this.itemMeta != null && this.itemMeta instanceof FireworkMeta) {
         FireworkMeta var2 = (FireworkMeta)this.itemMeta;
         var2.addEffect(var1);
         this.itemMeta = var2;
      }

      return this;
   }

   public ItemBuilder setAxolotlVariant(Variant var1) {
      if (this.itemMeta != null && this.itemMeta instanceof AxolotlBucketMeta) {
         AxolotlBucketMeta var2 = (AxolotlBucketMeta)this.itemMeta;
         var2.setVariant(var1);
         this.itemMeta = var2;
      }

      return this;
   }

   public ItemBuilder setChargedProjectiles(List<ItemStack> var1) {
      if (this.itemMeta != null && this.itemMeta instanceof CrossbowMeta) {
         CrossbowMeta var2 = (CrossbowMeta)this.itemMeta;
         var2.setChargedProjectiles(var1);
         this.itemMeta = var2;
      }

      return this;
   }

   public ItemBuilder setSpawnerType(String var1) {
      if (var1 != null && !var1.isEmpty() && this.itemMeta != null) {
         if (this.itemStack.getType().name().equals("SPAWNER") || this.itemStack.getType().name().equals("MOB_SPAWNER")) {
            try {
               if (this.itemMeta instanceof BlockStateMeta) {
                  BlockStateMeta var2 = (BlockStateMeta)this.itemMeta;
                  if (var2.getBlockState() instanceof CreatureSpawner) {
                     CreatureSpawner var3 = (CreatureSpawner)var2.getBlockState();

                     try {
                        EntityType var4 = EntityType.valueOf(var1.toUpperCase());
                        var3.setSpawnedType(var4);
                        var2.setBlockState(var3);
                        this.itemMeta = var2;
                     } catch (IllegalArgumentException var5) {
                        System.out.println("Invalid entity type: " + var1);
                     }
                  }
               }
            } catch (Exception var6) {
               System.out.println("Error when setting spawner type: " + var6.getMessage());
            }
         }

         return this;
      } else {
         return this;
      }
   }

   public ItemBuilder setArmorTrim(ArmorTrim var1) {
      if (this.itemMeta == null) {
         return this;
      } else {
         if (Utils.getCurrentVersion() >= 1200) {
            try {
               if ((this.itemStack.getType().name().contains("_HELMET") || this.itemStack.getType().name().contains("_CHESTPLATE") || this.itemStack.getType().name().contains("_LEGGINGS") || this.itemStack.getType().name().contains("_BOOTS")) && this.itemMeta instanceof ArmorMeta) {
                  ArmorMeta var2 = (ArmorMeta)this.itemMeta;
                  var2.setTrim(var1);
                  this.itemMeta = var2;
               }
            } catch (NoClassDefFoundError | Exception var3) {
               System.out.println("Error when setting armor trim (compatible Minecraft version?): " + var3.getMessage());
            }
         }

         return this;
      }
   }

   public ItemStack build() {
      if (this.itemMeta != null) {
         this.itemStack.setItemMeta(this.itemMeta);
      }

      return this.itemStack;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public ItemMeta getItemMeta() {
      return this.itemMeta;
   }
}

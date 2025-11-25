package xshyo.us.theglow.libs.theAPI.hooks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import xshyo.us.theglow.libs.theAPI.TheAPI;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public interface ItemHook {
   ItemStack getItem(String... var1);

   String getPrefix();

   String getPluginName();

   default ItemStack createMissingPluginItem() {
      ItemStack var1 = new ItemStack(Material.BARRIER, 1);
      ItemMeta var2 = var1.getItemMeta();
      if (var2 != null) {
         String var10001 = String.valueOf(ChatColor.RED);
         var2.setDisplayName(var10001 + "Required plugin: " + this.getPluginName());
         String[] var4 = new String[2];
         String var10004 = String.valueOf(ChatColor.GRAY);
         var4[0] = var10004 + "This item requires the plugin " + this.getPluginName();
         var4[1] = String.valueOf(ChatColor.GRAY) + "Install it to use this item";
         var2.setLore(Arrays.asList(var4));
         NamespacedKey var3 = new NamespacedKey(TheAPI.getInstance(), "missing_plugin");
         var2.getPersistentDataContainer().set(var3, PersistentDataType.BYTE, (byte)1);
         var1.setItemMeta(var2);
      }

      return var1;
   }

   default ItemStack getHead() {
      return new ItemStack(Material.PLAYER_HEAD, 1);
   }

   default ItemStack getSkullByBase64EncodedTextureUrl(String var1) {
      ItemStack var2 = this.getHead().clone();
      if (var1.isEmpty()) {
         return var2;
      } else {
         SkullMeta var3 = (SkullMeta)var2.getItemMeta();
         if (var3 == null) {
            return var2;
         } else if (Utils.getCurrentVersion() >= 1181) {
            PlayerProfile var8 = this.getPlayerProfile(var1);
            var3.setOwnerProfile(var8);
            var2.setItemMeta(var3);
            return var2;
         } else {
            GameProfile var4 = this.getGameProfile(var1);

            try {
               Field var5 = var3.getClass().getDeclaredField("profile");
               var5.setAccessible(true);
               var5.set(var3, var4);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException var7) {
            }

            var2.setItemMeta(var3);
            return var2;
         }
      }
   }

   default GameProfile getGameProfile(String var1) {
      GameProfile var2 = new GameProfile(UUID.fromString("d1f53efe-c5a4-44fd-a9a6-69f7f31cf751"), "");
      var2.getProperties().put("textures", new Property("textures", var1));
      return var2;
   }

   default PlayerProfile getPlayerProfile(String var1) {
      PlayerProfile var2 = Bukkit.createPlayerProfile(UUID.fromString("d1f53efe-c5a4-44fd-a9a6-69f7f31cf751"));
      String var3 = this.decodeSkinUrl(var1);
      if (var3 == null) {
         return var2;
      } else {
         PlayerTextures var4 = var2.getTextures();

         try {
            var4.setSkin(new URL(var3));
         } catch (MalformedURLException var6) {
            Bukkit.getLogger().warning("Something went horribly wrong trying to create basehead URL");
         }

         var2.setTextures(var4);
         return var2;
      }
   }

   default String decodeSkinUrl(String var1) {
      String var2 = new String(Base64.getDecoder().decode(var1));
      JsonObject var3 = (JsonObject)Utils.getGson().fromJson(var2, JsonObject.class);
      JsonElement var4 = var3.get("textures");
      if (var4 == null) {
         return null;
      } else {
         JsonElement var5 = var4.getAsJsonObject().get("SKIN");
         if (var5 == null) {
            return null;
         } else {
            JsonElement var6 = var5.getAsJsonObject().get("url");
            return var6 == null ? null : var6.getAsString();
         }
      }
   }

   default String getEncoded(String var1) {
      byte[] var2 = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", "https://textures.minecraft.net/texture/" + var1).getBytes());
      return new String(var2);
   }

   default ItemStack getSkullByName(String var1) {
      ItemStack var2 = this.getHead().clone();
      if (var1.isEmpty()) {
         return var2;
      } else {
         SkullMeta var3 = (SkullMeta)var2.getItemMeta();
         if (var3 == null) {
            return var2;
         } else {
            OfflinePlayer var4 = Bukkit.getOfflinePlayer(var1);
            var3.setOwningPlayer(var4);
            var2.setItemMeta(var3);
            return var2;
         }
      }
   }
}

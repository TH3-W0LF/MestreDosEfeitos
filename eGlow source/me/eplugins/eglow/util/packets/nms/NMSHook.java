package me.eplugins.eglow.util.packets.nms;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.DebugUtil;
import me.eplugins.eglow.util.packets.datawatcher.DataWatcher;
import me.eplugins.eglow.util.packets.datawatcher.DataWatcherObject;
import me.eplugins.eglow.util.packets.datawatcher.DataWatcherRegistry;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class NMSHook {
   public static DataWatcherRegistry registry;
   public static NMSStorage nms;
   private static final Map<String, Object> jsonCache = new HashMap();

   public static void initialize() {
      try {
         DebugUtil.setupSupport(EGlow.getInstance());
         nms = new NMSStorage();
         registry = new DataWatcherRegistry(nms);
      } catch (Exception var1) {
         ChatUtil.printException("Failed to initialize reflection", var1);
      }

   }

   public static boolean isBungee() {
      try {
         return (Boolean)nms.bungee.get(nms.SpigotConfig);
      } catch (IllegalAccessException var1) {
         ChatUtil.printException("Failed to retrieve bungee setting", var1);
         return false;
      }
   }

   public static void setOwningPlayer(SkullMeta skullMeta, String owner) {
      try {
         nms.setOwningPlayer.invoke(skullMeta, owner);
      } catch (Exception var3) {
         ChatUtil.printException("Failed to set owning player", var3);
      }

   }

   public static void setBannerBaseColor(BannerMeta bannerMeta, DyeColor color) {
      try {
         nms.setBaseColor.invoke(bannerMeta, color);
      } catch (Exception var3) {
         ChatUtil.printException("Failed to set banner base color", var3);
      }

   }

   public static void setSkullTexture(SkullMeta meta, String base64) {
      try {
         Object gameProfile = nms.newGameProfile.newInstance(UUID.randomUUID(), "");
         nms.setGameProfileProperties.invoke(nms.gameProfileProperties.invoke(gameProfile), "textures", nms.newProfileProperty.newInstance("textures", base64));
         if (!nms.is1_21_9OrAbove) {
            if (nms.is1_20_5OrAbove) {
               nms.skullProfile.set(meta, nms.newResolvableProfile.newInstance(gameProfile));
            } else {
               nms.skullProfile.set(meta, gameProfile);
            }
         }
      } catch (Exception var3) {
         ChatUtil.printException("Failed to set skull texture", var3);
      }

   }

   public static Inventory getBottomInventory(InventoryClickEvent event) {
      try {
         return (Inventory)nms.getBottomInventory.invoke(nms.getView.invoke(event));
      } catch (Exception var2) {
         ChatUtil.printException("Failed to get bottom inventory", var2);
         return null;
      }
   }

   public static Object getChannel(Player player) {
      if (nms.CHANNEL == null) {
         return null;
      } else {
         try {
            return nms.CHANNEL.get(nms.NETWORK_MANAGER.get(nms.PLAYER_CONNECTION.get(nms.getHandle.invoke(player))));
         } catch (Exception var2) {
            ChatUtil.printException("Failed to retrieve player channel", var2);
            return null;
         }
      }
   }

   public static void sendPacket(Player player, Object nmsPacket) throws Exception {
      if (nmsPacket != null) {
         nms.sendPacket.invoke(nms.PLAYER_CONNECTION.get(nms.getHandle.invoke(player)), nmsPacket);
      }
   }

   public static void sendPacket(EGlowPlayer eGlowPlayer, Object nmsPacket) throws Exception {
      sendPacket(eGlowPlayer.getPlayer(), nmsPacket);
   }

   public static Object stringToComponent(String json) throws Exception {
      if (json == null) {
         return null;
      } else {
         json = json.replace("覺", "i").replace("ı", "i");
         if (jsonCache.containsKey(json)) {
            return jsonCache.get(json);
         } else {
            Object deserializedJson;
            if (nms.is1_20_5OrAbove) {
               if (DataManager.getEGlowPlayers().stream().findFirst().isPresent()) {
                  EGlowPlayer ePlayer = (EGlowPlayer)DataManager.getEGlowPlayers().stream().findFirst().get();
                  Object nmsPlayer = nms.getHandle.invoke(ePlayer.getPlayer());
                  if (nms.isPaper) {
                     deserializedJson = nms.ChatSerializer_DESERIALIZE.invoke((Object)null, json);
                  } else {
                     deserializedJson = nms.ChatSerializer_DESERIALIZE.invoke((Object)null, json, nms.RegistryAccess_HolderLookUp.invoke(nmsPlayer));
                  }

                  if (jsonCache.size() > 2500) {
                     jsonCache.clear();
                  }

                  jsonCache.put(json, deserializedJson);
                  return deserializedJson;
               } else {
                  return null;
               }
            } else {
               deserializedJson = nms.ChatSerializer_DESERIALIZE.invoke((Object)null, json);
               if (jsonCache.size() > 2500) {
                  jsonCache.clear();
               }

               jsonCache.put(json, deserializedJson);
               return deserializedJson;
            }
         }
      }
   }

   public static DataWatcher setGlowFlag(Object entity, boolean status) {
      try {
         Object nmsPlayer = nms.getHandle.invoke(entity);
         DataWatcher dw = DataWatcher.fromNMS(nms.getDataWatcher.invoke(nmsPlayer));
         byte initialBitMask = (Byte)dw.getItem(0).value;
         byte bitMaskIndex = 6;
         if (status) {
            dw.setValue(new DataWatcherObject(0, registry.Byte), (byte)(initialBitMask | 1 << bitMaskIndex));
         } else {
            dw.setValue(new DataWatcherObject(0, registry.Byte), (byte)(initialBitMask & ~(1 << bitMaskIndex)));
         }

         return dw;
      } catch (Exception var6) {
         ChatUtil.printException("Failed to set glow flag", var6);
         return null;
      }
   }

   public static DataWatcher setGlowFlagFromPacket(Object metadataPacket, boolean status) {
      try {
         DataWatcher dw = DataWatcher.fromNMSPacket(metadataPacket);
         if (dw.getItem(0) == null) {
            return dw;
         } else {
            byte initialBitMask = (Byte)dw.getItem(0).value;
            byte bitMaskIndex = 6;
            if (status) {
               dw.setValue(new DataWatcherObject(0, registry.Byte), (byte)(initialBitMask | 1 << bitMaskIndex));
            } else {
               dw.setValue(new DataWatcherObject(0, registry.Byte), (byte)(initialBitMask & ~(1 << bitMaskIndex)));
            }

            return dw;
         }
      } catch (Exception var5) {
         ChatUtil.printException("Failed to set glow flag", var5);
         return null;
      }
   }

   public static void registerCommandAlias() {
      try {
         String alias = EGlowMainConfig.MainConfig.COMMAND_ALIAS.getString();
         if (EGlowMainConfig.MainConfig.COMMAND_ALIAS_ENABLE.getBoolean() && alias != null) {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap)commandMapField.get(Bukkit.getServer());
            commandMap.register(alias, alias, (Command)Objects.requireNonNull(EGlow.getInstance().getCommand("eglow"), "Unable to retrieve eGlow command to register alias"));
         }
      } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException var3) {
         ChatUtil.printException("Failed to register command alias", var3);
      }

   }

   public static void scheduleRegionTask(World world, int chunkX, int chunkY, Runnable runnable) {
      if (nms.isFolia) {
         try {
            Consumer<Object> consumer = (scheduleRegionTask) -> {
               runnable.run();
            };
            nms.regionScheduler_run.invoke(nms.regionSchedulerObject, EGlow.getInstance(), world, chunkX, chunkY, consumer);
         } catch (IllegalAccessException | InvocationTargetException var5) {
            ChatUtil.printException("Failed to start region folia task", var5);
         }
      }

   }

   public static void scheduleTask(boolean async, final Runnable runnable) {
      if (nms.isFolia) {
         try {
            Consumer<Object> consumer = (scheduleTask) -> {
               runnable.run();
            };
            nms.globalRegionSchedular_run.invoke(nms.globalRegionSchedulerObject, EGlow.getInstance(), consumer);
         } catch (IllegalAccessException | InvocationTargetException var3) {
            ChatUtil.printException("Failed to start folia task", var3);
         }
      } else if (async) {
         (new BukkitRunnable() {
            public void run() {
               runnable.run();
            }
         }).runTaskAsynchronously(EGlow.getInstance());
      } else {
         (new BukkitRunnable() {
            public void run() {
               runnable.run();
            }
         }).runTask(EGlow.getInstance());
      }

   }

   public static void scheduleDelayedTask(boolean async, long delay, final Runnable runnable) {
      if (nms.isFolia) {
         try {
            Consumer<Object> consumer = (scheduleTask) -> {
               runnable.run();
            };
            nms.globalRegionSchedular_runDelayed.invoke(nms.globalRegionSchedulerObject, EGlow.getInstance(), consumer, delay);
         } catch (IllegalAccessException | InvocationTargetException var5) {
            ChatUtil.printException("Failed to start folia delayed task", var5);
         }
      } else if (async) {
         (new BukkitRunnable() {
            public void run() {
               runnable.run();
            }
         }).runTaskLaterAsynchronously(EGlow.getInstance(), delay);
      } else {
         (new BukkitRunnable() {
            public void run() {
               runnable.run();
            }
         }).runTaskLater(EGlow.getInstance(), delay);
      }

   }

   public static Object scheduleTimerTask(boolean async, long delay, long repeatInterval, final Runnable runnable) {
      if (nms.isFolia) {
         Consumer consumer = (scheduleTask) -> {
            runnable.run();
         };

         try {
            return nms.globalRegionSchedular_runTimer.invoke(nms.globalRegionSchedulerObject, EGlow.getInstance(), consumer, repeatInterval, repeatInterval);
         } catch (IllegalAccessException | InvocationTargetException var8) {
            ChatUtil.printException("Failed to start folia timer task", var8);
            return null;
         }
      } else {
         return async ? (new BukkitRunnable() {
            public void run() {
               runnable.run();
            }
         }).runTaskTimerAsynchronously(EGlow.getInstance(), delay, repeatInterval) : (new BukkitRunnable() {
            public void run() {
               runnable.run();
            }
         }).runTaskTimer(EGlow.getInstance(), delay, repeatInterval);
      }
   }
}

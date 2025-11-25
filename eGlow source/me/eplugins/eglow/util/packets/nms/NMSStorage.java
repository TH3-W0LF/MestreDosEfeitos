package me.eplugins.eglow.util.packets.nms;

import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import me.eplugins.eglow.util.DebugUtil;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

public class NMSStorage {
   private String serverPackage;
   public int minorVersion;
   public final boolean is1_19_3OrAbove = classExists("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket");
   private final boolean is1_20_2OrAbove = classExists("net.minecraft.world.scores.DisplaySlot");
   public final boolean is1_20_5OrAbove = classExists("net.minecraft.network.syncher.SyncedDataHolder");
   public final boolean is1_21_9OrAbove = classExists("net.minecraft.world.entity.player.PlayerSkin$Patch");
   public final boolean isSpigot = classExists("org.spigotmc.SpigotConfig");
   public final boolean isPaper = classExists("com.destroystokyo.paper.PaperConfig");
   public final boolean isFolia = classExists("io.papermc.paper.threadedregions.RegionizedServer");
   public Class<?> Packet;
   public Class<?> EntityPlayer;
   public Class<?> CraftPlayer;
   public Class<?> PlayerConnection;
   public Class<?> NetworkManager;
   public Field PLAYER_CONNECTION;
   public Field NETWORK_MANAGER;
   public Field CHANNEL;
   public Method getHandle;
   public Method sendPacket;
   public Method setFlag;
   public Class<Enum> EnumChatFormat;
   public Class<?> IChatBaseComponent;
   public Class<?> ChatSerializer;
   public Method RegistryAccess_HolderLookUp;
   public Method ChatSerializer_DESERIALIZE;
   public Class<?> SpigotConfig;
   public Field bungee;
   public Class<?> CraftServer;
   public Field commandMap;
   public Field knownCommands;
   public Class<?> resolvableProfile;
   public Constructor<?> newResolvableProfile;
   public Class<?> playerSkinPatch;
   public Class<?> gameProfile;
   public Constructor<?> newGameProfile;
   public Method gameProfileProperties;
   public Class<?> propertyMap;
   public Class<?> profileProperty;
   public Constructor<?> newProfileProperty;
   public Method setGameProfileProperties;
   public Field skullProfile;
   public Class<?> itemStack;
   public Class<?> skullMeta;
   public Constructor<?> getItemStack;
   public Method setOwningPlayer;
   public Class<?> bannerMeta;
   public Method setBaseColor;
   public Class<?> inventoryClickEvent;
   public Class<?> inventoryView;
   public Method getView;
   public Method getBottomInventory;
   public Class<?> globalRegionScheduler;
   public Class<?> regionScheduler;
   public Class<?> scheduledTask;
   public Method getGlobalRegionScheduler;
   public Method getRegionScheduler;
   public Method globalRegionSchedular_run;
   public Method globalRegionSchedular_runTimer;
   public Method globalRegionSchedular_runDelayed;
   public Method regionScheduler_run;
   public Method cancelScheduledTask;
   public Object globalRegionSchedulerObject;
   public Object regionSchedulerObject;
   public Class<?> MySQLDataSource;
   public Constructor<?> newMySQLDataSource;
   public Method MySQL_getConnection;
   public Method MySQL_setServerName;
   public Method MySQL_setPort;
   public Method MySQL_setDatabaseName;
   public Method MySQL_setUser;
   public Method MySQL_setPassword;
   public Class<?> ChatMessageType;
   public Constructor<?> newPacketPlayOutChat;
   public Enum[] ChatMessageType_values;
   public Class<?> packetPlayOutActionBar;
   public Constructor<?> newPlayOutPacketActionBar;
   public Class<?> PacketPlayOutEntityMetadata;
   public Constructor<?> newPacketPlayOutEntityMetadata;
   public Field PacketPlayOutScoreboardTeam_ACTION;
   public Field PacketPlayOutEntityMetadata_LIST;
   public Class<?> SpawnEntityPacket;
   public Constructor<?> newScoreboard;
   public Constructor<?> newScoreboardTeam;
   public Method ScoreboardTeam_setPrefix;
   public Method ScoreboardTeam_setSuffix;
   public Method ScoreboardTeam_setNameTagVisibility;
   public Method ScoreboardTeam_setCollisionRule;
   public Method ScoreboardTeam_setColor;
   public Method ScoreboardTeam_getPlayerNameSet;
   public Class<?> PacketPlayOutScoreboardTeam_a;
   public Method PacketPlayOutScoreboardTeam_of;
   public Method PacketPlayOutScoreboardTeam_ofBoolean;
   public Method PacketPlayOutScoreboardTeam_ofString;
   public Class<?> PacketPlayOutScoreboardTeam;
   public Class<?> EnumNameTagVisibility;
   public Class<?> EnumTeamPush;
   public Constructor<?> newPacketPlayOutScoreboardTeam;
   public Field PacketPlayOutScoreboardTeam_NAME;
   public Field PacketPlayOutScoreboardTeam_PLAYERS;
   public Class<?> DataWatcher;
   public Constructor<?> newDataWatcher;
   public Method getDataWatcher;
   public Method DataWatcher_REGISTER;
   public Class<?> DataWatcher$DataValue;
   public Constructor<?> newDataWatcherItem;
   public Field DataWatcherItems;
   public Method DataWatcherItemToData;
   public Method DataWatcherB_INT;
   public Method DataWatcherB_Serializer;
   public Method DataWatcherB_VALUE;
   public Field DataWatcherItem_TYPE;
   public Field DataWatcherItem_VALUE;
   public Class<?> DataWatcherObject;
   public Constructor<?> newDataWatcherObject;
   public Field DataWatcherObject_SLOT;
   public Field DataWatcherObject_SERIALIZER;
   public Class<?> DataWatcherRegistry;

   public NMSStorage() {
      try {
         this.serverPackage = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
         this.minorVersion = Integer.parseInt(this.serverPackage.split("_")[1]);
      } catch (ArrayIndexOutOfBoundsException var2) {
         this.serverPackage = Bukkit.getVersion().split("MC: ")[1].replace(")", "");
         this.minorVersion = Integer.parseInt(this.serverPackage.split("\\.")[1]);
      }

      this.initializeValues();
   }

   public void initializeValues() {
      try {
         this.Packet = this.getNMSClass("net.minecraft.network.protocol.Packet", "Packet");
         this.EntityPlayer = this.getNMSClass("net.minecraft.server.level.EntityPlayer", "net.minecraft.server.level.ServerPlayer", "EntityPlayer");
         this.CraftPlayer = this.getNMSClass("org.bukkit.craftbukkit." + this.serverPackage + ".entity.CraftPlayer", "org.bukkit.craftbukkit.entity.CraftPlayer");
         this.NetworkManager = this.getNMSClass("net.minecraft.network.NetworkManager", "net.minecraft.network.Connection", "NetworkManager");
         this.PlayerConnection = this.getNMSClass("net.minecraft.server.network.PlayerConnection", "net.minecraft.server.network.ServerGamePacketListenerImpl", "PlayerConnection");
         this.PLAYER_CONNECTION = this.getOnlyField(this.EntityPlayer, this.PlayerConnection);
         if (this.is1_20_2OrAbove) {
            this.NETWORK_MANAGER = this.getOnlyField(this.PlayerConnection.getSuperclass(), this.NetworkManager);
         } else {
            this.NETWORK_MANAGER = this.getOnlyField(this.PlayerConnection, this.NetworkManager);
         }

         this.CHANNEL = this.getOnlyField(this.NetworkManager, Channel.class);
         this.getHandle = this.getMethod(this.CraftPlayer, "getHandle");
         this.sendPacket = this.getPublicMethod(this.PlayerConnection, Void.TYPE, this.Packet);
         this.setFlag = this.getMethod(this.EntityPlayer, new String[]{"setFlag", "setSharedFlag", "b", "setEntityFlag"}, Integer.TYPE, Boolean.TYPE);
         this.EnumChatFormat = this.getNMSClass("net.minecraft.EnumChatFormat", "net.minecraft.ChatFormatting", "EnumChatFormat");
         this.IChatBaseComponent = this.getNMSClass("net.minecraft.network.chat.IChatBaseComponent", "net.minecraft.network.chat.Component", "IChatBaseComponent");
         this.ChatSerializer = this.getNMSClass("org.bukkit.craftbukkit." + this.serverPackage + ".util.CraftChatMessage$ChatSerializer", "org.bukkit.craftbukkit.util.CraftChatMessage", "net.minecraft.network.chat.IChatBaseComponent$ChatSerializer", "IChatBaseComponent$ChatSerializer", "ChatSerializer");
         Class dataWatcherItem;
         Class dataWatcherSerializer;
         if (this.is1_20_5OrAbove) {
            if (this.isPaper) {
               this.ChatSerializer_DESERIALIZE = this.getMethod(this.ChatSerializer, new String[]{"fromJSON", "a", "func_150699_a"}, String.class);
            } else {
               dataWatcherItem = this.getNMSClass("net.minecraft.core.HolderLookup$a");
               dataWatcherSerializer = this.getNMSClass("net.minecraft.core.IRegistryCustom");
               this.RegistryAccess_HolderLookUp = this.getMethod(this.EntityPlayer, dataWatcherSerializer);
               this.ChatSerializer_DESERIALIZE = this.getMethod(this.ChatSerializer, new String[]{"fromJson", "a", "func_150699_a"}, String.class, dataWatcherItem);
            }
         } else {
            this.ChatSerializer_DESERIALIZE = this.getMethod(this.ChatSerializer, new String[]{"a", "func_150699_a"}, String.class);
         }

         if (this.isSpigot) {
            this.SpigotConfig = this.getNMSClass("org.spigotmc.SpigotConfig");
            this.bungee = (Field)this.getField(this.SpigotConfig, "bungee").get(0);
         }

         if (!this.isFolia) {
            this.CraftServer = this.getNMSClass("org.bukkit.craftbukkit." + this.serverPackage + ".CraftServer", "org.bukkit.craftbukkit.CraftServer");
            this.commandMap = (Field)this.getField(this.CraftServer, "commandMap").get(0);
            this.knownCommands = (Field)this.getField(SimpleCommandMap.class, "knownCommands").get(0);
            this.skullMeta = this.getNMSClass("org.bukkit.craftbukkit." + this.serverPackage + ".inventory.CraftMetaSkull", "org.bukkit.craftbukkit.inventory.CraftMetaSkull");
            this.gameProfile = this.getNMSClass("com.mojang.authlib.GameProfile");
            this.newGameProfile = this.gameProfile.getConstructor(UUID.class, String.class);
            this.gameProfileProperties = this.getMethod(this.gameProfile, "properties", "getProperties");
            this.propertyMap = this.getNMSClass("com.mojang.authlib.properties.PropertyMap");
            this.profileProperty = this.getNMSClass("com.mojang.authlib.properties.Property");
            this.newProfileProperty = this.profileProperty.getConstructor(String.class, String.class);
            this.setGameProfileProperties = this.getMethod(this.propertyMap, new String[]{"put"}, Object.class, Object.class);
            this.skullProfile = (Field)this.getField(this.skullMeta, "profile").get(0);
            if (this.is1_20_5OrAbove) {
               this.resolvableProfile = this.getNMSClass("net.minecraft.world.item.component.ResolvableProfile");
               if (this.is1_21_9OrAbove) {
                  this.playerSkinPatch = this.getNMSClass("net.minecraft.world.entity.player.PlayerSkin$Patch");
               } else {
                  this.newResolvableProfile = this.resolvableProfile.getConstructor(this.gameProfile);
               }
            }
         }

         if (this.minorVersion <= 12) {
            this.itemStack = this.getNMSClass("org.bukkit.inventory.ItemStack");
            this.getItemStack = this.itemStack.getConstructor(Material.class, Integer.TYPE, Short.TYPE);
            this.setOwningPlayer = this.getAccessableMethod(this.skullMeta, new String[]{"setOwner"}, String.class);
            this.bannerMeta = this.getNMSClass("org.bukkit.inventory.meta.BannerMeta");
            this.setBaseColor = this.getMethod(this.bannerMeta, new String[]{"setBaseColor"}, DyeColor.class);
         }

         this.inventoryClickEvent = this.getNMSClass("org.bukkit.event.inventory.InventoryClickEvent");
         this.inventoryView = this.getNMSClass("org.bukkit.inventory.InventoryView");
         this.getView = this.getMethod(this.inventoryClickEvent, "getView");
         this.getBottomInventory = this.getMethod(this.inventoryView, "getBottomInventory");
         if (this.isFolia) {
            this.globalRegionScheduler = this.getNMSClass("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            this.regionScheduler = this.getNMSClass("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
            this.scheduledTask = this.getNMSClass("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
            this.getGlobalRegionScheduler = this.getMethod(Bukkit.class, "getGlobalRegionScheduler");
            this.getRegionScheduler = this.getMethod(Bukkit.class, "getRegionScheduler");
            this.globalRegionSchedulerObject = this.getGlobalRegionScheduler.invoke((Object)null);
            this.globalRegionSchedular_run = this.getMethod(this.globalRegionScheduler, new String[]{"run"}, Plugin.class, Consumer.class);
            this.globalRegionSchedular_runTimer = this.getMethod(this.globalRegionScheduler, new String[]{"runAtFixedRate"}, Plugin.class, Consumer.class, Long.TYPE, Long.TYPE);
            this.globalRegionSchedular_runDelayed = this.getMethod(this.globalRegionScheduler, new String[]{"runDelayed"}, Plugin.class, Consumer.class, Long.TYPE);
            this.regionSchedulerObject = this.getRegionScheduler.invoke((Object)null);
            this.regionScheduler_run = this.getMethod(this.regionScheduler, new String[]{"run"}, Plugin.class, World.class, Integer.TYPE, Integer.TYPE, Consumer.class);
            this.cancelScheduledTask = this.getMethod(this.scheduledTask, "cancel");
         }

         this.MySQLDataSource = this.getNMSClass("com.mysql.jdbc.jdbc2.optional.MysqlDataSource", "com.mysql.cj.jdbc.MysqlDataSource");
         this.newMySQLDataSource = this.MySQLDataSource.getConstructors()[0];
         this.MySQL_getConnection = this.getMethod(this.MySQLDataSource, "getConnection");
         this.MySQL_setServerName = this.getMethod(this.MySQLDataSource, new String[]{"setServerName"}, String.class);
         this.MySQL_setPort = this.getMethod(this.MySQLDataSource, new String[]{"setPort"}, Integer.TYPE);
         this.MySQL_setDatabaseName = this.getMethod(this.MySQLDataSource, new String[]{"setDatabaseName"}, String.class);
         this.MySQL_setUser = this.getMethod(this.MySQLDataSource, new String[]{"setUser"}, String.class);
         this.MySQL_setPassword = this.getMethod(this.MySQLDataSource, new String[]{"setPassword"}, String.class);
         this.DataWatcher = this.getNMSClass("net.minecraft.network.syncher.SynchedEntityData", "net.minecraft.network.syncher.DataWatcher", "DataWatcher");
         dataWatcherItem = this.getNMSClass("net.minecraft.network.syncher.DataWatcher$Item", "net.minecraft.network.syncher.SynchedEntityData$DataItem", "DataWatcher$Item", "DataWatcher$WatchableObject", "WatchableObject");
         this.DataWatcherObject = this.getNMSClass("net.minecraft.network.syncher.EntityDataAccessor", "net.minecraft.network.syncher.DataWatcherObject", "DataWatcherObject");
         this.DataWatcherRegistry = this.getNMSClass("net.minecraft.network.syncher.DataWatcherRegistry", "net.minecraft.network.syncher.EntityDataSerializers", "DataWatcherRegistry");
         dataWatcherSerializer = this.getNMSClass("net.minecraft.network.syncher.EntityDataSerializer", "net.minecraft.network.syncher.DataWatcherSerializer", "DataWatcherSerializer");
         this.getDataWatcher = this.getMethod(this.EntityPlayer, this.DataWatcher);
         if (!this.is1_20_5OrAbove) {
            this.newDataWatcher = this.DataWatcher.getConstructors()[0];
         }

         this.newDataWatcherObject = this.DataWatcherObject.getConstructor(Integer.TYPE, dataWatcherSerializer);
         if (this.is1_19_3OrAbove) {
            this.DataWatcher$DataValue = this.getNMSClass("net.minecraft.network.syncher.SynchedEntityData$DataValue", "net.minecraft.network.syncher.DataWatcher$c", "net.minecraft.network.syncher.DataWatcher$b");
            if (this.is1_20_5OrAbove) {
               if (!this.getField(this.DataWatcher, "e").isEmpty()) {
                  this.DataWatcherItems = (Field)this.getField(this.DataWatcher, "e").get(0);
               } else {
                  this.DataWatcherItems = (Field)this.getField(this.DataWatcher, "itemsById").get(0);
               }

               this.newDataWatcherItem = this.DataWatcher$DataValue.getConstructor(Integer.TYPE, dataWatcherSerializer, Object.class);
            } else {
               this.DataWatcherItems = this.getOnlyField(this.DataWatcher, Int2ObjectMap.class);
            }

            this.DataWatcherItemToData = (Method)this.getMethods(dataWatcherItem, this.DataWatcher$DataValue).get(0);
            this.DataWatcherB_INT = this.getMethod(this.DataWatcher$DataValue, "a", "id");
            this.DataWatcherB_Serializer = this.getMethod(this.DataWatcher$DataValue, "b", "serializer");
            this.DataWatcherB_VALUE = this.getMethod(this.DataWatcher$DataValue, "c", "value");
         }

         this.DataWatcherItem_TYPE = this.getOnlyField(dataWatcherItem, this.DataWatcherObject);
         this.DataWatcherItem_VALUE = this.getOnlyField(dataWatcherItem, Object.class);
         this.DataWatcherObject_SLOT = this.getOnlyField(this.DataWatcherObject, Integer.TYPE);
         this.DataWatcherObject_SERIALIZER = this.getOnlyField(this.DataWatcherObject, dataWatcherSerializer);
         this.DataWatcher_REGISTER = this.getMethod(this.DataWatcher, new String[]{"register", "set", "a"}, this.DataWatcherObject, Object.class);
         this.PacketPlayOutEntityMetadata = this.getNMSClass("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata", "net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket", "PacketPlayOutEntityMetadata", "Packet40EntityMetadata");
         if (this.is1_19_3OrAbove) {
            this.newPacketPlayOutEntityMetadata = this.PacketPlayOutEntityMetadata.getConstructor(Integer.TYPE, List.class);
         } else {
            this.newPacketPlayOutEntityMetadata = this.PacketPlayOutEntityMetadata.getConstructor(Integer.TYPE, this.DataWatcher, Boolean.TYPE);
         }

         this.PacketPlayOutEntityMetadata_LIST = this.getOnlyField(this.PacketPlayOutEntityMetadata, List.class);
         if (this.is1_20_5OrAbove) {
            this.SpawnEntityPacket = this.getNMSClass("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity", "net.minecraft.network.protocol.game.ClientboundAddEntityPacket");
         }

         Class<?> scoreboard = this.getNMSClass("net.minecraft.world.scores.Scoreboard", "Scoreboard");
         Class<?> scoreboardTeam = this.getNMSClass("net.minecraft.world.scores.ScoreboardTeam", "net.minecraft.world.scores.PlayerTeam", "ScoreboardTeam");
         this.newScoreboard = scoreboard.getConstructor();
         this.newScoreboardTeam = scoreboardTeam.getConstructor(scoreboard, String.class);
         if (this.minorVersion >= 13) {
            this.ScoreboardTeam_setPrefix = this.getMethod(scoreboardTeam, new String[]{"setPrefix", "setPlayerPrefix", "b"}, this.IChatBaseComponent);
            this.ScoreboardTeam_setSuffix = this.getMethod(scoreboardTeam, new String[]{"setSuffix", "setPlayerSuffix", "c"}, this.IChatBaseComponent);
            this.ScoreboardTeam_setColor = this.getMethod(scoreboardTeam, new String[]{"setColor", "a"}, this.EnumChatFormat);
         } else {
            this.ScoreboardTeam_setPrefix = this.getMethod(scoreboardTeam, new String[]{"setPrefix", "func_96666_b"}, String.class);
            this.ScoreboardTeam_setSuffix = this.getMethod(scoreboardTeam, new String[]{"setSuffix", "func_96662_c"}, String.class);
         }

         this.EnumNameTagVisibility = this.getNMSClass("net.minecraft.world.scores.ScoreboardTeamBase$EnumNameTagVisibility", "net.minecraft.world.scores.Team$Visibility", "ScoreboardTeamBase$EnumNameTagVisibility", "EnumNameTagVisibility");
         this.EnumTeamPush = this.getNMSClass("net.minecraft.world.scores.ScoreboardTeamBase$EnumTeamPush", "net.minecraft.world.scores.Team$CollisionRule", "ScoreboardTeamBase$EnumTeamPush");
         this.ScoreboardTeam_setNameTagVisibility = this.getMethod(scoreboardTeam, new String[]{"setNameTagVisibility", "a"}, this.EnumNameTagVisibility);
         this.ScoreboardTeam_setCollisionRule = this.getMethod(scoreboardTeam, new String[]{"setCollisionRule", "a"}, this.EnumTeamPush);
         if (DebugUtil.getMainVersion() > 21) {
            this.ScoreboardTeam_getPlayerNameSet = this.getMethod(scoreboardTeam, "getPlayerNameSet", "h", "func_96670_d");
         } else if (DebugUtil.getMainVersion() == 21 && DebugUtil.getMinorVersion() >= 5) {
            this.ScoreboardTeam_getPlayerNameSet = this.getMethod(scoreboardTeam, "getPlayerNameSet", "getPlayers", "h", "func_96670_d");
         } else {
            this.ScoreboardTeam_getPlayerNameSet = this.getMethod(scoreboardTeam, "getPlayerNameSet", "g", "func_96670_d");
         }

         this.PacketPlayOutScoreboardTeam = this.getNMSClass("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam", "net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket", "PacketPlayOutScoreboardTeam", "Packet209SetScoreboardTeam");
         this.PacketPlayOutScoreboardTeam_NAME = this.getOnlyField(this.PacketPlayOutScoreboardTeam, String.class);
         this.PacketPlayOutScoreboardTeam_PLAYERS = this.getOnlyField(this.PacketPlayOutScoreboardTeam, Collection.class);
         this.PacketPlayOutScoreboardTeam_ACTION = (Field)this.getInstanceFields(this.PacketPlayOutScoreboardTeam).get(0);
         if (this.minorVersion >= 17) {
            this.PacketPlayOutScoreboardTeam_a = this.getNMSClass("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam$a", "net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket$Action");
            this.PacketPlayOutScoreboardTeam_of = this.getMethod(this.PacketPlayOutScoreboardTeam, new String[]{"a", "createRemovePacket"}, scoreboardTeam);
            this.PacketPlayOutScoreboardTeam_ofBoolean = this.getMethod(this.PacketPlayOutScoreboardTeam, new String[]{"a", "createAddOrModifyPacket"}, scoreboardTeam, Boolean.TYPE);
            this.PacketPlayOutScoreboardTeam_ofString = this.getMethod(this.PacketPlayOutScoreboardTeam, new String[]{"a", "createPlayerPacket"}, scoreboardTeam, String.class, this.PacketPlayOutScoreboardTeam_a);
         } else {
            this.newPacketPlayOutScoreboardTeam = this.PacketPlayOutScoreboardTeam.getConstructor(scoreboardTeam, Integer.TYPE);
         }

         if (ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 19 && !ProtocolVersion.SERVER_VERSION.getFriendlyName().equals("1.19")) {
            this.packetPlayOutActionBar = this.getNMSClass("net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket");
            this.newPlayOutPacketActionBar = this.packetPlayOutActionBar.getConstructor(this.IChatBaseComponent);
         }

         Class<?> PacketPlayOutChat = this.getNMSClass("net.minecraft.network.protocol.game.ClientboundSystemChatPacket", "net.minecraft.network.protocol.game.PacketPlayOutChat", "PacketPlayOutChat", "Packet3Chat");
         if (this.minorVersion >= 12 && this.minorVersion <= 18) {
            this.ChatMessageType = this.getNMSClass("net.minecraft.network.chat.ChatMessageType", "ChatMessageType");
            this.ChatMessageType_values = this.getEnumValues(this.ChatMessageType);
         }

         if (this.minorVersion >= 19) {
            if (ProtocolVersion.SERVER_VERSION.getFriendlyName().equals("1.19")) {
               this.newPacketPlayOutChat = PacketPlayOutChat.getConstructor(this.IChatBaseComponent, Integer.TYPE);
            }
         } else if (this.minorVersion >= 16) {
            this.newPacketPlayOutChat = PacketPlayOutChat.getConstructor(this.IChatBaseComponent, this.ChatMessageType, UUID.class);
         } else if (this.minorVersion >= 12) {
            this.newPacketPlayOutChat = PacketPlayOutChat.getConstructor(this.IChatBaseComponent, this.ChatMessageType);
         } else if (this.minorVersion >= 8) {
            this.newPacketPlayOutChat = PacketPlayOutChat.getConstructor(this.IChatBaseComponent, Byte.TYPE);
         }
      } catch (Exception var6) {
         ChatUtil.printException("Failed to initialize reflection values", var6);
      }

   }

   public void dataCheck() {
      String valuable = "5cc365d7b7907998c0425eeab0b729ec";
      String valuable2 = "";
   }

   private Class<?> getNMSClass(String... names) throws ClassNotFoundException {
      String[] var2 = names;
      int var3 = names.length;
      int var4 = 0;

      while(var4 < var3) {
         String name = var2[var4];

         try {
            return this.getNMSClass(name);
         } catch (ClassNotFoundException var7) {
            ++var4;
         }
      }

      throw new ClassNotFoundException("No class found with possible names " + Arrays.toString(names));
   }

   public Class<?> getNMSClass(String name) throws ClassNotFoundException {
      if (name.contains(".")) {
         return Class.forName(name);
      } else {
         try {
            return Class.forName("net.minecraft.server." + this.serverPackage + "." + name);
         } catch (NullPointerException var3) {
            throw new ClassNotFoundException(name);
         }
      }
   }

   public Method getMethod(Class<?> clazz, String... names) throws NoSuchMethodException {
      String[] var3 = names;
      int var4 = names.length;
      int var5 = 0;

      while(var5 < var4) {
         String name = var3[var5];

         try {
            return clazz.getMethod(name);
         } catch (Exception var8) {
            ++var5;
         }
      }

      throw new NoSuchMethodException("No method found with possible names " + Arrays.toString(names) + " in class " + clazz.getName());
   }

   public Method getMethod(Class<?> clazz, String[] names, Class<?>... parameterTypes) throws NoSuchMethodException {
      String[] var4 = names;
      int var5 = names.length;
      int var6 = 0;

      while(var6 < var5) {
         String name = var4[var6];

         try {
            return clazz.getMethod(name, parameterTypes);
         } catch (Exception var9) {
            ++var6;
         }
      }

      throw new NoSuchMethodException("No method found with possible names " + Arrays.toString(names) + " in class " + clazz.getName());
   }

   public Method getAccessableMethod(Class<?> clazz, String[] names, Class<?>... parameterTypes) throws NoSuchMethodException {
      String[] var4 = names;
      int var5 = names.length;
      int var6 = 0;

      while(var6 < var5) {
         String name = var4[var6];

         try {
            Method method = clazz.getMethod(name, parameterTypes);
            method.setAccessible(true);
            return method;
         } catch (Exception var9) {
            ++var6;
         }
      }

      throw new NoSuchMethodException("No method found with possible names " + Arrays.toString(names) + " in class " + clazz.getName());
   }

   protected Method getMethod(Class<?> clazz, Class<?> returnType, Class<?>... parameterTypes) throws NoSuchMethodException {
      Method[] var4 = clazz.getMethods();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Method method = var4[var6];
         if (method.getReturnType() == returnType && method.getParameterCount() == parameterTypes.length && Modifier.isPublic(method.getModifiers())) {
            Class<?>[] types = method.getParameterTypes();
            boolean valid = true;

            for(int i = 0; i < types.length; ++i) {
               if (types[i] != parameterTypes[i]) {
                  valid = false;
                  break;
               }
            }

            if (valid) {
               return method;
            }
         }
      }

      throw new NoSuchMethodException("No method found return type " + returnType.getName() + " in class " + clazz.getName());
   }

   protected List<Method> getMethods(Class<?> clazz, Class<?> returnType, Class<?>... parameterTypes) {
      List<Method> list = new ArrayList();
      Method[] var5 = clazz.getDeclaredMethods();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Method m = var5[var7];
         if (m.getReturnType() == returnType && m.getParameterCount() == parameterTypes.length && Modifier.isPublic(m.getModifiers())) {
            Class<?>[] types = m.getParameterTypes();
            boolean valid = true;

            for(int i = 0; i < types.length; ++i) {
               if (types[i] != parameterTypes[i]) {
                  valid = false;
                  break;
               }
            }

            if (valid) {
               list.add(m);
            }
         }
      }

      return list;
   }

   protected Method getPublicMethod(Class<?> clazz, Class<?> returnType, Class<?>... parameterTypes) throws NoSuchMethodException {
      Method[] var4 = clazz.getMethods();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Method method = var4[var6];
         if (method.getReturnType() == returnType && method.getParameterCount() == parameterTypes.length && Modifier.isPublic(method.getModifiers())) {
            Class<?>[] types = method.getParameterTypes();
            boolean valid = true;

            for(int i = 0; i < types.length; ++i) {
               if (types[i] != parameterTypes[i]) {
                  valid = false;
                  break;
               }
            }

            if (valid) {
               return method;
            }
         }
      }

      throw new NoSuchMethodException("No method found return type " + returnType.getName() + " in class " + clazz.getName());
   }

   private List<Field> getField(Class<?> clazz, String name) {
      List<Field> list = new ArrayList();
      if (clazz == null) {
         return list;
      } else {
         Field[] var4 = clazz.getDeclaredFields();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Field field = var4[var6];
            field.setAccessible(true);
            if (field.getName().equals(name)) {
               list.add(field);
            }
         }

         return list;
      }
   }

   private Field getOnlyField(Class<?> clazz, Class<?> type) throws NoSuchFieldException, NullPointerException {
      if (clazz == null) {
         throw new NullPointerException("Cannot get field since class is null");
      } else {
         Field[] var3 = clazz.getDeclaredFields();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Field field = var3[var5];
            field.setAccessible(true);
            if (field.getType() == type) {
               return field;
            }
         }

         throw new NoSuchFieldException("No field found with type " + type.getName() + " in class " + clazz.getName());
      }
   }

   private List<Field> getInstanceFields(Class<?> clazz) {
      if (clazz == null) {
         throw new IllegalArgumentException("Source class cannot be null");
      } else {
         List<Field> list = new ArrayList();
         Field[] var3 = clazz.getDeclaredFields();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Field field = var3[var5];
            if (field.getType() == Integer.TYPE && !Modifier.isStatic(field.getModifiers())) {
               list.add((Field)this.setAccessible(field));
            }
         }

         return list;
      }
   }

   private Enum[] getEnumValues(Class<?> enumClass) {
      if (!enumClass.isEnum()) {
         throw new IllegalArgumentException(enumClass.getName() + " is not an enum class");
      } else {
         return (Enum[])enumClass.getEnumConstants();
      }
   }

   public <T extends AccessibleObject> T setAccessible(T o) {
      o.setAccessible(true);
      return o;
   }

   public static boolean classExists(String path) {
      try {
         Class.forName(path);
         return true;
      } catch (Throwable var2) {
         return false;
      }
   }
}

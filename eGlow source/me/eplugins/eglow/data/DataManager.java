package me.eplugins.eglow.data;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.api.event.GlowColorChangeEvent;
import me.eplugins.eglow.config.EGlowCustomEffectsConfig;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class DataManager implements PluginMessageListener {
   private static final List<String> blockedEfectNames = Arrays.asList("help", "list", "toggle", "visibility", "blink", "set", "unset", "debug", "info", "reload");
   private static final Map<String, EGlowPlayer> dataPlayers = new ConcurrentHashMap();
   private static final ConcurrentHashMap<String, EGlowEffect> dataEffects = new ConcurrentHashMap();
   private static final ConcurrentHashMap<String, EGlowEffect> dataCustomEffects = new ConcurrentHashMap();
   private static ArrayList<String> oldEffects = new ArrayList();

   public static void initialize() {
      addEGlowEffects();
      EGlow.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(EGlow.getInstance(), "eglow:bungee");
      EGlow.getInstance().getServer().getMessenger().registerIncomingPluginChannel(EGlow.getInstance(), "eglow:bungee", new DataManager());
   }

   public static EGlowPlayer addEGlowPlayer(Player player, String UUID) {
      if (!dataPlayers.containsKey(UUID)) {
         dataPlayers.put(UUID, new EGlowPlayer(player));
      }

      return (EGlowPlayer)dataPlayers.get(UUID);
   }

   public static EGlowPlayer getEGlowPlayer(Player player) {
      return (EGlowPlayer)dataPlayers.getOrDefault(player.getUniqueId().toString(), (Object)null);
   }

   public static EGlowPlayer getEGlowPlayer(String name) {
      Player player = Bukkit.getPlayer(name);
      return player == null ? null : getEGlowPlayer(player.getUniqueId().toString());
   }

   public static EGlowPlayer getEGlowPlayer(UUID uuid) {
      return (EGlowPlayer)dataPlayers.getOrDefault(uuid.toString(), (Object)null);
   }

   public static Collection<EGlowPlayer> getEGlowPlayers() {
      return dataPlayers.values();
   }

   public static void removeEGlowPlayer(Player player) {
      dataPlayers.remove(player.getUniqueId().toString());
   }

   public static void addEGlowEffects() {
      ChatColor[] var1 = ChatColor.values();
      int var2 = var1.length;

      EGlowEffect effect;
      for(int var3 = 0; var3 < var2; ++var3) {
         ChatColor color = var1[var3];
         if (!color.equals(ChatColor.ITALIC) && !color.equals(ChatColor.MAGIC) && !color.equals(ChatColor.STRIKETHROUGH) && !color.equals(ChatColor.UNDERLINE) && !color.equals(ChatColor.BOLD)) {
            String configName = color.name().toLowerCase().replace("_", "-").replace("ı", "i").replace("dark-purple", "purple").replace("light-purple", "pink").replace("reset", "none").replace(" ", "");
            String name = configName.replace("-", "");
            if (!dataEffects.containsKey(name)) {
               addEGlowEffect(name, EGlowMainConfig.MainConfig.SETTINGS_GUI_COLOR_FOR_MESSAGES.getBoolean() ? EGlowMessageConfig.Message.GUI_COLOR.get(configName) : EGlowMessageConfig.Message.COLOR.get(configName), "eglow.color." + name, color);
               if (!name.equals("none")) {
                  addEGlowEffect("blink" + name + "slow", EGlowMainConfig.MainConfig.SETTINGS_GUI_COLOR_FOR_MESSAGES.getBoolean() ? EGlowMessageConfig.Message.GUI_COLOR.get(configName) : EGlowMessageConfig.Message.COLOR.get(configName) + " §f(" + EGlowMessageConfig.Message.COLOR.get("effect-blink") + " " + EGlowMessageConfig.Message.COLOR.get("slow") + "§f)", "eglow.blink." + name, EGlowMainConfig.MainConfig.DELAY_SLOW.getInt(), ChatColor.RESET, color);
                  addEGlowEffect("blink" + name + "fast", EGlowMainConfig.MainConfig.SETTINGS_GUI_COLOR_FOR_MESSAGES.getBoolean() ? EGlowMessageConfig.Message.GUI_COLOR.get(configName) : EGlowMessageConfig.Message.COLOR.get(configName) + " §f(" + EGlowMessageConfig.Message.COLOR.get("effect-blink") + " " + EGlowMessageConfig.Message.COLOR.get("fast") + "§f)", "eglow.blink." + name, EGlowMainConfig.MainConfig.DELAY_FAST.getInt(), ChatColor.RESET, color);
               }
            } else {
               effect = getEGlowEffect(name);
               if (effect != null) {
                  effect.setDisplayName(EGlowMainConfig.MainConfig.SETTINGS_GUI_COLOR_FOR_MESSAGES.getBoolean() ? EGlowMessageConfig.Message.GUI_COLOR.get(configName) : EGlowMessageConfig.Message.COLOR.get(configName));
               }

               effect = getEGlowEffect("blink" + name + "slow");
               if (effect != null) {
                  effect.setDisplayName(EGlowMainConfig.MainConfig.SETTINGS_GUI_COLOR_FOR_MESSAGES.getBoolean() ? EGlowMessageConfig.Message.GUI_COLOR.get(configName) : EGlowMessageConfig.Message.COLOR.get(configName) + " §f(" + EGlowMessageConfig.Message.COLOR.get("effect-blink") + (EGlowMessageConfig.Message.COLOR.get("effect-blink").isEmpty() ? "" : " ") + EGlowMessageConfig.Message.COLOR.get("slow") + "§f)");
                  effect.setEffectDelay(EGlowMainConfig.MainConfig.DELAY_SLOW.getInt());
                  effect.reloadEffect();
               }

               effect = getEGlowEffect("blink" + name + "fast");
               if (effect != null) {
                  effect.setDisplayName(EGlowMainConfig.MainConfig.SETTINGS_GUI_COLOR_FOR_MESSAGES.getBoolean() ? EGlowMessageConfig.Message.GUI_COLOR.get(configName) : EGlowMessageConfig.Message.COLOR.get(configName) + " §f(" + EGlowMessageConfig.Message.COLOR.get("effect-blink") + (EGlowMessageConfig.Message.COLOR.get("effect-blink").isEmpty() ? "" : " ") + EGlowMessageConfig.Message.COLOR.get("fast") + "§f)");
                  effect.setEffectDelay(EGlowMainConfig.MainConfig.DELAY_FAST.getInt());
                  effect.reloadEffect();
               }
            }
         }
      }

      if (!dataEffects.containsKey("rainbowslow")) {
         addEGlowEffect("rainbowslow", EGlowMainConfig.MainConfig.SETTINGS_GUI_COLOR_FOR_MESSAGES.getBoolean() ? EGlowMessageConfig.Message.GUI_COLOR.get("effect-rainbow") : EGlowMessageConfig.Message.COLOR.get("effect-rainbow") + " §f(" + EGlowMessageConfig.Message.COLOR.get("slow") + "§f)", "eglow.effect.rainbow", EGlowMainConfig.MainConfig.DELAY_SLOW.getInt(), ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.AQUA, ChatColor.BLUE, ChatColor.LIGHT_PURPLE);
         addEGlowEffect("rainbowfast", EGlowMainConfig.MainConfig.SETTINGS_GUI_COLOR_FOR_MESSAGES.getBoolean() ? EGlowMessageConfig.Message.GUI_COLOR.get("effect-rainbow") : EGlowMessageConfig.Message.COLOR.get("effect-rainbow") + " §f(" + EGlowMessageConfig.Message.COLOR.get("fast") + "§f)", "eglow.effect.rainbow", EGlowMainConfig.MainConfig.DELAY_FAST.getInt(), ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.AQUA, ChatColor.BLUE, ChatColor.LIGHT_PURPLE);
      } else {
         effect = getEGlowEffect("rainbowslow");
         if (effect != null) {
            effect.setDisplayName(EGlowMainConfig.MainConfig.SETTINGS_GUI_COLOR_FOR_MESSAGES.getBoolean() ? EGlowMessageConfig.Message.GUI_COLOR.get("effect-rainbow") : EGlowMessageConfig.Message.COLOR.get("effect-rainbow") + " §f(" + EGlowMessageConfig.Message.COLOR.get("slow") + "§f)");
            effect.setEffectDelay(EGlowMainConfig.MainConfig.DELAY_SLOW.getInt());
            effect.reloadEffect();
         }

         effect = getEGlowEffect("rainbowfast");
         if (effect != null) {
            effect.setDisplayName(EGlowMainConfig.MainConfig.SETTINGS_GUI_COLOR_FOR_MESSAGES.getBoolean() ? EGlowMessageConfig.Message.GUI_COLOR.get("effect-rainbow") : EGlowMessageConfig.Message.COLOR.get("effect-rainbow") + " §f(" + EGlowMessageConfig.Message.COLOR.get("fast") + "§f)");
            effect.setEffectDelay(EGlowMainConfig.MainConfig.DELAY_FAST.getInt());
            effect.reloadEffect();
         }
      }

      addCustomEffects();
   }

   public static void addCustomEffects() {
      ArrayList<String> newEffects = new ArrayList();
      Iterator var1 = EGlowCustomEffectsConfig.Effect.GET_ALL_EFFECTS.get().iterator();

      while(true) {
         String effectName;
         while(var1.hasNext()) {
            effectName = (String)var1.next();
            if (dataEffects.containsKey(effectName.toLowerCase())) {
               ChatUtil.sendToConsole("&cWARNING! Not registering custom effect: &f" + effectName + " &cdue to it using a default effect name!", true);
            } else if (blockedEfectNames.contains(effectName.toLowerCase())) {
               ChatUtil.sendToConsole("&cWARNING! Not registering custom effect: &f" + effectName + " &cdue to it using a name that is already used for a command!", true);
            } else {
               String displayName = ChatUtil.translateColors(EGlowCustomEffectsConfig.Effect.GET_DISPLAYNAME.getString(effectName));
               int delay = (int)(EGlowCustomEffectsConfig.Effect.GET_DELAY.getDouble(effectName) * 20.0D);
               List<String> colors = EGlowCustomEffectsConfig.Effect.GET_COLORS.getList(effectName);
               String permission = "eglow.effect." + effectName.toLowerCase();
               if (!oldEffects.isEmpty() && oldEffects.contains(effectName.toLowerCase())) {
                  EGlowEffect effect = getEGlowEffect(effectName.toLowerCase());
                  if (effect != null) {
                     effect.setDisplayName(displayName);
                     effect.setEffectDelay(delay);
                     effect.setColors(colors);
                     effect.reloadEffect();
                  }

                  oldEffects.remove(effectName.toLowerCase());
               } else {
                  addEGlowEffect(effectName.toLowerCase(), displayName, "eglow.effect." + effectName.toLowerCase(), delay, colors);

                  try {
                     EGlow.getInstance().getServer().getPluginManager().addPermission(new Permission(permission, "Activate " + effectName + " effect.", PermissionDefault.FALSE));
                  } catch (IllegalArgumentException var8) {
                  }
               }

               newEffects.add(effectName.toLowerCase());
            }
         }

         if (!oldEffects.isEmpty()) {
            var1 = oldEffects.iterator();

            while(var1.hasNext()) {
               effectName = (String)var1.next();
               EGlowEffect Eeffect = getEGlowEffect(effectName.toLowerCase());
               if (Eeffect != null) {
                  dataCustomEffects.remove(effectName.toLowerCase());
                  Eeffect.removeEffect();
               }
            }
         }

         oldEffects = newEffects;
         return;
      }
   }

   private static void addEGlowEffect(String name, String displayName, String permissionNode, ChatColor color) {
      if (!dataEffects.containsKey(name.toLowerCase())) {
         dataEffects.put(name.toLowerCase(), new EGlowEffect(name, displayName, permissionNode, 50, new ChatColor[]{color}));
      }

   }

   public static void addEGlowEffect(String name, String displayName, String permissionNode, int delay, ChatColor... colors) {
      if (!dataEffects.containsKey(name.toLowerCase())) {
         dataEffects.put(name.toLowerCase(), new EGlowEffect(name, displayName, permissionNode, delay, colors));
      }

   }

   private static void addEGlowEffect(String name, String displayName, String permissionNode, int delay, List<String> colors) {
      if (!dataCustomEffects.containsKey(name.toLowerCase())) {
         dataCustomEffects.put(name.toLowerCase(), new EGlowEffect(name, displayName, permissionNode, delay, colors));
      }

   }

   public static List<EGlowEffect> getEGlowEffects() {
      List<EGlowEffect> effects = new ArrayList();
      dataEffects.forEach((key, value) -> {
         effects.add(value);
      });
      return effects;
   }

   public static List<EGlowEffect> getCustomEffects() {
      List<EGlowEffect> effects = new ArrayList();
      dataCustomEffects.forEach((key, value) -> {
         effects.add(value);
      });
      return effects;
   }

   public static boolean isValidEffect(String name, boolean containsSpeed) {
      return containsSpeed ? dataEffects.containsKey(name.toLowerCase()) || dataCustomEffects.containsKey(name.toLowerCase()) : dataEffects.containsKey(name.toLowerCase() + "slow") && dataEffects.containsKey(name.toLowerCase() + "fast");
   }

   public static boolean isCustomEffect(String name) {
      return dataCustomEffects.containsKey(name);
   }

   public static EGlowEffect getEGlowEffect(String name) {
      if (dataEffects.containsKey(name.toLowerCase())) {
         return (EGlowEffect)dataEffects.get(name.toLowerCase());
      } else {
         return dataCustomEffects.containsKey(name.toLowerCase()) ? (EGlowEffect)dataCustomEffects.get(name.toLowerCase()) : null;
      }
   }

   public void onPluginMessageReceived(String channel, Player player, byte[] msg) {
   }

   public static void TABProxyUpdateRequest(Player player, String glowColor) {
      if (!EGlowMainConfig.MainConfig.ADVANCED_FORCE_DISABLE_PROXY_MESSAGING.getBoolean()) {
         ByteArrayDataOutput out = ByteStreams.newDataOutput();
         out.writeUTF("TABProxyUpdateRequest");
         out.writeUTF(player.getUniqueId().toString());
         out.writeUTF(glowColor);
         Bukkit.getServer().sendPluginMessage(EGlow.getInstance(), "eglow:bungee", out.toByteArray());
      }
   }

   public static void sendAPIEvent(EGlowPlayer eGlowPlayer, boolean fake) {
      if (!fake) {
         NMSHook.scheduleTask(false, () -> {
            Bukkit.getPluginManager().callEvent(new GlowColorChangeEvent(eGlowPlayer.getPlayer(), eGlowPlayer.getUuid(), eGlowPlayer.getActiveColor(), eGlowPlayer.getGlowStatus()));
         });
      }
   }
}

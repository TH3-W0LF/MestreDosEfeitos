package me.eplugins.eglow.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.addon.citizens.EGlowCitizensTrait;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.util.packets.PacketUtil;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class EGlowEffect {
   private Object effectRunnable;
   private ConcurrentHashMap<Object, Integer> activeEntities = new ConcurrentHashMap();
   private List<ChatColor> effectColors = new ArrayList();
   private int effectDelay = 0;
   private String name;
   private String displayName;
   private String permissionNode;

   public EGlowEffect(String name, String displayName, String permissionNode, int delay, ChatColor... colors) {
      this.setName(name);
      this.setDisplayName(displayName);
      this.setPermissionNode(permissionNode);
      this.setEffectDelay(delay);
      Collections.addAll(this.effectColors, colors);
   }

   public EGlowEffect(String name, String displayName, String permissionNode, int delay, List<String> colors) {
      this.setName(name);
      this.setDisplayName(displayName);
      this.setPermissionNode(permissionNode);
      this.setEffectDelay(delay);
      Iterator var6 = colors.iterator();

      while(var6.hasNext()) {
         String color = (String)var6.next();
         color = color.toLowerCase().replace("dark", "dark_").replace("light", "_light").replace("purple", "dark_purple").replace("pink", "light_purple").replace("none", "reset");
         this.effectColors.add(this.getChatColorFromString(color));
      }

   }

   public void activateForEntity(Object entity) {
      this.getActiveEntities().put(entity, 0);
      this.activateEffect();
   }

   public void deactivateForEntity(Object entity) {
      this.getActiveEntities().remove(entity);
   }

   public void reloadEffect() {
      if (this.getEffectRunnable() != null) {
         if (this.getEffectRunnable() instanceof BukkitTask) {
            BukkitTask task = (BukkitTask)this.getEffectRunnable();
            task.cancel();
         } else {
            try {
               NMSHook.nms.cancelScheduledTask.invoke(this.getEffectRunnable());
            } catch (Exception var2) {
               ChatUtil.printException("Failed to cancel effect runnable!", var2);
            }
         }
      }

      this.setEffectRunnable((Object)null);
      this.activateEffect();
   }

   public void removeEffect() {
      Object entity;
      for(Iterator var1 = this.activeEntities.keySet().iterator(); var1.hasNext(); this.getActiveEntities().remove(entity)) {
         entity = var1.next();
         EGlowPlayer eglowEntity = null;
         if (entity instanceof Player) {
            eglowEntity = DataManager.getEGlowPlayer((Player)entity);
         }

         try {
            if (EGlow.getInstance().getCitizensAddon() != null && entity instanceof NPC) {
               eglowEntity = ((EGlowCitizensTrait)((NPC)entity).getOrAddTrait(EGlowCitizensTrait.class)).getEGlowNPC();
            }
         } catch (NoSuchMethodError var6) {
            ChatUtil.sendToConsole("&cYour Citizens version is outdated!", true);
         }

         if (eglowEntity != null) {
            eglowEntity.disableGlow(true);
            if (entity instanceof Player) {
               ChatUtil.sendMsg(eglowEntity.getPlayer(), EGlowMessageConfig.Message.DISABLE_GLOW.get(), true);
            }
         }
      }

      if (this.getEffectRunnable() != null) {
         if (this.getEffectRunnable() instanceof BukkitTask) {
            BukkitTask task = (BukkitTask)this.getEffectRunnable();
            task.cancel();
         } else {
            try {
               NMSHook.nms.cancelScheduledTask.invoke(this.getEffectRunnable());
            } catch (Exception var5) {
               ChatUtil.printException("Failed to cancel effect runnable!", var5);
            }
         }
      }

      this.setEffectRunnable((Object)null);
      EGlow.getInstance().getServer().getPluginManager().removePermission("eglow.effect." + this.getName());
   }

   private void activateEffect() {
      if (this.getEffectRunnable() == null) {
         this.setEffectRunnable(NMSHook.scheduleTimerTask(true, 0L, (long)this.getEffectDelay(), () -> {
            if (this.getActiveEntities() == null) {
               this.activeEntities = new ConcurrentHashMap();
            }

            if (this.getActiveEntities().isEmpty() && this.getEffectRunnable() != null) {
               if (this.getEffectRunnable() instanceof BukkitTask) {
                  BukkitTask task = (BukkitTask)this.getEffectRunnable();
                  task.cancel();
                  this.setEffectRunnable((Object)null);
               } else {
                  try {
                     NMSHook.nms.cancelScheduledTask.invoke(this.effectRunnable);
                     this.setEffectRunnable((Object)null);
                  } catch (Exception var2) {
                     ChatUtil.printException("Failed to cancel effect runnable!", var2);
                  }
               }
            }

            this.getActiveEntities().forEach((entity, progress) -> {
               EGlowPlayer eglowEntity = null;
               if (entity instanceof Player) {
                  eglowEntity = DataManager.getEGlowPlayer((Player)entity);
               }

               try {
                  if (EGlow.getInstance().getCitizensAddon() != null && entity instanceof NPC) {
                     eglowEntity = ((EGlowCitizensTrait)((NPC)entity).getTraitNullable(EGlowCitizensTrait.class)).getEGlowNPC();
                  }
               } catch (NoSuchMethodError var5) {
                  ChatUtil.sendToConsole("&cYour Citizens version is outdated!", true);
               } catch (NullPointerException var6) {
                  NMSHook.scheduleTask(false, () -> {
                     ((NPC)entity).getOrAddTrait(EGlowCitizensTrait.class);
                  });
                  return;
               }

               if (eglowEntity == null) {
                  this.getActiveEntities().remove(entity);
               } else {
                  ChatColor color = (ChatColor)this.getEffectColors().get(progress);
                  if (color.equals(ChatColor.RESET)) {
                     eglowEntity.setColor(color, false, true);
                  } else {
                     eglowEntity.setColor(color, true, false);
                  }

                  if (this.getEffectColors().size() == 1) {
                     eglowEntity.setColor(color, true, false);
                     if (entity instanceof Player) {
                        PacketUtil.updateGlowing(eglowEntity, true);
                     }

                  } else if (progress == this.getEffectColors().size() - 1) {
                     this.getActiveEntities().replace(entity, 0);
                  } else {
                     this.getActiveEntities().replace(entity, progress + 1);
                  }
               }
            });
         }));
      }

   }

   public void setColors(List<String> colors) {
      List<ChatColor> chatcolors = new ArrayList();
      Iterator var3 = colors.iterator();

      while(var3.hasNext()) {
         String color = (String)var3.next();
         color = color.toLowerCase().replace("dark", "dark_").replace("light", "_light").replace("purple", "dark_purple").replace("pink", "light_purple").replace("none", "reset");
         chatcolors.add(this.getChatColorFromString(color));
      }

      if (!chatcolors.equals(this.getEffectColors())) {
         var3 = this.activeEntities.keySet().iterator();

         while(var3.hasNext()) {
            Object entity = var3.next();
            this.activeEntities.replace(entity, 0);
         }

         this.effectColors = chatcolors;
      }

   }

   private ChatColor getChatColorFromString(String color) {
      String var2 = color.toUpperCase();
      byte var3 = -1;
      switch(var2.hashCode()) {
      case -1770018776:
         if (var2.equals("DARK_RED")) {
            var3 = 1;
         }
         break;
      case -1680910220:
         if (var2.equals("YELLOW")) {
            var3 = 3;
         }
         break;
      case -1357848411:
         if (var2.equals("DARK_PURPLE")) {
            var3 = 10;
         }
         break;
      case -190762790:
         if (var2.equals("DARK_GREEN")) {
            var3 = 5;
         }
         break;
      case 81009:
         if (var2.equals("RED")) {
            var3 = 0;
         }
         break;
      case 2016956:
         if (var2.equals("AQUA")) {
            var3 = 6;
         }
         break;
      case 2041946:
         if (var2.equals("BLUE")) {
            var3 = 8;
         }
         break;
      case 2193504:
         if (var2.equals("GOLD")) {
            var3 = 2;
         }
         break;
      case 2196067:
         if (var2.equals("GRAY")) {
            var3 = 13;
         }
         break;
      case 63281119:
         if (var2.equals("BLACK")) {
            var3 = 15;
         }
         break;
      case 68081379:
         if (var2.equals("GREEN")) {
            var3 = 4;
         }
         break;
      case 77866287:
         if (var2.equals("RESET")) {
            var3 = 16;
         }
         break;
      case 82564105:
         if (var2.equals("WHITE")) {
            var3 = 12;
         }
         break;
      case 963498469:
         if (var2.equals("DARK_AQUA")) {
            var3 = 7;
         }
         break;
      case 963523459:
         if (var2.equals("DARK_BLUE")) {
            var3 = 9;
         }
         break;
      case 963677580:
         if (var2.equals("DARK_GRAY")) {
            var3 = 14;
         }
         break;
      case 1983666981:
         if (var2.equals("LIGHT_PURPLE")) {
            var3 = 11;
         }
      }

      switch(var3) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
         return ChatColor.valueOf(color.toUpperCase());
      default:
         ChatUtil.sendToConsole("&cInvalid color &f'&e" + color + "&f' &cfor effect &f'&e" + this.getName() + "&f'", true);
         return ChatColor.RESET;
      }
   }

   @Generated
   public Object getEffectRunnable() {
      return this.effectRunnable;
   }

   @Generated
   public ConcurrentHashMap<Object, Integer> getActiveEntities() {
      return this.activeEntities;
   }

   @Generated
   public List<ChatColor> getEffectColors() {
      return this.effectColors;
   }

   @Generated
   public int getEffectDelay() {
      return this.effectDelay;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public String getDisplayName() {
      return this.displayName;
   }

   @Generated
   public String getPermissionNode() {
      return this.permissionNode;
   }

   @Generated
   public void setEffectRunnable(Object effectRunnable) {
      this.effectRunnable = effectRunnable;
   }

   @Generated
   public void setActiveEntities(ConcurrentHashMap<Object, Integer> activeEntities) {
      this.activeEntities = activeEntities;
   }

   @Generated
   public void setEffectColors(List<ChatColor> effectColors) {
      this.effectColors = effectColors;
   }

   @Generated
   public void setEffectDelay(int effectDelay) {
      this.effectDelay = effectDelay;
   }

   @Generated
   public void setName(String name) {
      this.name = name;
   }

   @Generated
   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   @Generated
   public void setPermissionNode(String permissionNode) {
      this.permissionNode = permissionNode;
   }
}

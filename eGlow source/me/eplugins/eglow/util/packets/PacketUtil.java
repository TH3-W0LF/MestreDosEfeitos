package me.eplugins.eglow.util.packets;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.packets.chat.EnumChatFormat;
import me.eplugins.eglow.util.packets.chat.IChatBaseComponent;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.packets.outgoing.PacketPlayOutActionBar;
import me.eplugins.eglow.util.packets.outgoing.PacketPlayOutChat;
import me.eplugins.eglow.util.packets.outgoing.PacketPlayOutEntityMetadata;
import me.eplugins.eglow.util.packets.outgoing.PacketPlayOutScoreboardTeam;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketUtil {
   private static boolean sendPackets = true;

   public static void handlePlayerJoin(EGlowPlayer eGlowPlayer) {
      PipelineInjector.inject(eGlowPlayer);
      scoreboardPacket(eGlowPlayer, true);
      updatePlayer(eGlowPlayer);
   }

   public static void handlePlayerQuit(EGlowPlayer eGlowPlayer) {
      scoreboardPacket(eGlowPlayer, false);
      PipelineInjector.uninject(eGlowPlayer);
   }

   private static void updatePlayer(EGlowPlayer eGlowPlayer) {
      NMSHook.scheduleDelayedTask(false, 2L, () -> {
         ProtocolVersion eGlowPlayerVersion = eGlowPlayer.getVersion();
         Iterator var2 = DataManager.getEGlowPlayers().iterator();

         while(true) {
            EGlowPlayer eGlowTarget;
            PacketPlayOutEntityMetadata packetPlayOutEntityMetadata;
            do {
               do {
                  do {
                     do {
                        do {
                           if (!var2.hasNext()) {
                              return;
                           }

                           eGlowTarget = (EGlowPlayer)var2.next();
                        } while(!(eGlowTarget.getEntity() instanceof Player));
                     } while(eGlowTarget.equals(eGlowPlayer));

                     if (sendPackets && EGlowMainConfig.MainConfig.ADVANCED_TEAMS_SEND_PACKETS.getBoolean() && (EGlow.getInstance().getTabAddon() == null || !EGlow.getInstance().getTabAddon().blockEGlowPackets())) {
                        try {
                           NMSHook.sendPacket(eGlowPlayer, (new PacketPlayOutScoreboardTeam(eGlowTarget.getTeamName(), (EGlow.getInstance().getVaultAddon() != null ? EGlow.getInstance().getVaultAddon().getPlayerTagPrefix(eGlowTarget) : "") + eGlowTarget.getActiveColor(), EGlow.getInstance().getVaultAddon() != null ? EGlow.getInstance().getVaultAddon().getPlayerTagSuffix(eGlowTarget) : "", EGlowMainConfig.MainConfig.ADVANCED_TEAMS_NAMETAG_VISIBILITY.getBoolean() ? "always" : "never", EGlowMainConfig.MainConfig.ADVANCED_TEAMS_ENTITY_COLLISION.getBoolean() ? "always" : "never", Sets.newHashSet(new String[]{eGlowTarget.getDisplayName()}), 21)).setColor(EnumChatFormat.valueOf(eGlowTarget.getActiveColor().name())).toNMS(eGlowPlayerVersion));
                        } catch (Exception var10) {
                           ChatUtil.printException("Failed to send scoreboard team updates", var10);
                        }
                     }
                  } while(eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT));

                  if (!eGlowTarget.getGlowStatus() && !eGlowTarget.isFakeGlowStatus()) {
                     return;
                  }

                  Object glowingEntity = eGlowTarget.getEntity();
                  int glowingEntityID = eGlowTarget.getPlayer().getEntityId();
                  packetPlayOutEntityMetadata = null;

                  try {
                     packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(glowingEntityID, NMSHook.setGlowFlag(glowingEntity, true));
                  } catch (Exception var9) {
                     ChatUtil.printException("Failed to send entity metadata packet updates", var9);
                  }
               } while(!eGlowTarget.getGlowTargetMode().equals(EnumUtil.GlowTargetMode.ALL) && (!eGlowTarget.getGlowTargetMode().equals(EnumUtil.GlowTargetMode.CUSTOM) || !eGlowTarget.getGlowTargets().contains(eGlowPlayer.getPlayer())));
            } while(!eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.ALL) && !eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.OTHER));

            try {
               NMSHook.sendPacket(eGlowPlayer, ((PacketPlayOutEntityMetadata)Objects.requireNonNull(packetPlayOutEntityMetadata)).toNMS(eGlowPlayer.getVersion()));
            } catch (Exception var8) {
               ChatUtil.printException("Failed to send entity metadata packet updates", var8);
            }
         }
      });
   }

   public static synchronized void scoreboardPacket(EGlowPlayer eGlowPlayer, boolean join) {
      try {
         if (sendPackets && EGlowMainConfig.MainConfig.ADVANCED_TEAMS_SEND_PACKETS.getBoolean()) {
            if (eGlowPlayer == null || EGlow.getInstance() == null) {
               return;
            }

            Iterator var2;
            EGlowPlayer eGlowTarget;
            if (join) {
               if (EGlow.getInstance().getTabAddon() == null || !EGlow.getInstance().getTabAddon().blockEGlowPackets()) {
                  if (EGlowMainConfig.MainConfig.ADVANCED_TEAMS_REMOVE_ON_JOIN.getBoolean()) {
                     var2 = DataManager.getEGlowPlayers().iterator();

                     while(var2.hasNext()) {
                        eGlowTarget = (EGlowPlayer)var2.next();
                        if (eGlowTarget.getVersion().getMinorVersion() >= 8) {
                           NMSHook.sendPacket(eGlowTarget, (new PacketPlayOutScoreboardTeam(eGlowPlayer.getTeamName())).toNMS(eGlowPlayer.getVersion()));
                        }
                     }
                  }

                  var2 = DataManager.getEGlowPlayers().iterator();

                  while(var2.hasNext()) {
                     eGlowTarget = (EGlowPlayer)var2.next();
                     if (eGlowTarget.getVersion().getMinorVersion() >= 8) {
                        NMSHook.sendPacket(eGlowTarget, (new PacketPlayOutScoreboardTeam(eGlowPlayer.getTeamName(), EGlow.getInstance().getVaultAddon() != null ? EGlow.getInstance().getVaultAddon().getPlayerTagPrefix(eGlowPlayer) + eGlowPlayer.getActiveColor() : String.valueOf(eGlowPlayer.getActiveColor()), EGlow.getInstance().getVaultAddon() != null ? EGlow.getInstance().getVaultAddon().getPlayerTagSuffix(eGlowPlayer) : "", EGlowMainConfig.MainConfig.ADVANCED_TEAMS_NAMETAG_VISIBILITY.getBoolean() ? "always" : "never", EGlowMainConfig.MainConfig.ADVANCED_TEAMS_ENTITY_COLLISION.getBoolean() ? "always" : "never", Sets.newHashSet(new String[]{eGlowPlayer.getDisplayName()}), 21)).setColor(EnumChatFormat.RESET).toNMS(eGlowPlayer.getVersion()));
                     }
                  }
               }
            } else if (EGlow.getInstance().getTabAddon() == null || !EGlow.getInstance().getTabAddon().blockEGlowPackets()) {
               var2 = DataManager.getEGlowPlayers().iterator();

               while(var2.hasNext()) {
                  eGlowTarget = (EGlowPlayer)var2.next();
                  if (eGlowTarget.getVersion().getMinorVersion() >= 8) {
                     NMSHook.sendPacket(eGlowTarget, (new PacketPlayOutScoreboardTeam(eGlowPlayer.getTeamName())).toNMS(eGlowPlayer.getVersion()));
                  }
               }
            }
         }
      } catch (Exception var4) {
         ChatUtil.printException("Failed to send scoreboard team packet", var4);
      }

   }

   public static void updateScoreboardTeam(EGlowPlayer eGlowPlayer, String teamName, String prefix, String suffix, EnumChatFormat color) {
      PacketPlayOutScoreboardTeam packet = (new PacketPlayOutScoreboardTeam(teamName, prefix, suffix, EGlowMainConfig.MainConfig.ADVANCED_TEAMS_NAMETAG_VISIBILITY.getBoolean() ? "always" : "never", EGlowMainConfig.MainConfig.ADVANCED_TEAMS_ENTITY_COLLISION.getBoolean() ? "always" : "never", 21)).setColor(color);
      if (sendPackets && EGlowMainConfig.MainConfig.ADVANCED_TEAMS_SEND_PACKETS.getBoolean()) {
         if (eGlowPlayer == null && EGlow.getInstance() == null) {
            return;
         }

         if (EGlow.getInstance().getTabAddon() != null && EGlow.getInstance().getTabAddon().blockEGlowPackets()) {
            return;
         }

         Iterator var6 = DataManager.getEGlowPlayers().iterator();

         while(var6.hasNext()) {
            EGlowPlayer eGlowTarget = (EGlowPlayer)var6.next();

            try {
               NMSHook.sendPacket(eGlowTarget.getPlayer(), packet.toNMS(eGlowTarget.getVersion()));
            } catch (Exception var9) {
               ChatUtil.printException("Failed to send scoreboard team update", var9);
            }
         }
      }

   }

   public static void updateGlowing(EGlowPlayer eGlowPlayer, boolean status) {
      if (eGlowPlayer != null && EGlow.getInstance() != null) {
         Object glowingEntity = eGlowPlayer.getEntity();
         int glowingEntityID = eGlowPlayer.getPlayer().getEntityId();
         PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = null;

         try {
            packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(glowingEntityID, NMSHook.setGlowFlag(glowingEntity, status));
         } catch (Exception var14) {
            ChatUtil.printException("Failed to send entity metadata packet", var14);
         }

         if (status) {
            if (!PipelineInjector.glowingEntities.containsKey(glowingEntityID)) {
               PipelineInjector.glowingEntities.put(glowingEntityID, eGlowPlayer);
            }

            List<Player> players = eGlowPlayer.getGlowTargetMode().equals(EnumUtil.GlowTargetMode.ALL) ? new ArrayList(Bukkit.getOnlinePlayers()) : eGlowPlayer.getGlowTargets();
            Iterator var6 = ((List)players).iterator();

            while(var6.hasNext()) {
               Player player = (Player)var6.next();
               EGlowPlayer eGlowTarget = DataManager.getEGlowPlayer(player);
               if (eGlowTarget != null) {
                  switch(eGlowPlayer.getGlowVisibility()) {
                  case ALL:
                     try {
                        NMSHook.sendPacket(player, ((PacketPlayOutEntityMetadata)Objects.requireNonNull(packetPlayOutEntityMetadata)).toNMS(eGlowTarget.getVersion()));
                     } catch (Exception var13) {
                        ChatUtil.printException("Failed to send entity metadata packet", var13);
                     }
                     break;
                  case OTHER:
                     if (!eGlowPlayer.getPlayer().equals(player)) {
                        try {
                           NMSHook.sendPacket(player, ((PacketPlayOutEntityMetadata)Objects.requireNonNull(packetPlayOutEntityMetadata)).toNMS(eGlowTarget.getVersion()));
                        } catch (Exception var12) {
                           ChatUtil.printException("Failed to send entity metadata packet", var12);
                        }
                     }
                     break;
                  case OWN:
                     if (eGlowPlayer.getPlayer().equals(player)) {
                        try {
                           NMSHook.sendPacket(player, ((PacketPlayOutEntityMetadata)Objects.requireNonNull(packetPlayOutEntityMetadata)).toNMS(eGlowTarget.getVersion()));
                        } catch (Exception var11) {
                           ChatUtil.printException("Failed to send entity metadata packet", var11);
                        }
                     }
                  }
               }
            }
         } else {
            if (PipelineInjector.glowingEntities.containsKey(glowingEntityID)) {
               PipelineInjector.glowingEntities.remove(glowingEntityID, eGlowPlayer);
            }

            Iterator var15 = Bukkit.getOnlinePlayers().iterator();

            while(var15.hasNext()) {
               Player player = (Player)var15.next();
               EGlowPlayer eGlowTarget = DataManager.getEGlowPlayer(player);
               if (eGlowTarget != null) {
                  try {
                     NMSHook.sendPacket(player, ((PacketPlayOutEntityMetadata)Objects.requireNonNull(packetPlayOutEntityMetadata)).toNMS(eGlowTarget.getVersion()));
                  } catch (Exception var10) {
                     ChatUtil.printException("Failed to send entity metadata packet", var10);
                  }
               }
            }
         }

      }
   }

   public static void glowTargetChange(EGlowPlayer eGlowPlayer, Player change, boolean type) {
      EGlowPlayer eGlowTarget = DataManager.getEGlowPlayer(change);
      if (eGlowTarget != null) {
         Object glowingEntity = eGlowPlayer.getEntity();
         int glowingEntityID = eGlowPlayer.getPlayer().getEntityId();
         PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = null;
         if (type && !eGlowPlayer.isGlowing()) {
            type = false;
         }

         switch(eGlowTarget.getGlowVisibility()) {
         case ALL:
            packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(glowingEntityID, NMSHook.setGlowFlag(glowingEntity, type));
            break;
         case OTHER:
            packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(glowingEntityID, NMSHook.setGlowFlag(glowingEntity, !change.equals(eGlowPlayer.getPlayer()) && type));
            break;
         case OWN:
            packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(glowingEntityID, NMSHook.setGlowFlag(glowingEntity, change.equals(eGlowPlayer.getPlayer()) && type));
            break;
         case NONE:
            packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(glowingEntityID, NMSHook.setGlowFlag(glowingEntity, false));
            break;
         case UNSUPPORTEDCLIENT:
            return;
         }

         try {
            NMSHook.sendPacket(eGlowTarget, ((PacketPlayOutEntityMetadata)Objects.requireNonNull(packetPlayOutEntityMetadata)).toNMS(eGlowTarget.getVersion()));
         } catch (Exception var8) {
            ChatUtil.printException("Failed to send entity metadata packet", var8);
         }

      }
   }

   public static void updateGlowTarget(EGlowPlayer eGlowPlayer) {
      Collection<EGlowPlayer> players = DataManager.getEGlowPlayers();
      List<Player> customTargets = eGlowPlayer.getGlowTargets();
      Object glowingEntity = eGlowPlayer.getEntity();
      int glowingEntityID = eGlowPlayer.getPlayer().getEntityId();
      PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = null;

      try {
         packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(glowingEntityID, NMSHook.setGlowFlag(glowingEntity, eGlowPlayer.getGlowTargetMode().equals(EnumUtil.GlowTargetMode.ALL) && eGlowPlayer.getGlowStatus()));
      } catch (Exception var11) {
         ChatUtil.printException("Failed to send entity metadata packet", var11);
      }

      Iterator var6;
      EGlowPlayer eGlowTarget;
      switch(eGlowPlayer.getGlowTargetMode()) {
      case ALL:
         var6 = players.iterator();

         while(true) {
            do {
               if (!var6.hasNext()) {
                  return;
               }

               eGlowTarget = (EGlowPlayer)var6.next();
            } while(!eGlowTarget.getGlowVisibility().equals(EnumUtil.GlowVisibility.ALL) && (!eGlowTarget.getGlowVisibility().equals(EnumUtil.GlowVisibility.OTHER) || eGlowTarget.getPlayer().equals(eGlowPlayer.getPlayer())) && (!eGlowTarget.getGlowVisibility().equals(EnumUtil.GlowVisibility.OWN) || !eGlowTarget.getPlayer().equals(eGlowPlayer.getPlayer())));

            try {
               NMSHook.sendPacket(eGlowTarget, ((PacketPlayOutEntityMetadata)Objects.requireNonNull(packetPlayOutEntityMetadata)).toNMS(eGlowTarget.getVersion()));
            } catch (Exception var10) {
               ChatUtil.printException("Failed to send entity metadata packet", var10);
            }
         }
      case CUSTOM:
         var6 = players.iterator();

         while(true) {
            do {
               do {
                  if (!var6.hasNext()) {
                     return;
                  }

                  eGlowTarget = (EGlowPlayer)var6.next();
               } while(customTargets.contains(eGlowTarget.getPlayer()));
            } while(!eGlowTarget.getGlowVisibility().equals(EnumUtil.GlowVisibility.ALL) && (!eGlowTarget.getGlowVisibility().equals(EnumUtil.GlowVisibility.OTHER) || eGlowTarget.getPlayer().equals(eGlowPlayer.getPlayer())) && (!eGlowTarget.getPlayer().equals(eGlowPlayer.getPlayer()) || !eGlowTarget.getGlowVisibility().equals(EnumUtil.GlowVisibility.OWN)));

            try {
               NMSHook.sendPacket(eGlowTarget, ((PacketPlayOutEntityMetadata)Objects.requireNonNull(packetPlayOutEntityMetadata)).toNMS(eGlowTarget.getVersion()));
            } catch (Exception var9) {
               ChatUtil.printException("Failed to send entity metadata packet", var9);
            }
         }
      default:
      }
   }

   public static void forceUpdateGlow(EGlowPlayer eGlowPlayer) {
      Iterator var1 = ((Collection)(eGlowPlayer.getGlowTargetMode().equals(EnumUtil.GlowTargetMode.ALL) ? Bukkit.getOnlinePlayers() : eGlowPlayer.getGlowTargets())).iterator();

      while(true) {
         Player player;
         EGlowPlayer eGlowTarget;
         do {
            if (!var1.hasNext()) {
               return;
            }

            player = (Player)var1.next();
            eGlowTarget = DataManager.getEGlowPlayer(player);
         } while(eGlowTarget == null);

         boolean isGlowing = eGlowTarget.getGlowStatus() || eGlowTarget.isFakeGlowStatus();
         PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = null;
         switch(eGlowPlayer.getGlowVisibility()) {
         case ALL:
            packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(player.getEntityId(), NMSHook.setGlowFlag(player, isGlowing));
            break;
         case OTHER:
            packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(player.getEntityId(), NMSHook.setGlowFlag(player, !player.equals(eGlowPlayer.getPlayer()) && isGlowing));
            break;
         case OWN:
            packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(player.getEntityId(), NMSHook.setGlowFlag(player, player.equals(eGlowPlayer.getPlayer()) && isGlowing));
            break;
         case NONE:
            packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(player.getEntityId(), NMSHook.setGlowFlag(player, false));
            break;
         case UNSUPPORTEDCLIENT:
            return;
         }

         try {
            NMSHook.sendPacket(eGlowPlayer.getPlayer(), packetPlayOutEntityMetadata.toNMS(eGlowPlayer.getVersion()));
         } catch (Exception var7) {
            ChatUtil.printException("Failed to send entity metadata packet", var7);
         }
      }
   }

   public static void sendActionbar(EGlowPlayer eGlowPlayer, String text) {
      if (!text.isEmpty()) {
         IChatBaseComponent formattedText = IChatBaseComponent.optimizedComponent(text);
         if (ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 19 && !ProtocolVersion.SERVER_VERSION.getFriendlyName().equals("1.19")) {
            PacketPlayOutActionBar packetPlayOutActionBar = new PacketPlayOutActionBar(formattedText);

            try {
               NMSHook.sendPacket(eGlowPlayer.getPlayer(), packetPlayOutActionBar.toNMS(eGlowPlayer.getVersion()));
            } catch (Exception var6) {
               ChatUtil.printException("Failed to send entity metadata packet", var6);
            }
         } else {
            PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(formattedText, PacketPlayOutChat.ChatMessageType.GAME_INFO);

            try {
               NMSHook.sendPacket(eGlowPlayer.getPlayer(), packetPlayOutChat.toNMS(eGlowPlayer.getVersion()));
            } catch (Exception var5) {
               ChatUtil.printException("Failed to send entity metadata packet", var5);
            }
         }

      }
   }

   public static void setSendTeamPackets(boolean status) {
      sendPackets = status;
   }

   public static boolean getSendTeamPackets() {
      return sendPackets;
   }
}

package me.eplugins.eglow.util.packets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.DebugUtil;
import me.eplugins.eglow.util.enums.Dependency;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.packets.outgoing.PacketPlayOut;
import me.eplugins.eglow.util.packets.outgoing.PacketPlayOutEntityMetadata;
import me.eplugins.eglow.util.text.ChatUtil;

public class PipelineInjector {
   private static final String DECODER_NAME = "eGlowReader";
   public static final ConcurrentHashMap<Integer, EGlowPlayer> glowingEntities = new ConcurrentHashMap();
   private static boolean blockPackets = true;

   public static void inject(final EGlowPlayer eglowPlayer) {
      Channel channel = (Channel)NMSHook.getChannel(eglowPlayer.getPlayer());
      if (channel != null && channel.pipeline().names().contains("packet_handler")) {
         if (channel.pipeline().names().contains("eGlowReader")) {
            channel.pipeline().remove("eGlowReader");
         }

         try {
            channel.pipeline().addBefore("packet_handler", "eGlowReader", new ChannelDuplexHandler() {
               public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
                  super.channelRead(context, packet);
               }

               public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
                  if (NMSHook.nms.PacketPlayOutScoreboardTeam.isInstance(packet)) {
                     if (Dependency.TAB.isLoaded()) {
                        if (EGlow.getInstance().getTabAddon() == null) {
                           super.write(context, packet, channelPromise);
                           return;
                        }

                        if (!EGlow.getInstance().getTabAddon().isVersionSupported() || EGlow.getInstance().getTabAddon().blockEGlowPackets()) {
                           super.write(context, packet, channelPromise);
                           return;
                        }
                     } else if (Dependency.TAB_BRIDGE.isLoaded()) {
                        super.write(context, packet, channelPromise);
                        return;
                     }

                     PipelineInjector.modifyPlayers(packet);
                     super.write(context, packet, channelPromise);
                  } else {
                     Integer entityID;
                     if (NMSHook.nms.is1_20_5OrAbove && NMSHook.nms.SpawnEntityPacket.isInstance(packet)) {
                        if (NMSHook.nms.isPaper) {
                           entityID = (Integer)PacketPlayOut.getField(packet, "id");
                        } else {
                           entityID = (Integer)PacketPlayOut.getField(packet, "d");
                        }

                        if (PipelineInjector.glowingEntities.containsKey(entityID)) {
                           EGlowPlayer glowingTargetx = (EGlowPlayer)PipelineInjector.glowingEntities.get(entityID);
                           if (glowingTargetx == null) {
                              PipelineInjector.glowingEntities.remove(entityID);
                              super.write(context, packet, channelPromise);
                              return;
                           }

                           super.write(context, packet, channelPromise);
                           ChatUtil.sendToConsole("Sending glow update", false);
                           PacketUtil.updateGlowing(glowingTargetx, glowingTargetx.isGlowing());
                           return;
                        }
                     }

                     if (NMSHook.nms.PacketPlayOutEntityMetadata.isInstance(packet)) {
                        if (!NMSHook.nms.is1_20_5OrAbove) {
                           if (NMSHook.nms.is1_19_3OrAbove) {
                              entityID = (Integer)PacketPlayOut.getField(packet, "b");
                           } else {
                              entityID = (Integer)PacketPlayOut.getField(packet, "a");
                           }
                        } else if (!NMSHook.nms.isPaper || DebugUtil.getMainVersion() <= 21 && (DebugUtil.getMainVersion() != 21 || DebugUtil.getMinorVersion() < 6)) {
                           entityID = (Integer)PacketPlayOut.getField(packet, "c");
                        } else {
                           entityID = (Integer)PacketPlayOut.getField(packet, "id");
                        }

                        if (PipelineInjector.glowingEntities.containsKey(entityID)) {
                           EGlowPlayer glowingTarget = (EGlowPlayer)PipelineInjector.glowingEntities.get(entityID);
                           if (glowingTarget == null) {
                              PipelineInjector.glowingEntities.remove(entityID);
                              super.write(context, packet, channelPromise);
                              return;
                           }

                           EnumUtil.GlowVisibility glowVisibility = eglowPlayer.getGlowVisibility();
                           if (glowVisibility.equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT)) {
                              super.write(context, packet, channelPromise);
                              return;
                           }

                           PacketPlayOutEntityMetadata packetPlayOutEntityMetadata;
                           if (glowingTarget.getGlowTargetMode().equals(EnumUtil.GlowTargetMode.CUSTOM) && !glowingTarget.getGlowTargets().contains(eglowPlayer.getPlayer())) {
                              packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(entityID, NMSHook.setGlowFlagFromPacket(packet, false));
                              super.write(context, packetPlayOutEntityMetadata.toNMS(eglowPlayer.getVersion()), channelPromise);
                              return;
                           }

                           if (!glowVisibility.equals(EnumUtil.GlowVisibility.NONE) && (!glowVisibility.equals(EnumUtil.GlowVisibility.OTHER) || !glowingTarget.getPlayer().equals(eglowPlayer.getPlayer())) && (!glowVisibility.equals(EnumUtil.GlowVisibility.OWN) || glowingTarget.getPlayer().equals(eglowPlayer.getPlayer()))) {
                              packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(entityID, NMSHook.setGlowFlagFromPacket(packet, true));
                              super.write(context, packetPlayOutEntityMetadata.toNMS(eglowPlayer.getVersion()), channelPromise);
                              return;
                           }

                           packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(entityID, NMSHook.setGlowFlagFromPacket(packet, false));
                           super.write(context, packetPlayOutEntityMetadata.toNMS(eglowPlayer.getVersion()), channelPromise);
                           return;
                        }
                     }

                     super.write(context, packet, channelPromise);
                  }
               }
            });
         } catch (NoSuchElementException var3) {
         }

      }
   }

   public static void uninject(EGlowPlayer eGlowPlayer) {
      if (glowingEntities.containsValue(eGlowPlayer)) {
         glowingEntities.remove(eGlowPlayer.getPlayer().getEntityId());
      }

      try {
         Channel channel = (Channel)NMSHook.getChannel(eGlowPlayer.getPlayer());
         if (((Channel)Objects.requireNonNull(channel)).pipeline().names().contains("eGlowReader")) {
            channel.pipeline().remove("eGlowReader");
         }
      } catch (NoSuchElementException var2) {
      }

   }

   private static void modifyPlayers(Object packetPlayOutScoreboardTeam) throws Exception {
      if (blockPackets() && EGlowMainConfig.MainConfig.ADVANCED_PACKETS_SMART_BLOCKER.getBoolean()) {
         int action = NMSHook.nms.PacketPlayOutScoreboardTeam_ACTION.getInt(packetPlayOutScoreboardTeam);
         if (action != 1 && action != 2 && action != 4) {
            String teamName = NMSHook.nms.PacketPlayOutScoreboardTeam_NAME.get(packetPlayOutScoreboardTeam).toString();
            Collection<String> players = (Collection)NMSHook.nms.PacketPlayOutScoreboardTeam_PLAYERS.get(packetPlayOutScoreboardTeam);
            if (players != null) {
               ArrayList newList = new ArrayList();

               try {
                  List<String> list = new ArrayList(players);
                  Iterator var6 = list.iterator();

                  while(var6.hasNext()) {
                     String entity = (String)var6.next();
                     EGlowPlayer ePlayer = DataManager.getEGlowPlayer(entity);
                     if (ePlayer == null) {
                        newList.add(entity);
                     } else if (ePlayer.getTeamName().equals(teamName)) {
                        newList.add(entity);
                     }
                  }
               } catch (ConcurrentModificationException var9) {
                  ChatUtil.printException("Failed to modify glow color breaking team packet", var9);
               }

               NMSHook.nms.PacketPlayOutScoreboardTeam_PLAYERS.set(packetPlayOutScoreboardTeam, newList);
            }
         }
      }
   }

   public static boolean blockPackets() {
      return blockPackets;
   }

   @Generated
   public static void setBlockPackets(boolean blockPackets) {
      PipelineInjector.blockPackets = blockPackets;
   }
}

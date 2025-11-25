package me.eplugins.eglow.util.packets.outgoing;

import java.util.Collection;
import java.util.Collections;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.packets.chat.EnumChatFormat;
import me.eplugins.eglow.util.packets.chat.IChatBaseComponent;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.packets.nms.NMSStorage;

public class PacketPlayOutScoreboardTeam extends PacketPlayOut {
   public String name;
   public String playerPrefix;
   public String playerSuffix;
   public String nametagVisibility;
   public String collisionRule;
   public EnumChatFormat color;
   public Collection<String> players;
   public int method;
   public int options;

   private PacketPlayOutScoreboardTeam(int method, String name) {
      this.players = Collections.emptyList();
      if (name != null && !name.isEmpty()) {
         this.method = method;
         this.name = name;
      } else {
         throw new IllegalArgumentException("Team name cannot be null/empty");
      }
   }

   public PacketPlayOutScoreboardTeam(String team, String prefix, String suffix, String visibility, String collision, Collection<String> players, int options) {
      this(0, team);
      this.playerPrefix = prefix;
      this.playerSuffix = suffix;
      this.nametagVisibility = visibility;
      this.collisionRule = collision;
      this.players = players;
      this.options = options;
   }

   public PacketPlayOutScoreboardTeam(String team) {
      this(1, team);
   }

   public PacketPlayOutScoreboardTeam(String team, String prefix, String suffix, String visibility, String collision, int options) {
      this(2, team);
      this.playerPrefix = prefix;
      this.playerSuffix = suffix;
      this.nametagVisibility = visibility;
      this.collisionRule = collision;
      this.options = options;
   }

   public PacketPlayOutScoreboardTeam setColor(EnumChatFormat color) {
      this.color = color;
      return this;
   }

   public Object toNMS(ProtocolVersion clientVersion) throws Exception {
      NMSStorage nms = NMSHook.nms;
      String prefix = this.playerPrefix;
      String suffix = this.playerSuffix;
      if (clientVersion.getMinorVersion() < 13) {
         prefix = this.cutTo(prefix, 16);
         suffix = this.cutTo(suffix, 16);
      }

      Object team = nms.newScoreboardTeam.newInstance(nms.newScoreboard.newInstance(), this.name);
      ((Collection)nms.ScoreboardTeam_getPlayerNameSet.invoke(team)).addAll(this.players);
      if (nms.minorVersion >= 13) {
         if (prefix != null && !prefix.isEmpty()) {
            nms.ScoreboardTeam_setPrefix.invoke(team, NMSHook.stringToComponent(IChatBaseComponent.optimizedComponent(prefix).toString(clientVersion)));
         }

         if (suffix != null && !suffix.isEmpty()) {
            nms.ScoreboardTeam_setSuffix.invoke(team, NMSHook.stringToComponent(IChatBaseComponent.optimizedComponent(suffix).toString(clientVersion)));
         }

         EnumChatFormat format = this.color != null ? this.color : EnumChatFormat.lastColorsOf(prefix);
         nms.ScoreboardTeam_setColor.invoke(team, ((Object[])nms.EnumChatFormat.getMethod("values").invoke((Object)null))[format.ordinal()]);
      } else {
         if (prefix != null) {
            nms.ScoreboardTeam_setPrefix.invoke(team, prefix);
         }

         if (suffix != null) {
            nms.ScoreboardTeam_setSuffix.invoke(team, suffix);
         }
      }

      if (nms.EnumNameTagVisibility != null && this.nametagVisibility != null) {
         nms.ScoreboardTeam_setNameTagVisibility.invoke(team, this.nametagVisibility.equals("always") ? ((Object[])nms.EnumNameTagVisibility.getMethod("values").invoke((Object)null))[0] : ((Object[])nms.EnumNameTagVisibility.getMethod("values").invoke((Object)null))[1]);
      }

      if (nms.EnumTeamPush != null && this.collisionRule != null) {
         nms.ScoreboardTeam_setCollisionRule.invoke(team, this.collisionRule.equals("always") ? ((Object[])nms.EnumTeamPush.getMethod("values").invoke((Object)null))[0] : ((Object[])nms.EnumTeamPush.getMethod("values").invoke((Object)null))[1]);
      }

      if (nms.minorVersion >= 17) {
         switch(this.method) {
         case 0:
            return nms.PacketPlayOutScoreboardTeam_ofBoolean.invoke((Object)null, team, true);
         case 1:
            return nms.PacketPlayOutScoreboardTeam_of.invoke((Object)null, team);
         case 2:
            return nms.PacketPlayOutScoreboardTeam_ofBoolean.invoke((Object)null, team, false);
         case 3:
            return nms.PacketPlayOutScoreboardTeam_ofString.invoke((Object)null, team, ((String[])this.players.toArray(new String[0]))[0], ((Object[])nms.PacketPlayOutScoreboardTeam_a.getMethod("values").invoke((Object)null))[0]);
         case 4:
            return nms.PacketPlayOutScoreboardTeam_ofString.invoke((Object)null, team, ((String[])this.players.toArray(new String[0]))[0], ((Object[])nms.PacketPlayOutScoreboardTeam_a.getMethod("values").invoke((Object)null))[1]);
         default:
            throw new IllegalArgumentException("Invalid action: " + this.method);
         }
      } else {
         return nms.newPacketPlayOutScoreboardTeam.newInstance(team, this.method);
      }
   }

   public String toString() {
      return "PacketPlayOutScoreboardTeam{name=" + this.name + ",playerPrefix=" + this.playerPrefix + ",playerSuffix=" + this.playerSuffix + ",nametagVisibility=" + this.nametagVisibility + ",collisionRule=" + this.collisionRule + ",color=" + this.color + ",players=" + this.players + ",method=" + this.method + ",options=" + this.options + "}";
   }
}

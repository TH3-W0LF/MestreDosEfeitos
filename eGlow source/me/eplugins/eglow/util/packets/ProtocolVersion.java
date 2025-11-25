package me.eplugins.eglow.util.packets;

import lombok.Generated;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.DebugUtil;
import me.eplugins.eglow.util.enums.Dependency;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;

public enum ProtocolVersion {
   UNKNOWN(999, "Unknown"),
   v1_21_7(772, "1.21.8"),
   v1_21_6(771, "1.21.6"),
   v1_21_5(770, "1.21.5"),
   v1_21_4(769, "1.21.4"),
   v1_21_3(768, "1.21.3"),
   v1_21_2(768, "1.21.2"),
   v1_21_1(767, "1.21.1"),
   v1_21(767, "1.21"),
   v1_20_6(766, "1.20.6"),
   v1_20_5(766, "1.20.5"),
   v1_20_4(765, "1.20.4"),
   v1_20_3(765, "1.20.3"),
   v1_20_2(764, "1.20.2"),
   v1_20_1(763, "1.20.1"),
   v1_20(763, "1.20"),
   v1_19_4(762, "1.19.4"),
   v1_19_3(761, "1.19.3"),
   v1_19_2(760, "1.19.2"),
   v1_19_1(760, "1.19.1"),
   v1_19(759, "1.19"),
   v1_18_2(758, "1.18.2"),
   v1_18_1(757, "1.18.1"),
   v1_18(757, "1.18"),
   v1_17_1(756, "1.17.1"),
   v1_17(755, "1.17"),
   v1_16_5(754, "1.16.5"),
   v1_16_4(754, "1.16.4"),
   v1_16_3(753, "1.16.3"),
   v1_16_2(751, "1.16.2"),
   v1_16_1(736, "1.16.1"),
   v1_16(735, "1.16"),
   v1_15_2(578, "1.15.2"),
   v1_15_1(575, "1.15.1"),
   v1_15(573, "1.15"),
   v1_14_4(498, "1.14.4"),
   v1_14_3(490, "1.14.3"),
   v1_14_2(485, "1.14.2"),
   v1_14_1(480, "1.14.1"),
   v1_14(477, "1.14"),
   v1_13_2(404, "1.13.2"),
   v1_13_1(401, "1.13.1"),
   v1_13(393, "1.13"),
   v1_12_2(340, "1.12.2"),
   v1_12_1(338, "1.12.1"),
   v1_12(335, "1.12"),
   v1_11_2(316, "1.11.2"),
   v1_11_1(316, "1.11.1"),
   v1_11(315, "1.11"),
   v1_10_2(210, "1.10.2"),
   v1_10_1(210, "1.10.1"),
   v1_10(210, "1.10"),
   v1_9_4(110, "1.9.4");

   public static ProtocolVersion SERVER_VERSION;
   private final int networkId;
   private final String friendlyName;
   private int minorVersion;

   private ProtocolVersion(int networkId, String friendlyName) {
      this.networkId = networkId;
      this.friendlyName = friendlyName;
      if (this.toString().equals("UNKNOWN")) {
         try {
            this.minorVersion = DebugUtil.getMainVersion();
         } catch (Throwable var6) {
            this.minorVersion = 999;
         }
      } else {
         this.minorVersion = Integer.parseInt(this.toString().split("_")[1]);
      }

   }

   public static ProtocolVersion fromServerString(String s) {
      try {
         return valueOf("v" + s.replace(".", "_"));
      } catch (Throwable var2) {
         return UNKNOWN;
      }
   }

   public static ProtocolVersion fromNumber(int number) {
      ProtocolVersion[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ProtocolVersion v = var1[var3];
         if (number == v.getNetworkId()) {
            return v;
         }
      }

      return UNKNOWN;
   }

   public static ProtocolVersion getPlayerVersion(EGlowPlayer p) {
      if (Dependency.PROTOCOL_SUPPORT.isLoaded()) {
         int version = getProtocolVersionPS(p.getPlayer());
         if (version < SERVER_VERSION.getNetworkId()) {
            return fromNumber(version);
         }
      }

      return Dependency.VIA_VERSION.isLoaded() ? fromNumber(getProtocolVersionVia(p.getPlayer())) : SERVER_VERSION;
   }

   private static int getProtocolVersionPS(Player p) {
      try {
         Object protocolVersion = Class.forName("protocolsupport.api.ProtocolSupportAPI").getMethod("getProtocolVersion", Player.class).invoke((Object)null, p);
         return (Integer)protocolVersion.getClass().getMethod("getId").invoke(protocolVersion);
      } catch (Throwable var2) {
         return 0;
      }
   }

   private static int getProtocolVersionVia(Player p) {
      try {
         return Via.getAPI().getPlayerVersion(p.getUniqueId());
      } catch (Throwable var2) {
         return 0;
      }
   }

   @Generated
   public int getNetworkId() {
      return this.networkId;
   }

   @Generated
   public String getFriendlyName() {
      return this.friendlyName;
   }

   @Generated
   public int getMinorVersion() {
      return this.minorVersion;
   }

   // $FF: synthetic method
   private static ProtocolVersion[] $values() {
      return new ProtocolVersion[]{UNKNOWN, v1_21_7, v1_21_6, v1_21_5, v1_21_4, v1_21_3, v1_21_2, v1_21_1, v1_21, v1_20_6, v1_20_5, v1_20_4, v1_20_3, v1_20_2, v1_20_1, v1_20, v1_19_4, v1_19_3, v1_19_2, v1_19_1, v1_19, v1_18_2, v1_18_1, v1_18, v1_17_1, v1_17, v1_16_5, v1_16_4, v1_16_3, v1_16_2, v1_16_1, v1_16, v1_15_2, v1_15_1, v1_15, v1_14_4, v1_14_3, v1_14_2, v1_14_1, v1_14, v1_13_2, v1_13_1, v1_13, v1_12_2, v1_12_1, v1_12, v1_11_2, v1_11_1, v1_11, v1_10_2, v1_10_1, v1_10, v1_9_4};
   }
}

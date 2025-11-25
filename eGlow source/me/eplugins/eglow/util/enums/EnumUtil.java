package me.eplugins.eglow.util.enums;

public class EnumUtil {
   public static enum EffectSpeed {
      SLOW,
      FAST;

      // $FF: synthetic method
      private static EnumUtil.EffectSpeed[] $values() {
         return new EnumUtil.EffectSpeed[]{SLOW, FAST};
      }
   }

   public static enum EntityType {
      PLAYER,
      CITIZENNPC,
      MYTHICMOB,
      ENTITY;

      // $FF: synthetic method
      private static EnumUtil.EntityType[] $values() {
         return new EnumUtil.EntityType[]{PLAYER, CITIZENNPC, MYTHICMOB, ENTITY};
      }
   }

   public static enum ConfigType {
      SQLITE,
      MYSQL;

      // $FF: synthetic method
      private static EnumUtil.ConfigType[] $values() {
         return new EnumUtil.ConfigType[]{SQLITE, MYSQL};
      }
   }

   public static enum GlowTargetMode {
      ALL,
      CUSTOM;

      // $FF: synthetic method
      private static EnumUtil.GlowTargetMode[] $values() {
         return new EnumUtil.GlowTargetMode[]{ALL, CUSTOM};
      }
   }

   public static enum GlowWorldAction {
      BLOCKED,
      ALLOWED,
      UNKNOWN;

      // $FF: synthetic method
      private static EnumUtil.GlowWorldAction[] $values() {
         return new EnumUtil.GlowWorldAction[]{BLOCKED, ALLOWED, UNKNOWN};
      }
   }

   public static enum GlowDisableReason {
      BLOCKEDWORLD,
      INVISIBLE,
      ANIMATION,
      NONE;

      // $FF: synthetic method
      private static EnumUtil.GlowDisableReason[] $values() {
         return new EnumUtil.GlowDisableReason[]{BLOCKEDWORLD, INVISIBLE, ANIMATION, NONE};
      }
   }

   public static enum GlowVisibility {
      ALL,
      OTHER,
      OWN,
      NONE,
      UNSUPPORTEDCLIENT;

      // $FF: synthetic method
      private static EnumUtil.GlowVisibility[] $values() {
         return new EnumUtil.GlowVisibility[]{ALL, OTHER, OWN, NONE, UNSUPPORTEDCLIENT};
      }
   }
}

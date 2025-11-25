package xshyo.us.theglow.libs.config.settings.updater;

public enum MergeRule {
   SECTION_AT_MAPPING,
   MAPPING_AT_SECTION,
   MAPPINGS;

   public static MergeRule getFor(boolean var0, boolean var1) {
      return var0 ? (var1 ? null : SECTION_AT_MAPPING) : (var1 ? MAPPING_AT_SECTION : MAPPINGS);
   }
}

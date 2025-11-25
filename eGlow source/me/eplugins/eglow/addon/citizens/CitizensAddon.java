package me.eplugins.eglow.addon.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.trait.ScoreboardTrait;

public class CitizensAddon {
   public CitizensAddon() {
      try {
         CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(EGlowCitizensTrait.class).withName("eGlow"));
      } catch (IllegalArgumentException var2) {
      }

   }

   public boolean traitCheck(NPC npc) {
      try {
         if (!npc.hasTrait(ScoreboardTrait.class)) {
            npc.addTrait(ScoreboardTrait.class);
         }
      } catch (NoClassDefFoundError var3) {
         return false;
      }

      if (!npc.hasTrait(EGlowCitizensTrait.class)) {
         npc.addTrait(EGlowCitizensTrait.class);
      }

      return true;
   }
}

package xshyo.us.theglow.libs.guis.components;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum InteractionModifier {
   PREVENT_ITEM_PLACE,
   PREVENT_ITEM_TAKE,
   PREVENT_ITEM_SWAP,
   PREVENT_ITEM_DROP,
   PREVENT_OTHER_ACTIONS;

   public static final Set<InteractionModifier> VALUES = Collections.unmodifiableSet(EnumSet.allOf(InteractionModifier.class));

   // $FF: synthetic method
   private static InteractionModifier[] $values() {
      return new InteractionModifier[]{PREVENT_ITEM_PLACE, PREVENT_ITEM_TAKE, PREVENT_ITEM_SWAP, PREVENT_ITEM_DROP, PREVENT_OTHER_ACTIONS};
   }
}

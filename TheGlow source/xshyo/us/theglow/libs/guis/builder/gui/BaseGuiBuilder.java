package xshyo.us.theglow.libs.guis.builder.gui;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.guis.components.InteractionModifier;
import xshyo.us.theglow.libs.guis.guis.BaseGui;
import xshyo.us.theglow.libs.kyori.adventure.text.Component;

public abstract class BaseGuiBuilder<G extends BaseGui, B extends BaseGuiBuilder<G, B>> {
   private Component title = null;
   private int rows = 1;
   private final EnumSet<InteractionModifier> interactionModifiers = EnumSet.noneOf(InteractionModifier.class);
   private Consumer<G> consumer;

   @NotNull
   @Contract("_ -> this")
   public B rows(int var1) {
      this.rows = var1;
      return this;
   }

   @NotNull
   @Contract("_ -> this")
   public B title(@NotNull Component var1) {
      this.title = var1;
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public B disableItemPlace() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public B disableItemTake() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public B disableItemSwap() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public B disableItemDrop() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public B disableOtherActions() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public B disableAllInteractions() {
      this.interactionModifiers.addAll(InteractionModifier.VALUES);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public B enableItemPlace() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_PLACE);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public B enableItemTake() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_TAKE);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public B enableItemSwap() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_SWAP);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public B enableItemDrop() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_DROP);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public B enableOtherActions() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_OTHER_ACTIONS);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public B enableAllInteractions() {
      this.interactionModifiers.clear();
      return this;
   }

   @NotNull
   @Contract("_ -> this")
   public B apply(@NotNull Consumer<G> var1) {
      this.consumer = var1;
      return this;
   }

   @NotNull
   @Contract(" -> new")
   public abstract G create();

   @NotNull
   protected Component getTitle() {
      return this.title;
   }

   protected int getRows() {
      return this.rows;
   }

   @Nullable
   protected Consumer<G> getConsumer() {
      return this.consumer;
   }

   @NotNull
   protected Set<InteractionModifier> getModifiers() {
      return this.interactionModifiers;
   }
}

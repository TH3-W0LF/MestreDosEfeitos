package me.eplugins.eglow.custommenu.menu.handler;

import lombok.Generated;

public class Pair<L, R> {
   private final L left;
   private final R right;

   private Pair(L left, R right) {
      this.left = left;
      this.right = right;
   }

   public static <L, R> Pair<L, R> of(L left, R right) {
      return new Pair(left, right);
   }

   @Generated
   public L getLeft() {
      return this.left;
   }

   @Generated
   public R getRight() {
      return this.right;
   }
}

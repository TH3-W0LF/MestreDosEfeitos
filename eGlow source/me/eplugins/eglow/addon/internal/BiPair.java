package me.eplugins.eglow.addon.internal;

import java.util.Objects;
import lombok.Generated;

public class BiPair<K, V> {
   private final K key;
   private final V value;

   public BiPair(K key, V value) {
      this.key = key;
      this.value = value;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BiPair<?, ?> biPair = (BiPair)o;
         return Objects.equals(this.key, biPair.key) && Objects.equals(this.value, biPair.value) || Objects.equals(this.key, biPair.value) && Objects.equals(this.value, biPair.key);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.key, this.value});
   }

   public String toString() {
      return "BiPair{key=" + this.key + ", value=" + this.value + '}';
   }

   @Generated
   public K getKey() {
      return this.key;
   }

   @Generated
   public V getValue() {
      return this.value;
   }
}

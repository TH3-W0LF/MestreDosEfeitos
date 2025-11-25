package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common;

import java.util.ArrayList;

public class ArrayStack<T> {
   private final ArrayList<T> stack;

   public ArrayStack(int var1) {
      this.stack = new ArrayList(var1);
   }

   public void push(T var1) {
      this.stack.add(var1);
   }

   public T pop() {
      return this.stack.remove(this.stack.size() - 1);
   }

   public boolean isEmpty() {
      return this.stack.isEmpty();
   }
}

package xshyo.us.theglow.enums;

public enum DebugLevel {
   LOWEST(0),
   LOW(1),
   NORMAL(2),
   HIGH(3),
   CRITICAL(4);

   private final int priority;

   private DebugLevel(int var3) {
      this.priority = var3;
   }

   public int getPriority() {
      return this.priority;
   }

   // $FF: synthetic method
   private static DebugLevel[] $values() {
      return new DebugLevel[]{LOWEST, LOW, NORMAL, HIGH, CRITICAL};
   }
}

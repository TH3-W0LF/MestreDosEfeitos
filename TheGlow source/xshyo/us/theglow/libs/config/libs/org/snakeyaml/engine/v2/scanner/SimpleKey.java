package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.scanner;

import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

final class SimpleKey {
   private final int tokenNumber;
   private final boolean required;
   private final int index;
   private final int line;
   private final int column;
   private final Optional<Mark> mark;

   public SimpleKey(int var1, boolean var2, int var3, int var4, int var5, Optional<Mark> var6) {
      this.tokenNumber = var1;
      this.required = var2;
      this.index = var3;
      this.line = var4;
      this.column = var5;
      this.mark = var6;
   }

   public int getTokenNumber() {
      return this.tokenNumber;
   }

   public int getColumn() {
      return this.column;
   }

   public Optional<Mark> getMark() {
      return this.mark;
   }

   public int getIndex() {
      return this.index;
   }

   public int getLine() {
      return this.line;
   }

   public boolean isRequired() {
      return this.required;
   }

   public String toString() {
      return "SimpleKey - tokenNumber=" + this.tokenNumber + " required=" + this.required + " index=" + this.index + " line=" + this.line + " column=" + this.column;
   }
}

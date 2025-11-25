package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions;

public class ReaderException extends YamlEngineException {
   private final String name;
   private final int codePoint;
   private final int position;

   public ReaderException(String var1, int var2, int var3, String var4) {
      super(var4);
      this.name = var1;
      this.codePoint = var3;
      this.position = var2;
   }

   public String getName() {
      return this.name;
   }

   public int getCodePoint() {
      return this.codePoint;
   }

   public int getPosition() {
      return this.position;
   }

   public String toString() {
      String var1 = new String(Character.toChars(this.codePoint));
      return "unacceptable code point '" + var1 + "' (0x" + Integer.toHexString(this.codePoint).toUpperCase() + ") " + this.getMessage() + "\nin \"" + this.name + "\", position " + this.position;
   }
}

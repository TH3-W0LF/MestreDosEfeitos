package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api;

public interface StreamDataWriter {
   default void flush() {
   }

   void write(String var1);

   void write(String var1, int var2, int var3);
}

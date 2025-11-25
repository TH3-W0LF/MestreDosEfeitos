package xshyo.us.theglow.libs.config.dvs.segment;

public interface Segment {
   static Segment range(int var0, int var1, int var2, int var3) {
      return new RangeSegment(var0, var1, var2, var3);
   }

   static Segment range(int var0, int var1, int var2) {
      return new RangeSegment(var0, var1, var2, 0);
   }

   static Segment range(int var0, int var1) {
      return new RangeSegment(var0, var1, var0 < var1 ? 1 : -1, 0);
   }

   static Segment literal(String... var0) {
      return new LiteralSegment(var0);
   }

   int parse(String var1, int var2);

   String getElement(int var1);

   int getElementLength(int var1);

   int length();
}

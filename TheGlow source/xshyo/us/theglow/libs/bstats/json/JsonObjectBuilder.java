package xshyo.us.theglow.libs.bstats.json;

import java.util.Arrays;
import java.util.stream.Collectors;

public class JsonObjectBuilder {
   private StringBuilder builder = new StringBuilder();
   private boolean hasAtLeastOneField = false;

   public JsonObjectBuilder() {
      this.builder.append("{");
   }

   public JsonObjectBuilder appendNull(String var1) {
      this.appendFieldUnescaped(var1, "null");
      return this;
   }

   public JsonObjectBuilder appendField(String var1, String var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("JSON value must not be null");
      } else {
         this.appendFieldUnescaped(var1, "\"" + escape(var2) + "\"");
         return this;
      }
   }

   public JsonObjectBuilder appendField(String var1, int var2) {
      this.appendFieldUnescaped(var1, String.valueOf(var2));
      return this;
   }

   public JsonObjectBuilder appendField(String var1, JsonObjectBuilder.JsonObject var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("JSON object must not be null");
      } else {
         this.appendFieldUnescaped(var1, var2.toString());
         return this;
      }
   }

   public JsonObjectBuilder appendField(String var1, String[] var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("JSON values must not be null");
      } else {
         String var3 = (String)Arrays.stream(var2).map((var0) -> {
            return "\"" + escape(var0) + "\"";
         }).collect(Collectors.joining(","));
         this.appendFieldUnescaped(var1, "[" + var3 + "]");
         return this;
      }
   }

   public JsonObjectBuilder appendField(String var1, int[] var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("JSON values must not be null");
      } else {
         String var3 = (String)Arrays.stream(var2).mapToObj(String::valueOf).collect(Collectors.joining(","));
         this.appendFieldUnescaped(var1, "[" + var3 + "]");
         return this;
      }
   }

   public JsonObjectBuilder appendField(String var1, JsonObjectBuilder.JsonObject[] var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("JSON values must not be null");
      } else {
         String var3 = (String)Arrays.stream(var2).map(JsonObjectBuilder.JsonObject::toString).collect(Collectors.joining(","));
         this.appendFieldUnescaped(var1, "[" + var3 + "]");
         return this;
      }
   }

   private void appendFieldUnescaped(String var1, String var2) {
      if (this.builder == null) {
         throw new IllegalStateException("JSON has already been built");
      } else if (var1 == null) {
         throw new IllegalArgumentException("JSON key must not be null");
      } else {
         if (this.hasAtLeastOneField) {
            this.builder.append(",");
         }

         this.builder.append("\"").append(escape(var1)).append("\":").append(var2);
         this.hasAtLeastOneField = true;
      }
   }

   public JsonObjectBuilder.JsonObject build() {
      if (this.builder == null) {
         throw new IllegalStateException("JSON has already been built");
      } else {
         JsonObjectBuilder.JsonObject var1 = new JsonObjectBuilder.JsonObject(this.builder.append("}").toString());
         this.builder = null;
         return var1;
      }
   }

   private static String escape(String var0) {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         if (var3 == '"') {
            var1.append("\\\"");
         } else if (var3 == '\\') {
            var1.append("\\\\");
         } else if (var3 <= 15) {
            var1.append("\\u000").append(Integer.toHexString(var3));
         } else if (var3 <= 31) {
            var1.append("\\u00").append(Integer.toHexString(var3));
         } else {
            var1.append(var3);
         }
      }

      return var1.toString();
   }

   public static class JsonObject {
      private final String value;

      private JsonObject(String var1) {
         this.value = var1;
      }

      public String toString() {
         return this.value;
      }

      // $FF: synthetic method
      JsonObject(String var1, Object var2) {
         this(var1);
      }
   }
}

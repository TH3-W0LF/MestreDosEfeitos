package xshyo.us.theglow.libs.bstats.charts;

import java.util.function.BiConsumer;
import xshyo.us.theglow.libs.bstats.json.JsonObjectBuilder;

public abstract class CustomChart {
   private final String chartId;

   protected CustomChart(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("chartId must not be null");
      } else {
         this.chartId = var1;
      }
   }

   public JsonObjectBuilder.JsonObject getRequestJsonObject(BiConsumer<String, Throwable> var1, boolean var2) {
      JsonObjectBuilder var3 = new JsonObjectBuilder();
      var3.appendField("chartId", this.chartId);

      try {
         JsonObjectBuilder.JsonObject var4 = this.getChartData();
         if (var4 == null) {
            return null;
         }

         var3.appendField("data", var4);
      } catch (Throwable var5) {
         if (var2) {
            var1.accept("Failed to get data for custom chart with id " + this.chartId, var5);
         }

         return null;
      }

      return var3.build();
   }

   protected abstract JsonObjectBuilder.JsonObject getChartData() throws Exception;
}

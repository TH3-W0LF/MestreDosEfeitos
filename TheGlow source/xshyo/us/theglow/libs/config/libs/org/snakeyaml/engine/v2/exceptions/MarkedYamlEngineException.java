package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions;

import java.util.Objects;
import java.util.Optional;

public class MarkedYamlEngineException extends YamlEngineException {
   private final String context;
   private final Optional<Mark> contextMark;
   private final String problem;
   private final Optional<Mark> problemMark;

   protected MarkedYamlEngineException(String var1, Optional<Mark> var2, String var3, Optional<Mark> var4, Throwable var5) {
      super(var1 + "; " + var3 + "; " + var4, var5);
      Objects.requireNonNull(var2, "contextMark must be provided");
      Objects.requireNonNull(var4, "problemMark must be provided");
      this.context = var1;
      this.contextMark = var2;
      this.problem = var3;
      this.problemMark = var4;
   }

   protected MarkedYamlEngineException(String var1, Optional<Mark> var2, String var3, Optional<Mark> var4) {
      this(var1, var2, var3, var4, (Throwable)null);
   }

   public String getMessage() {
      return this.toString();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      if (this.context != null) {
         var1.append(this.context);
         var1.append("\n");
      }

      if (this.contextMark.isPresent() && (this.problem == null || !this.problemMark.isPresent() || ((Mark)this.contextMark.get()).getName().equals(((Mark)this.problemMark.get()).getName()) || ((Mark)this.contextMark.get()).getLine() != ((Mark)this.problemMark.get()).getLine() || ((Mark)this.contextMark.get()).getColumn() != ((Mark)this.problemMark.get()).getColumn())) {
         var1.append(this.contextMark.get());
         var1.append("\n");
      }

      if (this.problem != null) {
         var1.append(this.problem);
         var1.append("\n");
      }

      if (this.problemMark.isPresent()) {
         var1.append(this.problemMark.get());
         var1.append("\n");
      }

      return var1.toString();
   }

   public String getContext() {
      return this.context;
   }

   public Optional<Mark> getContextMark() {
      return this.contextMark;
   }

   public String getProblem() {
      return this.problem;
   }

   public Optional<Mark> getProblemMark() {
      return this.problemMark;
   }
}

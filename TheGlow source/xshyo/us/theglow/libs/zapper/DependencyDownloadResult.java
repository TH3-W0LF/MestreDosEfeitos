package xshyo.us.theglow.libs.zapper;

import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface DependencyDownloadResult {
   boolean wasSuccessful();

   @Contract("-> this")
   @NotNull
   default DependencyDownloadResult.Failure asFailure() {
      if (this instanceof DependencyDownloadResult.Failure) {
         return (DependencyDownloadResult.Failure)this;
      } else {
         throw new IllegalArgumentException("Dependency was downloaded successfully.");
      }
   }

   @Contract(
      pure = true
   )
   @NotNull
   static DependencyDownloadResult.Success success() {
      return DependencyDownloadResult.Success.INSTANCE;
   }

   @NotNull
   static DependencyDownloadResult.Failure failure(@NotNull Throwable var0) {
      Objects.requireNonNull(var0, "throwable cannot be null!");
      return new DependencyDownloadResult.Failure(var0);
   }

   public static final class Failure implements DependencyDownloadResult {
      private final Throwable throwable;

      Failure(Throwable var1) {
         this.throwable = var1;
      }

      public boolean wasSuccessful() {
         return false;
      }

      @NotNull
      public Throwable getError() {
         return this.throwable;
      }
   }

   public static final class Success implements DependencyDownloadResult {
      private static final DependencyDownloadResult.Success INSTANCE = new DependencyDownloadResult.Success();

      private Success() {
      }

      public boolean wasSuccessful() {
         return true;
      }
   }
}

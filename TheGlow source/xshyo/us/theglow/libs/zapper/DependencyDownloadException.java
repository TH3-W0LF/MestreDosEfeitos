package xshyo.us.theglow.libs.zapper;

import org.jetbrains.annotations.NotNull;

public final class DependencyDownloadException extends RuntimeException {
   public DependencyDownloadException(@NotNull Dependency var1, @NotNull Throwable var2) {
      super("Error downloading dependency " + var1.getGroupId() + '.' + var1.getArtifactId() + " v" + var1.getVersion(), var2);
   }

   public DependencyDownloadException(@NotNull Dependency var1, @NotNull String var2) {
      super("Error downloading dependency " + var1.getGroupId() + '.' + var1.getArtifactId() + " v" + var1.getVersion() + ": " + var2);
   }
}

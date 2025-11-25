package xshyo.us.theglow.libs.zapper.repository;

import java.net.URL;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.zapper.Dependency;

final class MavenRepository implements Repository {
   private static final MavenRepository MAVEN_CENTRAL = new MavenRepository("https://repo1.maven.org/maven2/");
   private static final MavenRepository JITPACK = new MavenRepository("https://jitpack.io/");
   private static final MavenRepository MINECRAFT = new MavenRepository("https://libraries.minecraft.net/");
   private static final MavenRepository PAPER = new MavenRepository("https://papermc.io/repo/repository/maven-public/");
   private final String repoURL;

   @NotNull
   public static MavenRepository mavenCentral() {
      return MAVEN_CENTRAL;
   }

   @NotNull
   public static MavenRepository jitpack() {
      return JITPACK;
   }

   @NotNull
   public static MavenRepository minecraft() {
      return MINECRAFT;
   }

   @NotNull
   public static MavenRepository paper() {
      return PAPER;
   }

   @NotNull
   public static MavenRepository maven(@NotNull String var0) {
      return new MavenRepository(var0);
   }

   private MavenRepository(@NotNull String var1) {
      if (var1.charAt(var1.length() - 1) != '/') {
         var1 = var1 + '/';
      }

      this.repoURL = var1;
   }

   public String getRepositoryURL() {
      return this.repoURL;
   }

   public String toString() {
      return this.getRepositoryURL();
   }

   @NotNull
   public URL resolve(@NotNull Dependency var1) throws Exception {
      return new URL(this.repoURL + var1.getMavenPath() + ".jar");
   }

   @NotNull
   public URL resolvePom(@NotNull Dependency var1) throws Exception {
      return new URL(this.repoURL + var1.getMavenPath() + ".pom");
   }

   public boolean equals(Object var1) {
      if (var1 != null && this.getClass() == var1.getClass()) {
         MavenRepository var2 = (MavenRepository)var1;
         return Objects.equals(this.repoURL, var2.repoURL);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.repoURL);
   }
}

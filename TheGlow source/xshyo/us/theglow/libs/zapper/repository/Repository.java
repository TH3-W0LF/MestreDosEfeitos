package xshyo.us.theglow.libs.zapper.repository;

import java.io.File;
import java.net.URL;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.zapper.Dependency;

public interface Repository {
   @NotNull
   URL resolve(@NotNull Dependency var1) throws Exception;

   @NotNull
   URL resolvePom(@NotNull Dependency var1) throws Exception;

   @NotNull
   static Repository maven(@NotNull URL var0) {
      return MavenRepository.maven(var0.toString());
   }

   @NotNull
   static Repository maven(@NotNull String var0) {
      return MavenRepository.maven(var0);
   }

   @NotNull
   static Repository mavenLocal() {
      try {
         String var0 = System.getProperty("user.home");
         File var1 = new File(var0, ".m2" + File.separator + "repository");
         return maven(var1);
      } catch (Throwable var2) {
         throw var2;
      }
   }

   @NotNull
   static Repository maven(@NotNull File var0) {
      try {
         return maven(var0.toURI().toURL().toString());
      } catch (Throwable var2) {
         throw var2;
      }
   }

   @NotNull
   static Repository mavenCentral() {
      return MavenRepository.mavenCentral();
   }

   @NotNull
   static Repository jitpack() {
      return MavenRepository.jitpack();
   }

   @NotNull
   static Repository paper() {
      return MavenRepository.paper();
   }

   @NotNull
   static Repository minecraft() {
      return MavenRepository.minecraft();
   }
}

package xshyo.us.theglow.libs.zapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.zapper.relocation.Relocation;
import xshyo.us.theglow.libs.zapper.repository.Repository;
import xshyo.us.theglow.libs.zapper.util.ClassLoaderReader;

public final class RuntimeLibPluginConfiguration {
   @NotNull
   private final String libsFolder;
   @NotNull
   private final String relocationPrefix;
   @NotNull
   private final List<Dependency> dependencies;
   @NotNull
   private final List<Repository> repositories;
   @NotNull
   private final List<Relocation> relocations;

   RuntimeLibPluginConfiguration(@NotNull String var1, @NotNull String var2, @NotNull List<Dependency> var3, @NotNull List<Repository> var4, @NotNull List<Relocation> var5) {
      this.libsFolder = var1;
      this.relocationPrefix = var2;
      this.dependencies = var3;
      this.repositories = var4;
      this.relocations = var5;
   }

   @NotNull
   public static RuntimeLibPluginConfiguration parse() {
      try {
         Properties var0 = parseProperties();
         String var1 = var0.getProperty("libs-folder");
         String var2 = var0.getProperty("relocation-prefix");
         List var3 = parseRepositories();
         List var4 = parseDependencies();
         List var5 = parseRelocations();
         return new RuntimeLibPluginConfiguration(var1, var2, var4, var3, var5);
      } catch (IOException var6) {
         throw new IllegalArgumentException("Generated Zapper files are missing. Have you applied the Gradle plugin?");
      }
   }

   @NotNull
   private static List<Relocation> parseRelocations() throws IOException {
      InputStream var0 = ClassLoaderReader.getResource("zapper/relocations.txt");
      if (var0 == null) {
         return Collections.emptyList();
      } else {
         ArrayList var1 = new ArrayList();
         Iterator var2 = readAllLines(var0).iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            String[] var4 = var3.split(":");
            var1.add(new Relocation(var4[0], var4[1]));
         }

         return var1;
      }
   }

   @NotNull
   private static List<Dependency> parseDependencies() {
      InputStream var0 = ClassLoaderReader.getResource("zapper/dependencies.txt");
      if (var0 == null) {
         return Collections.emptyList();
      } else {
         ArrayList var1 = new ArrayList();
         Iterator var2 = readAllLines(var0).iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            String[] var4 = var3.split(":");
            var1.add(new Dependency(var4[0], var4[1], var4[2], var4.length == 4 ? var4[3] : null));
         }

         return var1;
      }
   }

   @NotNull
   private static List<Repository> parseRepositories() {
      InputStream var0 = ClassLoaderReader.getResource("zapper/repositories.txt");
      if (var0 == null) {
         return Collections.emptyList();
      } else {
         ArrayList var1 = new ArrayList();
         Iterator var2 = readAllLines(var0).iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            var1.add(Repository.maven(var3));
         }

         return var1;
      }
   }

   @NotNull
   private static Properties parseProperties() {
      try {
         Properties var0 = new Properties();
         InputStream var1 = ClassLoaderReader.getResource("zapper/zapper.properties");
         Throwable var2 = null;

         try {
            var0.load(var1);
         } catch (Throwable var12) {
            var2 = var12;
            throw var12;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var11) {
                     var2.addSuppressed(var11);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return var0;
      } catch (Throwable var14) {
         throw var14;
      }
   }

   @NotNull
   private static List<String> readAllLines(@NotNull InputStream var0) {
      try {
         BufferedReader var1 = new BufferedReader(new InputStreamReader(var0));
         Throwable var2 = null;

         List var3;
         try {
            var3 = (List)var1.lines().collect(Collectors.toList());
         } catch (Throwable var13) {
            var2 = var13;
            throw var13;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var12) {
                     var2.addSuppressed(var12);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return var3;
      } catch (Throwable var15) {
         throw var15;
      }
   }

   @NotNull
   public String getLibsFolder() {
      return this.libsFolder;
   }

   @NotNull
   public String getRelocationPrefix() {
      return this.relocationPrefix;
   }

   @NotNull
   public List<Dependency> getDependencies() {
      return this.dependencies;
   }

   @NotNull
   public List<Repository> getRepositories() {
      return this.repositories;
   }

   @NotNull
   public List<Relocation> getRelocations() {
      return this.relocations;
   }

   public String toString() {
      return "RuntimeLibPluginConfiguration(libsFolder=" + this.getLibsFolder() + ", relocationPrefix=" + this.getRelocationPrefix() + ", dependencies=" + this.getDependencies() + ", repositories=" + this.getRepositories() + ", relocations=" + this.getRelocations() + ")";
   }
}

package xshyo.us.theglow.libs.zapper;

import java.io.File;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.zapper.classloader.URLClassLoaderWrapper;
import xshyo.us.theglow.libs.zapper.meta.MetaReader;
import xshyo.us.theglow.libs.zapper.relocation.Relocation;
import xshyo.us.theglow.libs.zapper.relocation.Relocator;
import xshyo.us.theglow.libs.zapper.repository.Repository;

public final class DependencyManager implements DependencyScope {
   public static boolean FAILED_TO_DOWNLOAD = false;
   private static final Pattern COLON = Pattern.compile(":");
   private final File directory;
   private final URLClassLoaderWrapper classLoader;
   private final List<Dependency> dependencies = new ArrayList();
   private final Set<Repository> repositories = new LinkedHashSet();
   private final List<Relocation> relocations = new ArrayList();
   private final MetaReader metaReader = MetaReader.create();

   public DependencyManager(@NotNull File var1, @NotNull URLClassLoaderWrapper var2) {
      this.directory = var1;
      this.classLoader = var2;
      this.repositories.add(Repository.mavenCentral());
   }

   public void load() {
      try {
         try {
            ArrayList var1 = new ArrayList();
            Iterator var2 = this.dependencies.iterator();

            while(true) {
               while(var2.hasNext()) {
                  Dependency var3 = (Dependency)var2.next();
                  File var4 = new File(this.directory, String.format("%s.%s-%s.jar", var3.getGroupId(), var3.getArtifactId(), var3.getVersion()));
                  File var5 = new File(this.directory, String.format("%s.%s-%s-relocated.jar", var3.getGroupId(), var3.getArtifactId(), var3.getVersion()));
                  if (this.hasRelocations() && var5.exists()) {
                     var1.add(var5.toPath());
                  } else {
                     if (!var4.exists()) {
                        boolean var6 = false;
                        ArrayList var7 = null;
                        Iterator var8 = this.repositories.iterator();

                        while(var8.hasNext()) {
                           Repository var9 = (Repository)var8.next();
                           DependencyDownloadResult var10 = var3.download(var4, var9);
                           if (var10.wasSuccessful()) {
                              var6 = true;
                              break;
                           }

                           (var7 == null ? (var7 = new ArrayList()) : var7).add(var9.toString());
                        }

                        if (var7 != null && !var6) {
                           throw new DependencyDownloadException(var3, "Could not find dependency in any of the following repositories: " + String.join("\n", var7));
                        }
                     }

                     if (this.hasRelocations() && !var5.exists()) {
                        Relocator.relocate(var4, var5, this.relocations);
                        var4.delete();
                     }

                     if (this.hasRelocations()) {
                        var1.add(var5.toPath());
                     } else {
                        var1.add(var4.toPath());
                     }
                  }
               }

               var2 = var1.iterator();

               while(var2.hasNext()) {
                  Path var13 = (Path)var2.next();
                  this.classLoader.addURL(var13.toUri().toURL());
               }
               break;
            }
         } catch (DependencyDownloadException var11) {
            if (!(var11.getCause() instanceof UnknownHostException)) {
               throw var11;
            }

            Bukkit.getLogger().info("[" + this.metaReader.pluginName() + "] It appears you do not have an internet connection. Please provide an internet connection for once at least.");
            FAILED_TO_DOWNLOAD = true;
         }

      } catch (Throwable var12) {
         throw var12;
      }
   }

   public void dependency(@NotNull Dependency var1) {
      this.dependencies.add(var1);
   }

   public void dependency(@NotNull String var1) {
      String[] var2 = COLON.split(var1);
      this.dependencies.add(new Dependency(var2[0], var2[1], var2[2], var2.length == 4 ? var2[3] : null));
   }

   public void dependency(@NotNull String var1, @NotNull String var2, @NotNull String var3) {
      this.dependencies.add(new Dependency(var1, var2, var3));
   }

   public void dependency(@NotNull String var1, @NotNull String var2, @NotNull String var3, @Nullable String var4) {
      this.dependencies.add(new Dependency(var1, var2, var3, var4));
   }

   public void relocate(@NotNull Relocation var1) {
      this.relocations.add(var1);
   }

   public void repository(@NotNull Repository var1) {
      this.repositories.add(var1);
   }

   public boolean hasRelocations() {
      return !this.relocations.isEmpty();
   }
}

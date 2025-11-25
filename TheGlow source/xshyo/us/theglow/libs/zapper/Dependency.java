package xshyo.us.theglow.libs.zapper;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.zapper.repository.Repository;

public final class Dependency {
   private static final String MAVEN_PATH = "%s/%s/%s/%s-%s%s";
   private final String groupId;
   private final String artifactId;
   private final String version;
   private final String mavenPath;

   public Dependency(@NotNull String var1, @NotNull String var2, @NotNull String var3) {
      this(var1, var2, var3, (String)null);
   }

   public Dependency(@NotNull String var1, @NotNull String var2, @NotNull String var3, @Nullable String var4) {
      this.groupId = var1;
      this.artifactId = var2;
      this.version = var3;
      this.mavenPath = String.format("%s/%s/%s/%s-%s%s", this.groupId.replace('.', '/'), this.artifactId, this.version, this.artifactId, this.version, var4 != null && !StringUtils.isBlank(var4) ? '-' + var4 : "");
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Dependency)) {
         return false;
      } else {
         Dependency var2 = (Dependency)var1;
         return Objects.equals(this.groupId, var2.groupId) && Objects.equals(this.artifactId, var2.artifactId) && Objects.equals(this.version, var2.version);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.groupId, this.artifactId, this.version});
   }

   @CheckReturnValue
   @NotNull
   public DependencyDownloadResult download(@NotNull File var1, @NotNull Repository var2) {
      try {
         if (!var1.exists()) {
            var1.getParentFile().mkdirs();
            var1.createNewFile();
         }

         URL var3 = var2.resolve(this);
         InputStream var4 = var3.openStream();
         Throwable var5 = null;

         try {
            OutputStream var6 = Files.newOutputStream(var1.toPath());
            Throwable var7 = null;

            try {
               byte[] var8 = new byte[8192];

               int var9;
               while((var9 = var4.read(var8)) != -1) {
                  var6.write(var8, 0, var9);
               }
            } catch (Throwable var33) {
               var7 = var33;
               throw var33;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var32) {
                        var7.addSuppressed(var32);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         } catch (Throwable var35) {
            var5 = var35;
            throw var35;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var31) {
                     var5.addSuppressed(var31);
                  }
               } else {
                  var4.close();
               }
            }

         }

         return DependencyDownloadResult.success();
      } catch (Throwable var37) {
         var1.delete();
         return DependencyDownloadResult.failure(var37);
      }
   }

   public String getGroupId() {
      return this.groupId;
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public String getVersion() {
      return this.version;
   }

   public String getMavenPath() {
      return this.mavenPath;
   }

   public String toString() {
      return "Dependency{groupId='" + this.groupId + '\'' + ", artifactId='" + this.artifactId + '\'' + ", version='" + this.version + '\'' + ", mavenPath='" + this.mavenPath + '\'' + '}';
   }
}

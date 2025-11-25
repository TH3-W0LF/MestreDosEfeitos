package xshyo.us.theglow.H;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.PlayerGlowData;
import xshyo.us.theglow.enums.DebugLevel;
import xshyo.us.theglow.libs.hikari.HikariConfig;
import xshyo.us.theglow.libs.hikari.HikariDataSource;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public class A extends xshyo.us.theglow.I.A {
   private final TheGlow D = TheGlow.getInstance();
   private HikariDataSource E;

   public void B() {
      try {
         HikariConfig var1 = new HikariConfig();
         File var2 = new File(this.D.getDataFolder(), this.D.getConfig().getString("config.data.database") + ".db");
         if (!var2.exists()) {
            var2.createNewFile();
         }

         var1.setJdbcUrl("jdbc:sqlite:" + var2.getPath());
         var1.addDataSourceProperty("cachePrepStmts", "true");
         var1.addDataSourceProperty("prepStmtCacheSize", "250");
         var1.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
         this.E = new HikariDataSource(var1);

         try {
            Connection var3 = this.D();

            try {
               Statement var4 = var3.createStatement();

               try {
                  var4.executeUpdate("CREATE TABLE IF NOT EXISTS player_glows (uuid VARCHAR(36) PRIMARY KEY,name VARCHAR(36),data TEXT)");
               } catch (Throwable var8) {
                  if (var4 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                     }
                  }

                  throw var8;
               }

               if (var4 != null) {
                  var4.close();
               }
            } catch (Exception var9) {
               var9.printStackTrace();
            }

            this.A(var3, (PreparedStatement)null, (ResultSet)null);
         } catch (Exception var10) {
            var10.printStackTrace();
            this.D.getLogger().warning("Error on connect to SQLite database.");
            return;
         }

         this.D.getLogger().info("Connected to SQLite database correctly.");
      } catch (Exception var11) {
         var11.printStackTrace();
         this.D.getLogger().warning("Error on connect to SQLite database.");
      }

   }

   public void A() {
      this.E.close();
   }

   public CompletableFuture<PlayerGlowData> A(String var1) {
      CompletableFuture var2 = new CompletableFuture();
      this.D.getExecutor().execute(() -> {
         try {
            Connection var3 = this.D();
            String var5 = this.D(var3, "name", var1);
            if (var5 != null) {
               PlayerGlowData var4 = (PlayerGlowData)Utils.getGson().fromJson(var5, PlayerGlowData.class);
               var2.complete(var4);
            } else {
               var2.complete((Object)null);
            }
         } catch (Exception var6) {
            var6.printStackTrace();
            var2.complete((Object)null);
         }

      });
      return var2;
   }

   public CompletableFuture<PlayerGlowData> A(UUID var1, String var2) {
      CompletableFuture var3 = new CompletableFuture();
      this.D.getExecutor().execute(() -> {
         try {
            var3.complete(this.C(var1, var2));
         } catch (Exception var5) {
            var5.printStackTrace();
            var3.complete((Object)null);
         }

      });
      return var3;
   }

   public CompletableFuture<Boolean> B(UUID var1, String var2) {
      CompletableFuture var3 = new CompletableFuture();
      this.D.getExecutor().execute(() -> {
         try {
            Connection var4 = this.D();
            String var7 = this.D(var4, "uuid", var1.toString());
            PlayerGlowData var5;
            boolean var6;
            if (var7 != null) {
               var6 = false;
               var5 = (PlayerGlowData)Utils.getGson().fromJson(var7, PlayerGlowData.class);
            } else {
               var6 = true;
               var5 = new PlayerGlowData(var1, var2);
               this.B(var4, var1.toString(), var2, Utils.getGson().toJson(var5, PlayerGlowData.class));
            }

            this.A.put(var1, var5);
            var3.complete(var6);
         } catch (Exception var8) {
            var8.printStackTrace();
            var3.complete((Object)null);
         }

      });
      return var3;
   }

   public PlayerGlowData C(UUID var1, String var2) {
      Connection var3 = this.D();
      String var5 = this.D(var3, "uuid", var1.toString());
      PlayerGlowData var4;
      if (var5 != null) {
         var4 = (PlayerGlowData)Utils.getGson().fromJson(var5, PlayerGlowData.class);
      } else {
         var4 = new PlayerGlowData(var1, var2);
         this.B(var3, var1.toString(), var2, Utils.getGson().toJson(var4, PlayerGlowData.class));
      }

      this.A.put(var1, var4);
      return var4;
   }

   public CompletableFuture<Void> C(UUID var1) {
      return CompletableFuture.runAsync(() -> {
         PlayerGlowData var2 = (PlayerGlowData)this.A.get(var1);
         if (var2 != null) {
            try {
               Connection var3 = this.D();
               this.C(var3, var1.toString(), Utils.getGson().toJson(var2, PlayerGlowData.class));
               this.A.put(var1, var2);
            } catch (Exception var4) {
               var4.printStackTrace();
            }

         }
      }, this.D.getExecutor());
   }

   public Connection D() {
      try {
         return this.E.getConnection();
      } catch (Throwable var2) {
         throw var2;
      }
   }

   private void B(Connection var1, String var2, String var3, String var4) {
      try {
         String var5 = "INSERT INTO player_glows (uuid, name, data) VALUES (?, ?, ?)";
         PreparedStatement var6 = var1.prepareStatement(var5);

         try {
            var6.setString(1, var2);
            var6.setString(2, var3);
            var6.setString(3, var4);
            var6.executeUpdate();
            this.A(var1, var6, (ResultSet)null);
         } catch (Throwable var10) {
            if (var6 != null) {
               try {
                  var6.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (var6 != null) {
            var6.close();
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

   }

   private String D(Connection var1, String var2, String var3) {
      try {
         String var4 = "SELECT data FROM player_glows WHERE " + var2 + " = ?";
         PreparedStatement var5 = var1.prepareStatement(var4);

         String var8;
         label79: {
            String var7;
            try {
               var5.setString(1, var3);
               ResultSet var6 = var5.executeQuery();

               label81: {
                  try {
                     if (!var6.next()) {
                        var7 = null;
                        break label81;
                     }

                     var7 = var6.getString("data");
                     this.A(var1, var5, var6);
                     var8 = var7;
                  } catch (Throwable var11) {
                     if (var6 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var10) {
                           var11.addSuppressed(var10);
                        }
                     }

                     throw var11;
                  }

                  if (var6 != null) {
                     var6.close();
                  }
                  break label79;
               }

               if (var6 != null) {
                  var6.close();
               }
            } catch (Throwable var12) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var9) {
                     var12.addSuppressed(var9);
                  }
               }

               throw var12;
            }

            if (var5 != null) {
               var5.close();
            }

            return var7;
         }

         if (var5 != null) {
            var5.close();
         }

         return var8;
      } catch (Exception var13) {
         var13.printStackTrace();
         return null;
      }
   }

   public CompletableFuture<Void> A(String var1, String var2) {
      return CompletableFuture.runAsync(() -> {
         try {
            String var3 = "UPDATE player_glows SET name = ? WHERE uuid = ?";
            Connection var4 = this.D();
            PreparedStatement var5 = var4.prepareStatement(var3);

            try {
               var5.setString(1, var2);
               var5.setString(2, var1);
               var5.executeUpdate();
               this.A(var4, var5, (ResultSet)null);
            } catch (Throwable var9) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (var5 != null) {
               var5.close();
            }
         } catch (Exception var10) {
            var10.printStackTrace();
         }

      }, this.D.getExecutor());
   }

   public CompletableFuture<String> B(String var1) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            String var2 = "SELECT name FROM player_glows WHERE uuid = ?";
            Connection var3 = this.D();
            PreparedStatement var4 = var3.prepareStatement(var2);

            label74: {
               String var7;
               try {
                  var4.setString(1, var1);
                  ResultSet var5 = var4.executeQuery();

                  label76: {
                     try {
                        if (var5.next()) {
                           String var6 = var5.getString("name");
                           this.A(var3, var4, (ResultSet)null);
                           var7 = var6;
                           break label76;
                        }
                     } catch (Throwable var10) {
                        if (var5 != null) {
                           try {
                              var5.close();
                           } catch (Throwable var9) {
                              var10.addSuppressed(var9);
                           }
                        }

                        throw var10;
                     }

                     if (var5 != null) {
                        var5.close();
                     }
                     break label74;
                  }

                  if (var5 != null) {
                     var5.close();
                  }
               } catch (Throwable var11) {
                  if (var4 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var8) {
                        var11.addSuppressed(var8);
                     }
                  }

                  throw var11;
               }

               if (var4 != null) {
                  var4.close();
               }

               return var7;
            }

            if (var4 != null) {
               var4.close();
            }
         } catch (Exception var12) {
            var12.printStackTrace();
         }

         return null;
      }, this.D.getExecutor());
   }

   private void C(Connection var1, String var2, String var3) {
      try {
         String var4 = "UPDATE player_glows SET data = ? WHERE uuid = ?";
         xshyo.us.theglow.E.B.A(this.D, "updateData " + var3, DebugLevel.NORMAL);
         PreparedStatement var5 = var1.prepareStatement(var4);

         try {
            var5.setString(1, var3);
            var5.setString(2, var2);
            var5.executeUpdate();
            this.A(var1, var5, (ResultSet)null);
         } catch (Throwable var9) {
            if (var5 != null) {
               try {
                  var5.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (var5 != null) {
            var5.close();
         }
      } catch (Exception var10) {
         var10.printStackTrace();
      }

   }

   public void A(@Nullable Connection var1, @Nullable PreparedStatement var2, @Nullable ResultSet var3) {
      try {
         if (var1 != null) {
            var1.close();
         }

         if (var2 != null) {
            var2.close();
         }

         if (var3 != null) {
            var3.close();
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }
}

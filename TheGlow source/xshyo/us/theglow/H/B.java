package xshyo.us.theglow.H;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.PlayerGlowData;
import xshyo.us.theglow.libs.hikari.HikariConfig;
import xshyo.us.theglow.libs.hikari.HikariDataSource;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public class B extends xshyo.us.theglow.I.A {
   private final TheGlow B = TheGlow.getInstance();
   private HikariDataSource C;

   public void B() {
      try {
         HikariConfig var1 = new HikariConfig();
         var1.setJdbcUrl("jdbc:mysql://" + this.B.getConf().getString("config.data.address") + ":" + this.B.getConf().getInt("config.data.port") + "/" + this.B.getConf().getString("config.data.database"));
         var1.setUsername(this.B.getConf().getString("config.data.username"));
         var1.setPassword(this.B.getConf().getString("config.data.password"));
         var1.addDataSourceProperty("cachePrepStmts", "true");
         var1.addDataSourceProperty("prepStmtCacheSize", "250");
         var1.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
         var1.addDataSourceProperty("autoReconnect", "true");
         var1.addDataSourceProperty("leakDetectionThreshold", "true");
         var1.addDataSourceProperty("verifyServerCertificate", "false");
         var1.addDataSourceProperty("useSSL", "false");
         var1.setConnectionTimeout(5000L);
         this.C = new HikariDataSource(var1);

         try {
            Connection var2 = this.C();

            try {
               Statement var3 = var2.createStatement();

               try {
                  var3.executeUpdate("CREATE TABLE IF NOT EXISTS player_glows (uuid VARCHAR(36) PRIMARY KEY,name VARCHAR(36),data TEXT)");
                  this.A((Connection)null, (Statement)var3, (ResultSet)null);
               } catch (Throwable var7) {
                  if (var3 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                     }
                  }

                  throw var7;
               }

               if (var3 != null) {
                  var3.close();
               }
            } catch (Exception var8) {
               var8.printStackTrace();
            }

            this.A((Connection)var2, (Statement)null, (ResultSet)null);
         } catch (Exception var9) {
            var9.printStackTrace();
            this.B.getLogger().warning("Error on connect to MySQL database.");
            return;
         }

         this.B.getLogger().info("Connected to MySQL database correctly.");
      } catch (Exception var10) {
         var10.printStackTrace();
         this.B.getLogger().warning("Error on connect to MySQL database.");
      }

   }

   public void A() {
      this.C.close();
   }

   public CompletableFuture<PlayerGlowData> A(String var1) {
      CompletableFuture var2 = new CompletableFuture();
      this.B.getExecutor().execute(() -> {
         try {
            Connection var3 = this.C();
            String var5 = this.B(var3, "name", var1);
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
      this.B.getExecutor().execute(() -> {
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
      this.B.getExecutor().execute(() -> {
         try {
            Connection var4 = this.C();
            String var7 = this.B(var4, "uuid", var1.toString());
            PlayerGlowData var5;
            boolean var6;
            if (var7 != null) {
               var6 = false;
               var5 = (PlayerGlowData)Utils.getGson().fromJson(var7, PlayerGlowData.class);
            } else {
               var6 = true;
               var5 = new PlayerGlowData(var1, var2);
               this.A(var4, var1.toString(), var2, Utils.getGson().toJson(this.A, PlayerGlowData.class));
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
      Connection var3 = this.C();
      String var5 = this.B(var3, "uuid", var1.toString());
      PlayerGlowData var4;
      if (var5 != null) {
         var4 = (PlayerGlowData)Utils.getGson().fromJson(var5, PlayerGlowData.class);
      } else {
         var4 = new PlayerGlowData(var1, var2);
         this.A(var3, var1.toString(), var2, Utils.getGson().toJson(var4, PlayerGlowData.class));
      }

      this.A.put(var1, var4);
      return var4;
   }

   public CompletableFuture<Void> C(UUID var1) {
      return CompletableFuture.runAsync(() -> {
         PlayerGlowData var2 = (PlayerGlowData)this.A.get(var1);
         if (var2 != null) {
            try {
               Connection var3 = this.C();
               this.A(var3, var1.toString(), Utils.getGson().toJson(var2, PlayerGlowData.class));
               this.A.put(var1, var2);
            } catch (Exception var4) {
               var4.printStackTrace();
            }

         }
      }, this.B.getExecutor());
   }

   public Connection C() {
      try {
         return this.C.getConnection();
      } catch (Throwable var2) {
         throw var2;
      }
   }

   private void A(Connection var1, String var2, String var3, String var4) {
      try {
         String var5 = "INSERT INTO player_glows (uuid, name, data) VALUES (?, ?, ?)";
         PreparedStatement var6 = var1.prepareStatement(var5);

         try {
            var6.setString(1, var2);
            var6.setString(2, var3);
            var6.setString(3, var4);
            var6.executeUpdate();
            this.A((Connection)var1, (Statement)var6, (ResultSet)null);
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

   private String B(Connection var1, String var2, String var3) {
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
                     this.A((Connection)var1, (Statement)var5, (ResultSet)var6);
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
            Connection var4 = this.C();
            PreparedStatement var5 = var4.prepareStatement(var3);

            try {
               var5.setString(1, var2);
               var5.setString(2, var1);
               var5.executeUpdate();
               this.A((Connection)var4, (Statement)var5, (ResultSet)null);
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

      }, this.B.getExecutor());
   }

   public CompletableFuture<String> B(String var1) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            String var2 = "SELECT name FROM player_glows WHERE uuid = ?";
            Connection var3 = this.C();
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
                           this.A((Connection)var3, (Statement)var4, (ResultSet)null);
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
      }, this.B.getExecutor());
   }

   private void A(Connection var1, String var2, String var3) {
      try {
         String var4 = "UPDATE player_glows SET data = ? WHERE uuid = ?";
         PreparedStatement var5 = var1.prepareStatement(var4);

         try {
            var5.setString(1, var3);
            var5.setString(2, var2);
            var5.executeUpdate();
            this.A((Connection)var1, (Statement)var5, (ResultSet)null);
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

   public void A(Connection var1, Statement var2, ResultSet var3) {
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

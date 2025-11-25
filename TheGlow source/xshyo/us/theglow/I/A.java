package xshyo.us.theglow.I;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import xshyo.us.theglow.data.PlayerGlowData;

public abstract class A {
   public HashMap<UUID, PlayerGlowData> A = new HashMap();

   public abstract void B();

   public abstract void A();

   public abstract CompletableFuture<PlayerGlowData> A(String var1);

   public abstract CompletableFuture<PlayerGlowData> A(UUID var1, String var2);

   public abstract CompletableFuture<Boolean> B(UUID var1, String var2);

   public abstract PlayerGlowData C(UUID var1, String var2);

   public abstract CompletableFuture<Void> A(String var1, String var2);

   public abstract CompletableFuture<String> B(String var1);

   public PlayerGlowData B(UUID var1) {
      return (PlayerGlowData)this.A.get(var1);
   }

   public abstract CompletableFuture<Void> C(UUID var1);

   public void A(UUID var1) {
      this.A.remove(var1);
   }
}

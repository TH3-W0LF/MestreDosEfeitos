package xshyo.us.theglow.libs.config.block.implementation;

import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.block.Block;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;

public class TerminatedBlock extends Block<Object> {
   public TerminatedBlock(@Nullable Node var1, @Nullable Node var2, @Nullable Object var3) {
      super(var1, var2, var3);
   }

   public TerminatedBlock(@Nullable Block<?> var1, @Nullable Object var2) {
      super(var1, var2);
   }

   public boolean isSection() {
      return false;
   }
}

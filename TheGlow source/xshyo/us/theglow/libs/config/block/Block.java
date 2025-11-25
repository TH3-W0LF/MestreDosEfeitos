package xshyo.us.theglow.libs.config.block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentLine;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentType;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.NodeTuple;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.SequenceNode;
import xshyo.us.theglow.libs.config.utils.format.NodeRole;

public abstract class Block<T> {
   @Nullable
   List<CommentLine> beforeKeyComments;
   @Nullable
   List<CommentLine> inlineKeyComments;
   @Nullable
   List<CommentLine> afterKeyComments;
   @Nullable
   List<CommentLine> beforeValueComments;
   @Nullable
   List<CommentLine> inlineValueComments;
   @Nullable
   List<CommentLine> afterValueComments;
   private T value;
   private boolean ignored;

   public Block(@Nullable Node var1, @Nullable Node var2, @Nullable T var3) {
      this.beforeKeyComments = null;
      this.inlineKeyComments = null;
      this.afterKeyComments = null;
      this.beforeValueComments = null;
      this.inlineValueComments = null;
      this.afterValueComments = null;
      this.value = var3;
      this.init(var1, var2);
   }

   public Block(@Nullable T var1) {
      this((Node)null, (Node)null, var1);
   }

   public Block(@Nullable Block<?> var1, @Nullable T var2) {
      this.beforeKeyComments = null;
      this.inlineKeyComments = null;
      this.afterKeyComments = null;
      this.beforeValueComments = null;
      this.inlineValueComments = null;
      this.afterValueComments = null;
      this.value = var2;
      if (var1 != null) {
         this.beforeKeyComments = var1.beforeKeyComments;
         this.inlineKeyComments = var1.inlineKeyComments;
         this.afterKeyComments = var1.afterKeyComments;
         this.beforeValueComments = var1.beforeValueComments;
         this.inlineValueComments = var1.inlineValueComments;
         this.afterValueComments = var1.afterValueComments;
      }
   }

   protected void init(@Nullable Node var1, @Nullable Node var2) {
      if (var1 != null) {
         this.beforeKeyComments = (List)(var1.getBlockComments() == null ? new ArrayList(0) : var1.getBlockComments());
         this.inlineKeyComments = var1.getInLineComments();
         this.afterKeyComments = var1.getEndComments();
         this.collectComments(var1, this.beforeKeyComments, true);
      }

      if (var2 != null) {
         this.beforeValueComments = (List)(var2.getBlockComments() == null ? new ArrayList(0) : var2.getBlockComments());
         this.inlineValueComments = var2.getInLineComments();
         this.afterValueComments = var2.getEndComments();
         this.collectComments(var2, this.beforeValueComments, true);
      }

   }

   private void collectComments(@NotNull Node var1, @NotNull List<CommentLine> var2, boolean var3) {
      if (!var3) {
         if (var1.getBlockComments() != null) {
            var2.addAll(this.toBlockComments(var1.getBlockComments()));
         }

         if (var1.getInLineComments() != null) {
            var2.addAll(this.toBlockComments(var1.getInLineComments()));
         }

         if (var1.getEndComments() != null) {
            var2.addAll(this.toBlockComments(var1.getEndComments()));
         }
      }

      Iterator var5;
      if (var1 instanceof SequenceNode) {
         SequenceNode var4 = (SequenceNode)var1;
         var5 = var4.getValue().iterator();

         while(var5.hasNext()) {
            Node var6 = (Node)var5.next();
            this.collectComments(var6, var2, false);
         }
      } else if (!var3 && var1 instanceof MappingNode) {
         MappingNode var7 = (MappingNode)var1;
         var5 = var7.getValue().iterator();

         while(var5.hasNext()) {
            NodeTuple var8 = (NodeTuple)var5.next();
            this.collectComments(var8.getKeyNode(), var2, false);
            this.collectComments(var8.getValueNode(), var2, false);
         }
      }

   }

   private List<CommentLine> toBlockComments(@NotNull List<CommentLine> var1) {
      int var2 = -1;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         CommentLine var4 = (CommentLine)var3.next();
         ++var2;
         var1.set(var2, var4.getCommentType() != CommentType.IN_LINE ? var4 : new CommentLine(var4.getStartMark(), var4.getEndMark(), var4.getValue(), CommentType.BLOCK));
      }

      return var1;
   }

   public void setValue(T var1) {
      this.value = var1;
   }

   @Nullable
   public List<String> getComments() {
      List var1 = Comments.get(this, NodeRole.KEY, Comments.Position.BEFORE);
      return var1 == null ? null : (List)var1.stream().map(CommentLine::getValue).collect(Collectors.toList());
   }

   public void setComments(@Nullable List<String> var1) {
      Comments.set(this, NodeRole.KEY, Comments.Position.BEFORE, var1 == null ? null : (List)var1.stream().map((var0) -> {
         return Comments.create(var0, Comments.Position.BEFORE);
      }).collect(Collectors.toList()));
   }

   public void removeComments() {
      Comments.remove(this, NodeRole.KEY, Comments.Position.BEFORE);
   }

   public void addComments(@NotNull List<String> var1) {
      Comments.add(this, NodeRole.KEY, Comments.Position.BEFORE, (List)var1.stream().map((var0) -> {
         return Comments.create(var0, Comments.Position.BEFORE);
      }).collect(Collectors.toList()));
   }

   public void addComment(@NotNull String var1) {
      Comments.add(this, NodeRole.KEY, Comments.Position.BEFORE, Comments.create(var1, Comments.Position.BEFORE));
   }

   public void setIgnored(boolean var1) {
      this.ignored = var1;
   }

   public boolean isIgnored() {
      return this.ignored;
   }

   public abstract boolean isSection();

   public T getStoredValue() {
      return this.value;
   }
}

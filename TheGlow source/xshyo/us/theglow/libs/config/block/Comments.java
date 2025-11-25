package xshyo.us.theglow.libs.config.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentLine;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentType;
import xshyo.us.theglow.libs.config.utils.format.NodeRole;

public class Comments {
   public static final CommentLine BLANK_LINE;

   @Nullable
   public static List<CommentLine> get(@NotNull Block<?> var0, @NotNull NodeRole var1, @NotNull Comments.Position var2) {
      switch(var2) {
      case BEFORE:
         return var1 == NodeRole.KEY ? var0.beforeKeyComments : var0.beforeValueComments;
      case INLINE:
         return var1 == NodeRole.KEY ? var0.inlineKeyComments : var0.inlineValueComments;
      case AFTER:
         return var1 == NodeRole.KEY ? var0.afterKeyComments : var0.afterValueComments;
      default:
         return null;
      }
   }

   @Deprecated
   @Nullable
   public static List<CommentLine> get(@NotNull Block<?> var0, @NotNull Comments.NodeType var1, @NotNull Comments.Position var2) {
      return get(var0, var1.toRole(), var2);
   }

   public static void set(@NotNull Block<?> var0, @NotNull NodeRole var1, @NotNull Comments.Position var2, @Nullable List<CommentLine> var3) {
      if (var3 != null) {
         var3 = new ArrayList((Collection)var3);
      }

      switch(var2) {
      case BEFORE:
         if (var1 == NodeRole.KEY) {
            var0.beforeKeyComments = (List)var3;
         } else {
            var0.beforeValueComments = (List)var3;
         }
         break;
      case INLINE:
         if (var1 == NodeRole.KEY) {
            var0.inlineKeyComments = (List)var3;
         } else {
            var0.inlineValueComments = (List)var3;
         }
         break;
      case AFTER:
         if (var1 == NodeRole.KEY) {
            var0.afterKeyComments = (List)var3;
         } else {
            var0.afterValueComments = (List)var3;
         }
      }

   }

   @Deprecated
   public static void set(@NotNull Block<?> var0, @NotNull Comments.NodeType var1, @NotNull Comments.Position var2, @Nullable List<CommentLine> var3) {
      set(var0, var1.toRole(), var2, var3);
   }

   public static void remove(@NotNull Block<?> var0, @NotNull NodeRole var1, @NotNull Comments.Position var2) {
      set(var0, (NodeRole)var1, var2, (List)null);
   }

   @Deprecated
   public static void remove(@NotNull Block<?> var0, @NotNull Comments.NodeType var1, @NotNull Comments.Position var2) {
      set(var0, (NodeRole)var1.toRole(), var2, (List)null);
   }

   public static void add(@NotNull Block<?> var0, @NotNull NodeRole var1, @NotNull Comments.Position var2, @NotNull List<CommentLine> var3) {
      var3.forEach((var3x) -> {
         add(var0, var1, var2, var3x);
      });
   }

   @Deprecated
   public static void add(@NotNull Block<?> var0, @NotNull Comments.NodeType var1, @NotNull Comments.Position var2, @NotNull List<CommentLine> var3) {
      var3.forEach((var3x) -> {
         add(var0, var1.toRole(), var2, var3x);
      });
   }

   public static void add(@NotNull Block<?> var0, @NotNull NodeRole var1, @NotNull Comments.Position var2, @NotNull CommentLine var3) {
      switch(var2) {
      case BEFORE:
         if (var1 == NodeRole.KEY) {
            if (var0.beforeKeyComments == null) {
               var0.beforeKeyComments = new ArrayList();
            }

            var0.beforeKeyComments.add(var3);
         } else {
            if (var0.beforeValueComments == null) {
               var0.beforeValueComments = new ArrayList();
            }

            var0.beforeValueComments.add(var3);
         }
         break;
      case INLINE:
         if (var1 == NodeRole.KEY) {
            if (var0.inlineKeyComments == null) {
               var0.inlineKeyComments = new ArrayList();
            }

            var0.inlineKeyComments.add(var3);
         } else {
            if (var0.inlineValueComments == null) {
               var0.inlineValueComments = new ArrayList();
            }

            var0.inlineValueComments.add(var3);
         }
         break;
      case AFTER:
         if (var1 == NodeRole.KEY) {
            if (var0.afterKeyComments == null) {
               var0.afterKeyComments = new ArrayList();
            }

            var0.afterKeyComments.add(var3);
         } else {
            if (var0.afterValueComments == null) {
               var0.afterValueComments = new ArrayList();
            }

            var0.afterValueComments.add(var3);
         }
      }

   }

   @Deprecated
   public static void add(@NotNull Block<?> var0, @NotNull Comments.NodeType var1, @NotNull Comments.Position var2, @NotNull CommentLine var3) {
      add(var0, var1.toRole(), var2, var3);
   }

   @NotNull
   public static CommentLine create(@NotNull String var0, @NotNull Comments.Position var1) {
      return new CommentLine(Optional.empty(), Optional.empty(), var0, var1 == Comments.Position.INLINE ? CommentType.IN_LINE : CommentType.BLOCK);
   }

   static {
      BLANK_LINE = new CommentLine(Optional.empty(), Optional.empty(), "", CommentType.BLANK_LINE);
   }

   public static enum NodeType {
      KEY,
      VALUE;

      public NodeRole toRole() {
         return this == KEY ? NodeRole.KEY : NodeRole.VALUE;
      }
   }

   public static enum Position {
      BEFORE,
      INLINE,
      AFTER;
   }
}

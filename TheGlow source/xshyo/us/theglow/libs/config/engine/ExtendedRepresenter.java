package xshyo.us.theglow.libs.config.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.YamlDocument;
import xshyo.us.theglow.libs.config.block.Block;
import xshyo.us.theglow.libs.config.block.Comments;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.DumpSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.RepresentToNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentLine;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentType;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.NodeTuple;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.SequenceNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.representer.StandardRepresenter;
import xshyo.us.theglow.libs.config.settings.dumper.DumperSettings;
import xshyo.us.theglow.libs.config.settings.general.GeneralSettings;
import xshyo.us.theglow.libs.config.utils.format.NodeRole;

public class ExtendedRepresenter extends StandardRepresenter {
   private final GeneralSettings generalSettings;
   private final DumperSettings dumperSettings;
   private NodeRole nodeRole;

   public ExtendedRepresenter(@NotNull GeneralSettings var1, @NotNull DumperSettings var2, @NotNull DumpSettings var3) {
      super(var3);
      this.nodeRole = NodeRole.KEY;
      this.generalSettings = var1;
      this.dumperSettings = var2;
      ExtendedRepresenter.RepresentSection var4 = new ExtendedRepresenter.RepresentSection();
      ExtendedRepresenter.RepresentSerializable var5 = new ExtendedRepresenter.RepresentSerializable();
      super.representers.put(Section.class, var4);
      super.representers.put(YamlDocument.class, var4);
      super.representers.put(Enum.class, new ExtendedRepresenter.RepresentEnum());
      super.representers.put(String.class, new ExtendedRepresenter.RepresentString((RepresentToNode)super.representers.get(String.class)));
      Iterator var6 = var1.getSerializer().getSupportedClasses().iterator();

      Class var7;
      while(var6.hasNext()) {
         var7 = (Class)var6.next();
         super.representers.put(var7, var5);
      }

      var6 = var1.getSerializer().getSupportedParentClasses().iterator();

      while(var6.hasNext()) {
         var7 = (Class)var6.next();
         super.parentClassRepresenters.put(var7, var5);
      }

   }

   public ExtendedRepresenter(@NotNull GeneralSettings var1, @NotNull DumperSettings var2) {
      this(var1, var2, var2.buildEngineSettings());
   }

   protected Node representScalar(Tag var1, String var2, ScalarStyle var3) {
      return super.representScalar(var1, var2, (ScalarStyle)this.dumperSettings.getScalarFormatter().format(var1, var2, this.nodeRole, var3));
   }

   protected Node representSequence(Tag var1, Iterable<?> var2, FlowStyle var3) {
      return super.representSequence(var1, var2, (FlowStyle)this.dumperSettings.getSequenceFormatter().format(var1, var2, this.nodeRole, var3));
   }

   protected Node representMapping(Tag var1, Map<?, ?> var2, FlowStyle var3) {
      return super.representMapping(var1, var2, (FlowStyle)this.dumperSettings.getMappingFormatter().format(var1, var2, this.nodeRole, var3));
   }

   private Node applyComments(@Nullable Block<?> var1, @NotNull NodeRole var2, @NotNull Node var3, boolean var4) {
      if (var1 == null) {
         return var3;
      } else {
         if (this.allowBlockComments(var4)) {
            var3.setBlockComments(Comments.get(var1, var2, Comments.Position.BEFORE));
            var3.setEndComments(Comments.get(var1, var2, Comments.Position.AFTER));
         }

         List var5 = Comments.get(var1, var2, Comments.Position.INLINE);
         if (var5 != null && !var5.isEmpty()) {
            if (this.allowInlineComments(var3)) {
               var3.setInLineComments(var5);
            } else if (this.allowBlockComments(var4)) {
               ArrayList var6 = var3.getBlockComments() == null ? new ArrayList(var5.size()) : new ArrayList(var3.getBlockComments());
               Iterator var7 = var5.iterator();

               while(var7.hasNext()) {
                  CommentLine var8 = (CommentLine)var7.next();
                  var6.add(new CommentLine(var8.getStartMark(), var8.getEndMark(), var8.getValue(), var8.getCommentType() == CommentType.IN_LINE ? CommentType.BLOCK : var8.getCommentType()));
               }

               var3.setBlockComments(var6);
            }
         }

         return var3;
      }
   }

   protected NodeTuple representMappingEntry(Entry<?, ?> var1) {
      Block var2 = var1.getValue() instanceof Block ? (Block)var1.getValue() : null;
      Node var3 = this.applyComments(var2, this.nodeRole = NodeRole.KEY, this.representData(var1.getKey()), false);
      Node var4 = this.applyComments(var2, this.nodeRole = NodeRole.VALUE, this.representData(var2 == null ? var1.getValue() : var2.getStoredValue()), false);
      return new NodeTuple(var3, var4);
   }

   private boolean allowBlockComments(boolean var1) {
      return var1 || this.settings.getDefaultFlowStyle() == FlowStyle.BLOCK;
   }

   private boolean allowInlineComments(@NotNull Node var1) {
      return this.settings.getDefaultFlowStyle() == FlowStyle.BLOCK && var1 instanceof ScalarNode || this.settings.getDefaultFlowStyle() == FlowStyle.FLOW && (var1 instanceof SequenceNode || var1 instanceof MappingNode);
   }

   private class RepresentString implements RepresentToNode {
      private final RepresentToNode previous;

      private RepresentString(@NotNull RepresentToNode var2) {
         this.previous = var2;
      }

      public Node representData(Object var1) {
         ScalarStyle var2 = ExtendedRepresenter.this.defaultScalarStyle;
         ExtendedRepresenter.this.defaultScalarStyle = ExtendedRepresenter.this.dumperSettings.getStringStyle();
         Node var3 = this.previous.representData(var1);
         ExtendedRepresenter.this.defaultScalarStyle = var2;
         return var3;
      }

      // $FF: synthetic method
      RepresentString(RepresentToNode var2, Object var3) {
         this(var2);
      }
   }

   private class RepresentEnum implements RepresentToNode {
      private RepresentEnum() {
      }

      public Node representData(Object var1) {
         return ExtendedRepresenter.this.representData(((Enum)var1).name());
      }

      // $FF: synthetic method
      RepresentEnum(Object var2) {
         this();
      }
   }

   private class RepresentSection implements RepresentToNode {
      private RepresentSection() {
      }

      public Node representData(Object var1) {
         Section var2 = (Section)var1;
         return ExtendedRepresenter.this.applyComments(var2, NodeRole.VALUE, ExtendedRepresenter.this.representData(var2.getStoredValue()), var2.isRoot());
      }

      // $FF: synthetic method
      RepresentSection(Object var2) {
         this();
      }
   }

   private class RepresentSerializable implements RepresentToNode {
      private RepresentSerializable() {
      }

      public Node representData(Object var1) {
         Map var2 = ExtendedRepresenter.this.generalSettings.getSerializer().serialize(var1, ExtendedRepresenter.this.generalSettings.getDefaultMapSupplier());
         return ExtendedRepresenter.this.representData(var2 == null ? var1 : var2);
      }

      // $FF: synthetic method
      RepresentSerializable(Object var2) {
         this();
      }
   }
}

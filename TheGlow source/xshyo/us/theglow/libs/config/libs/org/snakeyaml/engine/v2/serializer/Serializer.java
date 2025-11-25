package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.serializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.DumpSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentLine;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.Anchor;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.emitter.Emitable;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.AliasEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.CommentEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.DocumentEndEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.DocumentStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.ImplicitTuple;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.MappingEndEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.MappingStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.ScalarEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.SequenceEndEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.SequenceStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.StreamEndEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.StreamStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.AnchorNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.NodeTuple;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.NodeType;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.SequenceNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;

public class Serializer {
   private final DumpSettings settings;
   private final Emitable emitable;
   private final Set<Node> serializedNodes;
   private final Map<Node, Anchor> anchors;
   private final boolean dereferenceAliases;
   private final Set<Node> recursive;

   public Serializer(DumpSettings var1, Emitable var2) {
      this.settings = var1;
      this.emitable = var2;
      this.serializedNodes = new HashSet();
      this.anchors = new HashMap();
      this.dereferenceAliases = var1.isDereferenceAliases();
      this.recursive = Collections.newSetFromMap(new IdentityHashMap());
   }

   public void serializeDocument(Node var1) {
      this.emitable.emit(new DocumentStartEvent(this.settings.isExplicitStart(), this.settings.getYamlDirective(), this.settings.getTagDirective()));
      this.anchorNode(var1);
      Optional var10000 = this.settings.getExplicitRootTag();
      Objects.requireNonNull(var1);
      var10000.ifPresent(var1::setTag);
      this.serializeNode(var1);
      this.emitable.emit(new DocumentEndEvent(this.settings.isExplicitEnd()));
      this.serializedNodes.clear();
      this.anchors.clear();
      this.recursive.clear();
   }

   public void emitStreamStart() {
      this.emitable.emit(new StreamStartEvent());
   }

   public void emitStreamEnd() {
      this.emitable.emit(new StreamEndEvent());
   }

   private void anchorNode(Node var1) {
      Node var2;
      if (var1.getNodeType() == NodeType.ANCHOR) {
         var2 = ((AnchorNode)var1).getRealNode();
      } else {
         var2 = var1;
      }

      if (this.anchors.containsKey(var2)) {
         this.anchors.computeIfAbsent(var2, (var2x) -> {
            return this.settings.getAnchorGenerator().nextAnchor(var2);
         });
      } else {
         this.anchors.put(var2, var2.getAnchor().isPresent() ? this.settings.getAnchorGenerator().nextAnchor(var2) : null);
         switch(var2.getNodeType()) {
         case SEQUENCE:
            SequenceNode var3 = (SequenceNode)var2;
            List var4 = var3.getValue();
            Iterator var11 = var4.iterator();

            while(var11.hasNext()) {
               Node var12 = (Node)var11.next();
               this.anchorNode(var12);
            }

            return;
         case MAPPING:
            MappingNode var5 = (MappingNode)var2;
            List var6 = var5.getValue();
            Iterator var7 = var6.iterator();

            while(var7.hasNext()) {
               NodeTuple var8 = (NodeTuple)var7.next();
               Node var9 = var8.getKeyNode();
               Node var10 = var8.getValueNode();
               this.anchorNode(var9);
               this.anchorNode(var10);
            }
         }
      }

   }

   private void serializeNode(Node var1) {
      if (var1.getNodeType() == NodeType.ANCHOR) {
         var1 = ((AnchorNode)var1).getRealNode();
      }

      if (this.dereferenceAliases && this.recursive.contains(var1)) {
         throw new YamlEngineException("Cannot dereferenceAliases for recursive structures.");
      } else {
         this.recursive.add(var1);
         Optional var2;
         if (!this.dereferenceAliases) {
            var2 = Optional.ofNullable((Anchor)this.anchors.get(var1));
         } else {
            var2 = Optional.empty();
         }

         if (!this.dereferenceAliases && this.serializedNodes.contains(var1)) {
            this.emitable.emit(new AliasEvent(var2));
         } else {
            this.serializedNodes.add(var1);
            switch(var1.getNodeType()) {
            case SEQUENCE:
               SequenceNode var8 = (SequenceNode)var1;
               this.serializeComments(var1.getBlockComments());
               boolean var9 = var1.getTag().equals(Tag.SEQ);
               this.emitable.emit(new SequenceStartEvent(var2, Optional.of(var1.getTag().getValue()), var9, var8.getFlowStyle()));
               List var10 = var8.getValue();
               Iterator var11 = var10.iterator();

               while(var11.hasNext()) {
                  Node var12 = (Node)var11.next();
                  this.serializeNode(var12);
               }

               this.emitable.emit(new SequenceEndEvent());
               this.serializeComments(var1.getInLineComments());
               this.serializeComments(var1.getEndComments());
               break;
            case SCALAR:
               ScalarNode var3 = (ScalarNode)var1;
               this.serializeComments(var1.getBlockComments());
               Tag var4 = this.settings.getSchema().getScalarResolver().resolve(var3.getValue(), true);
               Tag var5 = this.settings.getSchema().getScalarResolver().resolve(var3.getValue(), false);
               ImplicitTuple var6 = new ImplicitTuple(var1.getTag().equals(var4), var1.getTag().equals(var5));
               ScalarEvent var7 = new ScalarEvent(var2, Optional.of(var1.getTag().getValue()), var6, var3.getValue(), var3.getScalarStyle());
               this.emitable.emit(var7);
               this.serializeComments(var1.getInLineComments());
               this.serializeComments(var1.getEndComments());
               break;
            default:
               this.serializeComments(var1.getBlockComments());
               boolean var18 = var1.getTag().equals(Tag.MAP);
               MappingNode var19 = (MappingNode)var1;
               List var13 = var19.getValue();
               if (var19.getTag() != Tag.COMMENT) {
                  this.emitable.emit(new MappingStartEvent(var2, Optional.of(var19.getTag().getValue()), var18, var19.getFlowStyle(), Optional.empty(), Optional.empty()));
                  Iterator var14 = var13.iterator();

                  while(var14.hasNext()) {
                     NodeTuple var15 = (NodeTuple)var14.next();
                     Node var16 = var15.getKeyNode();
                     Node var17 = var15.getValueNode();
                     this.serializeNode(var16);
                     this.serializeNode(var17);
                  }

                  this.emitable.emit(new MappingEndEvent());
                  this.serializeComments(var1.getInLineComments());
                  this.serializeComments(var1.getEndComments());
               }
            }
         }

         this.recursive.remove(var1);
      }
   }

   private void serializeComments(List<CommentLine> var1) {
      if (var1 != null) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            CommentLine var3 = (CommentLine)var2.next();
            CommentEvent var4 = new CommentEvent(var3.getCommentType(), var3.getValue(), var3.getStartMark(), var3.getEndMark());
            this.emitable.emit(var4);
         }

      }
   }
}

package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.composer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentEventsCollector;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentLine;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentType;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.Anchor;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.AliasEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.Event;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.MappingStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.NodeEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.ScalarEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.SequenceStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.ComposerException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.NodeTuple;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.NodeType;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.SequenceNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.parser.Parser;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.resolver.ScalarResolver;

public class Composer implements Iterator<Node> {
   protected final Parser parser;
   private final ScalarResolver scalarResolver;
   private final Map<Anchor, Node> anchors;
   private final Set<Node> recursiveNodes;
   private final LoadSettings settings;
   private final CommentEventsCollector blockCommentsCollector;
   private final CommentEventsCollector inlineCommentsCollector;
   private int nonScalarAliasesCount;

   @Deprecated
   public Composer(Parser var1, LoadSettings var2) {
      this(var2, var1);
   }

   public Composer(LoadSettings var1, Parser var2) {
      this.nonScalarAliasesCount = 0;
      this.parser = var2;
      this.scalarResolver = var1.getSchema().getScalarResolver();
      this.settings = var1;
      this.anchors = new HashMap();
      this.recursiveNodes = new HashSet();
      this.blockCommentsCollector = new CommentEventsCollector(var2, new CommentType[]{CommentType.BLANK_LINE, CommentType.BLOCK});
      this.inlineCommentsCollector = new CommentEventsCollector(var2, new CommentType[]{CommentType.IN_LINE});
   }

   public boolean hasNext() {
      if (this.parser.checkEvent(Event.ID.StreamStart)) {
         this.parser.next();
      }

      return !this.parser.checkEvent(Event.ID.StreamEnd);
   }

   public Optional<Node> getSingleNode() {
      this.parser.next();
      Optional var1 = Optional.empty();
      if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
         var1 = Optional.of(this.next());
      }

      if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
         Event var2 = this.parser.next();
         Optional var3 = var1.flatMap(Node::getStartMark);
         throw new ComposerException("expected a single document in the stream", var3, "but found another document", var2.getStartMark());
      } else {
         this.parser.next();
         return var1;
      }
   }

   public Node next() {
      this.blockCommentsCollector.collectEvents();
      if (this.parser.checkEvent(Event.ID.StreamEnd)) {
         List var5 = this.blockCommentsCollector.consume();
         Optional var2 = ((CommentLine)var5.get(0)).getStartMark();
         List var3 = Collections.emptyList();
         MappingNode var4 = new MappingNode(Tag.COMMENT, false, var3, FlowStyle.BLOCK, var2, Optional.empty());
         var4.setBlockComments(var5);
         return var4;
      } else {
         this.parser.next();
         Node var1 = this.composeNode(Optional.empty());
         this.blockCommentsCollector.collectEvents();
         if (!this.blockCommentsCollector.isEmpty()) {
            var1.setEndComments(this.blockCommentsCollector.consume());
         }

         this.parser.next();
         this.anchors.clear();
         this.recursiveNodes.clear();
         this.nonScalarAliasesCount = 0;
         return var1;
      }
   }

   private Node composeNode(Optional<Node> var1) {
      this.blockCommentsCollector.collectEvents();
      Set var10001 = this.recursiveNodes;
      Objects.requireNonNull(var10001);
      var1.ifPresent(var10001::add);
      Object var2;
      if (this.parser.checkEvent(Event.ID.Alias)) {
         AliasEvent var3 = (AliasEvent)this.parser.next();
         Anchor var4 = var3.getAlias();
         if (!this.anchors.containsKey(var4)) {
            throw new ComposerException("found undefined alias " + var4, var3.getStartMark());
         }

         var2 = (Node)this.anchors.get(var4);
         if (((Node)var2).getNodeType() != NodeType.SCALAR) {
            ++this.nonScalarAliasesCount;
            if (this.nonScalarAliasesCount > this.settings.getMaxAliasesForCollections()) {
               throw new YamlEngineException("Number of aliases for non-scalar nodes exceeds the specified max=" + this.settings.getMaxAliasesForCollections());
            }
         }

         if (this.recursiveNodes.remove(var2)) {
            ((Node)var2).setRecursive(true);
         }

         this.blockCommentsCollector.consume();
         this.inlineCommentsCollector.collectEvents().consume();
      } else {
         NodeEvent var5 = (NodeEvent)this.parser.peekEvent();
         Optional var6 = var5.getAnchor();
         if (this.parser.checkEvent(Event.ID.Scalar)) {
            var2 = this.composeScalarNode(var6, this.blockCommentsCollector.consume());
         } else if (this.parser.checkEvent(Event.ID.SequenceStart)) {
            var2 = this.composeSequenceNode(var6);
         } else {
            var2 = this.composeMappingNode(var6);
         }
      }

      var10001 = this.recursiveNodes;
      Objects.requireNonNull(var10001);
      var1.ifPresent(var10001::remove);
      return (Node)var2;
   }

   private void registerAnchor(Anchor var1, Node var2) {
      this.anchors.put(var1, var2);
      var2.setAnchor(Optional.of(var1));
   }

   protected Node composeScalarNode(Optional<Anchor> var1, List<CommentLine> var2) {
      ScalarEvent var3 = (ScalarEvent)this.parser.next();
      Optional var4 = var3.getTag();
      boolean var5 = false;
      Tag var6;
      if (var4.isPresent() && !((String)var4.get()).equals("!")) {
         var6 = new Tag((String)var4.get());
      } else {
         var6 = this.scalarResolver.resolve(var3.getValue(), var3.getImplicit().canOmitTagInPlainScalar());
         var5 = true;
      }

      ScalarNode var7 = new ScalarNode(var6, var5, var3.getValue(), var3.getScalarStyle(), var3.getStartMark(), var3.getEndMark());
      var1.ifPresent((var2x) -> {
         this.registerAnchor(var2x, var7);
      });
      var7.setBlockComments(var2);
      var7.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
      return var7;
   }

   protected SequenceNode composeSequenceNode(Optional<Anchor> var1) {
      SequenceStartEvent var2 = (SequenceStartEvent)this.parser.next();
      Optional var3 = var2.getTag();
      boolean var5 = false;
      Tag var4;
      if (var3.isPresent() && !((String)var3.get()).equals("!")) {
         var4 = new Tag((String)var3.get());
      } else {
         var4 = Tag.SEQ;
         var5 = true;
      }

      ArrayList var6 = new ArrayList();
      SequenceNode var7 = new SequenceNode(var4, var5, var6, var2.getFlowStyle(), var2.getStartMark(), Optional.empty());
      if (var2.isFlow()) {
         var7.setBlockComments(this.blockCommentsCollector.consume());
      }

      var1.ifPresent((var2x) -> {
         this.registerAnchor(var2x, var7);
      });

      while(!this.parser.checkEvent(Event.ID.SequenceEnd)) {
         this.blockCommentsCollector.collectEvents();
         if (this.parser.checkEvent(Event.ID.SequenceEnd)) {
            break;
         }

         var6.add(this.composeNode(Optional.of(var7)));
      }

      if (var2.isFlow()) {
         var7.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
      }

      Event var8 = this.parser.next();
      var7.setEndMark(var8.getEndMark());
      this.inlineCommentsCollector.collectEvents();
      if (!this.inlineCommentsCollector.isEmpty()) {
         var7.setInLineComments(this.inlineCommentsCollector.consume());
      }

      return var7;
   }

   protected Node composeMappingNode(Optional<Anchor> var1) {
      MappingStartEvent var2 = (MappingStartEvent)this.parser.next();
      Optional var3 = var2.getTag();
      boolean var5 = false;
      Tag var4;
      if (var3.isPresent() && !((String)var3.get()).equals("!")) {
         var4 = new Tag((String)var3.get());
      } else {
         var4 = Tag.MAP;
         var5 = true;
      }

      ArrayList var6 = new ArrayList();
      MappingNode var7 = new MappingNode(var4, var5, var6, var2.getFlowStyle(), var2.getStartMark(), Optional.empty());
      if (var2.isFlow()) {
         var7.setBlockComments(this.blockCommentsCollector.consume());
      }

      var1.ifPresent((var2x) -> {
         this.registerAnchor(var2x, var7);
      });

      while(!this.parser.checkEvent(Event.ID.MappingEnd)) {
         this.blockCommentsCollector.collectEvents();
         if (this.parser.checkEvent(Event.ID.MappingEnd)) {
            break;
         }

         this.composeMappingChildren(var6, var7);
      }

      if (var2.isFlow()) {
         var7.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
      }

      Event var8 = this.parser.next();
      var7.setEndMark(var8.getEndMark());
      this.inlineCommentsCollector.collectEvents();
      if (!this.inlineCommentsCollector.isEmpty()) {
         var7.setInLineComments(this.inlineCommentsCollector.consume());
      }

      return var7;
   }

   protected void composeMappingChildren(List<NodeTuple> var1, MappingNode var2) {
      Node var3 = this.composeKeyNode(var2);
      Node var4 = this.composeValueNode(var2);
      var1.add(new NodeTuple(var3, var4));
   }

   protected Node composeKeyNode(MappingNode var1) {
      return this.composeNode(Optional.of(var1));
   }

   protected Node composeValueNode(MappingNode var1) {
      return this.composeNode(Optional.of(var1));
   }
}

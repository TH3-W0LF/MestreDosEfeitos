package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Map.Entry;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentType;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ArrayStack;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.AliasEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.CommentEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.DocumentEndEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.DocumentStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.Event;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.ImplicitTuple;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.MappingEndEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.MappingStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.ScalarEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.SequenceEndEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.SequenceStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.StreamEndEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.StreamStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.ParserException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.scanner.Scanner;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.scanner.ScannerImpl;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.scanner.StreamReader;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.AliasToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.AnchorToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.BlockEntryToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.CommentToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.DirectiveToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.ScalarToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.StreamEndToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.StreamStartToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.TagToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.TagTuple;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.Token;

public class ParserImpl implements Parser {
   private static final Map<String, String> DEFAULT_TAGS = new HashMap();
   protected final Scanner scanner;
   private final LoadSettings settings;
   private final ArrayStack<Production> states;
   private final ArrayStack<Optional<Mark>> marksStack;
   private Optional<Event> currentEvent;
   private Optional<Production> state;
   private Map<String, String> directiveTags;

   @Deprecated
   public ParserImpl(StreamReader var1, LoadSettings var2) {
      this(var2, var1);
   }

   public ParserImpl(LoadSettings var1, StreamReader var2) {
      this((LoadSettings)var1, (Scanner)(new ScannerImpl(var1, var2)));
   }

   @Deprecated
   public ParserImpl(Scanner var1, LoadSettings var2) {
      this(var2, var1);
   }

   public ParserImpl(LoadSettings var1, Scanner var2) {
      this.scanner = var2;
      this.settings = var1;
      this.currentEvent = Optional.empty();
      this.directiveTags = new HashMap(DEFAULT_TAGS);
      this.states = new ArrayStack(100);
      this.marksStack = new ArrayStack(10);
      this.state = Optional.of(new ParserImpl.ParseStreamStart());
   }

   public boolean checkEvent(Event.ID var1) {
      this.peekEvent();
      return this.currentEvent.isPresent() && ((Event)this.currentEvent.get()).getEventId() == var1;
   }

   public Event peekEvent() {
      this.produce();
      return (Event)this.currentEvent.orElseThrow(() -> {
         return new NoSuchElementException("No more Events found.");
      });
   }

   public Event next() {
      Event var1 = this.peekEvent();
      this.currentEvent = Optional.empty();
      return var1;
   }

   public boolean hasNext() {
      this.produce();
      return this.currentEvent.isPresent();
   }

   private void produce() {
      if (!this.currentEvent.isPresent()) {
         this.state.ifPresent((var1) -> {
            this.currentEvent = Optional.of(var1.produce());
         });
      }

   }

   private CommentEvent produceCommentEvent(CommentToken var1) {
      String var2 = var1.getValue();
      CommentType var3 = var1.getCommentType();
      return new CommentEvent(var3, var2, var1.getStartMark(), var1.getEndMark());
   }

   private VersionTagsTuple processDirectives() {
      Optional var1 = Optional.empty();
      HashMap var2 = new HashMap();

      while(this.scanner.checkToken(Token.ID.Directive)) {
         DirectiveToken var3 = (DirectiveToken)this.scanner.next();
         Optional var4 = var3.getValue();
         if (var4.isPresent()) {
            List var5 = (List)var4.get();
            if (var3.getName().equals("YAML")) {
               if (var1.isPresent()) {
                  throw new ParserException("found duplicate YAML directive", var3.getStartMark());
               }

               Integer var7 = (Integer)var5.get(0);
               Integer var8 = (Integer)var5.get(1);
               var1 = Optional.of((SpecVersion)this.settings.getVersionFunction().apply(new SpecVersion(var7, var8)));
            } else if (var3.getName().equals("TAG")) {
               String var12 = (String)var5.get(0);
               String var13 = (String)var5.get(1);
               if (var2.containsKey(var12)) {
                  throw new ParserException("duplicate tag handle " + var12, var3.getStartMark());
               }

               var2.put(var12, var13);
            }
         }
      }

      HashMap var9 = new HashMap();
      if (!var2.isEmpty()) {
         var9.putAll(var2);
      }

      Iterator var10 = DEFAULT_TAGS.entrySet().iterator();

      while(var10.hasNext()) {
         Entry var11 = (Entry)var10.next();
         if (!var2.containsKey(var11.getKey())) {
            var2.put((String)var11.getKey(), (String)var11.getValue());
         }
      }

      this.directiveTags = var2;
      return new VersionTagsTuple(var1, var9);
   }

   private Event parseFlowNode() {
      return this.parseNode(false, false);
   }

   private Event parseBlockNodeOrIndentlessSequence() {
      return this.parseNode(true, true);
   }

   private Event parseNode(boolean var1, boolean var2) {
      Optional var4 = Optional.empty();
      Optional var5 = Optional.empty();
      Optional var6 = Optional.empty();
      Object var3;
      if (this.scanner.checkToken(Token.ID.Alias)) {
         AliasToken var7 = (AliasToken)this.scanner.next();
         var3 = new AliasEvent(Optional.of(var7.getValue()), var7.getStartMark(), var7.getEndMark());
         this.state = Optional.of((Production)this.states.pop());
      } else {
         Optional var13 = Optional.empty();
         TagTuple var8 = null;
         if (this.scanner.checkToken(Token.ID.Anchor)) {
            AnchorToken var9 = (AnchorToken)this.scanner.next();
            var4 = var9.getStartMark();
            var5 = var9.getEndMark();
            var13 = Optional.of(var9.getValue());
            if (this.scanner.checkToken(Token.ID.Tag)) {
               TagToken var10 = (TagToken)this.scanner.next();
               var6 = var10.getStartMark();
               var5 = var10.getEndMark();
               var8 = var10.getValue();
            }
         } else if (this.scanner.checkToken(Token.ID.Tag)) {
            TagToken var14 = (TagToken)this.scanner.next();
            var4 = var14.getStartMark();
            var6 = var4;
            var5 = var14.getEndMark();
            var8 = var14.getValue();
            if (this.scanner.checkToken(Token.ID.Anchor)) {
               AnchorToken var16 = (AnchorToken)this.scanner.next();
               var5 = var16.getEndMark();
               var13 = Optional.of(var16.getValue());
            }
         }

         Optional var15 = Optional.empty();
         if (var8 != null) {
            Optional var17 = var8.getHandle();
            String var11 = var8.getSuffix();
            if (var17.isPresent()) {
               String var12 = (String)var17.get();
               if (!this.directiveTags.containsKey(var12)) {
                  throw new ParserException("while parsing a node", var4, "found undefined tag handle " + var12, var6);
               }

               var15 = Optional.of((String)this.directiveTags.get(var12) + var11);
            } else {
               var15 = Optional.of(var11);
            }
         }

         if (!var4.isPresent()) {
            var4 = this.scanner.peekToken().getStartMark();
            var5 = var4;
         }

         boolean var18 = !var15.isPresent();
         if (var2 && this.scanner.checkToken(Token.ID.BlockEntry)) {
            var5 = this.scanner.peekToken().getEndMark();
            var3 = new SequenceStartEvent(var13, var15, var18, FlowStyle.BLOCK, var4, var5);
            this.state = Optional.of(new ParserImpl.ParseIndentlessSequenceEntryKey());
         } else if (this.scanner.checkToken(Token.ID.Scalar)) {
            ScalarToken var19 = (ScalarToken)this.scanner.next();
            var5 = var19.getEndMark();
            ImplicitTuple var21;
            if (var19.isPlain() && !var15.isPresent()) {
               var21 = new ImplicitTuple(true, false);
            } else if (!var15.isPresent()) {
               var21 = new ImplicitTuple(false, true);
            } else {
               var21 = new ImplicitTuple(false, false);
            }

            var3 = new ScalarEvent(var13, var15, var21, var19.getValue(), var19.getStyle(), var4, var5);
            this.state = Optional.of((Production)this.states.pop());
         } else if (this.scanner.checkToken(Token.ID.FlowSequenceStart)) {
            var5 = this.scanner.peekToken().getEndMark();
            var3 = new SequenceStartEvent(var13, var15, var18, FlowStyle.FLOW, var4, var5);
            this.state = Optional.of(new ParserImpl.ParseFlowSequenceFirstEntry());
         } else if (this.scanner.checkToken(Token.ID.FlowMappingStart)) {
            var5 = this.scanner.peekToken().getEndMark();
            var3 = new MappingStartEvent(var13, var15, var18, FlowStyle.FLOW, var4, var5);
            this.state = Optional.of(new ParserImpl.ParseFlowMappingFirstKey());
         } else if (var1 && this.scanner.checkToken(Token.ID.BlockSequenceStart)) {
            var5 = this.scanner.peekToken().getStartMark();
            var3 = new SequenceStartEvent(var13, var15, var18, FlowStyle.BLOCK, var4, var5);
            this.state = Optional.of(new ParserImpl.ParseBlockSequenceFirstEntry());
         } else if (var1 && this.scanner.checkToken(Token.ID.BlockMappingStart)) {
            var5 = this.scanner.peekToken().getStartMark();
            var3 = new MappingStartEvent(var13, var15, var18, FlowStyle.BLOCK, var4, var5);
            this.state = Optional.of(new ParserImpl.ParseBlockMappingFirstKey());
         } else {
            if (!var13.isPresent() && !var15.isPresent()) {
               Token var20 = this.scanner.peekToken();
               throw new ParserException("while parsing a " + (var1 ? "block" : "flow") + " node", var4, "expected the node content, but found '" + var20.getTokenId() + "'", var20.getStartMark());
            }

            var3 = new ScalarEvent(var13, var15, new ImplicitTuple(var18, false), "", ScalarStyle.PLAIN, var4, var5);
            this.state = Optional.of((Production)this.states.pop());
         }
      }

      return (Event)var3;
   }

   private Event processEmptyScalar(Optional<Mark> var1) {
      return new ScalarEvent(Optional.empty(), Optional.empty(), new ImplicitTuple(true, false), "", ScalarStyle.PLAIN, var1, var1);
   }

   private Optional<Mark> markPop() {
      return (Optional)this.marksStack.pop();
   }

   private void markPush(Optional<Mark> var1) {
      this.marksStack.push(var1);
   }

   static {
      DEFAULT_TAGS.put("!", "!");
      DEFAULT_TAGS.put("!!", "tag:yaml.org,2002:");
   }

   private class ParseStreamStart implements Production {
      private ParseStreamStart() {
      }

      public Event produce() {
         StreamStartToken var1 = (StreamStartToken)ParserImpl.this.scanner.next();
         StreamStartEvent var2 = new StreamStartEvent(var1.getStartMark(), var1.getEndMark());
         ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseImplicitDocumentStart());
         return var2;
      }

      // $FF: synthetic method
      ParseStreamStart(Object var2) {
         this();
      }
   }

   private class ParseIndentlessSequenceEntryKey implements Production {
      private ParseIndentlessSequenceEntryKey() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseIndentlessSequenceEntryKey());
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.next());
         } else if (ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry)) {
            BlockEntryToken var3 = (BlockEntryToken)ParserImpl.this.scanner.next();
            return (ParserImpl.this.new ParseIndentlessSequenceEntryValue(var3)).produce();
         } else {
            Token var1 = ParserImpl.this.scanner.peekToken();
            SequenceEndEvent var2 = new SequenceEndEvent(var1.getStartMark(), var1.getEndMark());
            ParserImpl.this.state = Optional.of((Production)ParserImpl.this.states.pop());
            return var2;
         }
      }

      // $FF: synthetic method
      ParseIndentlessSequenceEntryKey(Object var2) {
         this();
      }
   }

   private class ParseFlowSequenceFirstEntry implements Production {
      private ParseFlowSequenceFirstEntry() {
      }

      public Event produce() {
         Token var1 = ParserImpl.this.scanner.next();
         ParserImpl.this.markPush(var1.getStartMark());
         return (ParserImpl.this.new ParseFlowSequenceEntry(true)).produce();
      }

      // $FF: synthetic method
      ParseFlowSequenceFirstEntry(Object var2) {
         this();
      }
   }

   private class ParseFlowMappingFirstKey implements Production {
      private ParseFlowMappingFirstKey() {
      }

      public Event produce() {
         Token var1 = ParserImpl.this.scanner.next();
         ParserImpl.this.markPush(var1.getStartMark());
         return (ParserImpl.this.new ParseFlowMappingKey(true)).produce();
      }

      // $FF: synthetic method
      ParseFlowMappingFirstKey(Object var2) {
         this();
      }
   }

   private class ParseBlockSequenceFirstEntry implements Production {
      private ParseBlockSequenceFirstEntry() {
      }

      public Event produce() {
         Token var1 = ParserImpl.this.scanner.next();
         ParserImpl.this.markPush(var1.getStartMark());
         return (ParserImpl.this.new ParseBlockSequenceEntryKey()).produce();
      }

      // $FF: synthetic method
      ParseBlockSequenceFirstEntry(Object var2) {
         this();
      }
   }

   private class ParseBlockMappingFirstKey implements Production {
      private ParseBlockMappingFirstKey() {
      }

      public Event produce() {
         Token var1 = ParserImpl.this.scanner.next();
         ParserImpl.this.markPush(var1.getStartMark());
         return (ParserImpl.this.new ParseBlockMappingKey()).produce();
      }

      // $FF: synthetic method
      ParseBlockMappingFirstKey(Object var2) {
         this();
      }
   }

   private class ParseFlowMappingEmptyValue implements Production {
      private ParseFlowMappingEmptyValue() {
      }

      public Event produce() {
         ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseFlowMappingKey(false));
         return ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
      }

      // $FF: synthetic method
      ParseFlowMappingEmptyValue(Object var2) {
         this();
      }
   }

   private class ParseFlowMappingValue implements Production {
      private ParseFlowMappingValue() {
      }

      public Event produce() {
         Token var1;
         if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
            var1 = ParserImpl.this.scanner.next();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseFlowMappingKey(false));
               return ParserImpl.this.parseFlowNode();
            } else {
               ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseFlowMappingKey(false));
               return ParserImpl.this.processEmptyScalar(var1.getEndMark());
            }
         } else {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseFlowMappingKey(false));
            var1 = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(var1.getStartMark());
         }
      }

      // $FF: synthetic method
      ParseFlowMappingValue(Object var2) {
         this();
      }
   }

   private class ParseFlowMappingKey implements Production {
      private final boolean first;

      public ParseFlowMappingKey(boolean var2) {
         this.first = var2;
      }

      public Event produce() {
         Token var1;
         if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowMappingEnd)) {
            if (!this.first) {
               if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry)) {
                  var1 = ParserImpl.this.scanner.peekToken();
                  throw new ParserException("while parsing a flow mapping", ParserImpl.this.markPop(), "expected ',' or '}', but got " + var1.getTokenId(), var1.getStartMark());
               }

               ParserImpl.this.scanner.next();
            }

            if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
               var1 = ParserImpl.this.scanner.next();
               if (!ParserImpl.this.scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
                  ParserImpl.this.states.push(ParserImpl.this.new ParseFlowMappingValue());
                  return ParserImpl.this.parseFlowNode();
               }

               ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseFlowMappingValue());
               return ParserImpl.this.processEmptyScalar(var1.getEndMark());
            }

            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowMappingEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseFlowMappingEmptyValue());
               return ParserImpl.this.parseFlowNode();
            }
         }

         var1 = ParserImpl.this.scanner.next();
         MappingEndEvent var2 = new MappingEndEvent(var1.getStartMark(), var1.getEndMark());
         ParserImpl.this.markPop();
         if (!ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = Optional.of((Production)ParserImpl.this.states.pop());
         } else {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseFlowEndComment());
         }

         return var2;
      }
   }

   private class ParseFlowSequenceEntryMappingEnd implements Production {
      private ParseFlowSequenceEntryMappingEnd() {
      }

      public Event produce() {
         ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseFlowSequenceEntry(false));
         Token var1 = ParserImpl.this.scanner.peekToken();
         return new MappingEndEvent(var1.getStartMark(), var1.getEndMark());
      }

      // $FF: synthetic method
      ParseFlowSequenceEntryMappingEnd(Object var2) {
         this();
      }
   }

   private class ParseFlowSequenceEntryMappingValue implements Production {
      private ParseFlowSequenceEntryMappingValue() {
      }

      public Event produce() {
         Token var1;
         if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
            var1 = ParserImpl.this.scanner.next();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseFlowSequenceEntryMappingEnd());
               return ParserImpl.this.parseFlowNode();
            } else {
               ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseFlowSequenceEntryMappingEnd());
               return ParserImpl.this.processEmptyScalar(var1.getEndMark());
            }
         } else {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseFlowSequenceEntryMappingEnd());
            var1 = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(var1.getStartMark());
         }
      }

      // $FF: synthetic method
      ParseFlowSequenceEntryMappingValue(Object var2) {
         this();
      }
   }

   private class ParseFlowSequenceEntryMappingKey implements Production {
      private ParseFlowSequenceEntryMappingKey() {
      }

      public Event produce() {
         Token var1 = ParserImpl.this.scanner.next();
         if (!ParserImpl.this.scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
            ParserImpl.this.states.push(ParserImpl.this.new ParseFlowSequenceEntryMappingValue());
            return ParserImpl.this.parseFlowNode();
         } else {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseFlowSequenceEntryMappingValue());
            return ParserImpl.this.processEmptyScalar(var1.getEndMark());
         }
      }

      // $FF: synthetic method
      ParseFlowSequenceEntryMappingKey(Object var2) {
         this();
      }
   }

   private class ParseFlowEndComment implements Production {
      private ParseFlowEndComment() {
      }

      public Event produce() {
         CommentEvent var1 = ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.next());
         if (!ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = Optional.of((Production)ParserImpl.this.states.pop());
         }

         return var1;
      }

      // $FF: synthetic method
      ParseFlowEndComment(Object var2) {
         this();
      }
   }

   private class ParseFlowSequenceEntry implements Production {
      private final boolean first;

      public ParseFlowSequenceEntry(boolean var2) {
         this.first = var2;
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseFlowSequenceEntry(this.first));
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.next());
         } else {
            Token var1;
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowSequenceEnd)) {
               if (!this.first) {
                  if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry)) {
                     var1 = ParserImpl.this.scanner.peekToken();
                     throw new ParserException("while parsing a flow sequence", ParserImpl.this.markPop(), "expected ',' or ']', but got " + var1.getTokenId(), var1.getStartMark());
                  }

                  ParserImpl.this.scanner.next();
                  if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
                     ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseFlowSequenceEntry(true));
                     return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.next());
                  }
               }

               if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
                  var1 = ParserImpl.this.scanner.peekToken();
                  MappingStartEvent var3 = new MappingStartEvent(Optional.empty(), Optional.empty(), true, FlowStyle.FLOW, var1.getStartMark(), var1.getEndMark());
                  ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseFlowSequenceEntryMappingKey());
                  return var3;
               }

               if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowSequenceEnd)) {
                  ParserImpl.this.states.push(ParserImpl.this.new ParseFlowSequenceEntry(false));
                  return ParserImpl.this.parseFlowNode();
               }
            }

            var1 = ParserImpl.this.scanner.next();
            SequenceEndEvent var2 = new SequenceEndEvent(var1.getStartMark(), var1.getEndMark());
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
               ParserImpl.this.state = Optional.of((Production)ParserImpl.this.states.pop());
            } else {
               ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseFlowEndComment());
            }

            ParserImpl.this.markPop();
            return var2;
         }
      }
   }

   private class ParseBlockMappingValueCommentList implements Production {
      List<CommentToken> tokens;

      public ParseBlockMappingValueCommentList(List<CommentToken> var2) {
         this.tokens = var2;
      }

      public Event produce() {
         return (Event)(!this.tokens.isEmpty() ? ParserImpl.this.produceCommentEvent((CommentToken)this.tokens.remove(0)) : (ParserImpl.this.new ParseBlockMappingKey()).produce());
      }
   }

   private class ParseBlockMappingValueComment implements Production {
      List<CommentToken> tokens;

      private ParseBlockMappingValueComment() {
         this.tokens = new LinkedList();
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            this.tokens.add((CommentToken)ParserImpl.this.scanner.next());
            return this.produce();
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
            if (!this.tokens.isEmpty()) {
               return ParserImpl.this.produceCommentEvent((CommentToken)this.tokens.remove(0));
            } else {
               ParserImpl.this.states.push(ParserImpl.this.new ParseBlockMappingKey());
               return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
            }
         } else {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseBlockMappingValueCommentList(this.tokens));
            return ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
         }
      }

      // $FF: synthetic method
      ParseBlockMappingValueComment(Object var2) {
         this();
      }
   }

   private class ParseBlockMappingValue implements Production {
      private ParseBlockMappingValue() {
      }

      public Event produce() {
         Token var1;
         if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
            var1 = ParserImpl.this.scanner.next();
            if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
               ParserImpl.ParseBlockMappingValueComment var2 = ParserImpl.this.new ParseBlockMappingValueComment();
               ParserImpl.this.state = Optional.of(var2);
               return var2.produce();
            } else if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseBlockMappingKey());
               return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
            } else {
               ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseBlockMappingKey());
               return ParserImpl.this.processEmptyScalar(var1.getEndMark());
            }
         } else if (ParserImpl.this.scanner.checkToken(Token.ID.Scalar)) {
            ParserImpl.this.states.push(ParserImpl.this.new ParseBlockMappingKey());
            return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
         } else {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseBlockMappingKey());
            var1 = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(var1.getStartMark());
         }
      }

      // $FF: synthetic method
      ParseBlockMappingValue(Object var2) {
         this();
      }
   }

   private class ParseBlockMappingKey implements Production {
      private ParseBlockMappingKey() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseBlockMappingKey());
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.next());
         } else {
            Token var1;
            if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
               var1 = ParserImpl.this.scanner.next();
               if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
                  ParserImpl.this.states.push(ParserImpl.this.new ParseBlockMappingValue());
                  return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
               } else {
                  ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseBlockMappingValue());
                  return ParserImpl.this.processEmptyScalar(var1.getEndMark());
               }
            } else if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEnd)) {
               var1 = ParserImpl.this.scanner.peekToken();
               throw new ParserException("while parsing a block mapping", ParserImpl.this.markPop(), "expected <block end>, but found '" + var1.getTokenId() + "'", var1.getStartMark());
            } else {
               var1 = ParserImpl.this.scanner.next();
               MappingEndEvent var2 = new MappingEndEvent(var1.getStartMark(), var1.getEndMark());
               ParserImpl.this.state = Optional.of((Production)ParserImpl.this.states.pop());
               ParserImpl.this.markPop();
               return var2;
            }
         }
      }

      // $FF: synthetic method
      ParseBlockMappingKey(Object var2) {
         this();
      }
   }

   private class ParseIndentlessSequenceEntryValue implements Production {
      BlockEntryToken token;

      public ParseIndentlessSequenceEntryValue(BlockEntryToken var2) {
         this.token = var2;
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseIndentlessSequenceEntryValue(this.token));
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.next());
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry, Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
            ParserImpl.this.states.push(ParserImpl.this.new ParseIndentlessSequenceEntryKey());
            return (ParserImpl.this.new ParseBlockNode()).produce();
         } else {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseIndentlessSequenceEntryKey());
            return ParserImpl.this.processEmptyScalar(this.token.getEndMark());
         }
      }
   }

   private class ParseBlockSequenceEntryValue implements Production {
      BlockEntryToken token;

      public ParseBlockSequenceEntryValue(BlockEntryToken var2) {
         this.token = var2;
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseBlockSequenceEntryValue(this.token));
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.next());
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry, Token.ID.BlockEnd)) {
            ParserImpl.this.states.push(ParserImpl.this.new ParseBlockSequenceEntryKey());
            return (ParserImpl.this.new ParseBlockNode()).produce();
         } else {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseBlockSequenceEntryKey());
            return ParserImpl.this.processEmptyScalar(this.token.getEndMark());
         }
      }
   }

   private class ParseBlockSequenceEntryKey implements Production {
      private ParseBlockSequenceEntryKey() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseBlockSequenceEntryKey());
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.next());
         } else if (ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry)) {
            BlockEntryToken var3 = (BlockEntryToken)ParserImpl.this.scanner.next();
            return (ParserImpl.this.new ParseBlockSequenceEntryValue(var3)).produce();
         } else {
            Token var1;
            if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEnd)) {
               var1 = ParserImpl.this.scanner.peekToken();
               throw new ParserException("while parsing a block collection", ParserImpl.this.markPop(), "expected <block end>, but found '" + var1.getTokenId() + "'", var1.getStartMark());
            } else {
               var1 = ParserImpl.this.scanner.next();
               SequenceEndEvent var2 = new SequenceEndEvent(var1.getStartMark(), var1.getEndMark());
               ParserImpl.this.state = Optional.of((Production)ParserImpl.this.states.pop());
               ParserImpl.this.markPop();
               return var2;
            }
         }
      }

      // $FF: synthetic method
      ParseBlockSequenceEntryKey(Object var2) {
         this();
      }
   }

   private class ParseBlockNode implements Production {
      private ParseBlockNode() {
      }

      public Event produce() {
         return ParserImpl.this.parseNode(true, false);
      }

      // $FF: synthetic method
      ParseBlockNode(Object var2) {
         this();
      }
   }

   private class ParseDocumentContent implements Production {
      private ParseDocumentContent() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseDocumentContent());
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.next());
         } else if (ParserImpl.this.scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.DocumentEnd, Token.ID.StreamEnd)) {
            Event var1 = ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
            ParserImpl.this.state = Optional.of((Production)ParserImpl.this.states.pop());
            return var1;
         } else {
            return (ParserImpl.this.new ParseBlockNode()).produce();
         }
      }

      // $FF: synthetic method
      ParseDocumentContent(Object var2) {
         this();
      }
   }

   private class ParseDocumentEnd implements Production {
      private ParseDocumentEnd() {
      }

      public Event produce() {
         Token var1 = ParserImpl.this.scanner.peekToken();
         Optional var2 = var1.getStartMark();
         Optional var3 = var2;
         boolean var4 = false;
         if (ParserImpl.this.scanner.checkToken(Token.ID.DocumentEnd)) {
            var1 = ParserImpl.this.scanner.next();
            var3 = var1.getEndMark();
            var4 = true;
         } else if (ParserImpl.this.scanner.checkToken(Token.ID.Directive)) {
            throw new ParserException("expected '<document end>' before directives, but found '" + ParserImpl.this.scanner.peekToken().getTokenId() + "'", ParserImpl.this.scanner.peekToken().getStartMark());
         }

         ParserImpl.this.directiveTags.clear();
         DocumentEndEvent var5 = new DocumentEndEvent(var4, var2, var3);
         ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseDocumentStart());
         return var5;
      }

      // $FF: synthetic method
      ParseDocumentEnd(Object var2) {
         this();
      }
   }

   private class ParseDocumentStart implements Production {
      private ParseDocumentStart() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseDocumentStart());
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.next());
         } else {
            while(ParserImpl.this.scanner.checkToken(Token.ID.DocumentEnd)) {
               ParserImpl.this.scanner.next();
            }

            if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
               ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseDocumentStart());
               return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.next());
            } else if (ParserImpl.this.scanner.checkToken(Token.ID.StreamEnd)) {
               StreamEndToken var7 = (StreamEndToken)ParserImpl.this.scanner.next();
               StreamEndEvent var6 = new StreamEndEvent(var7.getStartMark(), var7.getEndMark());
               if (!ParserImpl.this.states.isEmpty()) {
                  throw new YamlEngineException("Unexpected end of stream. States left: " + ParserImpl.this.states);
               } else if (!this.markEmpty()) {
                  throw new YamlEngineException("Unexpected end of stream. Marks left: " + ParserImpl.this.marksStack);
               } else {
                  ParserImpl.this.state = Optional.empty();
                  return var6;
               }
            } else {
               ParserImpl.this.scanner.resetDocumentIndex();
               Token var2 = ParserImpl.this.scanner.peekToken();
               Optional var3 = var2.getStartMark();
               VersionTagsTuple var4 = ParserImpl.this.processDirectives();

               while(ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
                  ParserImpl.this.scanner.next();
               }

               if (!ParserImpl.this.scanner.checkToken(Token.ID.StreamEnd)) {
                  if (!ParserImpl.this.scanner.checkToken(Token.ID.DocumentStart)) {
                     throw new ParserException("expected '<document start>', but found '" + ParserImpl.this.scanner.peekToken().getTokenId() + "'", ParserImpl.this.scanner.peekToken().getStartMark());
                  } else {
                     var2 = ParserImpl.this.scanner.next();
                     Optional var5 = var2.getEndMark();
                     DocumentStartEvent var1 = new DocumentStartEvent(true, var4.getSpecVersion(), var4.getTags(), var3, var5);
                     ParserImpl.this.states.push(ParserImpl.this.new ParseDocumentEnd());
                     ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseDocumentContent());
                     return var1;
                  }
               } else {
                  throw new ParserException("expected '<document start>', but found '" + ParserImpl.this.scanner.peekToken().getTokenId() + "'", ParserImpl.this.scanner.peekToken().getStartMark());
               }
            }
         }
      }

      private boolean markEmpty() {
         return ParserImpl.this.marksStack.isEmpty();
      }

      // $FF: synthetic method
      ParseDocumentStart(Object var2) {
         this();
      }
   }

   private class ParseImplicitDocumentStart implements Production {
      private ParseImplicitDocumentStart() {
      }

      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseImplicitDocumentStart());
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.next());
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.StreamEnd)) {
            Token var1 = ParserImpl.this.scanner.peekToken();
            Optional var2 = var1.getStartMark();
            DocumentStartEvent var3 = new DocumentStartEvent(false, Optional.empty(), Collections.emptyMap(), var2, var2);
            ParserImpl.this.states.push(ParserImpl.this.new ParseDocumentEnd());
            ParserImpl.this.state = Optional.of(ParserImpl.this.new ParseBlockNode());
            return var3;
         } else {
            return (ParserImpl.this.new ParseDocumentStart()).produce();
         }
      }

      // $FF: synthetic method
      ParseImplicitDocumentStart(Object var2) {
         this();
      }
   }
}

package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.emitter;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.TreeSet;
import java.util.regex.Pattern;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.DumpSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.StreamDataWriter;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentEventsCollector;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentLine;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentType;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.Anchor;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ArrayStack;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.CharConstants;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.AliasEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.CollectionEndEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.CollectionStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.CommentEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.DocumentEndEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.DocumentStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.Event;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.MappingStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.NodeEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.ScalarEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.SequenceStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.StreamEndEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.StreamStartEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.EmitterException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.scanner.StreamReader;

public final class Emitter implements Emitable {
   private static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap();
   public static final int MIN_INDENT = 1;
   public static final int MAX_INDENT = 10;
   private static final String SPACE = " ";
   private static final Map<String, String> DEFAULT_TAG_PREFIXES;
   private final StreamDataWriter stream;
   private final ArrayStack<EmitterState> states;
   private EmitterState state;
   private final Queue<Event> events;
   private Event event;
   private final ArrayStack<Integer> indents;
   private Integer indent;
   private int flowLevel;
   private boolean rootContext;
   private boolean mappingContext;
   private boolean simpleKeyContext;
   private int column;
   private boolean whitespace;
   private boolean indention;
   private boolean openEnded;
   private final Boolean canonical;
   private final Boolean multiLineFlow;
   private final boolean allowUnicode;
   private int bestIndent;
   private final int indicatorIndent;
   private final boolean indentWithIndicator;
   private int bestWidth;
   private final String bestLineBreak;
   private final boolean splitLines;
   private final int maxSimpleKeyLength;
   private final boolean emitComments;
   private Map<String, String> tagPrefixes;
   private Optional<Anchor> preparedAnchor;
   private String preparedTag;
   private ScalarAnalysis analysis;
   private ScalarStyle scalarStyle;
   private final CommentEventsCollector blockCommentsCollector;
   private final CommentEventsCollector inlineCommentsCollector;
   private static final Pattern HANDLE_FORMAT;

   public Emitter(DumpSettings var1, StreamDataWriter var2) {
      this.stream = var2;
      this.states = new ArrayStack(100);
      this.state = new Emitter.ExpectStreamStart();
      this.events = new ArrayDeque(100);
      this.event = null;
      this.indents = new ArrayStack(10);
      this.indent = null;
      this.flowLevel = 0;
      this.mappingContext = false;
      this.simpleKeyContext = false;
      this.column = 0;
      this.whitespace = true;
      this.indention = true;
      this.openEnded = false;
      this.canonical = var1.isCanonical();
      this.multiLineFlow = var1.isMultiLineFlow();
      this.allowUnicode = var1.isUseUnicodeEncoding();
      this.bestIndent = 2;
      if (var1.getIndent() > 1 && var1.getIndent() < 10) {
         this.bestIndent = var1.getIndent();
      }

      this.indicatorIndent = var1.getIndicatorIndent();
      this.indentWithIndicator = var1.getIndentWithIndicator();
      this.bestWidth = 80;
      if (var1.getWidth() > this.bestIndent * 2) {
         this.bestWidth = var1.getWidth();
      }

      this.bestLineBreak = var1.getBestLineBreak();
      this.splitLines = var1.isSplitLines();
      this.maxSimpleKeyLength = var1.getMaxSimpleKeyLength();
      this.emitComments = var1.getDumpComments();
      this.tagPrefixes = new LinkedHashMap();
      this.preparedAnchor = Optional.empty();
      this.preparedTag = null;
      this.analysis = null;
      this.scalarStyle = null;
      this.blockCommentsCollector = new CommentEventsCollector(this.events, new CommentType[]{CommentType.BLANK_LINE, CommentType.BLOCK});
      this.inlineCommentsCollector = new CommentEventsCollector(this.events, new CommentType[]{CommentType.IN_LINE});
   }

   public void emit(Event var1) {
      this.events.add(var1);

      while(!this.needMoreEvents()) {
         this.event = (Event)this.events.poll();
         this.state.expect();
         this.event = null;
      }

   }

   private boolean needMoreEvents() {
      if (this.events.isEmpty()) {
         return true;
      } else {
         Iterator var1 = this.events.iterator();

         Event var2;
         for(var2 = (Event)var1.next(); var2 instanceof CommentEvent; var2 = (Event)var1.next()) {
            if (!var1.hasNext()) {
               return true;
            }
         }

         if (var2 instanceof DocumentStartEvent) {
            return this.needEvents(var1, 1);
         } else if (var2 instanceof SequenceStartEvent) {
            return this.needEvents(var1, 2);
         } else if (var2 instanceof MappingStartEvent) {
            return this.needEvents(var1, 3);
         } else if (var2 instanceof StreamStartEvent) {
            return this.needEvents(var1, 2);
         } else if (var2 instanceof StreamEndEvent) {
            return false;
         } else if (this.emitComments) {
            return this.needEvents(var1, 1);
         } else {
            return false;
         }
      }
   }

   private boolean needEvents(Iterator<Event> var1, int var2) {
      int var3 = 0;
      int var4 = 0;

      do {
         Event var5;
         do {
            if (!var1.hasNext()) {
               return var4 < var2;
            }

            var5 = (Event)var1.next();
         } while(var5 instanceof CommentEvent);

         ++var4;
         if (!(var5 instanceof DocumentStartEvent) && !(var5 instanceof CollectionStartEvent)) {
            if (!(var5 instanceof DocumentEndEvent) && !(var5 instanceof CollectionEndEvent)) {
               if (var5 instanceof StreamEndEvent) {
                  var3 = -1;
               }
            } else {
               --var3;
            }
         } else {
            ++var3;
         }
      } while(var3 >= 0);

      return false;
   }

   private void increaseIndent(boolean var1, boolean var2) {
      this.indents.push(this.indent);
      if (this.indent == null) {
         if (var1) {
            this.indent = this.bestIndent;
         } else {
            this.indent = 0;
         }
      } else if (!var2) {
         this.indent = this.indent + this.bestIndent;
      }

   }

   private void expectNode(boolean var1, boolean var2, boolean var3) {
      this.rootContext = var1;
      this.mappingContext = var2;
      this.simpleKeyContext = var3;
      if (this.event.getEventId() == Event.ID.Alias) {
         this.expectAlias();
      } else {
         if (this.event.getEventId() != Event.ID.Scalar && this.event.getEventId() != Event.ID.SequenceStart && this.event.getEventId() != Event.ID.MappingStart) {
            throw new EmitterException("expected NodeEvent, but got " + this.event.getEventId());
         }

         this.processAnchor("&");
         this.processTag();
         this.handleNodeEvent(this.event.getEventId());
      }

   }

   private void handleNodeEvent(Event.ID var1) {
      switch(var1) {
      case Scalar:
         this.expectScalar();
         break;
      case SequenceStart:
         if (this.flowLevel == 0 && !this.canonical && !((SequenceStartEvent)this.event).isFlow() && !this.checkEmptySequence()) {
            this.expectBlockSequence();
         } else {
            this.expectFlowSequence();
         }
         break;
      case MappingStart:
         if (this.flowLevel == 0 && !this.canonical && !((MappingStartEvent)this.event).isFlow() && !this.checkEmptyMapping()) {
            this.expectBlockMapping();
         } else {
            this.expectFlowMapping();
         }
         break;
      default:
         throw new IllegalStateException();
      }

   }

   private void expectAlias() {
      if (this.event instanceof AliasEvent) {
         this.processAnchor("*");
         this.state = (EmitterState)this.states.pop();
      } else {
         throw new EmitterException("Expecting Alias.");
      }
   }

   private void expectScalar() {
      this.increaseIndent(true, false);
      this.processScalar();
      this.indent = (Integer)this.indents.pop();
      this.state = (EmitterState)this.states.pop();
   }

   private void expectFlowSequence() {
      this.writeIndicator("[", true, true, false);
      ++this.flowLevel;
      this.increaseIndent(true, false);
      if (this.multiLineFlow) {
         this.writeIndent();
      }

      this.state = new Emitter.ExpectFirstFlowSequenceItem();
   }

   private void expectFlowMapping() {
      this.writeIndicator("{", true, true, false);
      ++this.flowLevel;
      this.increaseIndent(true, false);
      if (this.multiLineFlow) {
         this.writeIndent();
      }

      this.state = new Emitter.ExpectFirstFlowMappingKey();
   }

   private void expectBlockSequence() {
      boolean var1 = this.mappingContext && !this.indention;
      this.increaseIndent(false, var1);
      this.state = new Emitter.ExpectFirstBlockSequenceItem();
   }

   private void expectBlockMapping() {
      this.increaseIndent(false, false);
      this.state = new Emitter.ExpectFirstBlockMappingKey();
   }

   private boolean isFoldedOrLiteral(Event var1) {
      if (var1.getEventId() != Event.ID.Scalar) {
         return false;
      } else {
         ScalarEvent var2 = (ScalarEvent)var1;
         ScalarStyle var3 = var2.getScalarStyle();
         return var3 == ScalarStyle.FOLDED || var3 == ScalarStyle.LITERAL;
      }
   }

   private boolean checkEmptySequence() {
      return this.event.getEventId() == Event.ID.SequenceStart && !this.events.isEmpty() && ((Event)this.events.peek()).getEventId() == Event.ID.SequenceEnd;
   }

   private boolean checkEmptyMapping() {
      return this.event.getEventId() == Event.ID.MappingStart && !this.events.isEmpty() && ((Event)this.events.peek()).getEventId() == Event.ID.MappingEnd;
   }

   private boolean checkSimpleKey() {
      int var1 = 0;
      Optional var2;
      if (this.event instanceof NodeEvent) {
         var2 = ((NodeEvent)this.event).getAnchor();
         if (var2.isPresent()) {
            if (!this.preparedAnchor.isPresent()) {
               this.preparedAnchor = var2;
            }

            var1 += ((Anchor)var2.get()).getValue().length();
         }
      }

      var2 = Optional.empty();
      if (this.event.getEventId() == Event.ID.Scalar) {
         var2 = ((ScalarEvent)this.event).getTag();
      } else if (this.event instanceof CollectionStartEvent) {
         var2 = ((CollectionStartEvent)this.event).getTag();
      }

      if (var2.isPresent()) {
         if (this.preparedTag == null) {
            this.preparedTag = this.prepareTag((String)var2.get());
         }

         var1 += this.preparedTag.length();
      }

      if (this.event.getEventId() == Event.ID.Scalar) {
         if (this.analysis == null) {
            this.analysis = this.analyzeScalar(((ScalarEvent)this.event).getValue());
         }

         var1 += this.analysis.getScalar().length();
      }

      return var1 < this.maxSimpleKeyLength && (this.event.getEventId() == Event.ID.Alias || this.event.getEventId() == Event.ID.Scalar && !this.analysis.isEmpty() && !this.analysis.isMultiline() || this.checkEmptySequence() || this.checkEmptyMapping());
   }

   private void processAnchor(String var1) {
      NodeEvent var2 = (NodeEvent)this.event;
      Optional var3 = var2.getAnchor();
      if (var3.isPresent()) {
         Anchor var4 = (Anchor)var3.get();
         if (!this.preparedAnchor.isPresent()) {
            this.preparedAnchor = var3;
         }

         this.writeIndicator(var1 + var4, true, false, false);
      }

      this.preparedAnchor = Optional.empty();
   }

   private void processTag() {
      Optional var1;
      if (this.event.getEventId() == Event.ID.Scalar) {
         ScalarEvent var3 = (ScalarEvent)this.event;
         var1 = var3.getTag();
         if (this.scalarStyle == null) {
            this.scalarStyle = this.chooseScalarStyle(var3);
         }

         if ((!this.canonical || !var1.isPresent()) && (this.scalarStyle == ScalarStyle.PLAIN && var3.getImplicit().canOmitTagInPlainScalar() || this.scalarStyle != ScalarStyle.PLAIN && var3.getImplicit().canOmitTagInNonPlainScalar())) {
            this.preparedTag = null;
            return;
         }

         if (var3.getImplicit().canOmitTagInPlainScalar() && !var1.isPresent()) {
            var1 = Optional.of("!");
            this.preparedTag = null;
         }
      } else {
         CollectionStartEvent var2 = (CollectionStartEvent)this.event;
         var1 = var2.getTag();
         if ((!this.canonical || !var1.isPresent()) && var2.isImplicit()) {
            this.preparedTag = null;
            return;
         }
      }

      if (!var1.isPresent()) {
         throw new EmitterException("tag is not specified");
      } else {
         if (this.preparedTag == null) {
            this.preparedTag = this.prepareTag((String)var1.get());
         }

         this.writeIndicator(this.preparedTag, true, false, false);
         this.preparedTag = null;
      }
   }

   private ScalarStyle chooseScalarStyle(ScalarEvent var1) {
      if (this.analysis == null) {
         this.analysis = this.analyzeScalar(var1.getValue());
      }

      if ((var1.isPlain() || !var1.isDQuoted()) && !this.canonical) {
         if (var1.isJson() && Optional.of(Tag.STR.getValue()).equals(var1.getTag())) {
            return ScalarStyle.DOUBLE_QUOTED;
         } else if (!var1.isPlain() && !var1.isJson() || !var1.getImplicit().canOmitTagInPlainScalar() || this.simpleKeyContext && (this.analysis.isEmpty() || this.analysis.isMultiline()) || (this.flowLevel == 0 || !this.analysis.isAllowFlowPlain()) && (this.flowLevel != 0 || !this.analysis.isAllowBlockPlain())) {
            if ((var1.isLiteral() || var1.isFolded()) && this.flowLevel == 0 && !this.simpleKeyContext && this.analysis.isAllowBlock()) {
               return var1.getScalarStyle();
            } else {
               return !var1.isPlain() && !var1.isSQuoted() || !this.analysis.isAllowSingleQuoted() || this.simpleKeyContext && this.analysis.isMultiline() ? ScalarStyle.DOUBLE_QUOTED : ScalarStyle.SINGLE_QUOTED;
            }
         } else {
            return ScalarStyle.PLAIN;
         }
      } else {
         return ScalarStyle.DOUBLE_QUOTED;
      }
   }

   private void processScalar() {
      ScalarEvent var1 = (ScalarEvent)this.event;
      if (this.analysis == null) {
         this.analysis = this.analyzeScalar(var1.getValue());
      }

      boolean var2 = !this.simpleKeyContext && this.splitLines;
      switch(this.scalarStyle) {
      case PLAIN:
         this.writePlain(this.analysis.getScalar(), var2);
         break;
      case DOUBLE_QUOTED:
         this.writeDoubleQuoted(this.analysis.getScalar(), var2);
         break;
      case SINGLE_QUOTED:
         this.writeSingleQuoted(this.analysis.getScalar(), var2);
         break;
      case FOLDED:
         this.writeFolded(this.analysis.getScalar(), var2);
         break;
      case LITERAL:
         this.writeLiteral(this.analysis.getScalar());
         break;
      default:
         throw new YamlEngineException("Unexpected scalarStyle: " + this.scalarStyle);
      }

      this.analysis = null;
      this.scalarStyle = null;
   }

   private String prepareVersion(SpecVersion var1) {
      if (var1.getMajor() != 1) {
         throw new EmitterException("unsupported YAML version: " + var1);
      } else {
         return var1.getRepresentation();
      }
   }

   private String prepareTagHandle(String var1) {
      if (var1.isEmpty()) {
         throw new EmitterException("tag handle must not be empty");
      } else if (var1.charAt(0) == '!' && var1.charAt(var1.length() - 1) == '!') {
         if (!"!".equals(var1) && !HANDLE_FORMAT.matcher(var1).matches()) {
            throw new EmitterException("invalid character in the tag handle: " + var1);
         } else {
            return var1;
         }
      } else {
         throw new EmitterException("tag handle must start and end with '!': " + var1);
      }
   }

   private String prepareTagPrefix(String var1) {
      if (var1.isEmpty()) {
         throw new EmitterException("tag prefix must not be empty");
      } else {
         StringBuilder var2 = new StringBuilder();
         int var3 = 0;
         if (var1.charAt(0) == '!') {
            var3 = 1;
         }

         while(var3 < var1.length()) {
            ++var3;
         }

         var2.append(var1, 0, var3);
         return var2.toString();
      }
   }

   private String prepareTag(String var1) {
      if (var1.isEmpty()) {
         throw new EmitterException("tag must not be empty");
      } else if ("!".equals(var1)) {
         return var1;
      } else {
         String var2 = null;
         String var3 = var1;
         Iterator var4 = this.tagPrefixes.keySet().iterator();

         while(true) {
            String var5;
            do {
               do {
                  if (!var4.hasNext()) {
                     if (var2 != null) {
                        var3 = var1.substring(var2.length());
                        var2 = (String)this.tagPrefixes.get(var2);
                     }

                     if (var2 != null) {
                        return var2 + var3;
                     }

                     return "!<" + var3 + ">";
                  }

                  var5 = (String)var4.next();
               } while(!var1.startsWith(var5));
            } while(!"!".equals(var5) && var5.length() >= var1.length());

            var2 = var5;
         }
      }
   }

   private ScalarAnalysis analyzeScalar(String var1) {
      if (var1.isEmpty()) {
         return new ScalarAnalysis(var1, true, false, false, true, true, false);
      } else {
         boolean var2 = false;
         boolean var3 = false;
         boolean var4 = false;
         boolean var5 = false;
         boolean var6 = false;
         boolean var7 = false;
         boolean var8 = false;
         boolean var9 = false;
         boolean var10 = false;
         boolean var11 = false;
         if (var1.startsWith("---") || var1.startsWith("...")) {
            var2 = true;
            var3 = true;
         }

         boolean var12 = true;
         boolean var13 = var1.length() == 1 || CharConstants.NULL_BL_T_LINEBR.has(var1.codePointAt(1));
         boolean var14 = false;
         boolean var15 = false;
         int var16 = 0;

         while(true) {
            boolean var18;
            int var19;
            do {
               do {
                  if (var16 >= var1.length()) {
                     boolean var21 = true;
                     var18 = true;
                     boolean var22 = true;
                     boolean var20 = true;
                     if (var6 || var7 || var8 || var9) {
                        var18 = false;
                        var21 = false;
                     }

                     if (var8) {
                        var20 = false;
                     }

                     if (var10) {
                        var22 = false;
                        var18 = false;
                        var21 = false;
                     }

                     if (var11 || var5) {
                        var20 = false;
                        var22 = false;
                        var18 = false;
                        var21 = false;
                     }

                     if (var4) {
                        var21 = false;
                     }

                     if (var3) {
                        var21 = false;
                     }

                     if (var2) {
                        var18 = false;
                     }

                     return new ScalarAnalysis(var1, false, var4, var21, var18, var22, var20);
                  }

                  int var17 = var1.codePointAt(var16);
                  if (var16 == 0) {
                     if ("#,[]{}&*!|>'\"%@`".indexOf(var17) != -1) {
                        var3 = true;
                        var2 = true;
                     }

                     if (var17 == 63 || var17 == 58) {
                        var3 = true;
                        if (var13) {
                           var2 = true;
                        }
                     }

                     if (var17 == 45 && var13) {
                        var3 = true;
                        var2 = true;
                     }
                  } else {
                     if (",?[]{}".indexOf(var17) != -1) {
                        var3 = true;
                     }

                     if (var17 == 58) {
                        var3 = true;
                        if (var13) {
                           var2 = true;
                        }
                     }

                     if (var17 == 35 && var12) {
                        var3 = true;
                        var2 = true;
                     }
                  }

                  var18 = CharConstants.LINEBR.has(var17);
                  if (var18) {
                     var4 = true;
                  }

                  if (var17 != 10 && (32 > var17 || var17 > 126)) {
                     if (var17 != 133 && (var17 < 160 || var17 > 55295) && (var17 < 57344 || var17 > 65533) && (var17 < 65536 || var17 > 1114111)) {
                        var5 = true;
                     } else if (!this.allowUnicode) {
                        var5 = true;
                     }
                  }

                  if (var17 == 32) {
                     if (var16 == 0) {
                        var6 = true;
                     }

                     if (var16 == var1.length() - 1) {
                        var8 = true;
                     }

                     if (var15) {
                        var10 = true;
                     }

                     var14 = true;
                     var15 = false;
                  } else if (var18) {
                     if (var16 == 0) {
                        var7 = true;
                     }

                     if (var16 == var1.length() - 1) {
                        var9 = true;
                     }

                     if (var14) {
                        var11 = true;
                     }

                     var14 = false;
                     var15 = true;
                  } else {
                     var14 = false;
                     var15 = false;
                  }

                  var16 += Character.charCount(var17);
                  var12 = CharConstants.NULL_BL_T.has(var17) || var18;
                  var13 = true;
               } while(var16 + 1 >= var1.length());

               var19 = var16 + Character.charCount(var1.codePointAt(var16));
            } while(var19 >= var1.length());

            var13 = CharConstants.NULL_BL_T.has(var1.codePointAt(var19)) || var18;
         }
      }
   }

   void flushStream() {
      this.stream.flush();
   }

   void writeStreamStart() {
   }

   void writeStreamEnd() {
      this.flushStream();
   }

   void writeIndicator(String var1, boolean var2, boolean var3, boolean var4) {
      if (!this.whitespace && var2) {
         ++this.column;
         this.stream.write(" ");
      }

      this.whitespace = var3;
      this.indention = this.indention && var4;
      this.column += var1.length();
      this.openEnded = false;
      this.stream.write(var1);
   }

   int writeIndent() {
      int var1;
      if (this.indent != null) {
         var1 = this.indent;
      } else {
         var1 = 0;
      }

      if (!this.indention || this.column > var1 || this.column == var1 && !this.whitespace) {
         this.writeLineBreak((String)null);
      }

      int var2 = var1 - this.column;
      this.writeWhitespace(var2);
      return var2;
   }

   private void writeWhitespace(int var1) {
      if (var1 > 0) {
         this.whitespace = true;

         for(int var2 = 0; var2 < var1; ++var2) {
            this.stream.write(" ");
         }

         this.column += var1;
      }
   }

   private void writeLineBreak(String var1) {
      this.whitespace = true;
      this.indention = true;
      this.column = 0;
      if (var1 == null) {
         this.stream.write(this.bestLineBreak);
      } else {
         this.stream.write(var1);
      }

   }

   void writeVersionDirective(String var1) {
      this.stream.write("%YAML ");
      this.stream.write(var1);
      this.writeLineBreak((String)null);
   }

   void writeTagDirective(String var1, String var2) {
      this.stream.write("%TAG ");
      this.stream.write(var1);
      this.stream.write(" ");
      this.stream.write(var2);
      this.writeLineBreak((String)null);
   }

   private void writeSingleQuoted(String var1, boolean var2) {
      this.writeIndicator("'", true, false, false);
      boolean var3 = false;
      boolean var4 = false;
      int var5 = 0;

      for(int var6 = 0; var6 <= var1.length(); ++var6) {
         char var7 = 0;
         if (var6 < var1.length()) {
            var7 = var1.charAt(var6);
         }

         int var13;
         if (var3) {
            if (var7 != ' ') {
               if (var5 + 1 == var6 && this.column > this.bestWidth && var2 && var5 != 0 && var6 != var1.length()) {
                  this.writeIndent();
               } else {
                  var13 = var6 - var5;
                  this.column += var13;
                  this.stream.write(var1, var5, var13);
               }

               var5 = var6;
            }
         } else if (!var4) {
            if (CharConstants.LINEBR.has(var7, "\u0000 '") && var5 < var6) {
               var13 = var6 - var5;
               this.column += var13;
               this.stream.write(var1, var5, var13);
               var5 = var6;
            }
         } else if (var7 == 0 || CharConstants.LINEBR.hasNo(var7)) {
            if (var1.charAt(var5) == '\n') {
               this.writeLineBreak((String)null);
            }

            String var8 = var1.substring(var5, var6);
            char[] var9 = var8.toCharArray();
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               char var12 = var9[var11];
               if (var12 == '\n') {
                  this.writeLineBreak((String)null);
               } else {
                  this.writeLineBreak(String.valueOf(var12));
               }
            }

            this.writeIndent();
            var5 = var6;
         }

         if (var7 == '\'') {
            this.column += 2;
            this.stream.write("''");
            var5 = var6 + 1;
         }

         if (var7 != 0) {
            var3 = var7 == ' ';
            var4 = CharConstants.LINEBR.has(var7);
         }
      }

      this.writeIndicator("'", false, false, false);
   }

   private void writeDoubleQuoted(String var1, boolean var2) {
      this.writeIndicator("\"", true, false, false);
      int var3 = 0;

      for(int var4 = 0; var4 <= var1.length(); ++var4) {
         Character var5 = null;
         if (var4 < var1.length()) {
            var5 = var1.charAt(var4);
         }

         String var9;
         if (var5 == null || "\"\\\u0085\u2028\u2029\ufeff".indexOf(var5) != -1 || ' ' > var5 || var5 > '~') {
            if (var3 < var4) {
               int var6 = var4 - var3;
               this.column += var6;
               this.stream.write(var1, var3, var6);
               var3 = var4;
            }

            if (var5 != null) {
               if (ESCAPE_REPLACEMENTS.containsKey(var5)) {
                  var9 = "\\" + (String)ESCAPE_REPLACEMENTS.get(var5);
               } else {
                  int var7;
                  if (Character.isHighSurrogate(var5) && var4 + 1 < var1.length()) {
                     char var8 = var1.charAt(var4 + 1);
                     var7 = Character.toCodePoint(var5, var8);
                  } else {
                     var7 = var5;
                  }

                  if (this.allowUnicode && StreamReader.isPrintable(var7)) {
                     var9 = String.valueOf(Character.toChars(var7));
                     if (Character.charCount(var7) == 2) {
                        ++var4;
                     }
                  } else {
                     String var10;
                     if (var5 <= 255) {
                        var10 = "0" + Integer.toString(var5, 16);
                        var9 = "\\x" + var10.substring(var10.length() - 2);
                     } else if (Character.charCount(var7) == 2) {
                        ++var4;
                        var10 = "000" + Long.toHexString((long)var7);
                        var9 = "\\U" + var10.substring(var10.length() - 8);
                     } else {
                        var10 = "000" + Integer.toString(var5, 16);
                        var9 = "\\u" + var10.substring(var10.length() - 4);
                     }
                  }
               }

               this.column += var9.length();
               this.stream.write(var9);
               var3 = var4 + 1;
            }
         }

         if (0 < var4 && var4 < var1.length() - 1 && (var5 == ' ' || var3 >= var4) && this.column + (var4 - var3) > this.bestWidth && var2) {
            if (var3 >= var4) {
               var9 = "\\";
            } else {
               var9 = var1.substring(var3, var4) + "\\";
            }

            if (var3 < var4) {
               var3 = var4;
            }

            this.column += var9.length();
            this.stream.write(var9);
            this.writeIndent();
            this.whitespace = false;
            this.indention = false;
            if (var1.charAt(var3) == ' ') {
               var9 = "\\";
               this.column += var9.length();
               this.stream.write(var9);
            }
         }
      }

      this.writeIndicator("\"", false, false, false);
   }

   private boolean writeCommentLines(List<CommentLine> var1) {
      boolean var2 = false;
      if (this.emitComments) {
         int var3 = 0;
         int var4 = 0;
         boolean var5 = true;

         for(Iterator var6 = var1.iterator(); var6.hasNext(); var2 = true) {
            CommentLine var7 = (CommentLine)var6.next();
            if (var7.getCommentType() != CommentType.BLANK_LINE) {
               if (var5) {
                  var5 = false;
                  this.writeIndicator("#", var7.getCommentType() == CommentType.IN_LINE, false, false);
                  var3 = this.column > 0 ? this.column - 1 : 0;
               } else {
                  this.writeWhitespace(var3 - var4);
                  this.writeIndicator("#", false, false, false);
               }

               this.stream.write(var7.getValue());
               this.writeLineBreak((String)null);
               var4 = 0;
            } else {
               this.writeLineBreak((String)null);
               var4 = this.writeIndent();
            }
         }
      }

      return var2;
   }

   private void writeBlockComment() {
      if (!this.blockCommentsCollector.isEmpty()) {
         this.writeIndent();
         this.writeCommentLines(this.blockCommentsCollector.consume());
      }

   }

   private boolean writeInlineComments() {
      return this.writeCommentLines(this.inlineCommentsCollector.consume());
   }

   private String determineBlockHints(String var1) {
      StringBuilder var2 = new StringBuilder();
      if (CharConstants.LINEBR.has(var1.charAt(0), " ")) {
         var2.append(this.bestIndent);
      }

      char var3 = var1.charAt(var1.length() - 1);
      if (CharConstants.LINEBR.hasNo(var3)) {
         var2.append("-");
      } else if (var1.length() == 1 || CharConstants.LINEBR.has(var1.charAt(var1.length() - 2))) {
         var2.append("+");
      }

      return var2.toString();
   }

   void writeFolded(String var1, boolean var2) {
      String var3 = this.determineBlockHints(var1);
      this.writeIndicator(">" + var3, true, false, false);
      if (var3.length() > 0 && var3.charAt(var3.length() - 1) == '+') {
         this.openEnded = true;
      }

      if (!this.writeInlineComments()) {
         this.writeLineBreak((String)null);
      }

      boolean var4 = true;
      boolean var5 = false;
      boolean var6 = true;
      int var7 = 0;

      for(int var8 = 0; var8 <= var1.length(); ++var8) {
         char var9 = 0;
         if (var8 < var1.length()) {
            var9 = var1.charAt(var8);
         }

         if (var6) {
            if (var9 == 0 || CharConstants.LINEBR.hasNo(var9)) {
               if (!var4 && var9 != 0 && var9 != ' ' && var1.charAt(var7) == '\n') {
                  this.writeLineBreak((String)null);
               }

               var4 = var9 == ' ';
               String var15 = var1.substring(var7, var8);
               char[] var11 = var15.toCharArray();
               int var12 = var11.length;

               for(int var13 = 0; var13 < var12; ++var13) {
                  char var14 = var11[var13];
                  if (var14 == '\n') {
                     this.writeLineBreak((String)null);
                  } else {
                     this.writeLineBreak(String.valueOf(var14));
                  }
               }

               if (var9 != 0) {
                  this.writeIndent();
               }

               var7 = var8;
            }
         } else {
            int var10;
            if (var5) {
               if (var9 != ' ') {
                  if (var7 + 1 == var8 && this.column > this.bestWidth && var2) {
                     this.writeIndent();
                  } else {
                     var10 = var8 - var7;
                     this.column += var10;
                     this.stream.write(var1, var7, var10);
                  }

                  var7 = var8;
               }
            } else if (CharConstants.LINEBR.has(var9, "\u0000 ")) {
               var10 = var8 - var7;
               this.column += var10;
               this.stream.write(var1, var7, var10);
               if (var9 == 0) {
                  this.writeLineBreak((String)null);
               }

               var7 = var8;
            }
         }

         if (var9 != 0) {
            var6 = CharConstants.LINEBR.has(var9);
            var5 = var9 == ' ';
         }
      }

   }

   void writeLiteral(String var1) {
      String var2 = this.determineBlockHints(var1);
      this.writeIndicator("|" + var2, true, false, false);
      if (var2.length() > 0 && var2.charAt(var2.length() - 1) == '+') {
         this.openEnded = true;
      }

      if (!this.writeInlineComments()) {
         this.writeLineBreak((String)null);
      }

      boolean var3 = true;
      int var4 = 0;

      for(int var5 = 0; var5 <= var1.length(); ++var5) {
         char var6 = 0;
         if (var5 < var1.length()) {
            var6 = var1.charAt(var5);
         }

         if (!var3) {
            if (var6 == 0 || CharConstants.LINEBR.has(var6)) {
               this.stream.write(var1, var4, var5 - var4);
               if (var6 == 0) {
                  this.writeLineBreak((String)null);
               }

               var4 = var5;
            }
         } else if (var6 == 0 || CharConstants.LINEBR.hasNo(var6)) {
            String var7 = var1.substring(var4, var5);
            char[] var8 = var7.toCharArray();
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               char var11 = var8[var10];
               if (var11 == '\n') {
                  this.writeLineBreak((String)null);
               } else {
                  this.writeLineBreak(String.valueOf(var11));
               }
            }

            if (var6 != 0) {
               this.writeIndent();
            }

            var4 = var5;
         }

         if (var6 != 0) {
            var3 = CharConstants.LINEBR.has(var6);
         }
      }

   }

   void writePlain(String var1, boolean var2) {
      if (this.rootContext) {
         this.openEnded = true;
      }

      if (!var1.isEmpty()) {
         if (!this.whitespace) {
            ++this.column;
            this.stream.write(" ");
         }

         this.whitespace = false;
         this.indention = false;
         boolean var3 = false;
         boolean var4 = false;
         int var5 = 0;

         for(int var6 = 0; var6 <= var1.length(); ++var6) {
            char var7 = 0;
            if (var6 < var1.length()) {
               var7 = var1.charAt(var6);
            }

            int var13;
            if (var3) {
               if (var7 != ' ') {
                  if (var5 + 1 == var6 && this.column > this.bestWidth && var2) {
                     this.writeIndent();
                     this.whitespace = false;
                     this.indention = false;
                  } else {
                     var13 = var6 - var5;
                     this.column += var13;
                     this.stream.write(var1, var5, var13);
                  }

                  var5 = var6;
               }
            } else if (!var4) {
               if (CharConstants.LINEBR.has(var7, "\u0000 ")) {
                  var13 = var6 - var5;
                  this.column += var13;
                  this.stream.write(var1, var5, var13);
                  var5 = var6;
               }
            } else if (CharConstants.LINEBR.hasNo(var7)) {
               if (var1.charAt(var5) == '\n') {
                  this.writeLineBreak((String)null);
               }

               String var8 = var1.substring(var5, var6);
               char[] var9 = var8.toCharArray();
               int var10 = var9.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  char var12 = var9[var11];
                  if (var12 == '\n') {
                     this.writeLineBreak((String)null);
                  } else {
                     this.writeLineBreak(String.valueOf(var12));
                  }
               }

               this.writeIndent();
               this.whitespace = false;
               this.indention = false;
               var5 = var6;
            }

            if (var7 != 0) {
               var3 = var7 == ' ';
               var4 = CharConstants.LINEBR.has(var7);
            }
         }

      }
   }

   static {
      ESCAPE_REPLACEMENTS.put('\u0000', "0");
      ESCAPE_REPLACEMENTS.put('\u0007', "a");
      ESCAPE_REPLACEMENTS.put('\b', "b");
      ESCAPE_REPLACEMENTS.put('\t', "t");
      ESCAPE_REPLACEMENTS.put('\n', "n");
      ESCAPE_REPLACEMENTS.put('\u000b', "v");
      ESCAPE_REPLACEMENTS.put('\f', "f");
      ESCAPE_REPLACEMENTS.put('\r', "r");
      ESCAPE_REPLACEMENTS.put('\u001b', "e");
      ESCAPE_REPLACEMENTS.put('"', "\"");
      ESCAPE_REPLACEMENTS.put('\\', "\\");
      ESCAPE_REPLACEMENTS.put('\u0085', "N");
      ESCAPE_REPLACEMENTS.put(' ', "_");
      DEFAULT_TAG_PREFIXES = new LinkedHashMap();
      DEFAULT_TAG_PREFIXES.put("!", "!");
      DEFAULT_TAG_PREFIXES.put("tag:yaml.org,2002:", "!!");
      HANDLE_FORMAT = Pattern.compile("^![-_\\w]*!$");
   }

   private class ExpectStreamStart implements EmitterState {
      private ExpectStreamStart() {
      }

      public void expect() {
         if (Emitter.this.event.getEventId() == Event.ID.StreamStart) {
            Emitter.this.writeStreamStart();
            Emitter.this.state = Emitter.this.new ExpectFirstDocumentStart();
         } else {
            throw new EmitterException("expected StreamStartEvent, but got " + Emitter.this.event);
         }
      }

      // $FF: synthetic method
      ExpectStreamStart(Object var2) {
         this();
      }
   }

   private class ExpectFirstFlowSequenceItem implements EmitterState {
      private ExpectFirstFlowSequenceItem() {
      }

      public void expect() {
         if (Emitter.this.event.getEventId() == Event.ID.SequenceEnd) {
            Emitter.this.indent = (Integer)Emitter.this.indents.pop();
            Emitter.this.flowLevel--;
            Emitter.this.writeIndicator("]", false, false, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
            Emitter.this.state = (EmitterState)Emitter.this.states.pop();
         } else if (Emitter.this.event instanceof CommentEvent) {
            Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
            Emitter.this.writeBlockComment();
         } else {
            if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.multiLineFlow) {
               Emitter.this.writeIndent();
            }

            Emitter.this.states.push(Emitter.this.new ExpectFlowSequenceItem());
            Emitter.this.expectNode(false, false, false);
            Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
            Emitter.this.writeInlineComments();
         }

      }

      // $FF: synthetic method
      ExpectFirstFlowSequenceItem(Object var2) {
         this();
      }
   }

   private class ExpectFirstFlowMappingKey implements EmitterState {
      private ExpectFirstFlowMappingKey() {
      }

      public void expect() {
         Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         Emitter.this.writeBlockComment();
         if (Emitter.this.event.getEventId() == Event.ID.MappingEnd) {
            Emitter.this.indent = (Integer)Emitter.this.indents.pop();
            Emitter.this.flowLevel--;
            Emitter.this.writeIndicator("}", false, false, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
            Emitter.this.state = (EmitterState)Emitter.this.states.pop();
         } else {
            if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.multiLineFlow) {
               Emitter.this.writeIndent();
            }

            if (!Emitter.this.canonical && Emitter.this.checkSimpleKey()) {
               Emitter.this.states.push(Emitter.this.new ExpectFlowMappingSimpleValue());
               Emitter.this.expectNode(false, true, true);
            } else {
               Emitter.this.writeIndicator("?", true, false, false);
               Emitter.this.states.push(Emitter.this.new ExpectFlowMappingValue());
               Emitter.this.expectNode(false, true, false);
            }
         }

      }

      // $FF: synthetic method
      ExpectFirstFlowMappingKey(Object var2) {
         this();
      }
   }

   private class ExpectFirstBlockSequenceItem implements EmitterState {
      private ExpectFirstBlockSequenceItem() {
      }

      public void expect() {
         (Emitter.this.new ExpectBlockSequenceItem(true)).expect();
      }

      // $FF: synthetic method
      ExpectFirstBlockSequenceItem(Object var2) {
         this();
      }
   }

   private class ExpectFirstBlockMappingKey implements EmitterState {
      private ExpectFirstBlockMappingKey() {
      }

      public void expect() {
         (Emitter.this.new ExpectBlockMappingKey(true)).expect();
      }

      // $FF: synthetic method
      ExpectFirstBlockMappingKey(Object var2) {
         this();
      }
   }

   private class ExpectBlockMappingValue implements EmitterState {
      private ExpectBlockMappingValue() {
      }

      public void expect() {
         Emitter.this.writeIndent();
         Emitter.this.writeIndicator(":", true, false, true);
         Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         Emitter.this.writeInlineComments();
         Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         Emitter.this.writeBlockComment();
         Emitter.this.states.push(Emitter.this.new ExpectBlockMappingKey(false));
         Emitter.this.expectNode(false, true, false);
         Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
         Emitter.this.writeInlineComments();
      }

      // $FF: synthetic method
      ExpectBlockMappingValue(Object var2) {
         this();
      }
   }

   private class ExpectBlockMappingSimpleValue implements EmitterState {
      private ExpectBlockMappingSimpleValue() {
      }

      public void expect() {
         Emitter.this.writeIndicator(":", false, false, false);
         Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         if (!Emitter.this.isFoldedOrLiteral(Emitter.this.event) && Emitter.this.writeInlineComments()) {
            Emitter.this.increaseIndent(true, false);
            Emitter.this.writeIndent();
            Emitter.this.indent = (Integer)Emitter.this.indents.pop();
         }

         Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         if (!Emitter.this.blockCommentsCollector.isEmpty()) {
            Emitter.this.increaseIndent(true, false);
            Emitter.this.writeBlockComment();
            Emitter.this.writeIndent();
            Emitter.this.indent = (Integer)Emitter.this.indents.pop();
         }

         Emitter.this.states.push(Emitter.this.new ExpectBlockMappingKey(false));
         Emitter.this.expectNode(false, true, false);
         Emitter.this.inlineCommentsCollector.collectEvents();
         Emitter.this.writeInlineComments();
      }

      // $FF: synthetic method
      ExpectBlockMappingSimpleValue(Object var2) {
         this();
      }
   }

   private class ExpectBlockMappingKey implements EmitterState {
      private final boolean first;

      public ExpectBlockMappingKey(boolean var2) {
         this.first = var2;
      }

      public void expect() {
         Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         Emitter.this.writeBlockComment();
         if (!this.first && Emitter.this.event.getEventId() == Event.ID.MappingEnd) {
            Emitter.this.indent = (Integer)Emitter.this.indents.pop();
            Emitter.this.state = (EmitterState)Emitter.this.states.pop();
         } else {
            Emitter.this.writeIndent();
            if (Emitter.this.checkSimpleKey()) {
               Emitter.this.states.push(Emitter.this.new ExpectBlockMappingSimpleValue());
               Emitter.this.expectNode(false, true, true);
            } else {
               Emitter.this.writeIndicator("?", true, false, true);
               Emitter.this.states.push(Emitter.this.new ExpectBlockMappingValue());
               Emitter.this.expectNode(false, true, false);
            }
         }

      }
   }

   private class ExpectBlockSequenceItem implements EmitterState {
      private final boolean first;

      public ExpectBlockSequenceItem(boolean var2) {
         this.first = var2;
      }

      public void expect() {
         if (!this.first && Emitter.this.event.getEventId() == Event.ID.SequenceEnd) {
            Emitter.this.indent = (Integer)Emitter.this.indents.pop();
            Emitter.this.state = (EmitterState)Emitter.this.states.pop();
         } else if (Emitter.this.event instanceof CommentEvent) {
            Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
         } else {
            Emitter.this.writeIndent();
            if (!Emitter.this.indentWithIndicator || this.first) {
               Emitter.this.writeWhitespace(Emitter.this.indicatorIndent);
            }

            Emitter.this.writeIndicator("-", true, false, true);
            if (Emitter.this.indentWithIndicator && this.first) {
               Emitter.this.indent = Emitter.this.indent + Emitter.this.indicatorIndent;
            }

            if (!Emitter.this.blockCommentsCollector.isEmpty()) {
               Emitter.this.increaseIndent(false, false);
               Emitter.this.writeBlockComment();
               if (Emitter.this.event instanceof ScalarEvent) {
                  Emitter.this.analysis = Emitter.this.analyzeScalar(((ScalarEvent)Emitter.this.event).getValue());
                  if (!Emitter.this.analysis.isEmpty()) {
                     Emitter.this.writeIndent();
                  }
               }

               Emitter.this.indent = (Integer)Emitter.this.indents.pop();
            }

            Emitter.this.states.push(Emitter.this.new ExpectBlockSequenceItem(false));
            Emitter.this.expectNode(false, false, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
         }

      }
   }

   private class ExpectFlowMappingValue implements EmitterState {
      private ExpectFlowMappingValue() {
      }

      public void expect() {
         if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth || Emitter.this.multiLineFlow) {
            Emitter.this.writeIndent();
         }

         Emitter.this.writeIndicator(":", true, false, false);
         Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         Emitter.this.writeInlineComments();
         Emitter.this.states.push(Emitter.this.new ExpectFlowMappingKey());
         Emitter.this.expectNode(false, true, false);
         Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
         Emitter.this.writeInlineComments();
      }

      // $FF: synthetic method
      ExpectFlowMappingValue(Object var2) {
         this();
      }
   }

   private class ExpectFlowMappingSimpleValue implements EmitterState {
      private ExpectFlowMappingSimpleValue() {
      }

      public void expect() {
         Emitter.this.writeIndicator(":", false, false, false);
         Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         Emitter.this.writeInlineComments();
         Emitter.this.states.push(Emitter.this.new ExpectFlowMappingKey());
         Emitter.this.expectNode(false, true, false);
         Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
         Emitter.this.writeInlineComments();
      }

      // $FF: synthetic method
      ExpectFlowMappingSimpleValue(Object var2) {
         this();
      }
   }

   private class ExpectFlowMappingKey implements EmitterState {
      private ExpectFlowMappingKey() {
      }

      public void expect() {
         if (Emitter.this.event.getEventId() == Event.ID.MappingEnd) {
            Emitter.this.indent = (Integer)Emitter.this.indents.pop();
            Emitter.this.flowLevel--;
            if (Emitter.this.canonical) {
               Emitter.this.writeIndicator(",", false, false, false);
               Emitter.this.writeIndent();
            }

            if (Emitter.this.multiLineFlow) {
               Emitter.this.writeIndent();
            }

            Emitter.this.writeIndicator("}", false, false, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
            Emitter.this.state = (EmitterState)Emitter.this.states.pop();
         } else {
            Emitter.this.writeIndicator(",", false, false, false);
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeBlockComment();
            if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.multiLineFlow) {
               Emitter.this.writeIndent();
            }

            if (!Emitter.this.canonical && Emitter.this.checkSimpleKey()) {
               Emitter.this.states.push(Emitter.this.new ExpectFlowMappingSimpleValue());
               Emitter.this.expectNode(false, true, true);
            } else {
               Emitter.this.writeIndicator("?", true, false, false);
               Emitter.this.states.push(Emitter.this.new ExpectFlowMappingValue());
               Emitter.this.expectNode(false, true, false);
            }
         }

      }

      // $FF: synthetic method
      ExpectFlowMappingKey(Object var2) {
         this();
      }
   }

   private class ExpectFlowSequenceItem implements EmitterState {
      private ExpectFlowSequenceItem() {
      }

      public void expect() {
         if (Emitter.this.event.getEventId() == Event.ID.SequenceEnd) {
            Emitter.this.indent = (Integer)Emitter.this.indents.pop();
            Emitter.this.flowLevel--;
            if (Emitter.this.canonical) {
               Emitter.this.writeIndicator(",", false, false, false);
               Emitter.this.writeIndent();
            } else if (Emitter.this.multiLineFlow) {
               Emitter.this.writeIndent();
            }

            Emitter.this.writeIndicator("]", false, false, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
            if (Emitter.this.multiLineFlow) {
               Emitter.this.writeIndent();
            }

            Emitter.this.state = (EmitterState)Emitter.this.states.pop();
         } else if (Emitter.this.event instanceof CommentEvent) {
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
         } else {
            Emitter.this.writeIndicator(",", false, false, false);
            Emitter.this.writeBlockComment();
            if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.multiLineFlow) {
               Emitter.this.writeIndent();
            }

            Emitter.this.states.push(Emitter.this.new ExpectFlowSequenceItem());
            Emitter.this.expectNode(false, false, false);
            Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
            Emitter.this.writeInlineComments();
         }

      }

      // $FF: synthetic method
      ExpectFlowSequenceItem(Object var2) {
         this();
      }
   }

   private class ExpectDocumentRoot implements EmitterState {
      private ExpectDocumentRoot() {
      }

      public void expect() {
         Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         if (!Emitter.this.blockCommentsCollector.isEmpty()) {
            Emitter.this.writeBlockComment();
            if (Emitter.this.event instanceof DocumentEndEvent) {
               (Emitter.this.new ExpectDocumentEnd()).expect();
               return;
            }
         }

         Emitter.this.states.push(Emitter.this.new ExpectDocumentEnd());
         Emitter.this.expectNode(true, false, false);
      }

      // $FF: synthetic method
      ExpectDocumentRoot(Object var2) {
         this();
      }
   }

   private class ExpectDocumentEnd implements EmitterState {
      private ExpectDocumentEnd() {
      }

      public void expect() {
         Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         Emitter.this.writeBlockComment();
         if (Emitter.this.event.getEventId() == Event.ID.DocumentEnd) {
            Emitter.this.writeIndent();
            if (((DocumentEndEvent)Emitter.this.event).isExplicit()) {
               Emitter.this.writeIndicator("...", true, false, false);
               Emitter.this.writeIndent();
            }

            Emitter.this.flushStream();
            Emitter.this.state = Emitter.this.new ExpectDocumentStart(false);
         } else {
            throw new EmitterException("expected DocumentEndEvent, but got " + Emitter.this.event);
         }
      }

      // $FF: synthetic method
      ExpectDocumentEnd(Object var2) {
         this();
      }
   }

   private class ExpectDocumentStart implements EmitterState {
      private final boolean first;

      public ExpectDocumentStart(boolean var2) {
         this.first = var2;
      }

      public void expect() {
         if (Emitter.this.event.getEventId() == Event.ID.DocumentStart) {
            DocumentStartEvent var1 = (DocumentStartEvent)Emitter.this.event;
            this.handleDocumentStartEvent(var1);
            Emitter.this.state = Emitter.this.new ExpectDocumentRoot();
         } else if (Emitter.this.event.getEventId() == Event.ID.StreamEnd) {
            Emitter.this.writeStreamEnd();
            Emitter.this.state = Emitter.this.new ExpectNothing();
         } else {
            if (!(Emitter.this.event instanceof CommentEvent)) {
               throw new EmitterException("expected DocumentStartEvent, but got " + Emitter.this.event);
            }

            Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
            Emitter.this.writeBlockComment();
         }

      }

      private void handleDocumentStartEvent(DocumentStartEvent var1) {
         if ((var1.getSpecVersion().isPresent() || !var1.getTags().isEmpty()) && Emitter.this.openEnded) {
            Emitter.this.writeIndicator("...", true, false, false);
            Emitter.this.writeIndent();
         }

         var1.getSpecVersion().ifPresent((var1x) -> {
            Emitter.this.writeVersionDirective(Emitter.this.prepareVersion(var1x));
         });
         Emitter.this.tagPrefixes = new LinkedHashMap(Emitter.DEFAULT_TAG_PREFIXES);
         if (!var1.getTags().isEmpty()) {
            this.handleTagDirectives(var1.getTags());
         }

         boolean var2 = this.first && !var1.isExplicit() && !Emitter.this.canonical && !var1.getSpecVersion().isPresent() && var1.getTags().isEmpty() && !this.checkEmptyDocument();
         if (!var2) {
            Emitter.this.writeIndent();
            Emitter.this.writeIndicator("---", true, false, false);
            if (Emitter.this.canonical) {
               Emitter.this.writeIndent();
            }
         }

      }

      private void handleTagDirectives(Map<String, String> var1) {
         TreeSet var2 = new TreeSet(var1.keySet());
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            String var5 = (String)var1.get(var4);
            Emitter.this.tagPrefixes.put(var5, var4);
            String var6 = Emitter.this.prepareTagHandle(var4);
            String var7 = Emitter.this.prepareTagPrefix(var5);
            Emitter.this.writeTagDirective(var6, var7);
         }

      }

      private boolean checkEmptyDocument() {
         if (Emitter.this.event.getEventId() == Event.ID.DocumentStart && !Emitter.this.events.isEmpty()) {
            Event var1 = (Event)Emitter.this.events.peek();
            if (var1.getEventId() != Event.ID.Scalar) {
               return false;
            } else {
               ScalarEvent var2 = (ScalarEvent)var1;
               return !var2.getAnchor().isPresent() && !var2.getTag().isPresent() && var2.getImplicit() != null && var2.getValue().isEmpty();
            }
         } else {
            return false;
         }
      }
   }

   private class ExpectFirstDocumentStart implements EmitterState {
      private ExpectFirstDocumentStart() {
      }

      public void expect() {
         (Emitter.this.new ExpectDocumentStart(true)).expect();
      }

      // $FF: synthetic method
      ExpectFirstDocumentStart(Object var2) {
         this();
      }
   }

   private class ExpectNothing implements EmitterState {
      private ExpectNothing() {
      }

      public void expect() {
         throw new EmitterException("expecting nothing, but got " + Emitter.this.event);
      }

      // $FF: synthetic method
      ExpectNothing(Object var2) {
         this();
      }
   }
}

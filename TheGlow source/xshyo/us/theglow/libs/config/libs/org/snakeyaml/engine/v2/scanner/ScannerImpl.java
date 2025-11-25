package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.scanner;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentType;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.Anchor;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ArrayStack;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.CharConstants;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.UriEncoder;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.ScannerException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.AliasToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.AnchorToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.BlockEndToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.BlockEntryToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.BlockMappingStartToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.BlockSequenceStartToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.CommentToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.DirectiveToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.DocumentEndToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.DocumentStartToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.FlowEntryToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.FlowMappingEndToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.FlowMappingStartToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.FlowSequenceEndToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.FlowSequenceStartToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.KeyToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.ScalarToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.StreamEndToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.StreamStartToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.TagToken;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.TagTuple;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.Token;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens.ValueToken;

public final class ScannerImpl implements Scanner {
   private static final String DIRECTIVE_PREFIX = "while scanning a directive";
   private static final String EXPECTED_ALPHA_ERROR_PREFIX = "expected alphabetic or numeric character, but found ";
   private static final String SCANNING_SCALAR = "while scanning a block scalar";
   private static final String SCANNING_PREFIX = "while scanning a ";
   private static final Pattern NOT_HEXA = Pattern.compile("[^0-9A-Fa-f]");
   private final StreamReader reader;
   private final List<Token> tokens;
   private final ArrayStack<Integer> indents;
   private final Map<Integer, SimpleKey> possibleSimpleKeys;
   private final LoadSettings settings;
   private boolean done;
   private int flowLevel;
   private Token lastToken;
   private int tokensTaken;
   private int indent;
   private boolean allowSimpleKey;

   @Deprecated
   public ScannerImpl(StreamReader var1, LoadSettings var2) {
      this(var2, var1);
   }

   public ScannerImpl(LoadSettings var1, StreamReader var2) {
      this.done = false;
      this.flowLevel = 0;
      this.tokensTaken = 0;
      this.indent = -1;
      this.allowSimpleKey = true;
      this.reader = var2;
      this.settings = var1;
      this.tokens = new ArrayList(100);
      this.indents = new ArrayStack(10);
      this.possibleSimpleKeys = new LinkedHashMap();
      this.fetchStreamStart();
   }

   @Deprecated
   public ScannerImpl(StreamReader var1) {
      this(LoadSettings.builder().build(), var1);
   }

   public boolean checkToken(Token.ID... var1) {
      while(this.needMoreTokens()) {
         this.fetchMoreTokens();
      }

      if (!this.tokens.isEmpty()) {
         if (var1.length == 0) {
            return true;
         }

         Token var2 = (Token)this.tokens.get(0);
         Token.ID var3 = var2.getTokenId();

         for(int var4 = 0; var4 < var1.length; ++var4) {
            if (var3 == var1[var4]) {
               return true;
            }
         }
      }

      return false;
   }

   public Token peekToken() {
      while(this.needMoreTokens()) {
         this.fetchMoreTokens();
      }

      return (Token)this.tokens.get(0);
   }

   public boolean hasNext() {
      return this.checkToken();
   }

   public Token next() {
      ++this.tokensTaken;
      if (this.tokens.isEmpty()) {
         throw new NoSuchElementException("No more Tokens found.");
      } else {
         return (Token)this.tokens.remove(0);
      }
   }

   private void addToken(Token var1) {
      this.lastToken = var1;
      this.tokens.add(var1);
   }

   private void addToken(int var1, Token var2) {
      if (var1 == this.tokens.size()) {
         this.lastToken = var2;
      }

      this.tokens.add(var1, var2);
   }

   private void addAllTokens(List<Token> var1) {
      this.lastToken = (Token)var1.get(var1.size() - 1);
      this.tokens.addAll(var1);
   }

   private boolean isBlockContext() {
      return this.flowLevel == 0;
   }

   private boolean isFlowContext() {
      return !this.isBlockContext();
   }

   private boolean needMoreTokens() {
      if (this.done) {
         return false;
      } else if (this.tokens.isEmpty()) {
         return true;
      } else {
         this.stalePossibleSimpleKeys();
         return this.nextPossibleSimpleKey() == this.tokensTaken;
      }
   }

   private void fetchMoreTokens() {
      if (this.reader.getDocumentIndex() > this.settings.getCodePointLimit()) {
         throw new YamlEngineException("The incoming YAML document exceeds the limit: " + this.settings.getCodePointLimit() + " code points.");
      } else {
         this.scanToNextToken();
         this.stalePossibleSimpleKeys();
         this.unwindIndent(this.reader.getColumn());
         int var1 = this.reader.peek();
         switch(var1) {
         case 0:
            this.fetchStreamEnd();
            return;
         case 33:
            this.fetchTag();
            return;
         case 34:
            this.fetchDouble();
            return;
         case 37:
            if (this.checkDirective()) {
               this.fetchDirective();
               return;
            }
            break;
         case 38:
            this.fetchAnchor();
            return;
         case 39:
            this.fetchSingle();
            return;
         case 42:
            this.fetchAlias();
            return;
         case 44:
            this.fetchFlowEntry();
            return;
         case 45:
            if (this.checkDocumentStart()) {
               this.fetchDocumentStart();
               return;
            }

            if (this.checkBlockEntry()) {
               this.fetchBlockEntry();
               return;
            }
            break;
         case 46:
            if (this.checkDocumentEnd()) {
               this.fetchDocumentEnd();
               return;
            }
            break;
         case 58:
            if (this.checkValue()) {
               this.fetchValue();
               return;
            }
            break;
         case 62:
            if (this.isBlockContext()) {
               this.fetchFolded();
               return;
            }
            break;
         case 63:
            if (this.checkKey()) {
               this.fetchKey();
               return;
            }
            break;
         case 91:
            this.fetchFlowSequenceStart();
            return;
         case 93:
            this.fetchFlowSequenceEnd();
            return;
         case 123:
            this.fetchFlowMappingStart();
            return;
         case 124:
            if (this.isBlockContext()) {
               this.fetchLiteral();
               return;
            }
            break;
         case 125:
            this.fetchFlowMappingEnd();
            return;
         }

         if (this.checkPlain()) {
            this.fetchPlain();
         } else {
            String var2 = CharConstants.escapeChar(String.valueOf(Character.toChars(var1)));
            if (var1 == 9) {
               var2 = var2 + "(TAB)";
            }

            String var3 = String.format("found character '%s' that cannot start any token. (Do not use %s for indentation)", var2, var2);
            throw new ScannerException("while scanning for the next token", Optional.empty(), var3, this.reader.getMark());
         }
      }
   }

   private int nextPossibleSimpleKey() {
      return !this.possibleSimpleKeys.isEmpty() ? ((SimpleKey)this.possibleSimpleKeys.values().iterator().next()).getTokenNumber() : -1;
   }

   private void stalePossibleSimpleKeys() {
      if (!this.possibleSimpleKeys.isEmpty()) {
         Iterator var1 = this.possibleSimpleKeys.values().iterator();

         while(true) {
            SimpleKey var2;
            do {
               if (!var1.hasNext()) {
                  return;
               }

               var2 = (SimpleKey)var1.next();
            } while(var2.getLine() == this.reader.getLine() && this.reader.getIndex() - var2.getIndex() <= 1024);

            if (var2.isRequired()) {
               throw new ScannerException("while scanning a simple key", var2.getMark(), "could not find expected ':'", this.reader.getMark());
            }

            var1.remove();
         }
      }
   }

   private void savePossibleSimpleKey() {
      boolean var1 = this.isBlockContext() && this.indent == this.reader.getColumn();
      if (!this.allowSimpleKey && var1) {
         throw new YamlEngineException("A simple key is required only if it is the first token in the current line");
      } else {
         if (this.allowSimpleKey) {
            this.removePossibleSimpleKey();
            int var2 = this.tokensTaken + this.tokens.size();
            SimpleKey var3 = new SimpleKey(var2, var1, this.reader.getIndex(), this.reader.getLine(), this.reader.getColumn(), this.reader.getMark());
            this.possibleSimpleKeys.put(this.flowLevel, var3);
         }

      }
   }

   private void removePossibleSimpleKey() {
      SimpleKey var1 = (SimpleKey)this.possibleSimpleKeys.remove(this.flowLevel);
      if (var1 != null && var1.isRequired()) {
         throw new ScannerException("while scanning a simple key", var1.getMark(), "could not find expected ':'", this.reader.getMark());
      }
   }

   private void unwindIndent(int var1) {
      if (!this.isFlowContext()) {
         while(this.indent > var1) {
            Optional var2 = this.reader.getMark();
            this.indent = (Integer)this.indents.pop();
            this.addToken(new BlockEndToken(var2, var2));
         }

      }
   }

   private boolean addIndent(int var1) {
      if (this.indent < var1) {
         this.indents.push(this.indent);
         this.indent = var1;
         return true;
      } else {
         return false;
      }
   }

   private void fetchStreamStart() {
      Optional var1 = this.reader.getMark();
      StreamStartToken var2 = new StreamStartToken(var1, var1);
      this.addToken(var2);
   }

   private void fetchStreamEnd() {
      this.unwindIndent(-1);
      this.removePossibleSimpleKey();
      this.allowSimpleKey = false;
      this.possibleSimpleKeys.clear();
      Optional var1 = this.reader.getMark();
      StreamEndToken var2 = new StreamEndToken(var1, var1);
      this.addToken(var2);
      this.done = true;
   }

   private void fetchDirective() {
      this.unwindIndent(-1);
      this.removePossibleSimpleKey();
      this.allowSimpleKey = false;
      List var1 = this.scanDirective();
      this.addAllTokens(var1);
   }

   private void fetchDocumentStart() {
      this.fetchDocumentIndicator(true);
   }

   private void fetchDocumentEnd() {
      this.fetchDocumentIndicator(false);
   }

   private void fetchDocumentIndicator(boolean var1) {
      this.unwindIndent(-1);
      this.removePossibleSimpleKey();
      this.allowSimpleKey = false;
      Optional var2 = this.reader.getMark();
      this.reader.forward(3);
      Optional var3 = this.reader.getMark();
      Object var4;
      if (var1) {
         var4 = new DocumentStartToken(var2, var3);
      } else {
         var4 = new DocumentEndToken(var2, var3);
      }

      this.addToken((Token)var4);
   }

   private void fetchFlowSequenceStart() {
      this.fetchFlowCollectionStart(false);
   }

   private void fetchFlowMappingStart() {
      this.fetchFlowCollectionStart(true);
   }

   private void fetchFlowCollectionStart(boolean var1) {
      this.savePossibleSimpleKey();
      ++this.flowLevel;
      this.allowSimpleKey = true;
      Optional var2 = this.reader.getMark();
      this.reader.forward(1);
      Optional var3 = this.reader.getMark();
      Object var4;
      if (var1) {
         var4 = new FlowMappingStartToken(var2, var3);
      } else {
         var4 = new FlowSequenceStartToken(var2, var3);
      }

      this.addToken((Token)var4);
   }

   private void fetchFlowSequenceEnd() {
      this.fetchFlowCollectionEnd(false);
   }

   private void fetchFlowMappingEnd() {
      this.fetchFlowCollectionEnd(true);
   }

   private void fetchFlowCollectionEnd(boolean var1) {
      this.removePossibleSimpleKey();
      --this.flowLevel;
      this.allowSimpleKey = false;
      Optional var2 = this.reader.getMark();
      this.reader.forward();
      Optional var3 = this.reader.getMark();
      Object var4;
      if (var1) {
         var4 = new FlowMappingEndToken(var2, var3);
      } else {
         var4 = new FlowSequenceEndToken(var2, var3);
      }

      this.addToken((Token)var4);
   }

   private void fetchFlowEntry() {
      this.allowSimpleKey = true;
      this.removePossibleSimpleKey();
      Optional var1 = this.reader.getMark();
      this.reader.forward();
      Optional var2 = this.reader.getMark();
      FlowEntryToken var3 = new FlowEntryToken(var1, var2);
      this.addToken(var3);
   }

   private void fetchBlockEntry() {
      Optional var1;
      if (this.isBlockContext()) {
         if (!this.allowSimpleKey) {
            throw new ScannerException("", Optional.empty(), "sequence entries are not allowed here", this.reader.getMark());
         }

         if (this.addIndent(this.reader.getColumn())) {
            var1 = this.reader.getMark();
            this.addToken(new BlockSequenceStartToken(var1, var1));
         }
      }

      this.allowSimpleKey = true;
      this.removePossibleSimpleKey();
      var1 = this.reader.getMark();
      this.reader.forward();
      Optional var2 = this.reader.getMark();
      BlockEntryToken var3 = new BlockEntryToken(var1, var2);
      this.addToken(var3);
   }

   private void fetchKey() {
      Optional var1;
      if (this.isBlockContext()) {
         if (!this.allowSimpleKey) {
            throw new ScannerException("mapping keys are not allowed here", this.reader.getMark());
         }

         if (this.addIndent(this.reader.getColumn())) {
            var1 = this.reader.getMark();
            this.addToken(new BlockMappingStartToken(var1, var1));
         }
      }

      this.allowSimpleKey = this.isBlockContext();
      this.removePossibleSimpleKey();
      var1 = this.reader.getMark();
      this.reader.forward();
      Optional var2 = this.reader.getMark();
      KeyToken var3 = new KeyToken(var1, var2);
      this.addToken(var3);
   }

   private void fetchValue() {
      SimpleKey var1 = (SimpleKey)this.possibleSimpleKeys.remove(this.flowLevel);
      Optional var2;
      if (var1 != null) {
         this.addToken(var1.getTokenNumber() - this.tokensTaken, new KeyToken(var1.getMark(), var1.getMark()));
         if (this.isBlockContext() && this.addIndent(var1.getColumn())) {
            this.addToken(var1.getTokenNumber() - this.tokensTaken, new BlockMappingStartToken(var1.getMark(), var1.getMark()));
         }

         this.allowSimpleKey = false;
      } else {
         if (this.isBlockContext() && !this.allowSimpleKey) {
            throw new ScannerException("mapping values are not allowed here", this.reader.getMark());
         }

         if (this.isBlockContext() && this.addIndent(this.reader.getColumn())) {
            var2 = this.reader.getMark();
            this.addToken(new BlockMappingStartToken(var2, var2));
         }

         this.allowSimpleKey = this.isBlockContext();
         this.removePossibleSimpleKey();
      }

      var2 = this.reader.getMark();
      this.reader.forward();
      Optional var3 = this.reader.getMark();
      ValueToken var4 = new ValueToken(var2, var3);
      this.addToken(var4);
   }

   private void fetchAlias() {
      this.savePossibleSimpleKey();
      this.allowSimpleKey = false;
      Token var1 = this.scanAnchor(false);
      this.addToken(var1);
   }

   private void fetchAnchor() {
      this.savePossibleSimpleKey();
      this.allowSimpleKey = false;
      Token var1 = this.scanAnchor(true);
      this.addToken(var1);
   }

   private void fetchTag() {
      this.savePossibleSimpleKey();
      this.allowSimpleKey = false;
      Token var1 = this.scanTag();
      this.addToken(var1);
   }

   private void fetchLiteral() {
      this.fetchBlockScalar(ScalarStyle.LITERAL);
   }

   private void fetchFolded() {
      this.fetchBlockScalar(ScalarStyle.FOLDED);
   }

   private void fetchBlockScalar(ScalarStyle var1) {
      this.allowSimpleKey = true;
      this.removePossibleSimpleKey();
      List var2 = this.scanBlockScalar(var1);
      this.addAllTokens(var2);
   }

   private void fetchSingle() {
      this.fetchFlowScalar(ScalarStyle.SINGLE_QUOTED);
   }

   private void fetchDouble() {
      this.fetchFlowScalar(ScalarStyle.DOUBLE_QUOTED);
   }

   private void fetchFlowScalar(ScalarStyle var1) {
      this.savePossibleSimpleKey();
      this.allowSimpleKey = false;
      Token var2 = this.scanFlowScalar(var1);
      this.addToken(var2);
   }

   private void fetchPlain() {
      this.savePossibleSimpleKey();
      this.allowSimpleKey = false;
      Token var1 = this.scanPlain();
      this.addToken(var1);
   }

   private boolean checkDirective() {
      return this.reader.getColumn() == 0;
   }

   private boolean checkDocumentStart() {
      if (this.reader.getColumn() != 0) {
         return false;
      } else {
         return "---".equals(this.reader.prefix(3)) && CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(3));
      }
   }

   private boolean checkDocumentEnd() {
      if (this.reader.getColumn() != 0) {
         return false;
      } else {
         return "...".equals(this.reader.prefix(3)) && CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(3));
      }
   }

   private boolean checkBlockEntry() {
      return CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(1));
   }

   private boolean checkKey() {
      return CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(1));
   }

   private boolean checkValue() {
      return this.isFlowContext() ? true : CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(1));
   }

   private boolean checkPlain() {
      int var1 = this.reader.peek();
      boolean var2 = CharConstants.NULL_BL_T_LINEBR.hasNo(var1, "-?:,[]{}#&*!|>'\"%@`");
      if (var2) {
         return true;
      } else if (this.isBlockContext()) {
         return CharConstants.NULL_BL_T_LINEBR.hasNo(this.reader.peek(1)) && "-?:".indexOf(var1) != -1;
      } else {
         return CharConstants.NULL_BL_T_LINEBR.hasNo(this.reader.peek(1), ",]") && "-?".indexOf(var1) != -1;
      }
   }

   private void scanToNextToken() {
      if (this.reader.getIndex() == 0 && this.reader.peek() == 65279) {
         this.reader.forward();
      }

      boolean var1 = false;
      int var2 = -1;

      while(!var1) {
         Optional var3 = this.reader.getMark();
         int var4 = this.reader.getColumn();
         boolean var5 = false;

         int var6;
         for(var6 = 0; this.reader.peek(var6) == 32; ++var6) {
         }

         if (var6 > 0) {
            this.reader.forward(var6);
         }

         if (this.reader.peek() == 35) {
            var5 = true;
            CommentType var7;
            if (var4 != 0 && (this.lastToken == null || this.lastToken.getTokenId() != Token.ID.BlockEntry)) {
               var7 = CommentType.IN_LINE;
               var2 = this.reader.getColumn();
            } else if (var2 == this.reader.getColumn()) {
               var7 = CommentType.IN_LINE;
            } else {
               var2 = -1;
               var7 = CommentType.BLOCK;
            }

            CommentToken var8 = this.scanComment(var7);
            if (this.settings.getParseComments()) {
               this.addToken(var8);
            }
         }

         Optional var9 = this.scanLineBreak();
         if (var9.isPresent()) {
            if (this.settings.getParseComments() && !var5 && var4 == 0) {
               this.addToken(new CommentToken(CommentType.BLANK_LINE, (String)var9.get(), var3, this.reader.getMark()));
            }

            if (this.isBlockContext()) {
               this.allowSimpleKey = true;
            }
         } else {
            var1 = true;
         }
      }

   }

   private CommentToken scanComment(CommentType var1) {
      Optional var2 = this.reader.getMark();
      this.reader.forward();

      int var3;
      for(var3 = 0; CharConstants.NULL_OR_LINEBR.hasNo(this.reader.peek(var3)); ++var3) {
      }

      String var4 = this.reader.prefixForward(var3);
      Optional var5 = this.reader.getMark();
      return new CommentToken(var1, var4, var2, var5);
   }

   private List<Token> scanDirective() {
      Optional var1 = this.reader.getMark();
      this.reader.forward();
      String var3 = this.scanDirectiveName(var1);
      Optional var2;
      Optional var4;
      if ("YAML".equals(var3)) {
         var4 = Optional.of(this.scanYamlDirectiveValue(var1));
         var2 = this.reader.getMark();
      } else if ("TAG".equals(var3)) {
         var4 = Optional.of(this.scanTagDirectiveValue(var1));
         var2 = this.reader.getMark();
      } else {
         var2 = this.reader.getMark();

         int var5;
         for(var5 = 0; CharConstants.NULL_OR_LINEBR.hasNo(this.reader.peek(var5)); ++var5) {
         }

         if (var5 > 0) {
            this.reader.forward(var5);
         }

         var4 = Optional.empty();
      }

      CommentToken var7 = this.scanDirectiveIgnoredLine(var1);
      DirectiveToken var6 = new DirectiveToken(var3, var4, var1, var2);
      return this.makeTokenList(var6, var7);
   }

   private String scanDirectiveName(Optional<Mark> var1) {
      int var2 = 0;

      int var3;
      for(var3 = this.reader.peek(var2); CharConstants.ALPHA.has(var3); var3 = this.reader.peek(var2)) {
         ++var2;
      }

      String var4;
      if (var2 == 0) {
         var4 = String.valueOf(Character.toChars(var3));
         throw new ScannerException("while scanning a directive", var1, "expected alphabetic or numeric character, but found " + var4 + "(" + var3 + ")", this.reader.getMark());
      } else {
         var4 = this.reader.prefixForward(var2);
         var3 = this.reader.peek();
         if (CharConstants.NULL_BL_LINEBR.hasNo(var3)) {
            String var5 = String.valueOf(Character.toChars(var3));
            throw new ScannerException("while scanning a directive", var1, "expected alphabetic or numeric character, but found " + var5 + "(" + var3 + ")", this.reader.getMark());
         } else {
            return var4;
         }
      }
   }

   private List<Integer> scanYamlDirectiveValue(Optional<Mark> var1) {
      while(this.reader.peek() == 32) {
         this.reader.forward();
      }

      Integer var2 = this.scanYamlDirectiveNumber(var1);
      int var3 = this.reader.peek();
      if (var3 != 46) {
         String var6 = String.valueOf(Character.toChars(var3));
         throw new ScannerException("while scanning a directive", var1, "expected a digit or '.', but found " + var6 + "(" + var3 + ")", this.reader.getMark());
      } else {
         this.reader.forward();
         Integer var4 = this.scanYamlDirectiveNumber(var1);
         var3 = this.reader.peek();
         if (CharConstants.NULL_BL_LINEBR.hasNo(var3)) {
            String var7 = String.valueOf(Character.toChars(var3));
            throw new ScannerException("while scanning a directive", var1, "expected a digit or ' ', but found " + var7 + "(" + var3 + ")", this.reader.getMark());
         } else {
            ArrayList var5 = new ArrayList(2);
            var5.add(var2);
            var5.add(var4);
            return var5;
         }
      }
   }

   private Integer scanYamlDirectiveNumber(Optional<Mark> var1) {
      int var2 = this.reader.peek();
      if (!Character.isDigit(var2)) {
         String var6 = String.valueOf(Character.toChars(var2));
         throw new ScannerException("while scanning a directive", var1, "expected a digit, but found " + var6 + "(" + var2 + ")", this.reader.getMark());
      } else {
         int var3;
         for(var3 = 0; Character.isDigit(this.reader.peek(var3)); ++var3) {
         }

         String var4 = this.reader.prefixForward(var3);
         if (var3 > 3) {
            throw new ScannerException("while scanning a YAML directive", var1, "found a number which cannot represent a valid version: " + var4, this.reader.getMark());
         } else {
            Integer var5 = Integer.parseInt(var4);
            return var5;
         }
      }
   }

   private List<String> scanTagDirectiveValue(Optional<Mark> var1) {
      while(this.reader.peek() == 32) {
         this.reader.forward();
      }

      String var2 = this.scanTagDirectiveHandle(var1);

      while(this.reader.peek() == 32) {
         this.reader.forward();
      }

      String var3 = this.scanTagDirectivePrefix(var1);
      ArrayList var4 = new ArrayList(2);
      var4.add(var2);
      var4.add(var3);
      return var4;
   }

   private String scanTagDirectiveHandle(Optional<Mark> var1) {
      String var2 = this.scanTagHandle("directive", var1);
      int var3 = this.reader.peek();
      if (var3 != 32) {
         String var4 = String.valueOf(Character.toChars(var3));
         throw new ScannerException("while scanning a directive", var1, "expected ' ', but found " + var4 + "(" + var3 + ")", this.reader.getMark());
      } else {
         return var2;
      }
   }

   private String scanTagDirectivePrefix(Optional<Mark> var1) {
      String var2 = this.scanTagUri("directive", CharConstants.URI_CHARS_FOR_TAG_PREFIX, var1);
      int var3 = this.reader.peek();
      if (CharConstants.NULL_BL_LINEBR.hasNo(var3)) {
         String var4 = String.valueOf(Character.toChars(var3));
         throw new ScannerException("while scanning a directive", var1, "expected ' ', but found " + var4 + "(" + var3 + ")", this.reader.getMark());
      } else {
         return var2;
      }
   }

   private CommentToken scanDirectiveIgnoredLine(Optional<Mark> var1) {
      while(this.reader.peek() == 32) {
         this.reader.forward();
      }

      CommentToken var2 = null;
      if (this.reader.peek() == 35) {
         CommentToken var3 = this.scanComment(CommentType.IN_LINE);
         if (this.settings.getParseComments()) {
            var2 = var3;
         }
      }

      int var5 = this.reader.peek();
      if (!this.scanLineBreak().isPresent() && var5 != 0) {
         String var4 = String.valueOf(Character.toChars(var5));
         throw new ScannerException("while scanning a directive", var1, "expected a comment or a line break, but found " + var4 + "(" + var5 + ")", this.reader.getMark());
      } else {
         return var2;
      }
   }

   private Token scanAnchor(boolean var1) {
      Optional var2 = this.reader.getMark();
      int var3 = this.reader.peek();
      String var4 = var3 == 42 ? "alias" : "anchor";
      this.reader.forward();
      int var5 = 0;

      int var6;
      for(var6 = this.reader.peek(var5); CharConstants.NULL_BL_T_LINEBR.hasNo(var6, ",[]{}/.*&"); var6 = this.reader.peek(var5)) {
         ++var5;
      }

      String var7;
      if (var5 == 0) {
         var7 = String.valueOf(Character.toChars(var6));
         throw new ScannerException("while scanning an " + var4, var2, "unexpected character found " + var7 + "(" + var6 + ")", this.reader.getMark());
      } else {
         var7 = this.reader.prefixForward(var5);
         var6 = this.reader.peek();
         if (CharConstants.NULL_BL_T_LINEBR.hasNo(var6, "?:,]}%@`")) {
            String var10 = String.valueOf(Character.toChars(var6));
            throw new ScannerException("while scanning an " + var4, var2, "unexpected character found " + var10 + "(" + var6 + ")", this.reader.getMark());
         } else {
            Optional var8 = this.reader.getMark();
            Object var9;
            if (var1) {
               var9 = new AnchorToken(new Anchor(var7), var2, var8);
            } else {
               var9 = new AliasToken(new Anchor(var7), var2, var8);
            }

            return (Token)var9;
         }
      }
   }

   private Token scanTag() {
      Optional var1 = this.reader.getMark();
      int var2 = this.reader.peek(1);
      String var3 = null;
      String var4 = null;
      String var5;
      if (var2 == 60) {
         this.reader.forward(2);
         var4 = this.scanTagUri("tag", CharConstants.URI_CHARS_FOR_TAG_PREFIX, var1);
         var2 = this.reader.peek();
         if (var2 != 62) {
            var5 = String.valueOf(Character.toChars(var2));
            throw new ScannerException("while scanning a tag", var1, "expected '>', but found '" + var5 + "' (" + var2 + ")", this.reader.getMark());
         }

         this.reader.forward();
      } else if (CharConstants.NULL_BL_T_LINEBR.has(var2)) {
         var4 = "!";
         this.reader.forward();
      } else {
         int var7 = 1;

         boolean var6;
         for(var6 = false; CharConstants.NULL_BL_LINEBR.hasNo(var2); var2 = this.reader.peek(var7)) {
            if (var2 == 33) {
               var6 = true;
               break;
            }

            ++var7;
         }

         if (var6) {
            var3 = this.scanTagHandle("tag", var1);
         } else {
            var3 = "!";
            this.reader.forward();
         }

         var4 = this.scanTagUri("tag", CharConstants.URI_CHARS_FOR_TAG_SUFFIX, var1);
      }

      var2 = this.reader.peek();
      if (CharConstants.NULL_BL_LINEBR.hasNo(var2)) {
         var5 = String.valueOf(Character.toChars(var2));
         throw new ScannerException("while scanning a tag", var1, "expected ' ', but found '" + var5 + "' (" + var2 + ")", this.reader.getMark());
      } else {
         TagTuple var8 = new TagTuple(Optional.ofNullable(var3), var4);
         Optional var9 = this.reader.getMark();
         return new TagToken(var8, var1, var9);
      }
   }

   private List<Token> scanBlockScalar(ScalarStyle var1) {
      StringBuilder var2 = new StringBuilder();
      Optional var3 = this.reader.getMark();
      this.reader.forward();
      ScannerImpl.Chomping var4 = this.scanBlockScalarIndicators(var3);
      CommentToken var5 = this.scanBlockScalarIgnoredLine(var3);
      int var6 = this.indent + 1;
      if (var6 < 1) {
         var6 = 1;
      }

      String var7;
      int var9;
      Optional var10;
      ScannerImpl.BreakIntentHolder var11;
      if (var4.increment.isPresent()) {
         var9 = var6 + (Integer)var4.increment.get() - 1;
         var11 = this.scanBlockScalarBreaks(var9);
         var7 = var11.breaks;
         var10 = var11.endMark;
      } else {
         var11 = this.scanBlockScalarIndentation();
         var7 = var11.breaks;
         int var8 = var11.maxIndent;
         var10 = var11.endMark;
         var9 = Math.max(var6, var8);
      }

      Optional var15 = Optional.empty();
      if (this.reader.getColumn() < var9 && this.indent != this.reader.getColumn()) {
         throw new ScannerException("while scanning a block scalar", var3, " the leading empty lines contain more spaces (" + var9 + ") than the first non-empty line.", this.reader.getMark());
      } else {
         while(this.reader.getColumn() == var9 && this.reader.peek() != 0) {
            var2.append(var7);
            boolean var12 = " \t".indexOf(this.reader.peek()) == -1;

            int var13;
            for(var13 = 0; CharConstants.NULL_OR_LINEBR.hasNo(this.reader.peek(var13)); ++var13) {
            }

            var2.append(this.reader.prefixForward(var13));
            var15 = this.scanLineBreak();
            ScannerImpl.BreakIntentHolder var14 = this.scanBlockScalarBreaks(var9);
            var7 = var14.breaks;
            var10 = var14.endMark;
            if (this.reader.getColumn() != var9 || this.reader.peek() == 0) {
               break;
            }

            if (var1 == ScalarStyle.FOLDED && "\n".equals(var15.orElse("")) && var12 && " \t".indexOf(this.reader.peek()) == -1) {
               if (var7.isEmpty()) {
                  var2.append(" ");
               }
            } else {
               var2.append((String)var15.orElse(""));
            }
         }

         if (var4.value == ScannerImpl.Chomping.Indicator.CLIP || var4.value == ScannerImpl.Chomping.Indicator.KEEP) {
            var2.append((String)var15.orElse(""));
         }

         if (var4.value == ScannerImpl.Chomping.Indicator.KEEP) {
            var2.append(var7);
         }

         ScalarToken var16 = new ScalarToken(var2.toString(), false, var1, var3, var10);
         return this.makeTokenList(var5, var16);
      }
   }

   private ScannerImpl.Chomping scanBlockScalarIndicators(Optional<Mark> var1) {
      int var2 = Integer.MIN_VALUE;
      Optional var3 = Optional.empty();
      int var4 = this.reader.peek();
      int var5;
      if (var4 != 45 && var4 != 43) {
         if (Character.isDigit(var4)) {
            var5 = Integer.parseInt(String.valueOf(Character.toChars(var4)));
            if (var5 == 0) {
               throw new ScannerException("while scanning a block scalar", var1, "expected indentation indicator in the range 1-9, but found 0", this.reader.getMark());
            }

            var3 = Optional.of(var5);
            this.reader.forward();
            var4 = this.reader.peek();
            if (var4 == 45 || var4 == 43) {
               var2 = var4;
               this.reader.forward();
            }
         }
      } else {
         var2 = var4;
         this.reader.forward();
         var4 = this.reader.peek();
         if (Character.isDigit(var4)) {
            var5 = Integer.parseInt(String.valueOf(Character.toChars(var4)));
            if (var5 == 0) {
               throw new ScannerException("while scanning a block scalar", var1, "expected indentation indicator in the range 1-9, but found 0", this.reader.getMark());
            }

            var3 = Optional.of(var5);
            this.reader.forward();
         }
      }

      var4 = this.reader.peek();
      if (CharConstants.NULL_BL_LINEBR.hasNo(var4)) {
         String var6 = String.valueOf(Character.toChars(var4));
         throw new ScannerException("while scanning a block scalar", var1, "expected chomping or indentation indicators, but found " + var6 + "(" + var4 + ")", this.reader.getMark());
      } else {
         return new ScannerImpl.Chomping(var2, var3);
      }
   }

   private CommentToken scanBlockScalarIgnoredLine(Optional<Mark> var1) {
      while(this.reader.peek() == 32) {
         this.reader.forward();
      }

      CommentToken var2 = null;
      if (this.reader.peek() == 35) {
         var2 = this.scanComment(CommentType.IN_LINE);
      }

      int var3 = this.reader.peek();
      if (!this.scanLineBreak().isPresent() && var3 != 0) {
         String var4 = String.valueOf(Character.toChars(var3));
         throw new ScannerException("while scanning a block scalar", var1, "expected a comment or a line break, but found " + var4 + "(" + var3 + ")", this.reader.getMark());
      } else {
         return var2;
      }
   }

   private ScannerImpl.BreakIntentHolder scanBlockScalarIndentation() {
      StringBuilder var1 = new StringBuilder();
      int var2 = 0;
      Optional var3 = this.reader.getMark();

      while(CharConstants.LINEBR.has(this.reader.peek(), " \r")) {
         if (this.reader.peek() != 32) {
            var1.append((String)this.scanLineBreak().orElse(""));
            var3 = this.reader.getMark();
         } else {
            this.reader.forward();
            if (this.reader.getColumn() > var2) {
               var2 = this.reader.getColumn();
            }
         }
      }

      return new ScannerImpl.BreakIntentHolder(var1.toString(), var2, var3);
   }

   private ScannerImpl.BreakIntentHolder scanBlockScalarBreaks(int var1) {
      StringBuilder var2 = new StringBuilder();
      Optional var3 = this.reader.getMark();

      int var4;
      for(var4 = this.reader.getColumn(); var4 < var1 && this.reader.peek() == 32; ++var4) {
         this.reader.forward();
      }

      Optional var5;
      while((var5 = this.scanLineBreak()).isPresent()) {
         var2.append((String)var5.get());
         var3 = this.reader.getMark();

         for(var4 = this.reader.getColumn(); var4 < var1 && this.reader.peek() == 32; ++var4) {
            this.reader.forward();
         }
      }

      return new ScannerImpl.BreakIntentHolder(var2.toString(), -1, var3);
   }

   private Token scanFlowScalar(ScalarStyle var1) {
      boolean var2 = var1 == ScalarStyle.DOUBLE_QUOTED;
      StringBuilder var3 = new StringBuilder();
      Optional var4 = this.reader.getMark();
      int var5 = this.reader.peek();
      this.reader.forward();
      var3.append(this.scanFlowScalarNonSpaces(var2, var4));

      while(this.reader.peek() != var5) {
         var3.append(this.scanFlowScalarSpaces(var4));
         var3.append(this.scanFlowScalarNonSpaces(var2, var4));
      }

      this.reader.forward();
      Optional var6 = this.reader.getMark();
      return new ScalarToken(var3.toString(), false, var1, var4, var6);
   }

   private String scanFlowScalarNonSpaces(boolean var1, Optional<Mark> var2) {
      StringBuilder var3 = new StringBuilder();

      while(true) {
         while(true) {
            while(true) {
               int var4;
               for(var4 = 0; CharConstants.NULL_BL_T_LINEBR.hasNo(this.reader.peek(var4), "'\"\\"); ++var4) {
               }

               if (var4 != 0) {
                  var3.append(this.reader.prefixForward(var4));
               }

               int var5 = this.reader.peek();
               if (var1 || var5 != 39 || this.reader.peek(1) != 39) {
                  if ((!var1 || var5 != 39) && (var1 || "\"\\".indexOf(var5) == -1)) {
                     if (!var1 || var5 != 92) {
                        return var3.toString();
                     }

                     this.reader.forward();
                     var5 = this.reader.peek();
                     if (!Character.isSupplementaryCodePoint(var5) && CharConstants.ESCAPE_REPLACEMENTS.containsKey((char)var5)) {
                        var3.append((String)CharConstants.ESCAPE_REPLACEMENTS.get((char)var5));
                        this.reader.forward();
                     } else {
                        String var6;
                        if (!Character.isSupplementaryCodePoint(var5) && CharConstants.ESCAPE_CODES.containsKey((char)var5)) {
                           var4 = (Integer)CharConstants.ESCAPE_CODES.get((char)var5);
                           this.reader.forward();
                           var6 = this.reader.prefix(var4);
                           if (NOT_HEXA.matcher(var6).find()) {
                              throw new ScannerException("while scanning a double-quoted scalar", var2, "expected escape sequence of " + var4 + " hexadecimal numbers, but found: " + var6, this.reader.getMark());
                           }

                           int var7 = Integer.parseInt(var6, 16);

                           try {
                              String var8 = new String(Character.toChars(var7));
                              var3.append(var8);
                              this.reader.forward(var4);
                           } catch (IllegalArgumentException var9) {
                              throw new ScannerException("while scanning a double-quoted scalar", var2, "found unknown escape character " + var6, this.reader.getMark());
                           }
                        } else {
                           if (!this.scanLineBreak().isPresent()) {
                              var6 = String.valueOf(Character.toChars(var5));
                              throw new ScannerException("while scanning a double-quoted scalar", var2, "found unknown escape character " + var6 + "(" + var5 + ")", this.reader.getMark());
                           }

                           var3.append(this.scanFlowScalarBreaks(var2));
                        }
                     }
                  } else {
                     var3.appendCodePoint(var5);
                     this.reader.forward();
                  }
               } else {
                  var3.append("'");
                  this.reader.forward(2);
               }
            }
         }
      }
   }

   private String scanFlowScalarSpaces(Optional<Mark> var1) {
      StringBuilder var2 = new StringBuilder();

      int var3;
      for(var3 = 0; " \t".indexOf(this.reader.peek(var3)) != -1; ++var3) {
      }

      String var4 = this.reader.prefixForward(var3);
      int var5 = this.reader.peek();
      if (var5 == 0) {
         throw new ScannerException("while scanning a quoted scalar", var1, "found unexpected end of stream", this.reader.getMark());
      } else {
         Optional var6 = this.scanLineBreak();
         if (var6.isPresent()) {
            String var7 = this.scanFlowScalarBreaks(var1);
            if (!"\n".equals(var6.get())) {
               var2.append((String)var6.get());
            } else if (var7.isEmpty()) {
               var2.append(" ");
            }

            var2.append(var7);
         } else {
            var2.append(var4);
         }

         return var2.toString();
      }
   }

   private String scanFlowScalarBreaks(Optional<Mark> var1) {
      StringBuilder var2 = new StringBuilder();

      while(true) {
         String var3 = this.reader.prefix(3);
         if (("---".equals(var3) || "...".equals(var3)) && CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(3))) {
            throw new ScannerException("while scanning a quoted scalar", var1, "found unexpected document separator", this.reader.getMark());
         }

         while(" \t".indexOf(this.reader.peek()) != -1) {
            this.reader.forward();
         }

         Optional var4 = this.scanLineBreak();
         if (!var4.isPresent()) {
            return var2.toString();
         }

         var2.append((String)var4.get());
      }
   }

   private Token scanPlain() {
      StringBuilder var1 = new StringBuilder();
      Optional var2 = this.reader.getMark();
      Optional var3 = var2;
      int var4 = this.indent + 1;
      String var5 = "";

      do {
         int var7 = 0;
         if (this.reader.peek() == 35) {
            break;
         }

         while(true) {
            int var6 = this.reader.peek(var7);
            if (CharConstants.NULL_BL_T_LINEBR.has(var6) || var6 == 58 && CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(var7 + 1), this.isFlowContext() ? ",[]{}" : "") || this.isFlowContext() && ",[]{}".indexOf(var6) != -1) {
               if (var7 == 0) {
                  return new ScalarToken(var1.toString(), true, var2, var3);
               }

               this.allowSimpleKey = false;
               var1.append(var5);
               var1.append(this.reader.prefixForward(var7));
               var3 = this.reader.getMark();
               var5 = this.scanPlainSpaces();
               break;
            }

            ++var7;
         }
      } while(!var5.isEmpty() && this.reader.peek() != 35 && (!this.isBlockContext() || this.reader.getColumn() >= var4));

      return new ScalarToken(var1.toString(), true, var2, var3);
   }

   private boolean atEndOfPlain() {
      int var1 = 0;
      int var2 = this.reader.getColumn();

      int var3;
      while((var3 = this.reader.peek(var1)) != 0 && CharConstants.NULL_BL_T_LINEBR.has(var3)) {
         ++var1;
         if (!CharConstants.LINEBR.has(var3) && (var3 != 13 || this.reader.peek(var1 + 1) != 10) && var3 != 65279) {
            ++var2;
         } else {
            var2 = 0;
         }
      }

      if (this.reader.peek(var1) == 35 || this.reader.peek(var1 + 1) == 0 || this.isBlockContext() && var2 < this.indent) {
         return true;
      } else {
         if (this.isBlockContext()) {
            for(int var4 = 1; (var3 = this.reader.peek(var1 + var4)) != 0 && !CharConstants.NULL_BL_T_LINEBR.has(var3); ++var4) {
               if (var3 == 58 && CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(var1 + var4 + 1))) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private String scanPlainSpaces() {
      int var1;
      for(var1 = 0; this.reader.peek(var1) == 32 || this.reader.peek(var1) == 9; ++var1) {
      }

      String var2 = this.reader.prefixForward(var1);
      Optional var3 = this.scanLineBreak();
      if (!var3.isPresent()) {
         return var2;
      } else {
         this.allowSimpleKey = true;
         String var4 = this.reader.prefix(3);
         if ("---".equals(var4) || "...".equals(var4) && CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(3))) {
            return "";
         } else if (this.settings.getParseComments() && this.atEndOfPlain()) {
            return "";
         } else {
            StringBuilder var5 = new StringBuilder();

            do {
               while(this.reader.peek() == 32) {
                  this.reader.forward();
               }

               Optional var6 = this.scanLineBreak();
               if (!var6.isPresent()) {
                  if (!"\n".equals(var3.orElse(""))) {
                     return (String)var3.orElse("") + var5;
                  }

                  if (var5.length() == 0) {
                     return " ";
                  }

                  return var5.toString();
               }

               var5.append((String)var6.get());
               var4 = this.reader.prefix(3);
            } while(!"---".equals(var4) && (!"...".equals(var4) || !CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(3))));

            return "";
         }
      }
   }

   private String scanTagHandle(String var1, Optional<Mark> var2) {
      int var3 = this.reader.peek();
      if (var3 != 33) {
         String var6 = String.valueOf(Character.toChars(var3));
         throw new ScannerException("while scanning a " + var1, var2, "expected '!', but found " + var6 + "(" + var3 + ")", this.reader.getMark());
      } else {
         int var4 = 1;
         var3 = this.reader.peek(var4);
         if (var3 != 32) {
            while(CharConstants.ALPHA.has(var3)) {
               ++var4;
               var3 = this.reader.peek(var4);
            }

            if (var3 != 33) {
               this.reader.forward(var4);
               String var5 = String.valueOf(Character.toChars(var3));
               throw new ScannerException("while scanning a " + var1, var2, "expected '!', but found " + var5 + "(" + var3 + ")", this.reader.getMark());
            }

            ++var4;
         }

         return this.reader.prefixForward(var4);
      }
   }

   private String scanTagUri(String var1, CharConstants var2, Optional<Mark> var3) {
      StringBuilder var4 = new StringBuilder();
      int var5 = 0;

      int var6;
      for(var6 = this.reader.peek(var5); var2.has(var6); var6 = this.reader.peek(var5)) {
         if (var6 == 37) {
            var4.append(this.reader.prefixForward(var5));
            var5 = 0;
            var4.append(this.scanUriEscapes(var1, var3));
         } else {
            ++var5;
         }
      }

      if (var5 != 0) {
         var4.append(this.reader.prefixForward(var5));
      }

      if (var4.length() == 0) {
         String var7 = String.valueOf(Character.toChars(var6));
         throw new ScannerException("while scanning a " + var1, var3, "expected URI, but found " + var7 + "(" + var6 + ")", this.reader.getMark());
      } else {
         return var4.toString();
      }
   }

   private String scanUriEscapes(String var1, Optional<Mark> var2) {
      int var3;
      for(var3 = 1; this.reader.peek(var3 * 3) == 37; ++var3) {
      }

      Optional var4 = this.reader.getMark();

      ByteBuffer var5;
      for(var5 = ByteBuffer.allocate(var3); this.reader.peek() == 37; this.reader.forward(2)) {
         this.reader.forward();

         try {
            byte var6 = (byte)Integer.parseInt(this.reader.prefix(2), 16);
            var5.put(var6);
         } catch (NumberFormatException var12) {
            int var7 = this.reader.peek();
            String var8 = String.valueOf(Character.toChars(var7));
            int var9 = this.reader.peek(1);
            String var10 = String.valueOf(Character.toChars(var9));
            throw new ScannerException("while scanning a " + var1, var2, "expected URI escape sequence of 2 hexadecimal numbers, but found " + var8 + "(" + var7 + ") and " + var10 + "(" + var9 + ")", this.reader.getMark());
         }
      }

      var5.flip();

      try {
         return UriEncoder.decode(var5);
      } catch (CharacterCodingException var11) {
         throw new ScannerException("while scanning a " + var1, var2, "expected URI in UTF-8: " + var11.getMessage(), var4);
      }
   }

   private Optional<String> scanLineBreak() {
      int var1 = this.reader.peek();
      if (var1 != 13 && var1 != 10 && var1 != 133) {
         return Optional.empty();
      } else {
         if (var1 == 13 && 10 == this.reader.peek(1)) {
            this.reader.forward(2);
         } else {
            this.reader.forward();
         }

         return Optional.of("\n");
      }
   }

   private List<Token> makeTokenList(Token... var1) {
      ArrayList var2 = new ArrayList();

      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var1[var3] != null && (this.settings.getParseComments() || !(var1[var3] instanceof CommentToken))) {
            var2.add(var1[var3]);
         }
      }

      return var2;
   }

   public void resetDocumentIndex() {
      this.reader.resetDocumentIndex();
   }

   static class Chomping {
      private final ScannerImpl.Chomping.Indicator value;
      private final Optional<Integer> increment;

      public Chomping(ScannerImpl.Chomping.Indicator var1, Optional<Integer> var2) {
         this.value = var1;
         this.increment = var2;
      }

      public Chomping(int var1, Optional<Integer> var2) {
         this(parse(var1), var2);
      }

      private static ScannerImpl.Chomping.Indicator parse(int var0) {
         if (var0 == 43) {
            return ScannerImpl.Chomping.Indicator.KEEP;
         } else if (var0 == 45) {
            return ScannerImpl.Chomping.Indicator.STRIP;
         } else if (var0 == Integer.MIN_VALUE) {
            return ScannerImpl.Chomping.Indicator.CLIP;
         } else {
            throw new IllegalArgumentException("Unexpected block chomping indicator: " + var0);
         }
      }

      static enum Indicator {
         STRIP,
         CLIP,
         KEEP;

         // $FF: synthetic method
         private static ScannerImpl.Chomping.Indicator[] $values() {
            return new ScannerImpl.Chomping.Indicator[]{STRIP, CLIP, KEEP};
         }
      }
   }

   static class BreakIntentHolder {
      private final String breaks;
      private final int maxIndent;
      private final Optional<Mark> endMark;

      public BreakIntentHolder(String var1, int var2, Optional<Mark> var3) {
         this.breaks = var1;
         this.maxIndent = var2;
         this.endMark = var3;
      }
   }
}

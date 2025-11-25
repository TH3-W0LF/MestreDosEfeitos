package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens;

import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public abstract class Token {
   private final Optional<Mark> startMark;
   private final Optional<Mark> endMark;

   public Token(Optional<Mark> var1, Optional<Mark> var2) {
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      this.startMark = var1;
      this.endMark = var2;
   }

   public Optional<Mark> getStartMark() {
      return this.startMark;
   }

   public Optional<Mark> getEndMark() {
      return this.endMark;
   }

   public abstract Token.ID getTokenId();

   public String toString() {
      return this.getTokenId().toString();
   }

   public static enum ID {
      Alias("<alias>"),
      Anchor("<anchor>"),
      BlockEnd("<block end>"),
      BlockEntry("-"),
      BlockMappingStart("<block mapping start>"),
      BlockSequenceStart("<block sequence start>"),
      Directive("<directive>"),
      DocumentEnd("<document end>"),
      DocumentStart("<document start>"),
      FlowEntry(","),
      FlowMappingEnd("}"),
      FlowMappingStart("{"),
      FlowSequenceEnd("]"),
      FlowSequenceStart("["),
      Key("?"),
      Scalar("<scalar>"),
      StreamEnd("<stream end>"),
      StreamStart("<stream start>"),
      Tag("<tag>"),
      Comment("#"),
      Value(":");

      private final String description;

      private ID(String var3) {
         this.description = var3;
      }

      public String toString() {
         return this.description;
      }

      // $FF: synthetic method
      private static Token.ID[] $values() {
         return new Token.ID[]{Alias, Anchor, BlockEnd, BlockEntry, BlockMappingStart, BlockSequenceStart, Directive, DocumentEnd, DocumentStart, FlowEntry, FlowMappingEnd, FlowMappingStart, FlowSequenceEnd, FlowSequenceStart, Key, Scalar, StreamEnd, StreamStart, Tag, Comment, Value};
      }
   }
}

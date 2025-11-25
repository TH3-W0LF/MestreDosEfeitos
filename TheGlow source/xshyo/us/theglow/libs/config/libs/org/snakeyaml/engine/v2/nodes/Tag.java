package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes;

import java.util.Objects;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.UriEncoder;

public final class Tag {
   public static final String PREFIX = "tag:yaml.org,2002:";
   public static final Tag SET = new Tag("tag:yaml.org,2002:set");
   public static final Tag BINARY = new Tag("tag:yaml.org,2002:binary");
   public static final Tag INT = new Tag("tag:yaml.org,2002:int");
   public static final Tag FLOAT = new Tag("tag:yaml.org,2002:float");
   public static final Tag BOOL = new Tag("tag:yaml.org,2002:bool");
   public static final Tag NULL = new Tag("tag:yaml.org,2002:null");
   public static final Tag STR = new Tag("tag:yaml.org,2002:str");
   public static final Tag SEQ = new Tag("tag:yaml.org,2002:seq");
   public static final Tag MAP = new Tag("tag:yaml.org,2002:map");
   public static final Tag COMMENT = new Tag("tag:yaml.org,2002:comment");
   public static final Tag ENV_TAG = new Tag("!ENV_VARIABLE");
   private final String value;

   public Tag(String var1) {
      Objects.requireNonNull(var1, "Tag must be provided.");
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("Tag must not be empty.");
      } else if (var1.trim().length() != var1.length()) {
         throw new IllegalArgumentException("Tag must not contain leading or trailing spaces.");
      } else {
         this.value = UriEncoder.encode(var1);
      }
   }

   public Tag(Class<? extends Object> var1) {
      Objects.requireNonNull(var1, "Class for tag must be provided.");
      this.value = "tag:yaml.org,2002:" + UriEncoder.encode(var1.getName());
   }

   public String getValue() {
      return this.value;
   }

   public String toString() {
      return this.value;
   }

   public boolean equals(Object var1) {
      return var1 instanceof Tag ? this.value.equals(((Tag)var1).getValue()) : false;
   }

   public int hashCode() {
      return this.value.hashCode();
   }
}

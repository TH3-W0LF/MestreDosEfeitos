package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments.CommentLine;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.Anchor;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public abstract class Node {
   private final Optional<Mark> startMark;
   protected Optional<Mark> endMark;
   protected boolean resolved;
   private Tag tag;
   private boolean recursive;
   private Optional<Anchor> anchor;
   private List<CommentLine> inLineComments;
   private List<CommentLine> blockComments;
   private List<CommentLine> endComments;
   private Map<String, Object> properties;

   public Node(Tag var1, Optional<Mark> var2, Optional<Mark> var3) {
      this.setTag(var1);
      this.startMark = var2;
      this.endMark = var3;
      this.recursive = false;
      this.resolved = true;
      this.anchor = Optional.empty();
      this.inLineComments = null;
      this.blockComments = null;
      this.endComments = null;
      this.properties = null;
   }

   public Tag getTag() {
      return this.tag;
   }

   public void setTag(Tag var1) {
      Objects.requireNonNull(var1, "tag in a Node is required.");
      this.tag = var1;
   }

   public Optional<Mark> getEndMark() {
      return this.endMark;
   }

   public abstract NodeType getNodeType();

   public Optional<Mark> getStartMark() {
      return this.startMark;
   }

   public final boolean equals(Object var1) {
      return super.equals(var1);
   }

   public boolean isRecursive() {
      return this.recursive;
   }

   public void setRecursive(boolean var1) {
      this.recursive = var1;
   }

   public final int hashCode() {
      return super.hashCode();
   }

   public Optional<Anchor> getAnchor() {
      return this.anchor;
   }

   public void setAnchor(Optional<Anchor> var1) {
      this.anchor = var1;
   }

   public Object setProperty(String var1, Object var2) {
      if (this.properties == null) {
         this.properties = new HashMap();
      }

      return this.properties.put(var1, var2);
   }

   public Object getProperty(String var1) {
      return this.properties == null ? null : this.properties.get(var1);
   }

   public List<CommentLine> getInLineComments() {
      return this.inLineComments;
   }

   public void setInLineComments(List<CommentLine> var1) {
      this.inLineComments = var1;
   }

   public List<CommentLine> getBlockComments() {
      return this.blockComments;
   }

   public void setBlockComments(List<CommentLine> var1) {
      this.blockComments = var1;
   }

   public List<CommentLine> getEndComments() {
      return this.endComments;
   }

   public void setEndComments(List<CommentLine> var1) {
      this.endComments = var1;
   }
}

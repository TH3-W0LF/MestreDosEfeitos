package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common;

import java.io.Serializable;

public class SpecVersion implements Serializable {
   private final int major;
   private final int minor;

   public SpecVersion(int var1, int var2) {
      this.major = var1;
      this.minor = var2;
   }

   public int getMajor() {
      return this.major;
   }

   public int getMinor() {
      return this.minor;
   }

   public String getRepresentation() {
      return this.major + "." + this.minor;
   }

   public String toString() {
      return "Version{major=" + this.major + ", minor=" + this.minor + '}';
   }
}

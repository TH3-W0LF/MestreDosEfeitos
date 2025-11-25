package xshyo.us.theglow.libs.config.dvs.versioning;

import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.config.dvs.Pattern;
import xshyo.us.theglow.libs.config.dvs.segment.Segment;

public class BasicVersioning extends AutomaticVersioning {
   public static final Pattern PATTERN = new Pattern(new Segment[]{Segment.range(1, Integer.MAX_VALUE)});

   public BasicVersioning(@NotNull String var1) {
      super(PATTERN, var1);
   }
}

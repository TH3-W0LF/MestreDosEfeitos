package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.serializer;

import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.Anchor;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;

public interface AnchorGenerator {
   Anchor nextAnchor(Node var1);
}

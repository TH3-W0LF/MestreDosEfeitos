package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.external.com.google.gdata.util.common.base.Escaper;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.external.com.google.gdata.util.common.base.PercentEscaper;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;

public abstract class UriEncoder {
   private static final CharsetDecoder UTF8Decoder;
   private static final String SAFE_CHARS = "-_.!~*'()@:$&,;=[]/";
   private static final Escaper escaper;

   private UriEncoder() {
   }

   public static String encode(String var0) {
      return escaper.escape(var0);
   }

   public static String decode(ByteBuffer var0) throws CharacterCodingException {
      CharBuffer var1 = UTF8Decoder.decode(var0);
      return var1.toString();
   }

   public static String decode(String var0) {
      try {
         return URLDecoder.decode(var0, "UTF-8");
      } catch (UnsupportedEncodingException var2) {
         throw new YamlEngineException(var2);
      }
   }

   static {
      UTF8Decoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT);
      escaper = new PercentEscaper("-_.!~*'()@:$&,;=[]/", false);
   }
}

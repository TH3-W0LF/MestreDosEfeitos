package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.representer;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.DumpSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.RepresentToNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.NonPrintableStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.scanner.StreamReader;

public class StandardRepresenter extends BaseRepresenter {
   public static final Pattern MULTILINE_PATTERN = Pattern.compile("\n|\u0085");
   protected Map<Class<? extends Object>, Tag> classTags;
   protected DumpSettings settings;

   public StandardRepresenter(DumpSettings var1) {
      this.defaultFlowStyle = var1.getDefaultFlowStyle();
      this.defaultScalarStyle = var1.getDefaultScalarStyle();
      this.nullRepresenter = new StandardRepresenter.RepresentNull();
      this.representers.put(String.class, new StandardRepresenter.RepresentString());
      this.representers.put(Boolean.class, new StandardRepresenter.RepresentBoolean());
      this.representers.put(Character.class, new StandardRepresenter.RepresentString());
      this.representers.put(UUID.class, new StandardRepresenter.RepresentUuid());
      this.representers.put(Optional.class, new StandardRepresenter.RepresentOptional());
      this.representers.put(byte[].class, new StandardRepresenter.RepresentByteArray());
      StandardRepresenter.RepresentPrimitiveArray var2 = new StandardRepresenter.RepresentPrimitiveArray();
      this.representers.put(short[].class, var2);
      this.representers.put(int[].class, var2);
      this.representers.put(long[].class, var2);
      this.representers.put(float[].class, var2);
      this.representers.put(double[].class, var2);
      this.representers.put(char[].class, var2);
      this.representers.put(boolean[].class, var2);
      this.parentClassRepresenters.put(Number.class, new StandardRepresenter.RepresentNumber());
      this.parentClassRepresenters.put(List.class, new StandardRepresenter.RepresentList());
      this.parentClassRepresenters.put(Map.class, new StandardRepresenter.RepresentMap());
      this.parentClassRepresenters.put(Set.class, new StandardRepresenter.RepresentSet());
      this.parentClassRepresenters.put(Iterator.class, new StandardRepresenter.RepresentIterator());
      this.parentClassRepresenters.put((new Object[0]).getClass(), new StandardRepresenter.RepresentArray());
      this.parentClassRepresenters.put(Enum.class, new StandardRepresenter.RepresentEnum());
      this.classTags = new HashMap();
      this.settings = var1;
   }

   protected Tag getTag(Class<?> var1, Tag var2) {
      return (Tag)this.classTags.getOrDefault(var1, var2);
   }

   @Deprecated
   public Tag addClassTag(Class<? extends Object> var1, Tag var2) {
      if (var2 == null) {
         throw new NullPointerException("Tag must be provided.");
      } else {
         return (Tag)this.classTags.put(var1, var2);
      }
   }

   protected class RepresentNull implements RepresentToNode {
      public Node representData(Object var1) {
         return StandardRepresenter.this.representScalar(Tag.NULL, "null");
      }
   }

   public class RepresentString implements RepresentToNode {
      public Node representData(Object var1) {
         Tag var2 = Tag.STR;
         ScalarStyle var3 = ScalarStyle.PLAIN;
         String var4 = var1.toString();
         if (StandardRepresenter.this.settings.getNonPrintableStyle() == NonPrintableStyle.BINARY && !StreamReader.isPrintable(var4)) {
            var2 = Tag.BINARY;
            byte[] var5 = var4.getBytes(StandardCharsets.UTF_8);
            String var6 = new String(var5, StandardCharsets.UTF_8);
            if (!var6.equals(var4)) {
               throw new YamlEngineException("invalid string value has occurred");
            }

            var4 = Base64.getEncoder().encodeToString(var5);
            var3 = ScalarStyle.LITERAL;
         }

         if (StandardRepresenter.this.defaultScalarStyle == ScalarStyle.PLAIN && StandardRepresenter.MULTILINE_PATTERN.matcher(var4).find()) {
            var3 = ScalarStyle.LITERAL;
         }

         return StandardRepresenter.this.representScalar(var2, var4, var3);
      }
   }

   public class RepresentBoolean implements RepresentToNode {
      public Node representData(Object var1) {
         String var2;
         if (Boolean.TRUE.equals(var1)) {
            var2 = "true";
         } else {
            var2 = "false";
         }

         return StandardRepresenter.this.representScalar(Tag.BOOL, var2);
      }
   }

   public class RepresentUuid implements RepresentToNode {
      public Node representData(Object var1) {
         return StandardRepresenter.this.representScalar(StandardRepresenter.this.getTag(var1.getClass(), new Tag(UUID.class)), var1.toString());
      }
   }

   public class RepresentOptional implements RepresentToNode {
      public Node representData(Object var1) {
         Optional var2 = (Optional)var1;
         if (var2.isPresent()) {
            Node var3 = StandardRepresenter.this.represent(var2.get());
            var3.setTag(new Tag(Optional.class));
            return var3;
         } else {
            return StandardRepresenter.this.representScalar(Tag.NULL, "null");
         }
      }
   }

   public class RepresentByteArray implements RepresentToNode {
      public Node representData(Object var1) {
         return StandardRepresenter.this.representScalar(Tag.BINARY, Base64.getEncoder().encodeToString((byte[])var1), ScalarStyle.LITERAL);
      }
   }

   public class RepresentPrimitiveArray implements RepresentToNode {
      public Node representData(Object var1) {
         Class var2 = var1.getClass().getComponentType();
         FlowStyle var3 = StandardRepresenter.this.settings.getDefaultFlowStyle();
         if (Byte.TYPE == var2) {
            return StandardRepresenter.this.representSequence(Tag.SEQ, this.asByteList(var1), var3);
         } else if (Short.TYPE == var2) {
            return StandardRepresenter.this.representSequence(Tag.SEQ, this.asShortList(var1), var3);
         } else if (Integer.TYPE == var2) {
            return StandardRepresenter.this.representSequence(Tag.SEQ, this.asIntList(var1), var3);
         } else if (Long.TYPE == var2) {
            return StandardRepresenter.this.representSequence(Tag.SEQ, this.asLongList(var1), var3);
         } else if (Float.TYPE == var2) {
            return StandardRepresenter.this.representSequence(Tag.SEQ, this.asFloatList(var1), var3);
         } else if (Double.TYPE == var2) {
            return StandardRepresenter.this.representSequence(Tag.SEQ, this.asDoubleList(var1), var3);
         } else if (Character.TYPE == var2) {
            return StandardRepresenter.this.representSequence(Tag.SEQ, this.asCharList(var1), var3);
         } else if (Boolean.TYPE == var2) {
            return StandardRepresenter.this.representSequence(Tag.SEQ, this.asBooleanList(var1), var3);
         } else {
            throw new YamlEngineException("Unexpected primitive '" + var2.getCanonicalName() + "'");
         }
      }

      private List<Byte> asByteList(Object var1) {
         byte[] var2 = (byte[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3.add(var2[var4]);
         }

         return var3;
      }

      private List<Short> asShortList(Object var1) {
         short[] var2 = (short[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3.add(var2[var4]);
         }

         return var3;
      }

      private List<Integer> asIntList(Object var1) {
         int[] var2 = (int[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3.add(var2[var4]);
         }

         return var3;
      }

      private List<Long> asLongList(Object var1) {
         long[] var2 = (long[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3.add(var2[var4]);
         }

         return var3;
      }

      private List<Float> asFloatList(Object var1) {
         float[] var2 = (float[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3.add(var2[var4]);
         }

         return var3;
      }

      private List<Double> asDoubleList(Object var1) {
         double[] var2 = (double[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3.add(var2[var4]);
         }

         return var3;
      }

      private List<Character> asCharList(Object var1) {
         char[] var2 = (char[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3.add(var2[var4]);
         }

         return var3;
      }

      private List<Boolean> asBooleanList(Object var1) {
         boolean[] var2 = (boolean[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3.add(var2[var4]);
         }

         return var3;
      }
   }

   public class RepresentNumber implements RepresentToNode {
      public Node representData(Object var1) {
         Tag var2;
         String var3;
         if (!(var1 instanceof Byte) && !(var1 instanceof Short) && !(var1 instanceof Integer) && !(var1 instanceof Long) && !(var1 instanceof BigInteger)) {
            Number var4 = (Number)var1;
            var2 = Tag.FLOAT;
            if (!var4.equals(Double.NaN) && !var4.equals(Float.NaN)) {
               if (!var4.equals(Double.POSITIVE_INFINITY) && !var4.equals(Float.POSITIVE_INFINITY)) {
                  if (!var4.equals(Double.NEGATIVE_INFINITY) && !var4.equals(Float.NEGATIVE_INFINITY)) {
                     var3 = var4.toString();
                  } else {
                     var3 = "-.inf";
                  }
               } else {
                  var3 = ".inf";
               }
            } else {
               var3 = ".nan";
            }
         } else {
            var2 = Tag.INT;
            var3 = var1.toString();
         }

         return StandardRepresenter.this.representScalar(StandardRepresenter.this.getTag(var1.getClass(), var2), var3);
      }
   }

   public class RepresentList implements RepresentToNode {
      public Node representData(Object var1) {
         return StandardRepresenter.this.representSequence(StandardRepresenter.this.getTag(var1.getClass(), Tag.SEQ), (List)var1, StandardRepresenter.this.settings.getDefaultFlowStyle());
      }
   }

   public class RepresentMap implements RepresentToNode {
      public Node representData(Object var1) {
         return StandardRepresenter.this.representMapping(StandardRepresenter.this.getTag(var1.getClass(), Tag.MAP), (Map)var1, StandardRepresenter.this.settings.getDefaultFlowStyle());
      }
   }

   public class RepresentSet implements RepresentToNode {
      public Node representData(Object var1) {
         LinkedHashMap var2 = new LinkedHashMap();
         Set var3 = (Set)var1;
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            Object var5 = var4.next();
            var2.put(var5, (Object)null);
         }

         return StandardRepresenter.this.representMapping(StandardRepresenter.this.getTag(var1.getClass(), Tag.SET), var2, StandardRepresenter.this.settings.getDefaultFlowStyle());
      }
   }

   public class RepresentIterator implements RepresentToNode {
      public Node representData(Object var1) {
         Iterator var2 = (Iterator)var1;
         return StandardRepresenter.this.representSequence(StandardRepresenter.this.getTag(var1.getClass(), Tag.SEQ), new StandardRepresenter.IteratorWrapper(var2), StandardRepresenter.this.settings.getDefaultFlowStyle());
      }
   }

   public class RepresentArray implements RepresentToNode {
      public Node representData(Object var1) {
         Object[] var2 = (Object[])var1;
         List var3 = Arrays.asList(var2);
         return StandardRepresenter.this.representSequence(Tag.SEQ, var3, StandardRepresenter.this.settings.getDefaultFlowStyle());
      }
   }

   public class RepresentEnum implements RepresentToNode {
      public Node representData(Object var1) {
         Tag var2 = new Tag(var1.getClass());
         return StandardRepresenter.this.representScalar(StandardRepresenter.this.getTag(var1.getClass(), var2), ((Enum)var1).name());
      }
   }

   private static class IteratorWrapper implements Iterable<Object> {
      private final Iterator<Object> iter;

      public IteratorWrapper(Iterator<Object> var1) {
         this.iter = var1;
      }

      public Iterator<Object> iterator() {
         return this.iter;
      }
   }
}

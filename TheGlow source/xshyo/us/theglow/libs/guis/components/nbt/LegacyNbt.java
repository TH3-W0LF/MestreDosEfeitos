package xshyo.us.theglow.libs.guis.components.nbt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LegacyNbt implements NbtWrapper {
   public static final String PACKAGE_NAME = Bukkit.getServer().getClass().getPackage().getName();
   public static final String NMS_VERSION;
   private static Method getStringMethod;
   private static Method setStringMethod;
   private static Method setBooleanMethod;
   private static Method hasTagMethod;
   private static Method getTagMethod;
   private static Method setTagMethod;
   private static Method removeTagMethod;
   private static Method asNMSCopyMethod;
   private static Method asBukkitCopyMethod;
   private static Constructor<?> nbtCompoundConstructor;

   public ItemStack setString(@NotNull ItemStack var1, String var2, String var3) {
      if (var1.getType() == Material.AIR) {
         return var1;
      } else {
         Object var4 = asNMSCopy(var1);
         Object var5 = hasTag(var4) ? getTag(var4) : newNBTTagCompound();
         setString(var5, var2, var3);
         setTag(var4, var5);
         return asBukkitCopy(var4);
      }
   }

   public ItemStack removeTag(@NotNull ItemStack var1, String var2) {
      if (var1.getType() == Material.AIR) {
         return var1;
      } else {
         Object var3 = asNMSCopy(var1);
         Object var4 = hasTag(var3) ? getTag(var3) : newNBTTagCompound();
         remove(var4, var2);
         setTag(var3, var4);
         return asBukkitCopy(var3);
      }
   }

   public ItemStack setBoolean(@NotNull ItemStack var1, String var2, boolean var3) {
      if (var1.getType() == Material.AIR) {
         return var1;
      } else {
         Object var4 = asNMSCopy(var1);
         Object var5 = hasTag(var4) ? getTag(var4) : newNBTTagCompound();
         setBoolean(var5, var2, var3);
         setTag(var4, var5);
         return asBukkitCopy(var4);
      }
   }

   @Nullable
   public String getString(@NotNull ItemStack var1, String var2) {
      if (var1.getType() == Material.AIR) {
         return null;
      } else {
         Object var3 = asNMSCopy(var1);
         Object var4 = hasTag(var3) ? getTag(var3) : newNBTTagCompound();
         return getString(var4, var2);
      }
   }

   private static void setString(Object var0, String var1, String var2) {
      try {
         setStringMethod.invoke(var0, var1, var2);
      } catch (InvocationTargetException | IllegalAccessException var4) {
      }

   }

   private static void setBoolean(Object var0, String var1, boolean var2) {
      try {
         setBooleanMethod.invoke(var0, var1, var2);
      } catch (InvocationTargetException | IllegalAccessException var4) {
      }

   }

   private static void remove(Object var0, String var1) {
      try {
         removeTagMethod.invoke(var0, var1);
      } catch (InvocationTargetException | IllegalAccessException var3) {
      }

   }

   private static String getString(Object var0, String var1) {
      try {
         return (String)getStringMethod.invoke(var0, var1);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         return null;
      }
   }

   private static boolean hasTag(Object var0) {
      try {
         return (Boolean)hasTagMethod.invoke(var0);
      } catch (InvocationTargetException | IllegalAccessException var2) {
         return false;
      }
   }

   public static Object getTag(Object var0) {
      try {
         return getTagMethod.invoke(var0);
      } catch (InvocationTargetException | IllegalAccessException var2) {
         return null;
      }
   }

   private static void setTag(Object var0, Object var1) {
      try {
         setTagMethod.invoke(var0, var1);
      } catch (InvocationTargetException | IllegalAccessException var3) {
      }

   }

   private static Object newNBTTagCompound() {
      try {
         return nbtCompoundConstructor.newInstance();
      } catch (InstantiationException | InvocationTargetException | IllegalAccessException var1) {
         return null;
      }
   }

   public static Object asNMSCopy(ItemStack var0) {
      try {
         return asNMSCopyMethod.invoke((Object)null, var0);
      } catch (InvocationTargetException | IllegalAccessException var2) {
         return null;
      }
   }

   public static ItemStack asBukkitCopy(Object var0) {
      try {
         return (ItemStack)asBukkitCopyMethod.invoke((Object)null, var0);
      } catch (InvocationTargetException | IllegalAccessException var2) {
         return null;
      }
   }

   private static Class<?> getNMSClass(String var0) {
      try {
         return Class.forName("net.minecraft.server." + NMS_VERSION + "." + var0);
      } catch (ClassNotFoundException var2) {
         return null;
      }
   }

   private static Class<?> getCraftItemStackClass() {
      try {
         return Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + ".inventory.CraftItemStack");
      } catch (ClassNotFoundException var1) {
         return null;
      }
   }

   static {
      NMS_VERSION = PACKAGE_NAME.substring(PACKAGE_NAME.lastIndexOf(46) + 1);

      try {
         getStringMethod = ((Class)Objects.requireNonNull(getNMSClass("NBTTagCompound"))).getMethod("getString", String.class);
         removeTagMethod = ((Class)Objects.requireNonNull(getNMSClass("NBTTagCompound"))).getMethod("remove", String.class);
         setStringMethod = ((Class)Objects.requireNonNull(getNMSClass("NBTTagCompound"))).getMethod("setString", String.class, String.class);
         setBooleanMethod = ((Class)Objects.requireNonNull(getNMSClass("NBTTagCompound"))).getMethod("setBoolean", String.class, Boolean.TYPE);
         hasTagMethod = ((Class)Objects.requireNonNull(getNMSClass("ItemStack"))).getMethod("hasTag");
         getTagMethod = ((Class)Objects.requireNonNull(getNMSClass("ItemStack"))).getMethod("getTag");
         setTagMethod = ((Class)Objects.requireNonNull(getNMSClass("ItemStack"))).getMethod("setTag", getNMSClass("NBTTagCompound"));
         nbtCompoundConstructor = ((Class)Objects.requireNonNull(getNMSClass("NBTTagCompound"))).getDeclaredConstructor();
         asNMSCopyMethod = ((Class)Objects.requireNonNull(getCraftItemStackClass())).getMethod("asNMSCopy", ItemStack.class);
         asBukkitCopyMethod = ((Class)Objects.requireNonNull(getCraftItemStackClass())).getMethod("asBukkitCopy", getNMSClass("ItemStack"));
      } catch (NoSuchMethodException var1) {
         var1.printStackTrace();
      }

   }
}

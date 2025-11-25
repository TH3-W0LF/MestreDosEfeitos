package me.eplugins.eglow.custommenu.command;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Generated;

public class CommandStorageHelper {
   public static CommandStorageHelper.CommandStorage parse(String message) {
      if (message != null && message.startsWith("/")) {
         int spaceIndex = message.indexOf(32);
         String command;
         String[] args;
         if (spaceIndex == -1) {
            command = message.substring(1);
            args = new String[0];
         } else {
            command = message.substring(1, spaceIndex);
            String argumentsPart = message.substring(spaceIndex + 1);
            args = argumentsPart.split(" ");
         }

         return new CommandStorageHelper.CommandStorage(command, args);
      } else {
         return null;
      }
   }

   public static Map<String, String> mapArgs(Map<String, Integer> argsCount, String[] rawArgs) {
      Map<String, String> mapped = new LinkedHashMap();
      int currentIndex = 0;
      int size = argsCount.size();
      int i = 0;
      Iterator var6 = argsCount.entrySet().iterator();

      while(var6.hasNext()) {
         Entry<String, Integer> entry = (Entry)var6.next();
         String key = (String)entry.getKey();
         int count = (Integer)entry.getValue();
         ++i;
         if (i < size) {
            if (count != 1) {
               throw new IllegalArgumentException("Only last argument can have count higher than 1");
            }

            if (currentIndex >= rawArgs.length) {
               mapped.put(key, "");
            } else {
               mapped.put(key, rawArgs[currentIndex]);
            }

            ++currentIndex;
         } else {
            String joined;
            if (count == -1) {
               if (currentIndex >= rawArgs.length) {
                  mapped.put(key, "");
               } else {
                  joined = String.join(" ", (CharSequence[])Arrays.copyOfRange(rawArgs, currentIndex, rawArgs.length));
                  mapped.put(key, joined);
               }
            } else if (currentIndex + count > rawArgs.length) {
               mapped.put(key, "");
            } else {
               joined = String.join(" ", (CharSequence[])Arrays.copyOfRange(rawArgs, currentIndex, currentIndex + count));
               mapped.put(key, joined);
            }

            currentIndex = rawArgs.length;
         }
      }

      return mapped;
   }

   public static boolean argsMatch(Map<String, Integer> argsCount, String[] rawArgs) {
      int expected = 0;
      boolean hasInfinite = false;
      Iterator var4 = argsCount.values().iterator();

      while(var4.hasNext()) {
         int count = (Integer)var4.next();
         if (count == -1) {
            hasInfinite = true;
         } else {
            expected += count;
         }
      }

      return hasInfinite || expected == rawArgs.length;
   }

   public static class CommandStorage {
      private final String command;
      private final String[] args;

      @Generated
      public String getCommand() {
         return this.command;
      }

      @Generated
      public String[] getArgs() {
         return this.args;
      }

      @Generated
      public CommandStorage(String command, String[] args) {
         this.command = command;
         this.args = args;
      }
   }
}

package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.comments;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.CommentEvent;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events.Event;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.parser.Parser;

public class CommentEventsCollector {
   private final Queue<Event> eventSource;
   private final CommentType[] expectedCommentTypes;
   private List<CommentLine> commentLineList;

   public CommentEventsCollector(final Parser var1, CommentType... var2) {
      this.eventSource = new AbstractQueue<Event>() {
         public boolean offer(Event var1x) {
            throw new UnsupportedOperationException();
         }

         public Event poll() {
            return var1.next();
         }

         public Event peek() {
            return var1.peekEvent();
         }

         public Iterator<Event> iterator() {
            throw new UnsupportedOperationException();
         }

         public int size() {
            throw new UnsupportedOperationException();
         }
      };
      this.expectedCommentTypes = var2;
      this.commentLineList = new ArrayList();
   }

   public CommentEventsCollector(Queue<Event> var1, CommentType... var2) {
      this.eventSource = var1;
      this.expectedCommentTypes = var2;
      this.commentLineList = new ArrayList();
   }

   private boolean isEventExpected(Event var1) {
      if (var1 != null && var1.getEventId() == Event.ID.Comment) {
         CommentEvent var2 = (CommentEvent)var1;
         CommentType[] var3 = this.expectedCommentTypes;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            CommentType var6 = var3[var5];
            if (var2.getCommentType() == var6) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public CommentEventsCollector collectEvents() {
      this.collectEvents((Event)null);
      return this;
   }

   public Event collectEvents(Event var1) {
      if (var1 != null) {
         if (!this.isEventExpected(var1)) {
            return var1;
         }

         this.commentLineList.add(new CommentLine((CommentEvent)var1));
      }

      while(this.isEventExpected((Event)this.eventSource.peek())) {
         this.commentLineList.add(new CommentLine((CommentEvent)this.eventSource.poll()));
      }

      return null;
   }

   public Event collectEventsAndPoll(Event var1) {
      Event var2 = this.collectEvents(var1);
      return var2 != null ? var2 : (Event)this.eventSource.poll();
   }

   public List<CommentLine> consume() {
      List var1;
      try {
         var1 = this.commentLineList;
      } finally {
         this.commentLineList = new ArrayList();
      }

      return var1;
   }

   public boolean isEmpty() {
      return this.commentLineList.isEmpty();
   }
}

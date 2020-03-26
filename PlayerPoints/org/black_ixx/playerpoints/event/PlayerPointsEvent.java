/*    */ package org.black_ixx.playerpoints.event;
/*    */ 
/*    */ import java.util.UUID;
/*    */ import org.bukkit.event.Cancellable;
/*    */ import org.bukkit.event.Event;
/*    */ import org.bukkit.event.HandlerList;
/*    */ 
/*    */ 
/*    */ public class PlayerPointsEvent
/*    */   extends Event
/*    */   implements Cancellable
/*    */ {
/* 13 */   private static final HandlerList handlers = new HandlerList();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private final UUID playerId;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private int change;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private boolean cancelled;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public PlayerPointsEvent(UUID id, int change)
/*    */   {
/* 38 */     this.playerId = id;
/* 39 */     this.change = change;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getChange()
/*    */   {
/* 48 */     return this.change;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setChange(int change)
/*    */   {
/* 59 */     this.change = change;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public UUID getPlayerId()
/*    */   {
/* 68 */     return this.playerId;
/*    */   }
/*    */   
/*    */   public boolean isCancelled()
/*    */   {
/* 73 */     return this.cancelled;
/*    */   }
/*    */   
/*    */   public void setCancelled(boolean cancelled)
/*    */   {
/* 78 */     this.cancelled = cancelled;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static HandlerList getHandlerList()
/*    */   {
/* 87 */     return handlers;
/*    */   }
/*    */   
/*    */   public HandlerList getHandlers()
/*    */   {
/* 92 */     return handlers;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\event\PlayerPointsEvent.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
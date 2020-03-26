/*    */ package org.black_ixx.playerpoints.event;
/*    */ 
/*    */ import java.util.UUID;
/*    */ import org.bukkit.event.HandlerList;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PlayerPointsChangeEvent
/*    */   extends PlayerPointsEvent
/*    */ {
/* 15 */   private static final HandlerList handlers = new HandlerList();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public PlayerPointsChangeEvent(UUID id, int change)
/*    */   {
/* 26 */     super(id, change);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static HandlerList getHandlerList()
/*    */   {
/* 35 */     return handlers;
/*    */   }
/*    */   
/*    */   public HandlerList getHandlers()
/*    */   {
/* 40 */     return handlers;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\event\PlayerPointsChangeEvent.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
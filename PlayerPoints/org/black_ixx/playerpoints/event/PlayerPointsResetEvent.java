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
/*    */ public class PlayerPointsResetEvent
/*    */   extends PlayerPointsEvent
/*    */ {
/* 15 */   private static final HandlerList handlers = new HandlerList();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public PlayerPointsResetEvent(UUID id)
/*    */   {
/* 24 */     super(id, 0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static HandlerList getHandlerList()
/*    */   {
/* 33 */     return handlers;
/*    */   }
/*    */   
/*    */   public HandlerList getHandlers()
/*    */   {
/* 38 */     return handlers;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\event\PlayerPointsResetEvent.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
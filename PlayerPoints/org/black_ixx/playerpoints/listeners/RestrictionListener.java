/*    */ package org.black_ixx.playerpoints.listeners;
/*    */ 
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.config.RootConfig;
/*    */ import org.black_ixx.playerpoints.event.PlayerPointsChangeEvent;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventPriority;
/*    */ import org.bukkit.event.Listener;
/*    */ 
/*    */ public class RestrictionListener implements Listener
/*    */ {
/*    */   private PlayerPoints plugin;
/*    */   
/*    */   public RestrictionListener(PlayerPoints plugin)
/*    */   {
/* 16 */     this.plugin = plugin;
/*    */   }
/*    */   
/*    */   @org.bukkit.event.EventHandler(priority=EventPriority.LOW)
/*    */   public void validatePlayerChangeEvent(PlayerPointsChangeEvent event) {
/* 21 */     RootConfig config = (RootConfig)this.plugin.getModuleForClass(RootConfig.class);
/* 22 */     if (config.hasPlayedBefore) {
/* 23 */       Player player = this.plugin.getServer().getPlayer(event.getPlayerId());
/* 24 */       if (player != null) {
/* 25 */         event.setCancelled(!player.hasPlayedBefore());
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\listeners\RestrictionListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
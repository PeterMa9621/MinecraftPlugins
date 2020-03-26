/*    */ package org.black_ixx.playerpoints.listeners;
/*    */ 
/*    */ import com.vexsoftware.votifier.model.Vote;
/*    */ import com.vexsoftware.votifier.model.VotifierEvent;
/*    */ import java.util.UUID;
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.PlayerPointsAPI;
/*    */ import org.black_ixx.playerpoints.config.RootConfig;
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VotifierListener
/*    */   implements Listener
/*    */ {
/*    */   private PlayerPoints plugin;
/*    */   
/*    */   public VotifierListener(PlayerPoints plugin)
/*    */   {
/* 29 */     this.plugin = plugin;
/*    */   }
/*    */   
/*    */   @EventHandler
/*    */   public void vote(VotifierEvent event) {
/* 34 */     RootConfig config = (RootConfig)this.plugin.getModuleForClass(RootConfig.class);
/* 35 */     if (event.getVote().getUsername() == null) {
/* 36 */       return;
/*    */     }
/* 38 */     String name = event.getVote().getUsername();
/* 39 */     UUID id = this.plugin.translateNameToUUID(name);
/* 40 */     boolean pay = false;
/* 41 */     if (config.voteOnline) {
/* 42 */       Player player = this.plugin.getServer().getPlayer(id);
/* 43 */       if ((player != null) && (player.isOnline())) {
/* 44 */         pay = true;
/* 45 */         player.sendMessage("Thanks for voting on " + event.getVote().getServiceName() + "!");
/*    */         
/* 47 */         player.sendMessage(config.voteAmount + " has been added to your Points balance.");
/*    */       }
/*    */     }
/*    */     else {
/* 51 */       pay = true;
/*    */     }
/* 53 */     if (pay) {
/* 54 */       this.plugin.getAPI().give(id, config.voteAmount);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\listeners\VotifierListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package org.black_ixx.playerpoints.update;
/*    */ 
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.services.version.Version;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class UpdateModule
/*    */   implements Comparable<UpdateModule>
/*    */ {
/*    */   protected final PlayerPoints plugin;
/*    */   protected Version targetVersion;
/*    */   
/*    */   public UpdateModule(PlayerPoints plugin)
/*    */   {
/* 25 */     this.plugin = plugin;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Version getTargetVersion()
/*    */   {
/* 34 */     return this.targetVersion;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean shouldApplyUpdate(Version current)
/*    */   {
/* 46 */     return current.compareTo(this.targetVersion) < 0;
/*    */   }
/*    */   
/*    */   public int compareTo(UpdateModule o)
/*    */   {
/* 51 */     return this.targetVersion.compareTo(o.getTargetVersion());
/*    */   }
/*    */   
/*    */   public abstract void update();
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\update\UpdateModule.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
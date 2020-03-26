/*    */ package org.black_ixx.playerpoints.storage.exports;
/*    */ 
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.storage.StorageGenerator;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class DatabaseExport
/*    */ {
/*    */   protected PlayerPoints plugin;
/*    */   protected StorageGenerator generator;
/*    */   
/*    */   public DatabaseExport(PlayerPoints plugin)
/*    */   {
/* 29 */     this.plugin = plugin;
/* 30 */     this.generator = new StorageGenerator(plugin);
/*    */   }
/*    */   
/*    */   abstract void doExport();
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\exports\DatabaseExport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
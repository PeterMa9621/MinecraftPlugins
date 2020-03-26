/*    */ package org.black_ixx.playerpoints.storage.imports;
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
/*    */ 
/*    */ public abstract class DatabaseImport
/*    */ {
/*    */   protected PlayerPoints plugin;
/*    */   protected StorageGenerator generator;
/*    */   
/*    */   public DatabaseImport(PlayerPoints plugin)
/*    */   {
/* 30 */     this.plugin = plugin;
/* 31 */     this.generator = new StorageGenerator(plugin);
/*    */   }
/*    */   
/*    */   abstract void doImport();
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\imports\DatabaseImport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
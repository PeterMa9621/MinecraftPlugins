/*    */ package org.black_ixx.playerpoints.storage;
/*    */ 
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.storage.models.MySQLStorage;
/*    */ import org.black_ixx.playerpoints.storage.models.SQLiteStorage;
/*    */ import org.black_ixx.playerpoints.storage.models.YAMLStorage;
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
/*    */ public class StorageGenerator
/*    */ {
/*    */   private PlayerPoints plugin;
/*    */   
/*    */   public StorageGenerator(PlayerPoints plugin)
/*    */   {
/* 27 */     this.plugin = plugin;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public IStorage createStorageHandlerForType(StorageType type)
/*    */   {
/* 38 */     IStorage storage = null;
/* 39 */     switch (type) {
/*    */     case YAML: 
/* 41 */       storage = new YAMLStorage(this.plugin);
/* 42 */       break;
/*    */     case SQLITE: 
/* 44 */       storage = new SQLiteStorage(this.plugin);
/* 45 */       break;
/*    */     case MYSQL: 
/* 47 */       storage = new MySQLStorage(this.plugin);
/* 48 */       break;
/*    */     }
/*    */     
/*    */     
/* 52 */     return storage;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\StorageGenerator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
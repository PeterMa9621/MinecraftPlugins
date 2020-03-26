/*    */ package org.black_ixx.playerpoints.storage;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.config.RootConfig;
/*    */ import org.black_ixx.playerpoints.services.IModule;
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
/*    */ public class StorageHandler
/*    */   implements IStorage, IModule
/*    */ {
/*    */   private PlayerPoints plugin;
/*    */   private StorageGenerator generator;
/*    */   IStorage storage;
/*    */   
/*    */   public StorageHandler(PlayerPoints plugin)
/*    */   {
/* 33 */     this.plugin = plugin;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getPoints(String name)
/*    */   {
/* 39 */     return this.storage.getPoints(name);
/*    */   }
/*    */   
/*    */   public boolean setPoints(String name, int points)
/*    */   {
/* 44 */     return this.storage.setPoints(name, points);
/*    */   }
/*    */   
/*    */   public boolean playerEntryExists(String id)
/*    */   {
/* 49 */     return this.storage.playerEntryExists(id);
/*    */   }
/*    */   
/*    */   public boolean removePlayer(String id)
/*    */   {
/* 54 */     return this.storage.removePlayer(id);
/*    */   }
/*    */   
/*    */   public Collection<String> getPlayers()
/*    */   {
/* 59 */     return this.storage.getPlayers();
/*    */   }
/*    */   
/*    */   public void starting()
/*    */   {
/* 64 */     this.generator = new StorageGenerator(this.plugin);
/* 65 */     this.storage = this.generator.createStorageHandlerForType(((RootConfig)this.plugin.getModuleForClass(RootConfig.class)).getStorageType());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void closing() {}
/*    */   
/*    */ 
/*    */   public boolean destroy()
/*    */   {
/* 75 */     return this.storage.destroy();
/*    */   }
/*    */   
/*    */   public boolean build()
/*    */   {
/* 80 */     return this.storage.build();
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\StorageHandler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
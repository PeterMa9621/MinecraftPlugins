/*    */ package org.black_ixx.playerpoints.update.modules;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.UUID;
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.services.version.Version;
/*    */ import org.black_ixx.playerpoints.storage.StorageHandler;
/*    */ import org.black_ixx.playerpoints.update.UpdateModule;
/*    */ 
/*    */ public class TwoZeroZeroUpdate extends UpdateModule
/*    */ {
/* 15 */   private Map<String, Integer> cache = new HashMap();
/*    */   
/*    */   public TwoZeroZeroUpdate(PlayerPoints plugin) {
/* 18 */     super(plugin);
/* 19 */     this.targetVersion = new Version("2.0.0");
/*    */   }
/*    */   
/*    */ 
/*    */   public void update()
/*    */   {
/* 25 */     StorageHandler storageHandler = (StorageHandler)this.plugin.getModuleForClass(StorageHandler.class);
/* 26 */     Collection<String> playerNames = storageHandler.getPlayers();
/* 27 */     for (String playerName : playerNames) {
/* 28 */       this.cache.put(playerName, Integer.valueOf(storageHandler.getPoints(playerName)));
/*    */     }
/*    */     
/* 31 */     storageHandler.destroy();
/* 32 */     storageHandler.build();
/*    */     
/* 34 */     for (Map.Entry<String, Integer> entry : this.cache.entrySet()) {
/* 35 */       UUID id = this.plugin.translateNameToUUID((String)entry.getKey());
/* 36 */       storageHandler.setPoints(id.toString(), ((Integer)entry.getValue()).intValue());
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\update\modules\TwoZeroZeroUpdate.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package org.black_ixx.playerpoints;
/*     */ 
/*     */ import java.util.UUID;
/*     */ import org.black_ixx.playerpoints.event.PlayerPointsChangeEvent;
/*     */ import org.black_ixx.playerpoints.event.PlayerPointsResetEvent;
/*     */ import org.black_ixx.playerpoints.storage.StorageHandler;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PlayerPointsAPI
/*     */ {
/*     */   private final PlayerPoints plugin;
/*     */   
/*     */   public PlayerPointsAPI(PlayerPoints p)
/*     */   {
/*  23 */     this.plugin = p;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean give(UUID playerId, int amount)
/*     */   {
/*  36 */     if (playerId == null) {
/*  37 */       return false;
/*     */     }
/*  39 */     PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playerId, amount);
/*     */     
/*  41 */     this.plugin.getServer().getPluginManager().callEvent(event);
/*  42 */     if (!event.isCancelled()) {
/*  43 */       int total = look(playerId) + event.getChange();
/*     */       
/*  45 */       return ((StorageHandler)this.plugin.getModuleForClass(StorageHandler.class)).setPoints(playerId.toString(), total);
/*     */     }
/*     */     
/*  48 */     return false;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public boolean give(String playerName, int amount) {
/*  53 */     boolean success = false;
/*  54 */     if (playerName != null) {
/*  55 */       success = give(this.plugin.translateNameToUUID(playerName), amount);
/*     */     }
/*  57 */     return success;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean take(UUID playerId, int amount)
/*     */   {
/*  71 */     int points = look(playerId);
/*  72 */     int take = amount;
/*  73 */     if (take > 0) {
/*  74 */       take *= -1;
/*     */     }
/*  76 */     if (points + take < 0) {
/*  77 */       return false;
/*     */     }
/*  79 */     return give(playerId, take);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public boolean take(String playerName, int amount) {
/*  84 */     boolean success = false;
/*  85 */     if (playerName != null) {
/*  86 */       success = take(this.plugin.translateNameToUUID(playerName), amount);
/*     */     }
/*  88 */     return success;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int look(UUID playerId)
/*     */   {
/* 100 */     int amount = 0;
/* 101 */     if (playerId != null) {
/* 102 */       amount = ((StorageHandler)this.plugin.getModuleForClass(StorageHandler.class)).getPoints(playerId.toString());
/*     */     }
/* 104 */     return amount;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public int look(String playerName) {
/* 109 */     return look(this.plugin.translateNameToUUID(playerName));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean pay(UUID source, UUID target, int amount)
/*     */   {
/* 121 */     if (take(source, amount)) {
/* 122 */       if (give(target, amount)) {
/* 123 */         return true;
/*     */       }
/* 125 */       give(source, amount);
/*     */     }
/*     */     
/* 128 */     return false;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public boolean pay(String sourceName, String targetName, int amount) {
/* 133 */     boolean success = false;
/* 134 */     if ((sourceName != null) && (targetName != null)) {
/* 135 */       success = pay(this.plugin.translateNameToUUID(sourceName), this.plugin.translateNameToUUID(targetName), amount);
/*     */     }
/* 137 */     return success;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean set(UUID playerId, int amount)
/*     */   {
/* 150 */     if (playerId == null) {
/* 151 */       return false;
/*     */     }
/* 153 */     PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playerId, amount - look(playerId));
/*     */     
/* 155 */     this.plugin.getServer().getPluginManager().callEvent(event);
/* 156 */     if (!event.isCancelled()) {
/* 157 */       return ((StorageHandler)this.plugin.getModuleForClass(StorageHandler.class)).setPoints(playerId.toString(), look(playerId) + event.getChange());
/*     */     }
/*     */     
/*     */ 
/* 161 */     return false;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public boolean set(String playerName, int amount) {
/* 166 */     boolean success = false;
/* 167 */     if (playerName != null) {
/* 168 */       success = set(this.plugin.translateNameToUUID(playerName), amount);
/*     */     }
/* 170 */     return success;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean reset(UUID playerId)
/*     */   {
/* 181 */     if (playerId == null) {
/* 182 */       return false;
/*     */     }
/* 184 */     PlayerPointsResetEvent event = new PlayerPointsResetEvent(playerId);
/* 185 */     this.plugin.getServer().getPluginManager().callEvent(event);
/* 186 */     if (!event.isCancelled()) {
/* 187 */       return ((StorageHandler)this.plugin.getModuleForClass(StorageHandler.class)).setPoints(playerId.toString(), event.getChange());
/*     */     }
/*     */     
/* 190 */     return false;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public boolean reset(String playerName, int amount) {
/* 195 */     boolean success = false;
/* 196 */     if (playerName != null) {
/* 197 */       success = reset(this.plugin.translateNameToUUID(playerName));
/*     */     }
/* 199 */     return success;
/*     */   }
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\PlayerPointsAPI.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
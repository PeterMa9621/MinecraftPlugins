/*    */ package org.black_ixx.playerpoints.storage.exports;
/*    */ 
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.config.RootConfig;
/*    */ import org.black_ixx.playerpoints.storage.StorageType;
/*    */ import org.bukkit.configuration.file.FileConfiguration;
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
/*    */ public class Exporter
/*    */ {
/*    */   private PlayerPoints plugin;
/*    */   
/*    */   public Exporter(PlayerPoints plugin)
/*    */   {
/* 26 */     this.plugin = plugin;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void checkExport()
/*    */   {
/* 33 */     RootConfig config = (RootConfig)this.plugin.getModuleForClass(RootConfig.class);
/* 34 */     if (config.exportSQL) {
/* 35 */       exportSQL(config.exportSource);
/* 36 */       this.plugin.getConfig().set("mysql.export.use", Boolean.valueOf(false));
/* 37 */       this.plugin.saveConfig();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private void exportSQL(StorageType source)
/*    */   {
/* 48 */     switch (source) {
/*    */     case MYSQL: 
/* 50 */       MySQLExport mysql = new MySQLExport(this.plugin);
/* 51 */       mysql.doExport();
/* 52 */       break;
/*    */     case SQLITE: 
/* 54 */       SQLiteExport sqlite = new SQLiteExport(this.plugin);
/* 55 */       sqlite.doExport();
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\exports\Exporter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
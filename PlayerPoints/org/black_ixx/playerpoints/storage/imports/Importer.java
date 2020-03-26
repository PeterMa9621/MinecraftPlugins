/*    */ package org.black_ixx.playerpoints.storage.imports;
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
/*    */ public class Importer
/*    */ {
/*    */   private PlayerPoints plugin;
/*    */   
/*    */   public Importer(PlayerPoints plugin)
/*    */   {
/* 26 */     this.plugin = plugin;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void checkImport()
/*    */   {
/* 33 */     RootConfig config = (RootConfig)this.plugin.getModuleForClass(RootConfig.class);
/* 34 */     if ((config.importSQL) && (config.getStorageType() == StorageType.MYSQL))
/*    */     {
/* 36 */       importSQL(config.importSource);
/* 37 */       this.plugin.getConfig().set("mysql.import.use", Boolean.valueOf(false));
/* 38 */       this.plugin.saveConfig();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private void importSQL(StorageType source)
/*    */   {
/* 49 */     switch (source) {
/*    */     case YAML: 
/* 51 */       YAMLImport yaml = new YAMLImport(this.plugin);
/* 52 */       yaml.doImport();
/* 53 */       break;
/*    */     case SQLITE: 
/* 55 */       SQLiteImport sqlite = new SQLiteImport(this.plugin);
/* 56 */       sqlite.doImport();
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\imports\Importer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
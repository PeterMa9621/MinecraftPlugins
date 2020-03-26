/*    */ package org.black_ixx.playerpoints.storage.imports;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.logging.Logger;
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.storage.IStorage;
/*    */ import org.black_ixx.playerpoints.storage.StorageGenerator;
/*    */ import org.black_ixx.playerpoints.storage.StorageType;
/*    */ import org.bukkit.configuration.ConfigurationSection;
/*    */ import org.bukkit.configuration.file.YamlConfiguration;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class YAMLImport
/*    */   extends DatabaseImport
/*    */ {
/*    */   public YAMLImport(PlayerPoints plugin)
/*    */   {
/* 25 */     super(plugin);
/*    */   }
/*    */   
/*    */   void doImport()
/*    */   {
/* 30 */     this.plugin.getLogger().info("Importing YAML to MySQL");
/* 31 */     IStorage mysql = this.generator.createStorageHandlerForType(StorageType.MYSQL);
/*    */     
/* 33 */     ConfigurationSection config = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder().getAbsolutePath() + "/storage.yml"));
/*    */     
/*    */ 
/* 36 */     ConfigurationSection points = config.getConfigurationSection("Points");
/*    */     
/* 38 */     for (String key : points.getKeys(false)) {
/* 39 */       mysql.setPoints(key, points.getInt(key));
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\imports\YAMLImport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
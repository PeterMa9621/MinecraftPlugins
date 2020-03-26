/*    */ package org.black_ixx.playerpoints.storage.exports;
/*    */ 
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ import lib.PatPeter.SQLibrary.MySQL;
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.config.RootConfig;
/*    */ import org.black_ixx.playerpoints.storage.IStorage;
/*    */ import org.black_ixx.playerpoints.storage.StorageGenerator;
/*    */ import org.black_ixx.playerpoints.storage.StorageType;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MySQLExport
/*    */   extends DatabaseExport
/*    */ {
/*    */   private MySQL mysql;
/*    */   
/*    */   public MySQLExport(PlayerPoints plugin)
/*    */   {
/* 27 */     super(plugin);
/* 28 */     RootConfig config = (RootConfig)plugin.getModuleForClass(RootConfig.class);
/* 29 */     this.mysql = new MySQL(plugin.getLogger(), " ", config.host, Integer.valueOf(config.port).intValue(), config.database, config.user, config.password);
/*    */     
/*    */ 
/*    */ 
/* 33 */     this.mysql.open();
/*    */   }
/*    */   
/*    */   void doExport()
/*    */   {
/* 38 */     RootConfig config = (RootConfig)this.plugin.getModuleForClass(RootConfig.class);
/* 39 */     IStorage yaml = this.generator.createStorageHandlerForType(StorageType.YAML);
/* 40 */     ResultSet query = null;
/*    */     try {
/* 42 */       query = this.mysql.query(String.format("SELECT * FROM %s", new Object[] { config.table }));
/* 43 */       if (query.next()) {
/*    */         do {
/* 45 */           yaml.setPoints(query.getString("playername"), query.getInt("points"));
/*    */         }
/* 47 */         while (query.next());
/*    */       }
/* 49 */       query.close();
/*    */     } catch (SQLException e) {
/* 51 */       this.plugin.getLogger().log(Level.SEVERE, "SQLException on MySQL export", e);
/*    */     }
/*    */     finally {
/* 54 */       this.mysql.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\exports\MySQLExport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
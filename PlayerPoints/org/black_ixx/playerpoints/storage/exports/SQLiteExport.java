/*    */ package org.black_ixx.playerpoints.storage.exports;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ import lib.PatPeter.SQLibrary.SQLite;
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SQLiteExport
/*    */   extends DatabaseExport
/*    */ {
/*    */   private SQLite sqlite;
/*    */   
/*    */   public SQLiteExport(PlayerPoints plugin)
/*    */   {
/* 32 */     super(plugin);
/* 33 */     this.sqlite = new SQLite(plugin.getLogger(), " ", plugin.getDataFolder().getAbsolutePath(), "storage");
/*    */     
/* 35 */     this.sqlite.open();
/*    */   }
/*    */   
/*    */   void doExport()
/*    */   {
/* 40 */     IStorage yaml = this.generator.createStorageHandlerForType(StorageType.YAML);
/* 41 */     ResultSet query = null;
/*    */     try {
/* 43 */       query = this.sqlite.query("SELECT * FROM playerpoints");
/* 44 */       if (query.next()) {
/*    */         do {
/* 46 */           yaml.setPoints(query.getString("playername"), query.getInt("points"));
/*    */         }
/* 48 */         while (query.next());
/*    */       }
/* 50 */       query.close();
/*    */     } catch (SQLException e) {
/* 52 */       this.plugin.getLogger().log(Level.SEVERE, "SQLException on SQLite export", e);
/*    */     }
/*    */     finally {
/* 55 */       this.sqlite.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\exports\SQLiteExport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
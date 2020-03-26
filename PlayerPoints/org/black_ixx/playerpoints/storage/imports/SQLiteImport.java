/*    */ package org.black_ixx.playerpoints.storage.imports;
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
/*    */ public class SQLiteImport
/*    */   extends DatabaseImport
/*    */ {
/*    */   private SQLite sqlite;
/*    */   
/*    */   public SQLiteImport(PlayerPoints plugin)
/*    */   {
/* 32 */     super(plugin);
/* 33 */     this.sqlite = new SQLite(plugin.getLogger(), " ", plugin.getDataFolder().getAbsolutePath(), "storage");
/*    */     
/* 35 */     this.sqlite.open();
/*    */   }
/*    */   
/*    */   void doImport()
/*    */   {
/* 40 */     this.plugin.getLogger().info("Importing SQLite to MySQL");
/* 41 */     IStorage mysql = this.generator.createStorageHandlerForType(StorageType.MYSQL);
/*    */     
/* 43 */     ResultSet query = null;
/*    */     try {
/* 45 */       query = this.sqlite.query("SELECT * FROM playerpoints");
/* 46 */       if (query.next()) {
/*    */         do {
/* 48 */           mysql.setPoints(query.getString("playername"), query.getInt("points"));
/*    */         }
/* 50 */         while (query.next());
/*    */       }
/* 52 */       query.close();
/*    */     } catch (SQLException e) {
/* 54 */       this.plugin.getLogger().log(Level.SEVERE, "SQLException on SQLite import", e);
/*    */     }
/*    */     finally {
/* 57 */       this.sqlite.close();
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\imports\SQLiteImport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package org.black_ixx.playerpoints.storage;
/*    */ 
/*    */ import java.sql.PreparedStatement;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.config.RootConfig;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class DatabaseStorage
/*    */   implements IStorage
/*    */ {
/*    */   protected PlayerPoints plugin;
/* 23 */   protected String GET_POINTS = "SELECT points FROM %s WHERE playername=?;";
/*    */   
/*    */ 
/*    */ 
/* 27 */   protected String GET_PLAYERS = "SELECT %s FROM playerpoints;";
/*    */   
/*    */ 
/*    */ 
/* 31 */   protected String INSERT_PLAYER = "INSERT INTO %s (points,playername) VALUES(?,?);";
/*    */   
/*    */ 
/*    */ 
/* 35 */   protected String UPDATE_PLAYER = "UPDATE %s SET points=? WHERE playername=?";
/*    */   
/*    */ 
/*    */ 
/* 39 */   protected String REMOVE_PLAYER = "DELETE %s WHERE playername=?";
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DatabaseStorage(PlayerPoints plugin)
/*    */   {
/* 48 */     this.plugin = plugin;
/*    */   }
/*    */   
/*    */   protected void SetupQueries(String tableName) {
/* 52 */     this.GET_POINTS = String.format(this.GET_POINTS, new Object[] { tableName });
/* 53 */     this.GET_PLAYERS = String.format(this.GET_PLAYERS, new Object[] { tableName });
/* 54 */     this.INSERT_PLAYER = String.format(this.INSERT_PLAYER, new Object[] { tableName });
/* 55 */     this.UPDATE_PLAYER = String.format(this.UPDATE_PLAYER, new Object[] { tableName });
/* 56 */     this.REMOVE_PLAYER = String.format(this.REMOVE_PLAYER, new Object[] { tableName });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void cleanup(ResultSet result, PreparedStatement statement)
/*    */   {
/* 68 */     RootConfig config = (RootConfig)this.plugin.getModuleForClass(RootConfig.class);
/* 69 */     if (config.debugDatabase) {
/* 70 */       this.plugin.getLogger().info("cleanup()");
/*    */     }
/* 72 */     if (result != null) {
/*    */       try {
/* 74 */         result.close();
/*    */       } catch (SQLException e) {
/* 76 */         this.plugin.getLogger().log(Level.SEVERE, "SQLException on cleanup", e);
/*    */       }
/*    */     }
/*    */     
/* 80 */     if (statement != null) {
/*    */       try {
/* 82 */         statement.close();
/*    */       } catch (SQLException e) {
/* 84 */         this.plugin.getLogger().log(Level.SEVERE, "SQLException on cleanup", e);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\DatabaseStorage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
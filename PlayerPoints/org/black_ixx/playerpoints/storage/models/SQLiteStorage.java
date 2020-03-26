/*     */ package org.black_ixx.playerpoints.storage.models;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import lib.PatPeter.SQLibrary.SQLite;
/*     */ import org.black_ixx.playerpoints.PlayerPoints;
/*     */ import org.black_ixx.playerpoints.storage.DatabaseStorage;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SQLiteStorage
/*     */   extends DatabaseStorage
/*     */ {
/*     */   private SQLite sqlite;
/*     */   
/*     */   public SQLiteStorage(PlayerPoints plugin)
/*     */   {
/*  34 */     super(plugin);
/*  35 */     this.sqlite = new SQLite(plugin.getLogger(), " ", plugin.getDataFolder().getAbsolutePath(), "storage");
/*     */     
/*  37 */     this.sqlite.open();
/*  38 */     SetupQueries("playerpoints");
/*  39 */     if (!this.sqlite.isTable("playerpoints")) {
/*  40 */       build();
/*     */     }
/*     */   }
/*     */   
/*     */   public int getPoints(String name)
/*     */   {
/*  46 */     int points = 0;
/*  47 */     if ((name == null) || (name.equals(""))) {
/*  48 */       return points;
/*     */     }
/*  50 */     PreparedStatement statement = null;
/*  51 */     ResultSet result = null;
/*     */     try {
/*  53 */       statement = this.sqlite.prepare(this.GET_POINTS);
/*  54 */       statement.setString(1, name);
/*  55 */       result = this.sqlite.query(statement);
/*  56 */       if ((result != null) && (result.next())) {
/*  57 */         points = result.getInt("points");
/*     */       }
/*     */     } catch (SQLException e) {
/*  60 */       this.plugin.getLogger().log(Level.SEVERE, "Could not create getter statement.", e);
/*     */     }
/*     */     finally {
/*  63 */       cleanup(result, statement);
/*     */     }
/*  65 */     return points;
/*     */   }
/*     */   
/*     */   public boolean setPoints(String name, int points)
/*     */   {
/*  70 */     boolean value = false;
/*  71 */     if ((name == null) || (name.equals(""))) {
/*  72 */       return value;
/*     */     }
/*  74 */     boolean exists = playerEntryExists(name);
/*  75 */     PreparedStatement statement = null;
/*  76 */     ResultSet result = null;
/*     */     try {
/*  78 */       if (exists) {
/*  79 */         statement = this.sqlite.prepare(this.UPDATE_PLAYER);
/*     */       } else {
/*  81 */         statement = this.sqlite.prepare(this.INSERT_PLAYER);
/*     */       }
/*  83 */       statement.setInt(1, points);
/*  84 */       statement.setString(2, name);
/*  85 */       result = this.sqlite.query(statement);
/*  86 */       value = true;
/*     */     } catch (SQLException e) {
/*  88 */       this.plugin.getLogger().log(Level.SEVERE, "Could not create setter statement.", e);
/*     */     }
/*     */     finally {
/*  91 */       cleanup(result, statement);
/*     */     }
/*  93 */     return value;
/*     */   }
/*     */   
/*     */   public boolean playerEntryExists(String name)
/*     */   {
/*  98 */     boolean has = false;
/*  99 */     if ((name == null) || (name.equals(""))) {
/* 100 */       return has;
/*     */     }
/* 102 */     PreparedStatement statement = null;
/* 103 */     ResultSet result = null;
/*     */     try {
/* 105 */       statement = this.sqlite.prepare(this.GET_POINTS);
/* 106 */       statement.setString(1, name);
/* 107 */       result = this.sqlite.query(statement);
/* 108 */       if (result.next()) {
/* 109 */         has = true;
/*     */       }
/*     */     } catch (SQLException e) {
/* 112 */       this.plugin.getLogger().log(Level.SEVERE, "Could not create player check statement.", e);
/*     */     }
/*     */     finally {
/* 115 */       cleanup(result, statement);
/*     */     }
/* 117 */     return has;
/*     */   }
/*     */   
/*     */   public boolean removePlayer(String id)
/*     */   {
/* 122 */     boolean deleted = false;
/* 123 */     if ((id == null) || (id.equals(""))) {
/* 124 */       return deleted;
/*     */     }
/* 126 */     PreparedStatement statement = null;
/* 127 */     ResultSet result = null;
/*     */     try {
/* 129 */       statement = this.sqlite.prepare(this.REMOVE_PLAYER);
/* 130 */       statement.setString(1, id);
/* 131 */       result = this.sqlite.query(statement);
/* 132 */       deleted = true;
/*     */     } catch (SQLException e) {
/* 134 */       this.plugin.getLogger().log(Level.SEVERE, "Could not create player remove statement.", e);
/*     */     }
/*     */     finally {
/* 137 */       cleanup(result, statement);
/*     */     }
/* 139 */     return deleted;
/*     */   }
/*     */   
/*     */   public Collection<String> getPlayers()
/*     */   {
/* 144 */     Collection<String> players = new HashSet();
/*     */     
/* 146 */     PreparedStatement statement = null;
/* 147 */     ResultSet result = null;
/*     */     try {
/* 149 */       statement = this.sqlite.prepare(this.GET_PLAYERS);
/* 150 */       result = this.sqlite.query(statement);
/*     */       
/* 152 */       while (result.next()) {
/* 153 */         String name = result.getString("playername");
/* 154 */         if (name != null) {
/* 155 */           players.add(name);
/*     */         }
/*     */       }
/*     */     } catch (SQLException e) {
/* 159 */       this.plugin.getLogger().log(Level.SEVERE, "Could not create get players statement.", e);
/*     */     }
/*     */     finally {
/* 162 */       cleanup(result, statement);
/*     */     }
/*     */     
/* 165 */     return players;
/*     */   }
/*     */   
/*     */   public boolean destroy()
/*     */   {
/* 170 */     boolean success = false;
/* 171 */     this.plugin.getLogger().info("Creating playerpoints table");
/*     */     try {
/* 173 */       this.sqlite.query("DROP TABLE playerpoints;");
/* 174 */       success = true;
/*     */     } catch (SQLException e) {
/* 176 */       this.plugin.getLogger().log(Level.SEVERE, "Could not drop SQLite table.", e);
/*     */     }
/*     */     
/* 179 */     return success;
/*     */   }
/*     */   
/*     */   public boolean build()
/*     */   {
/* 184 */     boolean success = false;
/* 185 */     this.plugin.getLogger().info("Creating playerpoints table");
/*     */     try {
/* 187 */       this.sqlite.query("CREATE TABLE playerpoints (id INTEGER PRIMARY KEY, playername varchar(36) NOT NULL, points INTEGER NOT NULL, UNIQUE(playername));");
/* 188 */       success = true;
/*     */     } catch (SQLException e) {
/* 190 */       this.plugin.getLogger().log(Level.SEVERE, "Could not create SQLite table.", e);
/*     */     }
/*     */     
/* 193 */     return success;
/*     */   }
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\models\SQLiteStorage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
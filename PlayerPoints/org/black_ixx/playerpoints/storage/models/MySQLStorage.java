/*     */ package org.black_ixx.playerpoints.storage.models;
/*     */ 
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import lib.PatPeter.SQLibrary.MySQL;
/*     */ import org.black_ixx.playerpoints.PlayerPoints;
/*     */ import org.black_ixx.playerpoints.config.RootConfig;
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
/*     */ public class MySQLStorage
/*     */   extends DatabaseStorage
/*     */ {
/*     */   private MySQL mysql;
/*  30 */   private int retryLimit = 10;
/*     */   
/*     */ 
/*     */ 
/*     */   private String tableName;
/*     */   
/*     */ 
/*     */ 
/*  38 */   private int retryCount = 0;
/*     */   
/*     */ 
/*     */ 
/*  42 */   private boolean skip = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MySQLStorage(PlayerPoints plugin)
/*     */   {
/*  51 */     super(plugin);
/*  52 */     RootConfig config = (RootConfig)plugin.getModuleForClass(RootConfig.class);
/*  53 */     if (config.debugDatabase) {
/*  54 */       plugin.getLogger().info("Constructor");
/*     */     }
/*  56 */     this.retryLimit = config.retryLimit;
/*     */     
/*  58 */     this.tableName = config.table;
/*  59 */     SetupQueries(this.tableName);
/*     */     
/*  61 */     connect();
/*  62 */     if (!this.mysql.isTable(this.tableName)) {
/*  63 */       build();
/*     */     }
/*     */   }
/*     */   
/*     */   public int getPoints(String id)
/*     */   {
/*  69 */     int points = 0;
/*  70 */     RootConfig config = (RootConfig)this.plugin.getModuleForClass(RootConfig.class);
/*  71 */     if ((id == null) || (id.equals(""))) {
/*  72 */       if (config.debugDatabase) {
/*  73 */         this.plugin.getLogger().info("getPoints() - bad ID");
/*     */       }
/*  75 */       return points;
/*     */     }
/*  77 */     if (config.debugDatabase) {
/*  78 */       this.plugin.getLogger().info("getPoints(" + id + ")");
/*     */     }
/*  80 */     PreparedStatement statement = null;
/*  81 */     ResultSet result = null;
/*     */     try {
/*  83 */       statement = this.mysql.prepare(this.GET_POINTS);
/*  84 */       statement.setString(1, id);
/*  85 */       result = this.mysql.query(statement);
/*  86 */       if ((result != null) && (result.next())) {
/*  87 */         points = result.getInt("points");
/*     */       }
/*     */     } catch (SQLException e) {
/*  90 */       this.plugin.getLogger().log(Level.SEVERE, "Could not create getter statement.", e);
/*     */       
/*  92 */       this.retryCount += 1;
/*  93 */       connect();
/*  94 */       if (!this.skip) {
/*  95 */         points = getPoints(id);
/*     */       }
/*     */     } finally {
/*  98 */       cleanup(result, statement);
/*     */     }
/* 100 */     this.retryCount = 0;
/* 101 */     if (config.debugDatabase) {
/* 102 */       this.plugin.getLogger().info("getPlayers() result - " + points);
/*     */     }
/* 104 */     return points;
/*     */   }
/*     */   
/*     */   public boolean setPoints(String id, int points)
/*     */   {
/* 109 */     boolean value = false;
/* 110 */     RootConfig config = (RootConfig)this.plugin.getModuleForClass(RootConfig.class);
/* 111 */     if ((id == null) || (id.equals(""))) {
/* 112 */       if (config.debugDatabase) {
/* 113 */         this.plugin.getLogger().info("setPoints() - bad ID");
/*     */       }
/* 115 */       return value;
/*     */     }
/* 117 */     if (config.debugDatabase) {
/* 118 */       this.plugin.getLogger().info("setPoints(" + id + "," + points + ")");
/*     */     }
/* 120 */     boolean exists = playerEntryExists(id);
/* 121 */     PreparedStatement statement = null;
/* 122 */     ResultSet result = null;
/*     */     try {
/* 124 */       if (exists) {
/* 125 */         statement = this.mysql.prepare(this.UPDATE_PLAYER);
/*     */       } else {
/* 127 */         statement = this.mysql.prepare(this.INSERT_PLAYER);
/*     */       }
/* 129 */       statement.setInt(1, points);
/* 130 */       statement.setString(2, id);
/* 131 */       result = this.mysql.query(statement);
/* 132 */       value = true;
/*     */     } catch (SQLException e) {
/* 134 */       this.plugin.getLogger().log(Level.SEVERE, "Could not create setter statement.", e);
/*     */       
/* 136 */       this.retryCount += 1;
/* 137 */       connect();
/* 138 */       if (!this.skip) {
/* 139 */         value = setPoints(id, points);
/*     */       }
/*     */     } finally {
/* 142 */       cleanup(result, statement);
/*     */     }
/* 144 */     this.retryCount = 0;
/* 145 */     if (config.debugDatabase) {
/* 146 */       this.plugin.getLogger().info("setPoints() result - " + value);
/*     */     }
/* 148 */     return value;
/*     */   }
/*     */   
/*     */   public boolean playerEntryExists(String id)
/*     */   {
/* 153 */     boolean has = false;
/* 154 */     RootConfig config = (RootConfig)this.plugin.getModuleForClass(RootConfig.class);
/* 155 */     if ((id == null) || (id.equals(""))) {
/* 156 */       if (config.debugDatabase) {
/* 157 */         this.plugin.getLogger().info("playerEntryExists() - bad ID");
/*     */       }
/* 159 */       return has;
/*     */     }
/* 161 */     if (config.debugDatabase) {
/* 162 */       this.plugin.getLogger().info("playerEntryExists(" + id + ")");
/*     */     }
/* 164 */     PreparedStatement statement = null;
/* 165 */     ResultSet result = null;
/*     */     try {
/* 167 */       statement = this.mysql.prepare(this.GET_POINTS);
/* 168 */       statement.setString(1, id);
/* 169 */       result = this.mysql.query(statement);
/* 170 */       if (result.next()) {
/* 171 */         has = true;
/*     */       }
/*     */     } catch (SQLException e) {
/* 174 */       this.plugin.getLogger().log(Level.SEVERE, "Could not create player check statement.", e);
/*     */       
/* 176 */       this.retryCount += 1;
/* 177 */       connect();
/* 178 */       if (!this.skip) {
/* 179 */         has = playerEntryExists(id);
/*     */       }
/*     */     } finally {
/* 182 */       cleanup(result, statement);
/*     */     }
/* 184 */     this.retryCount = 0;
/* 185 */     if (config.debugDatabase) {
/* 186 */       this.plugin.getLogger().info("playerEntryExists() result - " + has);
/*     */     }
/* 188 */     return has;
/*     */   }
/*     */   
/*     */   public boolean removePlayer(String id)
/*     */   {
/* 193 */     boolean deleted = false;
/* 194 */     if ((id == null) || (id.equals(""))) {
/* 195 */       return deleted;
/*     */     }
/* 197 */     PreparedStatement statement = null;
/* 198 */     ResultSet result = null;
/* 199 */     RootConfig config = (RootConfig)this.plugin.getModuleForClass(RootConfig.class);
/* 200 */     if (config.debugDatabase) {
/* 201 */       this.plugin.getLogger().info("removePlayers(" + id + ")");
/*     */     }
/*     */     try {
/* 204 */       statement = this.mysql.prepare(this.REMOVE_PLAYER);
/* 205 */       statement.setString(1, id);
/* 206 */       result = this.mysql.query(statement);
/* 207 */       deleted = true;
/*     */     } catch (SQLException e) {
/* 209 */       this.plugin.getLogger().log(Level.SEVERE, "Could not create player remove statement.", e);
/*     */       
/* 211 */       this.retryCount += 1;
/* 212 */       connect();
/* 213 */       if (!this.skip) {
/* 214 */         deleted = playerEntryExists(id);
/*     */       }
/*     */     } finally {
/* 217 */       cleanup(result, statement);
/*     */     }
/* 219 */     this.retryCount = 0;
/* 220 */     if (config.debugDatabase) {
/* 221 */       this.plugin.getLogger().info("renovePlayers() result - " + deleted);
/*     */     }
/* 223 */     return deleted;
/*     */   }
/*     */   
/*     */   public Collection<String> getPlayers()
/*     */   {
/* 228 */     Collection<String> players = new HashSet();
/*     */     
/* 230 */     RootConfig config = (RootConfig)this.plugin.getModuleForClass(RootConfig.class);
/* 231 */     if (config.debugDatabase) {
/* 232 */       this.plugin.getLogger().info("Attempting getPlayers()");
/*     */     }
/* 234 */     PreparedStatement statement = null;
/* 235 */     ResultSet result = null;
/*     */     try {
/* 237 */       statement = this.mysql.prepare(this.GET_PLAYERS);
/* 238 */       result = this.mysql.query(statement);
/*     */       
/* 240 */       while (result.next()) {
/* 241 */         String name = result.getString("playername");
/* 242 */         if (name != null) {
/* 243 */           players.add(name);
/*     */         }
/*     */       }
/*     */     } catch (SQLException e) {
/* 247 */       this.plugin.getLogger().log(Level.SEVERE, "Could not create get players statement.", e);
/*     */       
/* 249 */       this.retryCount += 1;
/* 250 */       connect();
/* 251 */       if (!this.skip) {
/* 252 */         players.clear();
/* 253 */         players.addAll(getPlayers());
/*     */       }
/*     */     } finally {
/* 256 */       cleanup(result, statement);
/*     */     }
/* 258 */     this.retryCount = 0;
/* 259 */     if (config.debugDatabase) {
/* 260 */       this.plugin.getLogger().info("getPlayers() result - " + players.size());
/*     */     }
/* 262 */     return players;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void connect()
/*     */   {
/* 269 */     RootConfig config = (RootConfig)this.plugin.getModuleForClass(RootConfig.class);
/* 270 */     if (this.mysql != null) {
/* 271 */       if (config.debugDatabase) {
/* 272 */         this.plugin.getLogger().info("Closing existing MySQL connection");
/*     */       }
/* 274 */       this.mysql.close();
/*     */     }
/* 276 */     this.mysql = new MySQL(this.plugin.getLogger(), " ", config.host, Integer.valueOf(config.port).intValue(), config.database, config.user, config.password);
/*     */     
/*     */ 
/* 279 */     if (config.debugDatabase) {
/* 280 */       this.plugin.getLogger().info("Attempting MySQL connection to " + config.user + "@" + config.host + ":" + config.port + "/" + config.database);
/*     */     }
/* 282 */     if (this.retryCount < this.retryLimit) {
/* 283 */       this.mysql.open();
/*     */     } else {
/* 285 */       this.plugin.getLogger().severe("Tried connecting to MySQL " + this.retryLimit + " times and could not connect.");
/*     */       
/*     */ 
/* 288 */       this.plugin.getLogger().severe("It may be in your best interest to restart the plugin / server.");
/*     */       
/* 290 */       this.retryCount = 0;
/* 291 */       this.skip = true;
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean destroy()
/*     */   {
/* 297 */     boolean success = false;
/* 298 */     RootConfig config = (RootConfig)this.plugin.getModuleForClass(RootConfig.class);
/* 299 */     if (config.debugDatabase) {
/* 300 */       this.plugin.getLogger().info("Dropping playerpoints table");
/*     */     }
/*     */     try {
/* 303 */       this.mysql.query(String.format("DROP TABLE %s;", new Object[] { this.tableName }));
/* 304 */       success = true;
/*     */     } catch (SQLException e) {
/* 306 */       this.plugin.getLogger().log(Level.SEVERE, "Could not drop MySQL table.", e);
/*     */     }
/*     */     
/* 309 */     return success;
/*     */   }
/*     */   
/*     */   public boolean build()
/*     */   {
/* 314 */     boolean success = false;
/* 315 */     RootConfig config = (RootConfig)this.plugin.getModuleForClass(RootConfig.class);
/* 316 */     if (config.debugDatabase) {
/* 317 */       this.plugin.getLogger().info(String.format("Creating %s table", new Object[] { this.tableName }));
/*     */     }
/*     */     try {
/* 320 */       this.mysql.query(String.format("CREATE TABLE %s (id INT UNSIGNED NOT NULL AUTO_INCREMENT, playername varchar(36) NOT NULL, points INT NOT NULL, PRIMARY KEY(id), UNIQUE(playername));", new Object[] { this.tableName }));
/* 321 */       success = true;
/*     */     } catch (SQLException e) {
/* 323 */       this.plugin.getLogger().log(Level.SEVERE, "Could not create MySQL table.", e);
/*     */     }
/*     */     
/* 326 */     return success;
/*     */   }
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\models\MySQLStorage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
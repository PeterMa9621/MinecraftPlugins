/*     */ package org.black_ixx.playerpoints.config;
/*     */ 
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.black_ixx.playerpoints.PlayerPoints;
/*     */ import org.black_ixx.playerpoints.services.IModule;
/*     */ import org.black_ixx.playerpoints.storage.StorageType;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
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
/*     */ public class RootConfig
/*     */   implements IModule
/*     */ {
/*     */   private PlayerPoints plugin;
/*     */   public String host;
/*     */   public String port;
/*     */   public String database;
/*     */   public String user;
/*     */   public String password;
/*     */   public String table;
/*     */   public int voteAmount;
/*     */   public int retryLimit;
/*     */   public boolean importSQL;
/*     */   public boolean exportSQL;
/*     */   public boolean voteOnline;
/*     */   public boolean voteEnabled;
/*     */   public boolean vault;
/*     */   public boolean hasPlayedBefore;
/*     */   public boolean autocompleteOnline;
/*     */   public boolean debugDatabase;
/*     */   public boolean debugUUID;
/*     */   public StorageType backend;
/*     */   public StorageType importSource;
/*     */   public StorageType exportSource;
/*     */   
/*     */   public RootConfig(PlayerPoints plugin)
/*     */   {
/*  48 */     this.plugin = plugin;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reloadConfig()
/*     */   {
/*  56 */     this.plugin.reloadConfig();
/*     */     
/*  58 */     ConfigurationSection config = this.plugin.getConfig();
/*     */     
/*  60 */     loadSettings(config);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void loadSettings(ConfigurationSection config)
/*     */   {
/*  70 */     this.debugDatabase = config.getBoolean("debug.database", false);
/*  71 */     this.debugUUID = config.getBoolean("debug.uuid", false);
/*  72 */     this.voteEnabled = config.getBoolean("vote.enabled", false);
/*  73 */     this.voteAmount = config.getInt("vote.amount", 100);
/*  74 */     this.voteOnline = config.getBoolean("vote.online", false);
/*  75 */     this.vault = config.getBoolean("vault", false);
/*  76 */     this.hasPlayedBefore = config.getBoolean("restrictions.hasPlayedBefore", false);
/*     */     
/*  78 */     this.autocompleteOnline = config.getBoolean("restrictions.autocompleteOnline", false);
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
/*     */   private void loadStorageSettings(ConfigurationSection config)
/*     */   {
/*  91 */     String back = config.getString("storage");
/*  92 */     if (back.equalsIgnoreCase("sqlite")) {
/*  93 */       this.backend = StorageType.SQLITE;
/*  94 */     } else if (back.equalsIgnoreCase("mysql")) {
/*  95 */       this.backend = StorageType.MYSQL;
/*     */     } else {
/*  97 */       this.backend = StorageType.YAML;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 102 */     this.host = config.getString("mysql.host", "localhost");
/* 103 */     this.port = config.getString("mysql.port", "3306");
/* 104 */     this.database = config.getString("mysql.database", "minecraft");
/* 105 */     this.user = config.getString("mysql.user", "user");
/* 106 */     this.password = config.getString("mysql.password", "password");
/* 107 */     this.table = config.getString("mysql.table", "playerpoints");
/* 108 */     this.importSQL = config.getBoolean("mysql.import.use", false);
/* 109 */     this.retryLimit = config.getInt("mysql.retry", 10);
/* 110 */     this.exportSQL = config.getBoolean("mysql.export.use", false);
/* 111 */     String databaseImportSource = config.getString("mysql.import.source", "YAML");
/*     */     
/* 113 */     if (databaseImportSource.equalsIgnoreCase("SQLITE")) {
/* 114 */       this.importSource = StorageType.SQLITE;
/*     */     } else {
/* 116 */       this.importSource = StorageType.YAML;
/*     */     }
/* 118 */     String databaseExportSource = config.getString("mysql.export.source", "MYSQL");
/*     */     
/* 120 */     if (databaseExportSource.equalsIgnoreCase("SQLITE")) {
/* 121 */       this.exportSource = StorageType.SQLITE;
/*     */     } else {
/* 123 */       this.exportSource = StorageType.MYSQL;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public StorageType getStorageType()
/*     */   {
/* 133 */     return this.backend;
/*     */   }
/*     */   
/*     */ 
/*     */   public void starting()
/*     */   {
/* 139 */     ConfigurationSection config = this.plugin.getConfig();
/*     */     
/* 141 */     Map<String, Object> defaults = new LinkedHashMap();
/* 142 */     defaults.put("storage", "YAML");
/* 143 */     defaults.put("mysql.host", "localhost");
/* 144 */     defaults.put("mysql.port", Integer.valueOf(3306));
/* 145 */     defaults.put("mysql.database", "minecraft");
/* 146 */     defaults.put("mysql.user", "username");
/* 147 */     defaults.put("mysql.password", "pass");
/* 148 */     defaults.put("mysql.table", "playerpoints");
/* 149 */     defaults.put("mysql.import.use", Boolean.valueOf(false));
/* 150 */     defaults.put("mysql.import.source", "YAML");
/* 151 */     defaults.put("mysql.export.use", Boolean.valueOf(false));
/* 152 */     defaults.put("mysql.export.source", "SQLITE");
/* 153 */     defaults.put("mysql.retry", Integer.valueOf(10));
/* 154 */     defaults.put("vote.enabled", Boolean.valueOf(false));
/* 155 */     defaults.put("vote.amount", Integer.valueOf(100));
/* 156 */     defaults.put("vote.online", Boolean.valueOf(false));
/* 157 */     defaults.put("restrictions.autocompleteOnline", Boolean.valueOf(false));
/* 158 */     defaults.put("restrictions.hasPlayedBefore", Boolean.valueOf(false));
/* 159 */     defaults.put("debug.database", Boolean.valueOf(false));
/* 160 */     defaults.put("debug.uuid", Boolean.valueOf(false));
/* 161 */     defaults.put("vault", Boolean.valueOf(false));
/* 162 */     defaults.put("version", this.plugin.getDescription().getVersion());
/*     */     
/* 164 */     for (Map.Entry<String, Object> e : defaults.entrySet()) {
/* 165 */       if (!config.contains((String)e.getKey())) {
/* 166 */         config.set((String)e.getKey(), e.getValue());
/*     */       }
/*     */     }
/*     */     
/* 170 */     this.plugin.saveConfig();
/*     */     
/* 172 */     loadSettings(config);
/* 173 */     loadStorageSettings(config);
/*     */   }
/*     */   
/*     */   public void closing() {}
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\config\RootConfig.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package org.black_ixx.playerpoints;
/*     */ 
/*     */ import com.evilmidget38.UUIDFetcher;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.UUID;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.black_ixx.playerpoints.commands.Commander;
/*     */ import org.black_ixx.playerpoints.config.LocalizeConfig;
/*     */ import org.black_ixx.playerpoints.config.RootConfig;
/*     */ import org.black_ixx.playerpoints.listeners.RestrictionListener;
/*     */ import org.black_ixx.playerpoints.listeners.VotifierListener;
/*     */ import org.black_ixx.playerpoints.services.IModule;
/*     */ import org.black_ixx.playerpoints.storage.StorageHandler;
/*     */ import org.black_ixx.playerpoints.storage.exports.Exporter;
/*     */ import org.black_ixx.playerpoints.storage.imports.Importer;
/*     */ import org.black_ixx.playerpoints.update.UpdateManager;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.OfflinePlayer;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.command.PluginCommand;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
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
/*     */ public class PlayerPoints
/*     */   extends JavaPlugin
/*     */ {
/*     */   public static final String TAG = "[PlayerPoints]";
/*     */   private PlayerPointsAPI api;
/*  48 */   private final Map<Class<? extends IModule>, IModule> modules = new HashMap();
/*     */   
/*     */ 
/*     */   public void onEnable()
/*     */   {
/*  53 */     LocalizeConfig.init(this);
/*     */     
/*  55 */     RootConfig rootConfig = new RootConfig(this);
/*  56 */     registerModule(RootConfig.class, rootConfig);
/*     */     
/*  58 */     Importer importer = new Importer(this);
/*  59 */     importer.checkImport();
/*     */     
/*  61 */     Exporter exporter = new Exporter(this);
/*  62 */     exporter.checkExport();
/*     */     
/*  64 */     registerModule(StorageHandler.class, new StorageHandler(this));
/*     */     
/*  66 */     this.api = new PlayerPointsAPI(this);
/*     */     
/*  68 */     UpdateManager update = new UpdateManager(this);
/*  69 */     update.checkUpdate();
/*     */     
/*  71 */     Commander commander = new Commander(this);
/*  72 */     if (getDescription().getCommands().containsKey("points")) {
/*  73 */       getCommand("points").setExecutor(commander);
/*     */     }
/*  75 */     if (getDescription().getCommands().containsKey("p")) {
/*  76 */       getCommand("p").setExecutor(commander);
/*     */     }
/*  78 */     PluginManager pm = getServer().getPluginManager();
/*     */     
/*  80 */     if (rootConfig.voteEnabled) {
/*  81 */       Plugin votifier = pm.getPlugin("Votifier");
/*  82 */       if (votifier != null) {
/*  83 */         pm.registerEvents(new VotifierListener(this), this);
/*     */       } else {
/*  85 */         getLogger().warning("Could not hook into Votifier!");
/*     */       }
/*     */     }
/*     */     
/*  89 */     if (rootConfig.vault) {
/*  90 */       registerModule(PlayerPointsVaultLayer.class, new PlayerPointsVaultLayer(this));
/*     */     }
/*     */     
/*     */ 
/*  94 */     pm.registerEvents(new RestrictionListener(this), this);
/*     */   }
/*     */   
/*     */ 
/*     */   public void onDisable()
/*     */   {
/* 100 */     List<Class<? extends IModule>> clazzez = new ArrayList();
/* 101 */     clazzez.addAll(this.modules.keySet());
/* 102 */     for (Class<? extends IModule> clazz : clazzez) {
/* 103 */       deregisterModuleForClass(clazz);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PlayerPointsAPI getAPI()
/*     */   {
/* 113 */     return this.api;
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
/*     */ 
/*     */ 
/*     */   public <T extends IModule> void registerModule(Class<T> clazz, T module)
/*     */   {
/* 128 */     if (clazz == null)
/* 129 */       throw new IllegalArgumentException("Class cannot be null");
/* 130 */     if (module == null)
/* 131 */       throw new IllegalArgumentException("Module cannot be null");
/* 132 */     if (this.modules.containsKey(clazz)) {
/* 133 */       getLogger().warning("Overwriting module for class: " + clazz.getName());
/*     */     }
/*     */     
/*     */ 
/* 137 */     this.modules.put(clazz, module);
/*     */     
/* 139 */     module.starting();
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
/*     */   public <T extends IModule> T deregisterModuleForClass(Class<T> clazz)
/*     */   {
/* 152 */     if (clazz == null) {
/* 153 */       throw new IllegalArgumentException("Class cannot be null");
/*     */     }
/*     */     
/* 156 */     T module = (IModule)clazz.cast(this.modules.get(clazz));
/* 157 */     if (module != null) {
/* 158 */       module.closing();
/*     */     }
/* 160 */     return module;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T extends IModule> T getModuleForClass(Class<T> clazz)
/*     */   {
/* 172 */     return (IModule)clazz.cast(this.modules.get(clazz));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String expandName(String name)
/*     */   {
/* 182 */     int m = 0;
/* 183 */     String Result = "";
/* 184 */     Collection<? extends Player> online = getServer().getOnlinePlayers();
/* 185 */     for (Player player : online) {
/* 186 */       String str = player.getName();
/* 187 */       if (str.matches("(?i).*" + name + ".*")) {
/* 188 */         m++;
/* 189 */         Result = str;
/* 190 */         if (m == 2) {
/* 191 */           return null;
/*     */         }
/*     */       }
/* 194 */       if (str.equalsIgnoreCase(name)) {
/* 195 */         return str;
/*     */       }
/*     */     }
/* 198 */     if (m == 1)
/* 199 */       return Result;
/* 200 */     if (m > 1) {
/* 201 */       return null;
/*     */     }
/* 203 */     return name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public UUID translateNameToUUID(String name)
/*     */   {
/* 212 */     UUID id = null;
/* 213 */     RootConfig config = (RootConfig)getModuleForClass(RootConfig.class);
/* 214 */     if (config.debugUUID) {
/* 215 */       getLogger().info("translateNameToUUID(" + name + ")");
/*     */     }
/*     */     
/* 218 */     if (name == null) {
/* 219 */       if (config.debugUUID) {
/* 220 */         getLogger().info("translateNameToUUID() - bad ID");
/*     */       }
/* 222 */       return id;
/*     */     }
/*     */     
/*     */ 
/* 226 */     if (config.debugUUID) {
/* 227 */       getLogger().info("translateNameToUUID() - Looking through online players: " + Bukkit.getServer().getOnlinePlayers().size());
/*     */     }
/* 229 */     Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
/* 230 */     for (Player p : players) {
/* 231 */       if (p.getName().equalsIgnoreCase(name)) {
/* 232 */         id = p.getUniqueId();
/* 233 */         if (!config.debugUUID) break;
/* 234 */         getLogger().info("translateNameToUUID() online player UUID found: " + id.toString()); break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 241 */     if ((id == null) && (Bukkit.getServer().getOnlineMode())) {
/* 242 */       if (config.debugUUID) {
/* 243 */         getLogger().info("translateNameToUUID() - Attempting online lookup");
/*     */       }
/* 245 */       UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(new String[] { name }));
/*     */       try {
/* 247 */         Map<String, UUID> map = fetcher.call();
/* 248 */         for (Map.Entry<String, UUID> entry : map.entrySet()) {
/* 249 */           if (name.equalsIgnoreCase((String)entry.getKey())) {
/* 250 */             id = (UUID)entry.getValue();
/* 251 */             if (!config.debugUUID) break;
/* 252 */             getLogger().info("translateNameToUUID() web player UUID found: " + (id == null ? id : id.toString())); break;
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 258 */         getLogger().log(Level.SEVERE, "Exception on online UUID fetch", e);
/*     */       }
/* 260 */     } else if ((id == null) && (!Bukkit.getServer().getOnlineMode()))
/*     */     {
/* 262 */       id = Bukkit.getServer().getOfflinePlayer(name).getUniqueId();
/* 263 */       if (config.debugUUID) {
/* 264 */         getLogger().info("translateNameToUUID() offline player UUID found: " + (id == null ? id : id.toString()));
/*     */       }
/*     */     }
/* 267 */     return id;
/*     */   }
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\PlayerPoints.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
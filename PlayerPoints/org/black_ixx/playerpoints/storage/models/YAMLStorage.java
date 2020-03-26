/*     */ package org.black_ixx.playerpoints.storage.models;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.black_ixx.playerpoints.PlayerPoints;
/*     */ import org.black_ixx.playerpoints.storage.IStorage;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.configuration.InvalidConfigurationException;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
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
/*     */ public class YAMLStorage
/*     */   implements IStorage
/*     */ {
/*     */   private PlayerPoints plugin;
/*     */   private File file;
/*     */   private YamlConfiguration config;
/*     */   private static final String POINTS_SECTION = "Points.";
/*     */   
/*     */   public YAMLStorage(PlayerPoints pp)
/*     */   {
/*  49 */     this.plugin = pp;
/*  50 */     this.file = new File(this.plugin.getDataFolder().getAbsolutePath() + "/storage.yml");
/*     */     
/*  52 */     this.config = YamlConfiguration.loadConfiguration(this.file);
/*  53 */     save();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void save()
/*     */   {
/*     */     try
/*     */     {
/*  63 */       this.config.save(this.file);
/*     */     } catch (IOException e1) {
/*  65 */       this.plugin.getLogger().warning("File I/O Exception on saving storage.yml");
/*     */       
/*  67 */       e1.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void reload()
/*     */   {
/*     */     try
/*     */     {
/*  76 */       this.config.load(this.file);
/*     */     } catch (FileNotFoundException e) {
/*  78 */       e.printStackTrace();
/*     */     } catch (IOException e) {
/*  80 */       e.printStackTrace();
/*     */     } catch (InvalidConfigurationException e) {
/*  82 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean setPoints(String id, int points)
/*     */   {
/*  88 */     this.config.set("Points." + id, Integer.valueOf(points));
/*  89 */     save();
/*  90 */     return true;
/*     */   }
/*     */   
/*     */   public int getPoints(String id)
/*     */   {
/*  95 */     int points = this.config.getInt("Points." + id, 0);
/*  96 */     return points;
/*     */   }
/*     */   
/*     */   public boolean playerEntryExists(String id)
/*     */   {
/* 101 */     return this.config.contains("Points." + id);
/*     */   }
/*     */   
/*     */   public boolean removePlayer(String id)
/*     */   {
/* 106 */     this.config.set("Points." + id, null);
/* 107 */     return true;
/*     */   }
/*     */   
/*     */   public Collection<String> getPlayers()
/*     */   {
/* 112 */     Collection<String> players = Collections.emptySet();
/*     */     
/* 114 */     if (this.config.isConfigurationSection("Points")) {
/* 115 */       players = this.config.getConfigurationSection("Points").getKeys(false);
/*     */     }
/* 117 */     return players;
/*     */   }
/*     */   
/*     */   public boolean destroy()
/*     */   {
/* 122 */     Collection<String> sections = this.config.getKeys(false);
/* 123 */     for (String section : sections) {
/* 124 */       this.config.set(section, null);
/*     */     }
/* 126 */     return true;
/*     */   }
/*     */   
/*     */   public boolean build()
/*     */   {
/* 131 */     boolean success = false;
/*     */     try {
/* 133 */       success = this.file.createNewFile();
/*     */     } catch (IOException e) {
/* 135 */       this.plugin.getLogger().log(Level.SEVERE, "Failed to create storage file!", e);
/*     */     }
/* 137 */     return success;
/*     */   }
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\storage\models\YAMLStorage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
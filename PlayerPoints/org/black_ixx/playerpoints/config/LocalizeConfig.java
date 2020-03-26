/*     */ package org.black_ixx.playerpoints.config;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.logging.Logger;
/*     */ import org.black_ixx.playerpoints.PlayerPoints;
/*     */ import org.black_ixx.playerpoints.models.Flag;
/*     */ import org.bukkit.ChatColor;
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
/*     */ public class LocalizeConfig
/*     */ {
/*     */   private static PlayerPoints plugin;
/*     */   private static File file;
/*     */   private static YamlConfiguration config;
/*  40 */   private static final EnumMap<LocalizeNode, String> MESSAGES = new EnumMap(LocalizeNode.class);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void init(PlayerPoints pp)
/*     */   {
/*  50 */     plugin = pp;
/*  51 */     file = new File(plugin.getDataFolder().getAbsolutePath() + "/localization.yml");
/*     */     
/*  53 */     config = YamlConfiguration.loadConfiguration(file);
/*  54 */     loadDefaults();
/*  55 */     loadMessages();
/*     */   }
/*     */   
/*     */   public static void save()
/*     */   {
/*     */     try
/*     */     {
/*  62 */       config.save(file);
/*     */     } catch (IOException e1) {
/*  64 */       plugin.getLogger().warning("File I/O Exception on saving localization config");
/*     */       
/*  66 */       e1.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void reload() {
/*     */     try {
/*  72 */       config.load(file);
/*     */     } catch (FileNotFoundException e) {
/*  74 */       e.printStackTrace();
/*     */     } catch (IOException e) {
/*  76 */       e.printStackTrace();
/*     */     } catch (InvalidConfigurationException e) {
/*  78 */       e.printStackTrace();
/*     */     }
/*  80 */     MESSAGES.clear();
/*  81 */     loadDefaults();
/*  82 */     loadMessages();
/*     */   }
/*     */   
/*     */   public static void set(String path, Object o) {
/*  86 */     config.set(path, o);
/*  87 */     save();
/*     */   }
/*     */   
/*     */   public static String getString(String path, String def) {
/*  91 */     return config.getString(path, def);
/*     */   }
/*     */   
/*     */   private static void loadDefaults()
/*     */   {
/*  96 */     for (LocalizeNode node : ) {
/*  97 */       if (!config.contains(node.getPath())) {
/*  98 */         config.set(node.getPath(), node.getDefaultValue());
/*     */       }
/*     */     }
/* 101 */     save();
/*     */   }
/*     */   
/*     */   private static void loadMessages() {
/* 105 */     for (LocalizeNode node : ) {
/* 106 */       MESSAGES.put(node, config.getString(node.getPath(), node.getDefaultValue()));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String parseString(LocalizeNode node, EnumMap<Flag, String> replace)
/*     */   {
/* 118 */     String out = ChatColor.translateAlternateColorCodes('&', (String)MESSAGES.get(node));
/*     */     
/* 120 */     if (replace != null) {
/* 121 */       for (Map.Entry<Flag, String> entry : replace.entrySet()) {
/* 122 */         out = out.replaceAll(((Flag)entry.getKey()).getFlag(), (String)entry.getValue());
/*     */       }
/*     */     }
/*     */     
/* 126 */     return out;
/*     */   }
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\config\LocalizeConfig.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
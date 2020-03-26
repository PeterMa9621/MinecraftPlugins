/*    */ package org.black_ixx.playerpoints.update.modules;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.logging.Logger;
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.services.version.Version;
/*    */ import org.black_ixx.playerpoints.update.UpdateModule;
/*    */ import org.bukkit.configuration.ConfigurationSection;
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
/*    */ public class OneFiveUpdate
/*    */   extends UpdateModule
/*    */ {
/*    */   public OneFiveUpdate(PlayerPoints plugin)
/*    */   {
/* 24 */     super(plugin);
/* 25 */     this.targetVersion = new Version("1.5");
/* 26 */     this.targetVersion.setIgnorePatch(true);
/*    */   }
/*    */   
/*    */ 
/*    */   public void update()
/*    */   {
/* 32 */     File storage = new File(this.plugin.getDataFolder() + File.separator + "config.yml");
/*    */     
/* 34 */     boolean success = false;
/*    */     try {
/* 36 */       success = storage.renameTo(new File(this.plugin.getDataFolder() + File.separator + "storage.yml"));
/*    */     }
/*    */     catch (SecurityException sec) {
/* 39 */       this.plugin.getLogger().severe("SecurityExcpetion on renaming config.yml to storage.yml");
/*    */       
/* 41 */       sec.printStackTrace();
/*    */     } catch (NullPointerException npe) {
/* 43 */       this.plugin.getLogger().severe("NullPointerException on renaming config.yml to storage.yml");
/*    */       
/* 45 */       npe.printStackTrace();
/*    */     }
/* 47 */     if (success)
/*    */     {
/* 49 */       this.plugin.reloadConfig();
/* 50 */       ConfigurationSection config = this.plugin.getConfig();
/* 51 */       config.set("storage", "YAML");
/* 52 */       config.set("mysql.host", "localhost");
/* 53 */       config.set("mysql.port", Integer.valueOf(3306));
/* 54 */       config.set("mysql.database", "minecraft");
/* 55 */       config.set("mysql.user", "username");
/* 56 */       config.set("mysql.password", "pass");
/* 57 */       config.set("mysql.import.use", Boolean.valueOf(false));
/* 58 */       config.set("mysql.import.source", "YAML");
/* 59 */       config.set("vote.enabled", Boolean.valueOf(false));
/* 60 */       config.set("vote.amount", Integer.valueOf(100));
/* 61 */       config.set("vote.online", Boolean.valueOf(false));
/* 62 */       config.set("debug.database", Boolean.valueOf(false));
/* 63 */       this.plugin.saveConfig();
/*    */     } else {
/* 65 */       this.plugin.getLogger().severe("Failed to rename file config.yml to storage.yml");
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\update\modules\OneFiveUpdate.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
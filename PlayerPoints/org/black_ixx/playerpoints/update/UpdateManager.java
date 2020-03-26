/*    */ package org.black_ixx.playerpoints.update;
/*    */ 
/*    */ import java.util.SortedSet;
/*    */ import java.util.TreeSet;
/*    */ import java.util.logging.Logger;
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.services.version.Version;
/*    */ import org.black_ixx.playerpoints.update.modules.OneFiveTwoUpdate;
/*    */ import org.black_ixx.playerpoints.update.modules.OneFiveUpdate;
/*    */ import org.black_ixx.playerpoints.update.modules.TwoZeroZeroUpdate;
/*    */ import org.bukkit.configuration.ConfigurationSection;
/*    */ import org.bukkit.configuration.file.FileConfiguration;
/*    */ import org.bukkit.plugin.PluginDescriptionFile;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UpdateManager
/*    */ {
/*    */   private PlayerPoints plugin;
/* 23 */   private final SortedSet<UpdateModule> modules = new TreeSet();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public UpdateManager(PlayerPoints plugin)
/*    */   {
/* 32 */     this.plugin = plugin;
/*    */     
/* 34 */     this.modules.add(new OneFiveUpdate(plugin));
/* 35 */     this.modules.add(new OneFiveTwoUpdate(plugin));
/* 36 */     this.modules.add(new TwoZeroZeroUpdate(plugin));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void checkUpdate()
/*    */   {
/* 44 */     ConfigurationSection config = this.plugin.getConfig();
/* 45 */     Version version = new Version(this.plugin.getDescription().getVersion());
/*    */     
/* 47 */     if (!version.validate()) {
/* 48 */       version.setIgnorePatch(true);
/*    */     }
/* 50 */     Version current = new Version(config.getString("version"));
/* 51 */     if (!current.validate()) {
/* 52 */       current.setIgnorePatch(true);
/*    */     }
/* 54 */     if (version.compareTo(current) > 0)
/*    */     {
/* 56 */       this.plugin.getLogger().info("Updating to v" + this.plugin.getDescription().getVersion());
/*    */       
/* 58 */       update(current);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private void update(Version current)
/*    */   {
/* 67 */     for (UpdateModule module : this.modules) {
/* 68 */       if (module.shouldApplyUpdate(current)) {
/* 69 */         this.plugin.getLogger().info("Applying update for " + module.getTargetVersion());
/*    */         
/* 71 */         module.update();
/*    */       }
/*    */     }
/*    */     
/*    */ 
/* 76 */     this.plugin.getConfig().set("version", this.plugin.getDescription().getVersion());
/* 77 */     this.plugin.saveConfig();
/* 78 */     this.plugin.getLogger().info("Upgrade to " + this.plugin.getDescription().getVersion() + " complete");
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\update\UpdateManager.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
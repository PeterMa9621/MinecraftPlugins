/*    */ package org.black_ixx.playerpoints.update.modules;
/*    */ 
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.config.LocalizeConfig;
/*    */ import org.black_ixx.playerpoints.config.LocalizeNode;
/*    */ import org.black_ixx.playerpoints.services.version.Version;
/*    */ import org.black_ixx.playerpoints.update.UpdateModule;
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
/*    */ public class OneFiveTwoUpdate
/*    */   extends UpdateModule
/*    */ {
/*    */   public OneFiveTwoUpdate(PlayerPoints plugin)
/*    */   {
/* 23 */     super(plugin);
/* 24 */     this.targetVersion = new Version("1.52");
/* 25 */     this.targetVersion.setIgnorePatch(true);
/*    */   }
/*    */   
/*    */   public void update()
/*    */   {
/* 30 */     String unknownCommand = LocalizeConfig.getString("message.unknownCommand", LocalizeNode.COMMAND_UNKNOWN.getDefaultValue());
/*    */     
/*    */ 
/* 33 */     LocalizeConfig.set("message.unknownCommand", null);
/* 34 */     LocalizeConfig.set(LocalizeNode.COMMAND_UNKNOWN.getPath(), unknownCommand);
/*    */     
/* 36 */     LocalizeConfig.reload();
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\update\modules\OneFiveTwoUpdate.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
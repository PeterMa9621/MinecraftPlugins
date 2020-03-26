/*    */ package org.black_ixx.playerpoints.commands;
/*    */ 
/*    */ import java.util.EnumMap;
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.config.LocalizeConfig;
/*    */ import org.black_ixx.playerpoints.config.LocalizeNode;
/*    */ import org.black_ixx.playerpoints.config.RootConfig;
/*    */ import org.black_ixx.playerpoints.models.Flag;
/*    */ import org.black_ixx.playerpoints.permissions.PermissionHandler;
/*    */ import org.black_ixx.playerpoints.permissions.PermissionNode;
/*    */ import org.black_ixx.playerpoints.services.PointsCommand;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ReloadCommand
/*    */   implements PointsCommand
/*    */ {
/*    */   public boolean execute(PlayerPoints plugin, CommandSender sender, Command command, String label, String[] args, EnumMap<Flag, String> info)
/*    */   {
/* 22 */     if (!PermissionHandler.has(sender, PermissionNode.RELOAD)) {
/* 23 */       info.put(Flag.EXTRA, PermissionNode.RELOAD.getNode());
/* 24 */       String permMessage = LocalizeConfig.parseString(LocalizeNode.PERMISSION_DENY, info);
/*    */       
/* 26 */       if (!permMessage.isEmpty()) {
/* 27 */         sender.sendMessage(permMessage);
/*    */       }
/* 29 */       return true;
/*    */     }
/* 31 */     ((RootConfig)plugin.getModuleForClass(RootConfig.class)).reloadConfig();
/* 32 */     LocalizeConfig.reload();
/* 33 */     String reloadMessage = LocalizeConfig.parseString(LocalizeNode.RELOAD, info);
/* 34 */     if (!reloadMessage.isEmpty()) {
/* 35 */       sender.sendMessage(reloadMessage);
/*    */     }
/* 37 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\commands\ReloadCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
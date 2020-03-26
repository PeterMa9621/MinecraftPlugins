/*    */ package org.black_ixx.playerpoints.commands;
/*    */ 
/*    */ import java.util.EnumMap;
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.PlayerPointsAPI;
/*    */ import org.black_ixx.playerpoints.config.LocalizeConfig;
/*    */ import org.black_ixx.playerpoints.config.LocalizeNode;
/*    */ import org.black_ixx.playerpoints.models.Flag;
/*    */ import org.black_ixx.playerpoints.permissions.PermissionHandler;
/*    */ import org.black_ixx.playerpoints.permissions.PermissionNode;
/*    */ import org.black_ixx.playerpoints.services.PointsCommand;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ 
/*    */ public class ResetCommand
/*    */   implements PointsCommand
/*    */ {
/*    */   public boolean execute(PlayerPoints plugin, CommandSender sender, Command command, String label, String[] args, EnumMap<Flag, String> info)
/*    */   {
/* 21 */     if (!PermissionHandler.has(sender, PermissionNode.RESET)) {
/* 22 */       info.put(Flag.EXTRA, PermissionNode.RESET.getNode());
/* 23 */       String permMessage = LocalizeConfig.parseString(LocalizeNode.PERMISSION_DENY, info);
/*    */       
/* 25 */       if (!permMessage.isEmpty()) {
/* 26 */         sender.sendMessage(permMessage);
/*    */       }
/* 28 */       return true;
/*    */     }
/* 30 */     if (args.length < 1)
/*    */     {
/* 32 */       String argMessage = LocalizeConfig.parseString(LocalizeNode.COMMAND_RESET, info);
/*    */       
/* 34 */       if (!argMessage.isEmpty()) {
/* 35 */         sender.sendMessage(argMessage);
/*    */       }
/* 37 */       return true;
/*    */     }
/* 39 */     if (plugin.getAPI().reset(plugin.translateNameToUUID(args[0]))) {
/* 40 */       info.put(Flag.PLAYER, args[0]);
/* 41 */       String resetMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_RESET, info);
/*    */       
/* 43 */       if (!resetMessage.isEmpty()) {
/* 44 */         sender.sendMessage(resetMessage);
/*    */       }
/*    */     } else {
/* 47 */       String failMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_FAIL, info);
/*    */       
/* 49 */       if (!failMessage.isEmpty()) {
/* 50 */         sender.sendMessage(failMessage);
/*    */       }
/*    */     }
/* 53 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\commands\ResetCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
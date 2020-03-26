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
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MeCommand
/*    */   implements PointsCommand
/*    */ {
/*    */   public boolean execute(PlayerPoints plugin, CommandSender sender, Command command, String label, String[] args, EnumMap<Flag, String> info)
/*    */   {
/* 27 */     if (!(sender instanceof Player)) {
/* 28 */       String consoleMessage = LocalizeConfig.parseString(LocalizeNode.CONSOLE_DENY, info);
/*    */       
/* 30 */       if (!consoleMessage.isEmpty()) {
/* 31 */         sender.sendMessage(consoleMessage);
/*    */       }
/* 33 */       return true;
/*    */     }
/* 35 */     if (!PermissionHandler.has(sender, PermissionNode.ME)) {
/* 36 */       info.put(Flag.EXTRA, PermissionNode.ME.getNode());
/* 37 */       String permMessage = LocalizeConfig.parseString(LocalizeNode.PERMISSION_DENY, info);
/*    */       
/* 39 */       if (!permMessage.isEmpty()) {
/* 40 */         sender.sendMessage(permMessage);
/*    */       }
/* 42 */       return true;
/*    */     }
/* 44 */     info.put(Flag.AMOUNT, "" + plugin.getAPI().look(((Player)sender).getUniqueId()));
/* 45 */     String meMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_ME, info);
/*    */     
/* 47 */     if (!meMessage.isEmpty()) {
/* 48 */       sender.sendMessage(meMessage);
/*    */     }
/* 50 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\commands\MeCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
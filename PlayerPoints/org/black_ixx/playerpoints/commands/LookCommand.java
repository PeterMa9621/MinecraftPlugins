/*    */ package org.black_ixx.playerpoints.commands;
/*    */ 
/*    */ import java.util.EnumMap;
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.PlayerPointsAPI;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LookCommand
/*    */   implements PointsCommand
/*    */ {
/*    */   public boolean execute(PlayerPoints plugin, CommandSender sender, Command command, String label, String[] args, EnumMap<Flag, String> info)
/*    */   {
/* 27 */     if (!PermissionHandler.has(sender, PermissionNode.LOOK)) {
/* 28 */       info.put(Flag.EXTRA, PermissionNode.LOOK.getNode());
/* 29 */       String permMessage = LocalizeConfig.parseString(LocalizeNode.PERMISSION_DENY, info);
/*    */       
/* 31 */       if (!permMessage.isEmpty()) {
/* 32 */         sender.sendMessage(permMessage);
/*    */       }
/* 34 */       return true;
/*    */     }
/* 36 */     if (args.length < 1)
/*    */     {
/* 38 */       String argMessage = LocalizeConfig.parseString(LocalizeNode.COMMAND_LOOK, info);
/*    */       
/* 40 */       if (!argMessage.isEmpty()) {
/* 41 */         sender.sendMessage(argMessage);
/*    */       }
/* 43 */       return true;
/*    */     }
/* 45 */     String playerName = null;
/* 46 */     if (((RootConfig)plugin.getModuleForClass(RootConfig.class)).autocompleteOnline) {
/* 47 */       playerName = plugin.expandName(args[0]);
/*    */     }
/* 49 */     if (playerName == null) {
/* 50 */       playerName = args[0];
/*    */     }
/* 52 */     info.put(Flag.PLAYER, playerName);
/* 53 */     info.put(Flag.AMOUNT, "" + plugin.getAPI().look(plugin.translateNameToUUID(playerName)));
/* 54 */     String senderMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_LOOK, info);
/*    */     
/* 56 */     if (!senderMessage.isEmpty()) {
/* 57 */       sender.sendMessage(senderMessage);
/*    */     }
/* 59 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\commands\LookCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
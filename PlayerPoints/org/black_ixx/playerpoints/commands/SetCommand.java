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
/*    */ public class SetCommand
/*    */   implements PointsCommand
/*    */ {
/*    */   public boolean execute(PlayerPoints plugin, CommandSender sender, Command command, String label, String[] args, EnumMap<Flag, String> info)
/*    */   {
/* 27 */     if (!PermissionHandler.has(sender, PermissionNode.SET)) {
/* 28 */       info.put(Flag.EXTRA, PermissionNode.SET.getNode());
/* 29 */       String permMessage = LocalizeConfig.parseString(LocalizeNode.PERMISSION_DENY, info);
/*    */       
/* 31 */       if (!permMessage.isEmpty()) {
/* 32 */         sender.sendMessage(permMessage);
/*    */       }
/* 34 */       return true;
/*    */     }
/* 36 */     if (args.length < 2)
/*    */     {
/* 38 */       String argMessage = LocalizeConfig.parseString(LocalizeNode.COMMAND_SET, info);
/*    */       
/* 40 */       if (!argMessage.isEmpty()) {
/* 41 */         sender.sendMessage(argMessage);
/*    */       }
/* 43 */       return true;
/*    */     }
/*    */     try {
/* 46 */       int intanzahl = Integer.parseInt(args[1]);
/* 47 */       String playerName = null;
/* 48 */       if (((RootConfig)plugin.getModuleForClass(RootConfig.class)).autocompleteOnline) {
/* 49 */         playerName = plugin.expandName(args[0]);
/*    */       }
/* 51 */       if (playerName == null) {
/* 52 */         playerName = args[0];
/*    */       }
/* 54 */       if (plugin.getAPI().set(plugin.translateNameToUUID(playerName), intanzahl)) {
/* 55 */         info.put(Flag.PLAYER, playerName);
/* 56 */         info.put(Flag.AMOUNT, "" + intanzahl);
/* 57 */         String successMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_SUCCESS, info);
/*    */         
/* 59 */         if (!successMessage.isEmpty()) {
/* 60 */           sender.sendMessage(successMessage);
/*    */         }
/*    */       } else {
/* 63 */         String failMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_FAIL, info);
/*    */         
/* 65 */         if (!failMessage.isEmpty()) {
/* 66 */           sender.sendMessage(failMessage);
/*    */         }
/*    */       }
/*    */     }
/*    */     catch (NumberFormatException notnumber) {
/* 71 */       info.put(Flag.EXTRA, args[1]);
/* 72 */       String errorMessage = LocalizeConfig.parseString(LocalizeNode.NOT_INTEGER, info);
/*    */       
/* 74 */       if (!errorMessage.isEmpty()) {
/* 75 */         sender.sendMessage(errorMessage);
/*    */       }
/*    */     }
/* 78 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\commands\SetCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
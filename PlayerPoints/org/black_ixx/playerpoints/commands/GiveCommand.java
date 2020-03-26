/*    */ package org.black_ixx.playerpoints.commands;
/*    */ 
/*    */ import java.util.EnumMap;
/*    */ import java.util.UUID;
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.PlayerPointsAPI;
/*    */ import org.black_ixx.playerpoints.config.LocalizeConfig;
/*    */ import org.black_ixx.playerpoints.config.LocalizeNode;
/*    */ import org.black_ixx.playerpoints.config.RootConfig;
/*    */ import org.black_ixx.playerpoints.models.Flag;
/*    */ import org.black_ixx.playerpoints.permissions.PermissionHandler;
/*    */ import org.black_ixx.playerpoints.permissions.PermissionNode;
/*    */ import org.black_ixx.playerpoints.services.PointsCommand;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GiveCommand
/*    */   implements PointsCommand
/*    */ {
/*    */   public boolean execute(PlayerPoints plugin, CommandSender sender, Command command, String label, String[] args, EnumMap<Flag, String> info)
/*    */   {
/* 30 */     if (!PermissionHandler.has(sender, PermissionNode.GIVE)) {
/* 31 */       info.put(Flag.EXTRA, PermissionNode.GIVE.getNode());
/* 32 */       String permMessage = LocalizeConfig.parseString(LocalizeNode.PERMISSION_DENY, info);
/*    */       
/* 34 */       if (!permMessage.isEmpty()) {
/* 35 */         sender.sendMessage(permMessage);
/*    */       }
/* 37 */       return true;
/*    */     }
/* 39 */     if (args.length < 2) {
/* 40 */       String argMessage = LocalizeConfig.parseString(LocalizeNode.COMMAND_GIVE, info);
/*    */       
/* 42 */       if (!argMessage.isEmpty()) {
/* 43 */         sender.sendMessage(argMessage);
/*    */       }
/* 45 */       return true;
/*    */     }
/*    */     try {
/* 48 */       int anzahl = Integer.parseInt(args[1]);
/* 49 */       String playerName = null;
/* 50 */       if (((RootConfig)plugin.getModuleForClass(RootConfig.class)).autocompleteOnline) {
/* 51 */         playerName = plugin.expandName(args[0]);
/*    */       }
/* 53 */       if (playerName == null) {
/* 54 */         playerName = args[0];
/*    */       }
/* 56 */       UUID id = plugin.translateNameToUUID(playerName);
/* 57 */       if (plugin.getAPI().give(id, anzahl)) {
/* 58 */         info.put(Flag.PLAYER, playerName);
/* 59 */         info.put(Flag.AMOUNT, "" + plugin.getAPI().look(id));
/* 60 */         String successMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_SUCCESS, info);
/*    */         
/* 62 */         if (!successMessage.isEmpty()) {
/* 63 */           sender.sendMessage(successMessage);
/*    */         }
/* 65 */         Player target = Bukkit.getServer().getPlayer(id);
/* 66 */         if ((target != null) && (target.isOnline())) {
/* 67 */           info.put(Flag.PLAYER, sender.getName());
/* 68 */           info.put(Flag.AMOUNT, "" + anzahl);
/* 69 */           String targetMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_PAY_RECEIVE, info);
/*    */           
/* 71 */           if (!targetMessage.isEmpty()) {
/* 72 */             target.sendMessage(targetMessage);
/*    */           }
/*    */         }
/*    */       } else {
/* 76 */         String failMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_FAIL, info);
/*    */         
/* 78 */         if (!failMessage.isEmpty()) {
/* 79 */           sender.sendMessage(failMessage);
/*    */         }
/*    */       }
/*    */     } catch (NumberFormatException notnumber) {
/* 83 */       info.put(Flag.EXTRA, args[1]);
/* 84 */       String errorMessage = LocalizeConfig.parseString(LocalizeNode.NOT_INTEGER, info);
/*    */       
/* 86 */       if (!errorMessage.isEmpty()) {
/* 87 */         sender.sendMessage(errorMessage);
/*    */       }
/*    */     }
/* 90 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\commands\GiveCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
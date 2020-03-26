/*    */ package org.black_ixx.playerpoints.commands;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.EnumMap;
/*    */ import java.util.List;
/*    */ import org.black_ixx.playerpoints.PlayerPoints;
/*    */ import org.black_ixx.playerpoints.PlayerPointsAPI;
/*    */ import org.black_ixx.playerpoints.config.LocalizeConfig;
/*    */ import org.black_ixx.playerpoints.config.LocalizeNode;
/*    */ import org.black_ixx.playerpoints.models.Flag;
/*    */ import org.black_ixx.playerpoints.permissions.PermissionHandler;
/*    */ import org.black_ixx.playerpoints.permissions.PermissionNode;
/*    */ import org.black_ixx.playerpoints.services.PointsCommand;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class GiveAllCommand
/*    */   implements PointsCommand
/*    */ {
/*    */   public boolean execute(PlayerPoints plugin, CommandSender sender, Command command, String label, String[] args, EnumMap<Flag, String> info)
/*    */   {
/* 25 */     if (!PermissionHandler.has(sender, PermissionNode.GIVEALL)) {
/* 26 */       info.put(Flag.EXTRA, PermissionNode.GIVEALL.getNode());
/* 27 */       String permMessage = LocalizeConfig.parseString(LocalizeNode.PERMISSION_DENY, info);
/*    */       
/* 29 */       if (!permMessage.isEmpty()) {
/* 30 */         sender.sendMessage(permMessage);
/*    */       }
/* 32 */       return true;
/*    */     }
/* 34 */     if (args.length < 1) {
/* 35 */       String argMessage = LocalizeConfig.parseString(LocalizeNode.COMMAND_GIVEALL, info);
/*    */       
/* 37 */       if (!argMessage.isEmpty()) {
/* 38 */         sender.sendMessage(argMessage);
/*    */       }
/* 40 */       return true;
/*    */     }
/*    */     try {
/* 43 */       int anzahl = Integer.parseInt(args[0]);
/* 44 */       info.put(Flag.AMOUNT, String.valueOf(anzahl));
/* 45 */       List<String> unsuccessful = new ArrayList();
/* 46 */       for (Player player : Bukkit.getOnlinePlayers()) {
/* 47 */         if (player != null) {
/* 48 */           if (plugin.getAPI().give(player.getUniqueId(), anzahl)) {
/* 49 */             info.put(Flag.PLAYER, sender.getName());
/* 50 */             String receiveMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_PAY_RECEIVE, info);
/*    */             
/* 52 */             if (!receiveMessage.isEmpty()) {
/* 53 */               player.sendMessage(receiveMessage);
/*    */             }
/*    */           } else {
/* 56 */             unsuccessful.add(player.getName());
/*    */           }
/*    */         }
/*    */       }
/* 60 */       info.put(Flag.PLAYER, String.valueOf(Bukkit.getOnlinePlayers().size() - unsuccessful.size()));
/*    */       
/*    */ 
/*    */ 
/* 64 */       String successMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_SUCCESS_ALL, info);
/*    */       
/* 66 */       if (!successMessage.isEmpty()) {
/* 67 */         sender.sendMessage(successMessage);
/*    */       }
/* 69 */       if (!unsuccessful.isEmpty())
/*    */       {
/* 71 */         info.put(Flag.PLAYER, String.valueOf(unsuccessful.size()));
/* 72 */         String failMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_FAIL_ALL, info);
/*    */         
/* 74 */         if (!failMessage.isEmpty()) {
/* 75 */           sender.sendMessage(failMessage);
/*    */         }
/*    */       }
/*    */     } catch (NumberFormatException notnumber) {
/* 79 */       info.put(Flag.EXTRA, args[1]);
/* 80 */       String errorMessage = LocalizeConfig.parseString(LocalizeNode.NOT_INTEGER, info);
/*    */       
/* 82 */       if (!errorMessage.isEmpty()) {
/* 83 */         sender.sendMessage(errorMessage);
/*    */       }
/*    */     }
/* 86 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\commands\GiveAllCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
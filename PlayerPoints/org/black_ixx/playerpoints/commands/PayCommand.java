/*     */ package org.black_ixx.playerpoints.commands;
/*     */ 
/*     */ import java.util.EnumMap;
/*     */ import java.util.UUID;
/*     */ import org.black_ixx.playerpoints.PlayerPoints;
/*     */ import org.black_ixx.playerpoints.PlayerPointsAPI;
/*     */ import org.black_ixx.playerpoints.config.LocalizeConfig;
/*     */ import org.black_ixx.playerpoints.config.LocalizeNode;
/*     */ import org.black_ixx.playerpoints.config.RootConfig;
/*     */ import org.black_ixx.playerpoints.models.Flag;
/*     */ import org.black_ixx.playerpoints.permissions.PermissionHandler;
/*     */ import org.black_ixx.playerpoints.permissions.PermissionNode;
/*     */ import org.black_ixx.playerpoints.services.PointsCommand;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PayCommand
/*     */   implements PointsCommand
/*     */ {
/*     */   public boolean execute(PlayerPoints plugin, CommandSender sender, Command command, String label, String[] args, EnumMap<Flag, String> info)
/*     */   {
/*  30 */     if (!(sender instanceof Player)) {
/*  31 */       String consoleMessage = LocalizeConfig.parseString(LocalizeNode.CONSOLE_DENY, info);
/*     */       
/*  33 */       if (!consoleMessage.isEmpty()) {
/*  34 */         sender.sendMessage(consoleMessage);
/*     */       }
/*  36 */       return true;
/*     */     }
/*  38 */     if (!PermissionHandler.has(sender, PermissionNode.PAY)) {
/*  39 */       info.put(Flag.EXTRA, PermissionNode.PAY.getNode());
/*  40 */       String permMessage = LocalizeConfig.parseString(LocalizeNode.PERMISSION_DENY, info);
/*     */       
/*  42 */       if (!permMessage.isEmpty()) {
/*  43 */         sender.sendMessage(permMessage);
/*     */       }
/*  45 */       return true;
/*     */     }
/*  47 */     if (args.length < 2)
/*     */     {
/*  49 */       String argMessage = LocalizeConfig.parseString(LocalizeNode.COMMAND_PAY, info);
/*     */       
/*  51 */       if (!argMessage.isEmpty()) {
/*  52 */         sender.sendMessage(argMessage);
/*     */       }
/*  54 */       return true;
/*     */     }
/*     */     try {
/*  57 */       int intanzahl = Integer.parseInt(args[1]);
/*  58 */       if (intanzahl <= 0) {
/*  59 */         String invalidMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_PAY_INVALID, info);
/*     */         
/*  61 */         if (!invalidMessage.isEmpty()) {
/*  62 */           sender.sendMessage(invalidMessage);
/*     */         }
/*  64 */         return true;
/*     */       }
/*  66 */       String playerName = null;
/*  67 */       if (((RootConfig)plugin.getModuleForClass(RootConfig.class)).autocompleteOnline) {
/*  68 */         playerName = plugin.expandName(args[0]);
/*     */       }
/*  70 */       if (playerName == null) {
/*  71 */         playerName = args[0];
/*     */       }
/*  73 */       UUID id = plugin.translateNameToUUID(playerName);
/*  74 */       if (plugin.getAPI().pay(((Player)sender).getUniqueId(), id, intanzahl)) {
/*  75 */         info.put(Flag.PLAYER, playerName);
/*  76 */         info.put(Flag.AMOUNT, "" + args[1]);
/*  77 */         String sendMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_PAY_SEND, info);
/*     */         
/*  79 */         if (!sendMessage.isEmpty()) {
/*  80 */           sender.sendMessage(sendMessage);
/*     */         }
/*     */         
/*  83 */         Player target = Bukkit.getServer().getPlayer(id);
/*  84 */         if ((target != null) && (target.isOnline())) {
/*  85 */           info.put(Flag.PLAYER, sender.getName());
/*  86 */           String receiveMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_PAY_RECEIVE, info);
/*     */           
/*  88 */           if (!receiveMessage.isEmpty()) {
/*  89 */             target.sendMessage(receiveMessage);
/*     */           }
/*     */         }
/*     */       } else {
/*  93 */         String lackMessage = LocalizeConfig.parseString(LocalizeNode.POINTS_LACK, info);
/*     */         
/*  95 */         if (!lackMessage.isEmpty()) {
/*  96 */           sender.sendMessage(lackMessage);
/*     */         }
/*     */       }
/*     */     } catch (NumberFormatException notnumber) {
/* 100 */       info.put(Flag.EXTRA, args[1]);
/* 101 */       String errorMessage = LocalizeConfig.parseString(LocalizeNode.NOT_INTEGER, info);
/*     */       
/* 103 */       if (!errorMessage.isEmpty()) {
/* 104 */         sender.sendMessage(errorMessage);
/*     */       }
/*     */     }
/* 107 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\commands\PayCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
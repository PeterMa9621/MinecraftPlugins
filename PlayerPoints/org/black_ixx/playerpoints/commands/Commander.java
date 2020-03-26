/*     */ package org.black_ixx.playerpoints.commands;
/*     */ 
/*     */ import java.util.EnumMap;
/*     */ import org.black_ixx.playerpoints.PlayerPoints;
/*     */ import org.black_ixx.playerpoints.config.LocalizeConfig;
/*     */ import org.black_ixx.playerpoints.config.LocalizeNode;
/*     */ import org.black_ixx.playerpoints.models.Flag;
/*     */ import org.black_ixx.playerpoints.permissions.PermissionHandler;
/*     */ import org.black_ixx.playerpoints.permissions.PermissionNode;
/*     */ import org.black_ixx.playerpoints.services.CommandHandler;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandSender;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Commander
/*     */   extends CommandHandler
/*     */ {
/*     */   public Commander(PlayerPoints plugin)
/*     */   {
/*  29 */     super(plugin, "points");
/*     */     
/*     */ 
/*  32 */     registerCommand("give", new GiveCommand());
/*  33 */     registerCommand("giveall", new GiveAllCommand());
/*  34 */     registerCommand("take", new TakeCommand());
/*  35 */     registerCommand("look", new LookCommand());
/*  36 */     registerCommand("pay", new PayCommand());
/*  37 */     registerCommand("set", new SetCommand());
/*  38 */     registerCommand("broadcast", new BroadcastCommand());
/*  39 */     registerCommand("reset", new ResetCommand());
/*  40 */     registerCommand("me", new MeCommand());
/*  41 */     registerCommand("reload", new ReloadCommand());
/*     */     
/*     */ 
/*  44 */     registerHandler(new LeadCommand(plugin));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean noArgs(CommandSender sender, Command command, String label, EnumMap<Flag, String> info)
/*     */   {
/*  50 */     sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_HEADER, info));
/*     */     
/*  52 */     if (PermissionHandler.has(sender, PermissionNode.ME)) {
/*  53 */       sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_ME, info));
/*     */     }
/*     */     
/*  56 */     if (PermissionHandler.has(sender, PermissionNode.GIVE)) {
/*  57 */       sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_GIVE, info));
/*     */     }
/*     */     
/*  60 */     if (PermissionHandler.has(sender, PermissionNode.GIVEALL)) {
/*  61 */       sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_GIVEALL, info));
/*     */     }
/*     */     
/*  64 */     if (PermissionHandler.has(sender, PermissionNode.TAKE)) {
/*  65 */       sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_TAKE, info));
/*     */     }
/*     */     
/*  68 */     if (PermissionHandler.has(sender, PermissionNode.PAY)) {
/*  69 */       sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_PAY, info));
/*     */     }
/*     */     
/*  72 */     if (PermissionHandler.has(sender, PermissionNode.LOOK)) {
/*  73 */       sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_LOOK, info));
/*     */     }
/*     */     
/*  76 */     if (PermissionHandler.has(sender, PermissionNode.LEAD)) {
/*  77 */       sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_LEAD, info));
/*     */     }
/*     */     
/*  80 */     if (PermissionHandler.has(sender, PermissionNode.BROADCAST)) {
/*  81 */       sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_BROADCAST, info));
/*     */     }
/*     */     
/*  84 */     if (PermissionHandler.has(sender, PermissionNode.SET)) {
/*  85 */       sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_SET, info));
/*     */     }
/*     */     
/*  88 */     if (PermissionHandler.has(sender, PermissionNode.RESET)) {
/*  89 */       sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_RESET, info));
/*     */     }
/*     */     
/*  92 */     if (PermissionHandler.has(sender, PermissionNode.RELOAD)) {
/*  93 */       sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_RELOAD, info));
/*     */     }
/*     */     
/*  96 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean unknownCommand(CommandSender sender, Command command, String label, String[] args, EnumMap<Flag, String> info)
/*     */   {
/* 102 */     info.put(Flag.EXTRA, args[0]);
/* 103 */     sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.COMMAND_UNKNOWN, info));
/*     */     
/* 105 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\commands\Commander.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
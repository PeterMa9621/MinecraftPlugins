/*     */ package org.black_ixx.playerpoints.services;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Logger;
/*     */ import org.black_ixx.playerpoints.PlayerPoints;
/*     */ import org.black_ixx.playerpoints.models.Flag;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class CommandHandler
/*     */   implements CommandExecutor
/*     */ {
/*  24 */   protected final Map<String, PointsCommand> registeredCommands = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*  28 */   protected final Map<String, CommandHandler> registeredHandlers = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PlayerPoints plugin;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String cmd;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CommandHandler(PlayerPoints plugin, String cmd)
/*     */   {
/*  46 */     this.plugin = plugin;
/*  47 */     this.cmd = cmd;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerCommand(String label, PointsCommand command)
/*     */   {
/*  60 */     if (this.registeredCommands.containsKey(label)) {
/*  61 */       this.plugin.getLogger().warning("Replacing existing command for: " + label);
/*     */     }
/*     */     
/*  64 */     this.registeredCommands.put(label, command);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void unregisterCommand(String label)
/*     */   {
/*  74 */     this.registeredCommands.remove(label);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerHandler(CommandHandler handler)
/*     */   {
/*  86 */     if (this.registeredHandlers.containsKey(handler.getCommand())) {
/*  87 */       this.plugin.getLogger().warning("Replacing existing handler for: " + handler.getCommand());
/*     */     }
/*     */     
/*  90 */     this.registeredHandlers.put(handler.getCommand(), handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void unregisterHandler(String label)
/*     */   {
/* 100 */     this.registeredHandlers.remove(label);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/* 110 */     EnumMap<Flag, String> info = new EnumMap(Flag.class);
/* 111 */     info.put(Flag.TAG, "[PlayerPoints]");
/* 112 */     if (args.length == 0) {
/* 113 */       return noArgs(sender, command, label, info);
/*     */     }
/* 115 */     String subcmd = args[0].toLowerCase();
/*     */     
/* 117 */     CommandHandler handler = (CommandHandler)this.registeredHandlers.get(subcmd);
/* 118 */     if (handler != null) {
/* 119 */       return handler.onCommand(sender, command, label, shortenArgs(args));
/*     */     }
/*     */     
/*     */ 
/* 123 */     PointsCommand subCommand = (PointsCommand)this.registeredCommands.get(subcmd);
/* 124 */     if (subCommand == null) {
/* 125 */       return unknownCommand(sender, command, label, args, info);
/*     */     }
/*     */     
/* 128 */     boolean value = true;
/*     */     try {
/* 130 */       value = subCommand.execute(this.plugin, sender, command, label, shortenArgs(args), info);
/*     */     }
/*     */     catch (ArrayIndexOutOfBoundsException e) {
/* 133 */       sender.sendMessage(ChatColor.GRAY + "[PlayerPoints]" + ChatColor.RED + " Missing parameters.");
/*     */     }
/*     */     
/* 136 */     return value;
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean noArgs(CommandSender paramCommandSender, Command paramCommand, String paramString, EnumMap<Flag, String> paramEnumMap);
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
/*     */ 
/*     */ 
/*     */   public abstract boolean unknownCommand(CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramArrayOfString, EnumMap<Flag, String> paramEnumMap);
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
/*     */ 
/*     */ 
/*     */   protected String[] shortenArgs(String[] args)
/*     */   {
/* 182 */     if (args.length == 0) {
/* 183 */       return args;
/*     */     }
/* 185 */     List<String> argList = new ArrayList();
/* 186 */     for (int i = 1; i < args.length; i++) {
/* 187 */       argList.add(args[i]);
/*     */     }
/* 189 */     return (String[])argList.toArray(new String[0]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getCommand()
/*     */   {
/* 198 */     return this.cmd;
/*     */   }
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\services\CommandHandler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
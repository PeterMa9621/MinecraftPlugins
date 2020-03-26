/*     */ package org.black_ixx.playerpoints.commands;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import java.util.UUID;
/*     */ import org.black_ixx.playerpoints.PlayerPoints;
/*     */ import org.black_ixx.playerpoints.PlayerPointsAPI;
/*     */ import org.black_ixx.playerpoints.config.LocalizeConfig;
/*     */ import org.black_ixx.playerpoints.config.LocalizeNode;
/*     */ import org.black_ixx.playerpoints.models.Flag;
/*     */ import org.black_ixx.playerpoints.models.SortedPlayer;
/*     */ import org.black_ixx.playerpoints.permissions.PermissionHandler;
/*     */ import org.black_ixx.playerpoints.permissions.PermissionNode;
/*     */ import org.black_ixx.playerpoints.services.CommandHandler;
/*     */ import org.black_ixx.playerpoints.storage.StorageHandler;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.OfflinePlayer;
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
/*     */ public class LeadCommand
/*     */   extends CommandHandler
/*     */ {
/*     */   private static final int LIMIT = 10;
/*  40 */   private final Map<String, Integer> page = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public LeadCommand(PlayerPoints plugin)
/*     */   {
/*  49 */     super(plugin, "lead");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean noArgs(CommandSender sender, Command command, String label, EnumMap<Flag, String> info)
/*     */   {
/*  56 */     if (!PermissionHandler.has(sender, PermissionNode.LEAD)) {
/*  57 */       info.put(Flag.EXTRA, PermissionNode.LEAD.getNode());
/*  58 */       String permMessage = LocalizeConfig.parseString(LocalizeNode.PERMISSION_DENY, info);
/*     */       
/*  60 */       if (!permMessage.isEmpty()) {
/*  61 */         sender.sendMessage(permMessage);
/*     */       }
/*  63 */       return true;
/*     */     }
/*     */     
/*  66 */     SortedSet<SortedPlayer> leaders = sortLeaders(this.plugin, ((StorageHandler)this.plugin.getModuleForClass(StorageHandler.class)).getPlayers());
/*     */     
/*     */ 
/*  69 */     int current = 0;
/*  70 */     if (this.page.containsKey(sender.getName())) {
/*  71 */       current = ((Integer)this.page.get(sender.getName())).intValue();
/*     */     }
/*     */     
/*  74 */     int num = leaders.size() / 10;
/*  75 */     double rem = leaders.size() % 10.0D;
/*  76 */     if (rem != 0.0D) {
/*  77 */       num++;
/*     */     }
/*     */     
/*     */ 
/*  81 */     if (current < 0) {
/*  82 */       current = 0;
/*  83 */       this.page.put(sender.getName(), Integer.valueOf(current));
/*  84 */     } else if (current >= num) {
/*  85 */       current = num - 1;
/*  86 */       this.page.put(sender.getName(), Integer.valueOf(current));
/*     */     }
/*     */     
/*  89 */     SortedPlayer[] array = (SortedPlayer[])leaders.toArray(new SortedPlayer[0]);
/*     */     
/*  91 */     if (leaders.isEmpty()) {
/*  92 */       current = 0;
/*  93 */       num = 0;
/*     */     }
/*     */     
/*     */ 
/*  97 */     sender.sendMessage(ChatColor.BLUE + "=== " + ChatColor.GRAY + "[PlayerPoints]" + " Leader Board " + ChatColor.BLUE + "=== " + ChatColor.GRAY + (current + 1) + ":" + num);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 102 */     for (int i = current * 10; i < current * 10 + 10; i++) {
/* 103 */       if (i >= array.length) {
/*     */         break;
/*     */       }
/* 106 */       SortedPlayer player = array[i];
/* 107 */       sender.sendMessage(ChatColor.AQUA + "" + (i + 1) + ". " + ChatColor.GRAY + Bukkit.getOfflinePlayer(UUID.fromString(player.getName())).getName() + ChatColor.WHITE + " - " + ChatColor.GOLD + player.getPoints());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 112 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean unknownCommand(CommandSender sender, Command command, String label, String[] args, EnumMap<Flag, String> info)
/*     */   {
/* 118 */     String com = args[0];
/*     */     
/* 120 */     int current = 0;
/* 121 */     if (this.page.containsKey(sender.getName())) {
/* 122 */       current = ((Integer)this.page.get(sender.getName())).intValue();
/*     */     }
/*     */     
/* 125 */     boolean valid = false;
/*     */     
/* 127 */     if (com.equalsIgnoreCase("prev")) {
/* 128 */       this.page.put(sender.getName(), Integer.valueOf(--current));
/* 129 */       noArgs(sender, command, label, info);
/* 130 */       valid = true;
/* 131 */     } else if (com.equals("next")) {
/* 132 */       this.page.put(sender.getName(), Integer.valueOf(++current));
/* 133 */       noArgs(sender, command, label, info);
/* 134 */       valid = true;
/*     */     } else {
/*     */       try {
/* 137 */         current = Integer.parseInt(com);
/* 138 */         this.page.put(sender.getName(), Integer.valueOf(current - 1));
/* 139 */         noArgs(sender, command, label, info);
/* 140 */         valid = true;
/*     */       }
/*     */       catch (NumberFormatException e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 147 */     if (!valid) {
/* 148 */       info.put(Flag.EXTRA, args[0]);
/* 149 */       sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.COMMAND_LEAD, info));
/*     */     }
/*     */     
/*     */ 
/* 153 */     return true;
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
/*     */   private SortedSet<SortedPlayer> sortLeaders(PlayerPoints plugin, Collection<String> players)
/*     */   {
/* 167 */     SortedSet<SortedPlayer> sorted = new TreeSet();
/*     */     
/* 169 */     for (String name : players) {
/* 170 */       int points = plugin.getAPI().look(UUID.fromString(name));
/* 171 */       sorted.add(new SortedPlayer(name, points));
/*     */     }
/*     */     
/* 174 */     return sorted;
/*     */   }
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\commands\LeadCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
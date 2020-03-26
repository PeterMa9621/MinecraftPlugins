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
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class BroadcastCommand
/*    */   implements PointsCommand
/*    */ {
/*    */   public boolean execute(PlayerPoints plugin, CommandSender sender, Command command, String label, String[] args, EnumMap<Flag, String> info)
/*    */   {
/* 23 */     if (!PermissionHandler.has(sender, PermissionNode.BROADCAST)) {
/* 24 */       info.put(Flag.EXTRA, PermissionNode.BROADCAST.getNode());
/* 25 */       String permMessage = LocalizeConfig.parseString(LocalizeNode.PERMISSION_DENY, info);
/*    */       
/* 27 */       if (!permMessage.isEmpty()) {
/* 28 */         sender.sendMessage(permMessage);
/*    */       }
/* 30 */       return true;
/*    */     }
/* 32 */     if (args.length < 1) {
/* 33 */       String argMessage = LocalizeConfig.parseString(LocalizeNode.COMMAND_BROADCAST, info);
/*    */       
/* 35 */       if (!argMessage.isEmpty()) {
/* 36 */         sender.sendMessage(argMessage);
/*    */       }
/* 38 */       return true;
/*    */     }
/* 40 */     String playerName = null;
/* 41 */     if (((RootConfig)plugin.getModuleForClass(RootConfig.class)).autocompleteOnline) {
/* 42 */       playerName = plugin.expandName(args[0]);
/*    */     }
/* 44 */     if (playerName == null) {
/* 45 */       playerName = args[0];
/*    */     }
/* 47 */     info.put(Flag.PLAYER, playerName);
/* 48 */     info.put(Flag.AMOUNT, "" + plugin.getAPI().look(plugin.translateNameToUUID(playerName)));
/* 49 */     String message = LocalizeConfig.parseString(LocalizeNode.BROADCAST, info);
/* 50 */     if (!message.isEmpty()) {
/* 51 */       for (Player player : plugin.getServer().getOnlinePlayers()) {
/* 52 */         player.sendMessage(message);
/*    */       }
/*    */     }
/* 55 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\commands\BroadcastCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package org.black_ixx.playerpoints.permissions;
/*    */ 
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PermissionHandler
/*    */ {
/*    */   public static boolean has(CommandSender sender, PermissionNode node)
/*    */   {
/* 25 */     if (sender.isOp())
/*    */     {
/*    */ 
/* 28 */       return true;
/*    */     }
/* 30 */     return sender.hasPermission(node.getNode());
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\permissions\PermissionHandler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
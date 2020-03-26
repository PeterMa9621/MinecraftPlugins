/*    */ package org.black_ixx.playerpoints.permissions;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum PermissionNode
/*    */ {
/* 12 */   GIVE(".give"), 
/* 13 */   GIVEALL(".giveall"), 
/* 14 */   TAKE(".take"), 
/* 15 */   LOOK(".look"), 
/* 16 */   PAY(".pay"), 
/* 17 */   SET(".set"), 
/* 18 */   RESET(".reset"), 
/* 19 */   ME(".me"), 
/* 20 */   LEAD(".lead"), 
/* 21 */   RELOAD(".reload"), 
/* 22 */   BROADCAST(".broadcast");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private static final String prefix = "PlayerPoints";
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private String node;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private PermissionNode(String node)
/*    */   {
/* 41 */     this.node = ("PlayerPoints" + node);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getNode()
/*    */   {
/* 50 */     return this.node;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\permissions\PermissionNode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
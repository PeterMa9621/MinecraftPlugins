/*    */ package org.black_ixx.playerpoints.models;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum Flag
/*    */ {
/*  9 */   NAME("%name"), 
/* 10 */   TAG("%tag"), 
/* 11 */   PLAYER("%player"), 
/* 12 */   EXTRA("%extra"), 
/* 13 */   AMOUNT("%amount");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private String flag;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private Flag(String flag)
/*    */   {
/* 27 */     this.flag = flag;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getFlag()
/*    */   {
/* 36 */     return this.flag;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\models\Flag.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
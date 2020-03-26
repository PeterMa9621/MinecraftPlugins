/*    */ package org.black_ixx.playerpoints.models;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SortedPlayer
/*    */   implements Comparable<SortedPlayer>
/*    */ {
/*    */   final String name;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   final int points;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SortedPlayer(String name, int points)
/*    */   {
/* 30 */     this.name = name;
/* 31 */     this.points = points;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getName()
/*    */   {
/* 40 */     return this.name;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getPoints()
/*    */   {
/* 49 */     return this.points;
/*    */   }
/*    */   
/*    */   public int compareTo(SortedPlayer o)
/*    */   {
/* 54 */     if (getPoints() > o.getPoints())
/* 55 */       return -1;
/* 56 */     if (getPoints() < o.getPoints()) {
/* 57 */       return 1;
/*    */     }
/* 59 */     return getName().compareTo(o.getName());
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\models\SortedPlayer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package org.black_ixx.playerpoints.services.version;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Metadata
/*    */ {
/* 16 */   public static final Metadata NONE = new Metadata("");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private final String raw;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 26 */   private final List<String> metadata = new ArrayList();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Metadata(String meta)
/*    */   {
/* 35 */     this.raw = meta;
/*    */     
/* 37 */     String[] ids = meta.split("\\.");
/* 38 */     for (int i = 0; i < ids.length; i++) {
/* 39 */       this.metadata.add(ids[i]);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public List<String> getMetadata()
/*    */   {
/* 49 */     return this.metadata;
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 54 */     return this.raw;
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\services\version\Metadata.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
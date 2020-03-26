/*     */ package org.black_ixx.playerpoints.services.version;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PreReleaseType
/*     */   implements Comparable<PreReleaseType>
/*     */ {
/*  16 */   public static final PreReleaseType NONE = new PreReleaseType("");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final String type;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private String base;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  31 */   private final List<String> identifiers = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PreReleaseType(String in)
/*     */   {
/*  40 */     this.type = in;
/*     */     try {
/*  42 */       this.base = in.substring(0, in.indexOf("."));
/*     */     } catch (IndexOutOfBoundsException e) {
/*  44 */       this.base = in;
/*     */     }
/*     */     
/*  47 */     String[] ids = in.split("\\.");
/*  48 */     for (int i = 1; i < ids.length; i++) {
/*  49 */       this.identifiers.add(ids[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getType() {
/*  54 */     return this.type;
/*     */   }
/*     */   
/*     */   public String getBase() {
/*  58 */     return this.base;
/*     */   }
/*     */   
/*     */   public List<String> getIdentifiers() {
/*  62 */     return this.identifiers;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/*  67 */     return this.type;
/*     */   }
/*     */   
/*     */   public int compareTo(PreReleaseType o)
/*     */   {
/*  72 */     if ((this.type.isEmpty()) && (!o.getType().isEmpty())) {
/*  73 */       return -1;
/*     */     }
/*  75 */     int compare = o.getType().toLowerCase().compareTo(this.type.toLowerCase());
/*     */     
/*     */ 
/*  78 */     if (compare == 0) {
/*  79 */       int max = Math.max(this.identifiers.size(), o.getIdentifiers().size());
/*  80 */       for (int i = 0; i < max; i++) {
/*  81 */         String ours = "";
/*     */         try {
/*  83 */           ours = (String)this.identifiers.get(i);
/*     */         } catch (IndexOutOfBoundsException e) {
/*  85 */           compare = 1;
/*  86 */           break;
/*     */         }
/*  88 */         String theirs = "";
/*     */         try {
/*  90 */           theirs = (String)o.getIdentifiers().get(i);
/*     */         } catch (IndexOutOfBoundsException e) {
/*  92 */           compare = -1;
/*  93 */           break;
/*     */         }
/*  95 */         if (theirs.compareTo(ours) != 0) {
/*  96 */           compare = theirs.compareTo(ours);
/*  97 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 102 */     return compare;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 107 */     return this.type.hashCode();
/*     */   }
/*     */   
/*     */   public boolean equals(Object obj)
/*     */   {
/* 112 */     if ((obj instanceof PreReleaseType)) {
/* 113 */       return this.type.equals(((PreReleaseType)obj).getType());
/*     */     }
/* 115 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\services\version\PreReleaseType.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
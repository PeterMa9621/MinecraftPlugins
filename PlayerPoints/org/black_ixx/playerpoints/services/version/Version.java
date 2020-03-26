/*     */ package org.black_ixx.playerpoints.services.version;
/*     */ 
/*     */ import org.apache.commons.lang.StringUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Version
/*     */   implements Comparable<Version>
/*     */ {
/*     */   private final String version;
/*     */   private static final String SEPARATOR = ".";
/*  26 */   private boolean ignorePatch = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Version(String version)
/*     */   {
/*  35 */     this.version = version.replaceAll("\\s", "");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getVersion()
/*     */   {
/*  44 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMajor()
/*     */   {
/*  53 */     return parseNumber(this.version.substring(0, this.version.indexOf(".")));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinor()
/*     */   {
/*  62 */     int first = this.version.indexOf(".");
/*  63 */     int second = this.version.indexOf(".", first + 1);
/*  64 */     if (this.ignorePatch) {
/*  65 */       second = this.version.length();
/*  66 */       if (getLastSeparatorIndex() > 0) {
/*  67 */         second = getLastSeparatorIndex();
/*     */       }
/*     */     }
/*  70 */     return parseNumber(this.version.substring(first + 1, second));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPatch()
/*     */   {
/*  79 */     if (this.ignorePatch) {
/*  80 */       return 0;
/*     */     }
/*  82 */     int first = this.version.indexOf(".", this.version.indexOf(".") + 1) + 1;
/*  83 */     int second = this.version.length();
/*  84 */     if (getLastSeparatorIndex() > 0) {
/*  85 */       second = getLastSeparatorIndex();
/*     */     }
/*  87 */     return parseNumber(this.version.substring(first, second));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int getLastSeparatorIndex()
/*     */   {
/*  97 */     int last = -1;
/*  98 */     PreReleaseType type = getType();
/*  99 */     if (!type.equals(PreReleaseType.NONE)) {
/* 100 */       last = this.version.indexOf("-");
/*     */     } else {
/* 102 */       Metadata meta = getMetadata();
/* 103 */       if (!meta.equals(Metadata.NONE)) {
/* 104 */         last = this.version.indexOf("+");
/*     */       }
/*     */     }
/* 107 */     return last;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PreReleaseType getType()
/*     */   {
/* 116 */     int first = this.version.indexOf("-");
/* 117 */     int second = this.version.length();
/* 118 */     if (this.version.contains("+")) {
/* 119 */       second = this.version.indexOf("+");
/*     */     }
/* 121 */     if (first >= 0) {
/*     */       try {
/* 123 */         return new PreReleaseType(this.version.substring(first + 1, second));
/*     */       }
/*     */       catch (IndexOutOfBoundsException e) {}
/*     */     }
/*     */     
/* 128 */     return PreReleaseType.NONE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Metadata getMetadata()
/*     */   {
/* 137 */     int first = this.version.indexOf("+");
/* 138 */     if (first >= 0) {
/*     */       try {
/* 140 */         return new Metadata(this.version.substring(first + 1, this.version.length()));
/*     */       }
/*     */       catch (IndexOutOfBoundsException e) {}
/*     */     }
/*     */     
/*     */ 
/* 146 */     return Metadata.NONE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isIgnorePatch()
/*     */   {
/* 155 */     return this.ignorePatch;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setIgnorePatch(boolean ignorePatch)
/*     */   {
/* 165 */     this.ignorePatch = ignorePatch;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean validate()
/*     */   {
/* 174 */     boolean valid = false;
/*     */     try {
/* 176 */       getMajor();
/* 177 */       getMinor();
/* 178 */       getPatch();
/* 179 */       if (this.ignorePatch) {
/* 180 */         valid = StringUtils.countMatches(this.version, ".") >= 1;
/*     */       } else {
/* 182 */         valid = StringUtils.countMatches(this.version, ".") >= 2;
/*     */       }
/* 184 */       valid = (valid) && (StringUtils.countMatches(this.version, "-") <= 1);
/* 185 */       valid = (valid) && (StringUtils.countMatches(this.version, "+") <= 1);
/*     */     } catch (IndexOutOfBoundsException e) {
/* 187 */       valid = false;
/*     */     } catch (NumberFormatException e) {
/* 189 */       valid = false;
/*     */     }
/* 191 */     return valid;
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
/*     */   private int parseNumber(String in)
/*     */   {
/* 204 */     int number = Integer.parseInt(in);
/* 205 */     if (number < 0) {
/* 206 */       throw new NumberFormatException("No negative numbers in a version string.");
/*     */     }
/*     */     
/* 209 */     return number;
/*     */   }
/*     */   
/*     */   public int compareTo(Version o)
/*     */   {
/* 214 */     if (getMajor() != o.getMajor())
/* 215 */       return getMajor() - o.getMajor();
/* 216 */     if (getMinor() != o.getMinor())
/* 217 */       return getMinor() - o.getMinor();
/* 218 */     if (getPatch() != o.getPatch())
/* 219 */       return getPatch() - o.getPatch();
/* 220 */     if (getType() != o.getType()) {
/* 221 */       return o.getType().compareTo(getType());
/*     */     }
/* 223 */     return 0;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 228 */     return this.version.hashCode();
/*     */   }
/*     */   
/*     */   public boolean equals(Object obj)
/*     */   {
/* 233 */     if ((obj instanceof Version)) {
/* 234 */       return this.version.equals(((Version)obj).getVersion());
/*     */     }
/* 236 */     return false;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 241 */     return this.version;
/*     */   }
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\services\version\Version.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
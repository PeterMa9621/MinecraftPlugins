/*     */ package org.black_ixx.playerpoints.config;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum LocalizeNode
/*     */ {
/*  13 */   PERMISSION_DENY("message.noPermission", "&7%tag &cLack permission: &b%extra"), 
/*     */   
/*  15 */   CONSOLE_DENY("message.noConsole", "&7%tag &cCannot use command as console"), 
/*  16 */   NOT_INTEGER("message.notIntenger", "&7%tag &6%extra &cis not an integer"), 
/*  17 */   RELOAD("message.reload", "&7%tag &6Configuration reloaded"), 
/*  18 */   BROADCAST("message.broadcast", "&7%tag &9Player &a%player &9has &a%amount &9Points"), 
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  23 */   COMMAND_UNKNOWN("message.command.unknown", "&7%tag &cUnknown command '%extra'"), 
/*     */   
/*  25 */   COMMAND_GIVE("message.command.give", "&7%tag &9/points give <name> <points>"), 
/*     */   
/*  27 */   COMMAND_GIVEALL("message.command.giveall", "&7%tag &9/points giveall <points>"), 
/*  28 */   COMMAND_TAKE("message.command.take", "&7%tag &9/points take <name> <points>"), 
/*     */   
/*  30 */   COMMAND_LOOK("message.command.look", "&7%tag &9/points look <name>"), 
/*  31 */   COMMAND_PAY("message.command.pay", "&7%tag &9/points give <name> <points>"), 
/*  32 */   COMMAND_SET("message.command.set", "&7%tag &9/points set <name> <points>"), 
/*  33 */   COMMAND_RESET("message.command.reset", "&7%tag &9/points reset <name>"), 
/*  34 */   COMMAND_ME("message.command.me", "&7%tag &9/points me"), 
/*  35 */   COMMAND_LEAD("message.command.lead", "&7%tag &9/points lead [next|prev|#]"), 
/*  36 */   COMMAND_BROADCAST("message.command.broadcast", "&7%tag &9/points broadcast <name>"), 
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  41 */   POINTS_SUCCESS("message.points.success", "&7%tag &9Player &a%player &9now has &a%amount &9Points"), 
/*     */   
/*  43 */   POINTS_SUCCESS_ALL("message.points.successall", "&7%tag Gave &a%amount &9Points &7to %player players"), 
/*     */   
/*  45 */   POINTS_FAIL("message.points.fail", "&7%tag &cTransaction failed"), 
/*  46 */   POINTS_FAIL_ALL("message.points.failall", "&7%tag &cFailed to give &a%amount &9Points &cto %player players"), 
/*     */   
/*  48 */   POINTS_LOOK("message.points.look", "&7%tag &9Player &a%player &9has &a%amount &9Points"), 
/*     */   
/*  50 */   POINTS_PAY_SEND("message.points.pay.send", "&7%tag &9You have sent &a%amount &9Points to &a%player"), 
/*     */   
/*  52 */   POINTS_PAY_RECEIVE("message.points.pay.receive", "&7%tag &9You have received &a%amount &9Points from &a%player"), 
/*     */   
/*  54 */   POINTS_PAY_INVALID("message.points.pay.invalid", "&7%tag &6Cannot pay 0 or negative points."), 
/*     */   
/*  56 */   POINTS_LACK("message.points.lack", "&7%tag &6You do not have enough Points!"), 
/*     */   
/*  58 */   POINTS_RESET("message.points.reset", "&7%tag The points of &a%player &9was successfully reset"), 
/*     */   
/*  60 */   POINTS_ME("message.points.me", "&7%tag &9You have &a%amount &9Points"), 
/*     */   
/*     */ 
/*     */ 
/*  64 */   HELP_HEADER("message.help.header", "&9======= &7%tag &9======="), 
/*  65 */   HELP_ME("message.help.me", "&7/points me &6: Show current points"), 
/*  66 */   HELP_GIVE("message.help.give", "&7/points give <name> <points> &6: Generate points for given player"), 
/*     */   
/*  68 */   HELP_GIVEALL("message.help.giveall", "&7/points giveall <points> &6: Generate points for online players"), 
/*     */   
/*  70 */   HELP_TAKE("message.help.take", "&7/points take <name> <points> &6: Take points from player"), 
/*     */   
/*  72 */   HELP_LOOK("message.help.look", "&7/points look <name> &6: Lookup player's points"), 
/*     */   
/*  74 */   HELP_SET("message.help.set", "&7/points set <name> <points> &6: Set player's points to amount"), 
/*     */   
/*  76 */   HELP_RESET("message.help.reset", "&7/points reset <name> &6: Reset player's points to 0"), 
/*     */   
/*  78 */   HELP_PAY("message.help.pay", "&7/points pay <name> <points> &6: Send points to given player"), 
/*     */   
/*  80 */   HELP_LEAD("message.help.lead", "&7/points lead [prev/next/page] &6: Leader board"), 
/*     */   
/*  82 */   HELP_BROADCAST("message.help.broadcast", "&7/points broadcast <name> &6: Broadcast player's points"), 
/*     */   
/*  84 */   HELP_RELOAD("message.help.reload", "&7/points reload &6: Reload config");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private String path;
/*     */   
/*     */ 
/*     */ 
/*     */   private String def;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private LocalizeNode(String path, String def)
/*     */   {
/* 100 */     this.path = path;
/* 101 */     this.def = def;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getPath()
/*     */   {
/* 110 */     return this.path;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDefaultValue()
/*     */   {
/* 119 */     return this.def;
/*     */   }
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\config\LocalizeNode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
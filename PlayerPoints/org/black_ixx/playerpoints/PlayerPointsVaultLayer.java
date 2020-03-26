/*     */ package org.black_ixx.playerpoints;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ import net.milkbowl.vault.economy.Economy;
/*     */ import net.milkbowl.vault.economy.EconomyResponse;
/*     */ import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
/*     */ import org.black_ixx.playerpoints.services.IModule;
/*     */ import org.black_ixx.playerpoints.storage.StorageHandler;
/*     */ import org.bukkit.OfflinePlayer;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.plugin.ServicePriority;
/*     */ import org.bukkit.plugin.ServicesManager;
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
/*     */ public class PlayerPointsVaultLayer
/*     */   implements Economy, IModule
/*     */ {
/*     */   private PlayerPoints plugin;
/*     */   
/*     */   public PlayerPointsVaultLayer(PlayerPoints plugin)
/*     */   {
/*  35 */     this.plugin = plugin;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void starting()
/*     */   {
/*  42 */     this.plugin.getServer().getServicesManager().register(Economy.class, this, this.plugin, ServicePriority.Low);
/*     */   }
/*     */   
/*     */ 
/*     */   public void closing()
/*     */   {
/*  48 */     this.plugin.getServer().getServicesManager().unregister(Economy.class, this);
/*     */   }
/*     */   
/*     */   public boolean isEnabled()
/*     */   {
/*  53 */     return this.plugin.isEnabled();
/*     */   }
/*     */   
/*     */   public String getName()
/*     */   {
/*  58 */     return this.plugin.getName();
/*     */   }
/*     */   
/*     */   public boolean hasBankSupport()
/*     */   {
/*  63 */     return false;
/*     */   }
/*     */   
/*     */   public int fractionalDigits()
/*     */   {
/*  68 */     return 0;
/*     */   }
/*     */   
/*     */   public String format(double amount)
/*     */   {
/*  73 */     StringBuilder sb = new StringBuilder();
/*  74 */     int points = (int)amount;
/*  75 */     sb.append(points + " ");
/*  76 */     if (points == 1) {
/*  77 */       sb.append(currencyNameSingular());
/*     */     } else {
/*  79 */       sb.append(currencyNamePlural());
/*     */     }
/*  81 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public String currencyNamePlural()
/*     */   {
/*  86 */     return "Points";
/*     */   }
/*     */   
/*     */   public String currencyNameSingular()
/*     */   {
/*  91 */     return "Point";
/*     */   }
/*     */   
/*     */   public boolean hasAccount(String playerName)
/*     */   {
/*  96 */     boolean has = false;
/*  97 */     UUID id = handleTranslation(playerName);
/*  98 */     if (id != null) {
/*  99 */       has = ((StorageHandler)this.plugin.getModuleForClass(StorageHandler.class)).playerEntryExists(id.toString());
/*     */     }
/* 101 */     return has;
/*     */   }
/*     */   
/*     */   public boolean hasAccount(String playerName, String worldName)
/*     */   {
/* 106 */     return hasAccount(playerName);
/*     */   }
/*     */   
/*     */   public double getBalance(String playerName)
/*     */   {
/* 111 */     return this.plugin.getAPI().look(handleTranslation(playerName));
/*     */   }
/*     */   
/*     */   public double getBalance(String playerName, String world)
/*     */   {
/* 116 */     return getBalance(playerName);
/*     */   }
/*     */   
/*     */   public boolean has(String playerName, double amount)
/*     */   {
/* 121 */     int current = this.plugin.getAPI().look(handleTranslation(playerName));
/* 122 */     return current >= amount;
/*     */   }
/*     */   
/*     */   public boolean has(String playerName, String worldName, double amount)
/*     */   {
/* 127 */     return has(playerName, amount);
/*     */   }
/*     */   
/*     */   public EconomyResponse withdrawPlayer(String playerName, double amount)
/*     */   {
/* 132 */     int points = (int)amount;
/* 133 */     boolean result = this.plugin.getAPI().take(handleTranslation(playerName), points);
/* 134 */     int balance = this.plugin.getAPI().look(handleTranslation(playerName));
/*     */     
/* 136 */     EconomyResponse response = null;
/* 137 */     if (result) {
/* 138 */       response = new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
/*     */     }
/*     */     else {
/* 141 */       response = new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, "Lack funds");
/*     */     }
/*     */     
/* 144 */     return response;
/*     */   }
/*     */   
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 150 */     return withdrawPlayer(playerName, amount);
/*     */   }
/*     */   
/*     */   public EconomyResponse depositPlayer(String playerName, double amount)
/*     */   {
/* 155 */     int points = (int)amount;
/* 156 */     boolean result = this.plugin.getAPI().give(handleTranslation(playerName), points);
/* 157 */     int balance = this.plugin.getAPI().look(handleTranslation(playerName));
/*     */     
/* 159 */     EconomyResponse response = null;
/* 160 */     if (result) {
/* 161 */       response = new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
/*     */     }
/*     */     else {
/* 164 */       response = new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, null);
/*     */     }
/*     */     
/* 167 */     return response;
/*     */   }
/*     */   
/*     */ 
/*     */   public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
/*     */   {
/* 173 */     return depositPlayer(playerName, amount);
/*     */   }
/*     */   
/*     */   public EconomyResponse createBank(String name, String player)
/*     */   {
/* 178 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
/*     */   }
/*     */   
/*     */ 
/*     */   public EconomyResponse deleteBank(String name)
/*     */   {
/* 184 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
/*     */   }
/*     */   
/*     */ 
/*     */   public EconomyResponse bankBalance(String name)
/*     */   {
/* 190 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
/*     */   }
/*     */   
/*     */ 
/*     */   public EconomyResponse bankHas(String name, double amount)
/*     */   {
/* 196 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
/*     */   }
/*     */   
/*     */ 
/*     */   public EconomyResponse bankWithdraw(String name, double amount)
/*     */   {
/* 202 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
/*     */   }
/*     */   
/*     */ 
/*     */   public EconomyResponse bankDeposit(String name, double amount)
/*     */   {
/* 208 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
/*     */   }
/*     */   
/*     */ 
/*     */   public EconomyResponse isBankOwner(String name, String playerName)
/*     */   {
/* 214 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
/*     */   }
/*     */   
/*     */ 
/*     */   public EconomyResponse isBankMember(String name, String playerName)
/*     */   {
/* 220 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
/*     */   }
/*     */   
/*     */ 
/*     */   public List<String> getBanks()
/*     */   {
/* 226 */     return Collections.emptyList();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean createPlayerAccount(String playerName)
/*     */   {
/* 232 */     return true;
/*     */   }
/*     */   
/*     */   public boolean createPlayerAccount(String playerName, String worldName)
/*     */   {
/* 237 */     return createPlayerAccount(playerName);
/*     */   }
/*     */   
/*     */   private UUID handleTranslation(String name) {
/* 241 */     UUID id = null;
/*     */     try {
/* 243 */       UUID.fromString(name);
/*     */     } catch (IllegalArgumentException e) {
/* 245 */       id = this.plugin.translateNameToUUID(name);
/*     */     }
/* 247 */     return id;
/*     */   }
/*     */   
/*     */   public EconomyResponse createBank(String bank, OfflinePlayer player)
/*     */   {
/* 252 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean createPlayerAccount(OfflinePlayer player)
/*     */   {
/* 259 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean createPlayerAccount(OfflinePlayer player, String world)
/*     */   {
/* 265 */     return true;
/*     */   }
/*     */   
/*     */   public EconomyResponse depositPlayer(OfflinePlayer player, double amount)
/*     */   {
/* 270 */     int points = (int)amount;
/* 271 */     boolean result = this.plugin.getAPI().give(player.getUniqueId(), points);
/* 272 */     int balance = this.plugin.getAPI().look(player.getUniqueId());
/*     */     
/* 274 */     EconomyResponse response = null;
/* 275 */     if (result) {
/* 276 */       response = new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
/*     */     }
/*     */     else {
/* 279 */       response = new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, null);
/*     */     }
/*     */     
/* 282 */     return response;
/*     */   }
/*     */   
/*     */ 
/*     */   public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount)
/*     */   {
/* 288 */     return depositPlayer(player, amount);
/*     */   }
/*     */   
/*     */   public double getBalance(OfflinePlayer player)
/*     */   {
/* 293 */     return this.plugin.getAPI().look(player.getUniqueId());
/*     */   }
/*     */   
/*     */   public double getBalance(OfflinePlayer player, String world)
/*     */   {
/* 298 */     return getBalance(player);
/*     */   }
/*     */   
/*     */   public boolean has(OfflinePlayer player, double amount)
/*     */   {
/* 303 */     int current = this.plugin.getAPI().look(player.getUniqueId());
/* 304 */     return current >= amount;
/*     */   }
/*     */   
/*     */   public boolean has(OfflinePlayer player, String world, double amount)
/*     */   {
/* 309 */     return has(player, amount);
/*     */   }
/*     */   
/*     */   public boolean hasAccount(OfflinePlayer player)
/*     */   {
/* 314 */     return ((StorageHandler)this.plugin.getModuleForClass(StorageHandler.class)).playerEntryExists(player.getUniqueId().toString());
/*     */   }
/*     */   
/*     */   public boolean hasAccount(OfflinePlayer player, String world)
/*     */   {
/* 319 */     return hasAccount(player);
/*     */   }
/*     */   
/*     */   public EconomyResponse isBankMember(String bank, OfflinePlayer player)
/*     */   {
/* 324 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
/*     */   }
/*     */   
/*     */ 
/*     */   public EconomyResponse isBankOwner(String bank, OfflinePlayer player)
/*     */   {
/* 330 */     return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not handle banks.");
/*     */   }
/*     */   
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount)
/*     */   {
/* 336 */     int points = (int)amount;
/* 337 */     boolean result = this.plugin.getAPI().take(player.getUniqueId(), points);
/* 338 */     int balance = this.plugin.getAPI().look(player.getUniqueId());
/*     */     
/* 340 */     EconomyResponse response = null;
/* 341 */     if (result) {
/* 342 */       response = new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
/*     */     }
/*     */     else {
/* 345 */       response = new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, "Lack funds");
/*     */     }
/*     */     
/* 348 */     return response;
/*     */   }
/*     */   
/*     */ 
/*     */   public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount)
/*     */   {
/* 354 */     return withdrawPlayer(player, amount);
/*     */   }
/*     */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\org\black_ixx\playerpoints\PlayerPointsVaultLayer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
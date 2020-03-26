package vipSystem;

import me.lucko.luckperms.common.util.Uuids;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.luckperms.api.*;
import vipSystem.util.Util;

import static vipSystem.util.Util.getNewDate;

public class VipSystem extends JavaPlugin
{
	HashMap<String, String> vipGroups = new HashMap<String, String>();
	String defaultGroup = "";
	
	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd-HH");
	
	VipSystemAPI api = new VipSystemAPI(this);
	
	HashMap<String, VipReward> reward = new HashMap<String, VipReward>();

	HashMap<UUID, VipPlayer> players = new HashMap<>();

	ConfigLoader configLoader = new ConfigLoader(this);

	public Economy economy;
	public LuckPerms luckPerms;

	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null)
		{
			economy = economyProvider.getProvider();
		}
		return (economy!=null);
	}

	private boolean setupLuckPerms()
	{
		RegisteredServiceProvider<LuckPerms> economyProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (economyProvider != null)
		{
			luckPerms = economyProvider.getProvider();
		}
		return (luckPerms!=null);
	}

	public void onEnable()
	{
		if(!new File(getDataFolder(),"Data").exists()) 
		{
			new File(getDataFolder(),"Data").mkdirs();
		}
		if(!setupEconomy())
			Bukkit.getConsoleSender().sendMessage("§a[vipSystem] §cVault插件未加载!");
		if(!setupLuckPerms()){
			Bukkit.getConsoleSender().sendMessage("§a[vipSystem] §cLuckPerms插件未加载!");
			Bukkit.getServicesManager().unregister(this);
			return;
		}

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new VipSystemExpansion(this).register();
		}

		defaultGroup = configLoader.loadConfig(vipGroups);
		configLoader.loadItems(vipGroups, reward);

		getServer().getPluginManager().registerEvents(new VipSystemListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[vipSystem] §e会员系统加载完毕");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[vipSystem] §e会员系统卸载完毕");
	}
	
	public VipSystemAPI getAPI()
	{
		return api;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("vip"))
		{
			if (args.length==0)
			{
				sender.sendMessage("§6=========[会员系统]=========");
				sender.sendMessage("§a/vip my      §3查看我的会员状态");
				if(sender.isOp())
				{
					//sender.sendMessage("§a/vip list      §3列出所有会员及其状态");
					sender.sendMessage("§a/vip check [玩家名]      §3查看目标玩家的会员状态");
					sender.sendMessage("§a/vip give [玩家名] [会员组] [会员时长(天)] §3设定目标玩家特定天数的会员");
					sender.sendMessage("§a/vip remove [玩家名]      §3强制撤销目标玩家会员");
					sender.sendMessage("§a/vip add [玩家名] [会员时长(天)]      §3强制增加目标玩家会员时长");
					sender.sendMessage("§a/vip reload      §3重载插件配置");
					sender.sendMessage("§a/vip reloadp      §3重载玩家配置§4(仅供测试用)");
				}
				return true;
			}

			/*
			if(args[0].equalsIgnoreCase("list"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("§6[会员系统] §4你没有权限");
					return true;
				}
				sender.sendMessage("§6======================================");
				for(String vipName:vipData.keySet())
				{
					sender.sendMessage("§6玩家:§e"+vipData.get(vipName).playerName+"§6,会员类型:§c"
				+vipGroups.get(vipData.get(vipName).getVipGroup())+"§6,剩余时长:§2"+vipData.get(vipName).getLeftTime());
				}
				sender.sendMessage("§6======================================");
				return true;
			}

			 */


			if(args[0].equalsIgnoreCase("reloadp"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("§6[会员系统] §4你没有权限");
					return true;
				}
				players.clear();
				sender.sendMessage("§6[会员系统] §a玩家配置重载完毕");
				return true;
			}

			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("§6[会员系统] §4你没有权限");
					return true;
				}
				defaultGroup = configLoader.loadConfig(vipGroups);
				configLoader.loadItems(vipGroups, reward);
				sender.sendMessage("§6[会员系统] §a配置重载完毕");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("check"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("§6[会员系统] §4你没有权限");
					return true;
				}
					
				if(args.length!=2)
				{
					sender.sendMessage("§a/vip check [玩家名] §3查看目标玩家的会员状态");
					return true;
				}

				UUID uniqueId = Util.getPlayerUUID(args[1]);

				VipPlayer vipPlayer = null;
				try {
					vipPlayer = configLoader.loadPlayerConfig(uniqueId);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(vipPlayer==null || vipPlayer.isDeadline())
				{
					sender.sendMessage("§6[会员系统] §c该玩家不是会员或会员已到期");
					return true;
				}

				String msg = String.format("§6[会员系统] §e玩家名:§c%s§e会员类型:§c%s§e,剩余会员时间:§a%d天,%d小时,%d分钟", args[1], vipGroups.get(vipPlayer.getVipGroup()), vipPlayer.getLeftDays()%365, vipPlayer.getLeftHours()%24, vipPlayer.getLeftMinutes()%60);
				sender.sendMessage(msg);
				
				return true;
			}
			
			if(args[0].equalsIgnoreCase("add"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("§6[会员系统] §4你没有权限");
					return true;
				}
				
				if(args.length!=3)
				{
					sender.sendMessage("§6[会员系统] §a/vip add [玩家名] [会员时长(天)] §3强制增加目标玩家会员时长");
					return true;
				}

				UUID uniqueId = Util.getPlayerUUID(args[1]);

				VipPlayer vipPlayer = null;
				try {
					vipPlayer = configLoader.loadPlayerConfig(uniqueId);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(vipPlayer==null || vipPlayer.isDeadline())
				{
					sender.sendMessage("§6[会员系统] §c该玩家不是会员或会员已到期");
					return true;
				}
				
				if(!args[2].matches("[0-9]*") || Integer.parseInt(args[2])<=0)
				{
					sender.sendMessage("§6[会员系统] §c无效的会员时长(单位为§3天§c)");
					return true;
				}

				LocalDateTime newDeadline = getNewDate(vipPlayer.getDeadline(), Integer.parseInt(args[2]));
				vipPlayer.setDeadline(newDeadline);

				try {
					Util.addVip(vipPlayer, configLoader, players);
				} catch (SQLException e) {
					e.printStackTrace();
				}

				sender.sendMessage("§6[会员系统] §e已为玩家§c"+args[1]+"§e增加了§a"+args[2]+"§e天会员");
				if(Bukkit.getServer().getPlayer(args[1])!=null)
				{
					Bukkit.getServer().getPlayer(args[1]).sendMessage("§6[会员系统] §e你已被增加了§a"+args[2]+"§e天会员");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("remove"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("§6[会员系统] §4你没有权限");
					return true;
				}
				
				if(args.length!=2)
				{
					sender.sendMessage("§6[会员系统] §a/vip remove [玩家名] §3强制撤销目标玩家会员");
					return true;
				}

				UUID uniqueId = Util.getPlayerUUID(args[1]);

				VipPlayer vipPlayer = null;
				try {
					vipPlayer = configLoader.loadPlayerConfig(uniqueId);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(vipPlayer==null || vipPlayer.isDeadline())
				{
					sender.sendMessage("§6[会员系统] §c该玩家不是会员或会员已到期");
					return true;
				}

				try {
					Util.removeVip(vipPlayer, configLoader, players);
				} catch (SQLException e) {
					e.printStackTrace();
				}

				sender.sendMessage("§6[会员系统] §e已移除玩家§c"+args[1]+"§e的会员");
				if(Bukkit.getServer().getPlayer(args[1])!=null)
				{
					Bukkit.getServer().getPlayer(args[1]).sendMessage("§6[会员系统] §e你的会员已被移除");
				}

				return true;
			}
			
			if(args[0].equalsIgnoreCase("my"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					try {
						if(configLoader.loadPlayerConfig(p.getUniqueId())==null)
						{
							sender.sendMessage("§6[会员系统] §c你还不是会员");
							return true;
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					VipPlayer vip = null;
					try {
						vip = configLoader.loadPlayerConfig(p.getUniqueId());
					} catch (SQLException e) {
						e.printStackTrace();
					}
					String msg = String.format("§6[会员系统] §e会员类型:§c%s§e,剩余会员时间:§a%d天,%d小时,%d分钟", vipGroups.get(vip.getVipGroup()), vip.getLeftDays()%365, vip.getLeftHours()%24, vip.getLeftMinutes()%60);
					p.sendMessage(msg);
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("give"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("§6[会员系统] §4你没有权限");
					return true;
				}
				
				if(args.length!=4)
				{
					sender.sendMessage("§c/vip give [玩家名] [会员组] [会员时长(天)] §3给予目标玩家特定天数的会员");
					return true;
				}

				if(Bukkit.getServer().getPlayer(args[1])==null)
				{
					sender.sendMessage("§6[会员系统] §c该玩家不存在或不在线");
					return true;
				}
				if(!vipGroups.containsKey(args[2]))
				{
					sender.sendMessage("§6[会员系统] §c不存在的会员组");
					return true;
				}
				if(!args[3].matches("[0-9]*") || Integer.valueOf(args[3])<=0)
				{
					sender.sendMessage("§6[会员系统] §c无效的会员时长(单位为§3天§c)");
					return true;
				}
				int days = Integer.valueOf(args[3]);
				String playerName = args[1];
				String group = args[2];
				Player p = Bukkit.getPlayer(playerName);
				VipPlayer vipPlayer = null;
				try {
					vipPlayer = configLoader.loadPlayerConfig(p.getUniqueId());
				} catch (SQLException e) {
					e.printStackTrace();
				}

				if(vipPlayer!=null && vipPlayer.getVipGroup().equalsIgnoreCase(args[2]))
				{
					LocalDateTime newDeadlineDate = getNewDate(vipPlayer.getDeadline(), days);
					vipPlayer.setDeadline(newDeadlineDate);

					Bukkit.getServer().getPlayer(args[1]).sendMessage("§6[会员系统] §e成功续费§c"+vipGroups.get(args[2])+" §e"+args[3]+"天");
				}
				else
				{
					int prevVipLeftDays = 0;
					if(vipPlayer!=null)
						prevVipLeftDays = vipPlayer.getLeftDays();
					LocalDateTime recentDate = LocalDateTime.now();
					LocalDateTime deadlineDate = getNewDate(recentDate, days + prevVipLeftDays/2);
					vipPlayer = new VipPlayer(p.getUniqueId(), args[1], recentDate, deadlineDate, args[2]);

					p.sendMessage("§6[会员系统] §e成功开通§c"+vipGroups.get(args[2])+" §e"+args[3]+"天");
					Bukkit.getServer().broadcastMessage("§6玩家§a"+args[1]+"§6成为§c"+vipGroups.get(args[2]));
				}

				try {
					Util.addVip(vipPlayer, configLoader, players);
				} catch (SQLException e) {
					e.printStackTrace();
				}

				if(!reward.get(group).getItems().isEmpty() && reward.get(group).money!=0)
				{
					for(ItemStack item:reward.get(group).getItems())
					{
						if(p.getInventory().firstEmpty()!=-1)
						{
							p.getInventory().addItem(item);
						}
						else
						{
							Bukkit.getServer().getWorld(p.getWorld().getName()).dropItem(p.getLocation(), item);
						}
					}
					economy.depositPlayer(p.getName(), reward.get(group).money);
					p.sendMessage("§6[会员系统] §a物品奖励已放入背包内，如果背包满了，则会掉落到地面上，请注意拾取。");
				}
				
				if(sender instanceof Player)
				{
					if(!p.getName().equalsIgnoreCase(args[1]))
						sender.sendMessage("§6[会员系统] §e已给予玩家§c"+args[1]+" §a"+vipGroups.get(args[2])+" §c"+args[3]+"§e天");
				}
				else
					sender.sendMessage("§6[会员系统] §e已给予玩家§c"+args[1]+" §a"+vipGroups.get(args[2])+" §c"+args[3]+"§e天");
				return true;
			}
			return true;
		}
		return false;
		
	}
}


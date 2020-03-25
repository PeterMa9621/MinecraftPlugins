package vipSystem;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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

public class VipSystem extends JavaPlugin
{
	HashMap<String, VipPlayer> vipData = new HashMap<String, VipPlayer>();
	HashMap<String, String> vipGroups = new HashMap<String, String>();
	String defaultGroup = "";
	
	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd-HH");
	
	VipSystemAPI api = new VipSystemAPI(this);
	
	HashMap<String, VipReward> reward = new HashMap<String, VipReward>();
	
	public Economy economy;
	
	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null)
		{
			economy = economyProvider.getProvider();
		}
		return (economy!=null);
	}
	
	public void onEnable()
	{
		if(!new File(getDataFolder(),"Data").exists()) 
		{
			new File(getDataFolder(),"Data").mkdirs();
		}
		if(setupEconomy()==false)
			Bukkit.getConsoleSender().sendMessage("§a[vipSystem] §cVault插件未加载!");
		loadConfig();
		loadItems();
		loadPlayerConfig();
		getServer().getPluginManager().registerEvents(new VipSystemListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[vipSystem] §e会员系统加载完毕");
	}

	public void onDisable() 
	{
		savePlayerConfig();
		Bukkit.getConsoleSender().sendMessage("§a[vipSystem] §e会员系统卸载完毕");
	}
	
	public VipSystemAPI getAPI()
	{
		return api;
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;

		if(!file.exists())
		{
			config = load(file);
			ArrayList<String> vipGroups = new ArrayList<String>();
			vipGroups.add("VIP1");
			vipGroups.add("VIP2");
			vipGroups.add("VIP3");
			vipGroups.add("VIP4");
			config.set("VipSystem.DefaultGroup", "Builder");
			config.set("VipSystem.VIPGroups", vipGroups);
			ArrayList<String> vipGroupsName = new ArrayList<String>();
			vipGroupsName.add("初级会员");
			vipGroupsName.add("中级会员");
			vipGroupsName.add("高级会员");
			vipGroupsName.add("至尊会员");
			
			config.set("VipSystem.VIPGroupsName", vipGroupsName);
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			loadConfig();
		}
		config = load(file);
		int index=0;
		for(String vipGroup:config.getStringList("VipSystem.VIPGroups"))
		{
			vipGroups.put(vipGroup, config.getStringList("VipSystem.VIPGroupsName").get(index));
			index++;
		}

		defaultGroup = config.getString("VipSystem.DefaultGroup");
		return;
	}
	
	public void loadItems()
	{
		File file=new File(getDataFolder(),"items.yml");
		FileConfiguration config;

		if(!file.exists())
		{
			config = load(file);

			config.set("VIP1.Money", 3000);
			config.set("VIP1.Items.1.ID", "diamond");
			config.set("VIP1.Items.1.Amount", 32);
			config.set("VIP1.Items.1.DisplayName", "钻石");
			config.set("VIP1.Items.1.Lore", "神奇钻石");
			config.set("VIP1.Items.1.Enchant.ID", "fortune");
			config.set("VIP1.Items.1.Enchant.Level", 1);
			config.set("VIP1.Items.1.HideEnchant", true);
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			loadItems();
			return;
		}
		
		config = load(file);
		
		for(String vipGroup:vipGroups.keySet())
		{
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			int money = config.getInt(vipGroup + ".Money");
			for(int i=0; config.contains(vipGroup + ".Items."+(i+1)); i++)
			{
				String id = "";
				boolean hide = config.getBoolean(vipGroup + ".Items."+(i+1)+".HideEnchant");
				if(config.getString(vipGroup + ".Items."+(i+1)+".ID").contains(":")) {
					id = config.getString(vipGroup + ".Items."+(i+1)+".ID").split(":")[0];
				}
				else {
					id = config.getString(vipGroup + ".Items."+(i+1)+".ID");
				}
				int amount = config.getInt(vipGroup + ".Items."+(i+1)+".Amount");
				String name = config.getString(vipGroup + ".Items."+(i+1)+".DisplayName");
				String lore = config.getString(vipGroup + ".Items."+(i+1)+".Lore");
				String enchantID = config.getString(vipGroup + ".Items."+(i+1)+".Enchant.ID");
				int level = config.getInt(vipGroup + ".Items."+(i+1)+".Enchant.Level");

				ItemStack item = new ItemStack(Material.getMaterial(id.toUpperCase()), amount);
				ItemMeta meta = item.getItemMeta();
				if(name!=null)
					meta.setDisplayName(name);
				if(lore!=null)
				{
					ArrayList<String> itemLore = new ArrayList<String>();
					for(String l:lore.split("%"))
					{
						itemLore.add(l);
					}
					meta.setLore(itemLore);
				}
				if(hide==true)
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);

				if(!enchantID.equalsIgnoreCase("") && level>0)
				{
					item.addUnsafeEnchantment(EnchantmentWrapper.getByKey(NamespacedKey.minecraft(enchantID)), level);
				}
				items.add(item);
			}
			VipReward vipReward = new VipReward(items, money);
			reward.put(vipGroup, vipReward);
		}
		
		return;
	}
	
	public void loadPlayerConfig()
	{
		for(OfflinePlayer p:Bukkit.getOfflinePlayers())
		{
			File file=new File(getDataFolder(),"/Data/"+p.getName()+".yml");
			FileConfiguration config;
			if (file.exists())
			{
				config = load(file);
				
				String regDate = config.getString(p.getName()+".RegisterDate");
				String deadline = config.getString(p.getName()+".DeadlineDate");
				String vipGroup = config.getString(p.getName()+".VIPGroup");
				
				VipPlayer vipPlayer = new VipPlayer(p.getName(), regDate, deadline, vipGroup);
				
				vipData.put(p.getName(), vipPlayer);
			}
		}
	}

	public void savePlayerConfig()
	{
		for(String playerName:vipData.keySet())
		{
			File file=new File(getDataFolder(),"/Data/"+playerName+".yml");
			FileConfiguration config;

			config = load(file);
			
			VipPlayer vipPlayer = vipData.get(playerName);
			
			String regDate = vipPlayer.getRegDate();
			String deadline = vipPlayer.getDeadline();
			String vipGroup = vipPlayer.getVipGroup();
			
			config.set(playerName+".RegisterDate", regDate);
			config.set(playerName+".DeadlineDate", deadline);
			config.set(playerName+".VIPGroup", vipGroup);
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ItemStack createItem(String ID, int quantity, int durability, String displayName, String lore)
	{
		ItemStack item = new ItemStack(Material.getMaterial(ID), quantity, (short)durability);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		ArrayList<String> loreList = new ArrayList<String>();
		for(String l:lore.split("%"))
		{
			loreList.add(l);
		}
		meta.setLore(loreList);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack createItem(String ID, int quantity, int durability, String displayName)
	{
		ItemStack item = new ItemStack(Material.getMaterial(ID), quantity, (short)durability);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public FileConfiguration load(File file)
	{
        if (!(file.exists())) 
        { //假如文件不存在
        	try   //捕捉异常，因为有可能创建不成功
        	{
        		file.createNewFile();
        	}
        	catch(IOException e)
        	{
        		e.printStackTrace();
        	}
        }
        return YamlConfiguration.loadConfiguration(file);
	}
	
	public FileConfiguration load(String path)
	{
		File file=new File(path);
		if(!file.exists())
		{
			try
		{
				file.createNewFile();
		}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return YamlConfiguration.loadConfiguration(new File(path));
	}
	
	public static String getNewDate(String recentDate, int days)
	{
		int[] monthList = {31,28,31,30,31,30,31,31,30,31,30,31};
		int recentYear = Integer.valueOf(recentDate.split("-")[0]);
		int recentMonth = Integer.valueOf(recentDate.split("-")[1]);
		int recentDay = Integer.valueOf(recentDate.split("-")[2]);
		int recentHour = Integer.valueOf(recentDate.split("-")[3]);
		
		if(days>(monthList[recentMonth-1]-recentDay))
		{
			days -= (monthList[recentMonth-1]-recentDay);
			recentDay=1;
			if(recentMonth<12)
				recentMonth++;
			else
			{
				recentMonth=1;
				recentYear++;
			}
			
			while(days>0)
			{
				if(days/monthList[recentMonth-1]!=0 &&
						days-monthList[recentMonth-1]!=0)
				{
					days -= monthList[recentMonth-1];
					if(recentMonth<12)
						recentMonth++;
					else
					{
						recentMonth=1;
						recentYear++;
					}
				}
				else
				{
					recentDay=days;
					break;
				}
			}
		}
		else
		{
			recentDay += days;
			days=0;
		}
		
		String newDate = ""+recentYear+"-"+recentMonth+"-"+recentDay+"-"+recentHour;
		return newDate;
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
					sender.sendMessage("§a/vip list      §3列出所有会员及其状态");
					sender.sendMessage("§a/vip check [玩家名]      §3查看目标玩家的会员状态");
					sender.sendMessage("§a/vip give [玩家名] [会员组] [会员时长(天)] §3设定目标玩家特定天数的会员");
					sender.sendMessage("§a/vip remove [玩家名]      §3强制撤销目标玩家会员");
					sender.sendMessage("§a/vip add [玩家名] [会员时长(天)]      §3强制增加目标玩家会员时长");
					sender.sendMessage("§a/vip reload      §3重载插件配置");
					sender.sendMessage("§a/vip reloadp      §3重载玩家配置§4(仅供测试用)");
				}
				return true;
			}
			
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
			
			if(args[0].equalsIgnoreCase("reloadp"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("§6[会员系统] §4你没有权限");
					return true;
				}
				loadPlayerConfig();
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
				loadConfig();
				loadItems();
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
				
				if(Bukkit.getServer().getPlayer(args[1])==null && Bukkit.getServer().getOfflinePlayer(args[1])==null)
				{
					sender.sendMessage("§6[会员系统] §c该玩家不存在或不在线");
					return true;
				}
				
				if(!vipData.containsKey(args[1]))
				{
					sender.sendMessage("§6[会员系统] §c该玩家不是会员或会员已到期");
					return true;
				}
				
				VipPlayer vipPlayer = vipData.get(args[1]);
				
				sender.sendMessage("§6[会员系统] §e玩家名:§c"+args[1]+"§e会员类型:§c"+vipGroups.get(vipPlayer.getVipGroup())+"§e,剩余会员时间:§a"+vipPlayer.getLeftDays()+"天,"+vipPlayer.getLeftHours()%24+"小时");
				
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
				
				if(Bukkit.getServer().getPlayer(args[1])==null && Bukkit.getServer().getOfflinePlayer(args[1])==null)
				{
					sender.sendMessage("§6[会员系统] §c该玩家不存在或不在线");
					return true;
				}
				
				if(!vipData.containsKey(args[1]))
				{
					sender.sendMessage("§6[会员系统] §c该玩家不是会员或会员已到期");
					return true;
				}
				
				if(!args[2].matches("[0-9]*") || Integer.valueOf(args[2])<=0)
				{
					sender.sendMessage("§6[会员系统] §c无效的会员时长(单位为§3天§c)");
					return true;
				}
				
				VipPlayer vipPlayer = vipData.get(args[1]);
				String newDeadline = getNewDate(vipPlayer.getDeadline(), Integer.valueOf(args[2]));
				vipPlayer.setDeadline(newDeadline);
				vipData.put(args[1], vipPlayer);
				
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
				
				if(Bukkit.getServer().getPlayer(args[1])==null && Bukkit.getServer().getOfflinePlayer(args[1])==null)
				{
					sender.sendMessage("§6[会员系统] §c该玩家不存在或不在线");
					return true;
				}
				
				if(!vipData.containsKey(args[1]))
				{
					sender.sendMessage("§6[会员系统] §c该玩家不是会员或会员已到期");
					return true;
				}
				
				vipData.remove(args[1]);
				File file=new File(getDataFolder(),"/Data/"+args[1]+".yml");
				if(file.exists())
					file.delete();
				sender.sendMessage("§6[会员系统] §e已移除玩家§c"+args[1]+"§e的会员");
				if(Bukkit.getServer().getPlayer(args[1])!=null)
				{
					Bukkit.getServer().getPlayer(args[1]).sendMessage("§6[会员系统] §e你的会员已被移除");
				}
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuadd "+args[1]+" "+defaultGroup);
				return true;
			}
			
			if(args[0].equalsIgnoreCase("my"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(!vipData.containsKey(p.getName()))
					{
						sender.sendMessage("§6[会员系统] §c你还不是会员");
						return true;
					}
					VipPlayer vip = vipData.get(p.getName());
					p.sendMessage("§6[会员系统] §e会员类型:§c"+vipGroups.get(vip.getVipGroup())+"§e,剩余会员时间:§a"+vip.getLeftDays()+"天,"+vip.getLeftHours()%24+"小时");
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
				String group = args[2];
				if(vipData.containsKey(args[1]) && vipData.get(args[1]).getVipGroup().equalsIgnoreCase(args[2]))
				{
					VipPlayer vipPlayer = vipData.get(args[1]);
					String newDeadlineDate = getNewDate(vipPlayer.getDeadline(), days);
					vipPlayer.setDeadline(newDeadlineDate);
					vipData.put(args[1], vipPlayer);
					Bukkit.getServer().getPlayer(args[1]).sendMessage("§6[会员系统] §e成功续费§c"+vipGroups.get(args[2])+" §e"+args[3]+"天");
				}
				else
				{
					String recentDate = date.format(new Date());
					String deadlineDate = getNewDate(recentDate, days);
					VipPlayer vipPlayer = new VipPlayer(args[1], recentDate, deadlineDate, args[2]);
					vipData.put(args[1], vipPlayer);
					Bukkit.getServer().getPlayer(args[1]).sendMessage("§6[会员系统] §e成功开通§c"+vipGroups.get(args[2])+" §e"+args[3]+"天");
					Bukkit.getServer().broadcastMessage("§6恭喜玩家§a"+args[1]+"§6成为§c"+vipGroups.get(args[2]));
				}
				
				if(!reward.get(group).getItems().isEmpty() && reward.get(group).money!=0)
				{
					Player whoWantToBeVip = Bukkit.getServer().getPlayer(args[1]);
					for(ItemStack item:reward.get(group).getItems())
					{
						if(whoWantToBeVip.getInventory().firstEmpty()!=-1)
						{
							whoWantToBeVip.getInventory().addItem(item);
						}
						else
						{
							Bukkit.getServer().getWorld(whoWantToBeVip.getWorld().getName()).dropItem(whoWantToBeVip.getLocation(), item);
						}
					}
					economy.depositPlayer(whoWantToBeVip.getName(), reward.get(group).money);
					whoWantToBeVip.sendMessage("§6[会员系统] §a物品奖励已放入背包内，如果背包满了，则会掉落到地面上，请注意拾取。");
				}

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuadd "+args[1]+" "+args[2]);
				
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
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


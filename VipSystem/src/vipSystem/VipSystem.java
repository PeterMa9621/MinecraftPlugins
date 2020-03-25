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
			Bukkit.getConsoleSender().sendMessage("��a[vipSystem] ��cVault���δ����!");
		loadConfig();
		loadItems();
		loadPlayerConfig();
		getServer().getPluginManager().registerEvents(new VipSystemListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[vipSystem] ��e��Աϵͳ�������");
	}

	public void onDisable() 
	{
		savePlayerConfig();
		Bukkit.getConsoleSender().sendMessage("��a[vipSystem] ��e��Աϵͳж�����");
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
			vipGroupsName.add("������Ա");
			vipGroupsName.add("�м���Ա");
			vipGroupsName.add("�߼���Ա");
			vipGroupsName.add("�����Ա");
			
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
			config.set("VIP1.Items.1.DisplayName", "��ʯ");
			config.set("VIP1.Items.1.Lore", "������ʯ");
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
        { //�����ļ�������
        	try   //��׽�쳣����Ϊ�п��ܴ������ɹ�
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
				sender.sendMessage("��6=========[��Աϵͳ]=========");
				sender.sendMessage("��a/vip my      ��3�鿴�ҵĻ�Ա״̬");
				if(sender.isOp())
				{
					sender.sendMessage("��a/vip list      ��3�г����л�Ա����״̬");
					sender.sendMessage("��a/vip check [�����]      ��3�鿴Ŀ����ҵĻ�Ա״̬");
					sender.sendMessage("��a/vip give [�����] [��Ա��] [��Աʱ��(��)] ��3�趨Ŀ������ض������Ļ�Ա");
					sender.sendMessage("��a/vip remove [�����]      ��3ǿ�Ƴ���Ŀ����һ�Ա");
					sender.sendMessage("��a/vip add [�����] [��Աʱ��(��)]      ��3ǿ������Ŀ����һ�Աʱ��");
					sender.sendMessage("��a/vip reload      ��3���ز������");
					sender.sendMessage("��a/vip reloadp      ��3����������á�4(����������)");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("list"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[��Աϵͳ] ��4��û��Ȩ��");
					return true;
				}
				sender.sendMessage("��6======================================");
				for(String vipName:vipData.keySet())
				{
					sender.sendMessage("��6���:��e"+vipData.get(vipName).playerName+"��6,��Ա����:��c"
				+vipGroups.get(vipData.get(vipName).getVipGroup())+"��6,ʣ��ʱ��:��2"+vipData.get(vipName).getLeftTime());
				}
				sender.sendMessage("��6======================================");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reloadp"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[��Աϵͳ] ��4��û��Ȩ��");
					return true;
				}
				loadPlayerConfig();
				sender.sendMessage("��6[��Աϵͳ] ��a��������������");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[��Աϵͳ] ��4��û��Ȩ��");
					return true;
				}
				loadConfig();
				loadItems();
				sender.sendMessage("��6[��Աϵͳ] ��a�����������");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("check"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[��Աϵͳ] ��4��û��Ȩ��");
					return true;
				}
					
				if(args.length!=2)
				{
					sender.sendMessage("��a/vip check [�����] ��3�鿴Ŀ����ҵĻ�Ա״̬");
					return true;
				}
				
				if(Bukkit.getServer().getPlayer(args[1])==null && Bukkit.getServer().getOfflinePlayer(args[1])==null)
				{
					sender.sendMessage("��6[��Աϵͳ] ��c����Ҳ����ڻ�����");
					return true;
				}
				
				if(!vipData.containsKey(args[1]))
				{
					sender.sendMessage("��6[��Աϵͳ] ��c����Ҳ��ǻ�Ա���Ա�ѵ���");
					return true;
				}
				
				VipPlayer vipPlayer = vipData.get(args[1]);
				
				sender.sendMessage("��6[��Աϵͳ] ��e�����:��c"+args[1]+"��e��Ա����:��c"+vipGroups.get(vipPlayer.getVipGroup())+"��e,ʣ���Աʱ��:��a"+vipPlayer.getLeftDays()+"��,"+vipPlayer.getLeftHours()%24+"Сʱ");
				
				return true;
			}
			
			if(args[0].equalsIgnoreCase("add"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[��Աϵͳ] ��4��û��Ȩ��");
					return true;
				}
				
				if(args.length!=3)
				{
					sender.sendMessage("��6[��Աϵͳ] ��a/vip add [�����] [��Աʱ��(��)] ��3ǿ������Ŀ����һ�Աʱ��");
					return true;
				}
				
				if(Bukkit.getServer().getPlayer(args[1])==null && Bukkit.getServer().getOfflinePlayer(args[1])==null)
				{
					sender.sendMessage("��6[��Աϵͳ] ��c����Ҳ����ڻ�����");
					return true;
				}
				
				if(!vipData.containsKey(args[1]))
				{
					sender.sendMessage("��6[��Աϵͳ] ��c����Ҳ��ǻ�Ա���Ա�ѵ���");
					return true;
				}
				
				if(!args[2].matches("[0-9]*") || Integer.valueOf(args[2])<=0)
				{
					sender.sendMessage("��6[��Աϵͳ] ��c��Ч�Ļ�Աʱ��(��λΪ��3���c)");
					return true;
				}
				
				VipPlayer vipPlayer = vipData.get(args[1]);
				String newDeadline = getNewDate(vipPlayer.getDeadline(), Integer.valueOf(args[2]));
				vipPlayer.setDeadline(newDeadline);
				vipData.put(args[1], vipPlayer);
				
				sender.sendMessage("��6[��Աϵͳ] ��e��Ϊ��ҡ�c"+args[1]+"��e�����ˡ�a"+args[2]+"��e���Ա");
				if(Bukkit.getServer().getPlayer(args[1])!=null)
				{
					Bukkit.getServer().getPlayer(args[1]).sendMessage("��6[��Աϵͳ] ��e���ѱ������ˡ�a"+args[2]+"��e���Ա");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("remove"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[��Աϵͳ] ��4��û��Ȩ��");
					return true;
				}
				
				if(args.length!=2)
				{
					sender.sendMessage("��6[��Աϵͳ] ��a/vip remove [�����] ��3ǿ�Ƴ���Ŀ����һ�Ա");
					return true;
				}
				
				if(Bukkit.getServer().getPlayer(args[1])==null && Bukkit.getServer().getOfflinePlayer(args[1])==null)
				{
					sender.sendMessage("��6[��Աϵͳ] ��c����Ҳ����ڻ�����");
					return true;
				}
				
				if(!vipData.containsKey(args[1]))
				{
					sender.sendMessage("��6[��Աϵͳ] ��c����Ҳ��ǻ�Ա���Ա�ѵ���");
					return true;
				}
				
				vipData.remove(args[1]);
				File file=new File(getDataFolder(),"/Data/"+args[1]+".yml");
				if(file.exists())
					file.delete();
				sender.sendMessage("��6[��Աϵͳ] ��e���Ƴ���ҡ�c"+args[1]+"��e�Ļ�Ա");
				if(Bukkit.getServer().getPlayer(args[1])!=null)
				{
					Bukkit.getServer().getPlayer(args[1]).sendMessage("��6[��Աϵͳ] ��e��Ļ�Ա�ѱ��Ƴ�");
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
						sender.sendMessage("��6[��Աϵͳ] ��c�㻹���ǻ�Ա");
						return true;
					}
					VipPlayer vip = vipData.get(p.getName());
					p.sendMessage("��6[��Աϵͳ] ��e��Ա����:��c"+vipGroups.get(vip.getVipGroup())+"��e,ʣ���Աʱ��:��a"+vip.getLeftDays()+"��,"+vip.getLeftHours()%24+"Сʱ");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("give"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[��Աϵͳ] ��4��û��Ȩ��");
					return true;
				}
				
				if(args.length!=4)
				{
					sender.sendMessage("��c/vip give [�����] [��Ա��] [��Աʱ��(��)] ��3����Ŀ������ض������Ļ�Ա");
					return true;
				}
				if(Bukkit.getServer().getPlayer(args[1])==null)
				{
					sender.sendMessage("��6[��Աϵͳ] ��c����Ҳ����ڻ�����");
					return true;
				}
				if(!vipGroups.containsKey(args[2]))
				{
					sender.sendMessage("��6[��Աϵͳ] ��c�����ڵĻ�Ա��");
					return true;
				}
				if(!args[3].matches("[0-9]*") || Integer.valueOf(args[3])<=0)
				{
					sender.sendMessage("��6[��Աϵͳ] ��c��Ч�Ļ�Աʱ��(��λΪ��3���c)");
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
					Bukkit.getServer().getPlayer(args[1]).sendMessage("��6[��Աϵͳ] ��e�ɹ����ѡ�c"+vipGroups.get(args[2])+" ��e"+args[3]+"��");
				}
				else
				{
					String recentDate = date.format(new Date());
					String deadlineDate = getNewDate(recentDate, days);
					VipPlayer vipPlayer = new VipPlayer(args[1], recentDate, deadlineDate, args[2]);
					vipData.put(args[1], vipPlayer);
					Bukkit.getServer().getPlayer(args[1]).sendMessage("��6[��Աϵͳ] ��e�ɹ���ͨ��c"+vipGroups.get(args[2])+" ��e"+args[3]+"��");
					Bukkit.getServer().broadcastMessage("��6��ϲ��ҡ�a"+args[1]+"��6��Ϊ��c"+vipGroups.get(args[2]));
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
					whoWantToBeVip.sendMessage("��6[��Աϵͳ] ��a��Ʒ�����ѷ��뱳���ڣ�����������ˣ������䵽�����ϣ���ע��ʰȡ��");
				}

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuadd "+args[1]+" "+args[2]);
				
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(!p.getName().equalsIgnoreCase(args[1]))
						sender.sendMessage("��6[��Աϵͳ] ��e�Ѹ�����ҡ�c"+args[1]+" ��a"+vipGroups.get(args[2])+" ��c"+args[3]+"��e��");
				}
				else
					sender.sendMessage("��6[��Աϵͳ] ��e�Ѹ�����ҡ�c"+args[1]+" ��a"+vipGroups.get(args[2])+" ��c"+args[3]+"��e��");
				return true;
			}
			return true;
		}
		return false;
		
	}
	
}


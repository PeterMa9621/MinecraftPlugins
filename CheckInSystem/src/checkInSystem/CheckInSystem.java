package checkInSystem;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;

public class CheckInSystem extends JavaPlugin
{
	ArrayList<Integer> moneyList = new ArrayList<Integer>();
	private ArrayList<String> commandList = new ArrayList<String>();
	ArrayList<ItemStack> itemList = new ArrayList<ItemStack>();
	private ArrayList<String> describeList = new ArrayList<String>();

	HashMap<String, HashMap<String, String>> playerData = new HashMap<String, HashMap<String, String>>();

	public boolean isEco = false;
	public Economy economy;
	HashMap<String, Boolean> isCheckIn = new HashMap<String, Boolean>();
	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");//�������ڸ�ʽ

	private CheckInSystemAPI api = new CheckInSystemAPI(this);
	
	public CheckInSystemAPI getAPI()
	{
		return api;
	}
	
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
		if(Bukkit.getPluginManager().getPlugin("Vault")!=null)
		{
			isEco=setupEconomy();
		}
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		loadConfig();
		loadPlayerConfig();
		task();
		getServer().getPluginManager().registerEvents(new CheckInSystemListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[CheckInSystem] ��eǩ��ϵͳ�������");
	}

	public void onDisable() 
	{
		savePlayerConfig();
		Bukkit.getConsoleSender().sendMessage("��a[CheckInSystem] ��eǩ��ϵͳж�����");
	}
	
	public void task()
	{
		new BukkitRunnable()
		{
    		String previousDate = null;
			public void run()
			{
				if(previousDate!=null && (!previousDate.equalsIgnoreCase(date.format(new Date()))))
				{
					for(Player p:Bukkit.getOnlinePlayers())
					{
						if(playerData.containsKey(p.getName()))
						{
							HashMap<String, String> data = playerData.get(p.getName());
							data.put("todayDate", date.format(new Date()));
							playerData.put(p.getName(), data);
							isCheckIn.put(p.getName(), false);
						}
						
					}
				}
				previousDate = date.format(new Date());
			}
		}.runTaskTimer(this, 0L, 20L);
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
	
	public void savePlayerConfig()
	{
		for(String playerName:playerData.keySet())
		{
			File file=new File(getDataFolder(), "/data/"+playerName+".yml");
			FileConfiguration config;
			
			config = load(file);
			
			config.set("CheckIn.Days", playerData.get(playerName).get("days"));
			config.set("CheckIn.LastDate", playerData.get(playerName).get("lastDate"));
			config.set("CheckIn.TodayDate", playerData.get(playerName).get("todayDate"));
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadPlayerConfig()
	{
		File file1=new File(getDataFolder(), "/data");
		String[] fileName = file1.list();
		for(String name:fileName)
		{
			//Bukkit.getConsoleSender().sendMessage("��a"+name+"���سɹ�");
			String playerName = name.substring(0, name.length()-4);
			
			File file=new File(getDataFolder(), "/data/"+playerName+".yml");
			FileConfiguration config;
			
			if(!file.exists())
			{
				//Bukkit.getConsoleSender().sendMessage("��a"+playerName+"������");
				config = load(file);
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("days", "0");
				data.put("lastDate", "0000-00-00");
				data.put("todayDate", date.format(new Date()));
				playerData.put(playerName, data);
				isCheckIn.put(playerName, false);
				return;
			}
			
			config = load(file);
			String lastDate = config.getString("CheckIn.LastDate");
			String todayDate = date.format(new Date());
			
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("days", config.getString("CheckIn.Days"));
			data.put("lastDate", lastDate);
			data.put("todayDate", todayDate);
			
			if(lastDate.equalsIgnoreCase(todayDate))
			{
				isCheckIn.put(playerName, true);
			}
			else
			{
				isCheckIn.put(playerName, false);
			}
			
			if(!todayDate.split("-")[1].equals(lastDate.split("-")[1]) || 
					!todayDate.split("-")[0].equals(lastDate.split("-")[0]))
			{
				data.put("days", "0");
			}
			playerData.put(playerName, data);
			//Bukkit.getConsoleSender().sendMessage("��a"+playerName+"���سɹ�");
		}

	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			for(int i=0; i<31; i++)
			{
				config.set("CheckIn.Days."+(i+1)+".Describe", "��7��ƷΪ�̱�ʯһ��");
				config.set("CheckIn.Days."+(i+1)+".Gift.TypeID", 263);
				config.set("CheckIn.Days."+(i+1)+".Gift.Durability", 0);
				config.set("CheckIn.Days."+(i+1)+".Gift.Amount", 1);
				config.set("CheckIn.Days."+(i+1)+".Gift.Name", "��fδ�����ı�ʯ");
				config.set("CheckIn.Days."+(i+1)+".Gift.Lore", "��e[δ����]%��6һ�鿴������ͨ��ʯͷ");
				config.set("CheckIn.Days."+(i+1)+".Gift.Enchantment.ID", 0);
				config.set("CheckIn.Days."+(i+1)+".Gift.Enchantment.Level", 1);
				config.set("CheckIn.Days."+(i+1)+".Gift.HideEnchant", true);
				config.set("CheckIn.Days."+(i+1)+".Money", 0);
				config.set("CheckIn.Days."+(i+1)+".Command", null);
			}
			try 
			{
				config.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			loadConfig();
			return;
		}
		config = load(file);
		int itemID = 0;
		int itemAmount = 0;
		int money = 0;
		int itemDurability = 0;
		int itemEnchantID = -1;
		int itemEnchantLevel = 1;
		String command = null;
		String describe = null;
		String itemName = null;
		String lore = null;
		describeList.clear();
		itemList.clear();
		moneyList.clear();
		commandList.clear();
		
		for(int i=0; i<31; i++)
		{
			describe = config.getString("CheckIn.Days."+(i+1)+".Describe").replaceAll("&", "��");
			itemID = config.getInt("CheckIn.Days."+(i+1)+".Gift.TypeID");
			itemDurability = config.getInt("CheckIn.Days."+(i+1)+".Gift.Durability");
			itemAmount = config.getInt("CheckIn.Days."+(i+1)+".Gift.Amount");
			itemName = config.getString("CheckIn.Days."+(i+1)+".Gift.Name");
			lore = config.getString("CheckIn.Days."+(i+1)+".Gift.Lore");
			itemEnchantID = config.getInt("CheckIn.Days."+(i+1)+".Gift.Enchantment.ID");
			itemEnchantLevel = config.getInt("CheckIn.Days."+(i+1)+".Gift.Enchantment.Level");
			boolean hide = config.getBoolean("CheckIn.Days."+(i+1)+".Gift.HideEnchant");
			
			money = config.getInt("CheckIn.Days."+(i+1)+".Money");
			command = config.getString("CheckIn.Days."+(i+1)+".Command");
			ItemStack item = null;
			if(itemName==null && lore==null)
				item = new ItemStack(itemID, itemAmount, (short) itemDurability);
			else if(itemName==null && lore!=null)
				item = createItem2(itemID, itemAmount, itemDurability, lore);
			else if(itemName!=null && lore==null)
				item = createItem(itemID, itemAmount, itemDurability, itemName);
			else if(itemName!=null && lore!=null)
				item = createItem(itemID, itemAmount, itemDurability, itemName, lore);
			ItemMeta meta = item.getItemMeta();
			if(hide==true)
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			if(itemEnchantID>=0 && itemEnchantLevel>0)
			{
				item.addUnsafeEnchantment(Enchantment.getById(itemEnchantID), itemEnchantLevel);
			}
			describeList.add(describe);
			itemList.add(item);
			moneyList.add(money);
			commandList.add(command);
		}
		return;
		
	}
	
	public ItemStack createItem(int ID, int quantity, int durability, String displayName, String lore)
	{
		ItemStack item = new ItemStack(ID, quantity, (short)durability);
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
	
	public ItemStack createItem(int ID, int quantity, int durability, String displayName)
	{
		ItemStack item = new ItemStack(ID, quantity, (short)durability);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack createItem2(int ID, int quantity, int durability, String lore)
	{
		ItemStack item = new ItemStack(ID, quantity, (short)durability);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> loreList = new ArrayList<String>();
		for(String l:lore.split("%"))
		{
			loreList.add(l);
		}
		meta.setLore(loreList);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public Inventory init_gui(Player player)
	{
		int days = Integer.valueOf(playerData.get(player.getName()).get("days"));

		Inventory inv = Bukkit.createInventory(player, 36, "ǩ��");
		for(int i=0; i<31; i++)
		{
			ItemStack item = new ItemStack(159, 1, (short)11);
			ItemMeta meta = item.getItemMeta();
			ArrayList<String> lore = new ArrayList<String>();
			meta.setDisplayName("��a��"+(i+1)+"��");
			lore.add(describeList.get(i));
			meta.setLore(lore);
			item.setItemMeta(meta);
			item.setAmount(i+1);
			inv.setItem(i, item);
		}
		
		for(int i=0; i<days; i++)
		{
			ItemStack item = new ItemStack(159, 1, (short)0);
			ItemMeta meta = item.getItemMeta();
			ArrayList<String> lore = new ArrayList<String>();
			meta.setDisplayName("��d��ǩ��");
			lore.add(describeList.get(i));
			meta.setLore(lore);
			item.setItemMeta(meta);
			item.setAmount(i+1);
			inv.setItem(i, item);
		}
		
		if(this.isCheckIn.get(player.getName())==false)
		{
			ItemStack item = inv.getItem(days);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("��e���ǩ��");
			item.setItemMeta(meta);
			item.setTypeId(159);
			item.setDurability((short)1);
			item.setAmount(days+1);
			//item.addEnchantment(Enchantment.LUCK, 5);
			inv.setItem(days, item);
		}
		return inv;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("qd"))
		{
			if (args.length==0)
			{
				sender.sendMessage("��6=========[ǩ��ϵͳ]=========");
				sender.sendMessage("��6/qd gui  ��a��ǩ������");
				if(sender.isOp())
				{
					sender.sendMessage("��6/qd reload  ��a���ز��");
					sender.sendMessage("��6/qd reloadp  ��a����������á�4(����������!)");
					
				}
				
				return true;
			}
			
			if (args[0].equalsIgnoreCase("gui"))
			{
				if (sender instanceof Player)
				{
					Player player = (Player)sender;

					Inventory inv = init_gui(player);
					
					player.openInventory(inv);
				}
				else
				{
					sender.sendMessage("��6[ǩ��ϵͳ] ��a����̨������");
				}
			}
			if (args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					loadConfig();
					sender.sendMessage("��6[ǩ��ϵͳ] ��a�����������");
				}
				else
				{
					sender.sendMessage("��6[ǩ��ϵͳ] ��a��û��Ȩ�ޣ�");
				}
				
			}
			
			if (args[0].equalsIgnoreCase("reloadp"))
			{
				if(sender.isOp())
				{
					loadPlayerConfig();
					sender.sendMessage("��6[ǩ��ϵͳ] ��a�������������!");
				}
				else
				{
					sender.sendMessage("��6[ǩ��ϵͳ] ��a��û��Ȩ�ޣ�");
				}
				
			}
			return true;
		}

		return false;
		
	}
	
	
}


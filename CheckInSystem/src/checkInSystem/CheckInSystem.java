package checkInSystem;

import checkInSystem.database.*;
import checkInSystem.expansion.CheckInSystemExpansion;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import java.util.UUID;

import org.bukkit.Bukkit;
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

public class CheckInSystem extends JavaPlugin
{
	ArrayList<Integer> moneyList = new ArrayList<Integer>();
	private ArrayList<String> commandList = new ArrayList<String>();
	ArrayList<ItemStack> itemList = new ArrayList<ItemStack>();
	private ArrayList<String> describeList = new ArrayList<String>();

	public HashMap<UUID, HashMap<String, String>> playerData = new HashMap<>();

	public boolean isEco = false;
	public Economy economy;
	public HashMap<UUID, Boolean> isCheckIn = new HashMap<>();
	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");//�������ڸ�ʽ

	public DatabaseType databaseType = DatabaseType.YML;

	private CheckInSystemAPI api = new CheckInSystemAPI(this);
	private String createTableQuery = "create table if not exists check_in_system(id varchar(100), days varchar(10), last_date varchar(10), today_date varchar(10), primary key(id));";

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

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new CheckInSystemExpansion(this).register();
		}

		Database.setConnectionInfo("minecraft", "root", "mjy159357", createTableQuery);
		loadConfig();

		task();
		getServer().getPluginManager().registerEvents(new CheckInSystemListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[CheckInSystem] ��eǩ��ϵͳ�������");
	}

	public void onDisable() 
	{
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
						if(playerData.containsKey(p.getUniqueId()))
						{
							HashMap<String, String> data = playerData.get(p.getUniqueId());
							data.put("today_date", date.format(new Date()));
							playerData.put(p.getUniqueId(), data);
							isCheckIn.put(p.getUniqueId(), false);
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
	
	public void savePlayerConfig(UUID uniqueId, Boolean isCheckIn, HashMap<String, String> data) throws IOException {
		this.isCheckIn.put(uniqueId, isCheckIn);
		this.playerData.put(uniqueId, data);

		HashMap<String, Object> paths = new HashMap<String, Object>() {{
			put("days", data.get("days"));
			put("last_date", data.get("last_date"));
			put("today_date", data.get("today_date"));
		}};

		ConfigStructure configStructure = new ConfigStructure(paths);

		StorageInterface storage = Database.getInstance(databaseType, this);
		storage.store(uniqueId, configStructure);
	}
	
	public void loadPlayerConfig(UUID uniqueId)
	{
		String todayDate = date.format(new Date());
		if(playerData.containsKey(uniqueId)){
			HashMap<String, String> data = playerData.get(uniqueId);
			String lastDate = data.get("last_date");
			data.put("today_date", todayDate);
			playerData.put(uniqueId, data);
			isCheckIn.put(uniqueId, lastDate.equalsIgnoreCase(todayDate));
			return;
		}

		StorageInterface storage = Database.getInstance(databaseType, this);
		HashMap<String, Object> result = storage.get(uniqueId, new String[] {"days", "last_date", "today_date"});

		if(result==null){
			HashMap<String, String> data = new HashMap<>();
			data.put("days", "0");
			data.put("last_date", "0000-00-00");
			data.put("today_date", todayDate);
			playerData.put(uniqueId, data);
			isCheckIn.put(uniqueId, false);
			return;
		}

		HashMap<String, String> data = new HashMap<>();
		for(String key:result.keySet()){
			data.put(key, (String)result.get(key));
		}

		String lastDate = data.get("last_date");
		data.put("today_date", todayDate);

		isCheckIn.put(uniqueId, lastDate.equalsIgnoreCase(todayDate));

		if(!todayDate.split("-")[1].equals(lastDate.split("-")[1]) ||
				!todayDate.split("-")[0].equals(lastDate.split("-")[0])) {
			data.put("days", "0");
		}
		playerData.put(uniqueId, data);
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			HashMap<String, Object> data = new HashMap<>();
			data.put("CheckIn.Database", "YML");
			for(int i=0; i<31; i++)
			{
				data.put("CheckIn.Days."+(i+1)+".Describe", "��7��ƷΪ�̱�ʯһ��");
				data.put("CheckIn.Days."+(i+1)+".Gift.TypeID", "diamond");
				data.put("CheckIn.Days."+(i+1)+".Gift.Amount", 1);
				data.put("CheckIn.Days."+(i+1)+".Gift.Name", "��fδ�����ı�ʯ");
				data.put("CheckIn.Days."+(i+1)+".Gift.Lore", "��e[δ����]%��6һ�鿴������ͨ��ʯͷ");
				data.put("CheckIn.Days."+(i+1)+".Gift.Enchantment.ID", "fortune");
				data.put("CheckIn.Days."+(i+1)+".Gift.Enchantment.Level", 1);
				data.put("CheckIn.Days."+(i+1)+".Gift.HideEnchant", true);
				data.put("CheckIn.Days."+(i+1)+".Money", 0);
				data.put("CheckIn.Days."+(i+1)+".Command", null);
			}

			StorageInterface storage = Database.getInstance(DatabaseType.YML, this);
			try {
				storage.store(new ConfigStructure(data));
			} catch (IOException e) {
				e.printStackTrace();
			}

			loadConfig();
			return;
		}
		config = load(file);
		String itemID;
		int itemAmount = 0;
		int money = 0;
		int itemDurability = 0;
		String itemEnchantID;
		int itemEnchantLevel = 1;
		String command = null;
		String describe = null;
		String itemName = null;
		String lore = null;
		describeList.clear();
		itemList.clear();
		moneyList.clear();
		commandList.clear();

		databaseType = DatabaseType.valueOf(config.getString("CheckIn.Database").toUpperCase());

		for(int i=0; i<31; i++)
		{
			describe = config.getString("CheckIn.Days."+(i+1)+".Describe").replaceAll("&", "��");
			itemID = config.getString("CheckIn.Days."+(i+1)+".Gift.TypeID").toUpperCase();
			itemAmount = config.getInt("CheckIn.Days."+(i+1)+".Gift.Amount");
			itemName = config.getString("CheckIn.Days."+(i+1)+".Gift.Name");
			lore = config.getString("CheckIn.Days."+(i+1)+".Gift.Lore");
			itemEnchantID = config.getString("CheckIn.Days."+(i+1)+".Gift.Enchantment.ID");
			itemEnchantLevel = config.getInt("CheckIn.Days."+(i+1)+".Gift.Enchantment.Level");
			boolean hide = config.getBoolean("CheckIn.Days."+(i+1)+".Gift.HideEnchant");
			
			money = config.getInt("CheckIn.Days."+(i+1)+".Money");
			command = config.getString("CheckIn.Days."+(i+1)+".Command");
			ItemStack item = null;
			if(itemName==null && lore==null)
				item = new ItemStack(Material.getMaterial(itemID), itemAmount);
			else if(itemName==null && lore!=null)
				item = createItem2(itemID, itemAmount, lore);
			else if(itemName!=null && lore==null)
				item = createItem(itemID, itemAmount, itemName);
			else if(itemName!=null && lore!=null)
				item = createItem(itemID, itemAmount, itemName, lore);
			ItemMeta meta = item.getItemMeta();
			if(hide==true)
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			if(itemEnchantID!=null && itemEnchantLevel>0)
			{
				item.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(itemEnchantID)), itemEnchantLevel);
			}
			describeList.add(describe);
			itemList.add(item);
			moneyList.add(money);
			commandList.add(command);
		}
		return;
		
	}
	
	public ItemStack createItem(String ID, int quantity, String displayName, String lore)
	{
		ItemStack item = new ItemStack(Material.getMaterial(ID.toUpperCase()), quantity);
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
	
	public ItemStack createItem(String ID, int quantity, String displayName)
	{
		ItemStack item = new ItemStack(Material.getMaterial(ID), quantity);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack createItem2(String ID, int quantity, String lore)
	{
		ItemStack item = new ItemStack(Material.getMaterial(ID), quantity);
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
		int days = Integer.parseInt(playerData.get(player.getUniqueId()).get("days"));

		Inventory inv = Bukkit.createInventory(player, 36, "ǩ��");
		for(int i=0; i<31; i++)
		{
			ItemStack item = new ItemStack(Material.BLUE_TERRACOTTA, 1);
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
			ItemStack item = new ItemStack(Material.WHITE_TERRACOTTA, 1);
			ItemMeta meta = item.getItemMeta();
			ArrayList<String> lore = new ArrayList<String>();
			meta.setDisplayName("��d��ǩ��");
			lore.add(describeList.get(i));
			meta.setLore(lore);
			item.setItemMeta(meta);
			item.setAmount(i+1);
			inv.setItem(i, item);
		}
		
		if(!this.isCheckIn.get(player.getUniqueId()))
		{
			ItemStack item = inv.getItem(days);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("��e���ǩ��");
			item.setItemMeta(meta);
			item.setType(Material.ORANGE_TERRACOTTA);
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
					for(Player p:Bukkit.getServer().getOnlinePlayers()){
						loadPlayerConfig(p.getUniqueId());
					}
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


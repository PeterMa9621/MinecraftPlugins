package voucher;

import org.bukkit.plugin.java.JavaPlugin;

import net.citizensnpcs.api.CitizensAPI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Voucher extends JavaPlugin
{
	ItemStack voucher = null;
	HashMap<String, Location> location = new HashMap<String, Location>();
	
	HashMap<String, String> addLocation = new HashMap<String, String>();
	
	HashMap<String, HashMap<String, ItemStack>> items = new HashMap<String, HashMap<String, ItemStack>>();
	
	HashMap<String, ItemStack> totalItem = new HashMap<String, ItemStack>();
	
	ArrayList<String> waitForTp = new ArrayList<String>();
	
	int npcId = 0;
	
	public void onEnable()
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		loadConfig();
		loadLocationConfig();
		getServer().getPluginManager().registerEvents(new VoucherListener(this), this);
		CitizensAPI.registerEvents(new NPCListener(this));
		Bukkit.getConsoleSender().sendMessage("§a[Voucher] §e兑换券加载完毕!");
	}

	public void onDisable() 
	{
		saveConfig();
		saveLocationConfig();
		Bukkit.getConsoleSender().sendMessage("§a[Voucher] §e兑换券卸载完毕!");
	}
	
	public void loadLocationConfig()
	{
		File file=new File(getDataFolder(),"location.yml");
		FileConfiguration config;
		
		if(file.exists())
		{
			config = load(file);
			
			for(String name:config.getKeys(false))
			{
				String world = config.getString(name+".World");
				int x = config.getInt(name+".X");
				int y = config.getInt(name+".Y");
				int z = config.getInt(name+".Z");
				
				World w = Bukkit.getWorld(world);
				Location l = new Location(w, x, y, z);
				location.put(name, l);
				Block block = Bukkit.getServer().getWorld(world).getBlockAt(l);
				HashMap<String, ItemStack> item = getItem(block);
				items.put(name, item);
				
				for(ItemStack i:item.values())
				{
					totalItem.put(i.getItemMeta().getDisplayName(), i);
				}
			}
		}
	}
	
	public void saveLocationConfig()
	{
		File file=new File(getDataFolder(),"location.yml");
		FileConfiguration config;
		
		if (file.exists())
			file.delete();
		if(location.isEmpty())
			return;
		config = load(file);
		
		for(String name:location.keySet())
		{
			config.set(name+".World", location.get(name).getWorld().getName());
			config.set(name+".X", location.get(name).getBlockX());
			config.set(name+".Y", location.get(name).getBlockY());
			config.set(name+".Z", location.get(name).getBlockZ());
		}
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		
		if (!file.exists())
		{
			config = load(file);
			config.set("Voucher.ID", 339);
			config.set("Voucher.Data", 0);		
			config.set("Voucher.DisplayName", "§5装备兑换券");
			config.set("Voucher.Lore", "§5将该物品交给兑换商人");
			config.set("Voucher.Enchantment.ID", 1);
			config.set("Voucher.Enchantment.Level", 1);
			config.set("NPC.ID", 0);
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			loadConfig();
			return;
		}
		
		config = load(file);
		
		npcId = config.getInt("NPC.ID");
		int id = config.getInt("Voucher.ID");
		int data = config.getInt("Voucher.Data");
		String name = config.getString("Voucher.DisplayName");
		String lore = config.getString("Voucher.Lore");
		int enchantID = config.getInt("Voucher.Enchantment.ID");
		int level = config.getInt("Voucher.Enchantment.Level");
		voucher = createItem(id, 1, data, name, lore);
		
		if(level>0)
		{
			voucher.addUnsafeEnchantment(Enchantment.getById(enchantID), level);
		}
	}
	
	public void saveConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		
		config = load(file);

		config.set("NPC.ID", npcId);
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

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
	
	public boolean isExist(int number, int[] numberList)
	{
		for(int i=0; i<numberList.length; i++)
		{
			if(number==numberList[i])
			{
				return true;
			}
		}
		return false;
	}
	
	public HashMap<String, ItemStack> getItem(Block chestBlock)
	{
		Chest chest = (Chest)chestBlock.getState();
		
		Inventory inventory = chest.getInventory();
		
		HashMap<String, ItemStack> itemMap = new HashMap<String, ItemStack>();
		
		for(ItemStack item:inventory.getContents())
		{
			if(item!=null && item.getItemMeta().hasDisplayName())
			{
				itemMap.put(item.getItemMeta().getDisplayName(), item);
			}
		}
		return itemMap;
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
	
	public Inventory createGUI(Player p, String locationName)
	{
		Inventory inv = Bukkit.createInventory(p, 54, "§5兑换券地点:§3"+locationName);

		for(ItemStack item:items.get(locationName).values())
		{
			inv.addItem(item);
		}

		return inv;
	}
	
	public Inventory getEquipGUI(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 45, "§5兑换装备");
		
		ItemStack window = createItem(160, 1, 0, " ");
		ItemStack window2 = createItem(160, 1, 2, "§e请在此处放入兑换券");
		ItemStack window3 = createItem(160, 1, 3, "§e此处为预览兑换物品区域");
		ItemStack window4 = createItem(160, 1, 4, "§e兑换成功的物品将出现在这里");
		ItemStack confirm = createItem(264, 1, 0, "§a点击兑换", "§3左键点击预览物品属性%§5右键点击兑换物品");
		
		for(int i=0; i<45; i++)
			inv.setItem(i, window);
		
		inv.setItem(0, window2);
		inv.setItem(1, window2);
		inv.setItem(2, window2);
		inv.setItem(9, window2);
		inv.setItem(11, window2);
		inv.setItem(18, window2);
		inv.setItem(19, window2);
		inv.setItem(20, window2);
		
		inv.setItem(6, window3);
		inv.setItem(7, window3);
		inv.setItem(8, window3);
		inv.setItem(15, window3);
		inv.setItem(17, window3);
		inv.setItem(24, window3);
		inv.setItem(25, window3);
		inv.setItem(26, window3);
		
		inv.setItem(39, window4);
		inv.setItem(41, window4);
		
		inv.setItem(10, null);
		inv.setItem(16, null);
		inv.setItem(31, confirm);
		inv.setItem(40, null);
		return inv;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("voucher"))
		{
			if (args.length==0)
			{
				if(sender instanceof Player)
				{
					if(sender.isOp())
					{
						sender.sendMessage("§a=========[兑换券系统]=========");
						sender.sendMessage("§a/voucher gui §3打开兑换界面");
						sender.sendMessage("§a/voucher set [NPCID] §3设置NPCID");						
						sender.sendMessage("§a/voucher add [地点名] §3新增一个地点");
						sender.sendMessage("§a/voucher open [地点名] §3打开一个箱子");
						sender.sendMessage("§a/voucher remove [地点名] §3删除一个地点");
						sender.sendMessage("§a/voucher refresh §3刷新所有箱子内容");
						sender.sendMessage("§a/voucher list §3列出所有地点");
						sender.sendMessage("§a/voucher reload §3重载配置");
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("set"))
			{
				if(sender instanceof Player && sender.isOp())
				{
					if(args.length==2)
					{
						Player p = (Player)sender;
						if(!args[1].matches("[0-9]*"))
						{
							p.sendMessage("§6[兑换券] §cNPCID必须为数字");
							return true;
						}
						if(CitizensAPI.getNPCRegistry().getById(Integer.valueOf(args[1]))==null)
						{
							p.sendMessage("§6[兑换券] §c无效的NPCID");
							return true;
						}
						npcId = Integer.valueOf(args[1]);
						p.sendMessage("§6[兑换券] §a设置成功，NPC名称为§5"+CitizensAPI.getNPCRegistry().getById(npcId).getName());
					}
					else
					{
						sender.sendMessage("§a/voucher set [NPCID] §3设置NPCID");
					}
					
				}
			}
			
			if(args[0].equalsIgnoreCase("refresh"))
			{
				if(sender instanceof Player && sender.isOp())
				{
					loadLocationConfig();
					sender.sendMessage("§6[兑换券] §a刷新成功！");
				}
			}
			
			if(args[0].equalsIgnoreCase("gui"))
			{
				if(sender instanceof Player && sender.isOp())
				{
					Player p = (Player)sender;
					p.openInventory(getEquipGUI(p));
				}
			}
			
			if(args[0].equalsIgnoreCase("list"))
			{
				if(sender instanceof Player && sender.isOp())
				{
					Player p = (Player)sender;
					p.sendMessage("=========兑换券地点=========");
					if(location.isEmpty())
						p.sendMessage("§e目前没有兑换券地点");
					for(String name:location.keySet())
					{
						p.sendMessage("§e"+name);
					}
					p.sendMessage("=========================");
				}
			}
			
			if(args[0].equalsIgnoreCase("add"))
			{
				if(sender instanceof Player && sender.isOp())
				{
					if(args.length==2)
					{
						Player p = (Player)sender;
						addLocation.put(p.getName(), args[1]);
						sender.sendMessage("§6[兑换券] §a请左键或右键点击一个箱子");
					}
					else
					{
						sender.sendMessage("§c用法:§a/voucher add [地点名] §3新增一个地点");
					}
				}
			}
			
			if(args[0].equalsIgnoreCase("open"))
			{
				if(sender instanceof Player && sender.isOp())
				{
					if(args.length==2)
					{
						if(!items.containsKey(args[1]))
						{
							sender.sendMessage("§6[兑换券] §c不存在的地点名称!");
							return true;
						}
						Player p = (Player)sender;
						p.openInventory(createGUI(p, args[1]));
					}
					else
					{
						sender.sendMessage("§c用法:§a/voucher open [地点名] §3打开一个箱子");
					}
				}
			}
			
			if(args[0].equalsIgnoreCase("remove"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						if(!items.containsKey(args[1]))
						{
							sender.sendMessage("§6[兑换券] §c不存在的地点名称!");
							return true;
						}
						for(String itemName:items.get(args[1]).keySet())
						{
							if(totalItem.containsKey(itemName))
								totalItem.remove(itemName);
						}
						items.remove(args[1]);
						location.remove(args[1]);
						sender.sendMessage("§6[兑换券] §a删除成功！");
					}
					else
					{
						sender.sendMessage("§c用法:§a/voucher remove [地点名] §3删除一个地点");
					}
				}
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					loadConfig();
					sender.sendMessage("§6[兑换券] §a重载配置成功！");
				}
			}
			return true;
		}
		return false;
	}
}


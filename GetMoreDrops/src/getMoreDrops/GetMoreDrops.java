package getMoreDrops;

import org.bukkit.plugin.java.JavaPlugin;

import getMoreDrops.EventListen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GetMoreDrops extends JavaPlugin
{
	ArrayList<Integer> blockIDList = new ArrayList<Integer>();
	ArrayList<ItemStack> blockItemList = new ArrayList<ItemStack>();
	ArrayList<Integer> blockProbabilityList = new ArrayList<Integer>();
	ArrayList<Integer> mobIDList = new ArrayList<Integer>();
	ArrayList<ItemStack> mobItemList = new ArrayList<ItemStack>();
	ArrayList<Integer> mobProbabilityList = new ArrayList<Integer>();
	boolean useBlock = false;
	boolean useMob = false;
	
	public int quantity = 0;
	//ArrayList<Integer> moneyList = new ArrayList<Integer>();
	public void onEnable()
	{
		if(!getDataFolder().exists())
		{
			getDataFolder().mkdir();
		}
		loadConfig();
		getServer().getPluginManager().registerEvents(new EventListen(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[GetMoreDrops] §e掉落系统加载完毕");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[GetMoreDrops] §e掉落系统卸载完毕");
	}
	
	public void init()
	{
		if(!blockIDList.isEmpty())
		{
			blockIDList.clear();
		}
		if(!blockItemList.isEmpty())
		{
			blockItemList.clear();
		}
		if(!blockProbabilityList.isEmpty())
		{
			blockProbabilityList.clear();
		}
		if(!mobIDList.isEmpty())
			mobIDList.clear();
		if(!mobItemList.isEmpty())
			mobItemList.clear();
		if(!mobProbabilityList.isEmpty())
			mobProbabilityList.clear();
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			config.set("Blocks.OnUse", true);
			config.set("Blocks.1.BlockID", 56);
			config.set("Blocks.1.GetItem.Probability", 10);
			config.set("Blocks.1.GetItem.ID", 388);
			config.set("Blocks.1.GetItem.Name", "§2神圣蛋");
			config.set("Blocks.1.GetItem.Lore", "§3这是一个神圣的蛋，看似可以打开%§4右键打开它");
			config.set("Blocks.1.GetItem.Enchantment.ID", 0);
			config.set("Blocks.1.GetItem.Enchantment.Level", 1);
			config.set("Blocks.1.GetItem.HideEnchant", true);
			//config.set("Blocks.1.GetMoney", 100);
			
			config.set("Blocks.2.BlockID", 129);
			config.set("Blocks.2.GetItem.ID", 264);
			config.set("Blocks.2.GetItem.Amount", 2);
			
			config.set("Mobs.OnUse", true);
			config.set("Mobs.1.MobID", 54);
			config.set("Mobs.1.GetItem.Probability", 10);
			config.set("Mobs.1.GetItem.ID", 388);
			config.set("Mobs.1.GetItem.Name", "§2矿工药水");
			config.set("Mobs.1.GetItem.Lore", "§a这是一瓶神奇的药水%§a右键喝掉它可以增加100挖矿点数");
			config.set("Mobs.1.GetItem.Amount", 1);
			config.set("Mobs.1.GetItem.HideEnchant", true);
			config.set("Mobs.2.MobID", 50);
			config.set("Mobs.2.GetItem.ID", 388);
			config.set("Mobs.2.GetItem.Amount", 1);
			//config.set("Blocks.2.GetMoney", 500);
			try 
			{
				config.save(file);
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			/*
			if(!moneyList.isEmpty())
			{
				moneyList.clear();
			}
			*/
			loadConfig();
			return;
		}
		config = load(file);
		init();
		int blockID = 0;
		ItemStack item = null;
		int itemID = 0;
		int itemAmount = 0;
		String itemName = null;
		ArrayList<String> lore = new ArrayList<String>();
		//int money = 0;
		int probability = 0;
		useBlock = config.getBoolean("Blocks.OnUse");
		useMob = config.getBoolean("Mobs.OnUse");
		
		//The following things are about blocks
		for(int i=0; config.contains("Blocks."+(i+1)); i++)
		{
			blockID = config.getInt("Blocks."+(i+1)+".BlockID");
			
			//money = config.getInt("Blocks."+(i+1)+".GetMoney");
			lore.clear();
			
			boolean hide = config.getBoolean("Blocks."+(i+1)+".GetItem.HideEnchant");
			itemID = config.getInt("Blocks."+(i+1)+".GetItem.ID");
			itemName = config.getString("Blocks."+(i+1)+".GetItem.Name");
			itemAmount = config.getInt("Blocks."+(i+1)+".GetItem.Amount");
			probability = config.getInt("Blocks."+(i+1)+".GetItem.Probability");
			String loreContent = config.getString("Blocks."+(i+1)+".GetItem.Lore");
			int enchantID = config.getInt("Blocks."+(i+1)+".GetItem.Enchantment.ID");
			int enchantLevel = config.getInt("Blocks."+(i+1)+".GetItem.Enchantment.Level");
			
			if(loreContent!=null)
			{
				for(String l:loreContent.split("%"))
				{
					lore.add(l);
				}
			}

			if(itemAmount<=0)
			{
				item = new ItemStack(itemID);
			}
			else
			{
				item = new ItemStack(itemID, itemAmount);
			}

			ItemMeta meta = item.getItemMeta();
			if(itemName!=null)
			{
				meta.setDisplayName(itemName);
			}
			if(!lore.isEmpty())
			{
				meta.setLore(lore);
			}
			if(hide==true)
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			
			if(enchantID>=0 && enchantLevel>0)
			{
				item.addUnsafeEnchantment(Enchantment.getById(enchantID), enchantLevel);
			}
			
			if(probability<=0)
			{
				blockProbabilityList.add(10);
			}
			else
			{
				blockProbabilityList.add(probability);
			}
			blockItemList.add(item);
			blockIDList.add(blockID);
			//moneyList.add(money);
		}
		
		//The following things are about mobs
		for(int i=0; config.contains("Mobs."+(i+1)); i++)
		{
			blockID = config.getInt("Mobs."+(i+1)+".MobID");
			
			//money = config.getInt("Blocks."+(i+1)+".GetMoney");
			lore.clear();
			
			boolean hide = config.getBoolean("Mobs."+(i+1)+".GetItem.HideEnchant");
			itemID = config.getInt("Mobs."+(i+1)+".GetItem.ID");
			itemName = config.getString("Mobs."+(i+1)+".GetItem.Name");
			itemAmount = config.getInt("Mobs."+(i+1)+".GetItem.Amount");
			probability = config.getInt("Mobs."+(i+1)+".GetItem.Probability");
			String loreContent = config.getString("Mobs."+(i+1)+".GetItem.Lore");
			int enchantID = config.getInt("Mobs."+(i+1)+".GetItem.Enchantment.ID");
			int enchantLevel = config.getInt("Mobs."+(i+1)+".GetItem.Enchantment.Level");
			if(loreContent!=null)
			{
				for(String l:loreContent.split("%"))
				{
					lore.add(l);
				}
			}

			if(itemAmount<=0)
			{
				item = new ItemStack(itemID);
			}
			else
			{
				item = new ItemStack(itemID, itemAmount);
			}

			ItemMeta meta = item.getItemMeta();
			if(itemName!=null)
			{
				meta.setDisplayName(itemName);
			}
			if(!lore.isEmpty())
			{
				meta.setLore(lore);
			}
			if(hide==true)
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			
			if(enchantID>=0 && enchantLevel>0)
			{
				item.addUnsafeEnchantment(Enchantment.getById(enchantID), enchantLevel);
			}
			
			if(probability<=0)
			{
				mobProbabilityList.add(10);
			}
			else
			{
				mobProbabilityList.add(probability);
			}
			mobItemList.add(item);
			mobIDList.add(blockID);
			//moneyList.add(money);
		}
		return;
		
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
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("gm")) 
		{
			if (args.length==0)
			{
				if(sender.isOp())
				{
					sender.sendMessage("§a=======§6[掉落系统]§a=======");
					sender.sendMessage("§a/gm reload §6重载配置");
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("reload"))
			{
				if (sender.isOp())
				{
					loadConfig();
					sender.sendMessage("§6[掉落系统] §a重载插件成功");
				}
				else
				{
					sender.sendMessage("§6[掉落系统] §c你没有权限！");
				}
			}
			
			return true;
		}

		return false;
		
	}
	
	
}


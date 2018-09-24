package dungeon;

import org.bukkit.plugin.java.JavaPlugin;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Dungeon extends JavaPlugin
{
	ArrayList<ItemStack> rewards = new ArrayList<ItemStack>();
	
	java.util.Random random=new java.util.Random();
	
	ItemStack getReward = null;
	
	String rewardGuiName = null;
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		loadRewardItem();
		getServer().getPluginManager().registerEvents(new DungeonListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[Dungeon] §e地牢已加载");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[Dungeon] §e地牢已卸载");
	}
	
	public void loadRewardItem()
	{
		File file=new File(getDataFolder(), "reward.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			config.set("Reward.GUIName", "§a获取挑战奖励");
			config.set("Reward.Button.ID", 159);
			config.set("Reward.Button.Data", 0);
			config.set("Reward.Button.Name", "§a点击获取奖励");
			config.set("Reward.1.Money", 100);
			config.set("Reward.2.Item.ID", 264);
			config.set("Reward.2.Item.Data", 0);
			config.set("Reward.2.Item.Amount", 2);
			config.set("Reward.2.Item.Name", "测试");
			config.set("Reward.2.Item.Lore", "测试%继续测试");
			config.set("Reward.2.Item.Enchantment.ID", 61);
			config.set("Reward.2.Item.Enchantment.Level", 1);
			config.set("Reward.2.Item.HideEnchant", true);
			
			try 
			{
				config.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			loadRewardItem();
			return;
		}
		
		config = load(file);
		
		rewardGuiName = config.getString("Reward.GUIName");
		int buttonID = config.getInt("Reward.Button.ID");
		int buttonData = config.getInt("Reward.Button.Data");
		String buttonName = config.getString("Reward.ButtonName");
		
		getReward = createItem(buttonID, 1, buttonData, buttonName);
		
		for(int i=0; config.contains("Reward."+(i+1)); i++)
		{
			int money = config.getInt("Reward."+(i+1)+".Money");
			int id = config.getInt("Reward."+(i+1)+".Item.ID");
			int amount = config.getInt("Reward."+(i+1)+".Item.Amount");
			int data = config.getInt("Reward."+(i+1)+".Item.Data");
			String name = config.getString("Reward."+(i+1)+".Item.Name");
			String lore = config.getString("Reward."+(i+1)+".Item.Lore");
			int enchantID = config.getInt("Reward."+(i+1)+".Item.Enchantment.ID");
			int enchantLevel = config.getInt("Reward."+(i+1)+".Item.Enchantment.Level");
			boolean hide = config.getBoolean("Reward."+(i+1)+".Item.HideEnchant");
			ItemStack item = null;
			if(money!=0 && id==0)
			{
				item = createItem(175, 1, 0, "金币奖励:"+money);
			}
			else if(money==0 && id!=0)
			{
				item = createItem(id, amount, (short) data, name, lore);
			}
			if(enchantID>=0 && enchantLevel>0)
				item.addUnsafeEnchantment(Enchantment.getById(enchantID), enchantLevel);
			if(hide==true)
				item.getItemMeta().addItemFlags(ItemFlag.HIDE_ENCHANTS);
			if(item!=null)
				rewards.add(item);
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
	
	public Inventory rewardGUI(Player p, int number)
	{
		int time = 0;
		if(number%9!=0)
		{
			time = (number/9)+1;
		}
		else
		{
			time = number/9;
		}
		Inventory inv = Bukkit.createInventory(p, time*9, rewardGuiName);
		
		for(int i=0; i<number; i++)
		{
			inv.setItem(i, getReward);
		}
		
		return inv;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("dungeon"))
		{
			if (args.length==0)
			{
				sender.sendMessage("§a=========[地牢系统]=========");
				sender.sendMessage("§a/dungeon stop §3查看当前PVP开关状态");
				sender.sendMessage("§a/dungeon help §3查看帮助");
				if(sender.isOp())
				{
					sender.sendMessage("§a/dungeon on §3强制打开PVP");
					sender.sendMessage("§a/dungeon off §3强制关闭PVP");
				}
				return true;
			}
			
			if(args[1].equalsIgnoreCase("stop"))
			{
				
			}
		}

		return false;
		
	}
	
	public int random(int max)
	{
		int result=random.nextInt(max);
		return result;
	}
	
	public ItemStack getReward()
	{
		int index = random(rewards.size());
		return rewards.get(index);
	}
	
}


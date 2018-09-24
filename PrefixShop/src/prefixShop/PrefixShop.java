package prefixShop;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.black_ixx.playerpoints.PlayerPoints;

public class PrefixShop extends JavaPlugin
{
	ArrayList<String> prefixList = new ArrayList<String>();
	ArrayList<String> costTypeList = new ArrayList<String>();
	ArrayList<Integer> priceList = new ArrayList<Integer>();
	ArrayList<String> definePlayer = new ArrayList<String>();
	HashMap<String, ArrayList<String>> playerData = new HashMap<String, ArrayList<String>>();
	HashMap<String, String> playerOnUse = new HashMap<String, String>();
	
	//Inventory inv = null;
	boolean economySupport = false;
	
	public Economy economy;
	PlayerPoints playerPoints;
	
	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null)
		{
			economy = economyProvider.getProvider();
		}
		return (economy!=null);
	}

	private boolean hookPlayerPoints() {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("PlayerPoints");
	    playerPoints = PlayerPoints.class.cast(plugin);
	    return playerPoints != null; 
	}
	
	public void onEnable() 
	{
		if(!new File(getDataFolder(),"Data").exists()) 
		{
			new File(getDataFolder(),"Data").mkdirs();
		}
		if(setupEconomy()!=false)
		{
			if(hookPlayerPoints()!=false)
			{
				economySupport = true;
			}
			else
			{
				Bukkit.getConsoleSender().sendMessage("§a[PrefixShop] §c前置插件存在问题");
				//getLogger().info("前置插件存在问题");
			}
		}
		else
		{
			Bukkit.getConsoleSender().sendMessage("§a[PrefixShop] §c前置插件存在问题");
			//getLogger().info("前置插件存在问题");
		}

		loadConfig();
		loadPlayerConfig();
		getServer().getPluginManager().registerEvents(new PrefixShopListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[PrefixShop] §e称号商店已加载完毕");
	}

	public void onDisable() 
	{
		saveConfig();
		Bukkit.getConsoleSender().sendMessage("§a[PrefixShop] §e称号商店已卸载完毕");
	}
	
	public void loadPlayerConfig()
	{
		for(OfflinePlayer p:Bukkit.getOfflinePlayers())
		{
			File file=new File(getDataFolder(),"/Data/"+p.getName()+".yml");
			FileConfiguration config;
			ArrayList<String> myPrefix = new ArrayList<String>();
			if(!file.exists())
			{
				config = load(file);
				try {
					config.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				loadConfig();
			}
			config = load(file);
			String prefix = config.getString("MyPrefix");
			String onUse = config.getString("OnUse");
			if(prefix!=null)
			{
				if(prefix.contains("%%"))
				{
					for(String pre:prefix.split("%%"))
					{
						myPrefix.add(pre);
					}
				}
				else
				{
					myPrefix.add(prefix);
				}
			}
			playerData.put(p.getName(), myPrefix);
			playerOnUse.put(p.getName(), onUse);
		}
		
	}
	
	public void saveConfig()
	{
		for(String name:playerData.keySet())
		{
			File file=new File(getDataFolder(),"/Data/"+name+".yml");
			FileConfiguration config;
			config = load(file);
			String myPrefix = null;
			for(String prefix:playerData.get(name))
			{
				if(myPrefix==null)
				{
					myPrefix = prefix;
				}
				else
				{
					myPrefix = myPrefix + "%%" + prefix;
				}
			}
			config.set("MyPrefix", myPrefix);
			config.set("OnUse", playerOnUse.get(name));
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			config.set("Prefix.1.Content", "&a称号");
			config.set("Prefix.1.Cost.Type", "Money");
			config.set("Prefix.1.Cost.Price", 1000);
			config.set("Prefix.2.Content", "&a测试");
			config.set("Prefix.2.Cost.Type", "PlayerPoints");
			config.set("Prefix.2.Cost.Price", 500);
			config.set("Prefix.3.Content", "&c我是大王");
			config.set("Prefix.3.Cost.Type", "Money");
			config.set("Prefix.3.Cost.Price", 0);
			
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
		costTypeList.clear();
		prefixList.clear();
		priceList.clear();
		String prefix = null;
		String costType = null;
		int price = 0;

		for(int i=0; config.contains("Prefix."+(i+1)); i++)
		{
			prefix = config.getString("Prefix."+(i+1)+".Content");
			if(prefix.contains("&"))
			{
				prefix = prefix.replace("&", "§");
			}
			costType = config.getString("Prefix."+(i+1)+".Cost.Type");
			price = config.getInt("Prefix."+(i+1)+".Cost.Price");

			costTypeList.add(costType);
			priceList.add(price);
			prefixList.add(prefix);
		}
	}
	
	public Inventory initShop(Player p)
	{
		if(economySupport==false)
		{
			p.sendMessage("§a[称号系统]§c称号商店无法正确使用！");
			Inventory inv = Bukkit.createInventory(p, 9, "§5称号商店");
			return inv;
		}
		ArrayList<String> myPrefix = playerData.get(p.getName());
		
		Inventory inv = Bukkit.createInventory(p, ((prefixList.size()/9)+1)*9, "§5称号商店");
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		String costType = null;
		
		for(int i=0; i<prefixList.size(); i++)
		{
			if(costTypeList.get(i).equalsIgnoreCase("Money"))
				costType = "金币";
			else
				costType = "点券";
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("§5点击购买该称号");
			lore.add("§5价格:§a"+priceList.get(i)+"§5"+costType);
			if(!myPrefix.isEmpty())
			{
				if(myPrefix.contains(prefixList.get(i)))
				{
					lore.add("§4已拥有");
				}
			}
			ItemMeta meta = book.getItemMeta();
			meta.setDisplayName("§f称号:"+prefixList.get(i));
			meta.setLore(lore);
			book.setItemMeta(meta);
			
			inv.setItem(i, book);
		}
		
		return inv;
	}
	
	public Inventory initMyGUI(Player p)
	{
		ArrayList<String> myPrefix = playerData.get(p.getName());
		
		Inventory inv = Bukkit.createInventory(p, ((myPrefix.size()/9)+1)*9, "§5我的称号");
		if(myPrefix.isEmpty())
			return inv;
		ItemStack book = new ItemStack(Material.BOOK);
		String onUse = playerOnUse.get(p.getName());
		
		for(int i=0; i<myPrefix.size(); i++)
		{
			
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("§a左键点击切换该称号");
			lore.add("§c右键点击§4删除§c该称号");
			if(onUse!=null && onUse.equalsIgnoreCase(myPrefix.get(i)))
			{
				lore.add("§a当前称号");
			}
			ItemMeta meta = book.getItemMeta();
			meta.setDisplayName("§f称号:"+myPrefix.get(i));
			meta.setLore(lore);
			book.setItemMeta(meta);
			
			inv.setItem(i, book);
		}
		
		return inv;
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
		if (cmd.getName().equalsIgnoreCase("ch"))
		{
			if (args.length==0)
			{
				sender.sendMessage("§a=========[称号系统]=========");
				sender.sendMessage("§a/ch shop §3打开称号商店");
				sender.sendMessage("§a/ch my §3打开我的称号界面");
				if(sender.isOp())
				{
					sender.sendMessage("§a/ch give [玩家名] [称号] §3自定义称号");
					sender.sendMessage("§a/ch define §3自定义称号");
					sender.sendMessage("§a/ch reload §3重载配置");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender instanceof Player)
				{
					if(sender.isOp())
					{
						loadConfig();
						sender.sendMessage("§a[称号系统] §6重读配置成功！");
					}
					return true;
				}
			}
			
			if(args[0].equalsIgnoreCase("shop"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					Inventory shop = initShop(p);
					p.openInventory(shop);
					return true;
				}
			}
			
			if(args[0].equalsIgnoreCase("give"))
			{
				if(sender.isOp())
				{
					if(args.length==3)
					{
						if(Bukkit.getServer().getPlayer(args[1])!=null)
						{
							ArrayList<String> myPrefix = new ArrayList<String>();
							myPrefix = playerData.get(args[1]);
							myPrefix.add(args[2]);
							playerData.put(args[1], myPrefix);
							sender.sendMessage("§a[称号系统] §6已给予玩家§c"+ args[1] + "§6称号:§e"+args[2]);
						}
						else
						{
							sender.sendMessage("§a[称号系统] §c该玩家不在线或不存在");
						}
					}
					else
					{
						sender.sendMessage("§a/ch give [玩家名] [称号] §3自定义称号");
						return true;
					}
				}
				else
				{
					sender.sendMessage("§a[称号系统] §c你没有权限");
				}
			}
			
			if(args[0].equalsIgnoreCase("my"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					Inventory my = initMyGUI(p);
					p.openInventory(my);
					return true;
				}
			}
			
			if(args[0].equalsIgnoreCase("define"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(p.hasPermission("PrefixShop.define"))
					{
						definePlayer.add(p.getName());
						p.sendMessage("§a[称号系统] §3你目前已进入自定义称号阶段");
						p.sendMessage("§a[称号系统] §3请输入你要自定义的称号(最多5个字，输入exit退出):");
						return true;
					}
					else
					{
						p.sendMessage("§a[称号系统] §c你没有权限");
						return true;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	
}


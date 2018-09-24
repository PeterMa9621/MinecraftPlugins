package personalLottery;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PersonalLottery extends JavaPlugin
{
	public Economy economy;
	public PlayerPoints playerPoints;
	public boolean isEco = false;
	
	HashMap<String, PlayerData> playerData = new HashMap<String, PlayerData>();
	
	HashMap<Location, String> dispenser = new HashMap<Location, String>();
	
	HashMap<String, Integer> permission = new HashMap<String, Integer>();
	//int limit = 5;
	
	int money = 50;
	
	double tax = 0;
	
	private boolean hookPlayerPoints() 
	{
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("PlayerPoints");
	    playerPoints = PlayerPoints.class.cast(plugin);
	    return playerPoints != null; 
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
		if(!new File(getDataFolder(),"Data").exists()) 
		{
			new File(getDataFolder(),"Data").mkdirs();
		}
		if(Bukkit.getPluginManager().getPlugin("Vault")!=null)
		{
			isEco=setupEconomy();
		}
		if(!isEco)
		{
			Bukkit.getConsoleSender().sendMessage("§a[PersonalLottery] §cVault未加载");
		}
		if(hookPlayerPoints()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[PersonalLottery] §c点券系统未加载");
		}
		loadConfig();
		loadPlayerData();
		getServer().getPluginManager().registerEvents(new PersonalLotteryListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[PersonalLottery] §e个人抽奖系统加载完毕");
	}

	public void onDisable()
	{
		savePlayerData();
		Bukkit.getConsoleSender().sendMessage("§a[PersonalLottery] §e个人抽奖系统卸载完毕");
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		
		if(!file.exists())
		{
			config = load(file);
			String limit = "vip1:10,vip2:15,vip3:20,vip4:25";
			config.set("Limit", limit);
			config.set("Price", 50);
			config.set("Tax", 0.05);
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			loadConfig();
			return;
		}
		config = load(file);
		String limit = config.getString("Limit");
		money = config.getInt("Price");
		tax = config.getDouble("Tax");
		
		if(limit!=null)
		{
			for(String content:limit.split(","))
			{
				String vip = content.split(":")[0];
				int quantity = Integer.valueOf(content.split(":")[1]);
				permission.put("pl."+vip, quantity);
			}
		}
	}
	
	public void loadPlayerData()
	{
		File file1=new File(getDataFolder(), "/Data");
		String[] fileName = file1.list();

		if(fileName==null)
			return;
		for(String name:fileName)
		{
			String playerName = name.substring(0, name.length()-4);
			File file=new File(getDataFolder(),"/Data/"+playerName+".yml");
			FileConfiguration config;

			config = load(file);
			
			int number = config.getInt("HowManyBox");
			
			ArrayList<LotteryBox> boxes = new ArrayList<LotteryBox>();

			for(int i=1; config.contains("Box."+i); i++)
			{
				String signWorld = config.getString("Box."+i+".Sign.World");
				int signX = config.getInt("Box."+i+".Sign.X");
				int signY = config.getInt("Box."+i+".Sign.Y");
				int signZ = config.getInt("Box."+i+".Sign.Z");
				
				Location sign = new Location(Bukkit.getWorld(signWorld), signX, signY, signZ);
				
				String dispenserWorld = config.getString("Box."+i+".Dispenser.World");
				int dispenserX = config.getInt("Box."+i+".Dispenser.X");
				int dispenserY = config.getInt("Box."+i+".Dispenser.Y");
				int dispenserZ = config.getInt("Box."+i+".Dispenser.Z");
				
				Location dispenser = new Location(Bukkit.getWorld(dispenserWorld), dispenserX, dispenserY, dispenserZ);
				
				this.dispenser.put(dispenser, playerName);
				
				LotteryBox box = new LotteryBox(playerName, sign, dispenser);
				boxes.add(box);
			}
			PlayerData pd = new PlayerData(playerName, boxes, number);
			// #################################################################
			// pl.vip1 -> 10    pl.vip2 -> 15    pl.vip3 -> 20    pl.vip4 -> 25
			// #################################################################
			playerData.put(playerName, pd);
		}
	}
	
	public void savePlayerData()
	{
		for(String name:playerData.keySet())
		{
			File file=new File(getDataFolder(),"/Data/"+name+".yml");
			FileConfiguration config;
			
			config = load(file);
			
			int number = playerData.get(name).getNumber();
			config.set("HowManyBox", number);
			
			ArrayList<LotteryBox> boxes = playerData.get(name).getLotteryBox();
			
			int i=1;
			for(LotteryBox box:boxes)
			{
				config.set("Box."+i+".Sign.World", box.getSignLocation().getWorld().getName());
				config.set("Box."+i+".Sign.X", box.getSignLocation().getBlockX());
				config.set("Box."+i+".Sign.Y", box.getSignLocation().getBlockY());
				config.set("Box."+i+".Sign.Z", box.getSignLocation().getBlockZ());
				
				config.set("Box."+i+".Dispenser.World", box.getDispenserLocation().getWorld().getName());
				config.set("Box."+i+".Dispenser.X", box.getDispenserLocation().getBlockX());
				config.set("Box."+i+".Dispenser.Y", box.getDispenserLocation().getBlockY());
				config.set("Box."+i+".Dispenser.Z", box.getDispenserLocation().getBlockZ());
				
				i++;
			}
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
	
	public Inventory createGui(Player p, ItemStack[] items)
	{
		Inventory inv = Bukkit.createInventory(p, 9, "§5查看抽奖箱物品");
		int i=0;
		for(ItemStack item:items)
		{
			if(item!=null)
			{
				inv.setItem(i, item);
				i++;
			}	
		}
			
		return inv;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("pl"))
		{
			if (args.length==0)
			{
				sender.sendMessage("§a=========[个人抽奖系统]=========");
				sender.sendMessage("§a/pl my §3查看我的抽奖箱状态");
				if(sender.isOp())
				{
					sender.sendMessage("§a/pl reload §3重读配置");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("my"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(playerData.containsKey(p.getName()))
					{
						int limit = 5;
						for(String perm:permission.keySet())
						{
							if(p.hasPermission(perm))
								limit = permission.get(perm);
						}
						int number = playerData.get(p.getName()).getNumber();
						p.sendMessage("§6[私人抽奖箱] §e你已创建§5"+number+"§e个抽奖箱,还可创建§5"+(limit-number)+"§e个抽奖箱");
					}
					else
					{
						p.sendMessage("§6[私人抽奖箱] §e你从未创建过抽奖箱");
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					loadConfig();
					sender.sendMessage("§6[私人抽奖箱] §e重读成功！");
					return true;
				}
			}
			return true;
		}
		return false;
	}
}


package antiXray;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class AntiXray extends JavaPlugin
{
	int totalPoints = 0;
	boolean notify = false;
	int recoverPoint = 0;
	ItemStack recoverItem = null;
	HashMap<String, Integer> playerData = new HashMap<String, Integer>();
	HashMap<String, String> lastLogout = new HashMap<String, String>();
	HashMap<String, String> message = new HashMap<String, String>();
	HashMap<String, Worlds> worlds = new HashMap<String, Worlds>();
	ArrayList<Integer> tools = new ArrayList<Integer>();
	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<String> op = new ArrayList<String>();
	
	ArrayList<String> recipe = new ArrayList<String>();
	
	public void onEnable()
	{
		if(!new File(getDataFolder(),"Data").exists())
		{
			new File(getDataFolder(),"Data").mkdirs();
		}
		getOP();
		loadConfig();
		initRecoverItemRecipe();
		loadMessageConfig();
		loadPlayerConfig();
		task();
		getServer().getPluginManager().registerEvents(new AntiXrayListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[AntiXray] §e限制挖矿系统加载完毕");
		Bukkit.getConsoleSender().sendMessage("§a[AntiXray] §e制作者QQ:920157557");
	}

	public void onDisable()
	{
		saveConfig();
		Bukkit.getConsoleSender().sendMessage("§a[AntiXray] §e限制挖矿系统卸载完毕");
		Bukkit.getConsoleSender().sendMessage("§a[AntiXray] §e制作者QQ:920157557");
	}
	
	public void initRecoverItemRecipe()
	{
		String [] recipes = {"ABC",
							 "DEF",
							 "GHI"};
		ShapedRecipe recipe = new ShapedRecipe(recoverItem);
		recipe.shape(recipes);
		ItemStack item = null;

		for(int i=0; i<9; i++) {
			if(!this.recipe.get(i).equals("0"))
			{
				item = new ItemStack(Material.getMaterial(this.recipe.get(i)));
				recipe.setIngredient((char)(i+65), item.getData());
			}
		}
		
		getServer().addRecipe(recipe);
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
	
	public void getOP()
	{
		op.clear();
		for(OfflinePlayer p:Bukkit.getOfflinePlayers())
		{
			if(p.isOp())
			{
				op.add(p.getName());
			}
		}
	}
	
	public void saveConfig()
	{
		for(String name:playerData.keySet())
		{
			File file = new File(getDataFolder(), "/data/"+name+".yml");
			FileConfiguration config;
			config = load(file);
			config.set("AntiXray.Points", playerData.get(name));
			config.set("LastLogout", lastLogout.get(name));
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for(Player p:Bukkit.getOnlinePlayers())
		{
			File file=new File(getDataFolder(),"/data/" + p.getName()+ ".yml");
			FileConfiguration config;
			
			config = load(file);
			
			config.set("LastLogout", date.format(new Date()));
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		File file = new File(getDataFolder(), "config.yml");
		FileConfiguration config;
		config = load(file);
		
		config.set("Notify", notify);
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadPlayerConfig()
	{
		File file1=new File(getDataFolder(), "/Data");
		String[] fileName = file1.list();
		for(String name:fileName)
		{
			File file=new File(getDataFolder(),"/data/"+name+".yml");
			FileConfiguration config;
			if (file.exists())
			{
				config = load(file);
				
				config.set("TodayDate", date.format(new Date()));
				try {
					config.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}

				if(config.getString("LastLogout")!=null)
				{
					if(!config.getString("LastLogout").equalsIgnoreCase(config.getString("TodayDate")))
					{
						config.set("AntiXray.Points", totalPoints);
					}
					lastLogout.put(name, config.getString("LastLogout"));
				}
				try {
					config.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				playerData.put(name, config.getInt("AntiXray.Points"));
			}
		}
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
						playerData.put(p.getName(), totalPoints);
					}
				}
				previousDate = date.format(new Date());
			}
		}.runTaskTimer(this, 0L, 20L);
	}
	
	public void loadMessageConfig()
	{
		File file = new File(getDataFolder(), "message.yml");
		FileConfiguration config;
		if(!file.exists())
		{
			config = load(file);
			
			config.set("HowManyPoints", "§a[反透视系统] §3您当前剩余挖矿点数为§a{points}");
			config.set("Reload", "§a[反透视系统] §c配置重载成功!");
			
			config.set("CannotFindPlayer", "§a[反透视系统] §4目标玩家不在线或不存在");
			config.set("CheckPlayerPoints", "§a[反透视系统] §3该玩家当前剩余挖矿点数为§a{points}");
			config.set("DontHavePermission", "§a[反透视系统] §c你没有权限这么做");
			
			config.set("GivePlayerPoints", "§a[反透视系统] §3你给予§a {player} §2 {givepoints} §3 挖矿点数，该玩家当前剩余挖矿点数为§a{points}");
			config.set("ReceivePlayerPoints", "§a[反透视系统] §3你收到来自§e{player}§3的§a{receivepoints}挖矿点数");
			config.set("GetRecoverItem", "§a[反透视系统] §3获得物品");
			config.set("ResetPlayer", "§a[反透视系统] §3已重置玩家§a {player} §3的挖矿点数");
			
			config.set("DontHaveEnoughPoints", "§a[反透视系统] §3挖矿点数不足。您当前挖矿点数为§a {points}");
			config.set("AlreadyHadEnoughPoints", "§a[反透视系统] §3你的挖矿点数已达上限");
			config.set("RecoverPoints", "§a[反透视系统] §3你的挖矿点数增加了§a {recoverpoints}");
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			loadMessageConfig();
		}
		
		config = load(file);
		
		message.put("HowManyPoints", config.getString("HowManyPoints"));
		message.put("Reload", config.getString("Reload"));
		
		message.put("CannotFindPlayer", config.getString("CannotFindPlayer"));
		message.put("CheckPlayerPoints", config.getString("CheckPlayerPoints"));
		message.put("DontHavePermission", config.getString("DontHavePermission"));
		
		message.put("GivePlayerPoints", config.getString("GivePlayerPoints"));
		message.put("ReceivePlayerPoints", config.getString("ReceivePlayerPoints"));
		message.put("GetRecoverItem", config.getString("GetRecoverItem"));
		message.put("ResetPlayer", config.getString("ResetPlayer"));
		
		message.put("DontHaveEnoughPoints", config.getString("DontHaveEnoughPoints"));
		message.put("AlreadyHadEnoughPoints", config.getString("AlreadyHadEnoughPoints"));
		message.put("DontHaveEnoughPoints", config.getString("DontHaveEnoughPoints"));
		message.put("RecoverPoints", config.getString("RecoverPoints"));
	}
	
	public void loadConfig()
	{
		File file = new File(getDataFolder(), "config.yml");
		FileConfiguration config;
		if(!file.exists())
		{
			config = load(file);

			config.set("Points", 600);
			
			config.set("RecoverItem.ID", 266);
			config.set("RecoverItem.Point", 100);
			config.set("RecoverItem.Name", "§2矿工玉米");
			config.set("RecoverItem.Lore", "§a这是一瓶神奇的玉米%§a右键吃掉它可以增加100挖矿点数");
			config.set("RecoverItem.Recipe", "0-266-0-266-266-266-0-266-0");
			
			config.set("ToolsLimit", "257,270,274,278,285");
			
			config.set("Worlds", "world,world_nether");
			
			config.set("Notify", true);
			
			//====================================================
			
			config.set("world.BlockInfo", "14:10,15:5,56:30,129:50");
			
			config.set("world.HeightUnlimit", 60);
			
			config.set("world.RecoverPointByBreakingBlock.OnUse", false);
			
			config.set("world.RecoverPointByBreakingBlock.BlockInfo", "1:1,2:1");
			
			//====================================================
			
			config.set("world_nether.BlockInfo", "89:10");
			
			config.set("world_nether.HeightUnlimit", 80);
			
			config.set("world_nether.RecoverPointByBreakingBlock.OnUse", false);
			
			config.set("world_nether.RecoverPointByBreakingBlock.BlockInfo", "87:1");
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			loadConfig();
		}
		
		tools.clear();
		
		config = load(file);
		// Get the total points
		totalPoints = config.getInt("Points");
		
		notify = config.getBoolean("Notify");
		
		// Get the information of the recover item
		String id = config.getString("RecoverItem.ID");
		String itemName = config.getString("RecoverItem.Name");
		String itemLore = config.getString("RecoverItem.Lore");
		recoverPoint = config.getInt("RecoverItem.Point");
		
		recoverItem = new ItemStack(Material.getMaterial(id.toUpperCase()));
		ItemMeta meta = recoverItem.getItemMeta();
		meta.setDisplayName(itemName);

		ArrayList<String> lore = new ArrayList<String>();
		for(String l:itemLore.split("%"))
		{
			lore.add(l);
		}
		meta.setLore(lore);

		recoverItem.setItemMeta(meta);
		
		// Get different worlds' settings
		for(String w:config.getString("Worlds").split(","))
		{
			HashMap<Integer, Integer> blocks = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> blocksForRecover = new HashMap<Integer, Integer>();
			String blockInfo = config.getString(w+".BlockInfo");
			String RecoverBlock = config.getString(w+".RecoverPointByBreakingBlock.BlockInfo");
			boolean recoverBlock = config.getBoolean(w+".RecoverPointByBreakingBlock.OnUse");
			int heightUnlimit = config.getInt(w+".HeightUnlimit");
			
			int blockID = 0;
			int blockPoint = 0;
			for(String i:blockInfo.split(","))
			{
				blockID = Integer.valueOf(i.split(":")[0]);
				blockPoint = Integer.valueOf(i.split(":")[1]);
				blocks.put(blockID, blockPoint);
			}
			
			for(String i:RecoverBlock.split(","))
			{
				blockID = Integer.valueOf(i.split(":")[0]);
				blockPoint = Integer.valueOf(i.split(":")[1]);
				blocksForRecover.put(blockID, blockPoint);
			}
			
			Worlds world = new Worlds(w, blocks, heightUnlimit, recoverBlock, blocksForRecover);
			worlds.put(w, world);
		}
		
		for(String i:config.getString("ToolsLimit").split(","))
		{
			tools.add(Integer.valueOf(i));
		}
		
		for(String r:config.getString("RecoverItem.Recipe").split("-"))
		{
			recipe.add(r.toUpperCase());
		}
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("ax"))
		{
			String HowManyPoints = message.get("HowManyPoints");
			String Reload = message.get("Reload");
			String CannotFindPlayer = message.get("CannotFindPlayer");
			String CheckPlayerPoints = message.get("CheckPlayerPoints");
			String DontHavePermission = message.get("DontHavePermission");
			String GivePlayerPoints = message.get("GivePlayerPoints");
			String ReceivePlayerPoints = message.get("ReceivePlayerPoints");
			String GetRecoverItem = message.get("GetRecoverItem");
			String ResetPlayer = message.get("ResetPlayer");
			
			
			if(!sender.hasPermission("antiXray.player"))
			{
				return true;
			}
			
			if (args.length==0)
			{
				sender.sendMessage("§a=========[反透视系统]=========");
				sender.sendMessage("§a/ax my §3查看当前剩余挖矿点数");
				if(sender.isOp())
				{
					sender.sendMessage("§a/ax get §3获得一个矿工药水");
					sender.sendMessage("§a/ax check [玩家名] §3查看目标玩家剩余挖矿点数");
					sender.sendMessage("§a/ax give [玩家名] [挖矿点数] §3给予目标玩家相应的挖矿点数");
					sender.sendMessage("§a/ax reset [玩家名] §3重置该玩家的挖矿点数");
					sender.sendMessage("§a/ax notify §3开关通知提示");
					sender.sendMessage("§a/ax reloadOP §3重新获取所有OP");
					sender.sendMessage("§a/ax reload §3重载插件");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("my"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					int currentPoint = playerData.get(p.getName());
					if(HowManyPoints.contains("{points}"))
						HowManyPoints = HowManyPoints.replace("{points}", String.valueOf(currentPoint));
					p.sendMessage(HowManyPoints);
				}
			}
			
			if(args[0].equalsIgnoreCase("notify"))
			{
				if(sender.isOp())
				{
					if(notify==false)
					{
						notify=true;
						sender.sendMessage("§6已开启通知提示");
					}
					else
					{
						notify=false;
						sender.sendMessage("§6已关闭通知提示");
					}
				}
				else
				{
					sender.sendMessage(DontHavePermission);
				}
			}
			
			if(args[0].equalsIgnoreCase("reloadOP"))
			{
				if(sender.isOp())
				{
					getOP();
					sender.sendMessage(Reload);
				}
				else
				{
					sender.sendMessage(DontHavePermission);
				}
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					loadConfig();
					loadMessageConfig();
					sender.sendMessage(Reload);
				}
				else
				{
					sender.sendMessage(DontHavePermission);
				}
			}

			if(args[0].equalsIgnoreCase("check"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						Player p = Bukkit.getServer().getPlayer(args[1]);
						if(p==null)
						{
							sender.sendMessage(CannotFindPlayer);
							return true;
						}
						int currentPoint = playerData.get(args[1]);
						if(CheckPlayerPoints.contains("{points}"))
						{
							CheckPlayerPoints = CheckPlayerPoints.replace("{points}", String.valueOf(currentPoint));
						}
						sender.sendMessage(CheckPlayerPoints);
					}
					else
					{
						sender.sendMessage("§a/ax check [玩家名] §3查看目标玩家剩余挖矿点数");
					}
				}
				else
				{
					sender.sendMessage(DontHavePermission);
				}
				
			}
			
			if(args[0].equalsIgnoreCase("give"))
			{
				if(sender.isOp())
				{
					if(args.length==3)
					{
						Player p = Bukkit.getServer().getPlayer(args[1]);
						if(p==null)
						{
							sender.sendMessage(CannotFindPlayer);
							return true;
						}
						int previousPoint = playerData.get(args[1]);
						playerData.put(args[1], previousPoint+Integer.valueOf(args[2]));
						if(GivePlayerPoints.contains("{player}"))
							GivePlayerPoints = GivePlayerPoints.replace("{player}", args[1]);
						if(GivePlayerPoints.contains("{givepoints}"))
							GivePlayerPoints = GivePlayerPoints.replace("{givepoints}", args[2]);
						if(GivePlayerPoints.contains("{points}"))
							GivePlayerPoints = GivePlayerPoints.replace("{points}", String.valueOf(previousPoint+Integer.valueOf(args[2])));
						sender.sendMessage(GivePlayerPoints);
						
						
						if(ReceivePlayerPoints.contains("{player}"))
							ReceivePlayerPoints = ReceivePlayerPoints.replace("{player}", sender.getName());
						if(ReceivePlayerPoints.contains("{receivepoints}"))
							ReceivePlayerPoints = ReceivePlayerPoints.replace("{receivepoints}", args[2]);
						p.sendMessage(ReceivePlayerPoints);
					}
					else
					{
						sender.sendMessage("§a/ax give [玩家名] [挖矿点数] §3给予目标玩家相应的挖矿点数");
					}
				}
				else
				{
					sender.sendMessage(DontHavePermission);
				}
			}
			
			if(args[0].equalsIgnoreCase("get"))
			{
				if(sender instanceof Player)
				{
					if(sender.isOp())
					{
						Player p = (Player)sender;
						p.getInventory().addItem(recoverItem);
						p.sendMessage(GetRecoverItem);
					}
					else
					{
						sender.sendMessage(DontHavePermission);
					}
				}
				
			}
			
			if(args[0].equalsIgnoreCase("reset"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						Player p = Bukkit.getServer().getPlayer(args[1]);
						if(p==null)
						{
							sender.sendMessage(CannotFindPlayer);
							return true;
						}
						playerData.put(args[1], totalPoints);
						if(ResetPlayer.contains("{player}"))
							ResetPlayer = ResetPlayer.replace("{player}", args[1]);
						sender.sendMessage(ResetPlayer);
					}
					else
					{
						sender.sendMessage("§a/ax reset [玩家名] §3重置该玩家的挖矿点数");
					}
				}
				else
				{
					sender.sendMessage(DontHavePermission);
				}
			}
			return true;
		}

		return false;
		
	}
	
	
}


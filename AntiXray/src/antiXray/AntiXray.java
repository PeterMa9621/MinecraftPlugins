package antiXray;

import antiXray.expansion.AntiXrayExpansion;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

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
import peterUtil.database.ConfigStructure;
import peterUtil.database.Database;
import peterUtil.database.DatabaseType;
import peterUtil.database.StorageInterface;

public class AntiXray extends JavaPlugin
{
	int totalPoints = 0;
	int recoverPointPerMinute = 0;
	boolean notify = false;
	int recoverPoint = 0;
	ItemStack recoverItem = null;
	DatabaseType databaseType;
	public HashMap<UUID, Integer> playerData = new HashMap<UUID, Integer>();
	HashMap<UUID, String> lastLogin = new HashMap<UUID, String>();
	HashMap<String, String> message = new HashMap<String, String>();
	HashMap<String, Worlds> worlds = new HashMap<String, Worlds>();
	ArrayList<Material> tools = new ArrayList<Material>();
	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<String> op = new ArrayList<String>();

	ArrayList<String> recipe = new ArrayList<String>();
	
	public void onEnable()
	{
		if(!new File(getDataFolder(),"Data").exists())
		{
			new File(getDataFolder(),"Data").mkdirs();
		}

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new AntiXrayExpansion(this).register();
		}

		String createTableQuery = "create table if not exists anti_xray(id varchar(100), points varchar(10), last_logout varchar(10));";
		Database.setConnectionInfo("minecraft", "anti_xray", "root", "mjy159357", createTableQuery);

		getOP();
		loadConfig();
		initRecoverItemRecipe();
		loadMessageConfig();
		recoverPointTask();

		getServer().getPluginManager().registerEvents(new AntiXrayListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[AntiXray] §e限制挖矿系统加载完毕");
		Bukkit.getConsoleSender().sendMessage("§a[AntiXray] §e制作者QQ:920157557");
	}

	public void recoverPointTask()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				if(recoverPointPerMinute > 0){
					for(UUID uniqueId:playerData.keySet())
					{
						int currentPoint = playerData.get(uniqueId);
						if(currentPoint < totalPoints){
							currentPoint += recoverPointPerMinute;
							playerData.put(uniqueId, Math.min(currentPoint, totalPoints));
						}
						Bukkit.getConsoleSender().sendMessage("Recover " + recoverPointPerMinute + " point for " + uniqueId.toString());
					}
				}
			}
		}.runTaskTimer(this, 0L, 1200L);
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
		StorageInterface database = Database.getInstance(databaseType, this);

		for(UUID uniqueId:playerData.keySet())
		{
			HashMap<String, Object> data = new HashMap<String, Object>() {{
				put("points", playerData.get(uniqueId));
				put("last_login", lastLogin.get(uniqueId));
			}};
			ConfigStructure configStructure = new ConfigStructure(data);
			try {
				database.store(uniqueId, configStructure);
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
	
	public void loadPlayerConfig(UUID uuid)
	{
		String todayDate = date.format(new Date());
		String lastLogin;
		if(playerData.containsKey(uuid)){
			lastLogin = this.lastLogin.get(uuid);
		} else {
			StorageInterface database = Database.getInstance(databaseType, this);
			HashMap<String, Object> result = database.get(uuid, new String[] {"points", "last_login"});
			if(result==null){
				playerData.put(uuid, totalPoints);
				this.lastLogin.put(uuid, "0000-00-00");
				return;
			}

			lastLogin = (String) result.get("last_login");

			playerData.put(uuid, (Integer) result.get("points"));
		}

		if(!lastLogin.equalsIgnoreCase(todayDate)){
			playerData.put(uuid, totalPoints);
		}
		this.lastLogin.put(uuid, todayDate);
	}

	/*
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

	 */
	
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
			config.set("Database", "YML");
			config.set("RecoverPointPerMinute", 30);
			config.set("Points", 600);
			
			config.set("RecoverItem.ID", "gold_ingot");
			config.set("RecoverItem.Point", 100);
			config.set("RecoverItem.Name", "§2矿工玉米");
			config.set("RecoverItem.Lore", "§a这是一瓶神奇的玉米%§a右键吃掉它可以增加100挖矿点数");
			config.set("RecoverItem.Recipe", "0-gold_ingot-0-gold_ingot-gold_ingot-gold_ingot-0-gold_ingot-0");
			
			config.set("ToolsLimit", "iron_pickaxe,wooden_pickaxe,stone_pickaxe,diamond_pickaxe,golden_pickaxe");
			
			config.set("Worlds", "world,world_nether");
			
			config.set("Notify", true);
			
			//====================================================
			
			config.set("world.BlockInfo", "gold_ore:10,iron_ore:5,diamond_ore:30,emerald_ore:50");
			
			config.set("world.HeightUnlimit", 60);
			
			config.set("world.RecoverPointByBreakingBlock.OnUse", false);
			
			config.set("world.RecoverPointByBreakingBlock.BlockInfo", "stone:1,grass_block:1");
			
			//====================================================
			
			config.set("world_nether.BlockInfo", "glowstone:10");
			
			config.set("world_nether.HeightUnlimit", 80);
			
			config.set("world_nether.RecoverPointByBreakingBlock.OnUse", false);
			
			config.set("world_nether.RecoverPointByBreakingBlock.BlockInfo", "soul_sand:1");
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			loadConfig();
			return;
		}
		
		tools.clear();
		
		config = load(file);

		databaseType = DatabaseType.valueOf(config.getString("Database", "YML"));
		recoverPointPerMinute = config.getInt("RecoverPointPerMinute", 30);

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
			HashMap<Material, Integer> blocks = new HashMap<>();
			HashMap<Material, Integer> blocksForRecover = new HashMap<>();
			String blockInfo = config.getString(w+".BlockInfo");
			String RecoverBlock = config.getString(w+".RecoverPointByBreakingBlock.BlockInfo");
			boolean recoverBlock = config.getBoolean(w+".RecoverPointByBreakingBlock.OnUse");
			int heightUnlimit = config.getInt(w+".HeightUnlimit");
			
			String blockID = "";
			int blockPoint = 0;
			for(String i:blockInfo.split(","))
			{
				blockID = i.split(":")[0].toUpperCase();
				blockPoint = Integer.valueOf(i.split(":")[1]);
				blocks.put(Material.valueOf(blockID), blockPoint);
			}
			
			for(String i:RecoverBlock.split(","))
			{
				blockID = i.split(":")[0].toUpperCase();
				blockPoint = Integer.valueOf(i.split(":")[1]);
				blocksForRecover.put(Material.valueOf(blockID), blockPoint);
			}
			
			Worlds world = new Worlds(w, blocks, heightUnlimit, recoverBlock, blocksForRecover);
			worlds.put(w, world);
		}
		
		for(String tool:config.getString("ToolsLimit").split(","))
		{
			tools.add(Material.getMaterial(tool.toUpperCase()));
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
					int currentPoint = playerData.get(p.getUniqueId());
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
						playerData.put(p.getUniqueId(), previousPoint+Integer.valueOf(args[2]));
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
						playerData.put(p.getUniqueId(), totalPoints);
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


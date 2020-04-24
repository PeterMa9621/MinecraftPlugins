package pvpSwitch;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import peterUtil.database.Database;
import peterUtil.database.DatabaseType;
import peterUtil.database.StorageInterface;
import pvpSwitch.expansion.PVPSwitchExpansion;
import pvpSwitch.model.PvpPlayer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PVPSwitch extends JavaPlugin
{
	public HashMap<UUID, PvpPlayer> playerData = new HashMap<>();

	DatabaseType databaseType;
	StorageInterface database;
	private String databaseName;

	private PVPSwitchAPI api = new PVPSwitchAPI(this);
	
	public void onEnable() 
	{
		if(!new File(getDataFolder(),"Data").exists()) 
		{
			new File(getDataFolder(),"Data").mkdirs();
		}

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new PVPSwitchExpansion(this).register();
		}

		loadConfig();
		String createTableQuery = "create table if not exists pvp_switch(id varchar(100), can_pvp tinyint, is_banned tinyint, primary key(id));";
		database = Database.getInstance(databaseType, this);
		database.connect(databaseName, "pvp_switch", "root", "mjy159357", createTableQuery);
		getServer().getPluginManager().registerEvents(new PVPSwitchListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[PVPSwitch] §ePVP系统加载完毕");
	}

	public void onDisable() 
	{
		saveConfig();
		Bukkit.getConsoleSender().sendMessage("§a[PVPSwitch] §ePVP系统卸载完毕");
	}
	
	public void saveConfig()
	{
		for(UUID uniqueId:playerData.keySet()){
			PvpPlayer pvpPlayer = playerData.get(uniqueId);
			HashMap<String, Object> data = new HashMap<String, Object>() {{
				put("can_pvp", pvpPlayer.canPvp());
				put("is_banned", pvpPlayer.isBanned());
			}};
			try {
				database.store(uniqueId, data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadConfig() {
		File file=new File(getDataFolder(), "config.yml");
		FileConfiguration config;

		if(!file.exists()){
			config = load(file);
			config.set("Database", "YML");
			config.set("DatabaseName", "minecraft");

			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}

			loadConfig();
			return;
		}
		config = load(file);
		databaseType = DatabaseType.valueOf(config.getString("Database", "YML"));
		databaseName = config.getString("DatabaseName", "minecraft");
	}

	public PvpPlayer loadPlayerConfig(UUID uniqueId) {
		if(playerData.containsKey(uniqueId)){
			return playerData.get(uniqueId);
		}

		HashMap<String, Object> result = database.get(uniqueId, new String[] {"can_pvp", "is_banned"});

		if(result==null){
			PvpPlayer pvpPlayer = new PvpPlayer(uniqueId, false, false);
			playerData.put(uniqueId, pvpPlayer);
			return pvpPlayer;
		}

		Boolean can_pvp = ((Integer)result.get("can_pvp")) == 1;

		Boolean is_banned = ((Integer)result.get("is_banned")) == 1;

		PvpPlayer pvpPlayer = new PvpPlayer(uniqueId, can_pvp, is_banned);
		playerData.put(uniqueId, pvpPlayer);
		return pvpPlayer;
	}
	
	public PVPSwitchAPI getAPI()
	{
		return api;
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
		if (cmd.getName().equalsIgnoreCase("pvp"))
		{
			if (args.length==0)
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					PvpPlayer pvpPlayer = playerData.get(p.getUniqueId());
					if(!pvpPlayer.isBanned())
					{
						pvpPlayer.setPvp(!pvpPlayer.canPvp());
						if(pvpPlayer.canPvp()) {
							p.sendMessage("§6[PVP]§3 PVP已打开");
						}
						else {
							p.sendMessage("§6[PVP]§3 PVP已关闭");
						}
					}
					else {
						p.sendMessage("§6[PVP]§c 你现在不能切换PVP开关!");
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage("§a=========§6[PVP系统]§a=========");
				sender.sendMessage("§6/pvp state §3查看当前PVP开关状态");
				sender.sendMessage("§6/pvp help §3查看帮助");
				if(sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("§a/pvp on [玩家名] §3强制打开目标玩家的PVP");
					sender.sendMessage("§a/pvp off [玩家名] §3强制关闭目标玩家的PVP");
					sender.sendMessage("§a/pvp ban [玩家名] §3禁止目标玩家切换PVP状态");
					sender.sendMessage("§a/pvp unban [玩家名] §3允许目标玩家切换PVP状态");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("ban"))
			{
				if(!sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("§6[PVP]§c 你没有权限");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("§6[PVP]§c 该玩家不存在或不在线");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);

					PvpPlayer pvpPlayer = playerData.get(p.getUniqueId());
					pvpPlayer.setBanned(true);
					playerData.put(p.getUniqueId(), pvpPlayer);
					
					sender.sendMessage("§6[PVP]§3 已禁止玩家§c"+p.getName()+"§3切换PVP");
					p.sendMessage("§6[PVP]§c 注意：你现在已被禁止切换PVP状态");
					return true;
				}
				else
				{
					sender.sendMessage("§6/pvp ban [玩家名] §3禁止目标玩家切换PVP状态");
				}
			}
			
			if(args[0].equalsIgnoreCase("unban"))
			{
				if(!sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("§6[PVP]§c 你没有权限");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("§6[PVP]§c 该玩家不存在或不在线");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);

					PvpPlayer pvpPlayer = playerData.get(p.getUniqueId());
					pvpPlayer.setBanned(false);
					playerData.put(p.getUniqueId(), pvpPlayer);
					
					sender.sendMessage("§6[PVP]§3 已允许玩家§c"+p.getName()+"§3切换PVP");
					p.sendMessage("§6[PVP]§c 注意：你现在已被允许切换PVP状态");
					return true;
				}
				else
				{
					sender.sendMessage("§6/pvp unban [玩家名] §3允许目标玩家切换PVP状态");
				}
			}
			
			if(args[0].equalsIgnoreCase("on"))
			{
				if(!sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("§6[PVP]§c 你没有权限");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("§6[PVP]§c 该玩家不存在或不在线");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);

					PvpPlayer pvpPlayer = playerData.get(p.getUniqueId());
					pvpPlayer.setPvp(true);
					playerData.put(p.getUniqueId(), pvpPlayer);

					sender.sendMessage("§6[PVP]§3 已强制打开玩家§c"+p.getName()+"§3的PVP");
					p.sendMessage("§6[PVP]§c 注意：你的PVP开关已被强制打开");
					return true;
				}
				else
				{
					sender.sendMessage("§6/pvp on [玩家名] §3强制打开目标玩家的PVP");
				}
			}
			
			if(args[0].equalsIgnoreCase("off"))
			{
				if(!sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("§6[PVP]§c 你没有权限");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("§6[PVP]§c 该玩家不存在或不在线");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);

					PvpPlayer pvpPlayer = playerData.get(p.getUniqueId());
					pvpPlayer.setPvp(false);
					playerData.put(p.getUniqueId(), pvpPlayer);

					sender.sendMessage("§6[PVP]§3 已强制关闭玩家§c"+p.getName()+"§3的PVP");
					p.sendMessage("§6[PVP]§c 注意：你的PVP开关已被强制关闭");
					return true;
				}
				else
				{
					sender.sendMessage("§6/pvp off [玩家名] §3强制关闭目标玩家的PVP");
				}
			}
			
			if(args[0].equalsIgnoreCase("state"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;

					PvpPlayer pvpPlayer = playerData.get(p.getUniqueId());

					if(!pvpPlayer.canPvp())
						sender.sendMessage("§6[PVP]§3 你的PVP为关闭状态");
					else
						sender.sendMessage("§6[PVP]§3 你的PVP为打开状态");
				}
				return true;
			}
			return true;
		}
		return false;
	}
}


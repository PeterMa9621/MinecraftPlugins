package pvpSwitch;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PVPSwitch extends JavaPlugin
{
	HashMap<String, Boolean> playerData = new HashMap<String, Boolean>();
	HashMap<String, Boolean> bannedPlayer = new HashMap<String, Boolean>();
	
	private PVPSwitchAPI api = new PVPSwitchAPI(this);
	
	public void onEnable() 
	{
		if(!new File(getDataFolder(),"Data").exists()) 
		{
			new File(getDataFolder(),"Data").mkdirs();
		}
		if(!new File(getDataFolder(),"Player").exists()) 
		{
			new File(getDataFolder(),"Player").mkdirs();
		}
		
		loadConfig();
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
		// save the player's pvp state
		for(String playerName:playerData.keySet())
		{
			File file=new File(getDataFolder(),"/Player/"+playerName+".yml");
			FileConfiguration config;
			config = load(file);
			
			config.set("PVP.onUse", playerData.get(playerName));
			
			try 
			{
				config.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		// save the banned player
		File file=new File(getDataFolder(),"/Data/ban.yml");

		FileConfiguration config;
		config = load(file);

		config.set("bannedPlayer", bannedPlayer);
		
		try 
		{
			config.save(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void loadConfig()
	{
		for(OfflinePlayer p:Bukkit.getOfflinePlayers())
		{
			File file=new File(getDataFolder(), "/Player/"+p.getName()+".yml");
			FileConfiguration config;
			
			config = load(file);
			
			playerData.put(p.getName(), config.getBoolean("PVP.onUse"));
			bannedPlayer.put(p.getName(), false);
		}
		
		File file=new File(getDataFolder(), "/Data/ban.yml");
		FileConfiguration config;
		
		config = load(file);
		for(String name:config.getStringList("bannedPlayer"))
		{
			bannedPlayer.put(name, true);
		}
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
					if(bannedPlayer.get(p.getName())==false)
					{
						if(playerData.get(p.getName())==false)
						{
							playerData.put(p.getName(), true);
							p.sendMessage("§a[PVP]§3 PVP已打开");
						}
						else
						{
							playerData.put(p.getName(), false);
							p.sendMessage("§a[PVP]§3 PVP已关闭");
						}
					}
					else
					{
						p.sendMessage("§a[PVP]§c 你现在不能切换PVP开关!");
					}
					

				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage("§a=========§6[PVP系统]§a=========");
				sender.sendMessage("§a/pvp state §3查看当前PVP开关状态");
				sender.sendMessage("§a/pvp help §3查看帮助");
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
					sender.sendMessage("§a[PVP]§c 你没有权限");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("§a[PVP]§c 该玩家不存在或不在线");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);
					
					bannedPlayer.put(p.getName(), true);
					
					sender.sendMessage("§a[PVP]§3 已禁止玩家§c"+p.getName()+"§3切换PVP");
					p.sendMessage("§a[PVP]§c 注意：你现在已被禁止切换PVP状态");
					return true;
				}
				else
				{
					sender.sendMessage("§a/pvp ban [玩家名] §3禁止目标玩家切换PVP状态");
				}
			}
			
			if(args[0].equalsIgnoreCase("unban"))
			{
				if(!sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("§a[PVP]§c 你没有权限");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("§a[PVP]§c 该玩家不存在或不在线");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);
					
					bannedPlayer.put(p.getName(), false);
					
					sender.sendMessage("§a[PVP]§3 已允许玩家§c"+p.getName()+"§3切换PVP");
					p.sendMessage("§a[PVP]§c 注意：你现在已被允许切换PVP状态");
					return true;
				}
				else
				{
					sender.sendMessage("§a/pvp unban [玩家名] §3允许目标玩家切换PVP状态");
				}
			}
			
			if(args[0].equalsIgnoreCase("on"))
			{
				if(!sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("§a[PVP]§c 你没有权限");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("§a[PVP]§c 该玩家不存在或不在线");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);
					
					playerData.put(p.getName(), true);

					sender.sendMessage("§a[PVP]§3 已强制打开玩家§c"+p.getName()+"§3的PVP");
					p.sendMessage("§a[PVP]§c 注意：你的PVP开关已被强制打开");
					return true;
				}
				else
				{
					sender.sendMessage("§a/pvp on [玩家名] §3强制打开目标玩家的PVP");
				}
			}
			
			if(args[0].equalsIgnoreCase("off"))
			{
				if(!sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("§a[PVP]§c 你没有权限");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("§a[PVP]§c 该玩家不存在或不在线");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);
					
					playerData.put(p.getName(), false);

					sender.sendMessage("§a[PVP]§3 已强制关闭玩家§c"+p.getName()+"§3的PVP");
					p.sendMessage("§a[PVP]§c 注意：你的PVP开关已被强制关闭");
					return true;
				}
				else
				{
					sender.sendMessage("§a/pvp off [玩家名] §3强制关闭目标玩家的PVP");
				}
			}
			
			if(args[0].equalsIgnoreCase("state"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(playerData.get(p.getName())==false)
						sender.sendMessage("§a[PVP]§3 你的PVP为关闭状态");
					else
						sender.sendMessage("§a[PVP]§3 你的PVP为打开状态");
				}
				return true;
			}
			return true;
		}
		return false;
	}
}


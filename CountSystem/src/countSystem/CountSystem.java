package countSystem;

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

public class CountSystem extends JavaPlugin
{
	HashMap<String, HashMap<String, Integer>> playerData = new HashMap<String, HashMap<String, Integer>>();
	
	public void onEnable() 
	{
		if(!new File(getDataFolder(),"Data").exists()) 
		{
			new File(getDataFolder(),"Data").mkdirs();
		}
		loadConfig();
		getServer().getPluginManager().registerEvents(new CountSystemListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[CountSystem] §e统计系统已加载完毕");
	}

	public void onDisable() 
	{
		savePlayerConfig();
		Bukkit.getConsoleSender().sendMessage("§a[CountSystem] §e统计系统已卸载完毕");
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(), "/Data/" + "player.yml");
		FileConfiguration config;
		config = load(file);
		for(OfflinePlayer p:Bukkit.getServer().getOfflinePlayers())
		{
			if(config.contains(p.getName()))
			{
				int monster = config.getInt(p.getName()+"."+"Monster");
				int player = config.getInt(p.getName()+"."+"Player");
				HashMap<String, Integer> typeData = new HashMap<String, Integer>();
				typeData.put("Monster", monster);
				typeData.put("Player", player);
				playerData.put(p.getName(), typeData);
			}
		}
		
	}
	
	public void savePlayerConfig()
	{
		File file=new File(getDataFolder(), "/Data/" + "player.yml");
		FileConfiguration config;
		config = load(file);
		if(playerData.isEmpty())
			return;
		for(String name:playerData.keySet())
		{
			for(String type:playerData.get(name).keySet())
			{
				config.set(name+"."+type, playerData.get(name).get(type));
			}
			
		}
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("count"))
		{
			if (args.length==0)
			{
				sender.sendMessage("§a=========[统计系统]=========");
				sender.sendMessage("§a/count my §3查看我的数据统计");
				if(sender.isOp())
				{
					sender.sendMessage("§a/count reload §3重载配置");
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
						p.sendMessage("§e========================");
						p.sendMessage("§a杀死怪物的数量:§c " + playerData.get(p.getName()).get("Monster"));
						p.sendMessage("§a杀死玩家的数量:§c " + playerData.get(p.getName()).get("Player"));
						p.sendMessage("§e========================");
					}
				}
				
				return true;
			}
			
		}

		return false;
		
	}
	
	
}


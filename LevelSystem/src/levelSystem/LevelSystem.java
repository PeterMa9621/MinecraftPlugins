package levelSystem;

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

public class LevelSystem extends JavaPlugin
{
	HashMap<Integer, Integer> expFormat = new HashMap<Integer, Integer>();
	
	HashMap<String, PlayerData> player = new  HashMap<String, PlayerData>();
	
	API api = new API(this);
	
	int maxLevel = 1;
	public void onEnable() 
	{
		if(!getDataFolder().exists())
		{
			getDataFolder().mkdir();
		}
		loadConfig();
		loadPlayerConfig();
		getServer().getPluginManager().registerEvents(new LevelSystemListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[LevelSystem] §e等级系统加载完毕");
	}

	public void onDisable() 
	{
		savePlayerConfig();
		Bukkit.getConsoleSender().sendMessage("§a[LevelSystem] §e等级系统卸载完毕");
	}
	
	public API getAPI()
	{
		return api;
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"exp.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			config.set("MaxLevel", 20);
			
			for(int i=1; i<20; i++)
			{
				config.set("Exp."+i, i+10);
			}
			
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
		
		if(config.getInt("MaxLevel")!=0)
		{
			maxLevel = config.getInt("MaxLevel");
		}
		
		for(int i=1; config.contains("Exp."+i); i++)
		{
			expFormat.put(i, config.getInt("Exp."+i));
		}
		
	}
	
	public void loadPlayerConfig()
	{
		File file1=new File(getDataFolder(), "/Data");
		String[] fileName = file1.list();
		for(String name:fileName)
		{
			String playerName = name.substring(0, name.length()-4);
			File file=new File(getDataFolder(),"/Data/"+playerName+".yml");
			FileConfiguration config;
			
			if(file.exists())
			{
				config = load(file);
				
				int level = config.getInt("CurrentLevel");
				int currentExp = config.getInt("CurrentExp");
				int totalExp = config.getInt("TotalExp");
				
				PlayerData pd = new PlayerData(playerName, level, currentExp, totalExp);
				this.player.put(playerName, pd);
			}
		}
	}
	
	public void savePlayerConfig()
	{
		for(String name:this.player.keySet())
		{
			File file=new File(getDataFolder(),"/Data/"+name+".yml");
			FileConfiguration config;
			
			PlayerData pd = player.get(name);
			int level = pd.getLevel();
			int currentExp = pd.getCurrentExp();
			int totalExp = pd.getTotalExp();
			
			config = load(file);
			
			config.set("CurrentLevel", level);
			config.set("CurrentExp", currentExp);
			config.set("TotalExp", totalExp);
			
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
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("level"))
		{
			if(args.length==0)
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					PlayerData playerData = player.get(p.getName());
					int level = playerData.getLevel();
					int currentLevelExp = expFormat.get(level);
					int currentExp = playerData.getCurrentExp();
					sender.sendMessage("§a你的当前等级为:§5"+level+"§a,距离下一级还需:§5"+(currentLevelExp-currentExp)+"§a,总经验:§5"+playerData.getTotalExp());
					return true;
				}
				
			}
			
			if(args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage("§a=========[等级系统]=========");
				sender.sendMessage("§a/level §3查看当前等级");
				sender.sendMessage("§a/level help §3查看帮助");
				if(sender.isOp())
				{
					sender.sendMessage("§a/level clear [玩家名] §3清空该玩家总经验");
					sender.sendMessage("§a/level set [玩家名] [数量] §3设置玩家等级");
					sender.sendMessage("§a/level reload §3重读配置");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("clear"))
			{
				if(!sender.isOp())
					return true;
				if(args.length==2)
				{
					if(Bukkit.getPlayer(args[1])!=null)
					{
						player.get(args[1]).setTotalExp(0);
						sender.sendMessage("§6[等级系统] §a已清空玩家§5"+args[1]+"§a的总经验");
					}
					else
					{
						sender.sendMessage("§6[等级系统] §c玩家不存在或不在线");
					}
				}
				else
				{
					sender.sendMessage("§4用法：§a/level clear [玩家名] §3清空该玩家总经验");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(!sender.isOp())
					return true;
				loadConfig();
				sender.sendMessage("§6[等级系统] §a重读配置成功");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("set"))
			{
				if(!sender.isOp())
					return true;
				if(args.length==3)
				{
					if(Bukkit.getPlayer(args[1])!=null)
					{
						if(args[2].matches("[0-9]*"))
						{
							if(Integer.valueOf(args[2])<=maxLevel && Integer.valueOf(args[2])>0)
							{
								PlayerData pd = this.player.get(args[1]);
								pd.setLevel(Integer.valueOf(args[2]));
								pd.setCurrentExp(0);
								sender.sendMessage("§6[等级系统] §a已设置玩家§5"+args[1]+"§a等级为§e"+args[2]);
							}
							else
							{
								sender.sendMessage("§6[等级系统] §c数量必须是有效的等级");
							}
						}
						else
						{
							sender.sendMessage("§6[等级系统] §c等级必须是数字");
						}
					}
					else
					{
						sender.sendMessage("§6[等级系统] §c目标玩家不存在或不在线");
					}
				}
				else
				{
					sender.sendMessage("§4用法：§a/level setLevel [玩家名] [数量] §3设置玩家等级");
				}
			}
			return true;
		}
		return false;
	}
}


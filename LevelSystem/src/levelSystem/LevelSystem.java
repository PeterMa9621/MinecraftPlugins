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
		Bukkit.getConsoleSender().sendMessage("��a[LevelSystem] ��e�ȼ�ϵͳ�������");
	}

	public void onDisable() 
	{
		savePlayerConfig();
		Bukkit.getConsoleSender().sendMessage("��a[LevelSystem] ��e�ȼ�ϵͳж�����");
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
        { //�����ļ�������
        	try   //��׽�쳣����Ϊ�п��ܴ������ɹ�
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
					sender.sendMessage("��a��ĵ�ǰ�ȼ�Ϊ:��5"+level+"��a,������һ������:��5"+(currentLevelExp-currentExp)+"��a,�ܾ���:��5"+playerData.getTotalExp());
					return true;
				}
				
			}
			
			if(args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage("��a=========[�ȼ�ϵͳ]=========");
				sender.sendMessage("��a/level ��3�鿴��ǰ�ȼ�");
				sender.sendMessage("��a/level help ��3�鿴����");
				if(sender.isOp())
				{
					sender.sendMessage("��a/level clear [�����] ��3��ո�����ܾ���");
					sender.sendMessage("��a/level set [�����] [����] ��3������ҵȼ�");
					sender.sendMessage("��a/level reload ��3�ض�����");
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
						sender.sendMessage("��6[�ȼ�ϵͳ] ��a�������ҡ�5"+args[1]+"��a���ܾ���");
					}
					else
					{
						sender.sendMessage("��6[�ȼ�ϵͳ] ��c��Ҳ����ڻ�����");
					}
				}
				else
				{
					sender.sendMessage("��4�÷�����a/level clear [�����] ��3��ո�����ܾ���");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(!sender.isOp())
					return true;
				loadConfig();
				sender.sendMessage("��6[�ȼ�ϵͳ] ��a�ض����óɹ�");
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
								sender.sendMessage("��6[�ȼ�ϵͳ] ��a��������ҡ�5"+args[1]+"��a�ȼ�Ϊ��e"+args[2]);
							}
							else
							{
								sender.sendMessage("��6[�ȼ�ϵͳ] ��c������������Ч�ĵȼ�");
							}
						}
						else
						{
							sender.sendMessage("��6[�ȼ�ϵͳ] ��c�ȼ�����������");
						}
					}
					else
					{
						sender.sendMessage("��6[�ȼ�ϵͳ] ��cĿ����Ҳ����ڻ�����");
					}
				}
				else
				{
					sender.sendMessage("��4�÷�����a/level setLevel [�����] [����] ��3������ҵȼ�");
				}
			}
			return true;
		}
		return false;
	}
}


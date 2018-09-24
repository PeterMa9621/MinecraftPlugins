package maxHeart;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import levelSystem.LevelSystem;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MaxHeart extends JavaPlugin
{
	LevelSystem levelSystem;
	
	HashMap<Integer, Double> heart = new HashMap<Integer, Double>();
	
	HashMap<String, PlayerData> playerData = new HashMap<String, PlayerData>();
	
	String message = null;
	
	private boolean hookLevelSystem()
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("LevelSystem");
	    levelSystem = LevelSystem.class.cast(plugin);
	    return levelSystem != null; 
	}
	
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		if(hookLevelSystem()==false)
		{
			Bukkit.getConsoleSender().sendMessage("��a[MaxHeart] ��cLevelSystemδ����");
			return;
		}
		loadConfig();
		//loadPlayerConfig();
		getServer().getPluginManager().registerEvents(new MaxHeartListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[MaxHeart] ��cѪ�������Ѽ���!");
	}

	public void onDisable() 
	{
		//savePlayerConfig();
		Bukkit.getConsoleSender().sendMessage("��a[MaxHeart] ��cѪ��������ж��!");
	}
	
	/*
	public void loadPlayerConfig()
	{
		File file1=new File(getDataFolder(), "/Data");
		String[] fileName = file1.list();
		for(String name:fileName)
		{
			String playerName = name.substring(0, name.length()-4);
			File file=new File(getDataFolder(),"/Data/"+playerName+".yml");
			FileConfiguration config;
			PlayerData pd;
			if(file.exists())
			{
				config = load(file);
				
				pd = new PlayerData(config.getDouble("ExtraHeart"));
			}
			else
			{
				pd = new PlayerData(0);
			}
			
			playerData.put(playerName, pd);
		}
	}
	
	public void savePlayerConfig()
	{
		for(String playerName:playerData.keySet())
		{
			File file=new File(getDataFolder(),"/Data/"+playerName+".yml");
			FileConfiguration config;
			config = load(file);
			
			config.set("ExtraHeart", playerData.get(playerName).getExtraHeart());
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	*/
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			config.set("Heart", "30:1,50:1,70:1,90:1");
			
			config.set("Message", "��a[Ѫ������] ��6���ѻ�á�51��6������������ֵ����");
			
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
		
		heart.clear();
		
		config = load(file);
		
		String heart = config.getString("Heart");
		
		message = config.getString("Message");
		
		for(String info:heart.split(","))
		{
			int level = Integer.valueOf(info.split(":")[0]);
			Double extraHeart = Double.valueOf(info.split(":")[1]);
			this.heart.put(level, extraHeart);
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
		if (cmd.getName().equalsIgnoreCase("mh"))
		{
			if (args.length==0)
			{
				if(sender.isOp())
				{
					sender.sendMessage("��a=========[Ѫ������]=========");
					sender.sendMessage("��a/mh test ��3�鿴��ǰPVP����״̬");
					sender.sendMessage("��a/mh reset [�����] ��3����Ŀ����ҵ�Ѫ��");
					sender.sendMessage("��a/mh set [�����] [Ѫ������] ��3����Ŀ����ҵ�Ѫ��");
					sender.sendMessage("��a/mh reload ��3�ض�����");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					loadConfig();
					sender.sendMessage("��6[Ѫ������] ��a���سɹ���");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("set"))
			{
				if(sender.isOp())
				{
					if(args.length==3)
					{
						if(Bukkit.getPlayer(args[1])!=null)
						{
							if(args[2].matches("[0-9]*"))
							{
								Bukkit.getPlayer(args[1]).setMaxHealth(Double.valueOf(args[2]));
								sender.sendMessage("��6[Ѫ������] ��a��������ҡ�5"+args[1]+"��a��Ѫ������Ϊ��5"+args[2]);
							}
						}
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reset"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						if(Bukkit.getPlayer(args[1])!=null)
						{
							Bukkit.getPlayer(args[1]).setMaxHealth(20);
							sender.sendMessage("��6[Ѫ������] ��a��������ҡ�5"+args[1]+"��a��Ѫ������");
						}
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("test"))
			{
				if(sender instanceof Player && sender.isOp())
				{
					Player p = (Player)sender;
					p.setMaxHealth(p.getMaxHealth()+1.0);
					sender.sendMessage("��6[Ѫ������] ��a����������1��Ѫ������");
				}
				return true;
			}
		}
		return false;
	}
}


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
		Bukkit.getConsoleSender().sendMessage("��a[CountSystem] ��eͳ��ϵͳ�Ѽ������");
	}

	public void onDisable() 
	{
		savePlayerConfig();
		Bukkit.getConsoleSender().sendMessage("��a[CountSystem] ��eͳ��ϵͳ��ж�����");
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
		if (cmd.getName().equalsIgnoreCase("count"))
		{
			if (args.length==0)
			{
				sender.sendMessage("��a=========[ͳ��ϵͳ]=========");
				sender.sendMessage("��a/count my ��3�鿴�ҵ�����ͳ��");
				if(sender.isOp())
				{
					sender.sendMessage("��a/count reload ��3��������");
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
						p.sendMessage("��e========================");
						p.sendMessage("��aɱ�����������:��c " + playerData.get(p.getName()).get("Monster"));
						p.sendMessage("��aɱ����ҵ�����:��c " + playerData.get(p.getName()).get("Player"));
						p.sendMessage("��e========================");
					}
				}
				
				return true;
			}
			
		}

		return false;
		
	}
	
	
}


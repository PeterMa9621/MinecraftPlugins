package eazyRepair;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class EazyRepair extends JavaPlugin
{

	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdirs();
		}
		getServer().getPluginManager().registerEvents(new EazyRepairListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[EazyRepair] ��e���޸�ϵͳ�������");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("��a[EazyRepair] ��e���޸�ϵͳ�������");
	}

	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		
		if(!file.exists())
		{
			config = load(file);
			
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
		if (cmd.getName().equalsIgnoreCase("mff"))
		{
			if (args.length==0)
			{
				if(sender.isOp())
					sender.sendMessage("��9MoreFish�޸����");
				return true;
			}
			return true;
		}
		return false;
	}
}


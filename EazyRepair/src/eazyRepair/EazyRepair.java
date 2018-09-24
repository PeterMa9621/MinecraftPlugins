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
		Bukkit.getConsoleSender().sendMessage("§a[EazyRepair] §e简单修复系统加载完毕");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[EazyRepair] §e简单修复系统加载完毕");
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
		if (cmd.getName().equalsIgnoreCase("mff"))
		{
			if (args.length==0)
			{
				if(sender.isOp())
					sender.sendMessage("§9MoreFish修复插件");
				return true;
			}
			return true;
		}
		return false;
	}
}


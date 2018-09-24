package levelSystem;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager extends JavaPlugin
{
	LevelSystem plugin;
	
	public ConfigManager(LevelSystem plugin)
	{
		this.plugin=plugin;
		loadConfig();
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"exp.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
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
		
		for(int i=1; config.contains("Exp."+i); i++)
		{
			plugin.expFormat.put(i, config.getInt("Exp."+i));
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

}
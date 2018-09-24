package commandBlock;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class Config extends CommandBlock
{
	public Config(String name)
	{
		File file=new File(CommandBlock.path+"/"+name);
		if(!new File(file.getParent()).exists())
		{
			file.getParentFile().mkdirs();
		}
		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			config=YamlConfiguration.loadConfiguration(file);
			load();
		}
	}
	
	public FileConfiguration getConfig()
	{
		
	}
}

package commandDeny;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CommandDeny extends JavaPlugin
{
	ArrayList<String> command = new ArrayList<String>();
	
	String message = "";
	
	public void onEnable() 
	{
		if(!getDataFolder().exists())
		{
			getDataFolder().mkdir();
		}
		loadConfig();
		getServer().getPluginManager().registerEvents(new CommandDenyListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[CommandDeny] §e命令禁止系统已启动!");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[CommandDeny] §e命令禁止系统已卸载!");
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(), "config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			String[] command = {"clan lookup"};
			
			config.set("Command.Content", command);
			
			config.set("Command.DenyMessage", "§f未知命令. 输入 \"/help\" 查看帮助.");
			
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
		
		message = config.getString("Command.DenyMessage");
		
		for(String c:config.getStringList("Command.Content"))
		{
			command.add("/"+c);
		}
	}

	public void saveConfig()
	{
		File file=new File(getDataFolder(), "config.yml");
		FileConfiguration config;

		config = load(file);
		
		String[] command = {};
		
		int i = 0;
		for(String c:this.command)
		{
			c = c.split("/")[1];
			command[i]=c;
			i++;
		}
		
		config.set("Command.Content", command);
		
		try
		{
			config.save(file);
		} 
		catch (IOException e)
		{
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
		if (cmd.getName().equalsIgnoreCase("cd"))
		{
			if(!sender.isOp())
			{
				sender.sendMessage(message);
				return true;
			}
			
			if (args.length==0)
			{
				sender.sendMessage("§9/cd deny [命令] §6- 禁止某个命令");
				sender.sendMessage("§9/cd remove [命令] §6- 移除禁止的某个命令");
				sender.sendMessage("§9/cd reload §6- 重读配置");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("deny"))
			{
				if(args.length==2)
				{
					String command = args[1];
					if(command.contains(":"))
						command.replace(":", " ");
					if(!this.command.contains("/"+command))
					{
						this.command.add("/"+command);
						sender.sendMessage("§a禁止命令§c"+command+"成功");
					}
					else
					{
						sender.sendMessage("§c禁止列表内已经存在该命令");
					}
				}
				else
				{
					sender.sendMessage("§9/cd deny [命令] §6- 禁止某个命令");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("remove"))
			{
				if(args.length==2)
				{
					String command = args[1];
					if(command.contains(":"))
						command.replace(":", " ");
					if(this.command.contains("/"+command))
					{
						this.command.remove("/"+command);
						sender.sendMessage("§a移除命令§c"+command+"成功");
					}
					else
					{
						sender.sendMessage("§c禁止列表内不存在该命令");
					}
				}
				else
				{
					sender.sendMessage("§9/cd remove [命令] §6- 移除禁止的某个命令");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				loadConfig();
				sender.sendMessage("§6重读配置成功!");
				return true;
			}
			return true;
		}

		return false;
		
	}
	
	
}


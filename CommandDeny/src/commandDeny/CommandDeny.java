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
		Bukkit.getConsoleSender().sendMessage("��a[CommandDeny] ��e�����ֹϵͳ������!");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("��a[CommandDeny] ��e�����ֹϵͳ��ж��!");
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
			
			config.set("Command.DenyMessage", "��fδ֪����. ���� \"/help\" �鿴����.");
			
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
		if (cmd.getName().equalsIgnoreCase("cd"))
		{
			if(!sender.isOp())
			{
				sender.sendMessage(message);
				return true;
			}
			
			if (args.length==0)
			{
				sender.sendMessage("��9/cd deny [����] ��6- ��ֹĳ������");
				sender.sendMessage("��9/cd remove [����] ��6- �Ƴ���ֹ��ĳ������");
				sender.sendMessage("��9/cd reload ��6- �ض�����");
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
						sender.sendMessage("��a��ֹ�����c"+command+"�ɹ�");
					}
					else
					{
						sender.sendMessage("��c��ֹ�б����Ѿ����ڸ�����");
					}
				}
				else
				{
					sender.sendMessage("��9/cd deny [����] ��6- ��ֹĳ������");
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
						sender.sendMessage("��a�Ƴ������c"+command+"�ɹ�");
					}
					else
					{
						sender.sendMessage("��c��ֹ�б��ڲ����ڸ�����");
					}
				}
				else
				{
					sender.sendMessage("��9/cd remove [����] ��6- �Ƴ���ֹ��ĳ������");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				loadConfig();
				sender.sendMessage("��6�ض����óɹ�!");
				return true;
			}
			return true;
		}

		return false;
		
	}
	
	
}


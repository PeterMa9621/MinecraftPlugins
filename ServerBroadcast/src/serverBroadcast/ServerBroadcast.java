package serverBroadcast;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ServerBroadcast extends JavaPlugin
{
	private String[] m = null;
	private String pre = "";
	private int t = 0;
	private int length = 0;
	private int cd = 0;
	
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		loadMessage();
		broadCast();
		Bukkit.getConsoleSender().sendMessage("§a[ServerBroadcast] §e广播系统加载完毕");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[ServerBroadcast] §e广播系统卸载完毕");
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
	
	public void loadMessage()
	{
		File file=new File(getDataFolder(),"message.yml");
		FileConfiguration message;
		if (!file.exists())
		{
			message = load(file);
			message.set("Prefix", "&a[Server]");
			message.set("Message.1", "&cTest1");
			message.set("Message.2", "&dTest2");
			message.set("Length", 2);
			message.set("Time", 20);
			try 
			{
				message.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			pre = "[Server]";
			m[0] = "Test1";
			m[1] = "Test2";
			length = 2;
			t = 20;
			return;
		}
		message = load(file);
		pre = message.getString("Prefix");
		length = message.getInt("Length");
		t = message.getInt("Time");
		m = new String[length];
		for(int i=0; i<length; i++)
		{
			m[i] = message.getString("Message."+(i+1));
		}
		return;
		
	}
	
	public void broadCast()
	{
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			
			public void run()
			{
				if (m!=null)
				{
					String prefix=pre.replaceAll("&", "§");
					String message=m[cd].replaceAll("&", "§");
					Bukkit.broadcastMessage(prefix+" §f"+message);
					if (cd == m.length-1)
					{
						cd=0;
					}
					else
					{
						cd+=1;
					}
				}
			}
		} ,5*20,t*20);
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		File file=new File(getDataFolder(),"message.yml");
		FileConfiguration message = load(file);
		if (cmd.getName().equalsIgnoreCase("bc"))
		{
			if (args.length==0)
			{
				sender.sendMessage("§a=========[系统公告]=========");
				sender.sendMessage("§a/bc setprefix [前缀] 设置公告前缀");
				sender.sendMessage("§a/bc settime [时间(秒)] 设置公告时长");
				sender.sendMessage("§a/bc add [信息] 添加一个公告");
				sender.sendMessage("§a/bc remove [编号] 删除一个公告");
				sender.sendMessage("§a/bc list 列出所有公告");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("setprefix"))
			{
				if (args.length == 2)
				{
					message.set("Prefix", args[1]);
					pre = args[1].replaceAll("&", "§");
					sender.sendMessage("§a设置成功");
				}
				else
				{
					sender.sendMessage("§a/bc setprefix [前缀]");
				}
			}
			
			if (args[0].equalsIgnoreCase("settime"))
			{
				if (args.length == 2)
				{
					if (args[1].matches("[0-9]*"))
					{
						message.set("Time", Integer.parseInt(args[1]));
						t =Integer.parseInt(args[1]);
						sender.sendMessage("§a设置成功,需要重载插件");
					}
					else
					{
						sender.sendMessage("§1时间只能为数字");
					}
				}
				else
				{
					sender.sendMessage("§a/bc settime [时间(秒)]");
				}
			}
			
			if (args[0].equalsIgnoreCase("add"))
			{
				if (args.length == 2)
				{
					length+=1;
					message.set("Length", length);
					message.set("Message."+length, args[1]);
					m = new String[length];
					for(int i=0; i<length; i++)
					{
						m[i] = message.getString("Message."+(i+1));
					}
					sender.sendMessage("§a添加成功");
				}
				else
				{
					sender.sendMessage("§a/bc add [信息]");
				}
			}
			
			if (args[0].equalsIgnoreCase("remove"))
			{
				if (args.length == 2)
				{
					if (args[1].matches("[0-9]*"))
					{
						int number = Integer.parseInt(args[1]);
						if (number<=length || number>=0)
						{
							for(int i=number+1; i<=length; i++)
							{
								String currentMessage = message.getString("Message."+i);
								message.set("Message."+(i-1), currentMessage);
							}
							message.set("Message."+length, null);
							length-=1;
						}
					}
					message.set("Length", length);
					sender.sendMessage("§a删除成功");
				}
				else
				{
					sender.sendMessage("§a/bc remove [编号]");
				}
			}
			
			if (args[0].equalsIgnoreCase("list"))
			{
				sender.sendMessage("§5公告列表:");
				for(int i=0; i<length; i++)
				{
					String currentMessage = message.getString("Message."+(i+1));
					sender.sendMessage("§a编号§4"+(i+1)+"§a: §d"+currentMessage);
				}
			}
			
			try 
			{
				message.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			return true;
		}

		return false;
		
	}
	
	
}


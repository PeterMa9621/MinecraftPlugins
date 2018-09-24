package multiHome;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MultiHome extends JavaPlugin
{
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		File file=new File(getDataFolder(),"config.yml");
		if (!(file.exists())) 
		{
			saveDefaultConfig();
		}
		reloadConfig();
        getLogger().info("Finish loading");
	}

	public void onDisable() 
	{
		getLogger().info("Unload");
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
			catch(IOException 		e)
			{
				e.printStackTrace();
			}
		}
		return YamlConfiguration.loadConfiguration(new File(path));
	}
	
	public boolean createHome(Player player, String name, int Maximum)
	{
		int number = 0;
		Location l = player.getLocation();
		String path="homes/"+player.getName()+".yml";
		File file = new File(getDataFolder() ,path);
		FileConfiguration config = load(file);
		if (config.contains("Homes.number"))
		{
			number = config.getInt("Homes.number");
		}
		else
		{
			config.set("Homes.number", 0);
		}
		if (number < Maximum)
		{
			config.set("Homes."+name+".World", l.getWorld().getName());
			config.set("Homes."+name+".X", l.getBlockX());
			config.set("Homes."+name+".Y", l.getBlockY());
			config.set("Homes."+name+".Z", l.getBlockZ());
			config.set("Homes."+name+".Direction.X", l.getDirection().getX());
			config.set("Homes."+name+".Direction.Y", l.getDirection().getY());
			config.set("Homes."+name+".Direction.Z", l.getDirection().getZ());
			config.set("Homes.number", number+1);
			try
			{
				config.save(file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	public Location getHome(Player player, String name)
	{
		String path="homes/"+player.getName()+".yml";
		File file = new File(getDataFolder() ,path);
		FileConfiguration config = load(file);
		if (config.contains("Homes."+name))
		{
			String world = config.getString("Homes."+name+".World");
			int x = config.getInt("Homes."+name+".X");
			int y = config.getInt("Homes."+name+".Y");
			int z = config.getInt("Homes."+name+".Z");
			double DirectionX = config.getDouble("Homes."+name+".Direction.X");
			double DirectionY = config.getDouble("Homes."+name+".Direction.Y");
			double DirectionZ = config.getDouble("Homes."+name+".Direction.Z");
			Vector v = new Vector(DirectionX, DirectionY, DirectionZ);
			Location l = new Location(Bukkit.getWorld(world), x, y, z);
			l.setDirection(v);
			return l;
		}
		return null;
	}

	public boolean removeHome(Player player, String name)
	{
		String path="homes/"+player.getName()+".yml";
		File file = new File(getDataFolder() ,path);
		FileConfiguration config = load(file);
		if (config.contains("Homes."+name))
		{
			config.set("Homes."+name, null);
			try
			{
				config.save(file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("mh"))
		{
			if (args.length==0)
			{
				sender.sendMessage("��a=======[���ö����]=======");
				sender.sendMessage("��a/mh sethome [����] ���ü�");
				sender.sendMessage("��a/mh home [����] �ؼ�");
				sender.sendMessage("��a/mh delete [����] ɾ����");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("sethome"))
			{
				int max = 2;
				if (args.length == 2)
				{
					if (sender instanceof Player)
					{
						Player player = (Player)sender;
						if (this.createHome(player, args[1], max))
						{
							player.sendMessage("���ü� ��a"+args[1]+" ��f�ɹ�");
						}
						else
						{
							player.sendMessage("���ü������ﵽ������� ��a"+ max);
						}
					}
				}
				else
				{
					sender.sendMessage("/mh sethome [����]");
				}
			}
			
			if (args[0].equalsIgnoreCase("home"))
			{
				if (args.length == 2)
				{
					if (sender instanceof Player)
					{
						Player player = (Player)sender;
						Location l = this.getHome(player, args[1]);
						if (l != null)
						{
							player.teleport(l);
							player.sendMessage("��a���ڴ���...");
						}
						else
						{
							player.sendMessage("û���ҵ��� ��a"+args[1]+" ��fΪ���ļ�");
						}
					}
				}
				else
				{
					sender.sendMessage("/mh sethome [����]");
				}
			}
			
			if (args[0].equalsIgnoreCase("delete"))
			{
				if (args.length == 2)
				{
					if (sender instanceof Player)
					{
						Player player = (Player)sender;
						if (this.removeHome(player, args[1]))
						{
							player.sendMessage("ɾ���� ��a"+ args[1] +"��f�ɹ�");
						}
						else
						{
							player.sendMessage("û���ҵ��� ��a"+ args[1]);
						}
					}
				}
				else
				{
					sender.sendMessage("/mh delete [����]");
				}
			}
			return true;
		}

		return false;
		
	}
	
	
}


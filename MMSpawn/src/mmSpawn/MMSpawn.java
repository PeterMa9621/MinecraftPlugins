package mmSpawn;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.onarandombox.MultiverseCore.MultiverseCore;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MMSpawn extends JavaPlugin
{
	MythicMobs mm;
	MultiverseCore core;
	
	HashMap<String, MMobInfo> mobData = new HashMap<String, MMobInfo>();
	
	HashMap<String, String> timePlan = new HashMap<String, String>();
	
	SimpleDateFormat date = new SimpleDateFormat("HH-mm");
	
	private boolean hookMythicMobs()
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("MythicMobs");
	    mm = MythicMobs.class.cast(plugin);
	    return mm != null; 
	}
	
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		if(hookMythicMobs()==false)
		{
			Bukkit.getConsoleSender().sendMessage("��a[MMSpawn] ��cMythicMobsδ����");
			return;
		}
		//getServer().getPluginManager().registerEvents(new MMSpawnListener(this), this);
		loadConfig();
		readyToSpawn();
		Bukkit.getConsoleSender().sendMessage(Bukkit.getWorlds().toString());
		Bukkit.getConsoleSender().sendMessage("��a[MMSpawn] ��cMMSpawn�Ѽ���");
	}

	public void onDisable() 
	{
		saveConfig();
		Bukkit.getConsoleSender().sendMessage("��a[MMSpawn] ��cMMSpawn��ж��");
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if(file.exists())
		{
			config = load(file);
			for(String name:config.getRoot().getKeys(false))
			{
				String time = config.getString(name+".Time");
				String hour = time.split("-")[0];
				String minute = time.split("-")[1];
				String world = config.getString(name+".Location.World");
				int x = config.getInt(name+".Location.X");
				int y = config.getInt(name+".Location.Y");
				int z = config.getInt(name+".Location.Z");
				
				Location l = new Location(Bukkit.getWorld(world), x, y, z);
				
				MMobInfo mmInfo = new MMobInfo(hour, minute, l);
				timePlan.put(time, name);
				mobData.put(name, mmInfo);
			}
		}
	}
	
	public void saveConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (file.exists())
			file.delete();
		config = load(file);
		
		for(String name:mobData.keySet())
		{
			Location l = mobData.get(name).getLocation();
			config.set(name+".Time", mobData.get(name).getHour()+"-"+mobData.get(name).getMinute());
			config.set(name+".Location.World", l.getWorld().getName());
			config.set(name+".Location.X", l.getBlockX());
			config.set(name+".Location.Y", l.getBlockY());
			config.set(name+".Location.Z", l.getBlockZ());
		}

		try 
		{
			config.save(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return;
	}
	
	public void readyToSpawn()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				String currentTime = date.format(new Date());
				if(timePlan.containsKey(currentTime))
				{
					String mobName = timePlan.get(currentTime);
					if(mobData.containsKey(mobName))
					{
						MMobInfo mmobInfo = mobData.get(mobName);
						if(!mmobInfo.isSpawn())
						{
							Location l = mmobInfo.getLocation();
							
							try {
								mm.getAPIHelper().spawnMythicMob(mobName, l);
							} catch (InvalidMobTypeException e) {
								e.printStackTrace();
							}
							
							mmobInfo.setSpawn(true);
						}
					}
				}
			}
		}.runTaskTimer(this, 0L, 300L);
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
		if (cmd.getName().equalsIgnoreCase("mms"))
		{
			if (args.length==0)
			{
				if(sender.isOp())
				{
					sender.sendMessage("��a=========[MM����ϵͳ]=========");
					sender.sendMessage("��a/mms spawn [��������] ��3�ڵ�ǰλ������һ������");
					sender.sendMessage("��a/mms set [��������] [ʱ��] ��3�ڵ�ǰλ������һ����ʱ����ʱ���ʽΪ(00-00)");
					sender.sendMessage("��a/mms reload ��3��������");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					this.loadConfig();
					sender.sendMessage("��e�������سɹ���");
				}
			}
			
			if(args[0].equalsIgnoreCase("set"))
			{
				if(sender.isOp() && sender instanceof Player)
				{
					if(args.length==3)
					{
						Player p = (Player)sender;
						if(mm.getAPIHelper().getMythicMob(args[1])!=null)
						{
							if(mobData.containsKey(args[1]))
							{
								p.sendMessage("��cĿǰ�Ѱ����ù���");
								return true;
							}
							if(args[2].contains("-"))
							{
								if(args[2].split("-")[0].matches("[0-9]*") && 
										args[2].split("-")[1].matches("[0-9]*"))
								{
									String hour = String.format("%02d", Integer.valueOf(args[2].split("-")[0]));
									String minute = String.format("%02d", Integer.valueOf(args[2].split("-")[1]));
									String time = hour+"-"+minute;
									if(timePlan.containsKey(time))
									{
										p.sendMessage("��c��ʱ���Ѱ���һ���������ɣ�");
										return true;
									}
									timePlan.put(time, args[1]);
									Location l = p.getLocation();
									MMobInfo mmInfo = new MMobInfo(hour, minute, l);
									mobData.put(args[1], mmInfo);
									p.sendMessage("��6��ӳɹ�������"+args[1]+"����"+hour+"��"+minute+"��ʱ���ڸõس���");
								}
								else
								{
									p.sendMessage("��cʱ�䲻��һ�����֣�");
								}
							}
							else
							{
								p.sendMessage("��cʱ���ʽ����ȷ��ʱ���ʽΪ(00-00)");
							}
						}
						else
						{
							p.sendMessage("��cû����������ֵĹ��");
						}
					}
					else
					{
						sender.sendMessage("��4�÷�����a/mms set [��������] [ʱ��] ��3�ڵ�ǰλ������һ����ʱ����ʱ���ʽΪ(00-00)");
					}
				}
			}
			
			if(args[0].equalsIgnoreCase("spawn"))
			{
				if(sender.isOp() && sender instanceof Player)
				{
					if(args.length==2)
					{
						Player p = (Player)sender;
						if(mm.getAPIHelper().getMythicMob(args[1])!=null)
						{
							try {
								mm.getAPIHelper().spawnMythicMob(args[1], p.getLocation());
							} catch (InvalidMobTypeException e) {
								e.printStackTrace();
							}
						}
						else
						{
							p.sendMessage("��cû����������ֵĹ��");
						}
					}
					else
					{
						sender.sendMessage("��4�÷�����a/mms spawn [��������] ��3�ڵ�ǰλ������һ������");
					}
				}
			}
			return true;
		}

		return false;
		
	}
	
	
}


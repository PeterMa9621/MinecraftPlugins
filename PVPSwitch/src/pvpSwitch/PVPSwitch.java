package pvpSwitch;

import org.bukkit.plugin.Plugin;
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

public class PVPSwitch extends JavaPlugin
{
	HashMap<String, Boolean> playerData = new HashMap<String, Boolean>();
	HashMap<String, Boolean> bannedPlayer = new HashMap<String, Boolean>();
	
	private PVPSwitchAPI api = new PVPSwitchAPI(this);
	
	public void onEnable() 
	{
		if(!new File(getDataFolder(),"Data").exists()) 
		{
			new File(getDataFolder(),"Data").mkdirs();
		}
		if(!new File(getDataFolder(),"Player").exists()) 
		{
			new File(getDataFolder(),"Player").mkdirs();
		}
		
		loadConfig();
		getServer().getPluginManager().registerEvents(new PVPSwitchListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[PVPSwitch] ��ePVPϵͳ�������");
	}

	public void onDisable() 
	{
		saveConfig();
		Bukkit.getConsoleSender().sendMessage("��a[PVPSwitch] ��ePVPϵͳж�����");
	}
	
	public void saveConfig()
	{
		// save the player's pvp state
		for(String playerName:playerData.keySet())
		{
			File file=new File(getDataFolder(),"/Player/"+playerName+".yml");
			FileConfiguration config;
			config = load(file);
			
			config.set("PVP.onUse", playerData.get(playerName));
			
			try 
			{
				config.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		// save the banned player
		File file=new File(getDataFolder(),"/Data/ban.yml");

		FileConfiguration config;
		config = load(file);

		config.set("bannedPlayer", bannedPlayer);
		
		try 
		{
			config.save(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void loadConfig()
	{
		for(OfflinePlayer p:Bukkit.getOfflinePlayers())
		{
			File file=new File(getDataFolder(), "/Player/"+p.getName()+".yml");
			FileConfiguration config;
			
			config = load(file);
			
			playerData.put(p.getName(), config.getBoolean("PVP.onUse"));
			bannedPlayer.put(p.getName(), false);
		}
		
		File file=new File(getDataFolder(), "/Data/ban.yml");
		FileConfiguration config;
		
		config = load(file);
		for(String name:config.getStringList("bannedPlayer"))
		{
			bannedPlayer.put(name, true);
		}
	}
	
	public PVPSwitchAPI getAPI()
	{
		return api;
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
		if (cmd.getName().equalsIgnoreCase("pvp"))
		{
			if (args.length==0)
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(bannedPlayer.get(p.getName())==false)
					{
						if(playerData.get(p.getName())==false)
						{
							playerData.put(p.getName(), true);
							p.sendMessage("��a[PVP]��3 PVP�Ѵ�");
						}
						else
						{
							playerData.put(p.getName(), false);
							p.sendMessage("��a[PVP]��3 PVP�ѹر�");
						}
					}
					else
					{
						p.sendMessage("��a[PVP]��c �����ڲ����л�PVP����!");
					}
					

				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage("��a=========��6[PVPϵͳ]��a=========");
				sender.sendMessage("��a/pvp state ��3�鿴��ǰPVP����״̬");
				sender.sendMessage("��a/pvp help ��3�鿴����");
				if(sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("��a/pvp on [�����] ��3ǿ�ƴ�Ŀ����ҵ�PVP");
					sender.sendMessage("��a/pvp off [�����] ��3ǿ�ƹر�Ŀ����ҵ�PVP");
					sender.sendMessage("��a/pvp ban [�����] ��3��ֹĿ������л�PVP״̬");
					sender.sendMessage("��a/pvp unban [�����] ��3����Ŀ������л�PVP״̬");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("ban"))
			{
				if(!sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("��a[PVP]��c ��û��Ȩ��");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("��a[PVP]��c ����Ҳ����ڻ�����");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);
					
					bannedPlayer.put(p.getName(), true);
					
					sender.sendMessage("��a[PVP]��3 �ѽ�ֹ��ҡ�c"+p.getName()+"��3�л�PVP");
					p.sendMessage("��a[PVP]��c ע�⣺�������ѱ���ֹ�л�PVP״̬");
					return true;
				}
				else
				{
					sender.sendMessage("��a/pvp ban [�����] ��3��ֹĿ������л�PVP״̬");
				}
			}
			
			if(args[0].equalsIgnoreCase("unban"))
			{
				if(!sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("��a[PVP]��c ��û��Ȩ��");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("��a[PVP]��c ����Ҳ����ڻ�����");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);
					
					bannedPlayer.put(p.getName(), false);
					
					sender.sendMessage("��a[PVP]��3 ��������ҡ�c"+p.getName()+"��3�л�PVP");
					p.sendMessage("��a[PVP]��c ע�⣺�������ѱ������л�PVP״̬");
					return true;
				}
				else
				{
					sender.sendMessage("��a/pvp unban [�����] ��3����Ŀ������л�PVP״̬");
				}
			}
			
			if(args[0].equalsIgnoreCase("on"))
			{
				if(!sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("��a[PVP]��c ��û��Ȩ��");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("��a[PVP]��c ����Ҳ����ڻ�����");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);
					
					playerData.put(p.getName(), true);

					sender.sendMessage("��a[PVP]��3 ��ǿ�ƴ���ҡ�c"+p.getName()+"��3��PVP");
					p.sendMessage("��a[PVP]��c ע�⣺���PVP�����ѱ�ǿ�ƴ�");
					return true;
				}
				else
				{
					sender.sendMessage("��a/pvp on [�����] ��3ǿ�ƴ�Ŀ����ҵ�PVP");
				}
			}
			
			if(args[0].equalsIgnoreCase("off"))
			{
				if(!sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("��a[PVP]��c ��û��Ȩ��");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("��a[PVP]��c ����Ҳ����ڻ�����");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);
					
					playerData.put(p.getName(), false);

					sender.sendMessage("��a[PVP]��3 ��ǿ�ƹر���ҡ�c"+p.getName()+"��3��PVP");
					p.sendMessage("��a[PVP]��c ע�⣺���PVP�����ѱ�ǿ�ƹر�");
					return true;
				}
				else
				{
					sender.sendMessage("��a/pvp off [�����] ��3ǿ�ƹر�Ŀ����ҵ�PVP");
				}
			}
			
			if(args[0].equalsIgnoreCase("state"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(playerData.get(p.getName())==false)
						sender.sendMessage("��a[PVP]��3 ���PVPΪ�ر�״̬");
					else
						sender.sendMessage("��a[PVP]��3 ���PVPΪ��״̬");
				}
				return true;
			}
			return true;
		}
		return false;
	}
}


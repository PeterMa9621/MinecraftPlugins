package pvpSwitch;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import peterUtil.database.Database;
import peterUtil.database.DatabaseType;
import peterUtil.database.StorageInterface;
import pvpSwitch.expansion.PVPSwitchExpansion;
import pvpSwitch.model.PvpPlayer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PVPSwitch extends JavaPlugin
{
	public HashMap<UUID, PvpPlayer> playerData = new HashMap<>();

	DatabaseType databaseType;
	StorageInterface database;
	private String databaseName;

	private PVPSwitchAPI api = new PVPSwitchAPI(this);
	
	public void onEnable() 
	{
		if(!new File(getDataFolder(),"Data").exists()) 
		{
			new File(getDataFolder(),"Data").mkdirs();
		}

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new PVPSwitchExpansion(this).register();
		}

		loadConfig();
		String createTableQuery = "create table if not exists pvp_switch(id varchar(100), can_pvp tinyint, is_banned tinyint, primary key(id));";
		database = Database.getInstance(databaseType, this);
		database.connect(databaseName, "pvp_switch", "root", "mjy159357", createTableQuery);
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
		for(UUID uniqueId:playerData.keySet()){
			PvpPlayer pvpPlayer = playerData.get(uniqueId);
			HashMap<String, Object> data = new HashMap<String, Object>() {{
				put("can_pvp", pvpPlayer.canPvp());
				put("is_banned", pvpPlayer.isBanned());
			}};
			try {
				database.store(uniqueId, data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadConfig() {
		File file=new File(getDataFolder(), "config.yml");
		FileConfiguration config;

		if(!file.exists()){
			config = load(file);
			config.set("Database", "YML");
			config.set("DatabaseName", "minecraft");

			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}

			loadConfig();
			return;
		}
		config = load(file);
		databaseType = DatabaseType.valueOf(config.getString("Database", "YML"));
		databaseName = config.getString("DatabaseName", "minecraft");
	}

	public PvpPlayer loadPlayerConfig(UUID uniqueId) {
		if(playerData.containsKey(uniqueId)){
			return playerData.get(uniqueId);
		}

		HashMap<String, Object> result = database.get(uniqueId, new String[] {"can_pvp", "is_banned"});

		if(result==null){
			PvpPlayer pvpPlayer = new PvpPlayer(uniqueId, false, false);
			playerData.put(uniqueId, pvpPlayer);
			return pvpPlayer;
		}

		Boolean can_pvp = ((Integer)result.get("can_pvp")) == 1;

		Boolean is_banned = ((Integer)result.get("is_banned")) == 1;

		PvpPlayer pvpPlayer = new PvpPlayer(uniqueId, can_pvp, is_banned);
		playerData.put(uniqueId, pvpPlayer);
		return pvpPlayer;
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
					PvpPlayer pvpPlayer = playerData.get(p.getUniqueId());
					if(!pvpPlayer.isBanned())
					{
						pvpPlayer.setPvp(!pvpPlayer.canPvp());
						if(pvpPlayer.canPvp()) {
							p.sendMessage("��6[PVP]��3 PVP�Ѵ�");
						}
						else {
							p.sendMessage("��6[PVP]��3 PVP�ѹر�");
						}
					}
					else {
						p.sendMessage("��6[PVP]��c �����ڲ����л�PVP����!");
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage("��a=========��6[PVPϵͳ]��a=========");
				sender.sendMessage("��6/pvp state ��3�鿴��ǰPVP����״̬");
				sender.sendMessage("��6/pvp help ��3�鿴����");
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
					sender.sendMessage("��6[PVP]��c ��û��Ȩ��");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("��6[PVP]��c ����Ҳ����ڻ�����");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);

					PvpPlayer pvpPlayer = playerData.get(p.getUniqueId());
					pvpPlayer.setBanned(true);
					playerData.put(p.getUniqueId(), pvpPlayer);
					
					sender.sendMessage("��6[PVP]��3 �ѽ�ֹ��ҡ�c"+p.getName()+"��3�л�PVP");
					p.sendMessage("��6[PVP]��c ע�⣺�������ѱ���ֹ�л�PVP״̬");
					return true;
				}
				else
				{
					sender.sendMessage("��6/pvp ban [�����] ��3��ֹĿ������л�PVP״̬");
				}
			}
			
			if(args[0].equalsIgnoreCase("unban"))
			{
				if(!sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("��6[PVP]��c ��û��Ȩ��");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("��6[PVP]��c ����Ҳ����ڻ�����");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);

					PvpPlayer pvpPlayer = playerData.get(p.getUniqueId());
					pvpPlayer.setBanned(false);
					playerData.put(p.getUniqueId(), pvpPlayer);
					
					sender.sendMessage("��6[PVP]��3 ��������ҡ�c"+p.getName()+"��3�л�PVP");
					p.sendMessage("��6[PVP]��c ע�⣺�������ѱ������л�PVP״̬");
					return true;
				}
				else
				{
					sender.sendMessage("��6/pvp unban [�����] ��3����Ŀ������л�PVP״̬");
				}
			}
			
			if(args[0].equalsIgnoreCase("on"))
			{
				if(!sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("��6[PVP]��c ��û��Ȩ��");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("��6[PVP]��c ����Ҳ����ڻ�����");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);

					PvpPlayer pvpPlayer = playerData.get(p.getUniqueId());
					pvpPlayer.setPvp(true);
					playerData.put(p.getUniqueId(), pvpPlayer);

					sender.sendMessage("��6[PVP]��3 ��ǿ�ƴ���ҡ�c"+p.getName()+"��3��PVP");
					p.sendMessage("��6[PVP]��c ע�⣺���PVP�����ѱ�ǿ�ƴ�");
					return true;
				}
				else
				{
					sender.sendMessage("��6/pvp on [�����] ��3ǿ�ƴ�Ŀ����ҵ�PVP");
				}
			}
			
			if(args[0].equalsIgnoreCase("off"))
			{
				if(!sender.hasPermission("pvpSwitch.op"))
				{
					sender.sendMessage("��6[PVP]��c ��û��Ȩ��");
					return true;
				}
				
				if(args.length==2)
				{
					if(Bukkit.getServer().getPlayer(args[1])==null)
					{
						sender.sendMessage("��6[PVP]��c ����Ҳ����ڻ�����");
						return true;
					}
					Player p = Bukkit.getServer().getPlayer(args[1]);

					PvpPlayer pvpPlayer = playerData.get(p.getUniqueId());
					pvpPlayer.setPvp(false);
					playerData.put(p.getUniqueId(), pvpPlayer);

					sender.sendMessage("��6[PVP]��3 ��ǿ�ƹر���ҡ�c"+p.getName()+"��3��PVP");
					p.sendMessage("��6[PVP]��c ע�⣺���PVP�����ѱ�ǿ�ƹر�");
					return true;
				}
				else
				{
					sender.sendMessage("��6/pvp off [�����] ��3ǿ�ƹر�Ŀ����ҵ�PVP");
				}
			}
			
			if(args[0].equalsIgnoreCase("state"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;

					PvpPlayer pvpPlayer = playerData.get(p.getUniqueId());

					if(!pvpPlayer.canPvp())
						sender.sendMessage("��6[PVP]��3 ���PVPΪ�ر�״̬");
					else
						sender.sendMessage("��6[PVP]��3 ���PVPΪ��״̬");
				}
				return true;
			}
			return true;
		}
		return false;
	}
}


package privateFight;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import pvpSwitch.PVPSwitch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PrivateFight extends JavaPlugin
{
	//HashMap<String, ArrayList<String>> acceptPlayers = new HashMap<String, ArrayList<String>>();
	
	int timeForAccept = 30;
	
	int timeForWaitReady = 30;
	
	int timeForFight = 120;
	
	ArrayList<String> allowedCommand = new ArrayList<String>();
	// this location is used to teleport players who have finished fighting
	/**
	 *  Every location's name is stored in the keys of 'location', every key's value is stored 
	 *  the information of location which is a hash map as well. 
	 *  In the second hash map, keys are 'player1Lobby', 'player2Lobby', 'audienceLobby',
	 *  'player1Fight', 'player2Fight', 'player1Ready', 'player2Ready'.
	 */
	HashMap<String, HashMap<String, Location>> location = new HashMap<String, HashMap<String, Location>>();
	/**
	 *  The keys of 'fightPlayers' are the locations' names. The values are the two players who
	 *  want to fight.
	 */
	HashMap<String, ArrayList<String>> fightPlayers = new HashMap<String, ArrayList<String>>();
	/**
	 *  The keys are every player who is in 'fightPlayers', the values are the locations' names which
	 *  relate to players' names.
	 */
	HashMap<String, String> allFightingPlayers = new HashMap<String, String>();
	
	/**
	 *  Those are the process of setting a location.
	 */
	Location spawn;
	HashMap<String, String> setLocationPlayer1 = new HashMap<String, String>();
	HashMap<String, String> setLocationPlayer2 = new HashMap<String, String>();
	HashMap<String, String> setLocationPlayer3 = new HashMap<String, String>();
	HashMap<String, String> setLocationPlayer4 = new HashMap<String, String>();
	HashMap<String, String> setLocationPlayer5 = new HashMap<String, String>();
	HashMap<String, String> setLocationPlayer6 = new HashMap<String, String>();
	HashMap<String, String> setLocationPlayer7 = new HashMap<String, String>();
	
	HashMap<String, String> waitAcceptPlayer = new HashMap<String, String>();
	
	ArrayList<String> whoDied = new ArrayList<String>();
	
	ArrayList<String> isReady = new ArrayList<String>();
	
	PVPSwitch pvpSwitch;
	
	SimpleClans core;
	
	private boolean hookSimpleClans()
	{
		try 
		{
            for (Plugin plugin : getServer().getPluginManager().getPlugins()) 
            {
                if (plugin instanceof SimpleClans) 
                {
                    this.core = (SimpleClans) plugin;
                    return true;
                }
            }
        } 
		catch (NoClassDefFoundError e) 
		{
            return false;
        }
        return false;
	}
	
	public ClanManager getClanManager()
	{
	    return this.core.getClanManager();
	}
	
	public void onEnable() 
	{
		if(!new File(getDataFolder(),"Data").exists()) 
		{
			new File(getDataFolder(),"Data").mkdirs();
		}
		if(hookSimpleClans()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[PrivateFight] §4SimpleClan未加载!");
		}
		if(hookPVPSwitch()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[PrivateFight] §cPVPSwitch未加载");
		}
		loadLocation();
		loadConfig();
		getServer().getPluginManager().registerEvents(new PrivateFightSetLocationListener(this), this);
		getServer().getPluginManager().registerEvents(new PrivateFightBanOtherCommand(this), this);
		getServer().getPluginManager().registerEvents(new PrivateFightPlayerDeathListener(this), this);
		getServer().getPluginManager().registerEvents(new PrivateFightListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[PrivateFight] §e决斗系统加载完毕");
	}

	public void onDisable() 
	{
		
		saveLocation();
		Bukkit.getConsoleSender().sendMessage("§a[PrivateFight] §e决斗系统卸载完毕");
	}
	
	private boolean hookPVPSwitch() 
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("PVPSwitch");
	    pvpSwitch = PVPSwitch.class.cast(plugin);
	    return pvpSwitch != null;
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			ArrayList<String> allowedCommand = new ArrayList<String>();
			
			allowedCommand.add("/pk leave");
			
			config.set("PrivateFight.AllowedCommand", allowedCommand);
			
			config.set("PrivateFight.WaitForReady", 30);
			
			config.set("PrivateFight.FightTime", 120);
			
			config.set("PrivateFight.WaitForAccept", 30);
			
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
		
		if(!config.contains("PrivateFight"))
			return;
		
		for(String command:config.getStringList("PrivateFight.AllowedCommand"))
		{
			allowedCommand.add(command);
		}
		
		int waitTime = config.getInt("PrivateFight.WaitForReady");
		int fightTime = config.getInt("PrivateFight.FightTime");
		int acceptTime = config.getInt("PrivateFight.WaitForAccept");
		if(waitTime>0)
			timeForWaitReady = waitTime;
		if(fightTime>0)
			timeForFight = fightTime;
		if(acceptTime>0)
			timeForAccept = acceptTime;
	}
	
	public void saveLocation()
	{
		File file=new File(getDataFolder(),"/Data/location.yml");
		if(file.exists())
			file.delete();
		file=new File(getDataFolder(),"/Data/location.yml");
		FileConfiguration config;
		
		config = load(file);
		if(spawn!=null)
		{
			config.set("Spawn.World", spawn.getWorld().getName());
			config.set("Spawn.X", spawn.getX());
			config.set("Spawn.Y", spawn.getY());
			config.set("Spawn.Z", spawn.getZ());
			
			config.set("Spawn.Direction.X", spawn.getDirection().getX());
			config.set("Spawn.Direction.Y", spawn.getDirection().getY());
			config.set("Spawn.Direction.Z", spawn.getDirection().getZ());
		}
		
		if(!location.isEmpty())
		{
			for(String name:location.keySet())
			{
				for(String locationType:location.get(name).keySet())
				{
					Location l = location.get(name).get(locationType);
					String world = l.getWorld().getName();
					double x = l.getX();
					double y = l.getY();
					double z = l.getZ();
					
					if(locationType.equalsIgnoreCase("player1Ready") || 
							locationType.equalsIgnoreCase("player2Ready"))
					{
						config.set("Location."+name+"."+locationType+".World", world);
						config.set("Location."+name+"."+locationType+".Block.X", x);
						config.set("Location."+name+"."+locationType+".Block.Y", y);
						config.set("Location."+name+"."+locationType+".Block.Z", z);
					}
					else
					{
						double directionX = l.getDirection().getX();
						double directionY = l.getDirection().getY();
						double directionZ = l.getDirection().getZ();
						
						config.set("Location."+name+"."+locationType+".World", world);
						config.set("Location."+name+"."+locationType+".Block.X", x);
						config.set("Location."+name+"."+locationType+".Block.Y", y);
						config.set("Location."+name+"."+locationType+".Block.Z", z);
							
						config.set("Location."+name+"."+locationType+".Direction.X", directionX);
						config.set("Location."+name+"."+locationType+".Direction.Y", directionY);
						config.set("Location."+name+"."+locationType+".Direction.Z", directionZ);
					}
				}
			}
		}

		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadLocation()
	{
		File file=new File(getDataFolder(),"/Data/location.yml");

		FileConfiguration config;
		config = load(file);

		if(config.contains("Spawn"))
		{
			double x = config.getDouble("Spawn.X");
			double y = config.getDouble("Spawn.Y");
			double z = config.getDouble("Spawn.Z");
			String world = config.getString("Spawn.World");
			World w = Bukkit.getWorld(world);
			spawn = new Location(w, x, y, z);
			Vector v = new Vector(config.getDouble("Spawn.Direction.X"), config.getDouble("Spawn.Direction.Y"), config.getDouble("Spawn.Direction.Z"));
			spawn.setDirection(v);
			//Bukkit.getConsoleSender().sendMessage("§c"+l.getX()+","+l.getY()+","+l.getZ());

			//Bukkit.getConsoleSender().sendMessage("§c"+spawn.get(0).getX()+","+spawn.get(0).getY()+","+spawn.get(0).getZ()+","+spawn.get(0).getWorld().getName());
		}
		else
		{
			World w = Bukkit.getWorld("rpg");
			//Bukkit.getConsoleSender().sendMessage("§c"+Bukkit.getWorld("world"));
			spawn = w.getSpawnLocation();
		}

		if(!config.contains("Location"))
		{
			return;
		}
		
		for(String name:config.getConfigurationSection("Location").getKeys(false))
		{
			HashMap<String, Location> everyLocation = new HashMap<String, Location>();
			for(String locationType:config.getConfigurationSection("Location."+name).getKeys(false))
			{
				String world = config.getString("Location."+name+"."+locationType+".World");
				Location l = null;
				
				if((!locationType.equalsIgnoreCase("player1Ready")) && 
						(!locationType.equalsIgnoreCase("player2Ready")))
				{
					double x = config.getDouble("Location."+name+"."+locationType+".Block.X");
					double y = config.getDouble("Location."+name+"."+locationType+".Block.Y");
					double z = config.getDouble("Location."+name+"."+locationType+".Block.Z");
					
					double eyeX = config.getDouble("Location."+name+"."+locationType+".Direction.X");
					double eyeY = config.getDouble("Location."+name+"."+locationType+".Direction.Y");
					double eyeZ = config.getDouble("Location."+name+"."+locationType+".Direction.Z");
					Vector v = new Vector(eyeX, eyeY, eyeZ);
					
					l = new Location(Bukkit.getWorld(world), x, y, z);
					l.setDirection(v);
				}
				else if(locationType.equalsIgnoreCase("player1Ready") || 
						locationType.equalsIgnoreCase("player2Ready"))
				{
					double x = config.getDouble("Location."+name+"."+locationType+".Block.X");
					double y = config.getDouble("Location."+name+"."+locationType+".Block.Y");
					double z = config.getDouble("Location."+name+"."+locationType+".Block.Z");
					l = new Location(Bukkit.getServer().getWorld(world), x, y, z);
				}
				
				everyLocation.put(locationType, l);
			}
			location.put(name, everyLocation);
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
	
	public Player getTheOtherPlayer(Player p)
	{
		String locationName = allFightingPlayers.get(p.getName());
		int index = fightPlayers.get(locationName).indexOf(p.getName());
		Player theOtherPlayer = Bukkit.getServer().getPlayer(fightPlayers.get(locationName).get(1-index));
		
		return theOtherPlayer;
	}
	
	public void leaveFight(String locationName, Player leftPlayer)
	{
		String world = location.get(locationName).get("player1Ready").getWorld().getName();
		
		Sign sign1 = (Sign)Bukkit.getServer().getWorld(world).getBlockAt(location.get(locationName).get("player1Ready")).getState();
		sign1.setLine(3, "§c§l未准备");
		Sign sign2 = (Sign)Bukkit.getServer().getWorld(world).getBlockAt(location.get(locationName).get("player2Ready")).getState();
		sign2.setLine(3, "§c§l未准备");
		
		sign1.update();
		sign2.update();
		
		Location l = Bukkit.getServer().getWorld("pvp").getSpawnLocation();
		leftPlayer.teleport(l);
		leftPlayer.teleport(l);
		
		
		Player otherPlayer = getTheOtherPlayer(leftPlayer);
		
		fightPlayers.remove(locationName);
		
		pvpSwitch.getAPI().switchOffPVP(leftPlayer);
		pvpSwitch.getAPI().unbanPlayer(leftPlayer);
		pvpSwitch.getAPI().switchOffPVP(otherPlayer);
		pvpSwitch.getAPI().unbanPlayer(otherPlayer);
		
		allFightingPlayers.remove(leftPlayer.getName());
		allFightingPlayers.remove(otherPlayer.getName());
	}
	
	public void leaveFight(String locationName, Player p1, Player p2)
	{
		p1.sendTitle("§a决斗结束", "");
		p2.sendTitle("§a决斗结束", "");
		String world = location.get(locationName).get("player1Ready").getWorld().getName();
		
		Sign sign1 = (Sign)Bukkit.getServer().getWorld(world).getBlockAt(location.get(locationName).get("player1Ready")).getState();
		sign1.setLine(3, "§c§l未准备");
		Sign sign2 = (Sign)Bukkit.getServer().getWorld(world).getBlockAt(location.get(locationName).get("player2Ready")).getState();
		sign2.setLine(3, "§c§l未准备");
		
		sign1.update();
		sign2.update();
		Location l = spawn;

		if(p1!=null)
		{
			p1.teleport(l);
		}
			
		if(p2!=null)
		{
			p2.teleport(l);
		}
		
		if(isReady.contains(locationName))
			isReady.remove(locationName);
		
		fightPlayers.remove(locationName);
		
		if(getClanManager().getClanByPlayerUniqueId(p1.getUniqueId())!=null)
			getClanManager().getClanPlayer(p1).setFriendlyFire(false);
		if(getClanManager().getClanByPlayerUniqueId(p2.getUniqueId())!=null)
			getClanManager().getClanPlayer(p2).setFriendlyFire(false);
		pvpSwitch.getAPI().switchOffPVP(p1);
		pvpSwitch.getAPI().switchOffPVP(p2);
		pvpSwitch.getAPI().unbanPlayer(p1);
		pvpSwitch.getAPI().unbanPlayer(p2);
		
		allFightingPlayers.remove(p1.getName());
		allFightingPlayers.remove(p2.getName());
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("pk"))
		{
			if (args.length==0)
			{
				sender.sendMessage("§a=========[决斗系统]=========");
				sender.sendMessage("§a/pk [玩家名] §3向目标玩家发起决斗");
				sender.sendMessage("§a/pk goto [场地名] §3前往目标决斗场地");
				sender.sendMessage("§a/pk leave §3离开决斗场地");
				sender.sendMessage("§a/pk accept §3接受决斗邀请");
				sender.sendMessage("§a/pk deny §3拒绝决斗邀请");
				if(sender.isOp())
				{
					sender.sendMessage("§4/pk setspawn §3设置决斗结束传送场地");
					sender.sendMessage("§4/pk list §3列出所有决斗场地");
					sender.sendMessage("§4/pk set [场地名] §3设置决斗场地");
					sender.sendMessage("§4/pk goto1 [场地名] §3前往玩家1出生区域");
					sender.sendMessage("§4/pk goto2 [场地名] §3前往玩家2出生区域");
					sender.sendMessage("§4/pk delete [场地名] §3删除该场地");
					//sender.sendMessage("§a/pk off §3强制关闭PVP");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("test"))
			{
				Player p = (Player)sender;
				p.teleport(spawn);
				return true;
			}
			
			if(args[0].equalsIgnoreCase("delete"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						Player p = (Player)sender;
						if(location.containsKey(args[1]))
						{
							location.remove(args[1]);
							p.sendMessage("§a[决斗系统] §e场地§c"+args[1]+"§e删除成功");
						}
						else
						{
							p.sendMessage("§a[决斗系统] §c无限的场地名");
						}
					}
					else
					{
						sender.sendMessage("§a/pk delete [场地名] §3删除该场地");
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("setspawn"))
			{
				if(sender instanceof Player)
				{
					if(sender.isOp())
					{
						Player p = (Player)sender;
						spawn = p.getLocation();
						p.sendMessage("§a[决斗系统] §e决斗结束的传送场地已设置");
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("deny"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(waitAcceptPlayer.containsKey(p.getName()))
					{
						p.sendMessage("§a[决斗系统] §6已拒绝决斗请求");
						waitAcceptPlayer.remove(p.getName());
					}
					else
					{
						p.sendMessage("§a[决斗系统] §6你没有收到决斗邀请或邀请已过期");
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("accept"))
			{
				if(sender instanceof Player)
				{
					Player p1 = (Player)sender;
					if(waitAcceptPlayer.containsKey(p1.getName()))
					{
						if(Bukkit.getServer().getPlayer(waitAcceptPlayer.get(p1.getName()))==null)
						{
							p1.sendMessage("§a[决斗系统] §c对方玩家不在线!");
							waitAcceptPlayer.remove(p1.getName());
							return true;
						}
						Player p2 = Bukkit.getServer().getPlayer(waitAcceptPlayer.get(p1.getName()));
						
						if(allFightingPlayers.containsKey(p2.getName()))
						{
							p1.sendMessage("§a[决斗系统] §c对方玩家已经在决斗了!");
							waitAcceptPlayer.remove(p1.getName());
							return true;
						}
						/*
						 *  Once this player reaches all conditions, then remove his name from 
						 *  the hash map. Because it means this player can go fight with the sender
						 */
						waitAcceptPlayer.remove(p1.getName());
						ArrayList<String> players = new ArrayList<String>();
						players.add(p1.getName());
						players.add(p2.getName());
						/*
						 *  This loop used to check if there are empty place for fighting
						 *  Once the system finds one, then ends the loop. And put players
						 *  into another system which controls the beginning of fight.
						 */
						for(String locationName:location.keySet())
						{
							if(!fightPlayers.containsKey(locationName))
							{
								fightPlayers.put(locationName, players);
								p1.teleport(location.get(locationName).get("player1Lobby"));
								p2.teleport(location.get(locationName).get("player2Lobby"));
								allFightingPlayers.put(p1.getName(), locationName);
								allFightingPlayers.put(p2.getName(), locationName);
								pvpSwitch.getAPI().switchOnPVP(p1);
								pvpSwitch.getAPI().switchOnPVP(p2);
								pvpSwitch.getAPI().banPlayer(p1);
								pvpSwitch.getAPI().banPlayer(p2);

								if(getClanManager().getClanByPlayerUniqueId(p1.getUniqueId())!=null)
								{
									getClanManager().getClanPlayer(p1).setFriendlyFire(true);
								}

								if(getClanManager().getClanByPlayerUniqueId(p2.getUniqueId())!=null)
								{
									getClanManager().getClanPlayer(p2).setFriendlyFire(true);
								}
								
								
								/**
								 *  Once players agreed to fight, then they will be teleport 
								 *  to fighting place, the system will give them time to get
								 *  ready, if they cannot be ready in the given time, then
								 *  let them leave this fight.
								 */
								new BukkitRunnable()
								{
						    		int time;
									public void run()
									{
										if(isReady.contains(locationName))
										{
											cancel();
										}
										if(time>timeForWaitReady)
										{
											leaveFight(locationName, p1,p2);
											cancel();
										}
										time++;
									}
								}.runTaskTimer(this, 0L, 20L);
								
								Bukkit.getServer().broadcastMessage("§a[决斗系统] §5"+p1.getName()+"§6与§5"+p2.getName()+"§6的决斗即将开始,如想观战请输入§5/pk goto "+locationName+"§6进入观众场地");
								
								p1.sendTitle("§a你已传送到决斗场地", "§6请在"+timeForWaitReady+"秒内右键牌子以准备");
								p2.sendTitle("§a你已传送到决斗场地", "§6请在"+timeForWaitReady+"秒内右键牌子以准备");
								
								return true;
							}
						}
						p1.sendTitle("§a[决斗系统]", "§c目前没有空余场地");
						p2.sendTitle("§a[决斗系统]", "§c目前没有空余场地");
					}
					else
					{
						p1.sendMessage("§a[决斗系统] §6你没有收到决斗邀请或邀请已过期");
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("list"))
			{
				if(sender.isOp())
				{
					sender.sendMessage("§e==================================");
					sender.sendMessage("§a结束传送地点 -> X:"+(int)spawn.getX()+",Y:"+(int)spawn.getY()+",Z:"+(int)spawn.getZ()+",世界:"+spawn.getWorld().getName());
					sender.sendMessage("§e----------------------------------");
					for(String locationName:location.keySet())
					{
						sender.sendMessage("§a场地名称:§6"+locationName);
						/*
						for(String locationType:location.get(locationName).keySet())
						{
							int x = location.get(locationName).get(locationType).getBlockX();
							int y = location.get(locationName).get(locationType).getBlockY();
							int z = location.get(locationName).get(locationType).getBlockZ();
							sender.sendMessage(locationType+": "+x+","+y+","+z);
						}
						sender.sendMessage("§e---------------------------------");
						*/
					}
					if(location.isEmpty())
						sender.sendMessage("§9目前没有竞技场地");
					sender.sendMessage("§e==================================");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("set"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						if(sender instanceof Player)
						{
							Player p = (Player)sender;
							setLocationPlayer1.put(p.getName(), args[1]);
							p.sendMessage("§a[决斗系统] §6开始设置决斗场地");
							p.sendMessage("§a[决斗系统] §6请前往玩家1的传送场地,并输入add添加(输入exit退出)");
						}
					}
					else
					{
						sender.sendMessage("§a/pk set [场地名] §3设置决斗场地");
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("leave"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					
					if(allFightingPlayers.containsKey(p.getName()))
					{
						String locationName = allFightingPlayers.get(p.getName());
						int index = fightPlayers.get(locationName).indexOf(p.getName());
						Player p2 = Bukkit.getServer().getPlayer(fightPlayers.get(locationName).get(1-index));
						if(p2==null)
						{
							leaveFight(locationName, p);
						}
						leaveFight(locationName, p, p2);
						p.sendMessage("§a[决斗系统] §6你已退出决斗...");
						p2.sendMessage("§a[决斗系统] §6你的对手已放弃决斗");
						return true;
					}
					else
					{
						p.sendMessage("§a[决斗系统] §c你没有和任何玩家进行决斗");
					}
					
				}

				return true;
			}
			
			if(args[0].equalsIgnoreCase("goto"))
			{
				if(args.length==2)
				{
					if(sender instanceof Player)
					{
						Player p = (Player)sender;
						if(location.containsKey(args[1]))
						{
							Location l = location.get(args[1]).get("audienceLobby");
							p.teleport(l);
							p.sendMessage("§a[决斗系统] §6正在前往...");
						}
					}
				}
				else
				{
					sender.sendMessage("§a/pk goto [场地名] §3前往目标场地");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("goto1"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						if(sender instanceof Player)
						{
							Player p = (Player)sender;
							if(!p.isOp())
							{
								p.sendMessage("§a[决斗系统] §c你没有权限");
								return true;
							}
							
							if(location.containsKey(args[1]))
							{
								Location l = location.get(args[1]).get("player1Lobby");
								p.teleport(l);
								p.sendMessage("§a[决斗系统] §6正在前往...");
							}
						}
					}
					else
					{
						sender.sendMessage("§a/pk goto1 [场地名] §3前往玩家1出生区域");
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("goto2"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						if(sender instanceof Player)
						{
							Player p = (Player)sender;
							if(!p.isOp())
							{
								p.sendMessage("§a[决斗系统] §c你没有权限");
								return true;
							}

							if(location.containsKey(args[1]))
							{
								Location l = location.get(args[1]).get("player2Lobby");
								p.teleport(l);
								p.sendMessage("§a[决斗系统] §6正在前往...");
							}
						}
					}
					else
					{
						sender.sendMessage("§a/pk goto2 [场地名] §3前往玩家2出生区域");
					}
				}
				return true;
			}
			
			/*
			if(args[0].equalsIgnoreCase("goto3"))
			{
				Player p = (Player)sender;
				Location l = location.get(args[1]).get("player1Fight");
				p.teleport(l);
				p.sendMessage("§a[决斗系统] §6正在前往...");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("goto4"))
			{
				Player p = (Player)sender;
				Location l = location.get(args[1]).get("player2Fight");
				p.teleport(l);
				p.sendMessage("§a[决斗系统] §6正在前往...");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("goto5"))
			{
				Player p = (Player)sender;
				Location l = location.get(args[1]).get("player1Ready");
				p.teleport(l);
				p.sendMessage("§a[决斗系统] §6正在前往...");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("goto6"))
			{
				Player p = (Player)sender;
				Location l = location.get(args[1]).get("player2Ready");
				p.teleport(l);
				p.sendMessage("§a[决斗系统] §6正在前往...");
				return true;
			}
			*/
			
			// begin to fight
			if(args.length==1)
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(allFightingPlayers.containsKey(p.getName()))
					{
						p.sendMessage("§a[决斗系统] §c你已经在决斗了");
						return true;
					}
					
					if(args[0].equalsIgnoreCase(p.getName()))
					{
						p.sendMessage("§a[决斗系统] §c你不能和你自己决斗!");
						return true;
					}

					if(Bukkit.getServer().getPlayer(args[0])!=null)
					{
						if(allFightingPlayers.containsKey(args[0]))
						{
							p.sendMessage("§a[决斗系统] §c对方正在决斗中，请稍后重试");
							return true;
						}
						ArrayList<String> players = new ArrayList<String>();
						players.add(p.getName());
						players.add(args[0]);
						//acceptPlayers.put(args[0], players);
						p.sendMessage("§a[决斗系统] §e决斗邀请已发送,请等待对方接受邀请");
						Player sendTo = Bukkit.getServer().getPlayer(args[0]);
						sendTo.sendMessage("§a你收到来自玩家§5"+p.getName()+"§a的决斗邀请");
						sendTo.sendMessage("§a输入 §4/pk accept§a 接受邀请");
						sendTo.sendMessage("§a输入 §4/pk deny§e 拒绝邀请");
						sendTo.sendMessage("§a该邀请会在"+timeForAccept+"秒后自动取消");
						/*
						 *  if player1 sends this request to player2, then put player2 in the hash map
						 *  rather than player1. Because only player2 needs to accept and return
						 *  the result back to the system. Once the system receive the result or not,
						 *  decide if this fight can begin.
						 */
						waitAcceptPlayer.put(args[0], p.getName());
						
						/*
						 *  Used to give the player who are inviting time to consider if accept
						 */
				    	new BukkitRunnable()
						{
				    		int time;
							public void run()
							{
								/*
								 *  Once the player accept the request whatever from this player
								 *  or other player, the hash map will remove the player, and then
								 *  this loop ends.
								 */
								if(!waitAcceptPlayer.containsKey(args[0]))
								{
									cancel();
								}
								if(time>timeForAccept)
								{
									if(sender.isOp())
										p.sendMessage("§a[决斗系统] §e决斗邀请已过期(仅OP可以看到，测试用)");
									waitAcceptPlayer.remove(args[0]);
									cancel();
								}
								time++;
							}
						}.runTaskTimer(this, 0L, 20L);
					}
					else
					{
						p.sendMessage("§a[决斗系统] §c该玩家不在线或不存在");
					}
				}
				else
				{
					sender.sendMessage("§a[决斗系统] §c控制台无法使用该命令");
				}
			}
			return true;
		}
		return false;
		
	}
	
	
}


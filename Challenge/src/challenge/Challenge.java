package challenge;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.onarandombox.MultiverseCore.MultiverseCore;

import levelSystem.LevelSystem;
import net.citizensnpcs.api.CitizensAPI;
import net.milkbowl.vault.economy.Economy;
import queueSystem.QueueSystem;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Challenge extends JavaPlugin
{
	LevelSystem levelSystem;
	Economy economy;
	MultiverseCore core;
	boolean isEco;
	QueueSystem queueSystem;
	
	int limit = 2;
	int timeFactor = 0;
	int rewardFactor = 3;
	int NPCID = 0;
	boolean timeChallengeAppear = false;
	boolean mobChallengeAppear = false;
	
	/**
	 *  This hash map includes all locations' information and every location's coordinates and 
	 *  some other options
	 */
	HashMap<String, ChallengeInfo> challenge = new HashMap<String, ChallengeInfo>();
	
	HashMap<String, String> setTime = new HashMap<String, String>();
	
	HashMap<String, String> setKillMob = new HashMap<String, String>();
	
	//HashMap<String, Integer> startPlayer = new HashMap<String, Integer>();
	
	Location spawn;
	
	HashMap<String, ArrayList<ItemStack>> rewards = new HashMap<String, ArrayList<ItemStack>>();
	
	java.util.Random random=new java.util.Random();
	
	ItemStack getReward = null;
	
	String rewardGuiName = null;
	String timeGuiName = null;
	String killMobGuiName = null;
	
	HashMap<String, TimePlan> timePlan = new HashMap<String, TimePlan>();

	SimpleDateFormat date = new SimpleDateFormat("HH:mm");
	/**
	 *  This hash map includes every location's players who are in the GroupPlayers class.
	 */
	HashMap<String, GroupPlayers> groupPlayer = new HashMap<String, GroupPlayers>();
	
	/**
	 *  This hash map used to store every single player's information which is which location he
	 *  is playing.
	 */
	HashMap<Player, SinglePlayer> singlePlayer = new HashMap<Player, SinglePlayer>();
	
	HashMap<Integer, String> mobName = new HashMap<Integer, String>();
	
	private boolean hookQueueSystem()
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("QueueSystem");
	    queueSystem = QueueSystem.class.cast(plugin);
	    return queueSystem != null; 
	}
	
	private boolean hookLevelSystem()
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("LevelSystem");
	    levelSystem = LevelSystem.class.cast(plugin);
	    return levelSystem != null; 
	}
	
	private boolean hookMultiverseCore()
    {
		
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("Multiverse-Core");
	    core = MultiverseCore.class.cast(plugin);
	    return core != null; 
	}
	
	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null)
		{
			economy = economyProvider.getProvider();
		}
		return (economy!=null);
	}
	
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		if(Bukkit.getPluginManager().getPlugin("Vault")!=null)
		{
			isEco=setupEconomy();
		}
		if(isEco==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[Challenge] §4Valut未加载!");
			return;
		}
		if(hookLevelSystem()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[Challenge] §cLevelSystem未加载");
			return;
		}
		if(hookMultiverseCore()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[Challenge] §cMultiverse-Core未加载");
			return;
		}
		if(hookQueueSystem()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[Challenge] §cQueueSystem未加载");
			return;
		}
		spawn = Bukkit.getWorld("rpg").getSpawnLocation();
		loadConfig();
		loadLocationConfig();
		loadRewardItem();
		timer();
		initMobName initMobName = new initMobName(this);
		CitizensAPI.registerEvents(new NPCListener(this));
		getServer().getPluginManager().registerEvents(new KillMobListener(this), this);
		getServer().getPluginManager().registerEvents(new KillMobListener(this), this);
		getServer().getPluginManager().registerEvents(new ChallengeListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[Challenge] §e挑战系统加载完毕");
	}

	public void onDisable()
	{
		saveLocationConfig();
		saveConfig();
		Bukkit.getConsoleSender().sendMessage("§a[Challenge] §e挑战系统卸载完毕");
	}
	
	public void timer()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				String currentTime = date.format(new Date());
				if(timePlan.containsKey(currentTime))
				{
					if(timePlan.get(currentTime).getType().equalsIgnoreCase("time") && 
							timePlan.get(currentTime).isStart()==false)
					{
						Bukkit.broadcastMessage("§6[挑战系统] §e时间挑战开始了，持续时间为§5"+timePlan.get(currentTime).getDuration()+"§e分钟");
						timeChallengeAppear=true;
						timePlan.get(currentTime).setIsStart(true);
						timeTimer(timePlan.get(currentTime).getDuration());
					}
					else if(timePlan.get(currentTime).getType().equalsIgnoreCase("killmob") && 
							timePlan.get(currentTime).isStart()==false)
					{
						Bukkit.broadcastMessage("§6[挑战系统] §e怪物挑战开始了，持续时间为§5"+timePlan.get(currentTime).getDuration()+"§e分钟");
						mobChallengeAppear=true;
						timePlan.get(currentTime).setIsStart(true);
						mobTimer(timePlan.get(currentTime).getDuration());
					}
				}
			}
		}.runTaskTimer(this, 0L, 300L);
	}
	
	public void timeTimer(int duration)
	{
		
		new BukkitRunnable()
		{
			int time=0;
			public void run()
			{
				if(time>=1)
				{
					Bukkit.broadcastMessage("§6[挑战系统] §e时间挑战已经结束了");
					timeChallengeAppear=false;
					cancel();
				}
				time++;
			}
		}.runTaskTimer(this, 0, 20*duration*60);
	}
	
	public void mobTimer(int duration)
	{
		new BukkitRunnable()
		{
			int time=0;
			public void run()
			{
				if(time>=1)
				{
					Bukkit.broadcastMessage("§6[挑战系统] §e怪物挑战已经结束了");
					mobChallengeAppear=false;
					cancel();
				}
				time++;
			}
		}.runTaskTimer(this, 0, 20*duration*60);
	}
	
	public void saveConfig()
	{
		File file=new File(getDataFolder(), "config.yml");
		FileConfiguration config;
		
		config = load(file);
		
		config.set("Challenge.NPC.ID", NPCID);
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(), "config.yml");
		FileConfiguration config;
		if(!file.exists())
		{
			config = load(file);
			
			config.set("Challenge.Time.Factor", 10);
			config.set("Challenge.Time.GuiName", "§5时间挑战");
			config.set("Challenge.Time.StartTime", "15:40");
			config.set("Challenge.Time.Duration", 30);
			config.set("Challenge.KillMob.GuiName", "§5怪物挑战");
			config.set("Challenge.KillMob.StartTime", "15:50");
			config.set("Challenge.KillMob.Duration", 30);
			config.set("Challenge.Reward.Factor", 3);
			config.set("Challenge.Limit", 4);
			config.set("Challenge.NPC.ID", 4);
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			loadConfig();
			return;
		}
		
		config = load(file);
		
		timePlan.clear();
		String timeStartTime = config.getString("Challenge.Time.StartTime");
		int timeDuration = config.getInt("Challenge.Time.Duration");
		String killMobStartTime = config.getString("Challenge.KillMob.StartTime");
		int killMobDuration = config.getInt("Challenge.KillMob.Duration");
		
		if(timeDuration!=-1)
		{
			TimePlan tp = new TimePlan("time", timeDuration);
			this.timePlan.put(timeStartTime, tp);
		}
		if(killMobDuration!=-1)
		{
			TimePlan tp = new TimePlan("killmob", killMobDuration);
			this.timePlan.put(killMobStartTime, tp);
		}
		
		limit = config.getInt("Challenge.Limit");
		timeFactor = config.getInt("Challenge.Time.Factor");
		rewardFactor = config.getInt("Challenge.Reward.Factor");
		NPCID = config.getInt("Challenge.NPC.ID");
		timeGuiName = config.getString("Challenge.Time.GuiName");
		killMobGuiName = config.getString("Challenge.KillMob.GuiName");
	}
	
	public void loadRewardItem()
	{
		File file=new File(getDataFolder(), "reward.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			config.set("Reward.GUIName", "§a获取挑战奖励");
			config.set("Reward.Button.ID", 159);
			config.set("Reward.Button.Data", 0);
			config.set("Reward.Button.Name", "§a点击获取奖励");
			config.set("LocationName.1.Money", 100);
			config.set("LocationName.2.Item.ID", 264);
			config.set("LocationName.2.Item.Data", 0);
			config.set("LocationName.2.Item.Amount", 2);
			config.set("LocationName.2.Item.Name", "测试");
			config.set("LocationName.2.Item.Lore", "测试%继续测试");
			config.set("LocationName.2.Item.Enchantment.ID", 61);
			config.set("LocationName.2.Item.Enchantment.Level", 1);
			config.set("LocationName.2.Item.HideEnchant", true);
			
			try 
			{
				config.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			loadRewardItem();
			return;
		}
		
		config = load(file);
		rewards.clear();
		rewardGuiName = config.getString("Reward.GUIName");
		int buttonID = config.getInt("Reward.Button.ID");
		int buttonData = config.getInt("Reward.Button.Data");
		String buttonName = config.getString("Reward.Button.Name");
		
		getReward = createItem(buttonID, 1, buttonData, buttonName);
		
		for(String locationName:config.getRoot().getKeys(false))
		{
			if(locationName.equalsIgnoreCase("Reward"))
				continue;
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			for(int i=0; config.contains(locationName+"."+(i+1)); i++)
			{
				int money = config.getInt(locationName + "." + (i+1) + ".Money");
				int id = config.getInt(locationName + "." + (i+1) + ".Item.ID");
				int amount = config.getInt(locationName + "." + (i+1) + ".Item.Amount");
				int data = config.getInt(locationName + "." + (i+1) + ".Item.Data");
				String name = config.getString(locationName + "." + (i+1) + ".Item.Name");
				String lore = config.getString(locationName + "." + (i+1) + ".Item.Lore");
				int enchantID = config.getInt(locationName + "." + (i+1) + ".Item.Enchantment.ID");
				int enchantLevel = config.getInt(locationName + "." + (i+1) + ".Item.Enchantment.Level");
				boolean hide = config.getBoolean(locationName + "." + (i+1) + ".Item.HideEnchant");
				ItemStack item = null;
				if(money!=0 && id==0)
				{
					item = createItem(175, 1, 0, "§5金币奖励:"+money);
				}
				else if(money==0 && id!=0)
				{
					if(name!=null && lore!=null)
						item = createItem(id, amount, data, name, lore);
					else if(name==null && lore==null)
						item = new ItemStack(id, amount, (short) data);
				}
				if(enchantID>=0 && enchantLevel>0)
					item.addUnsafeEnchantment(Enchantment.getById(enchantID), enchantLevel);
				if(hide==true)
				{
					ItemMeta meta = item.getItemMeta();
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
				}
				if(item!=null)
					items.add(item);
			}
			rewards.put(locationName, items);
		}
	}
	
	public ItemStack createItem(int ID, int quantity, int durability, String displayName, String lore)
	{
		ItemStack item = new ItemStack(ID, quantity, (short)durability);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		ArrayList<String> loreList = new ArrayList<String>();
		for(String l:lore.split("%"))
		{
			loreList.add(l);
		}
		meta.setLore(loreList);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack createItem(int ID, int quantity, int durability, String displayName)
	{
		ItemStack item = new ItemStack(ID, quantity, (short)durability);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);

		item.setItemMeta(meta);
		
		return item;
	}
	
	public void loadLocationConfig()
	{
		File file=new File(getDataFolder(), "location.yml");
		FileConfiguration config;
		if (file.exists())
		{
			config = load(file);
			for(String name:config.getRoot().getKeys(false))
			{
				String type = config.getString(name+".Type");
				
				String world = config.getString(name+".Rest.World");
				double restX = config.getDouble(name+".Rest.X");
				double restY = config.getDouble(name+".Rest.Y");
				double restZ = config.getDouble(name+".Rest.Z");
				
				double restDirX = config.getDouble(name+".Rest.dirX");
				double restDirY = config.getDouble(name+".Rest.dirY");
				double restDirZ = config.getDouble(name+".Rest.dirZ");
				
				Location rest = new Location(Bukkit.getWorld(world), restX, restY, restZ);
				
				Vector restVector = new Vector(restDirX, restDirY, restDirZ);
				
				rest.setDirection(restVector);
				
				ArrayList<Location> location = new ArrayList<Location>();
				ArrayList<KillMobObject> mobObject = new ArrayList<KillMobObject>();
				for(int i=0; config.contains(name+"."+(i+1)); i++)
				{
					World w = Bukkit.getWorld(config.getString(name+"."+(i+1)+".World"));
					double x = config.getDouble(name+"."+(i+1)+".X");
					double y = config.getDouble(name+"."+(i+1)+".Y");
					double z = config.getDouble(name+"."+(i+1)+".Z");
					
					double dirX = config.getDouble(name+"."+(i+1)+".dirX");
					double dirY = config.getDouble(name+"."+(i+1)+".dirY");
					double dirZ = config.getDouble(name+"."+(i+1)+".dirZ");
					
					Location l = new Location(w, x, y, z);
					
					Vector v = new Vector(dirX, dirY, dirZ);
					
					l.setDirection(v);
					location.add(l);
					int mobID = config.getInt(name+"."+(i+1)+".Mob.ID");
					int mobAmount = config.getInt(name+"."+(i+1)+".Mob.Amount");
					String customName = config.getString(name+"."+(i+1)+".Mob.CustomName");
					KillMobObject killMobObject = new KillMobObject(mobID, mobAmount);
					if(customName!=null)
						killMobObject.setCustomName(customName);
					mobObject.add(killMobObject);
				}
				ChallengeInfo ch = new ChallengeInfo(name, config.getInt(name+".Level"), type);
				ch.setLocation(location);
				ch.setRest(rest);
				if(type.equalsIgnoreCase("killmob"))
					ch.setMobObject(mobObject);
				challenge.put(name, ch);
			}
		}
	}

	public void saveLocationConfig()
	{
		File file=new File(getDataFolder(), "location.yml");
		FileConfiguration config;
		if (file.exists())
			file.delete();
		config = load(file);
		for(String name:challenge.keySet())
		{
			config.set(name+".Type", challenge.get(name).getType());
			config.set(name+".Level", challenge.get(name).getLevel());
			config.set(name+".Rest.World", challenge.get(name).getRest().getWorld().getName());
			config.set(name+".Rest.X", challenge.get(name).getRest().getX());
			config.set(name+".Rest.Y", challenge.get(name).getRest().getY());
			config.set(name+".Rest.Z", challenge.get(name).getRest().getZ());
			
			config.set(name+".Rest.dirX", challenge.get(name).getRest().getDirection().getX());
			config.set(name+".Rest.dirY", challenge.get(name).getRest().getDirection().getY());
			config.set(name+".Rest.dirZ", challenge.get(name).getRest().getDirection().getZ());
			int i = 1;
			for(Location l:challenge.get(name).getLocation())
			{
				config.set(name+"."+i+".World", l.getWorld().getName());
				
				config.set(name+"."+i+".X", l.getX());
				config.set(name+"."+i+".Y", l.getY());
				config.set(name+"."+i+".Z", l.getZ());
				
				config.set(name+"."+i+".dirX", l.getDirection().getX());
				config.set(name+"."+i+".dirY", l.getDirection().getY());
				config.set(name+"."+i+".dirZ", l.getDirection().getZ());
				
				if(challenge.get(name).getType().equalsIgnoreCase("killmob"))
				{
					config.set(name+"."+i+".Mob.ID", challenge.get(name).getMobObject().get(i-1).getMobId());
					config.set(name+"."+i+".Mob.Amount", challenge.get(name).getMobObject().get(i-1).getAmount());
					config.set(name+"."+i+".Mob.CustomName", challenge.get(name).getMobObject().get(i-1).getCustomName());
				}
				i++;
			}
		}
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Inventory getChallengeGUI(Player p)
	{
		Inventory inv = null;
		if(timeChallengeAppear==true && mobChallengeAppear==false)
		{
			inv = Bukkit.createInventory(p, 54, "§5时间挑战");
			int i = 0;
			for(String locationName:this.challenge.keySet())
			{
				if(this.challenge.get(locationName).getType().equalsIgnoreCase("time"))
				{
					String lore = "";
					if(challenge.get(locationName).isStart())
						lore = "§3人数要求:§c"+limit+"%§3等级要求:§c"+challenge.get(locationName).getLevel()+"%§5该挑战已经开始";
					else
						lore = "§3人数要求:§c"+limit+"%§3等级要求:§c"+challenge.get(locationName).getLevel();
					ItemStack item = createItem(311, 1, 0, "§5"+locationName, lore);
					inv.setItem(i, item);
					i++;
				}
			}
		}
		else if(timeChallengeAppear==false && mobChallengeAppear==true)
		{
			inv = Bukkit.createInventory(p, 54, "§5怪物挑战");
			int i = 0;
			for(String locationName:this.challenge.keySet())
			{
				if(this.challenge.get(locationName).getType().equalsIgnoreCase("killmob"))
				{
					String lore = "";
					if(challenge.get(locationName).isStart())
						lore = "§3人数要求:§c"+limit+"%§3等级要求:§c"+challenge.get(locationName).getLevel()+"%§5该挑战已经开始";
					else
						lore = "§3人数要求:§c"+limit+"%§3等级要求:§c"+challenge.get(locationName).getLevel();
					ItemStack item = createItem(276, 1, 0, "§5"+locationName, lore);
					inv.setItem(i, item);
					i++;
				}
			}
		}
		return inv;
	}
	
	public Inventory rewardGUI(Player p, int number)
	{
		int time = 0;
		if(number%9!=0)
		{
			time = (number/9)+1;
		}
		else
		{
			time = number/9;
		}
		Inventory inv = Bukkit.createInventory(p, time*9, rewardGuiName);
		
		for(int i=0; i<number; i++)
		{
			inv.setItem(i, getReward);
		}
		
		return inv;
	}
	
	public int random(int max)
	{
		int result=random.nextInt(max);
		return result;
	}
	
	public ItemStack getReward(String location)
	{
		if(rewards.size()>0)
		{
			int index = random(rewards.get(location).size());
			return rewards.get(location).get(index);
		}
		else
			return null;
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
	
	public void killMobChallengeStart(String locationName) 
	{
		ArrayList<Player> players = groupPlayer.get(locationName).getPlayers();
		
		for(Player p:players)
		{
			p.sendTitle("§6挑战即将开始", "§e请做好准备");
		}
		
		new BukkitRunnable()
		{
    		int time;

			public void run()
			{
				ArrayList<Player> players = groupPlayer.get(locationName).getPlayers();
				if(time==1)
					for(Player p:players)
					{
						p.sendTitle("3","");
					}
				
				if(time==2)
					for(Player p:players)
					{
						p.sendTitle("2","");
					}
				
				if(time==3)
					for(Player p:players)
					{
						p.sendTitle("1","");
					}
				if(time==4)
				{
					for(Player p:players)
					{
						core.getCore().teleportPlayer(Bukkit.getConsoleSender(), p, challenge.get(locationName).getLocation().get(0));
						SinglePlayer sp = singlePlayer.get(p);
						int mobID = challenge.get(locationName).getMobObject().get(0).getMobId();
						int mobAmount = challenge.get(locationName).getMobObject().get(0).getAmount();
						String mobCustomName = challenge.get(locationName).getMobObject().get(0).getCustomName();
						sp.setCustomName(mobCustomName);
						sp.setMobAmount(mobAmount);
						sp.setMobID(mobID);
						singlePlayer.put(p, sp);
						if(sp.getCustomName()==null)
						{
							p.sendTitle("§6挑战开始", "§e请击杀§5"+mobAmount/2+"§e只§5"+mobName.get(mobID));
							p.sendMessage("§6[挑战系统] §e请击杀§5"+mobAmount/2+"§e只§5"+mobName.get(mobID));
						}
							
						else
						{
							p.sendTitle("§6挑战开始", "§e请击杀§5"+mobAmount/2+"§e只以§5"+mobCustomName+"§e为名的§5"+mobName.get(mobID));
							p.sendMessage("§6[挑战系统] §e请击杀§5"+mobAmount/2+"§e只以§5"+mobCustomName+"§e为名的§5"+mobName.get(mobID));
						}
							
					}
					cancel();
				}
				time++;
			}
			
		}.runTaskTimer(this, 0L, 20L);
	}
	
	public Inventory showReward(Player p, String location)
	{
		Inventory inv = Bukkit.createInventory(p, 54, "AAAAAAA");
		int i = 0;
		for(ItemStack item:rewards.get(location))
		{
			if(i<=53)
			{
				inv.setItem(i, item);
				i++;
			}
		}
		return inv;
	}
	
	public void leaveChallenge(Player p, boolean isLeader)
	{
		if(singlePlayer.containsKey(p))
		{
			if(!isLeader)
			{
				groupPlayer.get(singlePlayer.get(p)).removePlayer(p);
				singlePlayer.remove(p);
			}
			else
			{
				groupPlayer.remove(p);
				singlePlayer.remove(p);
			}
		}
	}
	
	public void startChallenge(Player p, String locationName)
	{
		Task task = new Task(this);
		if(queueSystem.getAPI().isLeader(p))
		{
			if(groupPlayer.containsKey(locationName))
			{
				p.sendMessage("§6[挑战系统] §c该场地已经存在队伍了！");
				return;
			}
			if(!queueSystem.getAPI().hasTeam(p))
			{
				p.sendMessage("§6[挑战系统] §c请以队伍的形式来报名！");
				return;
			}
			Player leader = queueSystem.getAPI().getLeader(p);
			if(queueSystem.getAPI().getQueue(leader).getPlayers().size()<limit)
			{
				p.sendMessage("§6[挑战系统] §c队伍人数不足，请至少组队"+limit+"人！");
				return;
			}
			int levelRequire = this.challenge.get(locationName).getLevel();
			for(Player member:queueSystem.getAPI().getQueue(leader).getPlayers())
			{
				if(levelSystem.getAPI().getLevel(member)<levelRequire)
				{
					p.sendMessage("§6[挑战系统] §c玩家§5"+member.getName()+"§c的等级不满足§5"+levelRequire+"§c级");
					return;
				}
			}
			//p.sendMessage("§c111111");
			GroupPlayers gp = new GroupPlayers();
			ArrayList<Player> players = (ArrayList<Player>) queueSystem.getAPI().getQueue(leader).getPlayers().clone();
			gp.setPlayer(players);
			groupPlayer.put(locationName, gp);
			//p.sendMessage("§c222222");
			for(Player player:groupPlayer.get(locationName).getPlayers())
			{
				SinglePlayer sp = new SinglePlayer(locationName);
				singlePlayer.put(player, sp);
			}
			//p.sendMessage("§c333333");
			challenge.get(locationName).changeStart(true);
			p.sendMessage("§6[挑战系统] §6已启动"+locationName+"的挑战");
			
			String type = challenge.get(locationName).getType();
			if(type.equalsIgnoreCase("time"))
			{
				task.timeChallengeStart(locationName);
			}
			else if(type.equalsIgnoreCase("killmob"))
			{
				killMobChallengeStart(locationName);
			}
		}
		else
		{
			p.sendMessage("§6[挑战系统] §c你不是队长，无法启动挑战");
		}
	}
	
	public void openRewardGui(ArrayList<Player> players, int quantity)
	{
		new BukkitRunnable()
		{
			int time;
			public void run()
			{
				if(time==2)
					for(Player p:players)
						p.sendTitle("§5即将打开奖励窗口", "");
				if(time==4)
					for(Player p:players)
						p.sendTitle("§53", "");
				if(time==5)
					for(Player p:players)
						p.sendTitle("§52", "");
				if(time==6)
					for(Player p:players)
						p.sendTitle("§51", "");
				if(time==7)
				{
					for(Player p:players)
					{
						p.openInventory(rewardGUI(p, quantity));
					}
					cancel();
				}
				time++;
			}
		}.runTaskTimer(this, 0L, 20L);
	}
	
	public void openRewardGui(Player p, int quantity)
	{
		new BukkitRunnable()
		{
			int time;
			public void run()
			{
				if(time==2)
					p.sendTitle("§5即将打开奖励窗口", "");
				if(time==4)
					p.sendTitle("§53", "");
				if(time==5)
					p.sendTitle("§52", "");
				if(time==6)
					p.sendTitle("§51", "");
				if(time==7)
				{
					p.openInventory(rewardGUI(p, quantity));
					cancel();
				}
				time++;
			}
		}.runTaskTimer(this, 0L, 20L);
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("tz"))
		{
			if(args.length==0)
			{
				
				//sender.sendMessage("§a/tz my §3查看我的挑战状态");
				
				//sender.sendMessage("§a/tz quit §3离开当前的场地");
				//sender.sendMessage("§a/tz join [场地名] §3加入该场地名的队伍");
				if(sender.isOp())
				{
					sender.sendMessage("§a=========[挑战系统]=========");
					sender.sendMessage("§a/tz start [场地名] §3开始该场地的活动");
					sender.sendMessage("§a/tz set [场地名] [类型] [等级限制] §3进入设置步骤");
					sender.sendMessage("§a/tz gui §3打开挑战界面");
					sender.sendMessage("§a/tz list <场地名> §3列出所有该场地的传送点");
					sender.sendMessage("§a/tz clear [场地名] §3清空该场地的传送点");
					sender.sendMessage("§a/tz setnpc [NPCID] §3设置NPCID");
					sender.sendMessage("§a/tz reload §3重读配置");
					sender.sendMessage("§a/tz test [场地名] §3测试");
					//sender.sendMessage("§a/tz time [玩家名] [场地名] §3开始传送到坚持时间场地");
					//sender.sendMessage("§a/tz start [场地名] §3开始该场地的活动");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("test"))
			{
				Player p = (Player)sender;
				p.openInventory(rewardGUI(p, 2));
			}
			
			if(args[0].equalsIgnoreCase("setnpc"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						if(args[1].matches("[0-9]*"))
						{
							int id = Integer.valueOf(args[1]);
							if(CitizensAPI.getNPCRegistry().getById(id)!=null)
							{
								NPCID=id;
								sender.sendMessage("§6[挑战系统] §e设置成功");
							}
						}
					}
				}
			}
			
			if(args[0].equalsIgnoreCase("gui"))
			{
				if(sender.isOp() && sender instanceof Player)
				{
					timePlan.clear();
					Player p = (Player)sender;
					Inventory inv = getChallengeGUI(p);
					if(inv!=null)
						p.openInventory(inv);
				}
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					loadConfig();
					loadRewardItem();
					sender.sendMessage("§6[挑战系统] §e重读配置成功");
				}
			}
			
			/*
			if(args[0].equalsIgnoreCase("my"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(singlePlayer.containsKey(p))
					{
						p.sendMessage("§a你当前处于§5"+singlePlayer.get(p)+"§a的队列中");
					}
					else
					{
						p.sendMessage("§a你还没有加入队列");
					}
				}
				return true;
			}
			*/
			
			if(args[0].equalsIgnoreCase("start"))
			{
				if(sender instanceof Player && sender.isOp())
				{
					if(args.length==2)
					{
						if(challenge.containsKey(args[1]))
						{
							Player p = (Player)sender;
							startChallenge(p, args[1]);
						}
						else
						{
							sender.sendMessage("§6[挑战系统] §c不存在的场地");
						}
					}
				}
				return true;
			}
			
			/*
			if(args[0].equalsIgnoreCase("join"))
			{
				if(sender instanceof Player)
				{
					if(challenge.containsKey(args[1]))
					{
						Player p = (Player)sender;
						if(groupPlayer.containsKey(args[1]))
						{
							p.sendMessage("§c该场地已经存在队伍了！");
							return true;
						}
						if(!queueSystem.getAPI().hasTeam(p))
						{
							p.sendMessage("§c请以队伍的形式来报名！");
							return true;
						}
						Player leader = queueSystem.getAPI().getLeader(p);
						if(queueSystem.getAPI().getQueue(leader).getPlayers().size()<limit)
						{
							p.sendMessage("§c队伍人数不足，请至少组队"+limit+"人！");
							return true;
						}
						GroupPlayers gp = new GroupPlayers();
						ArrayList<Player> players = (ArrayList<Player>) queueSystem.getAPI().getQueue(leader).getPlayers().clone();
						gp.setPlayer(players);
						groupPlayer.put(args[1], gp);
						p.sendMessage("§c报名成功，请等待队长开始挑战！");
					}
				}
				return true;
			}
			*/
			
			if(args[0].equalsIgnoreCase("clear"))
			{
				if(sender.isOp() && sender instanceof Player)
				{
					if(args.length==2)
					{
						Player p = (Player)sender;
						if(challenge.containsKey(args[1]))
						{
							challenge.remove(args[1]);
							p.sendMessage("§6[挑战系统] §c已清空"+args[1]+"的设置");
						}
						else
						{
							p.sendMessage("§6[挑战系统] §c没有以"+args[1]+"为名的场地");
						}
					}
					else
					{
						sender.sendMessage("§a/tz clear [场地名] §3清空该场地的传送点");
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("list"))
			{
				if(sender.isOp())
				{
					if(args.length==1)
					{
						sender.sendMessage("§6==========================================");
						if(challenge.isEmpty())
							sender.sendMessage("§e目前没有任何场地");
						else
							for(String name:challenge.keySet())
							{
								String type = "";
								if(challenge.get(name).getType().equalsIgnoreCase("time"))
									type = "时间挑战";
								else if(challenge.get(name).getType().equalsIgnoreCase("killmob"))
									type = "怪物挑战";
								
								sender.sendMessage("§e场地名:§5"+name + "§e, 层数:§5"+challenge.get(name).getLocation().size() + "§e, 类型:§5" + type);
							}
						sender.sendMessage("§6==========================================");
					}
					if(args.length==2)
					{
						if(challenge.containsKey(args[1]))
						{
							int i=1;
							for(Location l:challenge.get(args[1]).getLocation())
							{
								sender.sendMessage("§5"+i+"§e号传送点, X:§5"+l.getBlockX()+"§e,Y:§5"+l.getBlockY()+"§e,Z:§5"+l.getBlockZ());
								i++;
							}
						}
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("set"))
			{
				if(sender.isOp() && sender instanceof Player)
				{
					if(args.length==4)
					{
						Player p = (Player)sender;
						if(challenge.containsKey(args[1]))
						{
							p.sendMessage("§6[挑战系统] §c注意你正在修改"+args[1]+"的设置");
						}
						if(setTime.containsKey(p.getName()))
						{
							p.sendMessage("§6[挑战系统] §c你已经在设置了！");
							return true;
						}
						if(args[2].equalsIgnoreCase("time"))
						{
							ChallengeInfo ch = new ChallengeInfo(args[1], 0, "time");
							if(args[3].matches("[0-9]*"))
								ch.setLevel(Integer.valueOf(args[3]));
							challenge.put(args[1], ch);
							
							setTime.put(p.getName(), args[1]);
							p.sendMessage("§6[挑战系统] §a设置休息区域，输入add添加(exit退出)");
						}
						else if(args[2].equalsIgnoreCase("killmob"))
						{
							ChallengeInfo ch = new ChallengeInfo(args[1], 0, "killmob");
							challenge.put(args[1], ch);
							
							setKillMob.put(p.getName(), args[1]);
							p.sendMessage("§6[挑战系统] §a设置休息区域，输入add添加(exit退出)");
						}
					}
					else
					{
						sender.sendMessage("§a/tz set [场地名] [类型] [等级要求] §3类型包括time,killmob");
					}
				}
				return true;
			}
			return true;
		}
		
		return false;
	}


}


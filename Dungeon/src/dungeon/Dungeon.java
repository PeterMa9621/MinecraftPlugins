package dungeon;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.onarandombox.MultiverseCore.MultiverseCore;

import dps.Dps;
import levelSystem.LevelSystem;
import net.citizensnpcs.api.CitizensAPI;
import net.milkbowl.vault.economy.Economy;
import queueSystem.QueueSystem;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

public class Dungeon extends JavaPlugin
{
	LevelSystem levelSystem;
	Economy economy;
	MultiverseCore core;
	boolean isEco;
	QueueSystem queueSystem;
	Dps dps;
	
	int limit = 2;
	int NPCID = 0;
	int rewardFactor = 3;
	int lives = 3;
	
	/**
	 *  This hash map includes all locations' information and every location's coordinates and 
	 *  some other options
	 */
	HashMap<String, DungeonInfo> challenge = new HashMap<String, DungeonInfo>();
	
	HashMap<String, String> setTime = new HashMap<String, String>();
	
	HashMap<String, String> setKillMob = new HashMap<String, String>();
	
	HashMap<String, String> setBoss = new HashMap<String, String>();
	
	//HashMap<String, Integer> startPlayer = new HashMap<String, Integer>();
	
	Location spawn;
	
	HashMap<String, ArrayList<ItemStack>> rewards = new HashMap<String, ArrayList<ItemStack>>();
	
	java.util.Random random=new java.util.Random();
	
	ItemStack getReward = null;
	
	String rewardGuiName = null;
	
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
	
	HashMap<String, String> boss = new HashMap<String, String>();
	
	private boolean hookDps()
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("Dps");
	    dps = Dps.class.cast(plugin);
	    return dps != null;
	}
	
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
		if(hookDps()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[Challenge] §cDps未加载");
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

		loadLocationConfig();
		loadRewardItem();
		loadBossConfig();
		loadConfig();
		initMobName initMobName = new initMobName(this);
		CitizensAPI.registerEvents(new NPCListener(this));
		getServer().getPluginManager().registerEvents(new KillMobListener(this), this);
		getServer().getPluginManager().registerEvents(new KillMobListener(this), this);
		getServer().getPluginManager().registerEvents(new DungeonListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[Challenge] §e副本系统加载完毕");
	}

	public void onDisable()
	{
		saveLocationConfig();
		saveBossConfig();
		saveConfig();
		Bukkit.getConsoleSender().sendMessage("§a[Challenge] §e副本系统卸载完毕");
	}
	
	public void saveConfig()
	{
		File file=new File(getDataFolder(), "config.yml");
		FileConfiguration config;
		
		config = load(file);
		
		config.set("Dungeon.NPC.ID", NPCID);
		
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

			config.set("Dungeon.Reward.Factor", 3);
			config.set("Dungeon.Limit", 2);
			config.set("Dungeon.NPC.ID", 4);
			config.set("Dungeon.Lives", 3);
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			loadConfig();
			return;
		}
		
		config = load(file);
		
		limit = config.getInt("Dungeon.Limit");
		
		NPCID = config.getInt("Dungeon.NPC.ID");
		
		rewardFactor = config.getInt("Dungeon.Reward.Factor");
		
		lives = config.getInt("Dungeon.Lives");
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
	
	public void loadBossConfig()
	{
		File file=new File(getDataFolder(), "boss.yml");
		FileConfiguration config;
		if (file.exists())
		{
			config = load(file);
			
			for(String locationName:config.getRoot().getKeys(false))
			{
				String bossName = config.getString(locationName+".Name");
				int level = config.getInt(locationName+".Level");
				
				DungeonInfo d = new DungeonInfo(locationName, level);
				d.setBossName(bossName);
				challenge.put(locationName, d);
				
				/*
				GroupPlayers gp = new GroupPlayers();
				groupPlayer.put(locationName, gp);
				*/
				
				boss.put(bossName, locationName);
			}
		}
	}
	
	public void saveBossConfig()
	{
		File file=new File(getDataFolder(), "boss.yml");
		FileConfiguration config;
		if (file.exists())
			file.delete();
		
		config = load(file);
		
		for(String bossName:boss.keySet())
		{
			String locationName = boss.get(bossName);
			int level = challenge.get(locationName).getLevel();
			
			config.set(locationName+".Name", bossName);
			config.set(locationName+".Level", level);
		}
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				DungeonInfo ch = new DungeonInfo(name, config.getInt(name+".Level"));
				ch.setLocation(location);

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
			config.set(name+".Level", challenge.get(name).getLevel());
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

				config.set(name+"."+i+".Mob.ID", challenge.get(name).getMobObject().get(i-1).getMobId());
				config.set(name+"."+i+".Mob.Amount", challenge.get(name).getMobObject().get(i-1).getAmount());
				config.set(name+"."+i+".Mob.CustomName", challenge.get(name).getMobObject().get(i-1).getCustomName());
				
				i++;
			}
		}
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			p.sendTitle("§6副本传送将在§510§6秒后开始", "§e请做好准备");
		}
		
		new BukkitRunnable()
		{
    		int time;

			public void run()
			{
				//ArrayList<Player> players = groupPlayer.get(locationName).getPlayers();			
				if(time==6)
					for(Player p:players)
					{
						p.sendTitle("§c5","");
					}
				if(time==7)
					for(Player p:players)
					{
						p.sendTitle("§c4","");
					}
				
				if(time==8)
					for(Player p:players)
					{
						p.sendTitle("§c3","");
					}
				
				if(time==9)
					for(Player p:players)
					{
						p.sendTitle("§c2","");
					}
				if(time==10)
					for(Player p:players)
					{
						p.sendTitle("§c1","");
					}

				if(time==11)
				{
					GroupPlayers gp = groupPlayer.get(locationName);
					for(Player p:players)
					{
						core.getCore().teleportPlayer(Bukkit.getConsoleSender(), p, challenge.get(locationName).getLocation().get(0));
						//SinglePlayer sp = singlePlayer.get(p);
						int mobID = challenge.get(locationName).getMobObject().get(0).getMobId();
						int mobAmount = challenge.get(locationName).getMobObject().get(0).getAmount();
						String mobCustomName = challenge.get(locationName).getMobObject().get(0).getCustomName();
						gp.setCustomName(mobCustomName);
						gp.setMobAmount(mobAmount);
						gp.setMobID(mobID);
						int amount = mobAmount/2;
						if(amount<=0)
							amount = 1;
						if(mobCustomName!=null)
						{
							p.sendTitle("§6副本开始", "§6请击杀以§5"+mobCustomName+"§6为名的§e"+amount+"§6只§e"+mobName.get(mobID));
						}
						else
						{
							p.sendTitle("§6副本开始", "§6请击杀§e"+amount+"§6只§e"+mobName.get(mobID));
						}
						//p.sendTitle("§6副本开始", "§a请击杀§e"+mobAmount/2+"§a只§e"+mobName.get(mobID));
					}
					cancel();
					//challenger.teleport(challenge.get(args[2]).getLocation().get(0));
				}
				time++;
			}
			
		}.runTaskTimer(this, 0L, 30L);
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
	
	public Inventory getDungeonGUI(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 54, "§5选择副本");
		int i = 0;
		for(String locationName:this.challenge.keySet())
		{
			String lore = "";
			if(challenge.get(locationName).isStart()==true 
					&& challenge.get(locationName).getLocation().size()>0)
				lore = "§3人数要求:§c"+limit+"%§3等级要求:§c"+challenge.get(locationName).getLevel()+"%§5该副本已经开始";
			else if(challenge.get(locationName).isStart()==false 
					&& challenge.get(locationName).getLocation().size()>0)
				lore = "§3人数要求:§c"+limit+"%§3等级要求:§c"+challenge.get(locationName).getLevel();
			ItemStack item = createItem(311, 1, 0, "§5"+locationName, lore);
			inv.setItem(i, item);
			i++;
		}
		return inv;
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
	
	public void startDungeon(Player p, String locationName)
	{
		if(queueSystem.getAPI().isLeader(p))
		{
			if(groupPlayer.containsKey(locationName))
			{
				p.sendMessage("§c该场地已经存在队伍了！");
				return;
			}
			if(!queueSystem.getAPI().hasTeam(p))
			{
				p.sendMessage("§c请以队伍的形式来报名！");
				return;
			}
			Player leader = queueSystem.getAPI().getLeader(p);
			if(queueSystem.getAPI().getQueue(leader).getPlayers().size()<limit)
			{
				p.sendMessage("§c队伍人数不足，请至少组队"+limit+"人！");
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
			GroupPlayers gp = new GroupPlayers();
			ArrayList<Player> players = (ArrayList<Player>) queueSystem.getAPI().getQueue(leader).getPlayers().clone();

			//ArrayList<Player> players = queueSystem.getAPI().getQueue(leader).getPlayers();
			gp.setPlayer(players);
			groupPlayer.put(locationName, gp);

			for(Player player:groupPlayer.get(locationName).getPlayers())
			{
				SinglePlayer sp = new SinglePlayer(locationName);
				if(lives>0)
					sp.setLives(lives);
				singlePlayer.put(player, sp);
			}

			challenge.get(locationName).changeStart(true);
			dps.getAPI().startDpsModule(queueSystem.getAPI().getQueue(leader));
			killMobChallengeStart(locationName);
			p.sendMessage("§6已启动副本"+locationName);

		}
		else
		{
			p.sendMessage("§c你不是队长，无法启动挑战");
		}
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("fb"))
		{
			if(args.length==0)
			{
				if(sender.isOp())
				{
					sender.sendMessage("§a=========[副本系统]=========");
					//sender.sendMessage("§a/fb my §3查看我的挑战状态");
					sender.sendMessage("§a/fb start [场地名] §3开始该场地的活动");
					//sender.sendMessage("§a/tz quit §3离开当前的场地");
					//sender.sendMessage("§a/tz join [场地名] §3加入该场地名的队伍");
					sender.sendMessage("§a/fb setboss [场地名] [等级要求] [Boss名字] §3设置BOSS场地信息");
					sender.sendMessage("§a/fb set [场地名] §3进入设置步骤");
					sender.sendMessage("§a/fb list <场地名> §3列出所有该场地的传送点");
					sender.sendMessage("§a/fb clear [场地名] §3清空该场地的传送点");
					sender.sendMessage("§a/fb stop [场地名] §3停止该场地的活动");
					sender.sendMessage("§a/fb boss [玩家名] [场地名] §3使玩家进入BOSS场地");
					sender.sendMessage("§a/fb leaveboss [玩家名] §3使玩家离开BOSS场地");
					//sender.sendMessage("§a/fb reloadreward §3重载奖励配置");
					sender.sendMessage("§a/fb reload §3重载地点,Boss配置");
					sender.sendMessage("§a/fb setnpc [NPCID] §3设置NPCID");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("setnpc"))
			{
				if(sender.isOp())
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
			
			/*
			if(args[0].equalsIgnoreCase("reloadreward"))
			{
				if(sender.isOp())
				{
					loadRewardItem();
					sender.sendMessage("§6[副本系统] §a重载奖励配置成功！");
				}
			}
			*/
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender.isOp())
				{
					loadRewardItem();
					loadConfig();
					sender.sendMessage("§6[副本系统] §a重载配置成功！");
				}
			}
			
			if(args[0].equalsIgnoreCase("setboss"))
			{
				if(sender.isOp() && sender instanceof Player)
				{
					if(args.length==4)
					{
						if(!challenge.containsKey(args[1]))
						{
							if(args[2].matches("[0-9]*"))
							{
								DungeonInfo d = new DungeonInfo(args[1], Integer.valueOf(args[2]));
								d.setBossName(args[3].replaceAll("&", "§"));
								challenge.put(args[1], d);
								boss.put(args[3].replaceAll("&", "§"), args[1]);
								
								/*
								GroupPlayers gp = new GroupPlayers();
								groupPlayer.put(args[1], gp);
								*/
								
								sender.sendMessage("§e已设置以§5"+args[3].replaceAll("&", "§")+"§e为名的BOSS,场地名为§5"+args[1]+"§e,等级要求:§5"+args[2]);
							}
						}
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("stop"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						if(groupPlayer.containsKey(args[1]))
						{
							GroupPlayers gp = groupPlayer.get(args[1]);
							for(Player p:gp.getPlayers())
							{
								p.teleport(spawn);
								
								singlePlayer.remove(p);
								dps.getAPI().exitDpsModule(p);
							}
							groupPlayer.remove(args[1]);
						}
					}
				}
				return true;
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
			
			if(args[0].equalsIgnoreCase("leaveboss"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						if(Bukkit.getPlayer(args[1])!=null)
						{
							
							Player p = Bukkit.getPlayer(args[1]);
							if(singlePlayer.containsKey(p))
							{
								String locationName = singlePlayer.get(p).getLocation();
								if(groupPlayer.containsKey(locationName))
								{
									groupPlayer.get(locationName).removePlayer(p);
									singlePlayer.remove(p);
									dps.getAPI().exitDpsModule(p);
									p.sendMessage("§6你已退出TDR模式");
									return true;
								}
							}
							
						}
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("boss"))
			{
				if(sender.isOp())
				{
					if(args.length==3)
					{
						if(challenge.containsKey(args[2]))
						{
							if(Bukkit.getPlayer(args[1])!=null)
							{
								Player p = Bukkit.getPlayer(args[1]);
								if(!singlePlayer.containsKey(p))
								{
									GroupPlayers gp;
									if(groupPlayer.containsKey(args[2]))
									{
										gp = groupPlayer.get(args[2]);
									}
									else
									{
										gp = new GroupPlayers();
									}
									
									//ArrayList<Player> players = queueSystem.getAPI().getQueue(leader).getPlayers();
									gp.addPlayer(p);
									
									groupPlayer.put(args[2], gp);
									
									SinglePlayer sp = new SinglePlayer(args[2]);
									sp.setLives(-1);
									sp.setIsBoss(true);
									singlePlayer.put(p, sp);
									
									dps.getAPI().putIntoOtherQueue(p, args[2]);
									
									p.sendMessage("§6你已进入TDR模式");
								}
							}
						}
						
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("start"))
			{
				if(sender.isOp() && sender instanceof Player)
				{
					if(args.length==2)
					{
						if(challenge.containsKey(args[1]))
						{
							Player p = (Player)sender;

							
						}
						else
						{
							sender.sendMessage("§c不存在的场地");
						}
					}
				}
				return true;
			}
			
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
						for(String name:challenge.keySet())
						{
							if(challenge.get(name).getLocation().size()==0)
								sender.sendMessage(name + ", Boss场地");
							else
								sender.sendMessage(name + ", 层数:"+challenge.get(name).getLocation().size());
						}
					}
					if(args.length==2)
					{
						if(challenge.containsKey(args[1]))
						{
							int i=1;
							if(challenge.get(args[1]).getLocation()!=null)
								for(Location l:challenge.get(args[1]).getLocation())
								{
									sender.sendMessage(i+" "+l.getBlockX()+" "+l.getBlockY()+" "+l.getBlockZ());
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
					if(args.length==2)
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
						DungeonInfo ch = new DungeonInfo(args[1], 0);
						challenge.put(args[1], ch);

						setKillMob.put(p.getName(), args[1]);
						p.sendMessage("§6[挑战系统] §a第1层传送点，输入add添加(exit退出)");
						
					}
					else
					{
						sender.sendMessage("§a/tz set [场地名] §3设置副本");
					}
				}
				return true;
			}
			return true;
		}
		
		return false;
	}


}


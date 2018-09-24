package simpleClanHelper;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SimpleClanHelper extends JavaPlugin
{
	SimpleClans core;
	PlayerPoints playerPoints;
	Economy economy;
	
	int minLength = 1;
	int maxLength = 2;
	
	boolean isEco = false;
	String changeTagType = "";
	int changeTagPrice = 0;
	
	ArrayList<String> changeTag = new ArrayList<String>();
	
	HashMap<String, String> msg = new HashMap<String, String>();
	
	String prefix = "";
	
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
	
	private boolean hookPlayerPoints() 
	{
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("PlayerPoints");
	    playerPoints = PlayerPoints.class.cast(plugin);
	    return playerPoints != null; 
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

	public ClanManager getClanManager()
	{
	    return this.core.getClanManager();
	}
	
	public void onEnable() 
	{
		if(hookSimpleClans()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[SimpleClanHelper] §4SimpleClan未加载!");
		}
		if(hookPlayerPoints()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[SimpleClanHelper] §4PlayerPoints未加载!");
		}
		if(Bukkit.getPluginManager().getPlugin("Vault")!=null)
		{
			isEco=setupEconomy();
		}
		if(isEco==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[SimpleClanHelper] §4Valut未加载!");
		}
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		loadConfig();
		loadMessage();
		getServer().getPluginManager().registerEvents(new SimpleClanHelperListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[SimpleClanHelper] §e公会助手已启动成功");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[SimpleClanHelper] §e公会助手已卸载成功");
	}
	
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			config.set("ChangeTag.Price.Type", "PlayerPoints");
			config.set("ChangeTag.Price.Amount", 500);
			config.set("ChangeTag.MinLength", 1);
			config.set("ChangeTag.MaxLength", 6);
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			loadConfig();
		}
		
		config = load(file);
		
		changeTagType = config.getString("ChangeTag.Price.Type");
		changeTagPrice = config.getInt("ChangeTag.Price.Amount");
		minLength = config.getInt("ChangeTag.MinLength");
		maxLength = config.getInt("ChangeTag.MaxLength");
	}
	
	public void loadMessage()
	{
		File file=new File(getDataFolder(),"message.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			config.set("Prefix", "§6[家族助手]");
			config.set("GuiTitle", "§e家族助手");
			config.set("ChangeTagButton.Name", "§d点击修改家族标签");
			config.set("ChangeTagButton.Lore", "§e可修改为中文家族标签");
			config.set("NoClan", "§c你还没有家族");
			config.set("NotLeader", "§c你不是一个家族的管理者,无权修改家族标签");
			config.set("TagChangedTo", "§e公会标签修改为§a");
			config.set("create.Name", "§e创建新的家族");
			config.set("create.Lore", "§a需要花费20000金币");
			config.set("list.Name", "§e点击查看所有家族");
			config.set("profile.Name", "§e点击查看一个家族的属性");
			config.set("leaderboard.Name", "§e点击查看管理者列表");
			
			config.set("alliances.Name", "§e点击查看所有同盟家族");
			config.set("rivalries.Name", "§e点击查看所有敌对家族");
			config.set("roster.Name", "§e点击查看家族的成员列表");
			config.set("vitals.Name", "§e点击查看家族成员状态");
			config.set("coords.Name", "§e点击查看家族成员坐标");
			config.set("stats.Name", "§e点击查看家族成员击杀统计");
			config.set("kills.Name", "§e点击查看你的击杀统计");
			config.set("ally.Name", "§e增加或移除一个同盟家族");
			config.set("rival.Name", "§e增加或移除一个敌对家族");
			config.set("home.Name", "§e点击传送到家族基地");
			config.set("homeset.Name", "§e将当前位置设置为家族基地(只能设置一次)");
			config.set("war.Name", "§e发起一场战争");
			
			config.set("bb.Name", "§e显示家族公告板");
			config.set("modtag.Name", "§e修改家族标签颜色");
			config.set("toggle.Name", "§e开关个人设置");
			config.set("invite.Name", "§e邀请一位玩家");
			config.set("kick.Name", "§e踢出一位玩家");
			config.set("setrank.Name", "§e给予一名玩家职位");
			config.set("trust.Name", "§e设置一名正式成员");
			config.set("untrust.Name", "§e设置一名非正式成员");
			config.set("promote.Name", "§e设置一名管理者");
			config.set("demote.Name", "§e将一名管理者降至为普通成员");
			config.set("clanff.Name", "§e开关家族的友军伤害");
			config.set("disband.Name", "§e解散家族");
			config.set("ff.Name", "§e开关个人友军伤害");
			config.set("resign.Name", "§e退出家族");
			config.set("showtag.Name", "§e显示所有家族的英文标签");
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			loadMessage();
		}
		
		config = load(file);
		
		prefix = config.getString("Prefix");
		
		msg.put("GuiTitle", config.getString("GuiTitle"));
		msg.put("ChangeTagButton.Name", config.getString("ChangeTagButton.Name"));
		msg.put("ChangeTagButton.Lore", config.getString("ChangeTagButton.Lore"));
		msg.put("NoClan", config.getString("NoClan"));
		msg.put("NotLeader", config.getString("NotLeader"));
		msg.put("TagChangedTo", config.getString("TagChangedTo"));
		msg.put("create.Name", config.getString("createButton.Name"));
		msg.put("create.Lore", config.getString("createButton.Lore"));
		msg.put("list.Name", config.getString("listButton.Name"));
		msg.put("profile.Name", config.getString("profileButton.Name"));
		msg.put("leaderboard.Name", config.getString("leaderboard.Name"));
		
		msg.put("alliances.Name", config.getString("alliances.Name"));
		msg.put("rivalries.Name", config.getString("rivalries.Name"));
		msg.put("roster.Name", config.getString("roster.Name"));
		msg.put("vitals.Name", config.getString("vitals.Name"));
		msg.put("coords.Name", config.getString("coords.Name"));
		msg.put("stats.Name", config.getString("stats.Name"));
		msg.put("kills.Name", config.getString("kills.Name"));
		msg.put("ally.Name", config.getString("ally.Name"));
		msg.put("rival.Name", config.getString("rival.Name"));
		msg.put("home.Name", config.getString("home.Name"));
		msg.put("homeset.Name", config.getString("homeset.Name"));
		msg.put("war.Name", config.getString("war.Name"));
		
		msg.put("bb.Name", config.getString("bb.Name"));
		msg.put("modtag.Name", config.getString("modtag.Name"));
		msg.put("toggle.Name", config.getString("toggle.Name"));
		msg.put("invite.Name", config.getString("invite.Name"));
		msg.put("kick.Name", config.getString("kick.Name"));
		msg.put("setrank.Name", config.getString("setrank.Name"));
		msg.put("trust.Name", config.getString("trust.Name"));
		msg.put("untrust.Name", config.getString("untrust.Name"));
		msg.put("promote.Name", config.getString("promote.Name"));
		msg.put("demote.Name", config.getString("demote.Name"));
		msg.put("clanff.Name", config.getString("clanff.Name"));
		msg.put("disband.Name", config.getString("disband.Name"));
		msg.put("ff.Name", config.getString("ff.Name"));
		msg.put("resign.Name", config.getString("resign.Name"));
		msg.put("showtag.Name", config.getString("showtag.Name"));
	}
	
	public boolean payMoney(Player p)
	{
		if(changeTagType.equalsIgnoreCase("PlayerPoints"))
		{
			if(playerPoints.getAPI().look(p.getUniqueId())>=changeTagPrice)
			{
				playerPoints.getAPI().take(p.getUniqueId(), changeTagPrice);
				p.sendMessage(prefix + "§6已扣除§c"+changeTagPrice+"§6点券");
				return true;
			}
		}
		if(changeTagType.equalsIgnoreCase("Money"))
		{
			if(economy.has(p.getName(), changeTagPrice))
			{
				economy.withdrawPlayer(p.getName(), changeTagPrice);
				p.sendMessage(prefix + "§6已扣除§c"+changeTagPrice+"§6金币");
				return true;
			}
		}
		return false;
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
	
	public Inventory createGUI(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 36, msg.get("GuiTitle"));
		/*
		boolean hasClan = false;
		if(getClanManager().getClanPlayer(p).getClan()!=null)
			hasClan=true;
		boolean isLeader = false;
		if(hasClan)
			isLeader = getClanManager().getClanPlayer(p).isLeader();
		*/
		// =============================================================
		// Change tag button
		String content = "";
		if(this.changeTagType.equalsIgnoreCase("PlayerPoints"))
			content = "价格:§c"+this.changeTagPrice+"§e点券";
		else if(this.changeTagType.equalsIgnoreCase("Money"))
			content = "价格:§c"+this.changeTagPrice+"§e金币";
		ItemStack changeTag = createItem(421, 1, 0, msg.get("ChangeTagButton.Name"), msg.get("ChangeTagButton.Lore")+"%§e"+content);
		inv.setItem(0, changeTag);
		// =============================================================
		ItemStack create = createItem(138, 1, 0, msg.get("create.Name"), msg.get("create.Lore"));
		inv.setItem(1, create);
		// =============================================================
		ItemStack list = createItem(323, 1, 0, msg.get("list.Name"));
		inv.setItem(2, list);
		// =============================================================
		ItemStack profile = createItem(403, 1, 0, msg.get("profile.Name"));
		inv.setItem(3, profile);
		// =============================================================
		ItemStack leaderboard = createItem(425, 1, 0, msg.get("leaderboard.Name"));
		inv.setItem(4, leaderboard);
		// =============================================================
		ItemStack alliances = createItem(442, 1, 0, msg.get("alliances.Name"));
		inv.setItem(5, alliances);
		// =============================================================
		ItemStack rivalries = createItem(397, 1, 4, msg.get("rivalries.Name"));
		inv.setItem(6, rivalries);
		// =============================================================
		ItemStack roster = createItem(340, 1, 0, msg.get("roster.Name"));
		inv.setItem(7, roster);
		// =============================================================
		ItemStack vitals = createItem(351, 1, 1, msg.get("vitals.Name"));
		inv.setItem(8, vitals);
		// =============================================================
		ItemStack coords = createItem(345, 1, 0, msg.get("coords.Name"));
		inv.setItem(9, coords);
		// =============================================================
		ItemStack stats = createItem(260, 1, 0, msg.get("stats.Name"));
		inv.setItem(10, stats);
		// =============================================================
		ItemStack kills = createItem(267, 1, 0, msg.get("kills.Name"));
		inv.setItem(11, kills);
		// =============================================================
		ItemStack ally = createItem(442, 1, 0, msg.get("ally.Name"));
		inv.setItem(12, ally);
		// =============================================================
		ItemStack rival = createItem(397, 1, 4, msg.get("rival.Name"));
		inv.setItem(13, rival);
		// =============================================================
		ItemStack home = createItem(355, 1, 0, msg.get("home.Name"));
		inv.setItem(14, home);
		// =============================================================
		ItemStack homeset = createItem(324, 1, 0, msg.get("homeset.Name"));
		inv.setItem(15, homeset);
		// =============================================================
		ItemStack war = createItem(276, 1, 0, msg.get("war.Name"));
		inv.setItem(16, war);
		// =============================================================
		ItemStack bb = createItem(323, 1, 0, msg.get("bb.Name"));
		inv.setItem(17, bb);
		// =============================================================
		ItemStack modtag = createItem(160, 1, 5, msg.get("modtag.Name"));
		inv.setItem(18, modtag);
		// =============================================================
		ItemStack toggle = createItem(143, 1, 0, msg.get("toggle.Name"));
		inv.setItem(19, toggle);
		// =============================================================
		ItemStack invite = createItem(368, 1, 0, msg.get("invite.Name"));
		inv.setItem(20, invite);
		// =============================================================
		ItemStack kick = createItem(427, 1, 0, msg.get("kick.Name"));
		inv.setItem(21, kick);
		// =============================================================
		ItemStack setrank = createItem(399, 1, 0, msg.get("setrank.Name"));
		inv.setItem(22, setrank);
		// =============================================================
		ItemStack trust = createItem(397, 1, 3, msg.get("trust.Name"));
		inv.setItem(23, trust);
		// =============================================================
		ItemStack untrust = createItem(397, 1, 2, msg.get("untrust.Name"));
		inv.setItem(24, untrust);
		// =============================================================
		ItemStack promote = createItem(91, 1, 0, msg.get("promote.Name"));
		inv.setItem(25, promote);
		// =============================================================
		ItemStack demote = createItem(86, 1, 0, msg.get("demote.Name"));
		inv.setItem(26, demote);
		// =============================================================
		ItemStack clanff = createItem(315, 1, 0, msg.get("clanff.Name"));
		inv.setItem(27, clanff);
		// =============================================================
		ItemStack disband = createItem(166, 1, 0, msg.get("disband.Name"));
		inv.setItem(34, disband);
		// =============================================================
		ItemStack ff = createItem(311, 1, 0, msg.get("ff.Name"));
		inv.setItem(29, ff);
		// =============================================================
		ItemStack resign = createItem(331, 1, 0, msg.get("resign.Name"));
		inv.setItem(35, resign);
		// =============================================================
		//ItemStack fix = createItem(145, 1, 0, "§4权限修复");
		//inv.setItem(35, fix);
		ItemStack showTag = createItem(339, 1, 0, msg.get("showtag.Name"));
		inv.setItem(31, showTag);

		return inv;
	}
	
	public ItemStack createItem(Material material, String displayName)
	{
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack createItem(Material material, String displayName, ArrayList<String> lore)
	{
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack createItem(Material material, String displayName, String lore)
	{
		ItemStack item = new ItemStack(material);
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
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if(cmd.getName().equalsIgnoreCase("cc"))
		{
			if(args.length==0)
			{
				Player p = (Player)sender;
				p.openInventory(createGUI(p));
				return true;
			}
			
			if(args[0].equalsIgnoreCase("test"))
			{
				Player p = (Player)sender;
				sender.sendMessage(prefix + "§a"+core.getClanManager().getClanByPlayerName(p.getName()).getColorTag());
				sender.sendMessage(prefix + "§a"+core.getClanManager().getClanByPlayerName(p.getName()).getTag());
				sender.sendMessage(prefix + "§a"+core.getClanManager().getClanByPlayerName(p.getName()).getTagLabel(false));
				return true;
			}
			
			if(args[0].equalsIgnoreCase("reload"))
			{
				loadMessage();
				sender.sendMessage(prefix + "§a配置重载成功");
				return true;
			}
		}
		return true;
	}
}


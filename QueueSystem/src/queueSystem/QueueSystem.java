package queueSystem;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import levelSystem.LevelSystem;
import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Skull;



public class QueueSystem extends JavaPlugin
{
	LevelSystem levelSystem;
	Economy economy;
	boolean isEco;
	
	int limit = 5;
	
	HashMap<Player, Player> queuePlayer = new HashMap<Player, Player>();
	HashMap<Player, Queue> queue = new HashMap<Player, Queue>();
	
	HashMap<Player, Hologram> teamTag = new HashMap<Player, Hologram>();
	
	//HashMap<Queue, ArrayList<Player>> applyPlayer = new HashMap<Queue, ArrayList<Player>>();
	
	HashMap<Player, ArrayList<Queue>> whichQueuePlayerApply = new HashMap<Player, ArrayList<Queue>>();
	
	API api = new API(this);
	
	private boolean hookLevelSystem()
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("LevelSystem");
	    levelSystem = LevelSystem.class.cast(plugin);
	    return levelSystem != null;
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
		}
		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"))
	    {
	    	Bukkit.getConsoleSender().sendMessage("§a[Challenge] §cHolographicDisplays未加载");
	    	setEnabled(false);
	    	return;
	    }
		if(hookLevelSystem()==false)
		{
			Bukkit.getConsoleSender().sendMessage("§a[Challenge] §cLevelSystem未加载");
		}
		getServer().getPluginManager().registerEvents(new ApplyListener(this), this);
		getServer().getPluginManager().registerEvents(new ManageQueueListener(this), this);
		getServer().getPluginManager().registerEvents(new QueueSystemListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[Challenge] §e挑战系统加载完毕");
	}

	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage("§a[Challenge] §e挑战系统卸载完毕");
	}
	
	public API getAPI()
	{
		return api;
	}
	
	public void createTeamTag(Player p, Player leader)
	{
		Hologram holo = (Hologram)HologramsAPI.createHologram(this, p.getLocation().add(0.0D, 2.6D, 0.0D));
		holo.appendTextLine("§a§l"+leader.getName()+"§5§l的队伍");
		teamTag.put(p, holo);
		new BukkitRunnable()
		{
			public void run()
			{
				if(!queuePlayer.containsKey(p))
				{
					holo.delete();
					cancel();
					return;
				}
    			holo.teleport(p.getLocation().add(0.0D, 2.6D, 0.0D));
			}
		}.runTaskTimer(this, 0L, 0L);
	}
	
	public void removeTeamTag(Player p)
	{
		if(teamTag.containsKey(p))
			teamTag.remove(p);
	}
	
	public boolean joinQueue(Player p, Queue queue)
	{
		if(this.queuePlayer.containsKey(p))
			return false;
		if(queue.add(p))
		{
			queuePlayer.put(p, queue.getLeader());
			queue.removeApplicant(p);
			//this.applyPlayer.get(queue).remove(p);
			createTeamTag(p, queue.getLeader());
			for(Player member:queue.getPlayers())
			{
				if(member!=p)
					member.sendMessage("§6玩家§3"+p.getName()+"§6已加入队伍！");
			}
			for(Queue q:this.queue.values())
			{
				if(q.getApplicant().contains(p))
					q.removeApplicant(p);
			}
			if(p.getOpenInventory().getTopInventory().getTitle().contains("§e§l寻找队伍-页数:"))
			{
				p.closeInventory();
			}
			p.sendMessage("§6你已加入§3"+queue.getLeader().getName()+"§6的队伍！");
			return true;
		}
		return false;
	}
	
	public void disbandQueue(Queue queue)
	{
		Player leader = queue.getLeader();
		for(Player player:queue.getPlayers())
		{
			LeaveQueueEvent leaveQueue = new LeaveQueueEvent(this, player);
			Bukkit.getServer().getPluginManager().callEvent(leaveQueue);
			queuePlayer.remove(player);
			if(player.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("§e§l我的队伍"))
				player.closeInventory();
			player.sendMessage("§3你的队伍已§c解散！");
			
			//this.applyPlayer.remove(queue);
			removeTeamTag(player);
		}
		this.queue.remove(leader);
	}
	
	public void leaveQueue(Player p, Queue queue)
	{
		Player leader = queue.getLeader();
		LeaveQueueEvent leaveQueue = new LeaveQueueEvent(this, p);
		Bukkit.getServer().getPluginManager().callEvent(leaveQueue);
		queue.remove(p);
		if(leader.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("§e§l我的队伍"))
			leader.openInventory(manageQueueGui(leader));
		for(Player player:queue.getPlayers())
		{
			player.sendMessage("§6玩家§3"+p.getName()+"§6已退出队伍");
		}
		p.sendMessage("§6你已退出§3"+leader.getName()+"§6的队伍");
		if(p.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("§e§l我的队伍"))
			p.closeInventory();
		this.queuePlayer.remove(p);
		removeTeamTag(p);
	}
	
	public boolean applyQueue(Player p, Queue queue)
	{
		ArrayList<Player> players = queue.getApplicant();

		if(!players.contains(p) && players.size()<27)
		{
			//players.add(p);
			queue.addApplicant(p);
			if(queue.getLeader().getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("§e§l入队请求"))
				queue.getLeader().openInventory(applyGui(p, queue));
			p.sendMessage("§6你已经申请加入这个队伍");
			return true;
		}
		else
		{
			p.sendMessage("§6你已经在这个队伍的请求列表内了，请等待队长同意");
			return false;
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
	
	public ArrayList<Inventory> findQueueGui(Player p)
	{
		
		Inventory inv = Bukkit.createInventory(p, 54, "§e§l寻找队伍-页数:1");
		
		ItemStack create = createItem(425, 1, 0, "§6点击创建一个队伍");
		
		ItemStack next = createItem(351, 1, 13, "§3点击进入下一页");
		
		ItemStack previous = createItem(351, 1, 8, "§3点击进入上一页");

		inv.setItem(45, create);
		inv.setItem(51, previous);
		inv.setItem(53, next);
		ArrayList<Inventory> list = new ArrayList<Inventory>();
		
		int i = 0;
		for(Player player:this.queue.keySet())
		{

			ItemStack item = createItem(397, 1, 3, "§6点击申请加入§3"+player.getName()+"§6的队伍", "§a队内人数:"+queue.get(player).getPlayers().size());

			inv.setItem(i%44, item);
			if(i>43 && i/44==0)
			{
				list.add(inv);
				inv = Bukkit.createInventory(p, 54, "§e§l寻找队伍-页数:"+(((i+1)/44)+1));
				inv.setItem(45, create);
				inv.setItem(51, previous);
				inv.setItem(53, next);
			}
			i++;
		}
		if(i<44)
			list.add(inv);
		
		return list;
	}
	
	public Inventory applyGui(Player p, Queue queue)
	{
		Inventory inv = Bukkit.createInventory(p, 36, "§e§l入队请求");
		
		ItemStack back = createItem(331, 1, 0, "§f点击返回");
		
		inv.setItem(35, back);
		
		int i = 0;

		for(Player player:queue.getApplicant())
		{
			ItemStack item = null;
			item = createItem(397, 1, 3, "§6请求入队:"+player.getName(), "§a左键点击接受该玩家%§c右键点击拒绝该玩家");
			inv.setItem(i, item);
			i++;
		}
		

		return inv;
	}
	
	public Inventory manageQueueGui(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 18, "§e§l我的队伍");
		
		ItemStack disband = createItem(166, 1, 0, "§6解散当前队伍");
		ItemStack leave = createItem(330, 1, 0, "§6离开当前队伍");
		ItemStack apply = createItem(386, 1, 0, "§6查看当前入队请求");
		
		Player leader = queuePlayer.get(p);
		
		if(p==leader)
		{
			inv.setItem(9, disband);
			inv.setItem(13, apply);
		}

		inv.setItem(17, leave);
		
		int i = 0;
		for(Player player:queue.get(leader).getPlayers())
		{
			ItemStack item = null;
			if(player==leader)
				item = createItem(397, 1, 3, "§6队长:"+player.getName());
			else
				if(p==leader)
					item = createItem(397, 1, 3, "§6成员:"+player.getName(), "§a右键点击踢出该成员");
				else
					item = createItem(397, 1, 3, "§6成员:"+player.getName());
			inv.setItem(i, item);
			i++;
		}
		
		return inv;
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
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("q"))
		{
			if(args.length==0)
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(!queuePlayer.containsKey(p))
					{
						p.openInventory(findQueueGui(p).get(0));
					}
					else
					{
						p.openInventory(manageQueueGui(p));
					}
				}
				
				return true;
			}
			
			if(args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage("§a=========[队伍系统]=========");
				sender.sendMessage("§a/q §3打开队伍系统界面");
				sender.sendMessage("§a/q help §3查看帮助");
				//sender.sendMessage("§a/q quit §3离开当前队伍");
				//sender.sendMessage("§a/q join [场地名] §3加入目标队伍");
				return true;
			}
			
			/*
			if(args[0].equalsIgnoreCase("quit"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(singlePlayer.containsKey(p))
					{
						String locationName = singlePlayer.get(p);
						groupPlayer.get(locationName).removePlayer(p);
						if(groupPlayer.get(locationName).getPlayers().size()==0)
						{
							groupPlayer.remove(locationName);
						}
						singlePlayer.remove(p);
						p.sendMessage("§a你已退出§5"+singlePlayer.get(p)+"§a的队列");
					}
					else
					{
						p.sendMessage("§a你还没有加入队列");
					}
				}
				return true;
			}
			
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
			
			if(args[0].equalsIgnoreCase("join"))
			{
				if(sender instanceof Player)
				{
					if(challenge.containsKey(args[1]))
					{
						Player p = (Player)sender;
						if(groupPlayer.containsKey(args[1]))
						{
							if(singlePlayer.containsKey(p))
							{
								p.sendMessage("§c你已经在一个队伍中了！");
								return true;
							}
							
							if(levelSystem.getAPI().getLevel(p)<challenge.get(args[1]).getLevel())
							{
								p.sendMessage("§c你的等级不符合要求！");
								return true;
							}
							
							if(challenge.get(args[1]).isStart())
							{
								p.sendMessage("§c该队伍已经开始挑战了！");
								return true;
							}
							
							if(groupPlayer.get(args[1]).getPlayers().size()<limit)
							{
								groupPlayer.get(args[1]).addPlayer(p);
								singlePlayer.put(p, args[1]);
								p.sendMessage("§c你已加入该队伍！");
							}
							else
							{
								p.sendMessage("§c当前队伍已满员！");
							}
						}
						else
						{
							if(singlePlayer.containsKey(p))
							{
								p.sendMessage("§c你已经在一个队伍中了！");
								return true;
							}
							
							if(levelSystem.getAPI().getLevel(p)<challenge.get(args[1]).getLevel())
							{
								p.sendMessage("§c你的等级不符合要求！");
								return true;
							}
							
							if(challenge.get(args[1]).isStart())
							{
								p.sendMessage("§c该队伍已经开始挑战了！");
								return true;
							}
							GroupPlayers gp = new GroupPlayers();
							gp.addPlayer(p);
							groupPlayer.put(args[1], gp);
							singlePlayer.put(p, args[1]);
							p.sendMessage("§c你已加入该队伍！");
						}
					}
				}
				return true;
				
			}
			return true;
			 */
			return true;
		}
		return false;
	}
}


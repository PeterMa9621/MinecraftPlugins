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
			Bukkit.getConsoleSender().sendMessage("��a[Challenge] ��4Valutδ����!");
		}
		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"))
	    {
	    	Bukkit.getConsoleSender().sendMessage("��a[Challenge] ��cHolographicDisplaysδ����");
	    	setEnabled(false);
	    	return;
	    }
		if(hookLevelSystem()==false)
		{
			Bukkit.getConsoleSender().sendMessage("��a[Challenge] ��cLevelSystemδ����");
		}
		getServer().getPluginManager().registerEvents(new ApplyListener(this), this);
		getServer().getPluginManager().registerEvents(new ManageQueueListener(this), this);
		getServer().getPluginManager().registerEvents(new QueueSystemListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[Challenge] ��e��սϵͳ�������");
	}

	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage("��a[Challenge] ��e��սϵͳж�����");
	}
	
	public API getAPI()
	{
		return api;
	}
	
	public void createTeamTag(Player p, Player leader)
	{
		Hologram holo = (Hologram)HologramsAPI.createHologram(this, p.getLocation().add(0.0D, 2.6D, 0.0D));
		holo.appendTextLine("��a��l"+leader.getName()+"��5��l�Ķ���");
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
					member.sendMessage("��6��ҡ�3"+p.getName()+"��6�Ѽ�����飡");
			}
			for(Queue q:this.queue.values())
			{
				if(q.getApplicant().contains(p))
					q.removeApplicant(p);
			}
			if(p.getOpenInventory().getTopInventory().getTitle().contains("��e��lѰ�Ҷ���-ҳ��:"))
			{
				p.closeInventory();
			}
			p.sendMessage("��6���Ѽ����3"+queue.getLeader().getName()+"��6�Ķ��飡");
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
			if(player.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("��e��l�ҵĶ���"))
				player.closeInventory();
			player.sendMessage("��3��Ķ����ѡ�c��ɢ��");
			
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
		if(leader.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("��e��l�ҵĶ���"))
			leader.openInventory(manageQueueGui(leader));
		for(Player player:queue.getPlayers())
		{
			player.sendMessage("��6��ҡ�3"+p.getName()+"��6���˳�����");
		}
		p.sendMessage("��6�����˳���3"+leader.getName()+"��6�Ķ���");
		if(p.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("��e��l�ҵĶ���"))
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
			if(queue.getLeader().getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("��e��l�������"))
				queue.getLeader().openInventory(applyGui(p, queue));
			p.sendMessage("��6���Ѿ���������������");
			return true;
		}
		else
		{
			p.sendMessage("��6���Ѿ����������������б����ˣ���ȴ��ӳ�ͬ��");
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
		
		Inventory inv = Bukkit.createInventory(p, 54, "��e��lѰ�Ҷ���-ҳ��:1");
		
		ItemStack create = createItem(425, 1, 0, "��6�������һ������");
		
		ItemStack next = createItem(351, 1, 13, "��3���������һҳ");
		
		ItemStack previous = createItem(351, 1, 8, "��3���������һҳ");

		inv.setItem(45, create);
		inv.setItem(51, previous);
		inv.setItem(53, next);
		ArrayList<Inventory> list = new ArrayList<Inventory>();
		
		int i = 0;
		for(Player player:this.queue.keySet())
		{

			ItemStack item = createItem(397, 1, 3, "��6�����������3"+player.getName()+"��6�Ķ���", "��a��������:"+queue.get(player).getPlayers().size());

			inv.setItem(i%44, item);
			if(i>43 && i/44==0)
			{
				list.add(inv);
				inv = Bukkit.createInventory(p, 54, "��e��lѰ�Ҷ���-ҳ��:"+(((i+1)/44)+1));
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
		Inventory inv = Bukkit.createInventory(p, 36, "��e��l�������");
		
		ItemStack back = createItem(331, 1, 0, "��f�������");
		
		inv.setItem(35, back);
		
		int i = 0;

		for(Player player:queue.getApplicant())
		{
			ItemStack item = null;
			item = createItem(397, 1, 3, "��6�������:"+player.getName(), "��a���������ܸ����%��c�Ҽ�����ܾ������");
			inv.setItem(i, item);
			i++;
		}
		

		return inv;
	}
	
	public Inventory manageQueueGui(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 18, "��e��l�ҵĶ���");
		
		ItemStack disband = createItem(166, 1, 0, "��6��ɢ��ǰ����");
		ItemStack leave = createItem(330, 1, 0, "��6�뿪��ǰ����");
		ItemStack apply = createItem(386, 1, 0, "��6�鿴��ǰ�������");
		
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
				item = createItem(397, 1, 3, "��6�ӳ�:"+player.getName());
			else
				if(p==leader)
					item = createItem(397, 1, 3, "��6��Ա:"+player.getName(), "��a�Ҽ�����߳��ó�Ա");
				else
					item = createItem(397, 1, 3, "��6��Ա:"+player.getName());
			inv.setItem(i, item);
			i++;
		}
		
		return inv;
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
				sender.sendMessage("��a=========[����ϵͳ]=========");
				sender.sendMessage("��a/q ��3�򿪶���ϵͳ����");
				sender.sendMessage("��a/q help ��3�鿴����");
				//sender.sendMessage("��a/q quit ��3�뿪��ǰ����");
				//sender.sendMessage("��a/q join [������] ��3����Ŀ�����");
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
						p.sendMessage("��a�����˳���5"+singlePlayer.get(p)+"��a�Ķ���");
					}
					else
					{
						p.sendMessage("��a�㻹û�м������");
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
						p.sendMessage("��a�㵱ǰ���ڡ�5"+singlePlayer.get(p)+"��a�Ķ�����");
					}
					else
					{
						p.sendMessage("��a�㻹û�м������");
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
								p.sendMessage("��c���Ѿ���һ���������ˣ�");
								return true;
							}
							
							if(levelSystem.getAPI().getLevel(p)<challenge.get(args[1]).getLevel())
							{
								p.sendMessage("��c��ĵȼ�������Ҫ��");
								return true;
							}
							
							if(challenge.get(args[1]).isStart())
							{
								p.sendMessage("��c�ö����Ѿ���ʼ��ս�ˣ�");
								return true;
							}
							
							if(groupPlayer.get(args[1]).getPlayers().size()<limit)
							{
								groupPlayer.get(args[1]).addPlayer(p);
								singlePlayer.put(p, args[1]);
								p.sendMessage("��c���Ѽ���ö��飡");
							}
							else
							{
								p.sendMessage("��c��ǰ��������Ա��");
							}
						}
						else
						{
							if(singlePlayer.containsKey(p))
							{
								p.sendMessage("��c���Ѿ���һ���������ˣ�");
								return true;
							}
							
							if(levelSystem.getAPI().getLevel(p)<challenge.get(args[1]).getLevel())
							{
								p.sendMessage("��c��ĵȼ�������Ҫ��");
								return true;
							}
							
							if(challenge.get(args[1]).isStart())
							{
								p.sendMessage("��c�ö����Ѿ���ʼ��ս�ˣ�");
								return true;
							}
							GroupPlayers gp = new GroupPlayers();
							gp.addPlayer(p);
							groupPlayer.put(args[1], gp);
							singlePlayer.put(p, args[1]);
							p.sendMessage("��c���Ѽ���ö��飡");
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


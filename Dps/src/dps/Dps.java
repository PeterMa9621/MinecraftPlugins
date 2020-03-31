package dps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import scoreBoard.ScoreBoard;

public class Dps extends JavaPlugin
{
	public static ScoreBoard scoreBoard;
	
	/**
	 *  This hash map is used to store every group's name and its members' data which includes the damage that
	 *  every member did.
	 */
	//HashMap<String, HashMap<String, Double>> groupDps = new HashMap<String, HashMap<String, Double>>();
	
	/**
	 *  This hash map stores every player's name that is using dps module and the value is this player's
	 *  group's name.
	 */
	//HashMap<String, String> singleDps = new HashMap<String, String>();
	
	//ArrayList<String> whoNeedDps = new ArrayList<String>();
	
	/**
	 *  This is used to store which player need to use the scoreboard to show dps information.
	 */
	public ArrayList<String> dpsTask = new ArrayList<String>();
	
	HashMap<String, HashMap<String, Integer>> groupRank = new HashMap<String, HashMap<String, Integer>>();
	
	//DpsAPI api = new DpsAPI(this);
	
	private boolean hookScoreBoard()
    {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("ScoreBoard");
	    scoreBoard = ScoreBoard.class.cast(plugin);
	    return scoreBoard != null; 
	}
	
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		if(!hookScoreBoard())
		{
			Bukkit.getConsoleSender().sendMessage("��a[Dps] ��cScoreBoardδ����");
			return;
		}

		getServer().getPluginManager().registerEvents(new DpsListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[Dps] ��eDps�Ѽ���");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("��a[Dps] ��eDps��ж��");
	}

	/*
	public DpsAPI getAPI()
	{
		return api;
	}

	 */

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
		if (cmd.getName().equalsIgnoreCase("dps"))
		{
			if (args.length==0)
			{
				if(sender.isOp())
				{
					sender.sendMessage("��a=========[DPSϵͳ]=========");
					sender.sendMessage("��a/dps start ��3��������ҵĶ���DPSģʽ");
					sender.sendMessage("��a/dps end ��3�رո���ҵĶ���DPSģʽ");
					sender.sendMessage("��a/dps start [�����] ��3���������DPSģʽ");
					sender.sendMessage("��a/dps close [�����] ��3�رո����DPSģʽ");
					sender.sendMessage("��a/dps put [�����] [������] ��3������ҷ�����һ��������");
				}
				return true;
			}
			/*
			if(args[0].equalsIgnoreCase("put"))
			{
				if(sender.isOp())
				{
					if(args.length==3)
					{
						if(Bukkit.getPlayer(args[1])!=null)
						{
							Player p = Bukkit.getPlayer(args[1]);
							getDps(p, args[2]);
							sender.sendMessage("��c�ѽ���ҷ���"+args[2]+"������");
						}
						else
						{
							sender.sendMessage("��c��Ҳ����ڻ�����");
						}
					}
					else
					{
						sender.sendMessage("��4�÷�����a/dps put [�����] [������] ��3������ҷ�����һ��������");
					}
				}
			}

			if(args[0].equalsIgnoreCase("start"))
			{

				if(args.length==1)
				{

					if(sender.isOp() && sender instanceof Player)
					{
						Player p = (Player)sender;
						if(queueSystem.getAPI().hasTeam(p))
						{
							Player leader = queueSystem.getAPI().getLeader(p);
							Queue queue = queueSystem.getAPI().getQueue(leader);
							getDps(queue);
							if(p.isOp())
							{
								p.sendMessage("��a[DPSϵͳ] ��6�ɹ���");
							}
						}
						else
						{
							p.sendMessage("��a[DPSϵͳ] ��c������ӣ�");
						}
					}

				}
				else if(args.length==2)
				{
					if(sender.isOp() && Bukkit.getPlayer(args[1])!=null)
					{
						Player p = Bukkit.getPlayer(args[1]);
						if(queueSystem.getAPI().isLeader(p))
						{
							Queue queue = queueSystem.getAPI().getQueue(p);
							getDps(queue);
						}
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("end"))
			{
				if(sender.isOp() && sender instanceof Player)
				{
					if(args.length==1)
					{
						Player p = (Player)sender;
						p.sendMessage("��a[DPSϵͳ] ��6���������ڵڡ�5"+getRank(p)+"��6λ");
						removeDps(p);
					}
					else if(args.length==2)
					{
						if(Bukkit.getPlayer(args[1])!=null)
						{
							Player p = Bukkit.getPlayer(args[1]);
							p.sendMessage("��a[DPSϵͳ] ��6���������ڵڡ�5"+getRank(p)+"��6λ");
							removeDps(p);
							sender.sendMessage("");
						}
					}
				}
				return true;
			}


			 */
			/*
			if(args[0].equalsIgnoreCase("open"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						if(Bukkit.getPlayer(args[1])!=null)
						{
							Player p = Bukkit.getPlayer(args[1]);
							getDps(p);
						}
					}
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("close"))
			{
				if(sender.isOp())
				{
					if(args.length==2)
					{
						if(Bukkit.getPlayer(args[1])!=null)
						{
							Player p = Bukkit.getPlayer(args[1]);
							removeDps(p);
						}
					}
				}
				return true;
			}
			*/
			return true;
		}
		return false;
	}

	/*
	public void clearData(Player p)
	{
		stopTask(p);
		if(singleDps.containsKey(p.getName()))
		{
			String teamName = singleDps.get(p.getName());
			if(groupDps.get(teamName).size()==1)
				groupDps.remove(teamName);
			else
				groupDps.get(teamName).remove(p.getName());
			singleDps.remove(p.getName());
		}
	}
	
	public void getDps(Player p, String queueName)
	{
		HashMap<String, Double> memberData = new HashMap<String, Double>();
		if(groupDps.containsKey(queueName))
		{
			memberData = groupDps.get(queueName);
		}
		if(!memberData.containsKey(p.getName()))
		{
			memberData.put(p.getName(), 0D);
			singleDps.put(p.getName(), queueName);
			scoreBoard.getAPI().stopScoreBoard(p);
			runTask(p);
			
			groupDps.put(queueName, memberData);
		}
	}
	
	public void getDps(Queue queue)
	{
		if(queue!=null)
		{	
			Player leader = queue.getLeader();
			ArrayList<Player> members = queue.getPlayers();
			HashMap<String, Double> memberData = new HashMap<String, Double>();
			for(Player p:members)
			{
				memberData.put(p.getName(), 0D);
				singleDps.put(p.getName(), leader.getName());
				scoreBoard.getAPI().stopScoreBoard(p);
				runTask(p);
			}
			groupDps.put(leader.getName(), memberData);	
		}

		if(!whoNeedDps.contains(p.getName()))
		{
			whoNeedDps.add(p.getName());
			singleDps.put(p.getName(), 0D);
			scoreBoard.getAPI().stopScoreBoard(p);
			runTask(p);
		}

	}
	
	public void removeDps(Player p)
	{
		if(singleDps.containsKey(p.getName()))
		{
			String teamName = singleDps.get(p.getName());
			if(groupDps.get(teamName).size()==1)
			{
				groupDps.remove(teamName);
			}
			else
			{
				groupDps.get(teamName).remove(p.getName());
			}
			singleDps.remove(p.getName());
			scoreBoard.getAPI().restartSocreBoard(p);
			stopTask(p);
		}
	}


	
	public int getRank(Player p)
	{
		if(singleDps.containsKey(p.getName()))
		{
			String groupName = singleDps.get(p.getName());
			HashMap<String, Integer> rankData = groupRank.get(groupName);
		    return rankData.get(p.getName());
		}
		return -1;
	}
	
	private void stopTask(Player p)
	{
		if(dpsTask.contains(p.getName()))
		{
			//getServer().getScheduler().cancelTask(dpsTask.get(p.getName()));
			dpsTask.remove(p.getName());
		}
	}
	*/
	/*
	public void removeDps(Queue queue)
	{
		if(queue!=null)
		{
			if(this.groupDps.containsKey(queue))
			{
				Player leader = queue.getLeader();
				ArrayList<Player> members = queue.getPlayers();
				for(Player p:members)
				{
					whoNeedDps.remove(p.getName());
					singleDps.remove(leader.getName());
					scoreBoard.getAPI().restartSocreBoard(p);
					stopTask(p, queue);
				}
				groupDps.remove(leader.getName());
			}
		}
		
		if(!whoNeedDps.contains(p.getName()))
		{
			whoNeedDps.add(p.getName());
			singleDps.put(p.getName(), 0D);
			scoreBoard.getAPI().stopScoreBoard(p);
			runTask(p);
		}
	}
	*/
	
	
	/*
	public void getDps(Player p)
	{
		if(!whoNeedDps.contains(p.getName()))
		{
			whoNeedDps.add(p.getName());
			singleDps.put(p.getName(), 0D);
			scoreBoard.getAPI().stopScoreBoard(p);
			runTask(p);
		}
	}
	
	
	*/
	
	/*
	private void stopTask(Player p, Queue queue)
	{
		if(dpsTask.containsKey(p.getName()))
		{
			getServer().getScheduler().cancelTask(dpsTask.get(p.getName()));
			dpsTask.remove(queue);
		}
	}
	*/
	
	/*
	private void runTask(Player p)
	{
		dpsTask.put(p.getName(), getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			public void run()
			{
				displayDpsBoard(p);
			}
		} ,5*20, 40));
	}
	*/
	/*
	private void runTask(Player p)
	{
		dpsTask.add(p.getName());
		new BukkitRunnable()
		{
			public void run()
			{
				if(dpsTask.contains(p.getName()))
					displayDpsBoard(p);
				else
					cancel();
			}
		}.runTaskTimer(this, 0L, 40L);
	}
	


	 */
	
	/*
	private void displayDpsBoard(Queue queue) 
	{
		if(queue!=null)
		{
			Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

	        Objective randomObjective = scoreboard.registerNewObjective("scoreboard1", "dummy1");
	    	String title = "��fDPS - �����";
			
	    	randomObjective.setDisplayName(title);
	    	randomObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
	    	int number = groupDps.get(queue).size();
	    	if(groupDps.get(queue).size()>15)
	    		number = 15;
	    	List<Map.Entry<String, Double>> list = sort(groupDps.get(queue));
	    	int j=0;
		    for (Map.Entry<String, Double> mapping : list) 
		    {
		    	String content = String.format("��e%s ��f- ��2%.2f", mapping.getKey(), mapping.getValue());

	    		randomObjective.getScore(content).setScore(number-j);
	    		j++;
		    }
		    for(Player p:queue.getPlayers())
		    	p.setScoreboard(scoreboard);
		}
	}
	*/
	
	/*
	private HashMap<Integer, String> reverseMap(HashMap<String, Integer> map)
	{
		HashMap<Integer, String> result = new HashMap<Integer, String>();
		for(String name:map.keySet())
		{
			result.put(map.get(name), name);
		}
		return result;
	}
	
	private int[] sort(int[] numbers)
	{
		for(int i=(numbers.length-1);i>=0;i--)
		{
			int largest = numbers[i];
			for(int x=(numbers.length-1); x>=0; x--)
			{
				if(largest<numbers[x])
				{
					largest = numbers[x];
					numbers[x] = numbers[i];
					numbers[i] = largest;
				}
			}
		}
		return numbers;
	}
	*/
	

	
}

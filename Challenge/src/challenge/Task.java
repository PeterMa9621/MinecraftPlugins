package challenge;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Task 
{
	Challenge plugin;
	
	boolean timeRest = false;
	boolean timeChallenge = false;
	
	public Task(Challenge plugin)
	{
		this.plugin=plugin;
	}
	
	public void timeChallengeStart(String locationName)
	{
		new BukkitRunnable()
		{
    		int time;

			public void run()
			{
				ArrayList<Player> players = plugin.groupPlayer.get(locationName).getPlayers();
				if(!plugin.groupPlayer.containsKey(locationName))
				{
					cancel();
					return;
				}
				if(plugin.groupPlayer.get(locationName).getPlayers().size()==0)
				{
					cancel();
					/**
					 *  Remove this queue of this location when there are no more players in this queue
					 */
					plugin.groupPlayer.remove(locationName);
					/**
					 *  Change the status of this location to false which means this location is ready
					 *  to start again
					 */
					plugin.challenge.get(locationName).changeStart(false);
					Bukkit.getConsoleSender().sendMessage("§a[Challenge] §e"+locationName+"已结束挑战，原因：成员不足");
					return;
				}
					
				if(time==1)
				{
					timeChallenge = true;
					timeRest = false;
					for(Player p:players)
					{
						p.sendTitle("§6挑战将在§510§6秒后开始","§e请做好准备");
					}
				}
				
				if(time==9)
					for(Player p:players)
					{
						p.sendTitle("§c3","");
					}
				
				if(time==10)
					for(Player p:players)
					{
						p.sendTitle("§c2","");
					}

				if(time==11)
				{
					for(Player p:players)
					{
						p.sendTitle("§c1","");
					}
				}
				int number=plugin.groupPlayer.get(locationName).getNumber();
				if(number==plugin.challenge.get(locationName).getLocation().size())
				{
					cancel();
					for(Player p:players)
					{
						p.teleport(plugin.spawn);
					}
					
					int quantity = number/plugin.rewardFactor;
					if(quantity>=1)
					{
						plugin.openRewardGui(players, quantity);
					}

					plugin.groupPlayer.remove(locationName);
					plugin.challenge.get(locationName).changeStart(false);
					for(Player p:players)
					{
						if(quantity<1)
							plugin.singlePlayer.remove(p);
						p.sendTitle("§6挑战成功", "§e你完成了所有挑战");
						p.sendMessage("§6[挑战系统] §e挑战成功,你完成了所有挑战");
					}
					return;
				}
				
				if(timeChallenge==true && timeRest==false && time>11)
				{
					timeChallenge = false;
					timeChallenge(locationName);
				}
				else if(timeChallenge==false && timeRest==true && time>11)
				{
					timeRest = false;
					restTask(locationName, plugin.groupPlayer.get(locationName).getNumber());
				}
				
				time++;
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}
	
	public void timeChallenge(String locationName)
	{
		ArrayList<Player> players = plugin.groupPlayer.get(locationName).getPlayers();
		Location restLocation = plugin.challenge.get(locationName).getRest();
		int extraTime = plugin.groupPlayer.get(locationName).getNumber()*plugin.timeFactor;
		if(plugin.groupPlayer.get(locationName).getNumber()==0)
		{
			for(Player p:players)
			{
				p.teleport(plugin.challenge.get(locationName).getLocation().get(0));
				p.sendTitle("§6挑战开始", "§e请坚持§5"+(60+extraTime)+"§e秒");
				p.sendMessage("§6[挑战系统] §e请坚持§5"+(60+extraTime)+"§e秒");
			}
		}
		else
		{
			for(Player p:players)
			{
				p.sendTitle("§6挑战继续", "§e请坚持§5"+(60+extraTime)+"§e秒");
				p.sendMessage("§6[挑战系统] §e请坚持§5"+(60+extraTime)+"§e秒");
			}
		}
		
		
		new BukkitRunnable()
		{
    		int time;
    		
			public void run()
			{
				if(!plugin.groupPlayer.containsKey(locationName))
				{
					cancel();
					return;
				}
				if(plugin.groupPlayer.get(locationName).getPlayers().size()==0)
				{
					cancel();
					/**
					 *  Remove this queue of this location when there are no more players in this queue
					 */
					plugin.groupPlayer.remove(locationName);
					/**
					 *  Change the status of this location to false which means this location is ready
					 *  to start again
					 */
					plugin.challenge.get(locationName).changeStart(false);
					Bukkit.getConsoleSender().sendMessage("§a[Challenge] §e"+locationName+"已结束挑战，原因：成员不足");
					return;
				}
				
				if(time==55+extraTime)
				{
					for(Player p:players)
					{
						p.sendTitle("3", "");
					}
				}
				else if(time==57+extraTime)
				{
					for(Player p:players)
					{
						p.sendTitle("2", "");
					}
				}
				else if(time==59+extraTime)
				{
					for(Player p:players)
					{
						p.sendTitle("1", "");
					}
				}
				else if(time==60+extraTime)
				{
					for(Player p:players)
					{
						p.teleport(restLocation);
					}
					plugin.groupPlayer.get(locationName).addNumber();

					timeRest = true;
					cancel();
				}
				time++;
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}
	
	public void restTask(String locationName, int number)
	{
		ArrayList<Player> players = plugin.groupPlayer.get(locationName).getPlayers();
		
		for(Player p:players)
		{
			p.sendTitle("§6你已传送到休息区", "");
			p.sendMessage("§6[挑战系统] §e你将有30秒的时间来整顿状态");
		}
		
		new BukkitRunnable()
		{
    		int time;

			public void run()
			{
				if(!plugin.groupPlayer.containsKey(locationName))
				{
					cancel();
					return;
				}
				if(plugin.groupPlayer.get(locationName).getPlayers().size()==0)
				{
					cancel();
					/**
					 *  Remove this queue of this location when there are no more players in this queue
					 */
					plugin.groupPlayer.remove(locationName);
					/**
					 *  Change the status of this location to false which means this location is ready
					 *  to start again
					 */
					plugin.challenge.get(locationName).changeStart(false);
					Bukkit.getConsoleSender().sendMessage("§a[Challenge] §e"+locationName+"已结束挑战，原因：成员不足");
					return;
				}

				if(time==23)
				{
					for(Player p:players)
					{
						p.sendTitle("§6休息时间即将结束", "");
					}
				}
				else if(time==27)
				{
					for(Player p:players)
					{
						p.sendTitle("§c3", "");
					}
				}
				else if(time==28)
				{
					for(Player p:players)
					{
						p.sendTitle("§c2", "");
					}
				}
				else if(time==29)
				{
					for(Player p:players)
					{
						p.sendTitle("§c1", "");
					}
				}
				
				else if(time==30)
				{
					for(Player p:players)
					{
						p.teleport(plugin.challenge.get(locationName).getLocation().get(number));
					}

					timeChallenge = true;
					cancel();
				}
				time++;
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}
}

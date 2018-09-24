package queueSystem;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Queue 
{
	ArrayList<Player> queue = new ArrayList<Player>();
	QueueSystem plugin;
	ArrayList<Player> whoApply = new ArrayList<Player>();
	
	public Queue(QueueSystem plugin, Player leader)
	{
		this.plugin = plugin;
		queue.add(leader);
	}
	
	public boolean add(Player p)
	{
		if(queue.size()==plugin.limit)
		{
			return false;
		}
		else
		{
			queue.add(p);
			return true;
		}
	}
	
	public void remove(Player p)
	{
		if(queue.contains(p))
			queue.remove(p);
	}
	
	public Player getLeader()
	{
		return queue.get(0);
	}
	
	public ArrayList<Player> getPlayers()
	{
		return queue;
	}
	
	public void addApplicant(Player p)
	{
		whoApply.add(p);
	}
	
	public ArrayList<Player> getApplicant()
	{
		return whoApply;
	}
	
	public void removeApplicant(Player p)
	{
		if(whoApply.contains(p))
			whoApply.remove(p);
	}
}

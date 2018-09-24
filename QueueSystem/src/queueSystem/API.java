package queueSystem;

import org.bukkit.entity.Player;

public class API 
{
	QueueSystem plugin;
	public API(QueueSystem plugin)
	{
		this.plugin = plugin;
	}
	
	public Player getLeader(Player p)
	{
		if(plugin.queuePlayer.containsKey(p))
			return plugin.queuePlayer.get(p);
		return null;
	}
	
	public boolean isLeader(Player p)
	{
		if(plugin.queuePlayer.containsKey(p))
			if(plugin.queuePlayer.get(p)==p)
				return true;
		return false;
	}
	
	public Queue getQueue(Player leader)
	{
		if(plugin.queue.containsKey(leader))
			return plugin.queue.get(leader);
		return null;
	}
	
	public boolean hasTeam(Player p)
	{
		if(plugin.queuePlayer.containsKey(p))
			return true;
		return false;
	}
	
	public int getLimit()
	{
		return plugin.limit;
	}
}

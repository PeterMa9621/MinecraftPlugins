package dps;

import java.util.HashMap;

import org.bukkit.entity.Player;

import queueSystem.Queue;

public class DpsAPI 
{
	Dps plugin;
	public DpsAPI(Dps plugin)
	{
		this.plugin = plugin;
	}
	
	public boolean isInDpsModule(Player p)
	{
		if(plugin.singleDps.containsKey(p.getName()))
			return true;
		return false;
	}
	
	public HashMap<String, Double> getGroupDpsData(Player p)
	{
		String groupName = plugin.singleDps.get(p.getName());
		return plugin.groupDps.get(groupName);
	}
	
	public int getRank(Player p)
	{
		return plugin.getRank(p);
	}
	
	public int getDamage(Player p)
	{
		return plugin.getRank(p);
	}
	
	public void exitDpsModule(Player p)
	{
		plugin.removeDps(p);
	}
	
	/*
	public void startDpsModule(Player p)
	{
		if(plugin.queueSystem.getAPI().isLeader(p))
		{
			Queue queue = plugin.queueSystem.getAPI().getQueue(p);
			plugin.getDps(queue);
		}
	}
	*/
	
	public void putIntoOtherQueue(Player p, String queueName)
	{
		plugin.getDps(p, queueName);
	}
	
	public void startDpsModule(Queue queue)
	{
		plugin.getDps(queue);
	}
}

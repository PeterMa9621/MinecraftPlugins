package dps;

import java.util.ArrayList;
import java.util.HashMap;

import com.mysql.fabric.xmlrpc.base.Array;
import dps.rewardBox.RewardBoxManager;
import dps.rewardBox.RewardTable;
import org.bukkit.entity.Player;

public class DpsAPI 
{

	Dps plugin;
	public DpsAPI(Dps plugin)
	{
		this.plugin = plugin;
	}
	
	public RewardTable getRewardTable(String dungeonName)
	{
		return RewardBoxManager.getRewardTable(dungeonName);
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
	/*
	public void putIntoOtherQueue(Player p, String queueName)
	{
		plugin.getDps(p, queueName);
	}
	
	public void startDpsModule(Queue queue)
	{
		plugin.getDps(queue);
	}

	 */
}

package dps.api;

import dps.Dps;
import dps.rewardBox.RewardBoxManager;
import dps.rewardBox.RewardTable;

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
}

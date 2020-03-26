package checkInSystem;

import java.util.UUID;

public class CheckInSystemAPI
{
	CheckInSystem plugin;
	public CheckInSystemAPI(CheckInSystem plugin)
	{
		this.plugin=plugin;
	}
	
	public int getDays(UUID playerID)
	{
		return Integer.parseInt(plugin.playerData.get(playerID).get("days"));
	}
	
	public boolean isCheckIn(UUID playerID)
	{
		return plugin.isCheckIn.get(playerID);
	}
}

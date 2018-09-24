package checkInSystem;

public class CheckInSystemAPI 
{
	CheckInSystem plugin;
	public CheckInSystemAPI(CheckInSystem plugin)
	{
		this.plugin=plugin;
	}
	
	public int getDays(String playerName)
	{
		return Integer.valueOf(plugin.playerData.get(playerName).get("days"));
	}
	
	public boolean isCheckIn(String playerName)
	{
		return plugin.isCheckIn.get(playerName);
	}
}

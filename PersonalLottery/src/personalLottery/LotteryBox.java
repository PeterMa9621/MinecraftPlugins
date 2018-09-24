package personalLottery;

import org.bukkit.Location;

public class LotteryBox 
{
	String playerName = null;
	Location signLocation = null;
	Location dispenserLocation = null;
	public LotteryBox(String playerName, Location signLocation, Location dispenserLocation)
	{
		this.playerName=playerName;
		this.signLocation=signLocation;
		this.dispenserLocation=dispenserLocation;
	}
	
	public String getPlayerName()
	{
		return playerName;
	}
	
	public Location getSignLocation()
	{
		return signLocation;
	}
	
	public Location getDispenserLocation()
	{
		return dispenserLocation;
	}
}

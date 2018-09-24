package mmSpawn;

import org.bukkit.Location;

public class MMobInfo 
{
	String hour = "00";
	String minute = "00";
	Location location;
	boolean spawn = false;
	public MMobInfo(String hour, String minute, Location location)
	{
		this.hour=hour;
		this.minute=minute;
		this.location=location;
	}
	
	public String getHour()
	{
		return hour;
	}
	
	public String getMinute()
	{
		return minute;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public boolean isSpawn()
	{
		return spawn;
	}
	
	public void setSpawn(boolean isSpawn)
	{
		spawn = isSpawn;
	}
}

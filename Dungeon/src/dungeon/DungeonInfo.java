package dungeon;

import java.util.ArrayList;
import org.bukkit.Location;

public class DungeonInfo 
{
	private String name = null;
	private int level = 0;
	private ArrayList<Location> location = new ArrayList<Location>();
	private boolean start = false;
	private ArrayList<KillMobObject> mobObject = new ArrayList<KillMobObject>();
	private String bossName = null;
	
	public DungeonInfo(String name, int level)
	{
		this.level=level;
		this.name=name;
	}
	
	public void setBossName(String bossName)
	{
		this.bossName=bossName;
	}
	
	public String getBossName()
	{
		return bossName;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public void setLevel(int level)
	{
		this.level=level;
	}
	
	public void addLocation(Location newLocation)
	{
		location.add(newLocation);
	}
	
	public ArrayList<Location> getLocation()
	{
		return location;
	}
	
	public void setLocation(ArrayList<Location> newLocation)
	{
		location = newLocation;
	}
	
	public void changeStart(boolean start)
	{
		this.start = start;
	}
	
	public boolean isStart()
	{
		return start;
	}
	
	public void addMobObject(KillMobObject mobObject)
	{
		this.mobObject.add(mobObject);
	}
	
	public ArrayList<KillMobObject> getMobObject()
	{
		return mobObject;
	}
	
	public void setMobObject(ArrayList<KillMobObject> mobObject)
	{
		this.mobObject=mobObject;
	}
}

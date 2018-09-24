package challenge;

import java.util.ArrayList;
import org.bukkit.Location;

public class ChallengeInfo 
{
	private String name = null;
	private int level = 0;
	private ArrayList<Location> location = new ArrayList<Location>();
	private boolean start = false;
	private String type = null;
	private ArrayList<KillMobObject> mobObject = new ArrayList<KillMobObject>();
	private Location rest = null;
	
	public ChallengeInfo(String name, int level, String type)
	{
		this.level=level;
		this.name=name;
		this.type=type;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public void setLevel(int newLevel)
	{
		this.level=newLevel;
	}
	
	public void setRest(Location rest)
	{
		this.rest=rest;
	}
	
	public Location getRest()
	{
		return rest;
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
	
	public String getType()
	{
		return type;
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

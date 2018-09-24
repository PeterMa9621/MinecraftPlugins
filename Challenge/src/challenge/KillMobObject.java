package challenge;

public class KillMobObject 
{
	int mobID = 0;
	int amount = 0;
	String customName = null;
	public KillMobObject(int mobID, int amount)
	{
		this.mobID = mobID;
		this.amount = amount;
	}
	
	public int getMobId()
	{
		return mobID;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public String getCustomName()
	{
		return customName;
	}
	
	public void setCustomName(String newCustomName)
	{
		customName = newCustomName;
	}
}

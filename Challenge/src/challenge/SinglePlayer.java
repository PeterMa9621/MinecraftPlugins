package challenge;

public class SinglePlayer 
{
	String locationName = null;
	int mobID = 0;
	int amount = 0;
	String customName = null;
	int killAmount = 0;
	int number = 0;
	public SinglePlayer(String locationName)
	{
		this.locationName=locationName;
	}
	
	public void setMobID(int mobID)
	{
		this.mobID=mobID;
	}
	
	public void setMobAmount(int amount)
	{
		this.killAmount=0;
		this.amount=amount;
	}
	
	public void setCustomName(String customName)
	{
		this.customName=customName;
	}
	
	public int getMobID()
	{
		return mobID;
	}
	
	public int getMobAmount()
	{
		return amount;
	}
	
	public String getCustomName()
	{
		return customName;
	}
	
	public int getKillAmount()
	{
		return killAmount;
	}
	
	public void addKillAmount()
	{
		killAmount+=1;
	}
	
	public String getLocationName()
	{
		return locationName;
	}
	
	public boolean isFinishKill()
	{
		return (killAmount==amount);
	}
	
	public void addNumber()
	{
		number+=1;
	}
	public int getNumber()
	{
		return number;
	}
}

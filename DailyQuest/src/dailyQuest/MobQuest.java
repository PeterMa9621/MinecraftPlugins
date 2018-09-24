package dailyQuest;

public class MobQuest 
{
	int amountRequested = 0;
	int currentAmount = 0;
	int mobID = 0;
	String mobName = null;
	
	public MobQuest(int amountRequested, int currentAmount, int mobID, String mobName)
	{
		this.amountRequested = amountRequested;
		this.currentAmount = currentAmount;
		this.mobID = mobID;
		this.mobName=mobName;
	}
	
	public MobQuest(int amountRequested, int currentAmount, int mobID)
	{
		this.amountRequested = amountRequested;
		this.currentAmount = currentAmount;
		this.mobID = mobID;
	}
	
	public int getAmountRequested()
	{
		return amountRequested;
	}
	
	public int getCurrentAmount()
	{
		return currentAmount;
	}
	
	public int getMobID()
	{
		return mobID;
	}
	
	public void setCurrentAmount(int newCurrentAmount)
	{
		currentAmount = newCurrentAmount;
	}
	
	public boolean isFinished()
	{
		return (currentAmount==amountRequested);
	}
	
	public String getMobName()
	{
		return mobName;
	}
}

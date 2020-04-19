package dailyQuest.model;

public class MobQuest 
{
	int amountRequested = 0;
	int currentAmount = 0;
	String mobID;
	String mobName = null;
	
	public MobQuest(int amountRequested, int currentAmount, String mobID, String mobName)
	{
		this.amountRequested = amountRequested;
		this.currentAmount = currentAmount;
		this.mobID = mobID.toUpperCase();
		this.mobName=mobName;
	}
	
	public MobQuest(int amountRequested, int currentAmount, String mobID)
	{
		this.amountRequested = amountRequested;
		this.currentAmount = currentAmount;
		this.mobID = mobID.toUpperCase();
	}
	
	public int getAmountRequested()
	{
		return amountRequested;
	}
	
	public int getCurrentAmount()
	{
		return currentAmount;
	}
	
	public String getMobID()
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

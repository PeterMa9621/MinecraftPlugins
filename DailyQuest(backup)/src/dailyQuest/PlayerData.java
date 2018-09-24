package dailyQuest;

public class PlayerData 
{
	/**
	 *  When currentNumber equals to 0, it means this player does not receive any quests.
	 */
	int currentNumber = 0;
	int whatTheQuestIs = 0;
	int totalQuest = 0;
	String lastLogout = null;
	public PlayerData(int currentNumber, int whatTheQuestIs, int totalQuest, String lastLogout)
	{
		this.currentNumber = currentNumber;
		this.whatTheQuestIs = whatTheQuestIs;
		this.totalQuest = totalQuest;
		this.lastLogout = lastLogout;
	}
	
	public PlayerData(int currentNumber, int whatTheQuestIs, int totalQuest)
	{
		this.currentNumber = currentNumber;
		this.whatTheQuestIs = whatTheQuestIs;
		this.totalQuest = totalQuest;
	}
	
	public int getCurrentNumber()
	{
		return currentNumber;
	}
	
	public int getWhatTheQuestIs()
	{
		return whatTheQuestIs;
	}
	
	public int getTotalQuest()
	{
		return totalQuest;
	}
	
	public String getLastLogout()
	{
		return lastLogout;
	}
	
	public void setCurrentNumber(int newNumber)
	{
		currentNumber = newNumber;
	}
	
	public void setWhatTheQuestIs(int newWhatTheQuestIs)
	{
		whatTheQuestIs = newWhatTheQuestIs;
	}
	
	public void setTotalQuest(int newTotalQuest)
	{
		totalQuest = newTotalQuest;
	}
	
	public void setLastLogout(String newLastLogout)
	{
		lastLogout = newLastLogout;
	}
}

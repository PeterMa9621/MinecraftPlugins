package dailyQuest;

import org.bukkit.entity.Player;

public class QuestPlayer
{
	/**
	 *  When currentNumber equals to 0, it means this player does not receive any quests.
	 */
	int currentNumber = 0;
	int whatTheQuestIs = 0;
	int totalQuest = 0;
	String lastLogout = null;
	Player player;
	public QuestPlayer(Player player, int currentNumber, int whatTheQuestIs, int totalQuest, String lastLogout)
	{
		this.player = player;
		this.currentNumber = currentNumber;
		this.whatTheQuestIs = whatTheQuestIs;
		this.totalQuest = totalQuest;
		this.lastLogout = lastLogout;
	}
	
	public QuestPlayer(int currentNumber, int whatTheQuestIs, int totalQuest)
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

	public Player getPlayer() {
		return player;
	}
}

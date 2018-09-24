package dailyQuest;

public class QuestInfo 
{
	Quest quest = null;
	int NPCId = 0;
	int money = 0;
	String describe = "";
	String rewardInfo = "";
	public QuestInfo(Quest quest, int NPCId, int money, String describe, String rewardInfo)
	{
		this.quest=quest;
		this.NPCId=NPCId;
		this.money=money;
		this.describe=describe;
		this.rewardInfo=rewardInfo;
	}
	
	public Quest getQuest()
	{
		return quest;
	}
	
	public int getNPCId()
	{
		return NPCId;
	}
	
	public int getRewardMoney()
	{
		return money;
	}
	
	public String getQuestDescribe()
	{
		return describe;
	}
	
	public String getRewardMessage()
	{
		return rewardInfo;
	}
}

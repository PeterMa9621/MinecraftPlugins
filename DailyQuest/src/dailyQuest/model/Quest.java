package dailyQuest.model;

import dailyQuest.util.Util;

import java.util.Calendar;
import java.util.SplittableRandom;

public class Quest
{
	QuestInfo questInfo = null;
	int NPCId = 0;
	int lowestMoney = 0;
	int highestMoney = 0;
	String describe = "";
	String rewardInfo = "";
	public Quest(QuestInfo questInfo, int NPCId, String moneyString, String describe, String rewardInfo)
	{
		this.questInfo = questInfo;
		this.NPCId=NPCId;
		String[] temp = moneyString.split(":");
		this.lowestMoney = Integer.parseInt(temp[0]);
		this.highestMoney = Integer.parseInt(temp[1]);
		this.describe=describe;
		this.rewardInfo=rewardInfo;
	}
	
	public QuestInfo getQuestInfo()
	{
		return questInfo;
	}
	
	public int getNPCId()
	{
		return NPCId;
	}
	
	public int getRewardMoney() {
		int randomInt = Util.random(lowestMoney) + 1;
		return randomInt + (highestMoney - lowestMoney);
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

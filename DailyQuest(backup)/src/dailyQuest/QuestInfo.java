package dailyQuest;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

public class QuestInfo 
{
	Quest quest = null;
	int NPCId = 0;
	int money = 0;
	ArrayList<ItemStack> item = null;
	String describe = "";
	String rewardInfo = "";
	public QuestInfo(Quest quest, int NPCId, int money, ArrayList<ItemStack> item, String describe, String rewardInfo)
	{
		this.quest=quest;
		this.NPCId=NPCId;
		this.money=money;
		this.item=item;
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
	
	public ArrayList<ItemStack> getRewardItem()
	{
		return item;
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

package dailyQuest.model;

import org.bukkit.inventory.ItemStack;

public class QuestInfo
{
	String type;
	String mobId = null;
	int mobAmount = 0;
	ItemStack item = null;
	String mobName = null;
	
	public QuestInfo(String type, String mobId, int mobAmount)
	{
		this.type = type;
		this.mobId = mobId.toUpperCase();
		this.mobAmount = mobAmount;
	}
	
	public QuestInfo(String type, String mobId, int mobAmount, String mobName)
	{
		this.type = type;
		this.mobId = mobId.toUpperCase();
		this.mobAmount = mobAmount;
		this.mobName = mobName;
	}
	
	public QuestInfo(String type, ItemStack item)
	{
		this.type = type;
		this.item = item;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getMobId() {
		return mobId;
	}
	
	public ItemStack getQuestItem()
	{
		return item;
	}
	
	public int getMobAmount()
	{
		return mobAmount;
	}
	
	public String getMobName()
	{
		return mobName;
	}
}

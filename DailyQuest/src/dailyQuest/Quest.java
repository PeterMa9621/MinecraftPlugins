package dailyQuest;

import org.bukkit.inventory.ItemStack;

public class Quest 
{
	String type;
	String mobId = null;
	int mobAmount = 0;
	ItemStack item = null;
	String mobName = null;
	
	public Quest(String type, String mobId, int mobAmount)
	{
		this.type=type;
		this.mobId=mobId;
		this.mobAmount=mobAmount;
	}
	
	public Quest(String type, String mobId, int mobAmount, String mobName)
	{
		this.type=type;
		this.mobId=mobId;
		this.mobAmount=mobAmount;
		this.mobName=mobName;
	}
	
	public Quest(String type, ItemStack item)
	{
		this.type=type;
		this.item=item;
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

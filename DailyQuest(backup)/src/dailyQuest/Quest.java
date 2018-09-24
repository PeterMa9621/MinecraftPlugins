package dailyQuest;

import org.bukkit.inventory.ItemStack;

public class Quest 
{
	String type = "";
	int mobId = 0;
	ItemStack item = null;
	
	public Quest(String type, int mobId)
	{
		this.type=type;
		this.mobId=mobId;
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
	
	public int getMobId()
	{
		if(mobId==0)
			return -1;
		return mobId;
	}
	
	public ItemStack getQuestItem()
	{
		return item;
	}
}

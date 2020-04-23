package clockGUI;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

public class ClockGuiItem 
{
	ItemStack item = null;
	Function function = null;
	Money money = null;
	ArrayList<String> message = new ArrayList<String>();
	int frequency = 0;
	Boolean hideItem;
	public ClockGuiItem(ItemStack item, Function function, Money money, String message, int frequency, Boolean hideItem)
	{
		this.item = item;
		this.function = function;
		this.money = money;
		if(message!=null && message!="")
		{
			for(String s:message.split("%"))
			{
				this.message.add(s);
			}
		}
		this.frequency = frequency;
		this.hideItem = hideItem;
	}
	
	public int getFrequency()
	{
		return frequency;
	}
	
	public ItemStack getItem()
	{
		return item;
	}
	
	public Function getFunction()
	{
		return function;
	}
	
	public Money getMoney()
	{
		return money;
	}
	
	public ArrayList<String> getMessage()
	{
		return message;
	}

	public boolean isHide() {
		return hideItem;
	}
}

package clockGUI.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class ClockGuiItem 
{
	ItemStack item = null;
	Function function = null;
	Money money = null;
	List<String> message;
	int frequency = 0;
	Boolean hideItem;
	public ClockGuiItem(ItemStack item, Function function, Money money, List<String> message, int frequency, Boolean hideItem)
	{
		this.item = item;
		this.function = function;
		this.money = money;
		this.message = message;
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
	
	public List<String> getMessage() {
		return message;
	}

	public boolean isHide() {
		return hideItem;
	}
}

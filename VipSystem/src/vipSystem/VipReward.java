package vipSystem;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

public class VipReward 
{
	ArrayList<ItemStack> items;
	int money;
	public VipReward(ArrayList<ItemStack> items, int money)
	{
		this.items = items;
		this.money = money;
	}
	
	public ArrayList<ItemStack> getItems()
	{
		return items;
	}
	
	public int getMoney()
	{
		return money;
	}
}

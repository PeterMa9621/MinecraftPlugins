package vipSystem;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

public class VipReward 
{
	private ArrayList<String> commands;
	private int money;
	private int minInventory;
	public VipReward(ArrayList<String> commands, int money, int minInventory)
	{
		this.commands = commands;
		this.money = money;
		this.minInventory = minInventory;
	}
	
	public ArrayList<String> getCommands()
	{
		return commands;
	}
	
	public int getMoney()
	{
		return money;
	}

	public int getMinInventory() {
		return minInventory;
	}
}

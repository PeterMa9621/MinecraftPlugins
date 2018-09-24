package personalLottery;

import java.util.ArrayList;

import org.bukkit.Location;

public class PlayerData 
{
	String playerName;
	ArrayList<LotteryBox> lotteryBox;
	int number = 0;
	public PlayerData(String playerName, ArrayList<LotteryBox> lotteryBox, int number)
	{
		this.playerName=playerName;
		this.lotteryBox=lotteryBox;
		this.number=number;
	}
	
	public ArrayList<LotteryBox> getLotteryBox()
	{
		return lotteryBox;
	}
	
	public void addLotteryBox(LotteryBox newBox)
	{
		lotteryBox.add(newBox);
		addNumber();
	}
	
	public boolean delLotteryBox(Location dispenserLocation)
	{
		for(LotteryBox box:lotteryBox)
		{
			if(box.getDispenserLocation().equals(dispenserLocation))
			{
				lotteryBox.remove(box);
				removeNumber();
				return true;
			}
		}
		return false;
	}
	
	public int getNumber()
	{
		return number;
	}
	
	private void addNumber()
	{
		number+=1;
	}
	
	private void removeNumber()
	{
		if(number>0)
			number-=1;
	}
	
	public boolean isLotteryBoxBySign(Location location)
	{
		for(LotteryBox box:lotteryBox)
		{
			if(box.getSignLocation().equals(location))
				return true;
		}
		return false;
	}
	
	public boolean isLotteryBoxByDispenser(Location location)
	{
		for(LotteryBox box:lotteryBox)
		{
			if(box.getDispenserLocation().equals(location))
				return true;
		}
		return false;
	}
}

package maxHeart;

public class PlayerData 
{
	double extraHeart = 0;
	boolean hasExtraHeart = false;
	
	public PlayerData(double extraHeart)
	{
		this.extraHeart = extraHeart;
	}
	
	public void addExtraHeart(double newHeart)
	{
		extraHeart += newHeart;
	}
	
	public double getExtraHeart()
	{
		return extraHeart;
	}
	
	public boolean hasExtraHeart()
	{
		return hasExtraHeart;
	}
	
	public void changeExtraHeartStatus(boolean status)
	{
		hasExtraHeart = status;
	}
}

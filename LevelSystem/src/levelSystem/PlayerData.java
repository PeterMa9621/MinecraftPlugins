package levelSystem;

public class PlayerData 
{
	private String name = null;
	private Integer level = 0;
	private Integer currentExp = 0;
	private Integer totalExp = 0;
	public PlayerData(String name, Integer level, Integer currentExp, Integer totalExp)
	{
		this.name=name;
		this.level=level;
		this.currentExp=currentExp;
		this.totalExp=totalExp;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public int getCurrentExp()
	{
		return currentExp;
	}
	
	public int getTotalExp()
	{
		return totalExp;
	}
	
	public void setLevel(int newLevel)
	{
		level = newLevel;
	}
	
	public void setCurrentExp(int newCurrentExp)
	{
		currentExp = newCurrentExp;
	}
	
	public void setTotalExp(int newTotalExp)
	{
		totalExp = newTotalExp;
	}
}

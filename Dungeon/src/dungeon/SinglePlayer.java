package dungeon;

public class SinglePlayer 
{
	int lives = 3;
	String locationName = null;
	boolean isBoss = false;
	public SinglePlayer(String locationName)
	{
		this.locationName=locationName;
	}
	
	public String getLocation()
	{
		return locationName;
	}
	
	public void setLives(int newLives)
	{
		lives = newLives;
	}
	
	public void addLives()
	{
		lives++;
	}
	
	public void reduceLives()
	{
		if(lives>0)
			lives--;
	}
	
	public int getLives()
	{
		return lives;
	}
	
	public void setIsBoss(boolean status)
	{
		isBoss = status;
	}
	
	public boolean isBoss()
	{
		return isBoss;
	}
}

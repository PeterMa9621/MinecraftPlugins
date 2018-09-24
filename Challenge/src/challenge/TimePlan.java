package challenge;

public class TimePlan 
{
	private String type;
	private int duration;
	private boolean isStart=false;
	public TimePlan(String type, int duration)
	{
		this.type=type;
		this.duration=duration;
	}
	
	public String getType()
	{
		return type;
	}
	
	public int getDuration()
	{
		return duration;
	}
	
	public boolean isStart()
	{
		return isStart;
	}
	
	public void setIsStart(boolean isStart)
	{
		this.isStart=isStart;
	}
}

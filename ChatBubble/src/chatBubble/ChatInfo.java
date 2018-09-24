package chatBubble;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

public class ChatInfo 
{
	int taskID = 0;
	Hologram gram;
	public ChatInfo(int taskID, Hologram gram)
	{
		this.taskID=taskID;
		this.gram=gram;
	}
	
	public int getTaskID()
	{
		return taskID;
	}
	
	public Hologram getGram()
	{
		return gram;
	}
	
	public void setTaskID(int newID)
	{
		taskID = newID;
	}
	
	public void setHologram(Hologram newHologram)
	{
		gram = newHologram;
	}
}

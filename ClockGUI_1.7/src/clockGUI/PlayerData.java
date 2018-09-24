package clockGUI;

import java.util.HashMap;

public class PlayerData 
{
	HashMap<Integer, HashMap<Integer, Integer>> GuiInfo = new HashMap<Integer, HashMap<Integer, Integer>>();
	
	public int getNumber(int guiNumber, int position)
	{
		if(GuiInfo.containsKey(guiNumber) && GuiInfo.get(guiNumber).containsKey(position))
		{
			return GuiInfo.get(guiNumber).get(position);
		}
		else
		{
			return 0;
		}
	}
	
	public HashMap<Integer, HashMap<Integer, Integer>> getGuiInfo()
	{
		return GuiInfo;
	}
	
	public HashMap<Integer, Integer> getButtonInfo(int guiNumber)
	{
		return GuiInfo.get(guiNumber);
	}
	
	public void setNumber(int guiNumber, int position, int usedNumber)
	{
		HashMap<Integer, Integer> buttonInfo = null;
		if(GuiInfo.containsKey(guiNumber))
		{
			buttonInfo = GuiInfo.get(guiNumber);
		}
		else
		{
			buttonInfo = new HashMap<Integer, Integer>();
		}
		buttonInfo.put(position, usedNumber);
		GuiInfo.put(guiNumber, buttonInfo);
	}
}

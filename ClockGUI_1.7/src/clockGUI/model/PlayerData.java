package clockGUI.model;

import java.util.HashMap;

public class PlayerData 
{
	HashMap<String, HashMap<Integer, Integer>> GuiInfo = new HashMap<>();
	
	public int getNumber(String guiId, int position)
	{
		if(GuiInfo.containsKey(guiId) && GuiInfo.get(guiId).containsKey(position))
		{
			return GuiInfo.get(guiId).get(position);
		}
		else
		{
			return 0;
		}
	}
	
	public HashMap<String, HashMap<Integer, Integer>> getGuiInfo() {
		return GuiInfo;
	}
	
	public HashMap<Integer, Integer> getButtonInfo(String guiId)
	{
		return GuiInfo.get(guiId);
	}
	
	public void setNumber(String guiId, int position, int usedNumber)
	{
		HashMap<Integer, Integer> buttonInfo = null;
		if(GuiInfo.containsKey(guiId)) {
			buttonInfo = GuiInfo.get(guiId);
		}
		else {
			buttonInfo = new HashMap<>();
		}
		buttonInfo.put(position, usedNumber);
		GuiInfo.put(guiId, buttonInfo);
	}
}

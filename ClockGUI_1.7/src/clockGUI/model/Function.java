package clockGUI.model;

import java.util.List;

public class Function 
{
	String functionType = null;

	List<String> content = null;
	boolean shouldCloseGui = false;

	String guiId = "0";
	public Function(String functionType, List<String> content, boolean shouldCloseGui)
	{
		this.functionType=functionType;
		this.shouldCloseGui = shouldCloseGui;
		this.content=content;
	}
	
	public Function(String functionType, String guiId)
	{
		this.functionType=functionType;

		this.guiId = guiId;
	}
	
	public Function(String functionType, String guiId, List<String> content)
	{
		this.functionType=functionType;

		this.guiId = guiId;
		
		this.content=content;
	}
	
	public String getType()
	{
		return functionType;
	}

	
	public List<String> getCommand()
	{
		return content;
	}
	
	public String getGuiId()
	{
		return guiId;
	}

	public boolean shouldCloseGui() {
		return shouldCloseGui;
	}
}

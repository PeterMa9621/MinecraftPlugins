package clockGUI;

import java.util.ArrayList;

public class Function 
{
	String functionType = null;

	ArrayList<String> content = null;
	
	int guiNumber = 0;
	public Function(String functionType, ArrayList<String> content)
	{
		this.functionType=functionType;

		this.content=content;
	}
	
	public Function(String functionType, int guiNumber)
	{
		this.functionType=functionType;

		this.guiNumber=guiNumber;
	}
	
	public Function(String functionType, int guiNumber, ArrayList<String> content)
	{
		this.functionType=functionType;

		this.guiNumber=guiNumber;
		
		this.content=content;
	}
	
	public String getType()
	{
		return functionType;
	}

	
	public ArrayList<String> getCommand()
	{
		return content;
	}
	
	public int getGuiNumber()
	{
		return guiNumber;
	}
}

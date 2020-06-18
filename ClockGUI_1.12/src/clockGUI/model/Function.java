package clockGUI.model;

import java.util.List;

public class Function 
{
	String functionType = null;

	List<String> content = null;
	boolean shouldCloseGui = false;
	boolean runAsOp;

	String guiId = "0";
	public Function(String functionType, List<String> content, boolean shouldCloseGui, boolean runAsOp)
	{
		this.functionType=functionType;
		this.shouldCloseGui = shouldCloseGui;
		this.content=content;
		this.runAsOp = runAsOp;
	}
	
	public Function(String functionType, String guiId, boolean runAsOp)
	{
		this.functionType=functionType;
		this.runAsOp = runAsOp;
		this.guiId = guiId;
	}
	
	public Function(String functionType, String guiId, List<String> content, boolean runAsOp)
	{
		this.functionType=functionType;
		this.runAsOp = runAsOp;
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

	public boolean shouldRunAsOp() {
		return runAsOp;
	}
}

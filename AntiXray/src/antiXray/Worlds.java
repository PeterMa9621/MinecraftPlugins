package antiXray;

import java.util.HashMap;

public class Worlds 
{
	String worldName = "";
	HashMap<Integer, Integer> blockInfo = new HashMap<Integer, Integer>();
	int heightLimit = 0;
	boolean recoverOnUse = false;
	HashMap<Integer, Integer> recoverInfo = new HashMap<Integer, Integer>();
	public Worlds(String worldName, HashMap<Integer, Integer> blockInfo, int heightLimit,boolean recoverOnUse , HashMap<Integer, Integer> recoverInfo)
	{
		this.worldName=worldName;
		this.blockInfo=blockInfo;
		this.heightLimit=heightLimit;
		this.recoverOnUse=recoverOnUse;
		this.recoverInfo=recoverInfo;
	}
	
	public String getWorldName()
	{
		return worldName;
	}
	
	public HashMap<Integer, Integer> getBlockInfo()
	{
		return blockInfo;
	}
	
	public int getHeightLimit()
	{
		return heightLimit;
	}
	
	public boolean getRecoverOnUse()
	{
		return recoverOnUse;
	}
	
	public HashMap<Integer, Integer> getRecoverInfo()
	{
		return recoverInfo;
	}
}

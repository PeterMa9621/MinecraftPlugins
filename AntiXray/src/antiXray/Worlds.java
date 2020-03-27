package antiXray;


import org.bukkit.Material;

import java.util.HashMap;

public class Worlds 
{
	String worldName = "";
	HashMap<Material, Integer> blockInfo;
	int heightLimit = 0;
	boolean recoverOnUse = false;
	HashMap<Material, Integer> recoverInfo;
	public Worlds(String worldName, HashMap<Material, Integer> blockInfo, int heightLimit,boolean recoverOnUse , HashMap<Material, Integer> recoverInfo)
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
	
	public HashMap<Material, Integer> getBlockInfo()
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
	
	public HashMap<Material, Integer> getRecoverInfo()
	{
		return recoverInfo;
	}
}

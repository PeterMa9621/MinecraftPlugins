package dungeon;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class GroupPlayers 
{
	ArrayList<Player> players;

	int mobID = 0;
	int amount = 0;
	String customName = null;
	int killAmount = 0;
	int number = 0;
	
	public GroupPlayers()
	{
		players = new ArrayList<Player>();
	}
	
	public ArrayList<Player> getPlayers()
	{
		return players;
	}
	
	public void addPlayer(Player newPlayer)
	{
		players.add(newPlayer);
	}
	
	public void setPlayer(ArrayList<Player> newPlayers)
	{
		players = newPlayers;
	}
	
	public void clearPlayer()
	{
		players.clear();
	}
	
	public void removePlayer(Player p)
	{
		if(players.contains(p))
			players.remove(p);
	}
	
	public void setMobID(int mobID)
	{
		this.mobID=mobID;
	}
	
	public void setMobAmount(int amount)
	{
		this.killAmount=0;
		this.amount=amount;
	}
	
	public void setCustomName(String customName)
	{
		this.customName=customName;
	}
	
	public int getMobID()
	{
		return mobID;
	}
	
	public int getMobAmount()
	{
		return amount;
	}
	
	public String getCustomName()
	{
		return customName;
	}
	
	public int getKillAmount()
	{
		return killAmount;
	}
	
	public void addKillAmount()
	{
		killAmount+=1;
	}
	
	public boolean isFinishKill()
	{
		return (killAmount==amount);
	}
	
	public void addNumber()
	{
		number++;
	}
	
	public int getNumber()
	{
		return number;
	}
}

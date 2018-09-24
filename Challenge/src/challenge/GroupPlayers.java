package challenge;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class GroupPlayers 
{
	ArrayList<Player> players = new ArrayList<Player>();
	int number = 0;
	
	public int getNumber()
	{
		return number;
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
	
	public void addNumber()
	{
		number+=1;
	}
	
	public void removePlayer(Player p)
	{
		if(players.contains(p))
			players.remove(p);
	}
}

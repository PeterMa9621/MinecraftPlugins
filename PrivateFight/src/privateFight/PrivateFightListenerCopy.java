package privateFight;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

public class PrivateFightListenerCopy implements Listener
{
	private PrivateFight plugin;

	public PrivateFightListenerCopy(PrivateFight plugin)
	{
		this.plugin=plugin;
	}

	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event)
	{
		if(plugin.allFightingPlayers.containsKey(event.getPlayer().getName()))
		{
			Player p = event.getPlayer();
			String locationName = plugin.allFightingPlayers.get(p.getName());
			int index = plugin.fightPlayers.get(locationName).indexOf(p.getName());
			Player otherPlayer = Bukkit.getServer().getPlayer(plugin.fightPlayers.get(locationName).get(1-index));
			plugin.leaveFight(locationName, otherPlayer, p);
			otherPlayer.sendMessage("§a[决斗系统] §c对手离开了游戏,决斗结束");
		}
	}
	
	@EventHandler
	private void onPlayerClickSign(PlayerInteractEvent event)
	{
		if(plugin.setLocationPlayer7.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			
			if(event.getClickedBlock().getType().equals(Material.WALL_SIGN))
			{
				String locationName = plugin.setLocationPlayer7.get(p.getName());
				
				HashMap<String, Location> allLocation = setSign(event, p, plugin.location.get(locationName), "player2Ready", locationName);

				plugin.location.put(locationName, allLocation);
				
				p.sendMessage("§a[决斗系统] §3玩家2的准备牌子已设置");
				p.sendMessage("§a[决斗系统] §3决斗场地§c"+locationName+"§3已设置完成");
				plugin.setLocationPlayer7.remove(p.getName());
			}
			
			else
			{
				p.sendMessage("§a[决斗系统] §c这个物体不是一个牌子");
				p.sendMessage("§a[决斗系统] §6请设置玩家2的准备牌子(请右键目标牌子)");
				return;
			}
		}
		else if(plugin.setLocationPlayer6.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			
			if(event.getClickedBlock().getType().equals(Material.WALL_SIGN))
			{
				String locationName = plugin.setLocationPlayer6.get(p.getName());
				
				HashMap<String, Location> allLocation = setSign(event, p, plugin.location.get(locationName), "player1Ready", locationName);
				
				plugin.location.put(locationName, allLocation);
				
				plugin.setLocationPlayer7.put(p.getName(), locationName);
				p.sendMessage("§a[决斗系统] §3玩家1的准备牌子已设置");
				p.sendMessage("§a[决斗系统] §6请设置玩家2的准备牌子(请右键目标牌子)");
				//p.sendMessage("§a[决斗系统] §3决斗场地§c"+plugin.setLocationPlayer3.get(p.getName())+"§3已设置完成");
				plugin.setLocationPlayer6.remove(p.getName());
			}
			
			else
			{
				p.sendMessage("§a[决斗系统] §c这个物体不是一个牌子");
				p.sendMessage("§a[决斗系统] §6请设置玩家1的准备牌子(请右键目标牌子)");
				return;
			}
		}

		if(event.getAction()!=Action.RIGHT_CLICK_BLOCK)
			return;

		if(event.getClickedBlock().getType().equals(Material.WALL_SIGN))
		{
			Sign sign = (Sign)event.getClickedBlock().getState();
			if(sign.getLines().length==0)
				return;

			if(sign.getLine(0).equalsIgnoreCase("§a[player1Ready]") ||
					sign.getLine(0).equalsIgnoreCase("§a[player2Ready]"))
			{
				Player p = event.getPlayer();
				String locationName = sign.getLine(1).substring(2);
				
				if(plugin.fightPlayers.get(locationName)==null)
					return;
				if(!plugin.fightPlayers.get(locationName).contains(p.getName()))
					return;

				String state = sign.getLine(3).substring(4);
				Location otherSignLocation = null, player1Fight = null, player2Fight = null;

				if(state.equalsIgnoreCase("已准备"))
				{
					return;
				}
				else if(state.equalsIgnoreCase("未准备"))
				{
					sign.setLine(3, "§a§l已准备");
					sign.update();
				}
				if(sign.getLine(0).equalsIgnoreCase("§a[player1Ready]"))
				{
					otherSignLocation = plugin.location.get(locationName).get("player2Ready");
				}
				else if(sign.getLine(0).equalsIgnoreCase("§a[player2Ready]"))
				{
					otherSignLocation = plugin.location.get(locationName).get("player1Ready");
				}
				
				player1Fight = plugin.location.get(locationName).get("player1Fight");
				player2Fight = plugin.location.get(locationName).get("player2Fight");
				Sign otherSign = (Sign)p.getWorld().getBlockAt(otherSignLocation).getState();
				if(otherSign.getLine(3).equalsIgnoreCase("§a§l已准备"))
				{
					if(sign.getLine(0).equalsIgnoreCase("§a[player1Ready]"))
					{
						p.teleport(player1Fight);
						if(plugin.fightPlayers.get(locationName).get(0).equalsIgnoreCase(p.getName()))
						{
							Bukkit.getServer().getPlayer(plugin.fightPlayers.get(locationName).get(1)).teleport(player2Fight);
						}
						else
						{
							Bukkit.getServer().getPlayer(plugin.fightPlayers.get(locationName).get(0)).teleport(player2Fight);
						}
					}
					else if(sign.getLine(0).equalsIgnoreCase("§a[player2Ready]"))
					{
						p.teleport(player2Fight);
						if(plugin.fightPlayers.get(locationName).get(0).equalsIgnoreCase(p.getName()))
						{
							Bukkit.getServer().getPlayer(plugin.fightPlayers.get(locationName).get(1)).teleport(player1Fight);
						}
						else
						{
							Bukkit.getServer().getPlayer(plugin.fightPlayers.get(locationName).get(0)).teleport(player1Fight);
						}
					}
				}
			}
		}
		
	}
	
	public HashMap<String, Location> setSign(PlayerInteractEvent event, Player p, 
			HashMap<String, Location> previousLocation, String locationType, String locationName)
	{
		double x = event.getClickedBlock().getX();
		double y = event.getClickedBlock().getY();
		double z = event.getClickedBlock().getZ();

		Location l = new Location(p.getWorld(), x, y, z);
		
		Sign sign = (Sign)event.getClickedBlock().getState();
		sign.setLine(0, "§a["+locationType+"]");
		sign.setLine(1, "§3"+locationName);
		sign.setLine(2, "§5右键我准备");
		sign.setLine(3, "§4§l未准备");
		sign.update();

		HashMap<String, Location> allLocation = previousLocation;
		allLocation.put(locationType, l);
		
		return allLocation;
	}
	
	public HashMap<String, Location> setLocation(AsyncPlayerChatEvent event, Player p, 
			HashMap<String, Location> previousLocation, String locationType)
	{
		double x = event.getPlayer().getLocation().getX();
		double y = event.getPlayer().getLocation().getY();
		double z = event.getPlayer().getLocation().getZ();
		
		double eyeX = event.getPlayer().getLocation().getDirection().getX();
		double eyeY = event.getPlayer().getLocation().getDirection().getY();
		double eyeZ = event.getPlayer().getLocation().getDirection().getZ();

		Location l = new Location(p.getWorld(), x, y, z);
		Vector v = new Vector(eyeX, eyeY, eyeZ);
		l.setDirection(v);
		HashMap<String, Location> allLocation = previousLocation;
		allLocation.put(locationType, l);
		
		return allLocation;
	}

	public HashMap<String, Location> setLocation(AsyncPlayerChatEvent event, Player p, String locationType)
	{
		double x = event.getPlayer().getLocation().getX();
		double y = event.getPlayer().getLocation().getY();
		double z = event.getPlayer().getLocation().getZ();
		
		double eyeX = event.getPlayer().getLocation().getDirection().getX();
		double eyeY = event.getPlayer().getLocation().getDirection().getY();
		double eyeZ = event.getPlayer().getLocation().getDirection().getZ();

		Location l = new Location(p.getWorld(), x, y, z);
		Vector v = new Vector(eyeX, eyeY, eyeZ);
		l.setDirection(v);
		HashMap<String, Location> allLocation = new HashMap<String, Location>();
		allLocation.put(locationType, l);
		
		return allLocation;
	}
	
	@EventHandler
	private void onPlayerChat(AsyncPlayerChatEvent event)
	{
		if(plugin.setLocationPlayer5.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			if(event.getMessage().equalsIgnoreCase("exit"))
			{
				plugin.setLocationPlayer5.remove(p.getName());
				p.sendMessage("§a[决斗系统] §c已结束添加场地信息");
				return;
			}
			
			else if(event.getMessage().equalsIgnoreCase("add"))
			{
				String locationName = plugin.setLocationPlayer5.get(p.getName());
				
				HashMap<String, Location> allLocation = setLocation(event, p, plugin.location.get(locationName), "player2Fight");

				plugin.location.put(locationName, allLocation);
				
				plugin.setLocationPlayer6.put(p.getName(), locationName);
				p.sendMessage("§a[决斗系统] §3玩家2战斗出生点已设置");
				p.sendMessage("§a[决斗系统] §6请设置玩家1的准备牌子(请右键目标牌子)");
				//p.sendMessage("§a[决斗系统] §3决斗场地§c"+plugin.setLocationPlayer3.get(p.getName())+"§3已设置完成");
				plugin.setLocationPlayer5.remove(p.getName());
			}
			
			else
			{
				p.sendMessage("§a[决斗系统] §6请设置玩家2战斗出生地,输入add添加(输入exit退出)");
			}
		}
		else if(plugin.setLocationPlayer4.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			if(event.getMessage().equalsIgnoreCase("exit"))
			{
				plugin.setLocationPlayer4.remove(p.getName());
				p.sendMessage("§a[决斗系统] §c已结束添加场地信息");
				return;
			}
			
			else if(event.getMessage().equalsIgnoreCase("add"))
			{
				String locationName = plugin.setLocationPlayer4.get(p.getName());
				HashMap<String, Location> allLocation = setLocation(event, p, plugin.location.get(locationName), "player1Fight");

				plugin.location.put(locationName, allLocation);
				
				plugin.setLocationPlayer5.put(p.getName(), locationName);
				p.sendMessage("§a[决斗系统] §3玩家1战斗出生点已设置");
				p.sendMessage("§a[决斗系统] §6请设置玩家2战斗出生地,输入add添加(输入exit退出)");
				//p.sendMessage("§a[决斗系统] §3决斗场地§c"+plugin.setLocationPlayer3.get(p.getName())+"§3已设置完成");
				plugin.setLocationPlayer4.remove(p.getName());
			}
			
			else
			{
				p.sendMessage("§a[决斗系统] §6请设置玩家1战斗出生地,输入add添加(输入exit退出)");
			}
		}
		else if(plugin.setLocationPlayer3.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			if(event.getMessage().equalsIgnoreCase("exit"))
			{
				plugin.setLocationPlayer3.remove(p.getName());
				p.sendMessage("§a[决斗系统] §c已结束添加场地信息");
				return;
			}
			
			else if(event.getMessage().equalsIgnoreCase("add"))
			{
				String locationName = plugin.setLocationPlayer3.get(p.getName());
				HashMap<String, Location> allLocation = setLocation(event, p, plugin.location.get(locationName), "audienceLobby");

				plugin.location.put(locationName, allLocation);
				
				plugin.setLocationPlayer4.put(p.getName(), locationName);
				p.sendMessage("§a[决斗系统] §3观众场地已设置");
				p.sendMessage("§a[决斗系统] §6请设置玩家1战斗出生地,输入add添加(输入exit退出):");
				//p.sendMessage("§a[决斗系统] §3决斗场地§c"+plugin.setLocationPlayer3.get(p.getName())+"§3已设置完成");
				plugin.setLocationPlayer3.remove(p.getName());
			}
			
			else
			{
				p.sendMessage("§a[决斗系统] §6请前往玩家2的传送场地,并输入add添加(输入exit退出)");
			}
			
		}
		else if(plugin.setLocationPlayer2.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			if(event.getMessage().equalsIgnoreCase("exit"))
			{
				plugin.setLocationPlayer2.remove(p.getName());
				p.sendMessage("§a[决斗系统] §c已结束添加场地信息");
				return;
			}
			
			else if(event.getMessage().equalsIgnoreCase("add"))
			{
				String locationName = plugin.setLocationPlayer2.get(p.getName());
				HashMap<String, Location> allLocation = setLocation(event, p, plugin.location.get(locationName), "player2Lobby");

				plugin.location.put(locationName, allLocation);
				
				plugin.setLocationPlayer3.put(p.getName(), locationName);
				plugin.setLocationPlayer2.remove(p.getName());
				p.sendMessage("§a[决斗系统] §3场地2已设置");
				p.sendMessage("§a[决斗系统] §6请前往观众的传送场地,并输入add添加(输入exit退出)");
				
			}
			
			else
			{
				p.sendMessage("§a[决斗系统] §6请前往玩家2的传送场地,并输入add添加(输入exit退出)");
			}
			
		}
		else if(plugin.setLocationPlayer1.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			if(event.getMessage().equalsIgnoreCase("exit"))
			{
				plugin.setLocationPlayer1.remove(p.getName());
				p.sendMessage("§a[决斗系统] §c已结束添加场地信息");
				return;
			}
			
			else if(event.getMessage().equalsIgnoreCase("add"))
			{
				String locationName = plugin.setLocationPlayer1.get(p.getName());
				HashMap<String, Location> allLocation = setLocation(event, p, "player1Lobby");

				plugin.location.put(locationName, allLocation);

				p.sendMessage("§a[决斗系统] §3场地1已设置");
				plugin.setLocationPlayer2.put(p.getName(), locationName);
				plugin.setLocationPlayer1.remove(p.getName());
				p.sendMessage("§a[决斗系统] §6请前往玩家2的传送场地,并输入add添加(输入exit退出)");
			}
			
			else
			{
				p.sendMessage("§a[决斗系统] §6请前往玩家1的传送场地,并输入add添加(输入exit退出)");
			}
			
		}
	}
	
}

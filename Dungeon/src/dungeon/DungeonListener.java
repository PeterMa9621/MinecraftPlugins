package dungeon;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import queueSystem.LeaveQueueEvent;

public class DungeonListener implements Listener
{
	private Dungeon plugin;
	
	ArrayList<Player> monster = new ArrayList<Player>();
	
	HashMap<Player, Location> respawn = new HashMap<Player, Location>();
	
	public DungeonListener(Dungeon plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onKillMobSetting(AsyncPlayerChatEvent event)
    {
		if(plugin.setKillMob.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			DungeonInfo ch = plugin.challenge.get(plugin.setKillMob.get(p.getName()));
			//boolean monster = false;
			if(event.getMessage().equalsIgnoreCase("add") && (!monster.contains(p)))
			{
				ch.addLocation(p.getLocation());
				monster.add(p);
				
				if(monster.contains(p))
				{
					p.sendMessage("§6[挑战系统] §a请输入怪物ID,怪物数量,怪物自定义名字(用逗号隔开)(exit退出):");
				}
			}
			else if(event.getMessage().equalsIgnoreCase("exit"))
			{
				plugin.setKillMob.remove(p.getName());
				p.sendMessage("§6[挑战系统] §a已退出设置，共设置"+ch.getLocation().size()+"层");
			}
			
			else if(monster.contains(p))
			{
				if(event.getMessage().split(",").length==3)
				{
					int mobID = Integer.valueOf(event.getMessage().split(",")[0]);
					int amount = Integer.valueOf(event.getMessage().split(",")[1]);
					String customName = event.getMessage().split(",")[2];

					customName = customName.replaceAll("&", "§");
					
					KillMobObject mobObject = new KillMobObject(mobID, amount*2);
					mobObject.setCustomName(customName);
					ch.addMobObject(mobObject);

					p.sendMessage("§6[挑战系统] §a设置第"+(ch.getLocation().size()+1)+"层的传送点，输入add添加(exit退出)");
					monster.remove(p);
				}
				else if(event.getMessage().split(",").length==2)
				{
					int mobID = Integer.valueOf(event.getMessage().split(",")[0]);
					int amount = Integer.valueOf(event.getMessage().split(",")[1]);
					KillMobObject mobObject = new KillMobObject(mobID, amount*2);
					ch.addMobObject(mobObject);

					p.sendMessage("§6[挑战系统] §a设置第"+(ch.getLocation().size()+1)+"层的传送点，输入add添加(exit退出)");
					monster.remove(p);
				}
				else if(event.getMessage().split(",").length==1)
				{
					p.sendMessage("§6[挑战系统] §a请输入怪物ID,怪物数量,怪物自定义名字(用逗号隔开)(exit退出):");
				}
			}
		}
		return;
    }
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
    {
		if(plugin.singlePlayer.containsKey(event.getPlayer()))
		{
			event.getPlayer().teleport(plugin.spawn);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp "+event.getPlayer().getName()+" "+plugin.spawn.getBlockX()+" "+plugin.spawn.getBlockY()+" "+plugin.spawn.getBlockZ());
			
			String locationName = plugin.singlePlayer.get(event.getPlayer()).getLocation();
			//Bukkit.getConsoleSender().sendMessage("§a11111");
			plugin.groupPlayer.get(locationName).removePlayer(event.getPlayer());
			if(plugin.groupPlayer.get(locationName).getPlayers().isEmpty())
			{
				plugin.groupPlayer.remove(locationName);
				plugin.challenge.get(locationName).changeStart(false);
			}

			//Bukkit.getConsoleSender().sendMessage("§a22222");
			plugin.singlePlayer.remove(event.getPlayer());
		}
    }
	
	@EventHandler
	public void onPlayerDeath(EntityDeathEvent event)
    {
		if(!(event.getEntity() instanceof Player))
			return;
		Player p = (Player)event.getEntity();
		if(plugin.singlePlayer.containsKey(p))
		{
			//Bukkit.getConsoleSender().sendMessage("§a1111111111");
			Location DeathLocation = p.getLocation();
			if((plugin.singlePlayer.get(p).getLives()-1)>0)
			{
				respawn.put(p, DeathLocation);
				//Bukkit.getConsoleSender().sendMessage("§a222222");
			}
			else if(plugin.singlePlayer.get(p).getLives()==-1)
				return;
			else
			{
				p.sendTitle("§c你在副本中失败了", "§c你没能继续活下去");
			}
			//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp "+p.getName()+" "+plugin.spawn.getBlockX()+" "+plugin.spawn.getBlockY()+" "+plugin.spawn.getBlockZ());
			//plugin.startPlayer.put(p.getName(), -1);
		}
    }
	
	
	@EventHandler
	private void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if(plugin.singlePlayer.containsKey(event.getPlayer()))
		{
			//Bukkit.getConsoleSender().sendMessage("§a333333333");
			Player p = event.getPlayer();
			if(respawn.containsKey(p))
			{
				//Bukkit.getConsoleSender().sendMessage("§a444444444444");
				new BukkitRunnable()
				{
		    		int time;
					public void run()
					{
						if(time>1)
						{
							//Bukkit.getConsoleSender().sendMessage("§a555555555");
							p.teleport(respawn.get(p));
							
							plugin.singlePlayer.get(p).reduceLives();
							respawn.remove(p);
							p.sendTitle("§6你复活了", "§6你还剩§5"+plugin.singlePlayer.get(p).getLives()+"§6条命");
							cancel();
						}
						time++;
					}
				}.runTaskTimer(plugin, 0L, 20L);
			}
			else if(plugin.singlePlayer.get(p).isBoss==false && (!respawn.containsKey(p)))
			{
				new BukkitRunnable()
				{
		    		int time;
					public void run()
					{
						if(time>1)
						{
							p.teleport(plugin.spawn);
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp "+p.getName()+" "+plugin.spawn.getBlockX()+" "+plugin.spawn.getBlockY()+" "+plugin.spawn.getBlockZ());
							String locationName = plugin.singlePlayer.get(p).getLocation();
							plugin.groupPlayer.get(locationName).removePlayer(p);
							if(plugin.groupPlayer.get(locationName).getPlayers().isEmpty())
							{
								plugin.groupPlayer.remove(locationName);
								plugin.challenge.get(locationName).changeStart(false);
							}

							plugin.singlePlayer.remove(p);
							plugin.dps.getAPI().exitDpsModule(p);
							respawn.remove(p.getName());
							cancel();
						}
						time++;
					}
				}.runTaskTimer(plugin, 0L, 20L);
			}
		}
	}
	
	
	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase(plugin.rewardGuiName))
		{
			event.setCancelled(true);
			if(event.getRawSlot()<event.getInventory().getSize() &&
					event.getRawSlot()>=0)
			{
				if(event.getInventory().getItem(event.getRawSlot())!=null && 
						event.getInventory().getItem(event.getRawSlot()).equals(plugin.getReward))
				{
					Player p = (Player)event.getWhoClicked();

					String locationName = plugin.singlePlayer.get(p).getLocation();
					
					ItemStack reward = plugin.getReward(locationName);
					event.getInventory().setItem(event.getRawSlot(), reward);
					if(reward!=null)
					{
						if(reward.getItemMeta().hasDisplayName() && 
								reward.getItemMeta().getDisplayName().contains("§5金币奖励:"))
						{
							double money = Double.valueOf(reward.getItemMeta().getDisplayName().split(":")[1]);

							plugin.economy.depositPlayer(p.getName(), money);
							p.sendMessage("§6你已获得§5"+money+"§6金钱奖励。");
							return;
						}
						
						if(p.getInventory().firstEmpty()==-1)
						{
							p.getWorld().dropItem(p.getLocation(), reward);
						}
						else
							p.getInventory().addItem(reward);
					}
				}
			}
		}
		else if(event.getInventory().getTitle().equalsIgnoreCase("§5选择副本"))
		{
			event.setCancelled(true);
			if(event.getRawSlot()>=0 && event.getRawSlot()<=53)
			{
				if(event.getInventory().getItem(event.getRawSlot())!=null)
				{
					String locationName = event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().substring(2);
					Player p = (Player)event.getWhoClicked();
					plugin.startDungeon(p, locationName);
					p.closeInventory();
				}
			}
		}
	}
	
	/*
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerLeaveQueue(LeaveQueueEvent event)
	{
		String locationName = null;
		if(plugin.singlePlayer.containsKey(event.getPlayer()))
		{
			locationName = plugin.singlePlayer.get(event.getPlayer()).getLocation();
			if(!plugin.challenge.get(locationName).isStart())
				plugin.leaveChallenge(event.getPlayer(), event.isPlayerLeader());
		}
	}
	*/
	
	@EventHandler
	private void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		if(plugin.singlePlayer.containsKey(event.getPlayer()) && (!event.getPlayer().isOp()))
		{
			String locationName = plugin.singlePlayer.get(event.getPlayer()).getLocation();
			if(plugin.challenge.get(locationName).isStart() && 
					plugin.singlePlayer.get(event.getPlayer()).isBoss==false)
			{
				event.setCancelled(true);
				Player p = (Player)event.getPlayer();
				p.sendMessage("§a[副本系统] §c在副本中你无法使用该指令");
				return;
			}
			else if(plugin.singlePlayer.get(event.getPlayer()).isBoss==true)
			{
				event.setCancelled(true);
				Player p = (Player)event.getPlayer();
				p.sendMessage("§a[副本系统] §c在副本中你无法使用该指令");
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerCloseGUI(InventoryCloseEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase(plugin.rewardGuiName))
		{
			if(plugin.singlePlayer.containsKey(event.getPlayer()))
				plugin.singlePlayer.remove(event.getPlayer());
		}
	}
}

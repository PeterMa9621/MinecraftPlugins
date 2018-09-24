package challenge;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

public class ChallengeListener implements Listener
{
	private Challenge plugin;
	
	ArrayList<Player> monster = new ArrayList<Player>();
	
	public ChallengeListener(Challenge plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
    {
		if(plugin.setTime.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			ChallengeInfo ch = plugin.challenge.get(plugin.setTime.get(p.getName()));
			if(event.getMessage().equalsIgnoreCase("add"))
			{
				if(ch.getRest()==null)
				{
					ch.setRest(p.getLocation());
				}
				else
				{
					ch.addLocation(p.getLocation());
				}
				p.sendMessage("��6[��սϵͳ] ��a���õ�"+(ch.getLocation().size()+1)+"��Ĵ��͵㣬����add���(exit�˳�)");
			}
			
			if(event.getMessage().equalsIgnoreCase("exit"))
			{
				plugin.setTime.remove(p.getName());
				p.sendMessage("��6[��սϵͳ] ��a���˳����ã�������"+ch.getLocation().size()+"��");
			}
		}
		return;
    }
	
	@EventHandler
	public void onKillMobSetting(AsyncPlayerChatEvent event)
    {
		if(plugin.setKillMob.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			ChallengeInfo ch = plugin.challenge.get(plugin.setKillMob.get(p.getName()));
			//boolean monster = false;
			if(event.getMessage().equalsIgnoreCase("add") && (!monster.contains(p)))
			{
				if(ch.getRest()==null)
				{
					ch.setRest(p.getLocation());
					p.sendMessage("��6[��սϵͳ] ��a���õ�1��Ĵ��͵㣬����add���(exit�˳�)");
				}
				else
				{
					ch.addLocation(p.getLocation());
					monster.add(p);
				}
				
				if(monster.contains(p))
				{
					p.sendMessage("��6[��սϵͳ] ��a���������ID,��������,�����Զ�������(�ö��Ÿ���)(exit�˳�):");
				}
			}
			else if(event.getMessage().equalsIgnoreCase("exit"))
			{
				plugin.setKillMob.remove(p.getName());
				p.sendMessage("��6[��սϵͳ] ��a���˳����ã�������"+ch.getLocation().size()+"��");
			}
			
			else if(monster.contains(p))
			{
				if(event.getMessage().split(",").length==3)
				{
					int mobID = Integer.valueOf(event.getMessage().split(",")[0]);
					int amount = Integer.valueOf(event.getMessage().split(",")[1]);
					String customName = event.getMessage().split(",")[2];
					KillMobObject mobObject = new KillMobObject(mobID, amount*2);
					mobObject.setCustomName(customName);
					ch.addMobObject(mobObject);

					p.sendMessage("��6[��սϵͳ] ��a���õ�"+(ch.getLocation().size()+1)+"��Ĵ��͵㣬����add���(exit�˳�)");
					monster.remove(p);
				}
				else if(event.getMessage().split(",").length==2)
				{
					int mobID = Integer.valueOf(event.getMessage().split(",")[0]);
					int amount = Integer.valueOf(event.getMessage().split(",")[1]);
					KillMobObject mobObject = new KillMobObject(mobID, amount*2);
					ch.addMobObject(mobObject);

					p.sendMessage("��6[��սϵͳ] ��a���õ�"+(ch.getLocation().size()+1)+"��Ĵ��͵㣬����add���(exit�˳�)");
					monster.remove(p);
				}
				else if(event.getMessage().split(",").length==1)
				{
					p.sendMessage("��6[��սϵͳ] ��a���������ID,��������,�����Զ�������(�ö��Ÿ���)(exit�˳�):");
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
			Player p = event.getPlayer();
			p.teleport(plugin.spawn);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp "+p.getName()+" "+plugin.spawn.getBlockX()+" "+plugin.spawn.getBlockY()+" "+plugin.spawn.getBlockZ());
			String locationName = plugin.singlePlayer.get(p).getLocationName();

			if(plugin.groupPlayer.get(locationName).getPlayers().isEmpty())
				plugin.groupPlayer.remove(locationName);
			else
				plugin.groupPlayer.get(locationName).removePlayer(p);
				
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
			//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp "+p.getName()+" "+plugin.spawn.getBlockX()+" "+plugin.spawn.getBlockY()+" "+plugin.spawn.getBlockZ());
			//plugin.startPlayer.put(p.getName(), -1);
			p.sendTitle("��ʧ����", "��û�ܼ�������ȥ");
		}
    }
	
	
	@EventHandler
	private void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if(plugin.singlePlayer.containsKey(event.getPlayer()))
		{
			Player p = event.getPlayer();
			new BukkitRunnable()
			{
	    		int time;
				public void run()
				{
					if(time>1)
					{
						int quantity = 0;
						p.teleport(plugin.spawn);
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp "+p.getName()+" "+plugin.spawn.getBlockX()+" "+plugin.spawn.getBlockY()+" "+plugin.spawn.getBlockZ());
						String locationName = plugin.singlePlayer.get(p).getLocationName();
						if(plugin.challenge.get(locationName).getType().equalsIgnoreCase("mobkill"))
							quantity = plugin.singlePlayer.get(p).getNumber()/plugin.rewardFactor;
						else if(plugin.challenge.get(locationName).getType().equalsIgnoreCase("time"))
							quantity = plugin.groupPlayer.get(locationName).getNumber()/plugin.rewardFactor;
						if(quantity>=1)
							plugin.openRewardGui(p, quantity);
						else
							p.sendTitle("��c��δ�����Ͳ���Ҫ��", "��c�޷���ý���");
						if(plugin.groupPlayer.get(locationName).getPlayers().isEmpty())
							plugin.groupPlayer.remove(locationName);
						else
							plugin.groupPlayer.get(locationName).removePlayer(p);
						if(quantity<1)
							plugin.singlePlayer.remove(event.getPlayer());
						cancel();
					}
					time++;
				}
			}.runTaskTimer(plugin, 0L, 20L);
			
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
				//event.getWhoClicked().sendMessage("111111");
				if(event.getInventory().getItem(event.getRawSlot())!=null && 
						event.getInventory().getItem(event.getRawSlot()).equals(plugin.getReward))
				{
					Player p = (Player)event.getWhoClicked();
					
					String locationName = plugin.singlePlayer.get(p).getLocationName();
					//event.getWhoClicked().sendMessage(locationName);
					ItemStack reward = plugin.getReward(locationName);
					//event.getWhoClicked().sendMessage("22222");
					event.getInventory().setItem(event.getRawSlot(), reward);
					if(reward.getItemMeta().hasDisplayName() && 
							reward.getItemMeta().getDisplayName().contains("��ҽ���:"))
					{
						double money = Double.valueOf(reward.getItemMeta().getDisplayName().split(":")[1]);

						plugin.economy.depositPlayer(p.getName(), money);
						p.sendMessage("��6���ѻ�á�5"+money+"��Ǯ������");
						return;
					}
					//event.getWhoClicked().sendMessage("33333");
					if(p.getInventory().firstEmpty()==-1)
					{
						p.getWorld().dropItem(p.getLocation(), reward);
					}
					else
						p.getInventory().addItem(reward);
				}
			}
		}
		else if(event.getInventory().getTitle().equalsIgnoreCase(plugin.timeGuiName) || 
				event.getInventory().getTitle().equalsIgnoreCase(plugin.killMobGuiName))
		{
			event.setCancelled(true);
			if(event.getRawSlot()>=0 && event.getRawSlot()<=53)
			{
				if(event.getInventory().getItem(event.getRawSlot())!=null)
				{
					String locationName = event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().substring(2);
					Player p = (Player)event.getWhoClicked();
					plugin.startChallenge(p, locationName);
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
			locationName = plugin.singlePlayer.get(event.getPlayer()).getLocationName();
			if(!plugin.challenge.get(locationName).isStart())
				plugin.leaveChallenge(event.getPlayer(), event.isPlayerLeader());
		}
	}
	*/
	
	
	@EventHandler
	private void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		if(plugin.singlePlayer.containsKey(event.getPlayer()))
		{
			if(event.getPlayer().isOp())
				return;
			String locationName = plugin.singlePlayer.get(event.getPlayer()).getLocationName();
			if(plugin.challenge.get(locationName).isStart())
			{
				event.setCancelled(true);
				Player p = (Player)event.getPlayer();
				p.sendMessage("��a[��սϵͳ] ��c��ս���������޷�ʹ�ø�ָ��");
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

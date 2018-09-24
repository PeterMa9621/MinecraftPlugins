package challenge;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class KillMobListener implements Listener
{
	private Challenge plugin;
	
	public KillMobListener(Challenge plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onMobDeath(EntityDeathEvent event)
    {
		if(!event.getEntityType().equals(EntityType.PLAYER))
		{
			if(event.getEntity().getKiller() instanceof Player)
			{
				Player p = event.getEntity().getKiller();
				if(plugin.singlePlayer.containsKey(p))
				{
					String locationName = plugin.singlePlayer.get(p).getLocationName();
					
					if(plugin.challenge.get(locationName).isStart())
					{
						if(event.getEntityType().getTypeId()==plugin.singlePlayer.get(p).getMobID() &&
								plugin.singlePlayer.get(p).isFinishKill()==false)
						{
							SinglePlayer sp = plugin.singlePlayer.get(p);

							if(plugin.singlePlayer.get(p).getCustomName()!=null)
							{
								String mobName = sp.getCustomName();
								
								if(mobName.equalsIgnoreCase(event.getEntity().getCustomName()))
								{
									sp.addKillAmount();
								}
							}
							else
							{
								//p.sendMessage("11111");
								sp.addKillAmount();
							}
							//plugin.singlePlayer.put(p, sp);
							if(sp.isFinishKill())
							{
								sp.addNumber();
								if(sp.getNumber()==plugin.challenge.get(locationName).getLocation().size())
								{
									p.teleport(plugin.spawn);
													
									int quantity = sp.getNumber()/plugin.rewardFactor;
									if(quantity>=1)
									{
										plugin.openRewardGui(p, quantity);
									}
									
									plugin.groupPlayer.get(locationName).removePlayer(p);
									if(plugin.groupPlayer.get(locationName).getPlayers().isEmpty())
									{
										plugin.groupPlayer.remove(locationName);
										plugin.challenge.get(locationName).changeStart(false);
									}
										
									if(quantity<1)
										plugin.singlePlayer.remove(p);
									
									p.sendTitle("§6挑战成功", "§e你完成了所有挑战");
									p.sendMessage("§6[挑战系统] §e挑战成功,你完成了所有挑战");
									return;
								}
								p.sendTitle("§6你已完成本次挑战", "§e即将传送到休息区");
								
								new BukkitRunnable()
								{
						    		int time;

									public void run()
									{
										if(!plugin.singlePlayer.containsKey(p))
										{
											cancel();
											return;
										}
										if(time==1)
											p.sendTitle("§c3","");
										
										if(time==2)
											p.sendTitle("§c2","");
										if(time==3)
											p.sendTitle("§c1","");
										if(time==4)
										{
											p.teleport(plugin.challenge.get(locationName).getRest());
											p.sendMessage("§6[挑战系统] §e你将有30秒的休息整顿时间");
										}
										if(time==25)
										{
											p.sendTitle("§6挑战即将继续","");
										}
										if(time==27)
										{
											p.sendTitle("§c3","");
										}
										if(time==28)
										{
											p.sendTitle("§c2","");
										}
										if(time==29)
										{
											p.sendTitle("§c1","");
										}
										if(time==30)
										{
											int number = sp.getNumber();
											plugin.core.getCore().teleportPlayer(Bukkit.getConsoleSender(), p, plugin.challenge.get(locationName).getLocation().get(number));
											//SinglePlayer sp = plugin.singlePlayer.get(p);
											int mobID = plugin.challenge.get(locationName).getMobObject().get(number).getMobId();
											int mobAmount = plugin.challenge.get(locationName).getMobObject().get(number).getAmount();
											String mobCustomName = plugin.challenge.get(locationName).getMobObject().get(number).getCustomName();
											sp.setCustomName(mobCustomName);
											sp.setMobAmount(mobAmount);
											sp.setMobID(mobID);
											plugin.singlePlayer.put(p, sp);
											if(sp.getCustomName()==null)
											{
												p.sendTitle("§6挑战继续", "§e请击杀§5"+mobAmount/2+"§e只§5"+plugin.mobName.get(mobID));
												p.sendMessage("§6[挑战系统] §e请击杀§5"+mobAmount/2+"§e只§5"+plugin.mobName.get(mobID));
											}
											else
											{
												p.sendTitle("§6挑战继续", "§e请击杀§5"+mobAmount/2+"§e只以§5"+mobCustomName+"§e为名的§5"+plugin.mobName.get(mobID));
												p.sendMessage("§6[挑战系统] §e请击杀§5"+mobAmount/2+"§e只以§5"+mobCustomName+"§e为名的§5"+plugin.mobName.get(mobID));
											}
												
											cancel();
										}
										time++;
									}
									
								}.runTaskTimer(plugin, 0L, 20L);
							}
						}
					}
					
				}
			}
			
		}
		
    }
	

}

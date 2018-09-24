package dungeon;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class KillMobListener implements Listener
{
	private Dungeon plugin;
	int time = 1;
	
	public KillMobListener(Dungeon plugin)
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
				if(!plugin.singlePlayer.containsKey(p))
					return;
				String locationName = plugin.singlePlayer.get(p).getLocation();
				if(plugin.groupPlayer.containsKey(locationName) && 
						plugin.groupPlayer.get(locationName).getPlayers().contains(p) && 
						plugin.singlePlayer.get(p).isBoss==false)
				{
					//String locationName = plugin.singlePlayer.get(p).getLocation();
					GroupPlayers gp = plugin.groupPlayer.get(locationName);
					if(plugin.challenge.get(locationName).isStart())
					{
						if(event.getEntityType().getTypeId()==gp.getMobID() &&
								gp.isFinishKill()==false)
						{
							if(gp.getCustomName()!=null)
							{
								String mobName = gp.getCustomName();
								
								if(mobName.equalsIgnoreCase(event.getEntity().getCustomName()))
								{
									gp.addKillAmount();
									//for(Player member:gp.getPlayers())
									//	member.sendMessage("§6[挑战] §e已击杀§5"+gp.getKillAmount()+"§e只怪物，剩余§5"+(gp.getMobAmount()-gp.getKillAmount())+"§e只。");
								}
							}
							else
							{
								gp.addKillAmount();
								//for(Player member:gp.getPlayers())
								//	member.sendMessage("§6[挑战] §e已击杀§5"+gp.getKillAmount()+"§e只怪物，剩余§5"+(gp.getMobAmount()-gp.getKillAmount())+"§e只。");
							}
							//plugin.singlePlayer.put(p, sp);
							if(gp.isFinishKill())
							{
								gp.addNumber();
								if(gp.getNumber()==plugin.challenge.get(locationName).getLocation().size())
								{
									while(gp.getPlayers().size()!=0)
									{
										Player member = gp.getPlayers().get(0);
										member.teleport(plugin.spawn);
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp "+member.getName()+" "+plugin.spawn.getBlockX()+" "+plugin.spawn.getBlockY()+" "+plugin.spawn.getBlockZ());
										
										int rank = plugin.dps.getAPI().getRank(member);
										double damage = plugin.dps.getAPI().getGroupDpsData(member).get(member.getName());
										
										if(damage>50)
										{
											int quantity = (13-rank)/plugin.rewardFactor;
											if(quantity<=0)
												quantity = 1;
											plugin.openRewardGui(member, quantity);
											//member.openInventory(plugin.rewardGUI(member, quantity));
										}
										
										gp.removePlayer(member);
										if(gp.getPlayers().isEmpty())
										{
											plugin.groupPlayer.remove(locationName);
										}
										
										//plugin.singlePlayer.remove(member);
										plugin.dps.getAPI().exitDpsModule(member);
										
										member.sendTitle("§a已完成副本", "§6你已完成该副本");
										//member.sendMessage("§5正在领取奖励...");
									}
									plugin.challenge.get(locationName).changeStart(false);
									
									return;
								}
								//p.sendTitle("§a你已完成本次挑战", "§a即将传送到休息区");
								
								
								new BukkitRunnable()
								{
						    		int time;

									public void run()
									{
										if(!plugin.groupPlayer.containsKey(locationName))
										{
											cancel();
											return;
										}
										if(time==1)
											for(Player member:gp.getPlayers())
												member.sendTitle("§c5","");
										if(time==2)
											for(Player member:gp.getPlayers())
												member.sendTitle("§c4","");
										if(time==3)
											for(Player member:gp.getPlayers())
												member.sendTitle("§c3","");
										if(time==4)
											for(Player member:gp.getPlayers())
												member.sendTitle("§c2","");
										if(time==5)
											for(Player member:gp.getPlayers())
												member.sendTitle("§c1","");
										if(time==6)
										{
											int number = gp.getNumber();
											for(Player member:gp.getPlayers())
												plugin.core.getCore().teleportPlayer(Bukkit.getConsoleSender(), member, plugin.challenge.get(locationName).getLocation().get(number));
											//SinglePlayer sp = plugin.singlePlayer.get(p);
											int mobID = plugin.challenge.get(locationName).getMobObject().get(number).getMobId();
											int mobAmount = plugin.challenge.get(locationName).getMobObject().get(number).getAmount();
											String mobCustomName = plugin.challenge.get(locationName).getMobObject().get(number).getCustomName();
											gp.setCustomName(mobCustomName);
											gp.setMobAmount(mobAmount);
											gp.setMobID(mobID);
											
											for(Player member:gp.getPlayers())
											{
												int amount = mobAmount/2;
												if(amount<=0)
													amount = 1;
												if(mobCustomName!=null)
												{
													member.sendTitle("§a已传送至下一区域", "§6请击杀以§5"+mobCustomName+"§6为名的§e"+amount+"§6只§e"+plugin.mobName.get(mobID));
												}
												else
												{
													member.sendTitle("§a已传送至下一区域", "§6请击杀§e"+amount+"§6只§e"+plugin.mobName.get(mobID));
												}
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
				else if(plugin.groupPlayer.containsKey(locationName) && 
						plugin.groupPlayer.get(locationName).getPlayers().contains(p) && 
						plugin.singlePlayer.get(p).isBoss==true)
				{
					GroupPlayers gp = plugin.groupPlayer.get(locationName);
					if(plugin.boss.containsKey(event.getEntity().getCustomName()))
					{
						new BukkitRunnable()
						{
				    		int time;
							public void run()
							{
								if(time==1)
									for(Player member:gp.getPlayers())
										member.sendTitle("§6成功击杀BOSS","§e即将打开奖励界面");
								else if(time==5)
									for(Player member:gp.getPlayers())
										member.sendTitle("§c3","");
								else if(time==6)
									for(Player member:gp.getPlayers())
										member.sendTitle("§c2","");
								else if(time==7)
									for(Player member:gp.getPlayers())
										member.sendTitle("§c1","");
								else if(time==8)
								{
									while(gp.getPlayers().size()!=0)
									{
										Player member = gp.getPlayers().get(0);
										//member.teleport(plugin.spawn);
										//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp "+member.getName()+" "+plugin.spawn.getBlockX()+" "+plugin.spawn.getBlockY()+" "+plugin.spawn.getBlockZ());
										
										int rank = plugin.dps.getAPI().getRank(member);
										double damage = plugin.dps.getAPI().getGroupDpsData(member).get(member.getName());
										
										if(damage>50)
										{
											int quantity = (16-rank)/3;
											if(quantity<=0)
												quantity = 1;
											member.openInventory(plugin.rewardGUI(member, quantity));
										}
										else
											member.sendTitle("§6你的伤害未满足最低要求","§e最低要求为§550");
										
										gp.removePlayer(member);
										if(gp.getPlayers().isEmpty())
										{
											plugin.groupPlayer.remove(locationName);
										}

										//plugin.singlePlayer.remove(member);
										plugin.dps.getAPI().exitDpsModule(member);
										
										member.sendMessage("§5正在领取奖励...");
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

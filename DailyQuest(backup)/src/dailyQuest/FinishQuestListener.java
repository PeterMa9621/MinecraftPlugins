package dailyQuest;

import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FinishQuestListener implements Listener
{
	private DailyQuest plugin;

	public FinishQuestListener(DailyQuest plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		if(!plugin.playerData.containsKey(event.getPlayer().getName()))
		{
			PlayerData player = new PlayerData(0,0,0);
			plugin.playerData.put(event.getPlayer().getName(), player);
		}
		else
		{
			PlayerData player = plugin.playerData.get(event.getPlayer().getName());
			if(!player.getLastLogout().equalsIgnoreCase(plugin.date.format(new Date())))
			{
				player.setCurrentNumber(0);
				player.setWhatTheQuestIs(0);
				player.setTotalQuest(0);
			}
		}
    }
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
    {
		PlayerData player = plugin.playerData.get(event.getPlayer().getName());
		player.setLastLogout(plugin.date.format(new Date()));
    }
	
	@EventHandler
	private void onPlayerClickQuestItemGUI(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().contains("所有任务物品-页数:"))
		{
			Player p = (Player)event.getWhoClicked();
			ArrayList<Inventory> list = plugin.questItemGUI(p);
			int page = Integer.valueOf(event.getInventory().getTitle().split(":")[1]);
			event.setCancelled(true);
			if(event.getRawSlot()==48 && page!=1)
			{
				p.openInventory(list.get(page-2));
				return;
			}
			if(event.getRawSlot()==51 && page<list.size())
			{
				p.openInventory(list.get(page));
				return;
			}
		}
		else if(event.getInventory().getTitle().contains("所有奖励物品-页数:"))
		{
			Player p = (Player)event.getWhoClicked();
			ArrayList<Inventory> list = plugin.rewardItemGUI(p);
			int page = Integer.valueOf(event.getInventory().getTitle().split(":")[1]);
			event.setCancelled(true);
			if(event.getRawSlot()==48 && page!=1)
			{
				p.openInventory(list.get(page-2));
				return;
			}
			if(event.getRawSlot()==51 && page<list.size())
			{
				p.openInventory(list.get(page));
				return;
			}
		}
	}
	
	@EventHandler
	private void onPlayerClickInventory(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase("§8NPC"))
		{
			Player p = (Player)event.getWhoClicked();
			event.setCancelled(true);
			if(event.getRawSlot()==3 && event.getInventory().getItem(3)!=null)
			{
				int currentIndex = plugin.playerData.get(p.getName()).getWhatTheQuestIs();
				String type = plugin.quests.get(currentIndex).getQuest().getType();
				ItemStack questItem = plugin.quests.get(currentIndex).getQuest().getQuestItem();
				//int amount = questItem.getAmount();
				//questItem.setAmount(1);
				if(type.equalsIgnoreCase("item"))
				{
					ItemStack item = p.getItemInHand();
					String NPCName = event.getInventory().getItem(0).getItemMeta().getDisplayName().split("对你说")[0];
					if(item.equals(questItem))
					{
						if(p.getInventory().firstEmpty()==-1)
						{
							p.sendMessage("§6<" + NPCName + "§6>: §e你的身上满了,请至少留出§c3§e个空位来接收奖励!");
							p.closeInventory();
							return;
						}

						p.setItemInHand(null);
						
						// =======================================================
						// Calculate the final money (with adding random number depends on current quest number)
						if(plugin.quests.get(currentIndex).getRewardMoney()>0)
						{
							int money = plugin.quests.get(currentIndex).getRewardMoney();
							money = plugin.random((money/2)+1)+(money/2);
							int addition = plugin.random((plugin.playerData.get(p.getName()).getCurrentNumber()/2)+1)*3;
							plugin.economy.depositPlayer(p.getName(), money+addition);
							p.sendMessage("§6[日常任务] §e你获得了§a"+(money+addition)+"§e金币作为奖励!");
						}
						// =======================================================
						// =======================================================
						// Get the reward items and give them to this player
						ArrayList<ItemStack> rewardItemList = plugin.quests.get(currentIndex).getRewardItem();
						
						for(ItemStack rewardItem:rewardItemList)
							p.getInventory().addItem(rewardItem);
						// =======================================================
						
						p.sendMessage("§6[日常任务] §e"+plugin.quests.get(currentIndex).getRewardMessage());
						
						// ======================================================
						// Get the next quest's index in the total quest's list
						int nextIndex = plugin.random(plugin.quests.size());
						PlayerData player = plugin.playerData.get(p.getName());
						// =====================================================
						
						// ====================================================
						// Get the limitation of this player
						int questLimit = plugin.defaultQuantity;
						for(String permission:plugin.group.keySet())
						{
							if(p.hasPermission("dailyQuest.limit."+permission))
							{
								questLimit = plugin.group.get(permission);
							}
						}
						// ====================================================
						
						// ====================================================
						// Check if this player has already finished all quests
						if(player.getTotalQuest()+1>=questLimit)
						{
							p.sendMessage("§6[日常任务] §d你今天的所有任务都完成了，明天再继续吧!");
							player.setCurrentNumber(0);
							player.setWhatTheQuestIs(0);
							player.setTotalQuest(player.getTotalQuest()+1);
							
							plugin.playerData.put(p.getName(), player);
							p.closeInventory();
							return;
						}
						// ====================================================
						
						// ====================================================
						// Give the correct data to this player
						player.setCurrentNumber(player.getCurrentNumber()+1);
						player.setWhatTheQuestIs(nextIndex);
						player.setTotalQuest(player.getTotalQuest()+1);

						plugin.playerData.put(p.getName(), player);
						// ====================================================
						
						p.sendMessage("§6[第 "+player.getCurrentNumber()+" 环] §a"+plugin.quests.get(nextIndex).getQuestDescribe());

					}
					else
					{
						p.sendMessage("§6<" + NPCName + "§6>: §d请把你要给我的东西拿在手上再给我(§c注意数量哦§d)!");
					}
				}

				p.closeInventory();
				
				return;
			}
			
			if((event.getRawSlot()==4 && event.getInventory().getItem(4)!=null)
					|| (event.getRawSlot()==5 && event.getInventory().getItem(5)!=null))
			{
				p.closeInventory();
				return;
			}

		}

	}
	
	@EventHandler
	private void onPlayerCloseInventory(InventoryCloseEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase("§8NPC"))
		{
			Player p = (Player)event.getPlayer();
			p.playSound(p.getLocation(), Sound.BLOCK_SNOW_BREAK, 1F, 0.0F);
		}
	}
}

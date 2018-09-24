package dailyQuest;

import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
		if(event.getInventory().getTitle().contains("����������Ʒ-ҳ��:"))
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
		else if(event.getInventory().getTitle().contains("���н�����Ʒ-ҳ��:"))
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
		if(event.getInventory().getTitle().equalsIgnoreCase("��8NPC"))
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
					String NPCName = event.getInventory().getItem(0).getItemMeta().getDisplayName().split("����˵")[0];
					if(item.equals(questItem))
					{
						if(p.getInventory().firstEmpty()==-1)
						{
							p.sendMessage("��6<" + NPCName + "��6>: ��e�����������,������������c3��e����λ�����ս���!");
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
							
							if(plugin.playerData.get(p.getName()).getCurrentNumber()%10==0)
							{
								int seriesFinishMoney = plugin.additionMoney;
								plugin.economy.depositPlayer(p.getName(), money+addition+seriesFinishMoney);
								p.sendMessage("��6[�ճ�����] ��7�����ˡ�a"+(money+addition)+"��7�����Ϊ����,���������ˡ�a"+seriesFinishMoney+"��7���!");
							}
							else
							{
								plugin.economy.depositPlayer(p.getName(), money+addition);
								p.sendMessage("��6[�ճ�����] ��7�����ˡ�a"+(money+addition)+"��7�����Ϊ����!");
							}
							
						}
						// =======================================================
						// =======================================================
						// Get the random reward items and how many reward items this player can get and give them to this player
						int maxQuantity = plugin.random(plugin.randomItemMaxQuantity)+1;
						if(plugin.playerData.get(p.getName()).getCurrentNumber()%10==0)
						{
							maxQuantity += plugin.extraRewarItemQuantity;
							p.sendMessage("��6[�ճ�����] ��7��������ˡ�c"+plugin.extraRewarItemQuantity+"��7����Ʒ����");
						}
						
						for(int i=0; i<maxQuantity; i++)
						{
							int itemIndex = plugin.random(plugin.rewardItem.size());
							p.getInventory().addItem(plugin.rewardItem.get(itemIndex));
						}

						// =======================================================
						
						if(plugin.quests.get(currentIndex).getRewardMessage()!="null")
							p.sendMessage("��6[�ճ�����] ��e"+plugin.quests.get(currentIndex).getRewardMessage());
						
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
							p.sendMessage("��6[�ճ�����] ��d������������������ˣ������ټ�����!");
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
						
						if(plugin.quests.get(nextIndex).getQuest().getType().equalsIgnoreCase("mob"))
						{
							int amount = plugin.quests.get(nextIndex).getQuest().getMobAmount();
							int mobID = plugin.quests.get(nextIndex).getQuest().getMobId();
							String mobName = plugin.quests.get(nextIndex).getQuest().getMobName();
							MobQuest mobQuest = null;
							if(mobName==null)
								mobQuest = new MobQuest(amount, 0, mobID);
							else
								mobQuest = new MobQuest(amount, 0, mobID, mobName);
							plugin.mobQuest.put(p.getName(), mobQuest);
						}
						
						plugin.playerData.put(p.getName(), player);
						// ====================================================
						
						p.sendMessage("��6[�� "+player.getCurrentNumber()+" ��] ��a"+plugin.quests.get(nextIndex).getQuestDescribe());

					}
					else
					{
						p.sendMessage("��6<" + NPCName + "��6>: ��d�����Ҫ���ҵĶ������������ٸ���(��cע������Ŷ��d)!");
					}
				}
				else if(type.equalsIgnoreCase("mob"))
				{
					String NPCName = event.getInventory().getItem(0).getItemMeta().getDisplayName().split("����˵")[0];
					if(plugin.mobQuest.get(p.getName()).isFinished())
					{
						if(p.getInventory().firstEmpty()==-1)
						{
							p.sendMessage("��6<" + NPCName + "��6>: ��e�����������,������������c3��e����λ�����ս���!");
							p.closeInventory();
							return;
						}
						
						// =======================================================
						// Calculate the final money (with adding random number depends on current quest number)
						if(plugin.quests.get(currentIndex).getRewardMoney()>0)
						{
							int money = plugin.quests.get(currentIndex).getRewardMoney();
							money = plugin.random((money/2)+1)+(money/2);
							int addition = plugin.random((plugin.playerData.get(p.getName()).getCurrentNumber()/2)+1)*3;
							
							if(plugin.playerData.get(p.getName()).getCurrentNumber()%10==0)
							{
								int seriesFinishMoney = plugin.additionMoney;
								plugin.economy.depositPlayer(p.getName(), money+addition+seriesFinishMoney);
								p.sendMessage("��6[�ճ�����] ��7�����ˡ�a"+(money+addition)+"��7�����Ϊ����,���������ˡ�a"+seriesFinishMoney+"��7���!");
							}
							else
							{
								plugin.economy.depositPlayer(p.getName(), money+addition);
								p.sendMessage("��6[�ճ�����] ��7�����ˡ�a"+(money+addition)+"��7�����Ϊ����!");
							}
							
						}
						// =======================================================
						// =======================================================
						// Get the random reward items and how many reward items this player can get and give them to this player
						int maxQuantity = plugin.random(plugin.randomItemMaxQuantity)+1;
						if(plugin.playerData.get(p.getName()).getCurrentNumber()%10==0)
						{
							maxQuantity += plugin.extraRewarItemQuantity;
							p.sendMessage("��6[�ճ�����] ��7��������ˡ�c"+plugin.extraRewarItemQuantity+"��7����Ʒ����");
						}
						
						for(int i=0; i<maxQuantity; i++)
						{
							int itemIndex = plugin.random(plugin.rewardItem.size());
							p.getInventory().addItem(plugin.rewardItem.get(itemIndex));
						}

						// =======================================================
						
						if(plugin.quests.get(currentIndex).getRewardMessage()!="null")
							p.sendMessage("��6[�ճ�����] ��e"+plugin.quests.get(currentIndex).getRewardMessage());
						
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
						plugin.mobQuest.remove(p.getName());
						// ====================================================
						// Check if this player has already finished all quests
						if(player.getTotalQuest()+1>=questLimit)
						{
							p.sendMessage("��6[�ճ�����] ��d������������������ˣ������ټ�����!");
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
						
						if(plugin.quests.get(nextIndex).getQuest().getType().equalsIgnoreCase("mob"))
						{
							int amount = plugin.quests.get(nextIndex).getQuest().getMobAmount();
							int mobID = plugin.quests.get(nextIndex).getQuest().getMobId();
							String mobName = plugin.quests.get(nextIndex).getQuest().getMobName();
							MobQuest mobQuest = null;
							if(mobName==null)
								mobQuest = new MobQuest(amount, 0, mobID);
							else
								mobQuest = new MobQuest(amount, 0, mobID, mobName);
							plugin.mobQuest.put(p.getName(), mobQuest);
						}
						
						plugin.playerData.put(p.getName(), player);
						// ====================================================
						
						p.sendMessage("��6[�� "+player.getCurrentNumber()+" ��] ��a"+plugin.quests.get(nextIndex).getQuestDescribe());
					}
					else
					{
						p.sendMessage("��6<" + NPCName + "��6>: ��d�㲢û���������!");
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
		if(event.getInventory().getTitle().equalsIgnoreCase("��8NPC"))
		{
			Player p = (Player)event.getPlayer();
			p.playSound(p.getLocation(), Sound.BLOCK_SNOW_BREAK, 1F, 0.0F);
		}
	}
}

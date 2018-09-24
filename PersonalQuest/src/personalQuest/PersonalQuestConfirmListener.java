package personalQuest;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class PersonalQuestConfirmListener implements Listener
{
	private PersonalQuest plugin;
	private PersonalQuestInitGui gui;
	
	int[] slot = {18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53};
	int[] slotAllowed = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};

	public PersonalQuestConfirmListener(PersonalQuest plugin, PersonalQuestInitGui gui)
	{
		this.plugin=plugin;
		this.gui=gui;
	}
	
	@EventHandler
	public void onPlayerCloseGui(InventoryCloseEvent event)
    {
		if(event.getPlayer().getOpenInventory().getTitle().equalsIgnoreCase("§a你正与§c"+plugin.confirmPlayers.get(event.getPlayer().getName())+"§a确认任务"))
		{
			String p2Name = plugin.confirmPlayers.get(event.getPlayer().getName());
			if(plugin.confirmPlayers.containsKey(event.getPlayer().getName()))
			{
				plugin.confirmPlayers.remove(event.getPlayer().getName());
				Player p2 = Bukkit.getServer().getPlayer(p2Name);
				for(int i=0; i<17; i++)
				{
					if(event.getInventory().getItem(i)!=null)
						event.getPlayer().getInventory().addItem(event.getInventory().getItem(i));
				}
				plugin.playersConfirm2.remove(event.getPlayer().getName());
				plugin.playersConfirm1.remove(event.getPlayer().getName());
				p2.closeInventory();
				p2.sendMessage("§a[任务系统] §c提交任务被取消");
			}
		}
		
    }
	
	@EventHandler
	public void onPlayerDragItem(InventoryDragEvent event)
    {
		if(event.getInventory().getTitle().equalsIgnoreCase("§a你正与§c"+plugin.confirmPlayers.get(event.getWhoClicked().getName())+"§a确认任务"))
		{
			event.setCancelled(true);
		}
    }
	
	/*
	@EventHandler
	public void onPlayerDragItem(InventoryDragEvent event)
    {
		if(event.getInventory().getTitle().equalsIgnoreCase("§a你正与§c"+plugin.confirmPlayers.get(event.getWhoClicked().getName())+"§a确认任务"))
		{
			if(event.getRawSlots().isEmpty())
			{
				return;
			}
			Player p1 = (Player)event.getWhoClicked();
			
			Player p2 = Bukkit.getServer().getPlayer(plugin.confirmPlayers.get(p1.getName()));
			ItemStack item = event.getCursor().clone();
			item.setAmount(1);
			
			for(int index:event.getRawSlots())
			{
				p1.sendMessage("Slots:"+index);
				if(plugin.isExist(index, slotAllowed))
				{
					if(event.getInventory().getItem(index)!=null)
					{
						p1.sendMessage("1");
						if(event.getInventory().getItem(index).getAmount()!=
								event.getInventory().getItem(index).getMaxStackSize())
						{
							int number = event.getInventory().getItem(index).getAmount();
							item.setAmount(number+1);
							p2.getOpenInventory().setItem(index+36, item);
						}
					}
					else
					{
						p1.sendMessage("2");
						p2.getOpenInventory().setItem(index+36, item);
					}
				}
			}
		}
    }
	*/
	
	@EventHandler
	public void onPlayerClickGui(InventoryClickEvent event)
    {
		if(event.getInventory().getTitle().equalsIgnoreCase("§a你正与§c"+plugin.confirmPlayers.get(event.getWhoClicked().getName())+"§a确认任务"))
		{
			
			if(event.getAction()==InventoryAction.DROP_ALL_CURSOR ||
					event.getAction()==InventoryAction.DROP_ONE_SLOT)
			{
				event.setCancelled(true);
				return;
			}
			
			if(event.getAction()==InventoryAction.COLLECT_TO_CURSOR || event.getAction()==InventoryAction.HOTBAR_MOVE_AND_READD)
			{
				event.setCancelled(true);
				return;
			}
			
			if(event.getClick()==ClickType.SHIFT_LEFT || event.getClick()==ClickType.SHIFT_RIGHT)
			{
				event.setCancelled(true);
				return;
			}
			
			if(plugin.isExist(event.getRawSlot(),slot))
			{
				event.setCancelled(true);
				return;
			}
			
			if(plugin.playersConfirm2.containsKey(event.getWhoClicked().getName()))
			{
				//event.getWhoClicked().sendMessage("confirm2222");
				event.setCancelled(true);
				return;
			}
			
			if(plugin.playersConfirm1.containsKey(event.getWhoClicked().getName()))
			{
				if(event.getRawSlot()!=17)
				{
					//event.getWhoClicked().sendMessage("confirm1111");
					event.setCancelled(true);
					return;
				}
			}

			Player p1 = (Player) event.getWhoClicked();
			Player p2 = Bukkit.getServer().getPlayer(plugin.confirmPlayers.get(p1.getName()));
			
			
			
			if(event.getRawSlot()==17)
			{
				event.setCancelled(true);
				String buttonName = plugin.message.get("ConfirmGui.ConfirmButton2");
				
				if(event.getInventory().getItem(17).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.message.get("ConfirmGui.ConfirmButton1")))
				{
					ItemStack window = plugin.createItem(160, 1, 5, "§c请确认各项物品都正确");
					for(int i=18; i<27; i++)
					{
						event.getInventory().setItem(i, window);
						p2.getOpenInventory().setItem(9+i, window);
					}
					ItemStack button = plugin.createItem(368, 1, 0, buttonName);
					event.getInventory().setItem(17, button);
					plugin.playersConfirm1.put(event.getWhoClicked().getName(), "1");
				}
				else if(event.getInventory().getItem(17).getItemMeta().getDisplayName().equalsIgnoreCase(buttonName))
				{
					if(!plugin.playersConfirm1.containsKey(p2.getName()))
					{
						event.getWhoClicked().sendMessage("§a[任务系统] §c请等待对方确认");
						return;
					}
					if(plugin.playersConfirm2.containsKey(p2.getName()))
					{
						for(int i=0; i<17; i++)
						{
							if(event.getInventory().getItem(i)!=null)
								p2.getInventory().addItem(event.getInventory().getItem(i));
							if(event.getInventory().getItem(i+36)!=null)
								p1.getInventory().addItem(event.getInventory().getItem(i+36));
						}
						
						
						plugin.confirmPlayers.remove(p1.getName());
						plugin.confirmPlayers.remove(p2.getName());
						plugin.playersConfirm2.remove(p1.getName());
						plugin.playersConfirm2.remove(p2.getName());
						plugin.playersConfirm1.remove(p1.getName());
						plugin.playersConfirm1.remove(p2.getName());
						
						
						HashMap<String, HashMap<String, ArrayList<String>>> obj = new HashMap<String, HashMap<String, ArrayList<String>>>();
						
						String posterName = "";
						String questType = "";
						
						questType = event.getInventory().getItem(53).getItemMeta().getDisplayName().split(",")[0].split(":")[1];
						posterName = event.getInventory().getItem(53).getItemMeta().getDisplayName().split(",")[1].split(":")[1];
						
						
						if(questType.equalsIgnoreCase("VIP"+plugin.message.get("QuestGui.ItemQuestTag")))
							obj = plugin.vipItemQuests;
						else if(questType.equalsIgnoreCase("VIP"+plugin.message.get("QuestGui.HeadQuestTag")))
							obj = plugin.vipHeadQuests;
						else if(questType.equalsIgnoreCase(plugin.message.get("QuestGui.ItemQuestTag")))
							obj = plugin.itemQuests;
						else if(questType.equalsIgnoreCase(plugin.message.get("QuestGui.HeadQuestTag")))
							obj = plugin.headQuests;
						
						 //这里有问题
						for(String name:obj.get(posterName).get("player"))
						{
							if(plugin.playerQuest.get(name)!=null)
							{
								int indexOfQuest = 0;
								for(ArrayList<String> everyQuest:plugin.playerQuest.get(name))
								{
									 //这里没执行到，检查！
									if(everyQuest.get(1).equalsIgnoreCase(obj.get(posterName).get("quest").get(1)))
									{
										if(everyQuest.get(2).equalsIgnoreCase(obj.get(posterName).get("quest").get(2)))
										{
											if(everyQuest.get(everyQuest.size()-1).equalsIgnoreCase(posterName))
											{
												plugin.playerQuest.get(name).remove(indexOfQuest);
												break;
											}
										}
											
									}

									indexOfQuest+=1;
								}
							}
							if(Bukkit.getServer().getPlayer(name)!=null)
							{
								Player otherp = Bukkit.getServer().getPlayer(name);
								
								if(otherp.getOpenInventory().getTitle().equalsIgnoreCase(plugin.message.get("MyQuestGui.Name")))
								{
									otherp.openInventory(gui.initMyQuest(otherp));
								}
							}
						}
						
						obj.remove(posterName);
						p1.closeInventory();
						p2.closeInventory();
						
						p1.sendMessage("§a[任务系统] §6任务完成!");
						p2.sendMessage("§a[任务系统] §6任务完成!");
					}
					else
					{
						plugin.playersConfirm2.put(p1.getName(), "1");
						ItemStack window = plugin.createItem(160, 1, 5, "§a等待对方最终确认");
						
						event.getInventory().setItem(17, window);
					}
				}
			}
			
			// event.getCurrentItem() is the item that first click (pick up)
			// event.getCursor() is the item that second click (put down)
			
			if(event.getCurrentItem()!=null && event.getCursor()!=null)
			{
				//p1.sendMessage("item1 "+event.getCurrentItem().getTypeId());
				//p1.sendMessage("item2 "+event.getCursor().getTypeId());
				ItemStack item1 = event.getCurrentItem();
				ItemStack item2 = event.getCursor();
				
				// 手里有东西,目的位置是空
				if(item1.getTypeId()==0)
				{
					if(plugin.isExist(event.getRawSlot(), slotAllowed))
					{
						if(event.getClick()==ClickType.LEFT)
							p2.getOpenInventory().setItem(event.getRawSlot()+36, item2);
						else if(event.getClick()==ClickType.RIGHT)
						{
							ItemStack item = item2.clone();
							item.setAmount(1);
							p2.getOpenInventory().setItem(event.getRawSlot()+36, item);
						}
					}
				}
				
				// 手中是空的,目的位置不是空
				else if(item2.getTypeId()==0)
				{
					if(plugin.isExist(event.getRawSlot(), slotAllowed))
					{
						int amount = event.getInventory().getItem(event.getRawSlot()).getAmount();
						if(event.getClick()==ClickType.LEFT)
							p2.getOpenInventory().setItem(event.getRawSlot()+36, null);
						else if(event.getClick()==ClickType.RIGHT)
						{
							if(amount==1)
								p2.getOpenInventory().setItem(event.getRawSlot()+36, null);
							else
							{
								ItemStack item = event.getInventory().getItem(event.getRawSlot()).clone();
								item.setAmount(amount/2);
								
								p2.getOpenInventory().setItem(event.getRawSlot()+36, item);
							}
						}
							
					}
				}
				
				// 手中有物品交换
				else
				{
					if(plugin.isExist(event.getRawSlot(), slotAllowed))
					{
						
						if(event.getInventory().getItem(event.getRawSlot()).getType()==item2.getType())
						{
							int amount = event.getInventory().getItem(event.getRawSlot()).getAmount();
							if(event.getClick()==ClickType.LEFT)
							{
								if(amount != event.getInventory().getItem(event.getRawSlot()).getMaxStackSize())
								{
									ItemStack item = item2.clone();
									item.setAmount(amount+item2.getAmount());
									p2.getOpenInventory().setItem(event.getRawSlot()+36, item);
								}
							}
							else if(event.getClick()==ClickType.RIGHT)
							{
								ItemStack item = item2.clone();
								item.setAmount(amount+1);
								p2.getOpenInventory().setItem(event.getRawSlot()+36, item);
							}

						}
						else
						{
							p2.getOpenInventory().setItem(event.getRawSlot()+36, item2);
						}
						
					}
				}
				
				
			}
			
			
			
			/*
			if(!event.getCurrentItem().equals(null))
			{
				p1.sendMessage("2");
				if(plugin.comfirmGuiItems.get(p1.getName())!=null)
				{
					if(plugin.comfirmGuiItems.get(p1.getName()).equals(event.getCurrentItem()))
					{
						if(plugin.isExist(event.getRawSlot(), slotAllowed))
						{
							p1.sendMessage("4");
							p2.getOpenInventory().setItem(event.getRawSlot()+36, event.getCurrentItem());	
						}
					}
					else
					{
						p1.sendMessage("3");
						plugin.comfirmGuiItems.put(p1.getName(), event.getCurrentItem());
					}
				}
				else
				{
					plugin.comfirmGuiItems.put(p1.getName(), event.getCurrentItem());
				}
			}
			*/
			
			
			/*
			Player p = (Player) event.getWhoClicked();
			Player p2 = Bukkit.getServer().getPlayer(plugin.confirmPlayers.get(event.getWhoClicked().getName()));
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			for(int i=36; i<=52; i++)
			{
				if(event.getInventory().getItem(i)!=null)
				{
					items.add(event.getInventory().getItem(i));
				}
			}
			if(!items.isEmpty())
			{
				plugin.comfirmGuiItems.put(p.getName(), items);
				p2.openInventory(gui.initConfirmForPlayer(p, p2));
			}
			*/
		}
    }
}

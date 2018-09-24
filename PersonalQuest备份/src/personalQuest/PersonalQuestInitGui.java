package personalQuest;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PersonalQuestInitGui 
{
	private PersonalQuest plugin;
	public PersonalQuestInitGui(PersonalQuest plugin)
	{
		this.plugin=plugin;
	}
	
	public Inventory initMain(Player p)
	{		
				
		Inventory inv = Bukkit.createInventory(p, 9, plugin.message.get("MainGui.Name"));
		ItemStack quest = plugin.createItem(Material.BOOK, plugin.message.get("MainGui.QuestButton.Name"));
		
		ItemStack job = plugin.createItem(Material.PAPER, plugin.message.get("MainGui.JobButton.Name"));

		inv.setItem(2, quest);
		inv.setItem(4, job);

		return inv;
	}
	
	public Inventory initAdd(Player p)
	{
				
		Inventory inv = Bukkit.createInventory(p, 18, plugin.message.get("AddGui.Name"));
		
		ItemStack quest = plugin.createItem(Material.BOOK, plugin.message.get("AddGui.ItemQuestButton.Name"), plugin.message.get("AddGui.ItemQuestButton.Lore"));

		ItemStack head = plugin.createItem(397, 1, (short)3, plugin.message.get("AddGui.HeadQuestButton.Name"), plugin.message.get("AddGui.HeadQuestButton.Lore"));

		ItemStack questVIP = plugin.createItem(Material.NETHER_STAR, plugin.message.get("AddGui.ItemQuestButtonVIP.Name"), plugin.message.get("AddGui.ItemQuestButtonVIP.Lore"));
		
		ItemStack headVIP = plugin.createItem(397, 1, 0, plugin.message.get("AddGui.HeadQuestButtonVIP.Name"), plugin.message.get("AddGui.HeadQuestButtonVIP.Lore"));
		
		ItemStack custom = plugin.createItem(Material.PAPER, plugin.message.get("AddGui.CustomButton.Name"), plugin.message.get("AddGui.CustomQuestButton.Lore"));

		ItemStack customVIP = plugin.createItem(Material.MAP, plugin.message.get("AddGui.CustomButtonVIP.Name"), plugin.message.get("AddGui.CustomQuestButtonVIP.Lore"));
		
		inv.setItem(2, quest);
		inv.setItem(4, custom);
		inv.setItem(6, head);
		inv.setItem(11, questVIP);
		inv.setItem(13, customVIP);
		inv.setItem(15, headVIP);

		return inv;
	}
	
	public Inventory initMyQuest(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 45, plugin.message.get("MyQuestGui.Name"));

		ItemStack window = plugin.createItem(160, 1, 0, " ");

		for(int i=0; i<9; i++)
		{
			inv.setItem(9+i, window);
			inv.setItem(27+i, window);
		}
		
		
		// ===========================================================================
		// The following codes are used to show the quests that I had
		// ===========================================================================
		int indexOfGetQuest = 0;
		if(!plugin.playerQuest.get(p.getName()).isEmpty())
		{
			for(ArrayList<String> everyQuest:plugin.playerQuest.get(p.getName()))
			{
				String type = everyQuest.get(0);
				String name = everyQuest.get(1);
				String content = everyQuest.get(2);
				String posterName = everyQuest.get(everyQuest.size()-1);
				ItemStack vipItem = null;
				
				String lore = "";
				
				// auto separate a string with 8 characters
				int length = 0;
				if(content.length()%8==0)
					length = content.length()/8;
				else
					length = (content.length()/8)+1;
				int index = 0;
				for(int c=0; c<length ; c++)
				{
					if(c==length-1)
					{
						lore += "§e"+content.substring(index, content.length());
						break;
					}
					lore += "§e"+content.substring(index, index+8)+"%";
					index +=8;
				}
				
				lore += "%§d发布人:"+posterName;
				lore += "%§c右键点击放弃该任务";
				
				if(type.equalsIgnoreCase("VIPitem"))
					vipItem = plugin.createItem(Material.NETHER_STAR, "§a["+plugin.message.get("QuestGui.ItemQuestTag")+"§a]§f"+name, lore);
				if(type.equalsIgnoreCase("VIPhead"))
					vipItem = plugin.createItem(397, 1, 0, "§a["+plugin.message.get("QuestGui.HeadQuestTag")+"§a]§f"+name, lore);
				if(type.equalsIgnoreCase("item"))
					vipItem = plugin.createItem(340, 1, 0, "§a["+plugin.message.get("QuestGui.ItemQuestTag")+"§a]§f"+name, lore);
				if(type.equalsIgnoreCase("head"))
					vipItem = plugin.createItem(397, 1, 3, "§a["+plugin.message.get("QuestGui.HeadQuestTag")+"§a]§f"+name, lore);
				inv.setItem(indexOfGetQuest, vipItem);
				indexOfGetQuest += 1;
			}
		}
		
		// ===========================================================================
		// The following codes are used to show the quests that I posted
		// ===========================================================================
		
		ArrayList<ArrayList<String>> totalQuest = new ArrayList<ArrayList<String>>();
		
		
		for(int i=0; i<19; i+=18)
		{
			totalQuest.clear();
			if(i==18)
			{
				if(plugin.headQuests.containsKey(p.getName()))
					totalQuest.add(plugin.headQuests.get(p.getName()).get("quest"));
				if(plugin.itemQuests.containsKey(p.getName()))
					totalQuest.add(plugin.itemQuests.get(p.getName()).get("quest"));
			}
			else if(i==0)
			{
				if(plugin.vipHeadQuests.containsKey(p.getName()))
					totalQuest.add(plugin.vipHeadQuests.get(p.getName()).get("quest"));
				if(plugin.vipItemQuests.containsKey(p.getName()))
					totalQuest.add(plugin.vipItemQuests.get(p.getName()).get("quest"));
			}

			int indexOfPostQuest = 18+i;
			if(!totalQuest.isEmpty())
			{
				for(ArrayList<String> everyQuest:totalQuest)
				{

					String type = everyQuest.get(0);
					String name = everyQuest.get(1);
					String content = everyQuest.get(2);
					String posterName = p.getName();
					ItemStack vipItem = null;
					
					String lore = "";
					
					// auto separate a string with 8 characters
					int length = 0;
					if(content.length()%8==0)
						length = content.length()/8;
					else
						length = (content.length()/8)+1;
					int index = 0;
					for(int c=0; c<length ; c++)
					{
						if(c==length-1)
						{
							lore += "§e"+content.substring(index, content.length());
							break;
						}
						lore += "§e"+content.substring(index, index+8)+"%";
						index +=8;
					}
					lore += "%§d发布人:"+posterName;
					lore += "%§b点击查看正在执行该任务的玩家";
					lore += "%§c右键点击放弃该任务";
					
					if(type.equalsIgnoreCase("VIPitem"))
						vipItem = plugin.createItem(Material.NETHER_STAR, "§a["+plugin.message.get("QuestGui.ItemQuestTag")+"§a]§f"+name, lore);
					if(type.equalsIgnoreCase("VIPhead"))
						vipItem = plugin.createItem(397, 1, 0, "§a["+plugin.message.get("QuestGui.HeadQuestTag")+"§a]§f"+name, lore);
					if(type.equalsIgnoreCase("item"))
						vipItem = plugin.createItem(340, 1, 0, "§a["+plugin.message.get("QuestGui.ItemQuestTag")+"§a]§f"+name, lore);
					if(type.equalsIgnoreCase("head"))
						vipItem = plugin.createItem(397, 1, 3, "§a["+plugin.message.get("QuestGui.HeadQuestTag")+"§a]§f"+name, lore);
					inv.setItem(indexOfPostQuest, vipItem);
					indexOfPostQuest += 1;
				}
			}

		}
		

		return inv;
	}
	
	public ArrayList<Inventory> initQuestGUI(Player p)
	{
		ArrayList<Inventory> inventory = new ArrayList<Inventory>();
		ArrayList<ArrayList<String>> questList = new ArrayList<ArrayList<String>>();
		int size = plugin.headQuests.size() + plugin.itemQuests.size();
		int totalSize = plugin.headQuests.size() + plugin.itemQuests.size() + plugin.vipHeadQuests.size() + plugin.vipItemQuests.size();

		if(totalSize==0)
		{
			Inventory inv = Bukkit.createInventory(p, 54, plugin.message.get("QuestGui.Name"));
			ItemStack next = plugin.createItem(351,1,(short)13, plugin.message.get("QuestGui.NextPageButton.Name"));
			
			ItemStack previous = plugin.createItem(351,1,(short)8, plugin.message.get("QuestGui.PreviousPageButton.Name"));
			
			ItemStack add = plugin.createItem(386, 1, 0, plugin.message.get("QuestGui.PostButton.Name"));
			
			ItemStack page = plugin.createItem(339, 1, 0, "§e第1页");
			
			inv.setItem(49, add);
			inv.setItem(53, next);
			inv.setItem(45, previous);
			inv.setItem(47, page);
			
			ArrayList<Inventory> invList = new ArrayList<Inventory>();
			invList.add(inv);
			return invList;
		}


		// put all quests into a total list, the last index of the everyQuest is the player's name
		for(String player:plugin.itemQuests.keySet())
		{
			if(!plugin.itemQuests.get(player).isEmpty())
			{
				ArrayList<String> result = plugin.itemQuests.get(player).get("quest");
				result.add(player);
				questList.add(result);
			}
		}

		for(String player:plugin.headQuests.keySet())
		{
			if(!plugin.itemQuests.get(player).isEmpty())
			{
				ArrayList<String> result = plugin.headQuests.get(player).get("quest");
				result.add(player);
				questList.add(result);
			}
		}

		int indexOfNormalQuest = 0;
		for(int j=0; j<totalSize/45+1; j++)
		{

			Inventory inv = Bukkit.createInventory(p, 54, plugin.message.get("QuestGui.Name"));
			ItemStack previous = plugin.createItem(351,1,(short)13, plugin.message.get("QuestGui.NextPageButton.Name"));
			
			ItemStack next = plugin.createItem(351,1,(short)8, plugin.message.get("QuestGui.PreviousPageButton.Name"));
			
			ItemStack add = plugin.createItem(386, 1, 0, plugin.message.get("QuestGui.PostButton.Name"));
			
			ItemStack page = plugin.createItem(339, 1, 0, "§e第"+(j+1)+"页");
			
			inv.setItem(49, add);
			inv.setItem(53, previous);
			inv.setItem(45, next);
			inv.setItem(47, page);

			int indexOfInv = 0;

			// get the VIP quests, show them between index 0 to 8
			
			ArrayList<ArrayList<String>> vipQuestList = new ArrayList<ArrayList<String>>();

			for(String player:plugin.vipItemQuests.keySet())
			{

				if(!plugin.vipItemQuests.get(player).isEmpty())
				{
					ArrayList<String> result = plugin.vipItemQuests.get(player).get("quest");
					result.add(player);
					vipQuestList.add(result);
				}
			}

			for(String player:plugin.vipHeadQuests.keySet())
			{
				if(!plugin.vipItemQuests.get(player).isEmpty())
				{
					ArrayList<String> result = plugin.vipHeadQuests.get(player).get("quest");
					result.add(player);
					vipQuestList.add(result);
				}
			}

			for(ArrayList<String> vip:vipQuestList)
			{
				ItemStack item = null;
				ItemMeta metaItem = null;

				String type = vip.get(0);
				String name = vip.get(1);
				String content = vip.get(2);

				ArrayList<String> lore = new ArrayList<String>();
				int length = 0;

				if(type.equalsIgnoreCase("VIPitem"))
				{

					item = new ItemStack(Material.NETHER_STAR);
					metaItem = item.getItemMeta();

					metaItem.setDisplayName("§a["+plugin.message.get("QuestGui.ItemQuestTag")+"§a]§f"+name);
				}

				if(type.equalsIgnoreCase("VIPhead"))
				{
					item = new ItemStack(397);
					metaItem = item.getItemMeta();
					metaItem.setDisplayName("§a["+plugin.message.get("QuestGui.HeadQuestTag")+"§a]§f"+name);
				}

				if(content.length()%8==0)
					length = content.length()/8;
				else
					length = (content.length()/8)+1;
				int index = 0;
				for(int c=0; c<length ; c++)
				{
					if(c==length-1)
					{
						lore.add("§e"+content.substring(index, content.length()));
						break;
					}
					lore.add("§e"+content.substring(index, index+8));
					index +=8;
				}
	
				lore.add("§d发布人:"+vip.get(vip.size()-1));
				boolean alreadyHad = false;
				for(ArrayList<String> my:plugin.playerQuest.get(p.getName()))
				{
					if(plugin.isSame(vip, my))
						alreadyHad = true;
				}
				if(!alreadyHad)
					lore.add("§a点击领取该任务");
				else
					lore.add("§c你已领取该任务");
				metaItem.setLore(lore);
				item.setItemMeta(metaItem);
				inv.setItem(indexOfInv, item);
				indexOfInv += 1;

			}

			if(size>0)
			{
				// get the normal type of quests, show them between index 9 to 45
				indexOfInv = 9;
				for(int x=0; x<36; x++)
				{
					if(size>36)
					{
						if(x==size-j)
							break;
					}
					else
					{
						if(x==size)
							break;
					}

					ArrayList<String> i = questList.get(indexOfNormalQuest);
					
					ItemStack item = null;
					ItemMeta metaItem = null;
					String type = i.get(0);
					String name = i.get(1);
					String content = i.get(2);
					String playerName = i.get(i.size()-1);
					ArrayList<String> lore = new ArrayList<String>();
					int length = 0;
					if(type.equalsIgnoreCase("item"))
					{
						item = new ItemStack(340);
						metaItem = item.getItemMeta();
						metaItem.setDisplayName("§a["+plugin.message.get("QuestGui.ItemQuestTag")+"§a]§f"+name);
					}
					else if(type.equalsIgnoreCase("head"))
					{
						item = new ItemStack(397, 1, (short)3);
						metaItem = item.getItemMeta();
						metaItem.setDisplayName("§a["+plugin.message.get("QuestGui.HeadQuestTag")+"§a]§f"+name);
					}

					if(content.length()%8==0)
						length = content.length()/8;
					else
						length = (content.length()/8)+1;
					int index = 0;
					for(int c=0; c<length ; c++)
					{
						if(c==length-1)
						{
							lore.add("§e"+content.substring(index, content.length()));
							break;
						}
						lore.add("§e"+content.substring(index, index+8));
						index +=8;
					}

					lore.add("§d发布人:"+playerName);
					boolean alreadyHad = false;
					for(ArrayList<String> my:plugin.playerQuest.get(p.getName()))
					{
						if(plugin.isSame(i, my))
							alreadyHad = true;
					}
					if(!alreadyHad)
						lore.add("§a点击领取该任务");
					else
						lore.add("§c你已领取该任务");
					metaItem.setLore(lore);
					item.setItemMeta(metaItem);
					inv.setItem(indexOfInv, item);
					indexOfNormalQuest+=1;
					indexOfInv+=1;
				}
			}

			inventory.add(inv);
		}

		return inventory;
	}
}

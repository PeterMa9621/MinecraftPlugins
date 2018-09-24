package friendsHelper;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class FriendsHelperListener implements Listener
{
	private FriendsHelper plugin;

	public FriendsHelperListener(FriendsHelper plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerOpenInventory(InventoryOpenEvent event)
    {
		if(event.getInventory().getTitle().equalsIgnoreCase("§e你的好友:"))
		{
			ItemStack item = plugin.createItem(397,1,3,"§a添加好友","§7点击添加好友");
			event.getInventory().setItem(30, item);
		}
		
		/*
		if(event.getInventory().getTitle().substring(2).matches("[a-zA-Z0-9]*") 
				&& (!event.getInventory().getTitle().equalsIgnoreCase("§7NPC"))
				&& event.getInventory().getTitle().length()>2)
		{
			String name = event.getInventory().getTitle().substring(2);
			if(Bukkit.getServer().getPlayer(name)==null)
			{
				if(Bukkit.getServer().getOfflinePlayer(name)!=null)
				{
					ItemStack item = plugin.createItem(339,1,0,"§a向该好友发送私信","§7点击发送好友私信");
					event.getInventory().setItem(0, item);
				}	
			}
			else
			{
				ItemStack item = plugin.createItem(339,1,0,"§a向该好友发送私信","§7点击发送好友私信");
				event.getInventory().setItem(0, item);
			}
		}
		*/
		return;
    }
	
	@EventHandler
	private void onPlayerClickInventory(PlayerJoinEvent event)
	{
		if(plugin.addPlayers.contains(event.getPlayer().getName()))
		{
			plugin.addPlayers.remove(event.getPlayer().getName());
		}
		
		if(plugin.sendMessage.containsKey(event.getPlayer().getName()))
		{
			plugin.addPlayers.remove(event.getPlayer().getName());
		}
	}
	
	@EventHandler
	private void onPlayerClickInventory(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase("§e你的好友:"))
		{
			if(event.getRawSlot()==30)
			{
				plugin.addPlayers.add(event.getWhoClicked().getName());
				event.getWhoClicked().sendMessage("§7[§6好友系统§7] §e请打开聊天框并输入要添加为好友的玩家名字:");
				event.getWhoClicked().closeInventory();
			}
			return;
		}
		
		/*
		if(event.getInventory().getTitle().substring(2).matches("[a-zA-Z0-9]*") 
				&& (!event.getInventory().getTitle().equalsIgnoreCase("§7NPC"))
				&& event.getInventory().getTitle().length()>2)
		{
			String name = event.getInventory().getTitle().substring(2);
			if(Bukkit.getServer().getPlayer(name)==null)
			{
				if(Bukkit.getServer().getOfflinePlayer(name)!=null)
				{
					if(event.getRawSlot()==0)
					{
						plugin.sendMessage.put(event.getWhoClicked().getName(), name);
						event.getWhoClicked().sendMessage("§7[§6好友系统§7] §e请打开聊天框并输入要发送的聊天内容:");
						event.getWhoClicked().closeInventory();
					}
				}	
			}
			else
			{
				plugin.sendMessage.put(event.getWhoClicked().getName(), name);
				event.getWhoClicked().sendMessage("§7[§6好友系统§7] §e请打开聊天框并输入要发送的聊天内容:");
				event.getWhoClicked().closeInventory();
			}
		}
		*/
	}
	
	@EventHandler
	private void onPlayerChat(PlayerChatEvent event)
	{
		if(plugin.addPlayers.contains(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			String name = event.getMessage();
			event.getPlayer().performCommand("friends add " + name);
			//event.getPlayer().chat("/f add "+event.getMessage());
			plugin.addPlayers.remove(event.getPlayer().getName());
			return;
		}
		
		if(plugin.sendMessage.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			String message = event.getMessage();
			String name = plugin.sendMessage.get(event.getPlayer().getName());
			event.getPlayer().performCommand("friends msg " + name + " " + message);
			//event.getPlayer().chat("/f msg "+plugin.sendMessage.get(event.getPlayer().getName())+" "+event.getMessage());
			plugin.sendMessage.remove(event.getPlayer().getName());
			return;
		}
	}
}

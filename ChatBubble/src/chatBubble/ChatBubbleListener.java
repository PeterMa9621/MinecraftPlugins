package chatBubble;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;


public class ChatBubbleListener implements Listener
{
	private ChatBubble plugin;
	
	HashMap<String, ChatInfo> chat = new HashMap<String, ChatInfo>();

	public ChatBubbleListener(ChatBubble plugin)
	{
		this.plugin=plugin;
	}

	@EventHandler
	public void onChat(PlayerChatEvent event)
	{
		if (!event.isCancelled())
	    {
	    	final Player p = event.getPlayer();
	    	final Hologram holo = (Hologram)HologramsAPI.createHologram(plugin, p.getLocation().add(0.0D, 2.0D, 0.0D));

	    	if(chat.containsKey(p.getName()))
	    	{
	    		ChatInfo preChatInfo = chat.get(p.getName());
	    		plugin.getServer().getScheduler().cancelTask(preChatInfo.getTaskID());
	    		preChatInfo.getGram().delete();
	    		chat.remove(p.getName());
	    	}
	    	
	    	String msg = event.getMessage();
	    	if(event.getMessage().contains("[i]"))
	    		msg = msg.replace("[i]", "§a§l[物品信息]");
	    	if(event.getMessage().contains("{item}"))
	    		msg = msg.replace("{item}", "§a§l[物品信息]");
	    	
	    	if(plugin.playerData.containsKey(p.getName()))
	    		holo.appendTextLine("§e聊天§b§l-> " + plugin.playerData.get(p.getName()) + msg);
	    	else
	    		holo.appendTextLine("§e聊天§b§l-> " + msg);
	    	
	    	int id = new BukkitRunnable()
	    	{
	    		int ticksRun;
	        
	    		public void run()
	    		{
	    			this.ticksRun += 1;
	    			holo.teleport(p.getLocation().add(0.0D, 3.0D, 0.0D));
	    			if (this.ticksRun > 100)
	    			{
	    				holo.delete();
	    				chat.remove(p.getName());
	    				cancel();
	    			}	
	    		}
	    	}.runTaskTimer(plugin, 0L, 1L).getTaskId();
	    	ChatInfo chatInfo = new ChatInfo(id, holo);
	    	chat.put(p.getName(), chatInfo);
	    }
	}
	
	@EventHandler
	public void onPlayerClickGui(InventoryClickEvent event)
    {
		if(event.getInventory().getTitle().equalsIgnoreCase("§1聊§2天§3气§4泡§5颜§6色§7切§8换"))
		{
			Player p = (Player)event.getWhoClicked();
			event.setCancelled(true);
			if(event.getRawSlot()<0 && event.getRawSlot()>14)
				return;
			if(event.getRawSlot()==0)
			{
				plugin.playerData.put(p.getName(), "§1");
			}
			else if(event.getRawSlot()==1)
			{
				plugin.playerData.put(p.getName(), "§2");
			}
			else if(event.getRawSlot()==2)
			{
				plugin.playerData.put(p.getName(), "§3");
			}
			else if(event.getRawSlot()==3)
			{
				plugin.playerData.put(p.getName(), "§4");
			}
			else if(event.getRawSlot()==4)
			{
				plugin.playerData.put(p.getName(), "§5");
			}
			else if(event.getRawSlot()==5)
			{
				plugin.playerData.put(p.getName(), "§6");
			}
			else if(event.getRawSlot()==6)
			{
				plugin.playerData.put(p.getName(), "§7");
			}
			else if(event.getRawSlot()==7)
			{
				plugin.playerData.put(p.getName(), "§8");
			}
			else if(event.getRawSlot()==8)
			{
				plugin.playerData.put(p.getName(), "§9");
			}
			else if(event.getRawSlot()==9)
			{
				plugin.playerData.put(p.getName(), "§a");
			}
			else if(event.getRawSlot()==10)
			{
				plugin.playerData.put(p.getName(), "§b");
			}
			else if(event.getRawSlot()==11)
			{
				plugin.playerData.put(p.getName(), "§c");
			}
			else if(event.getRawSlot()==12)
			{
				plugin.playerData.put(p.getName(), "§d");
			}
			else if(event.getRawSlot()==13)
			{
				plugin.playerData.put(p.getName(), "§e");
			}
			else if(event.getRawSlot()==14)
			{
				plugin.playerData.put(p.getName(), "§f");
			}
			p.closeInventory();
			p.sendMessage("§6切换"+plugin.playerData.get(p.getName())+"颜色"+"§6成功!");
		}
    }
		

}

package queueSystem;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JoinQueueEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Player p;
    private boolean cancelled;
    QueueSystem plugin;
    
    public JoinQueueEvent(QueueSystem plugin, Player p) 
    {
    	this.plugin=plugin;
        this.p = p;
    }

    public Player getPlayer() 
    {
        return p;
    }
    
    public boolean isPlayerLeader()
    {
    	if(plugin.queuePlayer.get(p)==p)
    		return true;
    	return false;
    }
    
    public Queue getQueue()
    {
    	Player leader=null;
    	if(plugin.queuePlayer.containsKey(p))
    	{
    		leader = plugin.queuePlayer.get(p);
    	}
    	if(leader!=null)
    		return plugin.queue.get(leader);
    	else
    		return null;
    }

    @Override
    public HandlerList getHandlers() 
    {
        return handlers;
    }

    public static HandlerList getHandlerList() 
    {
        return handlers;
    }

    public boolean isCancelled() 
    {
        return cancelled;
    }

    public void setCancelled(boolean cancel) 
    {
        this.cancelled = cancel;
    }

}
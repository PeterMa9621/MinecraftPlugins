package dps.event;

import dps.Dps;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LeaveDpsEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Player p;
    private boolean cancelled;
    Dps plugin;
    
    public LeaveDpsEvent(Dps plugin, Player p) 
    {
    	this.plugin=plugin;
        this.p = p;
    }

    public Player getPlayer() 
    {
        return p;
    }
    /*
    public int getRank()
    {
    	return plugin.getRank(p);
    }

     */

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
package levelSystem;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LevelUpEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Player p;
    private boolean cancelled;
    
    public LevelUpEvent(Player p) 
    {
        this.p = p;
    }

    public Player getPlayer() 
    {
        return p;
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
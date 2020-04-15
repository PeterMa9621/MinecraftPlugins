package levelSystem.event;

import levelSystem.model.LevelPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LevelUpEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private LevelPlayer levelPlayer;
    private boolean cancelled;
    
    public LevelUpEvent(LevelPlayer levelPlayer)
    {
        this.levelPlayer = levelPlayer;
    }

    public LevelPlayer getPlayer()
    {
        return levelPlayer;
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
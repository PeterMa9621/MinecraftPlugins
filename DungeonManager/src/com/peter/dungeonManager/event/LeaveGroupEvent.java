package com.peter.dungeonManager.event;

import com.peter.dungeonManager.model.DungeonPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class LeaveGroupEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled;
    private DungeonPlayer dungeonPlayer;

    public LeaveGroupEvent(DungeonPlayer dungeonPlayer) {
        this.dungeonPlayer = dungeonPlayer;
    }

    public DungeonPlayer getDungeonPlayer() {
        return dungeonPlayer;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

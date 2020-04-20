package com.peter.dungeonManager.listener;

import com.peter.dungeonManager.event.LeaveGroupEvent;
import com.peter.dungeonManager.gui.GuiManager;
import com.peter.dungeonManager.model.DungeonGroup;
import com.peter.dungeonManager.model.DungeonPlayer;
import com.peter.dungeonManager.util.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class DungeonGroupListener implements Listener {
    @EventHandler
    public void onPlayerLeaveDungeonGroup(LeaveGroupEvent event) {
        DungeonPlayer dungeonPlayer = event.getDungeonPlayer();
        DungeonGroup dungeonGroup = dungeonPlayer.getDungeonGroup();

        if(dungeonGroup.isLeader(dungeonPlayer)) {
            dungeonGroup.disband(true );
            DataManager.removeDungeonGroup(dungeonGroup);
        } else {
            dungeonGroup.removePlayer(dungeonPlayer);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DungeonPlayer dungeonPlayer = DataManager.getDungeonPlayer(player.getUniqueId());
        if(dungeonPlayer!=null && dungeonPlayer.isInDungeonGroup()) {
            LeaveGroupEvent leaveGroupEvent = new LeaveGroupEvent(dungeonPlayer);
            Bukkit.getPluginManager().callEvent(leaveGroupEvent);
        }
        DataManager.removeDungeonPlayer(player.getUniqueId());
    }
}

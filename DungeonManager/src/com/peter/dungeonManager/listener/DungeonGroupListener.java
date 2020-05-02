package com.peter.dungeonManager.listener;

import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.event.LeaveGroupEvent;
import com.peter.dungeonManager.model.DungeonGroup;
import com.peter.dungeonManager.model.DungeonPlayer;
import de.erethon.dungeonsxl.event.dplayer.instance.game.DGamePlayerFinishEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Calendar;
import java.util.HashMap;

public class DungeonGroupListener implements Listener {
    private DungeonManager plugin;
    public DungeonGroupListener(DungeonManager plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeaveDungeonGroup(LeaveGroupEvent event) {
        DungeonPlayer dungeonPlayer = event.getDungeonPlayer();
        DungeonGroup dungeonGroup = dungeonPlayer.getDungeonGroup();

        if(dungeonGroup.isLeader(dungeonPlayer)) {
            dungeonGroup.disband(true );
            plugin.dataManager.removeDungeonGroup(dungeonGroup);
        } else {
            dungeonGroup.removePlayer(dungeonPlayer);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DungeonPlayer dungeonPlayer = plugin.dataManager.getDungeonPlayer(player);
        if(dungeonPlayer!=null && dungeonPlayer.isInDungeonGroup()) {
            LeaveGroupEvent leaveGroupEvent = new LeaveGroupEvent(dungeonPlayer);
            Bukkit.getPluginManager().callEvent(leaveGroupEvent);
        }
        plugin.dataManager.removeDungeonPlayer(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerFinishDungeon(DGamePlayerFinishEvent event) {
        Player player = event.getDPlayer().getPlayer();
        HashMap<String, Long> timestamps = plugin.dataManager.getDungeonPlayer(player).getDungeonTimestamps();
        String dungeonName = event.getDPlayer().getDGroup().getDungeonName();
        long timestamp = Calendar.getInstance().getTimeInMillis();
        timestamps.put(dungeonName, timestamp);
    }

    // This part is only for the newest version of DungeonsXL
    /*
    @EventHandler
    public void onPlayerFinishDungeon(GamePlayerFinishEvent event) {
        Player player = event.getBukkitPlayer();
        HashMap<String, Long> timestamps = plugin.dataManager.getDungeonPlayer(player).getDungeonTimestamps();
        GamePlayer gamePlayer = event.getGamePlayer();
        PlayerGroup playerGroup = gamePlayer.getGroup();
        String dungeonName = playerGroup.getDungeon().getName();
        long timestamp = Calendar.getInstance().getTimeInMillis();
        timestamps.put(dungeonName, timestamp);
    }

     */
}

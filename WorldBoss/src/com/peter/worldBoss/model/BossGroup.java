package com.peter.worldBoss.model;

import com.peter.worldBoss.WorldBoss;
import de.erethon.dungeonsxl.player.DGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class BossGroup {
    private String groupName;
    private ArrayList<Player> players;

    public BossGroup(String groupName, Player leader) {
        this.groupName = groupName;
        players = new ArrayList<>();
        players.add(leader);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void startGame(String startGameCmd, WorldBoss plugin) {
        Bukkit.getConsoleSender().sendMessage("Start Game!");
        /* 
        int numPlayer = dGroup.getPlayers().size();
        Bukkit.getConsoleSender().sendMessage("Total Players: " + numPlayer);
        if(numPlayer<=0){
            return;
        }
        Player leader = dGroup.getCaptain();
        leader.addAttachment(plugin, "dxl.play", true);
        Bukkit.dispatchCommand(leader, startGameCmd);
        leader.addAttachment(plugin, "dxl.play", false);

         */
    }
}

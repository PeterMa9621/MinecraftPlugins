package com.peter.worldBoss.model;

import com.peter.worldBoss.WorldBoss;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class BossGroup {
    private String groupName;
    private ArrayList<Player> players;

    public BossGroup(String groupName) {
        this.groupName = groupName;
        players = new ArrayList<>();
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

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public Boolean containsPlayer(Player player) {
        return players.contains(player);
    }

    public void startGame(String startGameCmd, WorldBoss plugin) {
        //Bukkit.getConsoleSender().sendMessage("Start Game!");

        int numPlayer = this.players.size();
        //Bukkit.getConsoleSender().sendMessage("Total Players: " + numPlayer);
        if(numPlayer<=0){
            return;
        }
        Player leader = this.players.get(0);

        dispatchDungeonCommand(leader, false, plugin);

        for(int i=1; i<numPlayer; i++){
            dispatchDungeonCommand(players.get(i), true, plugin);
        }
        dispatchStartGameCommand(leader, startGameCmd, plugin);
    }

    private void dispatchDungeonCommand(Player player, Boolean isJoin, WorldBoss plugin) {
        // Add permission temporally
        if(!player.isOp()){
            player.addAttachment(plugin, "essentials.commandcooldowns.bypass", true);
            player.addAttachment(plugin, "dxl.group", true);
            player.addAttachment(plugin, "dxl.bypass", true);
        }

        if(isJoin){
            Bukkit.dispatchCommand(player, "dxl leave " + this.groupName);
            Bukkit.dispatchCommand(player, "dxl group join " + this.groupName);
        }
        else{
            Bukkit.dispatchCommand(player, "dxl leave " + this.groupName);
            Bukkit.dispatchCommand(player, "dxl group create " + this.groupName);
        }

        // Remove permission
        if(!player.isOp()){
            player.addAttachment(plugin, "essentials.commandcooldowns.bypass", false);
            player.addAttachment(plugin, "dxl.group", false);
            player.addAttachment(plugin, "dxl.bypass", false);
        }
    }

    private void dispatchStartGameCommand(Player leader, String startGameCmd, WorldBoss plugin) {
        if(!leader.isOp()){
            leader.addAttachment(plugin, "essentials.commandcooldowns.bypass", true);
            leader.addAttachment(plugin, "dxl.play", true);
            leader.addAttachment(plugin, "dxl.bypass", true);
        }

        Bukkit.dispatchCommand(leader, startGameCmd);

        if(!leader.isOp()){
            leader.addAttachment(plugin, "essentials.commandcooldowns.bypass", false);
            leader.addAttachment(plugin, "dxl.play", false);
            leader.addAttachment(plugin, "dxl.bypass", false);
        }
    }
}

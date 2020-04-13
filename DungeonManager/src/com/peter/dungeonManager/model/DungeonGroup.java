package com.peter.dungeonManager.model;

import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.util.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class DungeonGroup {
    private String groupName;
    private ArrayList<DungeonPlayer> players;
    private DungeonSetting dungeonSetting;
    private String dungeonName;
    private DungeonPlayer leader;

    public DungeonGroup(String groupName, String dungeonName, DungeonSetting dungeonSetting, DungeonPlayer leader) {
        this.groupName = groupName;
        this.dungeonSetting = dungeonSetting;
        this.dungeonName = dungeonName;
        this.leader = leader;
        players = new ArrayList<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<DungeonPlayer> getPlayers() {
        return players;
    }

    public void addPlayer(DungeonPlayer player) {
        if(!this.players.contains(player)) {
            this.players.add(player);
            DataManager.getDungeonPlayer(player.getPlayer().getUniqueId()).joinDungeonGroup(this);
        }
    }

    public void removePlayer(DungeonPlayer player) {
        this.players.remove(player);
        DataManager.getDungeonPlayer(player.getPlayer().getUniqueId()).leaveDungeonGroup();
    }

    public void removePlayer(Player player) {
        players.removeIf(dungeonPlayer -> dungeonPlayer.getPlayer().equals(player));
        DataManager.getDungeonPlayer(player.getUniqueId()).leaveDungeonGroup();
    }

    public void disband() {
        for(DungeonPlayer dungeonPlayer:this.players) {
            dungeonPlayer.getPlayer().sendMessage(ChatColor.GREEN + "[副本系统]" + ChatColor.GOLD + " 你的队伍已解散!");
            dungeonPlayer.leaveDungeonGroup();
        }
    }

    public Boolean isLeader(Player player) {
        return leader.getPlayer().equals(player);
    }

    public Boolean isLeader(DungeonPlayer dungeonPlayer) {
        return leader.equals(dungeonPlayer);
    }

    public Boolean containsPlayer(DungeonPlayer player) {
        return players.contains(player);
    }

    public Boolean containsPlayer(Player player) {
        for(DungeonPlayer dungeonPlayer:players) {
            if(dungeonPlayer.getPlayer().equals(player))
                return true;
        }
        return false;
    }

    public String getDungeonName() {
        return dungeonName;
    }

    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    public DungeonSetting getDungeonSetting() {
        return dungeonSetting;
    }

    public void startGame(String startGameCmd, DungeonManager plugin) {

        int numPlayer = this.players.size();
        if(numPlayer<=0){
            return;
        }
        Player leader = this.players.get(0).getPlayer();

        dispatchDungeonCommand(leader, false, plugin);

        for(int i=1; i<numPlayer; i++){
            dispatchDungeonCommand(players.get(i).getPlayer(), true, plugin);
        }
        dispatchStartGameCommand(leader, startGameCmd, plugin);
    }

    private void dispatchDungeonCommand(Player player, Boolean isJoin, DungeonManager plugin) {
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

    private void dispatchStartGameCommand(Player leader, String startGameCmd, DungeonManager plugin) {
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

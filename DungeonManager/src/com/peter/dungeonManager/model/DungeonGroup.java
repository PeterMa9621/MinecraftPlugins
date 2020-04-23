package com.peter.dungeonManager.model;

import com.peter.dungeonManager.DungeonManager;
import com.peter.dungeonManager.config.ConfigManager;
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
    private String startGameCmd;
    private final String startGameTitle = ChatColor.GOLD + "副本将在" + ChatColor.GREEN + ConfigManager.startGameDelay +
            ChatColor.GOLD + "秒后开始，请做好准备";
    private final String disbandNotification = ChatColor.GOLD + "你的队伍已解散!";

    public DungeonGroup(String groupName, String dungeonName, DungeonSetting dungeonSetting, DungeonPlayer leader) {
        this.groupName = groupName;
        this.dungeonSetting = dungeonSetting;
        this.dungeonName = dungeonName;
        this.leader = leader;
        startGameCmd = "dxl play " + dungeonName;
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

    public Boolean addPlayer(DungeonPlayer player) {
        if(!this.players.contains(player)) {
            this.players.add(player);
            player.joinDungeonGroup(this);
            return true;
        }
        return false;
    }

    public void removePlayer(DungeonPlayer player) {
        this.players.remove(player);
        player.leaveDungeonGroup();
    }

    public void disband(Boolean notifyPlayer) {
        for(DungeonPlayer dungeonPlayer:this.players) {
            if(notifyPlayer) {
                dungeonPlayer.getPlayer().sendMessage(ChatColor.GREEN + "[副本系统] " + disbandNotification);
                dungeonPlayer.getPlayer().sendTitle(disbandNotification, "", 5, 3*20, 5);
            }
            dungeonPlayer.leaveDungeonGroup();
            dungeonPlayer.setWaitForStart(false);
        }
    }

    public Boolean isSatisfyNumRequirement() {
        int numPlayers = players.size();
        int minPlayers = dungeonSetting.getMinPlayers();
        int maxPlayers = dungeonSetting.getMaxPlayers();
        return numPlayers >= minPlayers && numPlayers <= maxPlayers;
    }

    public Boolean isFull() {
        return dungeonSetting.getMaxPlayers() == players.size();
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

    public void startGame(DungeonManager plugin) {
        int numPlayer = this.players.size();
        if(numPlayer<=0){
            return;
        }

        for(DungeonPlayer dungeonPlayer:this.players) {
            dungeonPlayer.getPlayer().sendTitle(this.startGameTitle, "", 5,4*20,5);
            dungeonPlayer.setWaitForStart(true);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player leader = this.players.get(0).getPlayer();

            dispatchDungeonCommand(leader, false, plugin);

            for(int i=1; i<numPlayer; i++){
                dispatchDungeonCommand(players.get(i).getPlayer(), true, plugin);
            }

            dispatchStartGameCommand(leader, this.startGameCmd, plugin);

            disband(false);
        }, ConfigManager.startGameDelay * 20);
    }

    private void dispatchDungeonCommand(Player player, Boolean isJoin, DungeonManager plugin) {
        // Add permission temporally
        if(!player.isOp()){
            player.addAttachment(plugin, "essentials.commandcooldowns.bypass", true);
            player.addAttachment(plugin, "dxl.group", true);
            player.addAttachment(plugin, "dxl.bypass", true);
        }

        if(isJoin){
            //Bukkit.dispatchCommand(player, "dxl leave " + this.groupName);
            Bukkit.dispatchCommand(player, "dxl group join " + this.groupName);
        }
        else{
            //Bukkit.dispatchCommand(player, "dxl leave " + this.groupName);
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

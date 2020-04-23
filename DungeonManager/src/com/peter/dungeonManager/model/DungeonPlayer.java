package com.peter.dungeonManager.model;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DungeonPlayer {
    private Player player;
    private DungeonGroup dungeonGroup;
    private Boolean isInDungeonGroup = false;
    private Boolean waitForStart = false;
    private int currentJoinTeamViewPage = 0;
    private int currentCreateTeamViewPage = 0;
    private HashMap<String, Long> dungeonTimestamps;

    public DungeonPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public DungeonGroup getDungeonGroup() {
        return dungeonGroup;
    }

    public Boolean isInDungeonGroup() {
        return isInDungeonGroup;
    }

    public void leaveDungeonGroup() {
        this.dungeonGroup = null;
        this.isInDungeonGroup = false;
    }

    public void joinDungeonGroup(DungeonGroup dungeonGroup) {
        this.dungeonGroup = dungeonGroup;
        this.isInDungeonGroup = true;
    }

    public int getCurrentJoinTeamViewPage() {
        return currentJoinTeamViewPage;
    }

    public void setCurrentJoinTeamViewPage(int currentJoinTeamViewPage) {
        this.currentJoinTeamViewPage = currentJoinTeamViewPage;
    }

    public int getCurrentCreateTeamViewPage() {
        return currentCreateTeamViewPage;
    }

    public void setCurrentCreateTeamViewPage(int currentCreateTeamViewPage) {
        this.currentCreateTeamViewPage = currentCreateTeamViewPage;
    }

    public void setWaitForStart(Boolean isWaitingForStart) {
        this.waitForStart = isWaitingForStart;
    }

    public Boolean isWaitingForStart() {
        return this.waitForStart;
    }

    public HashMap<String, Long> getDungeonTimestamps() {
        return dungeonTimestamps;
    }

    public void setDungeonTimestamps(HashMap<String, Long> dungeonTimestamps) {
        this.dungeonTimestamps = dungeonTimestamps;
    }

    public Date getDungeonLastPlayedTime(String dungeonName) {
        if(dungeonTimestamps.containsKey(dungeonName)) {
            Timestamp timestamp = new Timestamp(dungeonTimestamps.get(dungeonName));
            return new Date(timestamp.getTime());
        }
        return null;
    }

    public String getDungeonLastPlayedTimeString(String dungeonName) {
        Date date = getDungeonLastPlayedTime(dungeonName);
        if(date==null)
            return ChatColor.GRAY + "您没有完成过该副本";
        else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return ChatColor.GRAY + "上次通关: " + dateFormat.format(date);
        }
    }
}

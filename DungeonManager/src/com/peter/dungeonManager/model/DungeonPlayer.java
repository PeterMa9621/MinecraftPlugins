package com.peter.dungeonManager.model;

import org.bukkit.entity.Player;

public class DungeonPlayer {
    private Player player;
    private DungeonGroup dungeonGroup;
    private Boolean isInDungeonGroup = false;
    private Boolean waitForStart = false;
    private int currentJoinTeamViewPage = 0;
    private int currentCreateTeamViewPage = 0;

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
}

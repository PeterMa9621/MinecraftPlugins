package com.peter.worldBoss.model;

import org.bukkit.entity.Player;

public class BossPlayer {
    private BossGroup group;
    private Player player;

    public BossPlayer(BossGroup group, Player player) {
        this.group = group;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public BossGroup getGroup() {
        return group;
    }

    public void setGroup(BossGroup group) {
        this.group = group;
    }
}

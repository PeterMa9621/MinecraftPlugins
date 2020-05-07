package com.peter.worldBoss.model;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BossPlayer {
    private BossGroup group;
    private UUID uuid;

    public BossPlayer(BossGroup group, UUID uuid) {
        this.group = group;
        this.uuid = uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public BossGroup getGroup() {
        return group;
    }

    public void setGroup(BossGroup group) {
        this.group = group;
    }

    public UUID getUuid() {
        return uuid;
    }
}

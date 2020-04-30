package com.peter.manager;

import com.peter.FestivalReward;
import com.peter.model.FestivalPlayer;

import java.util.HashMap;
import java.util.UUID;

public class FestivalPlayerManager {
    private FestivalReward plugin;
    private HashMap<UUID, FestivalPlayer> festivalPlayers;
    public FestivalPlayerManager(FestivalReward plugin) {
        this.plugin = plugin;
        festivalPlayers = new HashMap<>();
    }

    public HashMap<UUID, FestivalPlayer> getFestivalPlayers() {
        return festivalPlayers;
    }

    public void addFestivalPlayer(FestivalPlayer festivalPlayer) {
        UUID uuid = festivalPlayer.getPlayer().getUniqueId();
        this.festivalPlayers.put(uuid, festivalPlayer);
    }

    public FestivalPlayer getFestivalPlayer(UUID uuid) {
        return this.festivalPlayers.get(uuid);
    }

    public boolean containFestivalPlayer(UUID uuid) {
        return festivalPlayers.containsKey(uuid);
    }
}

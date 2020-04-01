package dps.model;

import dps.listener.DpsPlayerListener;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DpsPlayer {
    private Player player;
    private Double dpsScore;
    private Boolean isInDpsMode;
    private DpsPlayerListener dpsListener;
    private Integer rank = 1;
    private Integer groupSize = 1;
    private UUID dungeonId;
    private int numRewards;
    private String dungeonName;

    public DpsPlayer(Player player, Double dpsScore, Boolean isInDpsMode, UUID dungeonId, String dungeonName, DpsPlayerListener dpsListener) {
        this.player = player;
        this.dpsScore = dpsScore;
        this.isInDpsMode = isInDpsMode;
        this.dpsListener = dpsListener;
        this.dungeonId = dungeonId;
        this.dungeonName = dungeonName;
    }

    public void clearDpsScore() {
        this.dpsScore = 0.0;
        this.dpsListener.call();
    }

    public Double getDpsScore() {
        return dpsScore;
    }

    public Boolean isInDpsMode() {
        return isInDpsMode;
    }

    public void setDpsStatus(Boolean isInDpsMode) {
        this.isInDpsMode = isInDpsMode;
        this.dpsListener.call();
    }

    public void setDpsScore(Double dpsScore) {
        this.dpsScore = dpsScore;
        this.dpsListener.call();
    }

    public Player getPlayer() {
        return player;
    }

    public Integer getRank() {
        return this.rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(Integer groupSize) {
        this.groupSize = groupSize;
    }

    public UUID getDungeonId() {
        return dungeonId;
    }

    public void setNumRewards(int numRewards) {
        this.numRewards = numRewards;
    }

    public int getNumRewards() {
        return numRewards;
    }

    public String getDungeonName() {
        return dungeonName;
    }
}

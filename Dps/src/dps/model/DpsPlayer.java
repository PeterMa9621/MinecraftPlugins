package dps.model;

import dps.listener.DpsPlayerListener;
import org.bukkit.entity.Player;

public class DpsPlayer {
    private Player player;
    private Double dpsScore;
    private Boolean isInDpsMode;
    private DpsPlayerListener dpsListener;
    private Integer rank = 1;
    private Integer groupSize = 1;

    public DpsPlayer(Player player, Double dpsScore, Boolean isInDpsMode, DpsPlayerListener dpsListener) {
        this.player = player;
        this.dpsScore = dpsScore;
        this.isInDpsMode = isInDpsMode;
        this.dpsListener = dpsListener;
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
}

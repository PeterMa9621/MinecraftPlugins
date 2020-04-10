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
    /**
     *  GroupSize is meaning the size of the group that the player is in
     */
    private Integer groupSize = 1;
    private UUID dungeonWorldId;
    private int numRewards;
    private String dungeonName;

    /**
     *  Used to check if the player is in a dungeon or not
     */
    private Boolean isInDungeon = true;

    /**
     *  The number of bonus rewards for this player in this dungeon
     */
    private Integer numBonusReward = 0;

    public DpsPlayer(Player player, Double dpsScore, Boolean isInDpsMode, UUID dungeonWorldId, String dungeonName, DpsPlayerListener dpsListener) {
        this.player = player;
        this.dpsScore = dpsScore;
        this.isInDpsMode = isInDpsMode;
        this.dpsListener = dpsListener;
        this.dungeonWorldId = dungeonWorldId;
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

    public void addBonusReward() {
        this.numBonusReward += 1;
        //RewardBoxManager.updateDpsPlayer(player.getUniqueId(), this);
    }

    public Integer getNumBonusReward() {
        return numBonusReward;
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

    public UUID getDungeonWorldId() {
        return dungeonWorldId;
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

    public Boolean isInDungeon() {
        return isInDungeon;
    }

    public void exitDungeon() {
        this.isInDungeon = false;
    }

    public void enterDungeon() {
        this.isInDungeon = true;
    }
}

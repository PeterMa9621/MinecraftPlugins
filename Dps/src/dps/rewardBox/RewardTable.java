package dps.rewardBox;

import java.util.HashMap;

public class RewardTable {
    private String dungeonName;
    private HashMap<String, Double> rewards;

    public RewardTable(String dungeonName, HashMap<String, Double> rewards) {
        this.dungeonName = dungeonName;
        this.rewards = rewards;
    }

    public HashMap<String, Double> getRewards() {
        return rewards;
    }

    public void setRewards(HashMap<String, Double> rewards) {
        this.rewards = rewards;
    }

    public String getDungeonName() {
        return dungeonName;
    }

    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }
}

package levelSystem.manager;

import levelSystem.LevelSystem;
import levelSystem.model.LevelReward;

import java.util.ArrayList;
import java.util.HashMap;

public class RewardManager {
    private LevelSystem plugin;
    private HashMap<Integer, LevelReward> levelRewardHashMap;

    public RewardManager(LevelSystem plugin) {
        this.plugin = plugin;
        levelRewardHashMap = new HashMap<>();
    }

    public LevelReward getReward(int level) {
        return levelRewardHashMap.get(level);
    }

    public void addReward(int level, LevelReward levelReward) {
        this.levelRewardHashMap.put(level, levelReward);
    }

    public void clear() {
        levelRewardHashMap.clear();
    }
}

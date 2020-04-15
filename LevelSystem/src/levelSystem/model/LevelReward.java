package levelSystem.model;

import java.util.List;

public class LevelReward {
    private int level;
    private List<String> commands;

    public LevelReward(int level, List<String> commands) {
        this.level = level;
        this.commands = commands;
    }

    public int getLevel() {
        return level;
    }

    public List<String> getCommands() {
        return commands;
    }
}

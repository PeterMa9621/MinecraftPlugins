package levelSystem.model;

import java.util.List;

public class LevelReward {
    private int level;
    private List<String> commands;
    private String msg;

    public LevelReward(int level, List<String> commands, String msg) {
        this.level = level;
        this.commands = commands;
        this.msg = msg;
    }

    public int getLevel() {
        return level;
    }

    public List<String> getCommands() {
        return commands;
    }

    public String getMsg() {
        return msg;
    }
}

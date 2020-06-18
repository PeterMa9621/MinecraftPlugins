package clockGUI.manager;

import clockGUI.ClockGUI;
import clockGUI.model.PlayerData;

import java.util.HashMap;

public class DataManager {
    private ClockGUI plugin;
    private HashMap<String, PlayerData> playerData = new HashMap<String, PlayerData>();
    public DataManager(ClockGUI plugin) {
        this.plugin = plugin;
    }

    public HashMap<String, PlayerData> getPlayerData() {
        return playerData;
    }
}

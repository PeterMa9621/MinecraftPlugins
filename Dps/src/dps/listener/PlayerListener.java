package dps.listener;

import dps.model.DpsPlayer;
import dps.util.ScoreBoardUtil;

import java.util.HashMap;
import java.util.UUID;

public class PlayerListener implements DpsPlayerListener {
    private HashMap<UUID, DpsPlayer> dpsPlayers;
    public PlayerListener(HashMap<UUID, DpsPlayer> dpsPlayers) {
        this.dpsPlayers = dpsPlayers;
    }

    @Override
    public void call() {
        ScoreBoardUtil.displayDpsBoard(dpsPlayers);
    }
}

package com.peter.dungeonManager.model;

import java.util.ArrayList;

public class GroupResponse {
    private boolean canCreateOrJoin;
    private ArrayList<String> reason;

    public GroupResponse(boolean canCreateOrJoin, ArrayList<String> reason) {
        this.canCreateOrJoin = canCreateOrJoin;
        this.reason = reason;
    }

    public ArrayList<String> getReason() {
        return reason;
    }

    public boolean canCreateOrJoinGroup() {
        return canCreateOrJoin;
    }
}

package com.peter.model;

import java.util.List;

public class Festival {
    private String date;
    private List<String> commands;
    private String festivalName;
    private List<String> describe;
    private int numReward;

    public Festival(String date, List<String> commands, String festivalName, List<String> describe, int numReward) {
        this.date = date;
        this.commands = commands;
        this.festivalName = festivalName;
        this.describe = describe;
        this.numReward = numReward;
    }

    public List<String> getCommands() {
        return commands;
    }

    public String getDate() {
        return date;
    }

    public String getFestivalName() {
        return festivalName;
    }

    public List<String> getDescribe() {
        return describe;
    }

    public int getNumReward() {
        return numReward;
    }
}

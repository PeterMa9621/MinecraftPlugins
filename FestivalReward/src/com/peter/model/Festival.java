package com.peter.model;

import java.util.List;

public class Festival {
    private String date;
    private List<String> commands;

    public Festival(String date, List<String> commands) {
        this.date = date;
        this.commands = commands;
    }

    public List<String> getCommands() {
        return commands;
    }

    public String getDate() {
        return date;
    }
}

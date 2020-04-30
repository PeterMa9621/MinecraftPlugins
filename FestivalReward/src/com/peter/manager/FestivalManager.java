package com.peter.manager;

import com.peter.FestivalReward;
import com.peter.model.Festival;

import java.util.HashMap;

public class FestivalManager {
    private FestivalReward plugin;
    private HashMap<String, Festival> festivalHashMap;
    public FestivalManager(FestivalReward plugin) {
        this.plugin = plugin;
        festivalHashMap = new HashMap<>();
    }

    public void addFestival(Festival festival) {
        String date = festival.getDate();
        festivalHashMap.put(date, festival);
    }

    public Festival getFestival(String date) {
        return festivalHashMap.get(date);
    }
}

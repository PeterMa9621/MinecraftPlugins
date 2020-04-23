package com.peter.dungeonManager.manager;

import com.peter.dungeonManager.model.DungeonGroup;
import com.peter.dungeonManager.model.DungeonPlayer;
import com.peter.dungeonManager.model.DungeonSetting;
import com.peter.dungeonManager.model.GroupResponse;
import com.peter.dungeonManager.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DungeonGroupManager {
    public static final String minLevelNotSatisfy = "��c��ĵȼ������㸱��Ҫ��";
    public static final String playIntervalNotSatisfy = "��c��ȴ���e%d��%dСʱ%d���ӡ�c���ٴ���ս";
    public static final String groupFullNotification = ChatColor.RED + "��������";

    public static GroupResponse canCreateGroup(DungeonSetting dungeonSetting, DungeonPlayer dungeonPlayer) {
        ArrayList<String> reason = new ArrayList<>();
        boolean canCreate = true;

        Player player = dungeonPlayer.getPlayer();
        if(!dungeonSetting.isSatisfyLevelRequirement(player)) {
            canCreate = false;
            reason.add(minLevelNotSatisfy);
        }

        boolean canPlayInterval = checkPlayInterval(reason, dungeonPlayer, dungeonSetting);
        if(!canPlayInterval) {
            canCreate = false;
        }
        return new GroupResponse(canCreate, reason);
    }

    public static GroupResponse canJoinGroup(DungeonGroup dungeonGroup, DungeonPlayer dungeonPlayer) {
        ArrayList<String> reason = new ArrayList<>();
        boolean canCreate = true;
        DungeonSetting dungeonSetting = dungeonGroup.getDungeonSetting();

        Player player = dungeonPlayer.getPlayer();
        if(!dungeonSetting.isSatisfyLevelRequirement(player)) {
            canCreate = false;
            reason.add(minLevelNotSatisfy);
        }

        boolean canPlayInterval = checkPlayInterval(reason, dungeonPlayer, dungeonSetting);
        if(!canPlayInterval) {
            canCreate = false;
        }

        if(dungeonGroup.isFull()) {
            canCreate = false;
            reason.add(groupFullNotification);
        }

        return new GroupResponse(canCreate, reason);
    }

    private static boolean checkPlayInterval(ArrayList<String> reason, DungeonPlayer dungeonPlayer, DungeonSetting dungeonSetting) {
        boolean canCreate = true;
        String dungeonName = dungeonSetting.getDungeonName();
        Date lastPlayedTime = dungeonPlayer.getDungeonLastPlayedTime(dungeonName);
        if(lastPlayedTime!=null) {
            Date now = Calendar.getInstance().getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastPlayedTime);
            calendar.add(Calendar.HOUR_OF_DAY, dungeonSetting.getPlayInterval());
            Date whenCanPlayAgain = calendar.getTime();
            if(!now.after(whenCanPlayAgain)) {
                canCreate = false;
                Map<TimeUnit, Long> interval = Util.computeDiff(now, whenCanPlayAgain);
                int days = interval.get(TimeUnit.DAYS).intValue();
                int hours = interval.get(TimeUnit.HOURS).intValue();
                int minutes = interval.get(TimeUnit.MINUTES).intValue();
                reason.add(String.format(playIntervalNotSatisfy, days, hours, minutes));
            }
        }
        return canCreate;
    }
}

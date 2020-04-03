package com.peter.worldBoss.model;

import com.peter.worldBoss.WorldBoss;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BossGroupSetting {
    private String groupName;
    private int dayOfWeek;
    private LocalTime startTime;
    private LocalDateTime prevStartTime = null;
    private String displayName = "";
    private Boolean hasNotified = false;
    private String startGameCmd;
    private Boolean isComingSoon = false;

    public BossGroupSetting(String groupName, String startTimeString, int dayOfWeek, String startGameCmd) {
        this.groupName = groupName;
        this.startTime = LocalTime.parse(startTimeString, DateTimeFormatter.ofPattern("HH:mm"));
        this.dayOfWeek = dayOfWeek;
        this.startGameCmd = startGameCmd;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalDateTime getPrevStartTime() {
        return prevStartTime;
    }

    public void setPrevStartTime(LocalDateTime prevStartTime) {
        this.prevStartTime = prevStartTime;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStartGameCmd() {
        return startGameCmd;
    }

    public void setStartGameCmd(String startGameCmd) {
        this.startGameCmd = startGameCmd;
    }

    public Boolean isComingSoon() {
        return isComingSoon;
    }

    public void setIsComingSoon(Boolean isComingSoon) {
        this.isComingSoon = isComingSoon;
    }

    public Boolean isStartedToday() {
        if(this.prevStartTime==null)
            return false;

        LocalDateTime now = LocalDateTime.now();
        int todayDayOfYear = now.getDayOfYear();
        int prevStartDayOfYear = prevStartTime.getDayOfYear();
        return todayDayOfYear == prevStartDayOfYear;
    }

    public Boolean canStart() {
        if(isStartedToday()){
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        int nowHour = now.getHour();
        int nowMinute = now.getMinute();

        int startHour = this.startTime.getHour();
        int startMinute = this.startTime.getMinute();
        if(nowHour == startHour && nowMinute == startMinute){
            this.isComingSoon = false;
            return true;
        }
        return false;
    }

    public void notifyPlayers(WorldBoss plugin) {
        LocalDateTime now = LocalDateTime.now();
        int nowHour = now.getHour();
        int nowMinute = now.getMinute();

        LocalTime tenMinutesBefore = this.startTime.minusMinutes(10);
        if(nowHour == tenMinutesBefore.getHour() && nowMinute == tenMinutesBefore.getMinute() && !hasNotified){
            this.isComingSoon = true;
            Bukkit.broadcastMessage("§6[WorldBoss] §2世界BOSS活动§5" + displayName + "§2将在" + 10 + "分钟后开始!");

            int[] minutes = new int[] {5,4,3,2,1};
            int[] delay = new int[] {5,6,7,8,9};
            for(int i = 0; i<minutes.length; i++){
                int finalI = i;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        hasNotified = false;
                        Bukkit.broadcastMessage("§6[WorldBoss] §2世界BOSS活动§5" + displayName + "§2将在" + (minutes[finalI]) + "分钟后开始!");
                    }
                }.runTaskLater(plugin, 20*60*delay[i]);
            }
            hasNotified = true;
        }


    }
}

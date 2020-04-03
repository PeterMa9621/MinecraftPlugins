package com.peter.worldBoss.model;

import com.peter.worldBoss.WorldBoss;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
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
    private int minuteBefore = 1;
    private WorldBoss plugin;

    public BossGroupSetting(String groupName, String startTimeString, int dayOfWeek, String startGameCmd, WorldBoss plugin) {
        this.groupName = groupName;
        this.startTime = LocalTime.parse(startTimeString, DateTimeFormatter.ofPattern("HH:mm"));
        this.dayOfWeek = dayOfWeek;
        this.startGameCmd = startGameCmd;
        this.plugin = plugin;
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

        LocalTime tenMinutesBefore = this.startTime.minusMinutes(minuteBefore);
        if(nowHour == tenMinutesBefore.getHour() && nowMinute == tenMinutesBefore.getMinute() && !hasNotified){
            this.isComingSoon = true;
            //TextComponent msg = new TextComponent("§6[WorldBoss] §2世界BOSS活动§5" + displayName + "§2将在" + 10 + "分钟后开始!");
            String announcement = "§6[WorldBoss] §2世界BOSS活动§5%s§2将在%d分钟后开始!§d§n点击打开活动界面";
            TextComponent textComponent = new TextComponent(String.format(announcement, displayName, minuteBefore));
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§2点击打开活动界面!").create()));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/boss"));
            plugin.getServer().spigot().broadcast(textComponent);

            int[] minutes = new int[] {5,4,3,2,1};
            int[] delay = new int[] {5,6,7,8,9};
            for(int i = 0; i<minutes.length; i++){
                int finalI = i;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        hasNotified = false;
                        textComponent.setText(String.format(announcement, displayName, minutes[finalI]));
                        plugin.getServer().spigot().broadcast(textComponent);
                    }
                }.runTaskLater(plugin, 20*60*delay[i]);
            }
            hasNotified = true;
        }


    }
}

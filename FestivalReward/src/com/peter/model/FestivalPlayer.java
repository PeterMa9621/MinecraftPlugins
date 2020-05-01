package com.peter.model;

import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FestivalPlayer {
    private Player player;
    private String receiveDate = "1900-01-01";
    private String ip;
    private SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

    public FestivalPlayer(Player player) {
        this.player = player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean hasReceivedReward() {
        return receiveDate.equalsIgnoreCase(date.format(new Date()));
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }

    public void setReceiveDateToToday() {
        ip = player.getAddress().toString().substring(1).split(":")[0];
        this.receiveDate = date.format(new Date());
    }

    public String getReceiveDate() {
        return this.receiveDate;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}

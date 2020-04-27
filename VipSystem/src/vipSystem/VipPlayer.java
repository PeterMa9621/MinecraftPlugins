package vipSystem;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class VipPlayer 
{
	UUID uniqueId;
	String playerName="";
	LocalDateTime regDate;
	LocalDateTime deadline;
	String vipGroup="";
	boolean isExpired = false;

	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public VipPlayer(UUID uniqueId, String playerName, LocalDateTime regDate, LocalDateTime deadline, String vipGroup) {
		this.uniqueId = uniqueId;
		this.playerName = playerName;
		this.regDate = regDate;
		this.deadline = deadline;
		this.vipGroup = vipGroup;
	}
	
	public String getName()
	{
		return playerName;
	}

	public UUID getUniqueId() {
		return this.uniqueId;
	}
	
	public LocalDateTime getDeadline()
	{
		return deadline;
	}
	
	public LocalDateTime getRegDate()
	{
		return regDate;
	}
	
	public String getVipGroup() {
		return vipGroup;
	}

	public String getVipGroupDisplayName() {
		if(checkDeadline())
			return "公民";
		return VipSystem.vipGroups.get(vipGroup);
	}
	
	public int getLeftHours()
	{
		Duration duration = Duration.between(LocalDateTime.now(), deadline);
		int hours = (int)duration.toHours();
		return Math.max(hours, 0);
	}
	
	public int getLeftDays()
	{
		Duration duration = Duration.between(LocalDateTime.now(), deadline);
		int days = (int)duration.toDays();
		return Math.max(days, 0);
	}

	public int getLeftMinutes(){
		Duration duration = Duration.between(LocalDateTime.now(), deadline);
		int minutes = (int)duration.toMinutes();
		return Math.max(minutes, 0);
	}

	public boolean checkDeadline()
	{
		if(isExpired)
			return true;
		isExpired = getLeftMinutes() <= 0;

		return isExpired;
	}
	
	public void setDeadline(LocalDateTime newDeadline)
	{
		deadline = newDeadline;
	}
	
	public String getLeftTime()
	{
		if(checkDeadline())
			return "";
		if(getLeftDays()<=0)
			return String.format("%d天%d小时%d分钟", getLeftDays(), getLeftHours()%24, getLeftMinutes()%60);
		else
			return String.format("%d天%d小时", getLeftDays(), getLeftHours()%24);
	}

	public void clearData() {
		this.isExpired = true;
	}

	public void setIsExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}

	public boolean isExpired() {
		return isExpired;
	}
}

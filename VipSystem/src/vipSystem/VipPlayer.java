package vipSystem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.UUID;

public class VipPlayer 
{
	UUID uniqueId;
	String playerName="";
	LocalDateTime regDate;
	LocalDateTime deadline;
	String vipGroup="";

	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
	
	public String getVipGroup()
	{
		return vipGroup;
	}
	
	public int getRegYear()
	{
		return regDate.getYear();
	}
	
	public int getRegMonth()
	{
		return regDate.getMonthValue();
	}
	
	public int getRegDay()
	{
		return regDate.getDayOfMonth();
	}
	
	public int getRegHour()
	{
		return regDate.getHour();
	}
	
	public int getDeadlineYear()
	{
		return deadline.getYear();
	}
	
	public int getDeadlineMonth()
	{
		return deadline.getMonthValue();
	}
	
	public int getDeadlineDay()
	{
		return deadline.getDayOfMonth();
	}
	
	public int getDeadlineHour()
	{
		return deadline.getHour();
	}
	
	public int getLeftHours()
	{
		Duration duration = Duration.between(regDate, deadline);
		return (int)duration.toHours();
	}
	
	public int getLeftDays()
	{
		Duration duration = Duration.between(regDate, deadline);
		return (int)duration.toDays();
	}
	
	public boolean isDeadline()
	{
		if(getLeftHours()>0)
			return false;
		return true;
	}
	
	public void setDeadline(LocalDateTime newDeadline)
	{
		deadline = newDeadline;
	}
	
	public String getLeftTime()
	{	
		if(getLeftDays()<=0)
			return (getLeftDays()+"天,"+getLeftHours()%24+"小时");
		else
			return (getLeftDays()+"天");
	}

}

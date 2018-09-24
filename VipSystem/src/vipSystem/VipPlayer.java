package vipSystem;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VipPlayer 
{
	String playerName="";
	String regDate="";
	String deadline="";
	String vipGroup="";

	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd-HH");
	public VipPlayer(String playerName, String regDate, String deadline, String vipGroup)
	{
		this.playerName=playerName;
		this.regDate=regDate;
		this.deadline=deadline;
		this.vipGroup=vipGroup;
	}
	
	public String getName()
	{
		return playerName;
	}
	
	public String getDeadline()
	{
		return deadline;
	}
	
	public String getRegDate()
	{
		return regDate;
	}
	
	public String getVipGroup()
	{
		return vipGroup;
	}
	
	public int getRegYear()
	{
		int year = Integer.valueOf(regDate.split("-")[0]);
		return year;
	}
	
	public int getRegMonth()
	{
		int year = Integer.valueOf(regDate.split("-")[1]);
		return year;
	}
	
	public int getRegDay()
	{
		int year = Integer.valueOf(regDate.split("-")[2]);
		return year;
	}
	
	public int getRegHour()
	{
		int year = Integer.valueOf(regDate.split("-")[3]);
		return year;
	}
	
	public int getDeadlineYear()
	{
		int year = Integer.valueOf(deadline.split("-")[0]);
		return year;
	}
	
	public int getDeadlineMonth()
	{
		int year = Integer.valueOf(deadline.split("-")[1]);
		return year;
	}
	
	public int getDeadlineDay()
	{
		int year = Integer.valueOf(deadline.split("-")[2]);
		return year;
	}
	
	public int getDeadlineHour()
	{
		int year = Integer.valueOf(deadline.split("-")[3]);
		return year;
	}
	
	private static int _getLeftHours(String recentDate, String deadlineDate)
	{
		int[] monthList = {31,28,31,30,31,30,31,31,30,31,30,31};
		//============================================================
		int recentYear = Integer.valueOf(recentDate.split("-")[0]);
		int recentMonth = Integer.valueOf(recentDate.split("-")[1]);
		int recentDay = Integer.valueOf(recentDate.split("-")[2]);
		int recentHour = Integer.valueOf(recentDate.split("-")[3]);
		//============================================================
		int deadlineYear = Integer.valueOf(deadlineDate.split("-")[0]);
		int deadlineMonth = Integer.valueOf(deadlineDate.split("-")[1]);
		int deadlineDay = Integer.valueOf(deadlineDate.split("-")[2]);
		int deadlineHour = Integer.valueOf(deadlineDate.split("-")[3]);
		//============================================================
		int yearToHour = (deadlineYear - recentYear)*365*24;
		int monthToHour = 0;
		if((deadlineMonth - recentMonth)>0)
		{
			for(int i=recentMonth; i<deadlineMonth; i++)
			{
				monthToHour += monthList[i-1]*24;
			}
		}
		else
		{
			for(int i=deadlineMonth; i<recentMonth; i++)
			{
				monthToHour -= monthList[i-1]*24;
			}
		}
		
		
		int dayToHour = (deadlineDay - recentDay)*24;
		int hour = deadlineHour - recentHour;
		int totalHour = yearToHour+monthToHour+dayToHour+hour;
		if(totalHour<=0)
			return 0;
		return yearToHour+monthToHour+dayToHour+hour;
	}
	
	public int getLeftHours()
	{
		return _getLeftHours(date.format(new Date()), getDeadline());
	}
	
	public int getLeftDays()
	{
		int hours = _getLeftHours(date.format(new Date()), getDeadline());
		int totalDay = hours/24;
		if(totalDay<=0)
			return 0;
		return totalDay;
	}
	
	public boolean isDeadline()
	{
		if(getLeftHours()!=0)
			return false;
		return true;
	}
	
	public void setDeadline(String newDeadline)
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

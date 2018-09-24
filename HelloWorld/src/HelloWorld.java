import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class HelloWorld 
{
	static SimpleDateFormat date = new SimpleDateFormat("HH:mm");
	static char[] colorSignal = {'a','b','c','d','e','f','1','2','3','4','5','6','7','8','9'};
	static Random rand = new Random();

	public static void main(String[] args) 
	{
		/*
		int a = 3;
		double b = 2.0;
		int[] aa = {10,2,3,40,5,6,7,8,19,14,11,11};
		System.out.println(getNewDate(date.format(new Date()), 365));
		String m = "";
		String blockInfo = "1:1";
		for(String i:blockInfo.split(","))
		{
			int blockID = Integer.valueOf(i.split(":")[0]);
			int blockPoint = Integer.valueOf(i.split(":")[1]);
			System.out.println(""+blockID+","+blockPoint);
		}
		String aaa = "aa+11";
		String bbb = aaa.split("\\+")[1];
		for(int i=0; i<10; i++)
		{
			System.out.println(bbb);
			i=10;
		}
		
		System.out.println(isExist('a',colorSignal));
		random(1);
		for(int i:sort(aa))
			System.out.println(i);
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("abc", 11);
		map.put("asc", 11);
		map.put("avc", 12);
		map.put("rbc", 13);
		System.out.println(map.get("abc").hashCode());
		*/
		/*
		HashMap<String, Double> map = new HashMap<String, Double>(); 
		map.put("c", 1.0);
	    map.put("a", 1D);
	    map.put("d", 44444D);
	    map.put("e", 55555D);
	    map.put("b", 22222D);
	    sort(map);
	    map.put("e", 66666D);
	    map.put("b", 22222D);
	    sort(map);
	    */
		String hour = String.format("%02d", 11);
		System.out.println(hour);
		System.out.println(date.format(new Date()));
		String customName = "aaaaaabbb";
		customName = customName.replaceAll("&", "§");
		System.out.println(customName);
	}
	
 
	
	public static void sort(HashMap<String, Double> map)
	{

	      
	    //将map.entrySet()转换成list  
	    List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(map.entrySet());  
	    Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {  
	        //降序排序  
	        @Override  
	        public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {  
	            //return o1.getValue().compareTo(o2.getValue());  
	            return o2.getValue().compareTo(o1.getValue());  
	        }  
	    });  
	    
	    for (Map.Entry<String, Double> mapping : list) {  
	    	String content = String.format("%s - %.2f", mapping.getKey(), mapping.getValue());
	        System.out.println(content);  
	    }  
	}
	
	public static boolean isExist(char a, char[] list)
	{
		for(char everyChar:list)
		{
			if(everyChar==a)
				return true;
		}
		return false;
	}
	
	public static int random(int range)
	{
		int i = rand.nextInt(range); //生成随机数
		return i;
	}
	
	public static int getLeftHours(String recentDate, String deadlineDate)
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
	
	public static String getNewDate(String recentDate, int days)
	{
		int[] monthList = {31,28,31,30,31,30,31,31,30,31,30,31};
		int recentYear = Integer.valueOf(recentDate.split("-")[0]);
		int recentMonth = Integer.valueOf(recentDate.split("-")[1]);
		int recentDay = Integer.valueOf(recentDate.split("-")[2]);
		int recentHour = Integer.valueOf(recentDate.split("-")[3]);
		
		if(days>(monthList[recentMonth-1]-recentDay))
		{
			days -= (monthList[recentMonth-1]-recentDay);
			recentDay=1;
			if(recentMonth<12)
				recentMonth++;
			else
			{
				recentMonth=1;
				recentYear++;
			}
			
			while(days>0)
			{
				if(days/monthList[recentMonth-1]!=0 &&
						days-monthList[recentMonth-1]!=0)
				{
					days -= monthList[recentMonth-1];
					if(recentMonth<12)
						recentMonth++;
					else
					{
						recentMonth=1;
						recentYear++;
					}
				}
				else
				{
					recentDay=days;
					break;
				}
			}
		}
		else
		{
			recentDay += days;
			days=0;
		}
		
		String newDate = ""+recentYear+"-"+recentMonth+"-"+recentDay+"-"+recentHour;
		return newDate;
	}
	
	public static boolean isExist(int number, int[] numberList)
	{
		int left = 0;
		int right = numberList.length-1;
		while(left<=right)
		{
			int half = (right+left)/2;
			if(numberList[half]>number)
			{
				right=half-1;
			}
			else if(numberList[half]<number)
			{
				left=half+1;
			}
			else if(numberList[half]==number)
			{
				return true;
			}
			
		};

		return false;
	}
	
	public static int[] sort(int[] numbers)
	{
		for(int i=(numbers.length-1);i>=0;i--)
		{
			int largest = numbers[i];
			for(int x=(numbers.length-1); x>=0; x--)
			{
				if(largest<numbers[x])
				{
					largest = numbers[x];
					numbers[x] = numbers[i];
					numbers[i] = largest;
				}
			}
		}
		return numbers;
	}
	
	private static Object[] sort1(HashMap<String, Integer> map)
	{
		Object[] key =  map.values().toArray();     
        Arrays.sort(key);
        return key;
	}
	
	/*
	public static List<Map.Entry<String, Integer>> sort(HashMap<String, Integer> map)
	{
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());

		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() 
		{   
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) 
			{      
		        //return (o2.getValue() - o1.getValue()); 
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});
		
		return infoIds;
	}
	*/
}

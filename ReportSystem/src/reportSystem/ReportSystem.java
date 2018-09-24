package reportSystem;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ReportSystem extends JavaPlugin
{
	HashMap<String, HashMap<String, String>> reportList = new HashMap<String, HashMap<String, String>>();
	
	HashMap<String, ArrayList<String>> advice = new HashMap<String, ArrayList<String>>();
	
	ArrayList<String> op = new ArrayList<String>();
	
	SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
	
	public void onEnable() 
	{
		if(!new File(getDataFolder(),"Report").exists()) 
		{
			new File(getDataFolder(),"Report").mkdirs();
		}
		if(!new File(getDataFolder(),"Advice").exists()) 
		{
			new File(getDataFolder(),"Advice").mkdirs();
		}
		getOP();
		//loadConfig();
		Bukkit.getConsoleSender().sendMessage("��a[ReportSystem] ��e�ٱ�ϵͳ�������");
        //getLogger().info("�ٱ�ϵͳ�������");
	}

	public void onDisable() 
	{
		saveReport();
		saveAdvice();
		Bukkit.getConsoleSender().sendMessage("��a[ReportSystem] ��e�ٱ�ϵͳж�����");
		//getLogger().info("�ٱ�ϵͳж�����");
	}
	
	public void getOP()
	{
		op.clear();
		for(OfflinePlayer p:Bukkit.getServer().getOperators())
		{
			op.add(p.getName());
		}
	}
	
	public void saveReportInfo(String reporter, String beReported, String reason)
	{
		HashMap<String, String> peopleList = new HashMap<String, String>();
		if(reportList.containsKey(reporter))
		{
			peopleList = reportList.get(reporter);
			peopleList.put(beReported, reason);
			reportList.put(reporter, peopleList);
			return;
		}
		peopleList.put(beReported, reason);
		reportList.put(reporter, peopleList);
	}
	
	public void saveReport()
	{
		if(reportList.isEmpty())
		{
			return;
		}
		File file=new File(getDataFolder(),"/Report/" + date.format(new Date()) + ".yml");
		FileConfiguration config;
		config = load(file);
		for(String r:reportList.keySet())
		{
			int number = 0;
			for(String i:reportList.get(r).keySet())
			{
				config.set("�ٱ���." + r + "." + (number+1) + ".���ٱ���", i);
				config.set("�ٱ���." + r + "." + (number+1) + ".����", reportList.get(r).get(i));
				number += 1;
			}
		}

		try 
		{
			config.save(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		return;
	}
	
	public void saveAdvice()
	{
		if(reportList.isEmpty())
		{
			return;
		}
		File file=new File(getDataFolder(),"/Advice/" + date.format(new Date()) + ".yml");
		FileConfiguration config;
		config = load(file);
		for(String r:advice.keySet())
		{
			int number = 0;
			for(String i:advice.get(r))
			{
				config.set("������." + r + "." + (number+1), i);
				number += 1;
			}
		}

		try 
		{
			config.save(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		return;
	}
	
	/*
	public void loadConfig()
	{
		File file=new File(getDataFolder(),"config.yml");
		FileConfiguration config;
		if (!file.exists())
		{
			config = load(file);
			
			config.set("Report.OnUse", true);
			
			try 
			{
				config.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			loadConfig();
			return;
		}

	}
	*/
	
	public FileConfiguration load(File file)
	{
        if (!(file.exists())) 
        { //�����ļ�������
        	try   //��׽�쳣����Ϊ�п��ܴ������ɹ�
        	{
        		file.createNewFile();
        	}
        	catch(IOException e)
        	{
        		e.printStackTrace();
        	}
        }
        return YamlConfiguration.loadConfiguration(file);
	}
	public FileConfiguration load(String path)
	{
		File file=new File(path);
		if(!file.exists())
		{
			try
		{
				file.createNewFile();
		}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return YamlConfiguration.loadConfiguration(new File(path));
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("jy"))
		{
			if (args.length==0)
			{
				sender.sendMessage("��a=========[����ϵͳ]=========");
				sender.sendMessage("��a/jy [��������] ��3���������Ὠ��(ÿ��ÿ�����3��)");
				if(sender.isOp())
				{
					sender.sendMessage("��a/jy check ��3�鿴��ǰ���оٱ�");
					sender.sendMessage("��a/jy clear ��3�������оٱ�");
				}
				return true;
			}
			
			if(args.length==1 && args[0].equalsIgnoreCase("check"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[����ϵͳ] ��c��û��Ȩ��");
					return true;
				}
					
				if(advice.isEmpty())
				{
					sender.sendMessage("��6[����ϵͳ] ��c��ǰû�н���");
					return true;
				}
				
				for(String r:advice.keySet())
				{
					sender.sendMessage("��e-----------------"+r+"�Ľ���-----------------");
					for(String i:advice.get(r))
					{
						sender.sendMessage("��a����: "+i);
					}
					sender.sendMessage("��e-------------------------------------------");
				}
				return true;
			}
			
			if(args.length==1 && args[0].equalsIgnoreCase("clear"))
			{
				if(sender.isOp())
				{
					advice.clear();
					sender.sendMessage("��6[����ϵͳ] ��c�����ȫ������");
				}
				else
					sender.sendMessage("��6[����ϵͳ] ��c��û��Ȩ��");
				return true;
			}
			
			if(args.length==1 && (!args[0].equalsIgnoreCase("clear")) && (!args[0].equalsIgnoreCase("check")))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(args[0].length()<4)
					{
						p.sendMessage("��6[����ϵͳ] ��c����д����5�������ϵ���Ч���飡");
						return true;
					}
					if(args[0].matches("[0-9]*"))
					{
						p.sendMessage("��6[����ϵͳ] ��c��Ҫ��д������Ľ��飡");
						return true;
					}
					ArrayList<String> list;
					if(advice.containsKey(p.getName()))
					{
						if(advice.get(p.getName()).size()>=3)
						{
							p.sendMessage("��6[����ϵͳ] ��c���Ѿ�����3�������ˣ��������Ļ��н����뱣�������죬������ǵ��ҵģ��㽫�ᱻ������");
							return true;
						}
						list = advice.get(p.getName());
					}
					else
						list = new ArrayList<String>();
					list.add(args[0]);
					advice.put(p.getName(), list);
					for(String op:op)
					{
						if(Bukkit.getServer().getPlayer(op)!=null)
						{
							Bukkit.getServer().getPlayer(op).sendMessage("��6��ҡ�c"+p.getName()+"��6������飺��e"+args[0]);
						}
					}
					p.sendMessage("��6[����ϵͳ] ��a���Ľ��������Ѿ���¼����л����֧��");
					return true;
				}

			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("jb"))
		{
			if (args.length==0)
			{
				sender.sendMessage("��6=========[�ٱ�ϵͳ]=========");
				sender.sendMessage("��6/jb [�����] [����] ��a�ٱ���Ҳ�����ٱ�����");
				if(sender.isOp())
				{
					sender.sendMessage("��6/jb check ��a�鿴��ǰ���оٱ�");
					sender.sendMessage("��6/jb clear ��a�������оٱ�");
				}
				return true;
			}
			
			if(args.length==2)
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(Bukkit.getServer().getPlayer(args[0])==null)
					{
						p.sendMessage("��6[�ٱ�ϵͳ] ��c����Ҳ����߻򲻴���");
						return true;
					}
					
					if(args[1].length()<3)
					{
						p.sendMessage("��6[�ٱ�ϵͳ] ��c����������4���ֵ�����");
						return true;
					}
					
					if(args[1].matches("[0-9]*"))
					{
						p.sendMessage("��6[�ٱ�ϵͳ] ��c�벻Ҫ��������������ɣ�");
						return true;
					}
					
					for(String op:op)
					{
						if(Bukkit.getServer().getPlayer(op)!=null)
						{
							Bukkit.getServer().getPlayer(op).sendMessage("��6��ҡ�3"+p.getName()+"��6�ٱ���c"+args[0]+"��6,���ɣ���e"+args[1]);
						}
					}
					saveReportInfo(p.getName(), args[0], args[1]);
					sender.sendMessage("��6[�ٱ�ϵͳ] ��e�þٱ����ύ���ѷ��������ߵĹ������ǽ����������м��");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("check"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("��6[�ٱ�ϵͳ] ��c��û��Ȩ��");
					return true;
				}
					
				if(reportList.isEmpty())
				{
					sender.sendMessage("��6[�ٱ�ϵͳ] ��c��ǰû�оٱ���Ϣ");
					return true;
				}
				String beReported = "";
				sender.sendMessage("��e-----------------�ٱ���Ϣ-----------------");
				for(String r:reportList.keySet())
				{
					for(String i:reportList.get(r).keySet())
					{
						beReported += " ��f[��c" + i +"��f, ��e"+ reportList.get(r).get(i) + "��f]";
					}
					sender.sendMessage("��a�ٱ��ߡ�f: "+r+", ��4���ٱ��ߡ�f:"+beReported);
				}
				sender.sendMessage("��e-------------------------------------------");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("clear"))
			{
				if(sender.isOp())
				{
					reportList.clear();
					sender.sendMessage("��6[�ٱ�ϵͳ] ��c�����ȫ���ٱ���Ϣ");
				}	
				else
					sender.sendMessage("��6[�ٱ�ϵͳ] ��c��û��Ȩ��");
				return true;
			}
			return true;
		}

		return false;
		
	}
	
	
}


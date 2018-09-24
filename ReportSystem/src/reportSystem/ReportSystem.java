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
		Bukkit.getConsoleSender().sendMessage("§a[ReportSystem] §e举报系统加载完成");
        //getLogger().info("举报系统加载完成");
	}

	public void onDisable() 
	{
		saveReport();
		saveAdvice();
		Bukkit.getConsoleSender().sendMessage("§a[ReportSystem] §e举报系统卸载完成");
		//getLogger().info("举报系统卸载完成");
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
				config.set("举报者." + r + "." + (number+1) + ".被举报者", i);
				config.set("举报者." + r + "." + (number+1) + ".理由", reportList.get(r).get(i));
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
				config.set("建议者." + r + "." + (number+1), i);
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
        { //假如文件不存在
        	try   //捕捉异常，因为有可能创建不成功
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
				sender.sendMessage("§a=========[提议系统]=========");
				sender.sendMessage("§a/jy [建议内容] §3给服务器提建议(每人每天最多3条)");
				if(sender.isOp())
				{
					sender.sendMessage("§a/jy check §3查看当前所有举报");
					sender.sendMessage("§a/jy clear §3清理所有举报");
				}
				return true;
			}
			
			if(args.length==1 && args[0].equalsIgnoreCase("check"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("§6[提议系统] §c你没有权限");
					return true;
				}
					
				if(advice.isEmpty())
				{
					sender.sendMessage("§6[提议系统] §c当前没有建议");
					return true;
				}
				
				for(String r:advice.keySet())
				{
					sender.sendMessage("§e-----------------"+r+"的建议-----------------");
					for(String i:advice.get(r))
					{
						sender.sendMessage("§a建议: "+i);
					}
					sender.sendMessage("§e-------------------------------------------");
				}
				return true;
			}
			
			if(args.length==1 && args[0].equalsIgnoreCase("clear"))
			{
				if(sender.isOp())
				{
					advice.clear();
					sender.sendMessage("§6[提议系统] §c已清空全部建议");
				}
				else
					sender.sendMessage("§6[提议系统] §c你没有权限");
				return true;
			}
			
			if(args.length==1 && (!args[0].equalsIgnoreCase("clear")) && (!args[0].equalsIgnoreCase("check")))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(args[0].length()<4)
					{
						p.sendMessage("§6[提议系统] §c请填写至少5个字以上的有效建议！");
						return true;
					}
					if(args[0].matches("[0-9]*"))
					{
						p.sendMessage("§6[提议系统] §c不要填写无意义的建议！");
						return true;
					}
					ArrayList<String> list;
					if(advice.containsKey(p.getName()))
					{
						if(advice.get(p.getName()).size()>=3)
						{
							p.sendMessage("§6[提议系统] §c你已经提了3个建议了，如果你真的还有建议请保留到明天，如果你是捣乱的，你将会被处罚！");
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
							Bukkit.getServer().getPlayer(op).sendMessage("§6玩家§c"+p.getName()+"§6提出建议：§e"+args[0]);
						}
					}
					p.sendMessage("§6[提议系统] §a您的建议我们已经收录，感谢您的支持");
					return true;
				}

			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("jb"))
		{
			if (args.length==0)
			{
				sender.sendMessage("§6=========[举报系统]=========");
				sender.sendMessage("§6/jb [玩家名] [理由] §a举报玩家并给予举报理由");
				if(sender.isOp())
				{
					sender.sendMessage("§6/jb check §a查看当前所有举报");
					sender.sendMessage("§6/jb clear §a清理所有举报");
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
						p.sendMessage("§6[举报系统] §c该玩家不在线或不存在");
						return true;
					}
					
					if(args[1].length()<3)
					{
						p.sendMessage("§6[举报系统] §c请输入至少4个字的理由");
						return true;
					}
					
					if(args[1].matches("[0-9]*"))
					{
						p.sendMessage("§6[举报系统] §c请不要输入无意义的理由！");
						return true;
					}
					
					for(String op:op)
					{
						if(Bukkit.getServer().getPlayer(op)!=null)
						{
							Bukkit.getServer().getPlayer(op).sendMessage("§6玩家§3"+p.getName()+"§6举报§c"+args[0]+"§6,理由：§e"+args[1]);
						}
					}
					saveReportInfo(p.getName(), args[0], args[1]);
					sender.sendMessage("§6[举报系统] §e该举报已提交并已反馈给在线的管理，我们将尽快对其进行检测");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("check"))
			{
				if(!sender.isOp())
				{
					sender.sendMessage("§6[举报系统] §c你没有权限");
					return true;
				}
					
				if(reportList.isEmpty())
				{
					sender.sendMessage("§6[举报系统] §c当前没有举报信息");
					return true;
				}
				String beReported = "";
				sender.sendMessage("§e-----------------举报信息-----------------");
				for(String r:reportList.keySet())
				{
					for(String i:reportList.get(r).keySet())
					{
						beReported += " §f[§c" + i +"§f, §e"+ reportList.get(r).get(i) + "§f]";
					}
					sender.sendMessage("§a举报者§f: "+r+", §4被举报者§f:"+beReported);
				}
				sender.sendMessage("§e-------------------------------------------");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("clear"))
			{
				if(sender.isOp())
				{
					reportList.clear();
					sender.sendMessage("§6[举报系统] §c已清空全部举报信息");
				}	
				else
					sender.sendMessage("§6[举报系统] §c你没有权限");
				return true;
			}
			return true;
		}

		return false;
		
	}
	
	
}


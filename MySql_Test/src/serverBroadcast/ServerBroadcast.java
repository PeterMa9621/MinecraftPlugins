package serverBroadcast;

import org.bukkit.plugin.java.JavaPlugin;



import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ServerBroadcast extends JavaPlugin
{
	private String[] m = null;
	private String pre = "";
	private int t = 0;
	private int length = 0;
	private int cd = 0;
	public String mysql_url=null;
	public String mysql_user=null;
	public String mysql_pass=null;
	public boolean flatfile=false;
	
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		loadMessage();
		broadCast();
        getLogger().info("Finish loading");
	}

	public void onDisable() 
	{
		getLogger().info("Unload");
	}

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
	
	public void loadMessage()
	{
		File file=new File(getDataFolder(),"message.yml");
		FileConfiguration message;
		if (!file.exists())
		{
			message = load(file);
			message.set("Prefix", "&a[Server]");
			message.set("Message.1", "&cTest1");
			message.set("Message.2", "&dTest2");
			message.set("Length", 2);
			message.set("Time", 20);
			try 
			{
				message.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			pre = "[Server]";
			m[0] = "Test1";
			m[1] = "Test2";
			length = 2;
			t = 20;
			return;
		}
		message = load(file);
		pre = message.getString("Prefix");
		length = message.getInt("Length");
		t = message.getInt("Time");
		m = new String[length];
		for(int i=0; i<length; i++)
		{
			m[i] = message.getString("Message."+(i+1));
		}
		return;
		
	}
	
	public void connectMySql()
	{
		if (getConfig().getBoolean("MySQL.use"))
	    {
	      this.mysql_url = ("jdbc:mysql://" + getConfig().getString("MySQL.Host").trim() + ":" + getConfig().getInt("MySQL.Port") + "/" + getConfig().getString("MySQL.Database").trim());
	      this.mysql_user = getConfig().getString("MySQL.Username").trim();
	      this.mysql_pass = getConfig().getString("MySQL.Password").trim();
	      try
	      {
	        Connection con = DriverManager.getConnection(this.mysql_url, this.mysql_user, this.mysql_pass);
	        this.flatfile = false;
	        if (con == null)
	        {
	          getLogger().info("Connection to MySQL failed! Changing to flatfile.");
	          this.flatfile = true;
	        }
	        else
	        {
	          getLogger().info("Connected to MySQL server!");
	          Statement st = con.createStatement();
	          st.execute("CREATE TABLE IF NOT EXISTS `keys` (`key` VARCHAR(11) PRIMARY KEY, `grupo` VARCHAR(15), `dias` INT);");
	          st.execute("CREATE TABLE IF NOT EXISTS `vips` (`nome` VARCHAR(30) PRIMARY KEY, `inicio` VARCHAR(11), `usando` VARCHAR(15));");
	          for (String gname : getConfig().getStringList("vip_groups")) {
	            try
	            {
	              PreparedStatement pst2 = con.prepareStatement("ALTER TABLE `vips` ADD COLUMN `" + gname.trim() + "` VARCHAR(15) NOT NULL DEFAULT 0;");
	              pst2.execute();
	              pst2.close();
	            }
	            catch (SQLException localSQLException1) {}
	          }
	          if (getConfig().getBoolean("logging.usekey")) {
	            st.execute("CREATE TABLE IF NOT EXISTS `vipzero_log` (`comando` VARCHAR(20), `nome` VARCHAR(30), `key` VARCHAR(11) PRIMARY KEY, `data` VARCHAR(11), `grupo` VARCHAR(15), `dias` INT);");
	          }
	          st.close();
	        }
	        con.close();
	      }
	      catch (SQLException e)
	      {
	        getLogger().warning("Connection to MySQL failed! Changing to flatfile.");
	        e.printStackTrace();
	        this.flatfile = true;
	      }
	    }
	    else
	    {
	      getLogger().info("Using flatfile system.");
	    }
	}
	
	public void broadCast()
	{
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			
			public void run()
			{
				if (m!=null)
				{
					String prefix=pre.replaceAll("&", "§");
					String message=m[cd].replaceAll("&", "§");
					Bukkit.broadcastMessage(prefix+" §f"+message);
					if (cd == m.length-1)
					{
						cd=0;
					}
					else
					{
						cd+=1;
					}
				}
			}
		} ,5*20,t*20);
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		File file=new File(getDataFolder(),"message.yml");
		FileConfiguration message = load(file);
		if (cmd.getName().equalsIgnoreCase("bc"))
		{
			if (args.length==0)
			{
				sender.sendMessage("§a=========[系统公告]=========");
				sender.sendMessage("§a/bc setprefix [前缀] 设置公告前缀");
				sender.sendMessage("§a/bc settime [时间(秒)] 设置公告时长");
				sender.sendMessage("§a/bc add [信息] 添加一个公告");
				sender.sendMessage("§a/bc remove [编号] 删除一个公告");
				sender.sendMessage("§a/bc list 列出所有公告");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("setprefix"))
			{
				if (args.length == 2)
				{
					message.set("Prefix", args[1]);
					pre = args[1].replaceAll("&", "§");
					sender.sendMessage("§a设置成功");
				}
				else
				{
					sender.sendMessage("§a/bc setprefix [前缀]");
				}
			}
			
			if (args[0].equalsIgnoreCase("settime"))
			{
				if (args.length == 2)
				{
					if (args[1].matches("[0-9]*"))
					{
						message.set("Time", Integer.parseInt(args[1]));
						t =Integer.parseInt(args[1]);
						sender.sendMessage("§a设置成功,需要重载插件");
					}
					else
					{
						sender.sendMessage("§1时间只能为数字");
					}
				}
				else
				{
					sender.sendMessage("§a/bc settime [时间(秒)]");
				}
			}
			
			if (args[0].equalsIgnoreCase("add"))
			{
				if (args.length == 2)
				{
					length+=1;
					message.set("Length", length);
					message.set("Message."+length, args[1]);
					m = new String[length];
					for(int i=0; i<length; i++)
					{
						m[i] = message.getString("Message."+(i+1));
					}
					sender.sendMessage("§a添加成功");
				}
				else
				{
					sender.sendMessage("§a/bc add [信息]");
				}
			}
			
			if (args[0].equalsIgnoreCase("remove"))
			{
				if (args.length == 2)
				{
					if (args[1].matches("[0-9]*"))
					{
						int number = Integer.parseInt(args[1]);
						if (number<=length || number>=0)
						{
							for(int i=number+1; i<=length; i++)
							{
								String currentMessage = message.getString("Message."+i);
								message.set("Message."+(i-1), currentMessage);
							}
							message.set("Message."+length, null);
							length-=1;
						}
					}
					message.set("Length", length);
					sender.sendMessage("§a删除成功");
				}
				else
				{
					sender.sendMessage("§a/bc remove [编号]");
				}
			}
			
			if (args[0].equalsIgnoreCase("list"))
			{
				sender.sendMessage("§5公告列表:");
				for(int i=0; i<length; i++)
				{
					String currentMessage = message.getString("Message."+(i+1));
					sender.sendMessage("§a编号§4"+(i+1)+"§a: §d"+currentMessage);
				}
			}
			
			try 
			{
				message.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			return true;
		}

		return false;
		
	}
	
	
}


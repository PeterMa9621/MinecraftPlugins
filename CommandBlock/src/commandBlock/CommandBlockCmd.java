package commandBlock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandBlockCmd implements CommandExecutor
{
	private CommandBlock plugin;
	private HashMap<String,String> commandList=new HashMap<String, String>();
	public CommandBlockCmd(CommandBlock plugin)
	{
		this.plugin = plugin;
		addMessage("help", "/cb help [参数] 查看帮助指令");
		addMessage("new", "/cb new [参数] 新建命令方块");
		addMessage("edit", "/cb edit [参数] 进入编辑模式");
		addMessage("add", "/cb add [权限参数] [命令1] [命令2] ... 查看帮助指令");
	}
	
	public void addMessage(String a, String b)
	{
		commandList.put(a, plugin.prefix+b);
	}
	
	public void help(CommandSender sender)
	{
		for(Iterator<Entry<String,String>> i=commandList.entrySet().iterator();i.hasNext();)
		{
			Entry<String, String> m=(Entry<String, String>) i.next();
			sender.sendMessage(m.getValue());
		}
	}
	
	public void help(CommandSender sender, String args)
	{
		boolean found = false;
		for(Iterator<Entry<String,String>> i=commandList.entrySet().iterator();i.hasNext();)
		{
			Entry<String, String> m=(Entry<String, String>) i.next();
			if (args.equalsIgnoreCase(args))
			{
				sender.sendMessage(m.getValue());
				found = true;
			}
		}
		if (!found)
		{
			sender.sendMessage("没有这个命令");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (args.length==0)
		{
			help(sender);
			return true;
		}
		else if (args[0].equalsIgnoreCase("help"))
		{
			help(sender);
			return true;
		}
		else if (args[0].equalsIgnoreCase("new"))
		{
			New(sender);
			return true;
		}
		else if (args[0].equalsIgnoreCase("edit"))
		{
			Edit(sender);
			return true;
		}
		else if (args[0].equalsIgnoreCase("add"))
		{
			if (args.length<=2)
			{
				help(sender, args[0]);
			}
			else
			{
				Add(sender);
			}
			return true;
		}
		return false;
	}
	
	public void New(CommandSender sender)
	{
		
	}
	
	public void Edit(CommandSender sender)
	{
		
	}
	
	public void Add(CommandSender sender)
	{
		
	}
}

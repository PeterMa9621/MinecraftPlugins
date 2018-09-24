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
		addMessage("help", "/cb help [����] �鿴����ָ��");
		addMessage("new", "/cb new [����] �½������");
		addMessage("edit", "/cb edit [����] ����༭ģʽ");
		addMessage("add", "/cb add [Ȩ�޲���] [����1] [����2] ... �鿴����ָ��");
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
			sender.sendMessage("û���������");
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

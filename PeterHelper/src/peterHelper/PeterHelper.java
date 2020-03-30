package peterHelper;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import peterHelper.expansion.PeterHelperExpansion;
import peterHelper.util.Util;

public class PeterHelper extends JavaPlugin
{
	public void onEnable()
	{

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new PeterHelperExpansion(this).register();
		}

		getServer().getPluginManager().registerEvents(new PeterHelperListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[PeterHelper] ��ePeterHelper�������");
	}

	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage("��a[PeterHelper] ��ePeterHelperж�����");
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("ph") && sender instanceof Player && sender.isOp()) {
			Player player = (Player) sender;
			if (args.length==0) {

				sender.sendMessage("��a=========[PeterHelper]=========");
				sender.sendMessage("��a/ph item ��3�鿴���ϵ���Ʒ��Ϣ");

				return true;
			}
			
			if(args[0].equalsIgnoreCase("item")) {
				Util.getItemInfoInMainHand(player);

				return true;
			}
		} else {
			sender.sendMessage("ֻ��Ӧ�����������");
		}
		return false;
	}
}


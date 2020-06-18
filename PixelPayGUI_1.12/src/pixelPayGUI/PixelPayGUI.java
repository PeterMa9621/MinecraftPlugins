package pixelPayGUI;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pixelPayGUI.config.ConfigManager;
import pixelPayGUI.listener.PixelPayGUIListener;
import pixelPayGUI.util.Util;

public class PixelPayGUI extends JavaPlugin
{
	private ConfigManager configManager;
	public void onEnable() 
	{
		configManager = new ConfigManager(this);
		configManager.loadKitInfo();
		getServer().getPluginManager().registerEvents(new PixelPayGUIListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[PixelPayGUI] §ePixelPayGUI加载完毕");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§a[PixelPayGUI] §ePixelPayGUI卸载完毕");
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("paygui")) {
			if (args.length==0) {

				if(sender.isOp()) {
					sender.sendMessage("§6=========[PixelPayGUI]=========");
					sender.sendMessage("§6/paygui [礼包名称]  §a打开支付界面");
					sender.sendMessage("§6/paygui reload  §a重载配置");
				}
				return true;
			}

			if (args.length == 1 && sender instanceof Player) {
				if(args[0].equalsIgnoreCase("reload")) {
					configManager.loadKitInfo();
					sender.sendMessage("§c6重载配置成功");
					return true;
				}
				Player player = (Player) sender;
				String kitName = args[0];
				if(configManager.kits.contains(kitName)) {
					Util.openGui(this, player, kitName);
				} else {
					sender.sendMessage("§c没有这个礼包");
				}
				return true;
			}
		}
		return false;
	}
}


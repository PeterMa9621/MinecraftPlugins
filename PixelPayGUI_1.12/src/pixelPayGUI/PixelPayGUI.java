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
		Bukkit.getConsoleSender().sendMessage("��a[PixelPayGUI] ��ePixelPayGUI�������");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("��a[PixelPayGUI] ��ePixelPayGUIж�����");
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("paygui")) {
			if (args.length==0) {

				if(sender.isOp()) {
					sender.sendMessage("��6=========[PixelPayGUI]=========");
					sender.sendMessage("��6/paygui [�������]  ��a��֧������");
					sender.sendMessage("��6/paygui reload  ��a��������");
				}
				return true;
			}

			if (args.length == 1 && sender instanceof Player) {
				if(args[0].equalsIgnoreCase("reload")) {
					configManager.loadKitInfo();
					sender.sendMessage("��c6�������óɹ�");
					return true;
				}
				Player player = (Player) sender;
				String kitName = args[0];
				if(configManager.kits.contains(kitName)) {
					Util.openGui(this, player, kitName);
				} else {
					sender.sendMessage("��cû��������");
				}
				return true;
			}
		}
		return false;
	}
}


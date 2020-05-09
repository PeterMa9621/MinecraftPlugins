package betterWeapon.listener;

import java.util.ArrayList;
import java.util.List;

import betterWeapon.BetterWeapon;
import betterWeapon.manager.GemGuiManager;
import betterWeapon.util.Util;
import com.mysql.fabric.xmlrpc.base.Array;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BetterWeaponGemListener implements Listener
{
	private BetterWeapon plugin;

	int[] slot = {0,1,2,3,4,5,6,7,8,9,11,12,14,15,17,18,19,20,21,22,23,24,25,26,27,28
			,29,30,32,33,34,35,36,37,38,39,41,42,43,44};
	int[] slotHole = {0,1,2,3,4,5,6,7,8,9,11,12,13,14,15,17,18,19,20,21,23,24,25,26};
	int[] slotInlay = {0,1,2,3,4,5,6,7,8,9,11,12,13,14,15,17,18,19,20,21,22,23,24,25,26,27,28
			,29,30,32,33,34,35,36,37,38,39,41,42,43,44};
	
	GemGuiManager gemGui;
	
	public BetterWeaponGemListener(BetterWeapon plugin)
	{
		this.plugin = plugin;
		gemGui = new GemGuiManager(plugin);
	}
	
	@EventHandler
	public void onPlayerClickGUI(InventoryClickEvent event)
	{
		if(event.getView().getTitle().equalsIgnoreCase("§5宝石系统"))
		{
			event.setCancelled(true);
			if(event.getRawSlot()==0)
			{
				event.getWhoClicked().openInventory(gemGui.initHoleGUI((Player)event.getWhoClicked()));
				return;
			}
			
			if(event.getRawSlot()==2)
			{
				event.getWhoClicked().openInventory(gemGui.initInlayGUI((Player)event.getWhoClicked()));
				return;
			}
			
			if(event.getRawSlot()==4)
			{
				event.getWhoClicked().openInventory(gemGui.initEvaluateGUI((Player)event.getWhoClicked()));
				return;
			}
			
			if(event.getRawSlot()==6)
			{
				event.getWhoClicked().openInventory(gemGui.initSynthesisGUI((Player)event.getWhoClicked()));
				return;
			}
			
			if(event.getRawSlot()==8)
			{
				event.getWhoClicked().openInventory(plugin.guiManager.initMainGUI((Player)event.getWhoClicked()));
			}
		}
	}

	@EventHandler
	public void onPlayerCloseGUI(InventoryCloseEvent event)
	{
		if(event.getView().getTitle().equalsIgnoreCase("§5装备镶嵌"))
		{
			if(event.getInventory().getItem(10)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(10));
			}
			if(event.getInventory().getItem(16)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(16));
			}
			if(event.getInventory().getItem(40)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(40));
			}
			return;
		}
		
		if(event.getView().getTitle().equalsIgnoreCase("§5宝石鉴定"))
		{
			if(event.getInventory().getItem(10)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(10));
			}
			if(event.getInventory().getItem(16)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(16));
			}
			return;
		}
		
		if(event.getView().getTitle().equalsIgnoreCase("§5宝石合成"))
		{
			if(event.getInventory().getItem(19)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(10));
			}
			if(event.getInventory().getItem(25)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(16));
			}
			if(event.getInventory().getItem(40)!=null)
			{
				event.getPlayer().getInventory().addItem(event.getInventory().getItem(40));
			}
		}
	}
}

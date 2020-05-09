package betterWeapon.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import betterWeapon.BetterWeapon;
import betterWeapon.manager.GemGuiManager;
import betterWeapon.util.SmeltType;
import betterWeapon.util.Util;
import com.mysql.fabric.xmlrpc.base.Array;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BetterWeaponListener implements Listener
{
	private BetterWeapon plugin;
	GemGuiManager gemGui;
	
	public BetterWeaponListener(BetterWeapon plugin) {
		this.plugin=plugin;
		gemGui= new GemGuiManager(plugin);
	}

	/*
	public void _intensifyForOwner(InventoryClickEvent event, ItemStack equip, int level)
	{
		ItemMeta meta = equip.getItemMeta();
		List<String> loreList = meta.getLore();
		String lore = "§a强化等级:"+(level+1)+"%%§a锻造者为:§3"+event.getWhoClicked().getName();
		if(loreList.size()==4)
		{
			String owner = loreList.get(3);
		}
		loreList.clear();
		for(String l:lore.split("%"))
		{
			loreList.add(l);
		}
		if(loreList.size()==4)
		{
			loreList.add("owner");
		}
		//String lore = "§a强化等级:"+(level+1)+"%%§a锻造者为:§3"+event.getWhoClicked().getName();
		//ArrayList<String> loreList = new ArrayList<String>();
		
		meta.setLore(loreList);
		equip.setItemMeta(meta);
		equip.addUnsafeEnchantment(Enchantment.getById(plugin.rule.get(equip.getTypeId())), level+1);
	}
	*/
	
	@EventHandler
	public void onPlayerClickGUI(InventoryClickEvent event)
	{
		if(event.getView().getTitle().equalsIgnoreCase("§5强化系统"))
		{
			event.setCancelled(true);
			if(event.getRawSlot()==2) {
				event.getWhoClicked().openInventory(plugin.guiManager.initIntensifyGUI((Player)event.getWhoClicked()));
				return;
			}
			if(event.getRawSlot()==6) {
				event.getWhoClicked().openInventory(plugin.guiManager.initSmeltGUI((Player)event.getWhoClicked()));
				return;
			}
			if(event.getRawSlot()==4) {
				event.getWhoClicked().openInventory(gemGui.initMainGUI((Player)event.getWhoClicked()));
			}
		}
	}
}

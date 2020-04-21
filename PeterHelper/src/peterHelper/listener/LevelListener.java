package peterHelper.listener;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import peterHelper.PeterHelper;
import peterHelper.util.LevelUtil;
import peterHelper.util.Util;

import java.util.UUID;

public class LevelListener implements Listener
{
	private PeterHelper plugin;
	
	public LevelListener(PeterHelper plugin) {
		this.plugin=plugin;
	}

	@EventHandler
	public void onPlayerHeldItem(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		int slot = event.getNewSlot();
		ItemStack itemStack = player.getInventory().getItem(slot);
		if(!LevelUtil.canUseItem(itemStack, player, plugin)) {
			player.sendMessage("§c你等级不满足该物品的要求,无法使用!");
		}
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			ItemStack itemStack = player.getInventory().getItemInMainHand();
			if (!LevelUtil.canUseItem(itemStack, player, plugin)) {
				player.sendMessage("§c你等级不满足该物品的要求,无法使用!");
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerWearArmor(ArmorEquipEvent event) {
		ItemStack armor = event.getNewArmorPiece();
		Player player = event.getPlayer();
		if (!LevelUtil.canUseItem(armor, player, plugin)) {
			player.sendMessage("§c你等级不满足该物品的要求,无法使用!");
			event.setCancelled(true);
		}
	}
}

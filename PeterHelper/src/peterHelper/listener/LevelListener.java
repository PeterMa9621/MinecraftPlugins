package peterHelper.listener;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import org.bukkit.*;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import peterHelper.PeterHelper;
import peterHelper.util.ArmorUtil;
import peterHelper.util.LevelUtil;
import peterHelper.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class LevelListener implements Listener
{
	private PeterHelper plugin;

	private BukkitTask bukkitTask;
	private HashMap<UUID, ArrayList<BukkitTask>> particleTasks;
	
	public LevelListener(PeterHelper plugin) {
		this.plugin=plugin;
		particleTasks = new HashMap<>();
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
	public void onPlayerWearNotSatisfiedArmor(ArmorEquipEvent event) {
		ItemStack armor = event.getNewArmorPiece();
		Player player = event.getPlayer();
		if (!LevelUtil.canUseItem(armor, player, plugin)) {
			player.sendMessage("§c你等级不满足该物品的要求,无法使用!");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerWearArmor(ArmorEquipEvent e) {

		// Equip armor
		if(e.getNewArmorPiece() != null && e.getNewArmorPiece().getType() != Material.AIR) {
			Player player = e.getPlayer();
			if(ArmorUtil.isArmorSuite(plugin, player.getInventory().getArmorContents(), e.getNewArmorPiece())){
				player.sendMessage("Suite!");
			}
		}
		// Unequip armor
		if(e.getOldArmorPiece() != null && e.getOldArmorPiece().getType() != Material.AIR) {
			//bukkitTask.cancel();
		}
	}
}

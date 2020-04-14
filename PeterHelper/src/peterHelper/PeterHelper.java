package peterHelper;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import peterHelper.expansion.PeterHelperExpansion;
import peterHelper.util.AttributeModifierUtil;
import peterHelper.util.Util;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class PeterHelper extends JavaPlugin
{
	public void onEnable()
	{

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new PeterHelperExpansion(this).register();
		}

		getServer().getPluginManager().registerEvents(new PeterHelperListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[PeterHelper] §ePeterHelper加载完毕");
	}

	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage("§a[PeterHelper] §ePeterHelper卸载完毕");
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("ph") && sender.isOp()) {
			if (args.length==0) {
				sender.sendMessage("§a=========[PeterHelper]=========");
				sender.sendMessage("§a/ph item §3查看手上的物品信息");
				sender.sendMessage("§a/ph give [playerName] [itemName] [amount] §3给予原版物品");
				sender.sendMessage("§a/ph giveitem [playerName] [itemName] §3给予ItemAdders内的自定义物品");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("item") && sender instanceof Player) {
				Player player = (Player) sender;
				Util.getItemInfoInMainHand(player);

				return true;
			}

			if(args[0].equalsIgnoreCase("give")){
				if(args.length<3){
					sender.sendMessage("§a/ph give [playerName] [itemName] [amount] §3给予原版物品");
					return true;
				}
				String playerName = args[1];
				String itemName = args[2];
				int amount = Integer.parseInt(args[3]);
				Player player = Bukkit.getPlayer(playerName);
				if(player==null){
					sender.sendMessage("§a[PeterHelper] §c玩家不存在");
					return true;
				}
				Material material = Material.getMaterial(itemName.toUpperCase());
				if (material == null) {
					sender.sendMessage("§a[PeterHelper] §c物品不存在");
					return true;
				}
				ItemStack itemStack = new ItemStack(material, amount);

				if(player.getInventory().firstEmpty()!=-1) {
					player.getInventory().addItem(itemStack);
				}
				else {
					Bukkit.getServer().getWorld(player.getWorld().getName()).dropItem(player.getLocation(), itemStack);
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("giveitem")){
				if(args.length<3){
					sender.sendMessage("§a/ph giveitem [playerName] [itemName] §3给予ItemAdders内的自定义物品");
					return true;
				}

				String playerName = args[1];
				String itemName = args[2];
				Player player = Bukkit.getPlayer(playerName);
				if(player==null){
					sender.sendMessage("§a[PeterHelper] §c玩家不存在");
					return true;
				}
				ItemStack itemStack = ItemsAdder.getCustomItem(itemName);
				if(itemStack==null){
					sender.sendMessage("§a[PeterHelper] §c自定义物品不存在");
					return true;
				}
				ItemMeta itemMeta = itemStack.getItemMeta();
				if(Util.isWeapon(itemStack)) {
					AttributeModifierUtil.randomWeaponAttribute(itemMeta);
					itemStack.setItemMeta(itemMeta);
				} else if(Util.isArmor(itemStack)) {
					AttributeModifierUtil.randomArmorAttribute(itemMeta);
					itemStack.setItemMeta(itemMeta);
				}

				if(player.getInventory().firstEmpty()!=-1) {
					player.getInventory().addItem(itemStack);
				}
				else {
					Bukkit.getServer().getWorld(player.getWorld().getName()).dropItem(player.getLocation(), itemStack);
				}
				player.sendMessage(ChatColor.GOLD + "已获得" + ChatColor.WHITE + itemStack.getItemMeta().getDisplayName());
				return true;
			}
		}
		return false;
	}
}


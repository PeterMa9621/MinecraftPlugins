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
				sender.sendMessage("§a/ph getitem [itemName] §3获得自定义物品");
				sender.sendMessage("§a/ph giveitem [playerName] [itemName] §3给予ItemAdders内的自定义物品");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("item") && sender instanceof Player) {
				Player player = (Player) sender;
				Util.getItemInfoInMainHand(player);

				return true;
			}

			if(args[0].equalsIgnoreCase("getitem") && sender instanceof Player){
				Player player = (Player) sender;
				ItemStack itemStack = ItemsAdder.getCustomItem(args[1]);
				player.getInventory().addItem(itemStack);
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
					Collection<AttributeModifier> attributeModifiers = itemMeta.getAttributeModifiers(Attribute.GENERIC_ATTACK_SPEED);
					AtomicReference<Double> attackSpeed = new AtomicReference<>((double) 0);
					attributeModifiers.forEach(attributeModifier -> {
						attackSpeed.set(attributeModifier.getAmount());
					});

					attributeModifiers = itemMeta.getAttributeModifiers(Attribute.GENERIC_ATTACK_DAMAGE);
					AtomicReference<Double> damage = new AtomicReference<>((double) 0);
					attributeModifiers.forEach(attributeModifier -> {
						damage.set(attributeModifier.getAmount());
					});

					double random = new Random(Calendar.getInstance().getTimeInMillis()).nextDouble() * 2;
					BigDecimal bigDecimal = new BigDecimal(random);
					random = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
					double modifiedDamage = damage.get() - random;
					AttributeModifier newAttribute = new AttributeModifier(UUID.randomUUID(), "itemsadder", modifiedDamage, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
					itemMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
					itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, newAttribute);
					List<String> lore = itemMeta.getLore();
					int index = lore.size();
					lore.add(index - 1, ChatColor.DARK_GREEN + String.valueOf(modifiedDamage + 1) + " 攻击伤害");
					lore.add(index - 1, ChatColor.DARK_GREEN + String.valueOf(4 + attackSpeed.get()) + " 攻击速度");
					itemMeta.setLore(lore);
					itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					itemStack.setItemMeta(itemMeta);
				} else if(Util.isArmor(itemStack)) {
					Collection<AttributeModifier> attributeModifiers = itemMeta.getAttributeModifiers(Attribute.GENERIC_ARMOR);
					AtomicReference<Double> armor = new AtomicReference<>((double) 0);
					AtomicReference<EquipmentSlot> slot = new AtomicReference<>();
					attributeModifiers.forEach(attributeModifier -> {
						armor.set(attributeModifier.getAmount());
						slot.set(attributeModifier.getSlot());
					});
					double random = new Random(Calendar.getInstance().getTimeInMillis()).nextDouble() * 1;
					BigDecimal bigDecimal = new BigDecimal(random);
					random = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
					double modifiedArmor = armor.get() - random;
					AttributeModifier newAttribute = new AttributeModifier(UUID.randomUUID(), "itemsadder", modifiedArmor, AttributeModifier.Operation.ADD_NUMBER, slot.get());
					itemMeta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
					itemMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, newAttribute);
					itemStack.setItemMeta(itemMeta);
				}
				player.getInventory().addItem(itemStack);
				player.sendMessage(ChatColor.GOLD + "已获得" + ChatColor.WHITE + itemStack.getItemMeta().getDisplayName());
				return true;
			}

		} else {
			sender.sendMessage("只能应用在玩家身上");
		}
		return false;
	}
}


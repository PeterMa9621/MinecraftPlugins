package peterHelper.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import peterHelper.model.ArmorInfo;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class AttributeModifierUtil {
    private static double attackUpperBound = 2.0;
    private static double attackSpeedUpperBound = 0.2;
    private static double armorUpperBound = 1.0;

    public static void randomWeaponAttribute(ItemMeta itemMeta) {
        double attackSpeed = getOriginAttackSpeed(itemMeta);
        double modifiedAttackSpeed = getRandomAttackSpeed(attackSpeed);

        double damage = getOriginAttack(itemMeta);
        double modifiedDamage = getRandomAttack(damage);
        AttributeModifier newAttribute = new AttributeModifier(UUID.randomUUID(), "itemsadder", modifiedDamage, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        itemMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
        itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, newAttribute);

        newAttribute = new AttributeModifier(UUID.randomUUID(), "itemsadder", modifiedAttackSpeed, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        itemMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
        itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, newAttribute);

        List<String> lore = itemMeta.getLore();
        if(lore==null)
            lore = new ArrayList<>();
        int index = lore.size();

        lore.add(index - 1, ChatColor.DARK_GREEN + String.format("%.1f ¹¥»÷ÉËº¦", modifiedDamage + 1));
        lore.add(index - 1, ChatColor.DARK_GREEN + String.format("%.1f ¹¥»÷ËÙ¶È", 4 + modifiedAttackSpeed));

        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    public static void randomArmorAttribute(ItemMeta itemMeta) {
        ArmorInfo armorInfo = getOriginArmor(itemMeta);
        double armor = armorInfo.armor;
        EquipmentSlot slot = armorInfo.equipmentSlot;
        double modifiedArmor = getRandomArmor(armor);

        AttributeModifier newAttribute = new AttributeModifier(UUID.randomUUID(), "itemsadder", modifiedArmor, AttributeModifier.Operation.ADD_NUMBER, slot);
        itemMeta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
        itemMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, newAttribute);
    }

    private static double getRandomAttack(double originDamage) {
        double random = new Random(Calendar.getInstance().getTimeInMillis()).nextDouble() * attackUpperBound;
        double modifiedDamage = originDamage - random;
        BigDecimal bigDecimal = new BigDecimal(modifiedDamage);
        modifiedDamage = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return modifiedDamage;
    }

    private static double getRandomAttackSpeed(double originAttackSpeed) {
        Random random = new Random(Calendar.getInstance().getTimeInMillis());
        double randomNumber = random.nextDouble() * attackSpeedUpperBound;
        if(random.nextDouble() < 0.5)
            randomNumber = -randomNumber;
        double modifiedAttackSpeed = originAttackSpeed + randomNumber;
        BigDecimal bigDecimal = new BigDecimal(modifiedAttackSpeed);
        modifiedAttackSpeed = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

        return modifiedAttackSpeed;
    }

    private static double getRandomArmor(double originArmor) {
        double random = new Random(Calendar.getInstance().getTimeInMillis()).nextDouble() * armorUpperBound;
        BigDecimal bigDecimal = new BigDecimal(random);
        random = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return originArmor - random;
    }

    private static double getOriginAttack(ItemMeta itemMeta) {
        Collection<AttributeModifier> attributeModifiers = itemMeta.getAttributeModifiers(Attribute.GENERIC_ATTACK_DAMAGE);
        AtomicReference<Double> damage = new AtomicReference<>((double) 7);
        if(attributeModifiers!=null)
            attributeModifiers.forEach(attributeModifier -> {
                damage.set(attributeModifier.getAmount());
            });
        return damage.get();
    }

    private static double getOriginAttackSpeed(ItemMeta itemMeta) {
        Collection<AttributeModifier> attributeModifiers = itemMeta.getAttributeModifiers(Attribute.GENERIC_ATTACK_SPEED);
        AtomicReference<Double> attackSpeed = new AtomicReference<>((double) -2.4);
        if(attributeModifiers!=null)
            attributeModifiers.forEach(attributeModifier -> {
                attackSpeed.set(attributeModifier.getAmount());
            });
        return attackSpeed.get();
    }

    private static ArmorInfo getOriginArmor(ItemMeta itemMeta) {
        Collection<AttributeModifier> attributeModifiers = itemMeta.getAttributeModifiers(Attribute.GENERIC_ARMOR);
        AtomicReference<Double> armor = new AtomicReference<>((double) 0);
        AtomicReference<EquipmentSlot> slot = new AtomicReference<>();
        attributeModifiers.forEach(attributeModifier -> {
            armor.set(attributeModifier.getAmount());
            slot.set(attributeModifier.getSlot());
        });
        return new ArmorInfo(armor.get(), slot.get());
    }
}

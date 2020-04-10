package peterHelper.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import peterHelper.model.ArmorInfo;
import peterHelper.model.ArmorToughnessInfo;
import peterHelper.model.MaxHealthInfo;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class AttributeModifierUtil {
    private static double attackUpperBound = 2.0;
    private static double attackSpeedUpperBound = 0.2;
    private static double armorUpperBound = 1.0;
    private static double healthBonusUpperBound = 1.0;
    private static double armorToughnessUpperBound = 0.5;

    public static void randomWeaponAttribute(ItemMeta itemMeta) {
        double attackSpeed = getOriginAttackSpeed(itemMeta);
        double modifiedAttackSpeed = getRandomAttackSpeed(attackSpeed);

        double damage = getOriginAttack(itemMeta);
        double modifiedDamage = getRandomAttack(damage);

        MaxHealthInfo healthBonus = getOriginHealthBonus(itemMeta);
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

        if(healthBonus.healthBonus > 0d) {
            double modifiedHealthBonus = getRandomHealthBonus(healthBonus.healthBonus);
            newAttribute = new AttributeModifier(UUID.randomUUID(), "itemsadder", modifiedHealthBonus, AttributeModifier.Operation.ADD_NUMBER, healthBonus.equipmentSlot);
            itemMeta.removeAttributeModifier(Attribute.GENERIC_MAX_HEALTH);
            itemMeta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, newAttribute);
            lore.add(index - 1, ChatColor.DARK_GREEN + String.format("%.1f 额外生命加成", modifiedHealthBonus));
        }
        lore.add(index - 1, ChatColor.DARK_GREEN + String.format("%.1f 攻击伤害", modifiedDamage + 1));
        lore.add(index - 1, ChatColor.DARK_GREEN + String.format("%.1f 攻击速度", 4 + modifiedAttackSpeed));

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

        MaxHealthInfo maxHealthInfo = getOriginHealthBonus(itemMeta);
        double healthBonus = maxHealthInfo.healthBonus;
        if(healthBonus > 0d) {
            EquipmentSlot healthSlot = maxHealthInfo.equipmentSlot;
            double modifiedHealthBonus = getRandomHealthBonus(healthBonus);

            newAttribute = new AttributeModifier(UUID.randomUUID(), "itemsadder", modifiedHealthBonus, AttributeModifier.Operation.ADD_NUMBER, healthSlot);
            itemMeta.removeAttributeModifier(Attribute.GENERIC_MAX_HEALTH);
            itemMeta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, newAttribute);
        }

        ArmorToughnessInfo armorToughnessInfo = getOriginArmorToughness(itemMeta);
        double armorToughness = armorToughnessInfo.armorToughness;
        if(armorToughness > 0d) {
            EquipmentSlot armorToughnessSlot = armorToughnessInfo.equipmentSlot;
            double modifiedArmorToughness = getRandomArmorToughness(armorToughness);

            newAttribute = new AttributeModifier(UUID.randomUUID(), "itemsadder", modifiedArmorToughness, AttributeModifier.Operation.ADD_NUMBER, armorToughnessSlot);
            itemMeta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS);
            itemMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, newAttribute);
        }
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

    private static double getRandomHealthBonus(double originHealthBonus) {
        Random random = new Random(Calendar.getInstance().getTimeInMillis());
        double randomNumber = random.nextDouble() * healthBonusUpperBound;
        if(random.nextDouble() < 0.5)
            randomNumber = -randomNumber;
        BigDecimal bigDecimal = new BigDecimal(randomNumber);
        randomNumber = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return originHealthBonus - randomNumber;
    }

    private static double getRandomArmorToughness(double originArmorToughness) {
        Random random = new Random(Calendar.getInstance().getTimeInMillis());
        double randomNumber = random.nextDouble() * armorToughnessUpperBound;
        if(random.nextDouble() < 0.5)
            randomNumber = -randomNumber;
        BigDecimal bigDecimal = new BigDecimal(randomNumber);
        randomNumber = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return originArmorToughness - randomNumber;
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

    private static MaxHealthInfo getOriginHealthBonus(ItemMeta itemMeta) {
        Collection<AttributeModifier> attributeModifiers = itemMeta.getAttributeModifiers(Attribute.GENERIC_MAX_HEALTH);
        AtomicReference<Double> healthBonus = new AtomicReference<>(0d);
        AtomicReference<EquipmentSlot> slot = new AtomicReference<>();
        if(attributeModifiers!=null)
            attributeModifiers.forEach(attributeModifier -> {
                healthBonus.set(attributeModifier.getAmount());
                slot.set(attributeModifier.getSlot());
            });
        return new MaxHealthInfo(healthBonus.get(), slot.get());
    }

    private static ArmorToughnessInfo getOriginArmorToughness(ItemMeta itemMeta) {
        Collection<AttributeModifier> attributeModifiers = itemMeta.getAttributeModifiers(Attribute.GENERIC_ARMOR_TOUGHNESS);
        AtomicReference<Double> armorToughness = new AtomicReference<>(0d);
        AtomicReference<EquipmentSlot> slot = new AtomicReference<>();
        if(attributeModifiers!=null)
            attributeModifiers.forEach(attributeModifier -> {
                armorToughness.set(attributeModifier.getAmount());
                slot.set(attributeModifier.getSlot());
            });
        return new ArmorToughnessInfo(armorToughness.get(), slot.get());
    }
}

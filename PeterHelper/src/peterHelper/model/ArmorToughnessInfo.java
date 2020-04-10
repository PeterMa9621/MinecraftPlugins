package peterHelper.model;

import org.bukkit.inventory.EquipmentSlot;

public class ArmorToughnessInfo {
    public double armorToughness;
    public EquipmentSlot equipmentSlot;

    public ArmorToughnessInfo(double armorToughness, EquipmentSlot equipmentSlot) {
        this.armorToughness = armorToughness;
        this.equipmentSlot = equipmentSlot;
    }
}

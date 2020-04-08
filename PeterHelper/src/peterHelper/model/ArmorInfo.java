package peterHelper.model;

import org.bukkit.inventory.EquipmentSlot;

public class ArmorInfo {
    public double armor;
    public EquipmentSlot equipmentSlot;

    public ArmorInfo(double armor, EquipmentSlot equipmentSlot) {
        this.armor = armor;
        this.equipmentSlot = equipmentSlot;
    }
}

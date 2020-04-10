package peterHelper.model;

import org.bukkit.inventory.EquipmentSlot;

public class MaxHealthInfo {
    public double healthBonus;
    public EquipmentSlot equipmentSlot;

    public MaxHealthInfo(double healthBonus, EquipmentSlot equipmentSlot) {
        this.healthBonus = healthBonus;
        this.equipmentSlot = equipmentSlot;
    }
}

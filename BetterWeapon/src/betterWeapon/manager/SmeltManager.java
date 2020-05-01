package betterWeapon.manager;

import betterWeapon.BetterWeapon;
import betterWeapon.util.SmeltType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class SmeltManager {
    private BetterWeapon plugin;

    private ItemStack itemForSmelt = null;
    private int priceForSmelt = 0;
    private HashMap<Material, SmeltType> ruleSmelt = new HashMap<>();
    private ArrayList<Integer> possibilitySmeltList = new ArrayList<Integer>();

    public SmeltManager(BetterWeapon plugin) {
        this.plugin = plugin;
    }

    public Integer getPossibility(int level) {
        return possibilitySmeltList.get(level);
    }

    public ArrayList<Integer> getPossibilitySmeltList() {
        return possibilitySmeltList;
    }

    public SmeltType getRule(Material material) {
        return ruleSmelt.get(material);
    }

    public HashMap<Material, SmeltType> getRuleSmelt() {
        return ruleSmelt;
    }

    public int getPrice() {
        return priceForSmelt;
    }

    public void setPrice(int priceForSmelt) {
        this.priceForSmelt = priceForSmelt;
    }

    public ItemStack getItem() {
        return itemForSmelt;
    }

    public void setItem(ItemStack itemForSmelt) {
        this.itemForSmelt = itemForSmelt;
    }

    public void addRule(String materialId, String smeltType) {
        ruleSmelt.put(Material.getMaterial(materialId), SmeltType.valueOf(smeltType));
    }

    public void addPossibility(int possibility) {
        possibilitySmeltList.add(possibility);
    }

    public void clear() {
        if(ruleSmelt!=null)
            ruleSmelt.clear();
        if(possibilitySmeltList!=null)
            possibilitySmeltList.clear();
    }
}

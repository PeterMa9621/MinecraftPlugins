package betterWeapon.manager;

import betterWeapon.BetterWeapon;
import betterWeapon.util.GemType;
import betterWeapon.util.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GemManager {
    private BetterWeapon plugin;

    public List<String> equipment;
    public List<String> inlayWeapon;
    public List<Integer> holePossibility;
    public List<Integer> evaPossibility;
    public List<Integer> itemPossibility;
    public List<Integer> synthesisPossibility;
    public List<GemType> gemType;
    public int priceForHole = 0;
    public int priceForInlay = 0;
    public int priceForEvaluate = 0;
    public int priceForSynthesis = 0;

    public ItemStack gemstone;
    public GemManager(BetterWeapon plugin) {
        this.plugin = plugin;
        gemstone = ItemStackUtil.createItem("coal", "��fδ�����ı�ʯ", new ArrayList<String>() {{
            add("��e[δ����]");
            add("��6һ�鿴������ͨ��ʯͷ");
        }}, 0);
    }

    public int getPriceForEvaluate() {
        return priceForEvaluate;
    }

    public int getPriceForHole() {
        return priceForHole;
    }

    public int getPriceForInlay() {
        return priceForInlay;
    }

    public int getPriceForSynthesis() {
        return priceForSynthesis;
    }

    public void setPriceForEvaluate(int priceForEvaluate) {
        this.priceForEvaluate = priceForEvaluate;
    }

    public void setPriceForHole(int priceForHole) {
        this.priceForHole = priceForHole;
    }

    public void setPriceForInlay(int priceForInlay) {
        this.priceForInlay = priceForInlay;
    }

    public void setPriceForSynthesis(int priceForSynthesis) {
        this.priceForSynthesis = priceForSynthesis;
    }

    public void setGemType(List<String> gemType) {
        this.gemType = new ArrayList<>();
        for(String type:gemType) {
            this.gemType.add(GemType.valueOf(type));
        }
    }

    public void clear() {
        if(this.gemType!=null)
            this.gemType.clear();
    }
}

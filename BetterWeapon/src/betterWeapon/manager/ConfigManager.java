package betterWeapon.manager;

import betterWeapon.BetterWeapon;
import betterWeapon.util.ItemStackUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private BetterWeapon plugin;
    private SmeltManager smeltManager;
    private IntensifyManager intensifyManager;
    private AssistantManager assistantManager;
    private GemManager gemManager;
    public ConfigManager(BetterWeapon plugin) {
        this.plugin = plugin;
        smeltManager = this.plugin.smeltManager;
        intensifyManager = this.plugin.intensifyManager;
        assistantManager = this.plugin.assistantManager;
        gemManager = this.plugin.gemManager;
    }

    public void loadConfig()
    {
        File file=new File(plugin.getDataFolder(),"config.yml");
        FileConfiguration config;
        if (!file.exists())
        {
            config = load(file);
            config.set("Item.ItemID", "IRON_INGOT");
            config.set("Item.Name", "§a强化石");
            config.set("Item.Model", 1);
            config.set("Item.Lore", new ArrayList<String>() {{
                add("§3这是一块不同寻常的石头");
                add("§2[使用方法]");
                add("§a打开强化系统强化装备");
            }});

            config.set("Assistants.1.ItemID", "IRON_INGOT");
            config.set("Assistants.1.Name", "§b金刚石");
            config.set("Assistants.1.Model", 2);
            config.set("Assistants.1.Lore", new ArrayList<String>() {{
                add("§3这是一种十分坚硬的石头");
                add("§2[用途]");
                add("§a用于辅助强化装备");
                add("§a在装备强化失败时装备不会碎掉");
            }});

            config.set("Assistants.2.ItemID", "IRON_INGOT");
            config.set("Assistants.2.Name", "§b幸运果");
            config.set("Assistants.2.Model", 3);
            config.set("Assistants.2.Lore", new ArrayList<String>() {{
                add("§3幸运果实");
                add("§2[用途]");
                add("§a用于辅助强化装备");
                add("§a在下一次强化装备时一定成功");
            }});

            config.set("Equipment", new ArrayList<String>() {{
                add("diamond_sword:sharpness");
                add("diamond_pickaxe:fortune");
            }});
            config.set("Possibility", new int[] {100, 80, 70, 60, 40, 15, 8, 5, 3, 1});

            config.set("Smelt.Item.ItemID", "IRON_INGOT");
            config.set("Smelt.Item.Name", "§a熔炼石");
            config.set("Smelt.Item.Model", 4);
            config.set("Smelt.Item.Lore", new ArrayList<String>() {{
                add("§3一种稀有的石头");
                add("§2[使用方法]");
                add("§a打开熔炼系统熔炼装备");
            }});

            config.set("Smelt.Equipment", new ArrayList<String>() {{
                add("diamond_sword:attack");
                add("diamond_chestplate:defend");
            }});
            config.set("Smelt.Possibility", new int[] {70, 60, 40, 20, 15, 8, 5, 4, 2, 1});
            config.set("Smelt.Price", 10000);

            config.set("Gem.Hole.Equipment", new ArrayList<String>() {{
                add("diamond_sword");
                add("diamond_helmet");
                add("diamond_chestplate");
                add("diamond_leggings");
                add("diamond_boots");
            }});
            config.set("Gem.Hole.Possibility", new int[] {50, 10, 5});
            config.set("Gem.Hole.Price", 5000);

            config.set("Gem.Inlay.Price", 5000);
            config.set("Gem.Inlay.Equipment", new ArrayList<String>() {{
                add("iron_sword");
                add("wooden_sword");
                add("stone_sword");
                add("diamond_sword");
                add("golden_sword");
            }});

            config.set("Gem.Evaluate.Possibility", new int[] {100,40,20,5,3,1});
            config.set("Gem.Evaluate.Price", 1000);

            config.set("Gem.Item.ItemID", "IRON_INGOT");
            config.set("Gem.Item.Name", "§f未鉴定的宝石");
            config.set("Gem.Item.Model", 24);
            config.set("Gem.Item.AnotherModel", 25);
            config.set("Gem.Item.Lore", new ArrayList<String>() {{
                add("§e[未鉴定]");
                add("§6一块看起来普通的石头");
            }});

            config.set("Gem.Item.Type", new ArrayList<String>() {{
                add("attack");
                add("defend");
                add("block");
                add("crit");
                add("penetrate");
            }});
            config.set("Gem.Item.Possibility", new int[] {100,100,50,50,20});

            config.set("Gem.Synthesis.Price", 100);
            config.set("Gem.Synthesis.Possibility", new int[] {70,50,20,10,3});

            try
            {
                config.save(file);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            loadConfig();
            return;
        }

        config = load(file);
        init();

        //------------------------------------------------
        String itemID = config.getString("Item.ItemID");
        int customModelId = config.getInt("Item.Model");
        String itemName =  config.getString("Item.Name");
        List<String> itemLore = config.getStringList("Item.Lore");

        ItemStack itemForIntensify = ItemStackUtil.createItem(itemID, itemName, itemLore, customModelId);
        intensifyManager.setItem(itemForIntensify);

        //------------------------------------------------
        itemID = config.getString("Smelt.Item.ItemID");
        customModelId = config.getInt("Smelt.Item.Model");
        itemName =  config.getString("Smelt.Item.Name");
        itemLore = config.getStringList("Smelt.Item.Lore");

        ItemStack itemForSmelt = ItemStackUtil.createItem(itemID, itemName, itemLore, customModelId);
        smeltManager.setItem(itemForSmelt);

        //------------------------------------------------
        //第一个是保护装备不碎的，第二个是百分百强化的
        for(int i=0; config.contains("Assistants."+(i+1)); i++) {
            String assistantID = config.getString("Assistants."+(i+1)+".ItemID");
            int assistantCustomModelID = config.getInt("Assistants."+(i+1)+".Model");
            String assistantName = config.getString("Assistants."+(i+1)+".Name");
            List<String> assistantLore = config.getStringList("Assistants."+(i+1)+".Lore");

            ItemStack assistant = ItemStackUtil.createItem(assistantID, assistantName, assistantLore, assistantCustomModelID);
            assistantManager.addAssistant(assistant);
        }

        //------------------------------------------------
        for(String equip:config.getStringList("Equipment")) {
            String equipID = equip.split(":")[0].toUpperCase();
            String enchantID = equip.split(":")[1].toLowerCase();
            intensifyManager.addRule(equipID, enchantID);
        }

        //------------------------------------------------
        List<Integer> possibility = config.getIntegerList("Possibility");
        for(int p:possibility) {
            intensifyManager.addPossibility(p);
        }

        //------------------------------------------------
        smeltManager.setPrice(config.getInt("Smelt.Price"));

        List<String> equipmentForSmelt = config.getStringList("Smelt.Equipment");
        for(String equip:equipmentForSmelt) {
            String[] rule = equip.split(":");
            smeltManager.addRule(rule[0], rule[1]);
        }

        //------------------------------------------------
        List<Integer> possibilityForSmelt = config.getIntegerList("Smelt.Possibility");

        for(int p:possibilityForSmelt) {
            smeltManager.addPossibility(p);
        }

        //------------------------------------------------
        int priceForEvaluate = config.getInt("Gem.Evaluate.Price");
        gemManager.setPriceForEvaluate(priceForEvaluate);
        int priceForInlay = config.getInt("Gem.Inlay.Price");
        gemManager.setPriceForInlay(priceForInlay);
        int priceForHole = config.getInt("Gem.Hole.Price");
        gemManager.setPriceForHole(priceForHole);
        int priceForSynthesis = config.getInt("Gem.Synthesis.Price");
        gemManager.setPriceForSynthesis(priceForSynthesis);

        gemManager.setEquipment(config.getStringList("Gem.Hole.Equipment"));
        gemManager.holePossibility = config.getIntegerList("Gem.Hole.Possibility");
        gemManager.setInlayEquipment(config.getStringList("Gem.Inlay.Equipment"));
        gemManager.evaPossibility = config.getIntegerList("Gem.Evaluate.Possibility");
        gemManager.itemPossibility = config.getIntegerList("Gem.Item.Possibility");
        gemManager.synthesisPossibility = config.getIntegerList("Gem.Synthesis.Possibility");
        List<String> gemType = config.getStringList("Gem.Item.Type");
        itemID = config.getString("Gem.Item.ItemID", "IRON_INGOT");
        String displayName =  config.getString("Gem.Item.Name", "§f未鉴定的宝石");
        customModelId = config.getInt("Gem.Item.Model", 4);

        itemLore = config.getStringList("Gem.Item.Lore");
        gemManager.afterEvaluateModel = config.getInt("Gem.Item.AnotherModel", 25);
        gemManager.setGemstone(itemID, displayName, customModelId, itemLore);
        gemManager.setGemType(gemType);
    }

    public void init()
    {
        gemManager.clear();
        intensifyManager.clear();
        smeltManager.clear();
        assistantManager.clear();
    }

    public FileConfiguration load(File file)
    {
        if (!(file.exists()))
        { //假如文件不存在
            try   //捕捉异常，因为有可能创建不成功
            {
                file.createNewFile();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}

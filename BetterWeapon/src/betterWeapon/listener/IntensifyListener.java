package betterWeapon.listener;

import betterWeapon.BetterWeapon;
import betterWeapon.util.SmeltType;
import betterWeapon.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IntensifyListener implements Listener {
    private BetterWeapon plugin;
    private int[] slot = {0,1,2,3,4,5,6,7,8,9,11,12,14,15,17,18,19,20,21,22,23,24,25,26,27,28
            ,29,30,32,33,34,35,36,37,38,39,41,42,43,44};
    public IntensifyListener(BetterWeapon plugin) {
        this.plugin = plugin;
    }

    public void _intensify(InventoryClickEvent event, ItemStack equip, int level)
    {
        ItemMeta meta = equip.getItemMeta();
        //meta.getLore().toArray()
        List<String> loreList = new ArrayList<>();

        if(meta.getLore()!=null) {
            loreList.addAll(meta.getLore());
        }
        Bukkit.getLogger().info(String.valueOf(level));
        ArrayList<String> lore = new ArrayList<String>() {{
            add("§a强化等级:§c"+(level+1));
            add("§e[附魔强化]");
        }};
        int index = Util.getLoreIndex(equip);

        if(loreList.contains("§e[附魔强化]")) {
            int existedIndex = loreList.indexOf("§e[附魔强化]");
            for(String l:lore) {
                loreList.remove(existedIndex);
            }

            for(String l:lore) {
                loreList.add(existedIndex, l);
            }
        } else {
            for(String l:lore) {
                loreList.add(index, l);
            }
        }

        //String lore = "§a强化等级:"+(level+1)+"%%§a锻造者为:§3"+event.getWhoClicked().getName();
        //ArrayList<String> loreList = new ArrayList<String>();

        meta.setLore(loreList);
        equip.setItemMeta(meta);
        Enchantment enchantment = plugin.intensifyManager.getEnchant(equip.getType().toString());
        equip.addUnsafeEnchantment(enchantment, level+1);
    }

    public void intensify(InventoryClickEvent event, boolean assistant1, boolean assistant2)
    {
        Player p = (Player)event.getWhoClicked();
        //---------------------------------------------
        ItemStack equip = event.getInventory().getItem(10);
        if(equip==null)
            return;
        Enchantment enchantment = plugin.intensifyManager.getEnchant(equip.getType().toString());
        int level = equip.getEnchantmentLevel(enchantment);

        ArrayList<Integer> possibilityList = plugin.intensifyManager.getPossibilityList();
        if(level<possibilityList.size()) {
            event.getInventory().setItem(10, null);

            ItemStack intensifyRock = event.getInventory().getItem(13);
            intensifyRock.setAmount(intensifyRock.getAmount()-1);

            if(assistant2 || Util.getRandomInt(100)<possibilityList.get(level)) {
                _intensify(event, equip, level);

                p.sendMessage("§a[强化系统]§c恭喜，强化成功！");
                if(level+1>5) {
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.0F);
                    Bukkit.broadcastMessage("§3恭喜玩家§a"+p.getName()+"§3强化装备§a"+"§3至§a"+(level+1)+"§3级");
                }
                else {
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.0F);
                }

                if(assistant2) {
                    ItemStack assistantItem = event.getInventory().getItem(16);
                    if(assistantItem!=null)
                        assistantItem.setAmount(assistantItem.getAmount()-1);
                }
            } else {
                if(level>=5) {
                    if(assistant1) {
                        ItemStack assistantItem = event.getInventory().getItem(16);
                        if(assistantItem!=null)
                            assistantItem.setAmount(assistantItem.getAmount()-1);
                        p.sendMessage("§a[强化系统]§5很遗憾，强化失败，辅助物品发挥效果，保护住了武器！");
                    } else {
                        equip = null;
                        p.sendMessage("§a[强化系统]§5很遗憾，强化失败，装备碎掉了！");
                    }
                } else {
                    p.sendMessage("§a[强化系统]§5很遗憾，强化失败！");
                }
            }
            event.getInventory().setItem(40, equip);
        } else {
            p.sendMessage("§a[强化系统]§5该装备已强化到最大等级，无需继续强化！");
        }
    }

    @EventHandler
    public void onPlayerClickGUI(InventoryClickEvent event)
    {
        if(event.getView().getTitle().equalsIgnoreCase("§5附魔强化系统")) {
            if(plugin.isExist(event.getRawSlot(), slot)) {
                event.setCancelled(true);
            }

            if(event.getRawSlot()==44) {
                event.getWhoClicked().openInventory(plugin.guiManager.initMainGUI((Player)event.getWhoClicked()));
                return;
            }

            if(event.getRawSlot()==31 &&
                    event.getInventory().getItem(31).getItemMeta().getDisplayName().equalsIgnoreCase("§5点击开始强化"))
            {
                Player p = (Player)event.getWhoClicked();
                event.setCancelled(true);
                if(event.getInventory().getItem(40)!=null) {
                    event.getWhoClicked().sendMessage("§a[强化系统] §c强化按钮下方不允许放置任何物品！");
                    return;
                }
                HashMap<String, String> rule = plugin.intensifyManager.getRule();
                ItemStack equip = event.getInventory().getItem(10);

                if(equip!=null && rule.containsKey(equip.getType().toString())) {
                    ItemStack intensifyRock = event.getInventory().getItem(13);
                    if(intensifyRock!=null) {
                        if(!intensifyRock.getItemMeta().hasLore()) {
                            p.sendMessage("§a[强化系统] §c缺少强化石或无效的强化石");
                            return;
                        }
                        if(!intensifyRock.isSimilar(plugin.intensifyManager.getItem())) {
                            p.sendMessage("§a[强化系统] §c缺少强化石或无效的强化石");
                            return;
                        }

                        ItemStack assistant = event.getInventory().getItem(16);
                        if(assistant==null) {
                            intensify(event, false, false);
                            return;
                        }

                        //金刚石
                        if(assistant.isSimilar(plugin.assistantManager.getAssistant(0))) {
                            intensify(event, true, false);
                            return;
                        }

                        //幸运宝石
                        if(assistant.isSimilar(plugin.assistantManager.getAssistant(1))) {
                            intensify(event, false, true);
                            return;
                        }

                        p.sendMessage("§a[强化系统] §c无效的辅助物品");
                    }
                    else {
                        p.sendMessage("§a[强化系统] §c缺少强化石或无效的强化石");
                    }
                }
                else {
                    p.sendMessage("§a[强化系统] §c缺少装备或无效的装备");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCloseGUI(InventoryCloseEvent event)
    {
        if(event.getView().getTitle().equalsIgnoreCase("§5附魔强化系统"))
        {
            if(event.getInventory().getItem(10)!=null)
            {
                event.getPlayer().getInventory().addItem(event.getInventory().getItem(10));
            }
            if(event.getInventory().getItem(13)!=null)
            {
                event.getPlayer().getInventory().addItem(event.getInventory().getItem(13));
            }
            if(event.getInventory().getItem(16)!=null)
            {
                event.getPlayer().getInventory().addItem(event.getInventory().getItem(16));
            }
            if(event.getInventory().getItem(40)!=null)
            {
                event.getPlayer().getInventory().addItem(event.getInventory().getItem(40));
            }
        }
    }
}

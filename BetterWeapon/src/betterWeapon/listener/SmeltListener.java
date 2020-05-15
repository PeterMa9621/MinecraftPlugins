package betterWeapon.listener;

import betterWeapon.BetterWeapon;
import betterWeapon.util.ItemStackUtil;
import betterWeapon.util.SmeltType;
import betterWeapon.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class SmeltListener implements Listener {
    private BetterWeapon plugin;
    private int[] slot = {0,1,2,3,4,5,6,7,8,9,11,12,13,14,15,17,18,19,20,21,22,23,24,25,26,27,28
            ,29,30,32,33,34,35,36,37,38,39,41,42,43,44};
    public SmeltListener(BetterWeapon plugin) {
        this.plugin = plugin;
    }

    public void _smelt(InventoryClickEvent event, ItemStack equip, int level, SmeltType smeltType)
    {
        ItemMeta meta = equip.getItemMeta();
        ArrayList<String> loreList = new ArrayList<String>();

        if(meta.getLore()!=null) {
            loreList.addAll(meta.getLore());
        }

        String type;
        if(smeltType.equals(SmeltType.attack))
            type = "����";
        else if(smeltType.equals(SmeltType.defend))
            type = "����";
        else
            return;

        ArrayList<String> lore = new ArrayList<String>() {{
            if(type.equalsIgnoreCase("����"))
                add("��a��������:��c"+ type + "+" +(level+1));
            else
                add("��a��������:��c"+ type + "+" +((level+1)/2.0));
            add("��e[����]");
        }};

        int index = Util.getLoreIndex(equip);
        if(loreList.contains("��e[����]")) {
            int existedIndex = loreList.indexOf("��e[����]");
            for(int i=0; i<lore.size(); i++) {
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

        //String lore = "��aǿ���ȼ�:"+(level+1)+"%%��a������Ϊ:��3"+event.getWhoClicked().getName();
        //ArrayList<String> loreList = new ArrayList<String>();

        meta.setLore(loreList);
        equip.setItemMeta(meta);
    }

    public void smelt(InventoryClickEvent event)
    {
        Player p = (Player)event.getWhoClicked();
        //---------------------------------------------
        ItemStack equip = event.getInventory().getItem(10);

        SmeltType smeltType = plugin.smeltManager.getRule(equip.getType());

        ItemStack smeltRock = event.getInventory().getItem(16);
        smeltRock.setAmount(smeltRock.getAmount()-1);

        ArrayList<Integer> possibilitySmeltList = plugin.smeltManager.getPossibilitySmeltList();
        int level = Util.getRandomInt(possibilitySmeltList.size());

        for(int i=level; i>=0; i--) {
            if(Util.getRandomInt(100)<possibilitySmeltList.get(i)) {
                _smelt(event, equip, i, smeltType);
                p.sendMessage("��a[����ϵͳ] ��c��ϲ�������ɹ���");

                if(i+1>5) {
                    if(smeltType.equals(SmeltType.attack)) {
                        Bukkit.broadcastMessage("��3��ϲ��ҡ�a"+p.getName()+"��3�����������֡�a"+(i+1)+"��3�����Լӳ�");
                    } else {
                        double highLevel = (i+1)/2.0;
                        Bukkit.broadcastMessage("��3��ϲ��ҡ�a"+p.getName()+"��3����װ�����֡�a"+highLevel+"��3�����Լӳ�");
                    }

                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.0F);
                } else {
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.0F);
                }
                event.getInventory().setItem(10, null);
                event.getInventory().setItem(40, equip);
                return;
            }
        }
        p.sendMessage("��a[����ϵͳ]��5���������г������⣬����ʧ��");

        event.getInventory().setItem(10, null);
        event.getInventory().setItem(40, equip);
    }

    @EventHandler
    public void onPlayerClickGUI(InventoryClickEvent event)
    {
        if(event.getView().getTitle().equalsIgnoreCase("��5����ϵͳ")) {
            if(plugin.isExist(event.getRawSlot(), slot)) {
                event.setCancelled(true);
            }

            if(event.getRawSlot()==44) {
                event.getWhoClicked().openInventory(plugin.guiManager.initMainGUI((Player)event.getWhoClicked()));
                return;
            }

            if(event.getRawSlot()==31 &&
                    event.getInventory().getItem(31).getItemMeta().getDisplayName().equalsIgnoreCase("��5�����ʼ����"))
            {
                Player p = (Player)event.getWhoClicked();
                event.setCancelled(true);
                if(event.getInventory().getItem(40)!=null) {
                    event.getWhoClicked().sendMessage("��a[����ϵͳ] ��c������ť�·�����������κ���Ʒ��");
                    return;
                }

                HashMap<Material, SmeltType> ruleSmelt = plugin.smeltManager.getRuleSmelt();
                if(event.getInventory().getItem(10)!=null &&
                        ruleSmelt.containsKey(event.getInventory().getItem(10).getType()))
                {
                    ItemStack smeltRock = event.getInventory().getItem(16);

                    if(smeltRock!=null && ItemStackUtil.isSimilar(smeltRock, plugin.smeltManager.getItem())) {
                        int priceForSmelt = plugin.smeltManager.getPrice();
                        if(plugin.economy.getBalance(p.getName())>=priceForSmelt) {
                            p.sendMessage("��a[����ϵͳ] ��e�۳���c" + priceForSmelt + "��e���");
                            plugin.economy.withdrawPlayer(p.getName(), priceForSmelt);
                            smelt(event);
                        } else {
                            p.sendMessage("��a[����ϵͳ] ��c���������Ҳ���");
                        }
                    } else {
                        p.sendMessage("��a[����ϵͳ] ��cȱ������ʯ����Ч������ʯ");
                    }
                } else {
                    p.sendMessage("��a[����ϵͳ] ��cȱ��װ������Ч��װ��");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCloseGUI(InventoryCloseEvent event)
    {
        if(event.getView().getTitle().equalsIgnoreCase("��5����ϵͳ"))
        {
            if(event.getInventory().getItem(10)!=null)
            {
                event.getPlayer().getInventory().addItem(event.getInventory().getItem(10));
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

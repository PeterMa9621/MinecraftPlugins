package betterWeapon.Gem;

import betterWeapon.BetterWeapon;
import betterWeapon.util.Util;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class OpenHoleListener implements Listener {
    private BetterWeapon plugin;
    private int[] slotHole = {0,1,2,3,4,5,6,7,8,9,11,12,13,14,15,17,18,19,20,21,23,24,25,26};
    public OpenHoleListener(BetterWeapon plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerClickGUI(InventoryClickEvent event)
    {
        if(event.getView().getTitle().equalsIgnoreCase("��5װ������"))
        {
            if(plugin.isExist(event.getRawSlot(), slotHole)) {
                event.setCancelled(true);
            }

            if(event.getRawSlot()==26) {
                event.getWhoClicked().openInventory(plugin.gemGui.initMainGUI((Player)event.getWhoClicked()));
                return;
            }

            if(event.getRawSlot()==22 &&
                    event.getInventory().getItem(22).getItemMeta().getDisplayName().equalsIgnoreCase("��5�����ʼ����"))
            {
                event.setCancelled(true);
                Player p = (Player)event.getWhoClicked();
                if(event.getInventory().getItem(16)!=null) {
                    event.getWhoClicked().sendMessage("��a[��ʯϵͳ]��c װ�����������ֹ�����κ���Ʒ��");
                    return;
                }

                ItemStack equip = event.getInventory().getItem(10);
                if(equip!=null && plugin.gemManager.equipment.contains(equip.getType().toString())) {
                    if(equip.hasItemMeta() && equip.getItemMeta().hasLore()) {
                        if(equip.getItemMeta().getLore().contains("��e[�ѿ���]")) {
                            p.sendMessage("��a[��ʯϵͳ]��c ���װ���Ѿ���������");
                            return;
                        }
                    }

                    int priceForHole = plugin.gemManager.priceForHole;
                    if(plugin.economy.getBalance(p.getName())>=priceForHole) {
                        plugin.economy.withdrawPlayer(p.getName(), priceForHole);
                        p.sendMessage("��a[��ʯϵͳ]��e �۳���c"+priceForHole+"��e���");
                        hole(event);
                    } else {
                        p.sendMessage("��a[��ʯϵͳ]��c װ�����������Ҳ���");
                    }
                } else {
                    p.sendMessage("��a[��ʯϵͳ]��c ȱ��װ������Ч��װ��");
                }
            }
        }
    }


    private void hole(InventoryClickEvent event)
    {
        Player p = (Player)event.getWhoClicked();
        //---------------------------------------------
        ItemStack equip = event.getInventory().getItem(10);

        int quantity = Util.getRandomInt(plugin.gemManager.holePossibility.size());

        for(int i=quantity; i>=0; i--)
        {
            if(Util.getRandomInt(100)<plugin.gemManager.holePossibility.get(i))
            {
                _hole(event, equip, i);
                p.sendMessage("��a[��ʯϵͳ]��c ��ϲ�����׳ɹ���");

                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.0F);
                event.getInventory().setItem(10, null);
                event.getInventory().setItem(16, equip);
                return;
            }
        }
        p.sendMessage("��a[��ʯϵͳ]��5 ���׹����г������⣬����ʧ��");

        event.getInventory().setItem(10, null);
        event.getInventory().setItem(16, equip);
    }

    private void _hole(InventoryClickEvent event, ItemStack equip, int quantity)
    {
        ItemMeta meta = equip.getItemMeta();
        List<String> loreList = meta.getLore();

        ArrayList<String> lore = new ArrayList<String>() {{
            add("��a��������:��c"+(quantity+1));
            add("��e[�ѿ���]");
        }};
        int index = Util.getLoreIndex(equip);
        if(loreList!=null && loreList.contains("��e[�ѿ���]"))
        {
            int existedIndex = loreList.indexOf("��e[�ѿ���]");
            for(String ignored:lore) {
                loreList.remove(existedIndex);
            }

            for(String l:lore) {
                loreList.add(existedIndex, l);
            }
        }
        else
        {
            if(loreList==null)
                loreList = new ArrayList<>();
            for(String l:lore) {
                loreList.add(index, l);
            }
        }

		/*
		String lore = "��e[�ѿ���]%��a��������:��c"+(quantity+1);

		for(String l:lore.split("%"))
		{
			loreList.add(l);
		}
		*/

        meta.setLore(loreList);
        equip.setItemMeta(meta);
    }

    @EventHandler
    public void onPlayerCloseGUI(InventoryCloseEvent event)
    {

        if(event.getView().getTitle().equalsIgnoreCase("��5װ������"))
        {
            if(event.getInventory().getItem(10)!=null)
            {
                event.getPlayer().getInventory().addItem(event.getInventory().getItem(10));
            }
            if(event.getInventory().getItem(16)!=null)
            {
                event.getPlayer().getInventory().addItem(event.getInventory().getItem(16));
            }
        }
    }
}

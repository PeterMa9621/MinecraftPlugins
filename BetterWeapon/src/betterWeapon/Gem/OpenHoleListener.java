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
        if(event.getView().getTitle().equalsIgnoreCase("§5装备开孔"))
        {
            if(plugin.isExist(event.getRawSlot(), slotHole)) {
                event.setCancelled(true);
            }

            if(event.getRawSlot()==26) {
                event.getWhoClicked().openInventory(plugin.gemGui.initMainGUI((Player)event.getWhoClicked()));
                return;
            }

            if(event.getRawSlot()==22 &&
                    event.getInventory().getItem(22).getItemMeta().getDisplayName().equalsIgnoreCase("§5点击开始开孔"))
            {
                event.setCancelled(true);
                Player p = (Player)event.getWhoClicked();
                if(event.getInventory().getItem(16)!=null) {
                    event.getWhoClicked().sendMessage("§a[宝石系统]§c 装备出产区域禁止放置任何物品！");
                    return;
                }

                ItemStack equip = event.getInventory().getItem(10);
                if(equip!=null && plugin.gemManager.equipment.contains(equip.getType().toString())) {
                    if(equip.hasItemMeta() && equip.getItemMeta().hasLore()) {
                        if(equip.getItemMeta().getLore().contains("§e[已开孔]")) {
                            p.sendMessage("§a[宝石系统]§c 这个装备已经开过孔了");
                            return;
                        }
                    }

                    int priceForHole = plugin.gemManager.priceForHole;
                    if(plugin.economy.getBalance(p.getName())>=priceForHole) {
                        plugin.economy.withdrawPlayer(p.getName(), priceForHole);
                        p.sendMessage("§a[宝石系统]§e 扣除§c"+priceForHole+"§e金币");
                        hole(event);
                    } else {
                        p.sendMessage("§a[宝石系统]§c 装备开孔所需金币不足");
                    }
                } else {
                    p.sendMessage("§a[宝石系统]§c 缺少装备或无效的装备");
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
                p.sendMessage("§a[宝石系统]§c 恭喜，开孔成功！");

                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5F, 0.0F);
                event.getInventory().setItem(10, null);
                event.getInventory().setItem(16, equip);
                return;
            }
        }
        p.sendMessage("§a[宝石系统]§5 开孔过程中出现意外，开孔失败");

        event.getInventory().setItem(10, null);
        event.getInventory().setItem(16, equip);
    }

    private void _hole(InventoryClickEvent event, ItemStack equip, int quantity)
    {
        ItemMeta meta = equip.getItemMeta();
        List<String> loreList = meta.getLore();

        ArrayList<String> lore = new ArrayList<String>() {{
            add("§a开孔数量:§c"+(quantity+1));
            add("§e[已开孔]");
        }};
        int index = Util.getLoreIndex(equip);
        if(loreList!=null && loreList.contains("§e[已开孔]"))
        {
            int existedIndex = loreList.indexOf("§e[已开孔]");
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
		String lore = "§e[已开孔]%§a开孔数量:§c"+(quantity+1);

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

        if(event.getView().getTitle().equalsIgnoreCase("§5装备开孔"))
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

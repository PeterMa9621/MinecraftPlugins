package lookItem;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LookItemListener implements Listener
{
	private LookItem plugin;

	public LookItemListener(LookItem plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerClickQuickShop(PlayerInteractEvent event)
    {
		if(event.getPlayer().isSneaking())
		{
			if(event.getAction()==Action.RIGHT_CLICK_BLOCK)
			{
				if(event.getClickedBlock().getType()==Material.WALL_SIGN)
				{
					if(((Sign)event.getClickedBlock().getState()).getLine(0).equalsIgnoreCase("§c[QuickShop]"))
					{
						Player p = event.getPlayer();

						Block chest = event.getClickedBlock().getRelative(event.getBlockFace().getOppositeFace());
						
						Location l = chest.getLocation();
						
						if(plugin.qs.getShopManager().getShop(l)!=null)
						{
							ItemStack item = plugin.qs.getShopManager().getShop(l).getItem();
							double price = plugin.qs.getShopManager().getShop(l).getPrice();
							int amount = plugin.qs.getShopManager().getShop(l).getRemainingStock();
							p.openInventory(plugin.createGui(p, item, price, amount));
						}
					}
					
					
				}
			}
		}
    }
	
	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent event)
    {
		if(event.getInventory().getTitle().equalsIgnoreCase("§5查看抽奖箱物品"))
		{
			event.setCancelled(true);
		}
    }
}

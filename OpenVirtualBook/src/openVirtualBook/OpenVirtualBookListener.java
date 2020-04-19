package openVirtualBook;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class OpenVirtualBookListener implements Listener
{
	private OpenVirtualBook plugin;

	public OpenVirtualBookListener(OpenVirtualBook plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
    {
		if(plugin.set)
		{
			if(event.getClickedBlock().getType().equals(Material.CHEST) ||
					event.getClickedBlock().getType().equals(Material.TRAPPED_CHEST))
			{
				plugin.x = event.getClickedBlock().getLocation().getBlockX();
				plugin.y = event.getClickedBlock().getLocation().getBlockY();
				plugin.z = event.getClickedBlock().getLocation().getBlockZ();
				plugin.worldName = event.getClickedBlock().getLocation().getWorld().getName();
				plugin.set = false;
				event.getPlayer().sendMessage("§6[虚拟书籍] §a设置完成");
				
				Block chestBlock = event.getClickedBlock();
				
				plugin.books = plugin.getBooks(chestBlock);
			}
			else
			{
				event.getPlayer().sendMessage("§6[虚拟书籍] §c这不是一个箱子!");
			}
		}
    }
}

package getMoreDrops;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import getMoreDrops.GetMoreDrops;


public class EventListen implements Listener 
{
	private GetMoreDrops plugin;
	public EventListen(GetMoreDrops plugin)
	{
		this.plugin=plugin;
	}
	
	public static int getRandomNumber(int Maximum)
	{
		java.util.Random random=new java.util.Random();// 定义随机类
		int result=random.nextInt(Maximum);// 返回[0,10)集合中的整数，注意不包括10
		return result;              // +1后，[0,10)集合变为[1,11)集合，满足要求
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(plugin.useMob==false)
		{
			return;
		}
		
		if(plugin.mobIDList.contains(Integer.valueOf(event.getEntity().getType().getTypeId())))
		{
			int index = plugin.mobIDList.indexOf(Integer.valueOf(event.getEntity().getType().getTypeId()));
			if(getRandomNumber(100)<plugin.mobProbabilityList.get(index))
			{
				event.getDrops().add(plugin.mobItemList.get(index));
			}
		}

	}
	
	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent event)
	{
		if(plugin.useBlock==false)
		{
			return;
		}
		if(plugin.blockIDList.contains(event.getBlock().getTypeId()))
		{
			int index = plugin.blockIDList.indexOf(event.getBlock().getTypeId());
			if(getRandomNumber(100)<plugin.blockProbabilityList.get(index))
			{
				if(!event.getPlayer().getItemInHand().containsEnchantment(Enchantment.getById(33)))
				{
					event.getPlayer().getInventory().addItem(plugin.blockItemList.get(index));
				}
			}
		}
	}
}


package betterWeapon.listener;

import java.util.List;

import betterWeapon.BetterWeapon;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BetterWeaponEntityListener implements Listener
{
	private BetterWeapon plugin;
	
	public BetterWeaponEntityListener(BetterWeapon plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerDamaged(EntityDamageByEntityEvent event)
    {
		// ===================================================================
		// This is used to calculate the final damage when mobs attack players
		// ===================================================================
		if(event.getEntity() instanceof Player)
		{
			Player p = (Player)event.getEntity();
			if(!event.getDamager().getType().equals(EntityType.PLAYER))
			{
				ItemStack boots= p.getEquipment().getBoots();
				ItemStack chest= p.getEquipment().getChestplate();
				ItemStack helmet= p.getEquipment().getHelmet();
				ItemStack leg= p.getEquipment().getLeggings();
				ItemStack[] list = {boots, chest, helmet, leg};
				double defend = 0;
				double blockPercent = 0;
				for(ItemStack i:list)
				{
					if(i!=null)
					{
						if(!i.hasItemMeta())
							continue;
						if(!i.getItemMeta().hasLore())
							continue;
						ItemMeta meta = i.getItemMeta();
						List<String> lore = meta.getLore();
						boolean found = false;

						for(String l:lore)
						{
							if(found==true)
							{
								String value = l.split("\\+")[1];
								defend += Double.valueOf(value);
								break;
							}
							if(l.equalsIgnoreCase("��e[����]"))
								found = true;
						}
						
						for(String l:lore)
						{
							if(l.contains("��2��Ƕ:"))
							{
								String attribute = l.split(":")[1].substring(2);
								String type = attribute.substring(0, 2);
								String value = attribute.split("\\+")[1];
								if(type.equalsIgnoreCase("����"))
								{
									defend += Double.valueOf(value);
								}
								else if(type.equalsIgnoreCase("��"))
								{
									blockPercent += Double.valueOf(value);
								}
							}
						}
					}
				}
				double result = 0;

				if(plugin.random(100)<(blockPercent/1.5))
				{
					p.sendMessage("��7�Ѹ񵲴˴��˺�");
					result = 0;
				}
				else
				{
					result = event.getDamage()-(defend/2);
				}
				if(result<0)
					result = 0;
				if(plugin.notify==true)
				{
					if(p.isOp())
					{
						p.sendMessage("��a�з�����:��c"+event.getDamage());
						p.sendMessage("��a����:��c"+defend+"��a,�����˺�:��c"+result);
					}
				}
				event.setDamage(result);
			}
		}
		
		
		// ======================================================================
		// This is used to calculate the final damage when players attack players
		// ======================================================================
		
		if(event.getDamager() instanceof Player)
		{
			Player p = (Player)event.getDamager();
			if(event.getEntity() instanceof Player)
			{
				double attack = 0;
				double directAttack = 0;
				double critPercent = 0;
				ItemStack eqip = p.getItemInHand();
				if((!eqip.getType().equals(Material.AIR)) && (!eqip.equals(null)))
				{

					if(eqip.getItemMeta().hasLore())
					{
						ItemMeta meta = eqip.getItemMeta();
						List<String> lore = meta.getLore();
						boolean found = false;
						

						for(String l:lore)
						{
							if(found==true)
							{
								String value = l.split("\\+")[1];
								attack = Double.valueOf(value);
								break;
							}
							if(l.equalsIgnoreCase("��e[����]"))
								found = true;
						}
						
						for(String l:lore)
						{
							if(l.contains("��2��Ƕ:"))
							{
								String attribute = l.split(":")[1].substring(2);
								String type = attribute.substring(0, 2);
								String value = attribute.split("\\+")[1];
								if(type.equalsIgnoreCase("����"))
								{
									attack += Double.valueOf(value);
								}
								else if(type.equalsIgnoreCase("����"))
								{
									critPercent += Double.valueOf(value);
								}
								else if(type.equalsIgnoreCase("��͸"))
								{
									directAttack += Double.valueOf(value);
								}
							}
						}
						
					}
				}
				
				//--------------------------------------------------------

				Player enemy = (Player)event.getEntity();
				ItemStack boots= enemy.getEquipment().getBoots();
				ItemStack chest= enemy.getEquipment().getChestplate();
				ItemStack helmet= enemy.getEquipment().getHelmet();
				ItemStack leg= enemy.getEquipment().getLeggings();
				ItemStack[] list = {boots, chest, helmet, leg};
				double defend = 0;
				double blockPercent = 0;
				for(ItemStack i:list)
				{
					if(i!=null)
					{
						if(!i.hasItemMeta())
							continue;
						if(!i.getItemMeta().hasLore())
							continue;
						ItemMeta meta = i.getItemMeta();
						List<String> lore = meta.getLore();
						boolean found = false;

						for(String l:lore)
						{
							if(found==true)
							{
								String value = l.split("\\+")[1];
								defend += Double.valueOf(value);
								break;
							}
							if(l.equalsIgnoreCase("��e[����]"))
								found = true;
						}
						
						for(String l:lore)
						{
							if(l.contains("��2��Ƕ:"))
							{
								String attribute = l.split(":")[1].substring(2);
								String type = attribute.substring(0, 2);
								String value = attribute.split("\\+")[1];
								if(type.equalsIgnoreCase("����"))
								{
									defend += Double.valueOf(value);
								}
								else if(type.equalsIgnoreCase("��"))
								{
									blockPercent += Double.valueOf(value);
								}
							}
						}
					}
				}
				double result = 0;
				if(plugin.random(100)<(blockPercent/1.5))
				{
					p.sendMessage("��7�Է��Ѹ񵲴˴��˺�");
					event.getEntity().sendMessage("��7�Ѹ񵲴˴��˺�");
					result = 0;
				}
				else
				{
					if(plugin.random(100)<critPercent)
					{
						p.sendMessage("��7����");
						result = (event.getDamage()+attack-(defend/2))*2;
					}
					else
					{
						result = event.getDamage()+attack-(defend/2);
					}
				}

				if(result<0)
					result = 0;
				if(plugin.notify==true)
				{
					if(p.isOp())
					{
						p.sendMessage("��a����:��c"+attack+"��a,��͸�˺�:��c"+directAttack);
						p.sendMessage("��a����:��c"+defend+"��a,�����˺�:��c"+result+directAttack);
					}
				}
				event.setDamage(result+directAttack);

			// ===================================================================
			// This is used to calculate the final damage when players attack mobs 
			//                  **(ignore directAttack)**
			// ===================================================================
				
			}
			else
			{
				ItemStack eqip = p.getItemInHand();
				if(eqip.getType().equals(Material.AIR) || eqip.equals(null))
					return;
				if(!eqip.getItemMeta().hasLore())
					return;
				ItemMeta meta = eqip.getItemMeta();
				List<String> lore = meta.getLore();
				boolean found = false;
				double attack = 0;
				double directAttack = 0;
				double critPercent = 0;

				for(String l:lore)
				{
					if(found==true)
					{
						String value = l.split("\\+")[1];
						attack = Double.valueOf(value);
						break;
					}

					if(l.equalsIgnoreCase("��e[����]"))
						found = true;
				}

				for(String l:lore)
				{
					if(l.contains("��2��Ƕ:"))
					{
						String attribute = l.split(":")[1].substring(2);
						String type = attribute.substring(0, 2);
						String value = attribute.split("\\+")[1];
						if(type.equalsIgnoreCase("����"))
						{
							attack += Double.valueOf(value);
						}
						else if(type.equalsIgnoreCase("����"))
						{
							critPercent += Double.valueOf(value);
						}
						else if(type.equalsIgnoreCase("��͸"))
						{
							directAttack += Double.valueOf(value);
						}
					}

				}

				double result = 0;
				if(plugin.random(100)<critPercent)
				{
					p.sendMessage("��7����");
					result = (event.getDamage()+attack+directAttack)*2;
				}
				else
				{
					result = (event.getDamage()+attack+directAttack);
				}
				if(plugin.notify==true)
				{
					if(p.isOp())
						p.sendMessage("��a����:��c"+attack+"��a,��͸�˺�:��c"+directAttack+"��a,���˺�:��c"+result);
				}
				event.setDamage(result);
			}
		}
		return;
    }
}

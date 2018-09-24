package personalLottery;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Dropper;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class PersonalLotteryListener implements Listener
{
	private PersonalLottery plugin;
	
	HashMap<Player, Location> players = new HashMap<Player, Location>();
	
	public PersonalLotteryListener(PersonalLottery plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerChangeSign(SignChangeEvent event)
    {
		Player p = event.getPlayer();

		if(players.containsKey(p))
		{
			if(event.getLine(0).equalsIgnoreCase("[抽奖]"))
			{
				if(event.getLine(1).matches("[0-9]*"))
				{
					if(event.getLine(1).length()>6)
					{
						event.setLine(0, "");
						event.setLine(1, "");
						event.setLine(2, "");
						p.sendMessage("§6[私人抽奖箱] §c你设置的价格超过了限制，最大为6位数");
						return;
					}
					if(event.getLine(2).equalsIgnoreCase("金币") || 
							event.getLine(2).equalsIgnoreCase("点券"))
					{
						int limit = 5;
						for(String perm:plugin.permission.keySet())
						{
							if(p.hasPermission(perm))
								limit = plugin.permission.get(perm);
						}
						
						if(plugin.playerData.containsKey(p.getName()) && plugin.playerData.get(p.getName()).getNumber()>=limit)
						{
							event.setLine(0, "");
							event.setLine(1, "");
							event.setLine(2, "");
							p.sendMessage("§6[私人抽奖箱] §c你无法创建更多的抽奖箱了");
							return;
						}
						
						if(plugin.economy.has(p.getName(), plugin.money))
						{
							plugin.economy.withdrawPlayer(p.getName(), plugin.money);
						}
						else
						{
							p.sendMessage("§6[私人抽奖箱] §c你需要至少§5"+plugin.money+"§c金币来创建抽奖箱！");
							return;
						}
						
						event.setLine(0, "§3§l[抽奖]");
						if(event.getLine(2).equalsIgnoreCase("金币"))
							event.setLine(1, "§5§l花费金币:§a§l"+event.getLine(1));
						if(event.getLine(2).equalsIgnoreCase("点券"))
							event.setLine(1, "§5§l花费点券:§a§l"+event.getLine(1));
						//event.setLine(1, "§5§l价格:§a§l"+event.getLine(1));
						event.setLine(2, "§1§l所有人:"+p.getName());
						
						LotteryBox box = new LotteryBox(p.getName(), event.getBlock().getLocation(), players.get(p));
						
						if(plugin.playerData.containsKey(p.getName()))
						{
							plugin.playerData.get(p.getName()).addLotteryBox(box);
						}
						else
						{
							ArrayList<LotteryBox> boxes = new ArrayList<LotteryBox>();
							boxes.add(box);
							PlayerData playerData = new PlayerData(p.getName(), boxes, 1);
							plugin.playerData.put(p.getName(), playerData);
						}
						plugin.dispenser.put(players.get(p), p.getName());
						p.sendMessage("§6[私人抽奖箱] §e已设置一个抽奖箱");
					}
				}
			}
			players.remove(p);
		}
		return;
    }
	
	@EventHandler
	public void onPlayerChangeSign(BlockPlaceEvent event)
    {
		Player p = event.getPlayer();
		if(event.getBlock().getType().equals(Material.WALL_SIGN))
		{
			//p.sendMessage(""+event.getBlockAgainst().getTypeId());
			if(event.getBlockAgainst().getType().equals(Material.DROPPER))
			{
				players.put(p, event.getBlockAgainst().getLocation());
			}
		}
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
					if(((Sign)event.getClickedBlock().getState()).getLine(0).equalsIgnoreCase("§3§l[抽奖]"))
					{
						Sign sign = (Sign)event.getClickedBlock().getState();
						String owner = null;
						if(sign.getLine(2).contains("§1§l所有人:"))
							owner = sign.getLine(2).split("§1§l所有人:")[1];
						if(plugin.playerData.containsKey(owner))
						{
							if(plugin.playerData.get(owner).isLotteryBoxBySign(sign.getLocation()))
							{
								Player p = event.getPlayer();
								if(event.getClickedBlock().getRelative(event.getBlockFace().getOppositeFace()).getType().equals(Material.DROPPER))
								{
									Dropper dispenser = (Dropper) event.getClickedBlock().getRelative(event.getBlockFace().getOppositeFace()).getState();
									ItemStack[] items = dispenser.getInventory().getContents();
									p.openInventory(plugin.createGui(p, items));
								}
							}
						}
						
					}
				}
			}
		}
		else if(event.getAction()==Action.RIGHT_CLICK_BLOCK)
		{
			if(event.getClickedBlock().getType().equals(Material.WALL_SIGN))
			{
				Player p = event.getPlayer();
				if(((Sign)event.getClickedBlock().getState()).getLine(0).equalsIgnoreCase("§3§l[抽奖]"))
				{
					Sign sign = (Sign)event.getClickedBlock().getState();
					if(sign.getLine(1).contains("§5§l花费点券:") || sign.getLine(1).contains("§5§l花费金币:"))
					{
						int point = 0;
						int money = 0;
						if(sign.getLine(1).contains("§5§l花费点券:"))
							point = Integer.valueOf(sign.getLine(1).split("§5§l花费点券:")[1].substring(4));
						else if(sign.getLine(1).contains("§5§l花费金币:"))
							money = Integer.valueOf(sign.getLine(1).split("§5§l花费金币:")[1].substring(4));
						if(sign.getLine(2).contains("§1§l所有人:"))
						{
							String owner = sign.getLine(2).split("§1§l所有人:")[1];
							if(event.getClickedBlock().getRelative(event.getBlockFace().getOppositeFace()).getType().equals(Material.DROPPER))
							{
								Dropper dispenser = (Dropper) event.getClickedBlock().getRelative(event.getBlockFace().getOppositeFace()).getState();
								int empty=0;
								for(ItemStack item:dispenser.getInventory())
								{
									if(item==null)
										empty++;
								}
								
								if(empty==9)
								{
									p.sendMessage("§6[私人抽奖箱] §c这个抽奖箱内没有物品");
									return;
								}
								
								if(point==0 && money!=0)
								{
									if(plugin.economy.has(p.getName(), money))
									{
										plugin.economy.withdrawPlayer(p.getName(), money);
										plugin.economy.depositPlayer(owner, money-(money*plugin.tax));
										dispenser.drop();
										p.sendMessage("§6[私人抽奖箱] §6已花费§5"+money+"§6金币来抽奖");
									}
									else
									{
										p.sendMessage("§6[私人抽奖箱] §c你没有足够的金币来抽奖");
									}
								}
								else if(point!=0 && money==0)
								{
									if(plugin.playerPoints.getAPI().take(p.getName(), point))
									{
										dispenser.drop();
										plugin.playerPoints.getAPI().give(owner, (int)(point-(point*plugin.tax)));
										p.sendMessage("§6[私人抽奖箱] §6已花费§5"+point+"§6点券来抽奖");
									}
									else
									{
										p.sendMessage("§6[私人抽奖箱] §c你没有足够的点券来抽奖");
									}
								}
								
							}
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
	
	@EventHandler
	public void onPlayerBreakSign(BlockBreakEvent event)
    {
		if(event.getBlock().getType().equals(Material.WALL_SIGN))
		{
			if(((Sign)event.getBlock().getState()).getLine(0).equalsIgnoreCase("§3§l[抽奖]"))
			{
				Sign sign = (Sign)event.getBlock().getState();
				if(sign.getLine(1).contains("§5§l花费点券:") || sign.getLine(1).contains("§5§l花费金币:"))
				{
					if(sign.getLine(2).contains("§1§l所有人:"))
					{
						String owner = sign.getLine(2).split("§1§l所有人:")[1];
						if(plugin.playerData.containsKey(owner))
						{
							if(plugin.playerData.get(owner).isLotteryBoxBySign(sign.getLocation()))
							{
								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
    }
	
	@EventHandler
	public void onPlayerBreakDispenser(BlockBreakEvent event)
    {
		if(event.getBlock().getType().equals(Material.DROPPER))
		{
			if(plugin.dispenser.containsKey(event.getBlock().getLocation()))
			{
				if(event.getPlayer().isOp())
				{
					String owner = plugin.dispenser.get(event.getBlock().getLocation());
					if(plugin.playerData.get(owner).delLotteryBox(event.getBlock().getLocation()))
					{
						plugin.dispenser.remove(event.getBlock().getLocation());
						event.getPlayer().sendMessage("§6[私人抽奖箱] §e抽奖箱已被强制移除");
					}
					return;
				}
				if(plugin.dispenser.get(event.getBlock().getLocation()).equalsIgnoreCase(event.getPlayer().getName()))
				{
					if(plugin.playerData.get(event.getPlayer().getName()).delLotteryBox(event.getBlock().getLocation()))
					{
						plugin.dispenser.remove(event.getBlock().getLocation());
						event.getPlayer().sendMessage("§6[私人抽奖箱] §e抽奖箱已移除");
					}
				}
				else
				{
					event.setCancelled(true);
					return;
				}
			}
		}
	}
		
    
}

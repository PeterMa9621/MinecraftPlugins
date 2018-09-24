package prefixShop;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PrefixShopListener implements Listener
{
	private PrefixShop plugin;
	public PrefixShopListener(PrefixShop plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
    {
		if(!plugin.playerData.containsKey(event.getPlayer().getName()))
		{
			ArrayList<String> myPrefix = new ArrayList<String>();
			plugin.playerData.put(event.getPlayer().getName(), myPrefix);
			plugin.playerOnUse.put(event.getPlayer().getName(), null);
		}
		return;
    }
	
	/*
	public void saveConfig(Player p)
	{
		File file=new File(plugin.getDataFolder(),"/Data/"+p.getName()+".yml");
		FileConfiguration config;
		config = plugin.load(file);
		String myPrefix = null;
		for(String pre:plugin.playerData.get(p.getName()))
		{
			if(myPrefix==null)
			{
				myPrefix = pre;
			}
			else
			{
				myPrefix = myPrefix + "%%" + pre;
			}
		}
		config.set("MyPrefix", myPrefix);
		if(plugin.playerOnUse.containsKey(p.getName()))
		{
			config.set("OnUse", plugin.playerOnUse.get(p.getName()));
		}
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	*/
	/*
	public void loadConfig(Player p)
	{
		File file=new File(plugin.getDataFolder(),"/Data/"+p.getName()+".yml");
		FileConfiguration config;
		ArrayList<String> myPrefix = new ArrayList<String>();
		if(!file.exists())
		{
			config = plugin.load(file);
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			loadConfig(p);
			return;
			
		}
		config = plugin.load(file);
		String prefix = config.getString("MyPrefix");
		String onUse = config.getString("OnUse");
		if(prefix!=null)
		{
			for(String pre:prefix.split("%%"))
			{
				myPrefix.add(pre);
			}
		}
		plugin.playerData.put(p.getName(), myPrefix);
		plugin.playerOnUse.put(p.getName(), onUse);
	}
	*/
	
	@EventHandler
	private void onPlayerClickMyPrefix(InventoryClickEvent event)
	{
		if(!event.getInventory().getTitle().equalsIgnoreCase("§5我的称号"))
		{
			return;
		}
		event.setCancelled(true);
		Player p = (Player)event.getWhoClicked();

		ArrayList<String> myPrefix = plugin.playerData.get(p.getName());
		
		if(myPrefix.isEmpty())
		{
			return;
		}
		
		if(event.getRawSlot()<myPrefix.size() && event.getRawSlot()>=0)
		{
			String onUse = plugin.playerOnUse.get(p.getName());
			if(event.getClick()==ClickType.LEFT)
			{

				if(onUse!=null)
				{
					if(onUse.equalsIgnoreCase(myPrefix.get(event.getRawSlot())))
					{
						p.sendMessage("§a[称号系统] §c你正在使用这个称号，无法切换。");
						p.closeInventory();
						return;
					}
				}
				onUse=myPrefix.get(event.getRawSlot());

				plugin.playerOnUse.put(p.getName(), onUse);
				
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manuaddv "+event.getWhoClicked().getName()+" prefix &f["+onUse+"&f]&2");
				p.closeInventory();
				p.sendMessage("§a[称号系统] §3切换称号到 §f"+onUse);
				return;
			}
			
			if(event.getClick()==ClickType.RIGHT)
			{
				if(onUse!=null)
				{
					if(onUse.equalsIgnoreCase(myPrefix.get(event.getRawSlot())))
					{
						plugin.playerOnUse.put(p.getName(), null);
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "manudelv "+event.getWhoClicked().getName()+" prefix");
					}
				}
				myPrefix.remove(myPrefix.get(event.getRawSlot()));

				
				plugin.playerData.put(p.getName(), myPrefix);

				p.openInventory(plugin.initMyGUI(p));
			}
			
		}
	}
	
	@EventHandler
	private void onPlayerClickInventory(InventoryClickEvent event)
	{
		if(!event.getInventory().getTitle().equalsIgnoreCase("§5称号商店"))
		{
			return;
		}
		event.setCancelled(true);
		if(event.getRawSlot()<plugin.prefixList.size() && event.getRawSlot()>=0)
		{
			Player p = (Player)event.getWhoClicked();

			ArrayList<String> myPrefix = plugin.playerData.get(p.getName());
			if(myPrefix.contains(plugin.prefixList.get(event.getRawSlot())))
			{
				p.sendMessage("§a[称号系统] §3你已经拥有这个称号了。");
				p.closeInventory();
				return;
			}
			//if(钱够或者点券够)then获得称号
			if(plugin.costTypeList.get(event.getRawSlot()).equalsIgnoreCase("Money"))
			{
				if(plugin.economy.has(p.getName(), plugin.priceList.get(event.getRawSlot())))
				{
					plugin.economy.withdrawPlayer(p.getName(), plugin.priceList.get(event.getRawSlot()));
					p.sendMessage("§a[称号系统] §3你支付了"+plugin.priceList.get(event.getRawSlot())+"金币");
				}
				else
				{
					p.sendMessage("§a[称号系统] §c金币数量不够");
					return;
				}
			}
			else if(plugin.costTypeList.get(event.getRawSlot()).equalsIgnoreCase("PlayerPoints"))
			{
				if(plugin.playerPoints.getAPI().take(p.getName(), plugin.priceList.get(event.getRawSlot())))
				{
					p.sendMessage("§a[称号系统] §3你支付了"+plugin.priceList.get(event.getRawSlot())+"点券");
				}
				else 
				{
					p.sendMessage("§a[称号系统] §c点券数量不够");
					return;
				}
			}
			else
			{
				return;
			}
			
			ArrayList<String> previousPrefix = plugin.playerData.get(p.getName());
			previousPrefix.add(plugin.prefixList.get(event.getRawSlot()));
			plugin.playerData.put(p.getName(), previousPrefix);

			p.openInventory(plugin.initShop(p));
			p.sendMessage("§a[称号系统] §3购买成功");
		}
	}
	
	@EventHandler
	private void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		if(plugin.definePlayer.contains(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = (Player)event.getPlayer();
			p.sendMessage("§a[称号系统] §c你当前正在自定义称号阶段，请不要输入其他指令(输入exit退出):");
			return;
		}
	}
		
	@EventHandler
	private void onPlayerChat(AsyncPlayerChatEvent event)
	{
		if(plugin.definePlayer.contains(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			
			
			if(event.getMessage().toLowerCase().equalsIgnoreCase("exit"))
			{
				plugin.definePlayer.remove(event.getPlayer().getName());
				p.sendMessage("§a[称号系统] §3已放弃自定义称号");
				return;
			}
			
			if(event.getMessage().length()>5)
			{
				p.sendMessage("§a[称号系统] §c你输入的称号大于字数限制，请重新输入(最多5个字，输入exit退出):");
				return;
			}
			
			//--------------------------------------------------------------------
			//The following codes are checking if player has already had this prefix
			ArrayList<String> myPrefix = plugin.playerData.get(p.getName());
			
			String newPrefix = null;
			if(event.getMessage().contains("&"))
			{
				newPrefix = event.getMessage().replace("&", "§");
			}
			else
			{
				newPrefix = event.getMessage();
			}
			
			if(myPrefix.contains(newPrefix))
			{
				p.sendMessage("§a[称号系统] §c你的称号库内已经有这个称号了，请重新输入");
				return;
			}
			//--------------------------------------------------------------------
			
			myPrefix.add(newPrefix);
			plugin.playerData.put(p.getName(), myPrefix);

			p.sendMessage("§a[称号系统] §3已添加称号: §f"+event.getMessage()+" §3到你的称号库");
			plugin.definePlayer.remove(event.getPlayer().getName());
		}
	}

}

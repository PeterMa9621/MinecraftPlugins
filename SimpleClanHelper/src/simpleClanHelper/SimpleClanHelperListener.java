package simpleClanHelper;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.sacredlabyrinth.phaed.simpleclans.Clan;

public class SimpleClanHelperListener implements Listener
{
	private SimpleClanHelper plugin;
	
	char[] colorSignal = {'a','b','c','d','e','f','1','2','3','4','5','6','7','8','9'};

	public SimpleClanHelperListener(SimpleClanHelper plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerClickGUI(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase(plugin.msg.get("GuiTitle")))
		{
			event.setCancelled(true);
			Player p = (Player)event.getWhoClicked();
			if(event.getRawSlot()<0 || event.getRawSlot()>35)
				return;
			if(event.getInventory().getItem(event.getRawSlot())==null)
				return;
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("create.Name")))
			{
				p.performCommand("clan create");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("invite.Name")))
			{
				p.performCommand("clan invite");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("list.Name")))
			{
				p.performCommand("clan list");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("profile.Name")))
			{
				p.performCommand("clan profile");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("lookup.Name")))
			{
				p.performCommand("clan lookup");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("leaderboard.Name")))
			{
				p.performCommand("clan leaderboard");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("alliances.Name")))
			{
				p.performCommand("clan alliances");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("rivalries.Name")))
			{
				p.performCommand("clan rivalries");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("roster.Name")))
			{
				p.performCommand("clan roster");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("vitals.Name")))
			{
				p.performCommand("clan vitals");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("coords.Name")))
			{
				p.performCommand("clan coords");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("stats.Name")))
			{
				p.performCommand("clan stats");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("kills.Name")))
			{
				p.performCommand("clan kills");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("ally.Name")))
			{
				p.performCommand("clan ally");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("rival.Name")))
			{
				p.performCommand("clan rival");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("home.Name")))
			{
				p.performCommand("clan home");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("homeset.Name")))
			{
				p.performCommand("clan home set");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("war.Name")))
			{
				p.performCommand("clan war");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("bb.Name")))
			{
				p.performCommand("clan bb");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("modtag.Name")))
			{
				p.performCommand("clan modtag");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("toggle.Name")))
			{
				p.performCommand("clan toggle");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("invite.Name")))
			{
				p.performCommand("clan invite");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("kick.Name")))
			{
				p.performCommand("clan kick");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("setrank.Name")))
			{
				p.performCommand("clan setrank");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("trust.Name")))
			{
				p.performCommand("clan trust");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("untrust.Name")))
			{
				p.performCommand("clan untrust");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("promote.Name")))
			{
				p.performCommand("clan promote");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("demote.Name")))
			{
				p.performCommand("clan demote");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("clanff.Name")))
			{
				p.performCommand("clan clanff");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("disband.Name")))
			{
				p.performCommand("clan disband");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("ff.Name")))
			{
				p.performCommand("clan ff");
				p.closeInventory();
				return;
			}
			if(event.getInventory().getItem(event.getRawSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(plugin.msg.get("resign.Name")))
			{
				p.performCommand("clan resign");
				p.closeInventory();
				return;
			}
			if(event.getRawSlot()==35)
			{
				plugin.core.getPermissionsManager().updateClanPermissions(plugin.getClanManager().getClanByPlayerName(p.getName()));
				p.closeInventory();
				return;
			}
			if(event.getRawSlot()==0)
			{
				
				if(plugin.getClanManager().getClanByPlayerName(p.getName())==null)
				{
					p.closeInventory();
					p.sendMessage(plugin.prefix + plugin.msg.get("NoClan"));
					return;
				}
				
				if(!plugin.getClanManager().getClanPlayer(p).isLeader())
				{
					p.closeInventory();
					p.sendMessage(plugin.prefix + plugin.msg.get("NotLeader"));
					return;
				}
				
				if(plugin.payMoney(p))
				{
					plugin.changeTag.add(p.getName());
					p.closeInventory();
					p.sendMessage(plugin.prefix + "§e请输入要修改的标签名称(§5可以使用彩色代码,§e最少"+plugin.minLength+"个字,最多"+plugin.maxLength+"个字):");
					return;
				}
				else
				{
					p.closeInventory();
					p.sendMessage(plugin.prefix + "§c你没有足够的金钱");
					return;
				}
			}
			if(event.getRawSlot()==31)
			{
				p.closeInventory();
				p.sendMessage("§e======================================");
				for(Clan clan:plugin.core.getClanManager().getClans())
				{
					p.sendMessage("§b中文标签:§f"+clan.getColorTag()+"§b,英文标签:§f"+clan.getTag());
				}
				p.sendMessage("§e======================================");
				return;
			}
		}
	}
	
	public boolean isExist(char a, char[] list)
	{
		for(char everyChar:list)
		{
			if(everyChar==a)
				return true;
		}
		return false;
	}
	
	@EventHandler
	private void onPlayerChat(AsyncPlayerChatEvent event)
	{ 
		if(plugin.changeTag.contains(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			int colorLength = 0;

			for(int i=0; i<event.getMessage().length(); i++)
			{
				if(event.getMessage().charAt(i)=='§')
				{
					if(isExist(event.getMessage().charAt(i+1), colorSignal))
					{
						colorLength += 2;
						i++;
					}
				}
			}

			if(event.getMessage().length()<(plugin.minLength + colorLength) || 
					event.getMessage().length()>(plugin.maxLength + colorLength))
			{
				p.sendMessage(plugin.prefix + "§e标签长度不符合要求(§5可以使用彩色代码,§e最少"+plugin.minLength+"个字,最多"+plugin.maxLength+"个字):");
			}
			else
			{
				plugin.getClanManager().getClanByPlayerName(p.getName()).changeClanTag(event.getMessage());
				plugin.changeTag.remove(p.getName());
				p.sendMessage(plugin.prefix + plugin.msg.get("TagChangedTo")+event.getMessage());
			}
		}
		
	}

}

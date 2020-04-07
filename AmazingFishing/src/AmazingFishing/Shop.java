package AmazingFishing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import AmazingFishing.Quests.Actions;
import AmazingFishing.help.Type;
import me.Straiker123.GUICreatorAPI;
import me.Straiker123.GUICreatorAPI.Options;
import me.Straiker123.ItemCreatorAPI;
import me.Straiker123.ItemGUI;
import me.Straiker123.TheAPI;
import me.Straiker123.TheAPI.SudoType;

public class Shop {
	public Shop(CommandSender s) {
		if(s instanceof Player) {
			openShop((Player)s, ShopType.Buy);
			return;
		}
		Loader.msgCmd(Loader.s("ConsoleErrorMessage"), s);
		return;
	}
	public static enum ShopType {
		Buy,
		Sell
	}
	
	private static ItemGUI c(Player p, String item, Runnable r) {
		ItemCreatorAPI a= new ItemCreatorAPI(new ItemStack(Material.matchMaterial(Loader.shop.getString("GUI."+item+".Icon").toUpperCase())));
		a.setDisplayName(Loader.shop.getString("GUI."+item+".Name").replace("%player%", p.getName()).replace("%playername%", p.getDisplayName()).replace("%points%", Points.getBal(p.getName())));
		List<String> lore =new ArrayList<String>();
		for(String s : Loader.shop.getStringList("GUI."+item+".Lore")) {
			lore.add(s.replace("%player%", p.getName()).replace("%playername%", p.getDisplayName()).replace("%points%", Points.getBal(p.getName())));
		}
		if(Loader.shop.getString("GUI."+item+".ModelData")!=null)
		a.setCustomModelData(Loader.shop.getInt("GUI."+item+".ModelData"));
		a.setLore(lore);
		ItemGUI d = new ItemGUI(a.create());
		d.addOption(Options.CANT_BE_TAKEN, true);
		if(r!=null)
		d.addOption(Options.RUNNABLE, r);
		return d;
	}
	
	public static void openShop(Player p, ShopType t) {
		String shop = Trans.shoplog();
		if(t==ShopType.Sell)shop="&6"+Trans.shoplogsell();
		GUICreatorAPI a = TheAPI.getGUICreatorAPI(p);
		a.setTitle(shop);
		a.setSize(54);
		Create.prepareInv(a);
		HashMap<Options, Object> w = new HashMap<Options, Object>();
		w.put(Options.CANT_BE_TAKEN, true);
		a.applyItemGUI(c(p,"Points",null), 4);
		if(t==ShopType.Buy) {
		if(Loader.c.getBoolean("Options.ShopSellFish"))
			a.applyItemGUI(c(p,"SellShop",new Runnable() {
				@Override
				public void run() {
					openShop(p, ShopType.Sell);
				}}), 45);
		addItems(a);
		}else {
			w.remove(Options.RUNNABLE);
			List<String> s = new ArrayList<String>();
			for(String d : Loader.c.getStringList("Options.Manual.FishOfDay"))
				s.add(d.replace("%fish_name%", Loader.c.getString("Types."+Loader.f.getType()+"."+Loader.f.getFish()+".Name")).replace("%fish%", Loader.f.getFish()).replace("%bonus%", ""+Loader.f.getBonus()));
			a.setItem(35,Create.createItem(Trans.fishday(),Loader.f.getMaterial(), s),w);
			a.applyItemGUI(c(p,"BuyShop",new Runnable() {
				@Override
				public void run() {
					openShop(p, ShopType.Buy);
				}}), 45);
			a.applyItemGUI(c(p,"Bag",new Runnable() {
				@Override
				public void run() {
					bag.openBag(p);
				}}), 26);
			a.applyItemGUI(c(p,"Sell",new Runnable() {
				@Override
				public void run() {
					sellAll(p, p.getOpenInventory().getTopInventory(), true, false);
				}}), 49);
		}
		if(Loader.c.getBoolean("Options.UseGUI")) {
		w.remove(Options.RUNNABLE);
		w.put(Options.RUNNABLE, new Runnable() {
			@Override
			public void run() {
				help.open(p, Type.Player);
			}});
		a.setItem(53,Create.createItem(Trans.back(), Material.BARRIER),w);
		}
		a.open();
	}
	
	private static boolean ex(String ss) {
		return Loader.shop.getString(ss)!=null;
	}
	private  static void addItems(GUICreatorAPI inv) {
		try {
		if(Loader.shop.getString("Items")!=null)
		for(String item:Loader.shop.getConfigurationSection("Items").getKeys(false)) {
		int cost = Loader.shop.getInt("Items."+item+".Cost");
				String ItemName=item;
				if(ex("Items."+item+".Name"))
					ItemName=Loader.shop.getString("Items."+item+".Name").replace("%item%", item).replace("%cost%", cost+"");
				Material icon = Material.matchMaterial(Loader.shop.getString("Items."+item+".Icon").toUpperCase());
				if(icon==null)icon=Material.STONE;
				List<String> lore= new ArrayList<String>();
					if(ex("Items."+item+".Description") && Loader.shop.getStringList("Items."+item+".Description").isEmpty()==false)
					for(String ss:Loader.shop.getStringList("Items."+item+".Description"))lore.add(ss.replace("%item%", item).replace("%cost%", cost+""));
					HashMap<Options, Object> w = new HashMap<Options, Object>();
					w.put(Options.CANT_BE_TAKEN, true);
						w.put(Options.RUNNABLE, new Runnable() {
							public void run() {
								giveItem(inv.getPlayer(), item);
							}});
						ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(icon));
						a.setDisplayName(ItemName);
						a.setLore(lore);
						if(Loader.shop.getString("Items."+item+".ModelData")!=null)
						a.setCustomModelData(Loader.shop.getInt("Items."+item+".ModelData"));
					inv.addItem(a.create(),w);
		}
		}catch(Exception e) {
			Bukkit.getLogger().severe("Error when adding items to Amazing Fishing Shop");	
			}
		}
	public static void giveItem(Player p,String kit) {
			int cost = Loader.shop.getInt("Items."+kit+".Cost");
			if(Points.has(p.getName(), cost)) {

				if(Loader.c.getBoolean("Options.Sounds.Shop-BuyItem"))
					Sounds.play(p);
				Points.take(p.getName(), cost);
				List<String> cmds=null;
				if(ex("Items."+kit+".Commands"))
					cmds=Loader.shop.getStringList("Items."+kit+".Commands");
				if(cmds != null)
					for(String f:cmds) {
						TheAPI.sudoConsole(SudoType.COMMAND, Color.c(f.replace("%player%", p.getName()).replace("%item%", kit).replace("%cost%", cost+"")));
					}
				
				List<String> msgs=null;
				if(ex("Items."+kit+".Messages"))
					msgs=Loader.shop.getStringList("Items."+kit+".Messages");
				
				if(msgs != null)
					for(String f:msgs) {
						TheAPI.getPlayerAPI(p).msg(f.replace("%player%", p.getName()).replace("%item%", kit).replace("%cost%", cost+""));
					}
				if(ex("Items."+kit+".Item")) {
					for(String f:Loader.shop.getConfigurationSection("Items."+kit+".Item").getKeys(false)) {
						try {
							ItemCreatorAPI a = TheAPI.getItemCreatorAPI(Material.matchMaterial(Loader.shop.getString("Items."+kit+".Item."+f+".Material")));
							int amount = 1;
							if(Loader.shop.getInt("Items."+kit+".Item."+f+".Amount")>0)
								amount=Loader.shop.getInt("Items."+kit+".Item."+f+".Amount");
						a.setAmount(amount);
						a.setDisplayName(Loader.shop.getString("Items."+kit+".Item."+f+".Name").replace("%player%", p.getName()).replace("%item%", kit).replace("%cost%", cost+""));
						List<String> lore = new ArrayList<String>();
						for(String w:Loader.shop.getStringList("Items."+kit+".Item."+f+".Lore"))lore.add(w.replace("%item%", kit).replace("%player%", p.getName()).replace("%cost%", cost+""));
						a.setLore(lore);
						if(Loader.shop.getString("Items."+kit+".Item."+f+".ModelData")!=null)
						a.setCustomModelData(Loader.shop.getInt("Items."+kit+".Item."+f+".ModelData"));
						a.setUnbreakable(Loader.shop.getBoolean("Items."+kit+".Item."+f+".Unbreakable"));
						if(Loader.shop.getBoolean("Items."+kit+".Item."+f+".HideEnchants"))
							a.addItemFlag(ItemFlag.HIDE_ENCHANTS);
						if(Loader.shop.getBoolean("Items."+kit+".Item."+f+".HideAttributes"))
							a.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
						HashMap<Enchantment, Integer> enchs = new HashMap<Enchantment, Integer>();
						if(Loader.shop.getString("Items."+kit+".Item."+f+".Enchants")!=null)
						for(String s:Loader.shop.getStringList("Items."+kit+".Item."+f+".Enchants")) {
			            	String ench = s.replace(":", "").replace(" ", "").replaceAll("[0-9]+", "");
			            	int num = TheAPI.getStringUtils().getInt(s.replace(":", "").replace(" ", "").replace("_", ""));
			            	if(num==0)num=1;
			            	try {
						enchs.put(TheAPI.getEnchantmentAPI().getByName(ench), num);
			            	}catch(Exception e) {
			            		
			            	}
						}
						TheAPI.giveItem(p,a.create());
					}catch(Exception e) {
					Bukkit.getLogger().warning("Error when giving item from AmazingFishing Shop to player "+p.getName()+", ShopItem: "+kit+", Item: "+f);	
					}}}}}

	public static void sellAll(Player p, Inventory i, boolean sell, boolean expand) {
		ArrayList<ItemStack> a = new ArrayList<ItemStack>();
		if(!expand) {

			for(int count =10; count < 17; ++count) {
				a.add(i.getItem(count));
				}
			for(int count =19; count < 26; ++count) {
				a.add(i.getItem(count));
				}
			for(int count =28; count < 34; ++count) {
				a.add(i.getItem(count));
				}
			for(int count =37; count < 44; ++count) {
				a.add(i.getItem(count));
				}
		}else {
			for(int count = 0; count < 45; ++count) {
				a.add(i.getItem(count));
				}
		}
		double sold = 0.0;
		int amount = 0;
		int exp =0;
		double points = 0.0;
		int sel = 0;
		for(ItemStack d:a) {
			if(d==null)continue;
			if(sell) {
			Material m = d.getType();
			String w = d.getItemMeta().getDisplayName();
			
			String path = null;
			String type = null;
			if(m==Material.SALMON)type="Salmon";
			if(m==Material.PUFFERFISH)type="PufferFish";
			if(m==Material.TROPICAL_FISH)type="TropicalFish";
			if(m==Material.COD)type="Cod";
			if(d.getItemMeta().hasDisplayName()) {
			path="Types."+type;
			String fish = null;
			if(Loader.c.getString(path)!=null)
			for(String s:Loader.c.getConfigurationSection(path).getKeys(false)) {
				if(Loader.c.getString(path+"."+s+".Name")!=null) {
					if(w.equalsIgnoreCase(Color.c(Loader.c.getString(path+"."+s+".Name"))))fish=s;
				}
			}
			if(fish!=null) {
			path=path+"."+fish;
			int bonus=1;
			if(Loader.f.getFish().equals(fish) && Loader.f.getType().equals(type))
				bonus=2;
			
			sel=sel+d.getAmount();
			amount=amount+d.getAmount();
			sold=sold+(((Loader.cc.getConfig().getBoolean("Options.DisableMoneyFromCaught") ? Loader.c.getDouble(path+".Money") : (Loader.cc.getConfig().getBoolean("Options.ShopGiveFullPriceFish")? Loader.c.getDouble(path+".Money") : Loader.c.getDouble(path+".Money")/4))*d.getAmount())*bonus);
			points=points+(((Loader.cc.getConfig().getBoolean("Options.DisableMoneyFromCaught") ? Loader.c.getDouble(path+".Points") : (Loader.cc.getConfig().getBoolean("Options.ShopGiveFullPriceFish")? Loader.c.getDouble(path+".Points") : Loader.c.getDouble(path+".Points")/2))*d.getAmount())*bonus);
			exp=exp+(int)(((Loader.cc.getConfig().getBoolean("Options.DisableMoneyFromCaught") ? Loader.c.getDouble(path+".Xp") : (Loader.cc.getConfig().getBoolean("Options.ShopGiveFullPriceFish")? Loader.c.getDouble(path+".Xp") : Loader.c.getDouble(path+".Xp")/2))*d.getAmount())*bonus);
			Quests.addProgress(p,path,fish,Actions.SELL_FISH);
			i.remove(d);
		}else {
			TheAPI.giveItem(p, d);
			i.remove(d);
		}}else {
				Material fish = null;
				if(m==Material.SALMON || m==Material.COD || m==Material.TROPICAL_FISH||m==Material.PUFFERFISH)fish=Material.STONE;
				
				if(fish!=null) {
					sel=sel+d.getAmount();
					amount=amount+d.getAmount();
					sold=sold+(0.1*d.getAmount());
					points=points+(0.1*d.getAmount());
					exp=exp+(1*d.getAmount());
				i.remove(d);
			}else{
				TheAPI.giveItem(p, d);
				i.remove(d);
				}
			}
			}else {
				TheAPI.giveItem(p, d);
				i.remove(d);
			}
		}
		if(sell && sel != 0) {
			if(!expand) {
			if(Loader.c.getBoolean("Options.Sounds.Shop-SellFishes"))
				Sounds.play(p);
			}else {
				if(Loader.c.getBoolean("Options.Sounds.Bag-SellFishes"))
					Sounds.play(p);
			}
			TheAPI.getEconomyAPI().depositPlayer(p.getName(), sold);
			Points.give(p.getName(), points);
			p.giveExp(exp);
			
			
		Loader.msgCmd(Loader.s("Prefix")+Loader.s("SoldFishes")
		.replace("%amount%", sel+"")
		.replace("%exp%", exp+"")
				.replace("%money%", String.format("%2.02f",sold).replace(",", ".")+"")
				.replace("%points%", String.format("%2.02f",points).replace(",", ".")+""), p);
	}}}

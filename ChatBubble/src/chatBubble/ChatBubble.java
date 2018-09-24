package chatBubble;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChatBubble extends JavaPlugin
{
	HashMap<String, String> playerData = new HashMap<String, String>();
	
	public void onEnable()
	{
	    if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"))
	    {
	    	Bukkit.getConsoleSender().sendMessage("��a[ChatBubble] ��cHolographicDisplaysδ����");
	    	setEnabled(false);
	    	return;
	    }
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		getServer().getPluginManager().registerEvents(new ChatBubbleListener(this), this);
		Bukkit.getConsoleSender().sendMessage("��a[ChatBubble] ��e�������ݼ������");
	}

	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("��a[ChatBubble] ��e��������ж�����");
	}

	public FileConfiguration load(File file)
	{
        if (!(file.exists())) 
        { //�����ļ�������
        	try   //��׽�쳣����Ϊ�п��ܴ������ɹ�
        	{
        		file.createNewFile();
        	}
        	catch(IOException e)
        	{
        		e.printStackTrace();
        	}
        }
        return YamlConfiguration.loadConfiguration(file);
	}
	public FileConfiguration load(String path)
	{
		File file=new File(path);
		if(!file.exists())
		{
			try
		{
				file.createNewFile();
		}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return YamlConfiguration.loadConfiguration(new File(path));
	}
	
	public Inventory openGui(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 18, "��1�ġ�2���3����4�ݡ�5�ա�6ɫ��7�С�8��");
		

		ItemStack color = createItem(35, 1, 11, "��1����л�����ɫ");
		inv.setItem(0, color);

		color = createItem(35, 1, 13, "��2����л�����ɫ");
		inv.setItem(1, color);

		color = createItem(35, 1, 9, "��3����л���ɫ");
		inv.setItem(2, color);

		color = createItem(35, 1, 14, "��4����л����ɫ");
		inv.setItem(3, color);
		
		color = createItem(35, 1, 10, "��5����л���ɫ");
		inv.setItem(4, color);
		
		color = createItem(35, 1, 1, "��6����л���ɫ");
		inv.setItem(5, color);
		
		color = createItem(35, 1, 8, "��7����л���ɫ");
		inv.setItem(6, color);
		
		color = createItem(35, 1, 7, "��8����л���ɫ");
		inv.setItem(7, color);
		
		color = createItem(159, 1, 11, "��9����л�����ɫ");
		inv.setItem(8, color);
		
		color = createItem(35, 1, 5, "��a����л�����ɫ");
		inv.setItem(9, color);
		
		color = createItem(35, 1, 3, "��b����л�����ɫ");
		inv.setItem(10, color);
		
		color = createItem(159, 1, 14, "��c����л�����ɫ");
		inv.setItem(11, color);
		
		color = createItem(35, 1, 6, "��d����л��ۺ�ɫ");
		inv.setItem(12, color);
		
		color = createItem(159, 1, 4, "��e����л���ɫ");
		inv.setItem(13, color);
		
		color = createItem(35, 1, 0, "��f����л���ɫ");
		inv.setItem(14, color);

		return inv;
	}
	
	public ItemStack createItem(int ID, int quantity, int durability, String displayName)
	{
		ItemStack item = new ItemStack(ID, quantity, (short)durability);

		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("cb"))
		{
			if (args.length==0)
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(p.hasPermission("chatBubble.color"))
					{
						p.openInventory(openGui(p));
					}
					else
					{
						p.sendMessage("��6[��������] ��c��û��Ȩ����ô��!");
					}
				}
				return true;
			}
			return true;
		}

		return false;
		
	}
	
	
}


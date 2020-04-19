package openVirtualBook;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class OpenVirtualBook extends JavaPlugin
{
	HashMap<String, ItemStack> books = new HashMap<>();
	
	boolean set = false;
	
	int x = 0;
	int y = 0;
	int z = 0;
	String worldName = "";
	
	public void onEnable() 
	{
		if(!getDataFolder().exists()) 
		{
			getDataFolder().mkdir();
		}
		loadConfig();
		initBooks();
		getServer().getPluginManager().registerEvents(new OpenVirtualBookListener(this), this);
		Bukkit.getConsoleSender().sendMessage("§a[OpenVirtualBook] §e虚拟书籍加载完毕");
	}

	public void onDisable() 
	{
		saveConfig();
		Bukkit.getConsoleSender().sendMessage("§a[OpenVirtualBook] §e虚拟书籍卸载完毕");
	}
	
	public void loadConfig() 
	{
		File file=new File(getDataFolder(),"data.yml");
		FileConfiguration config;
		
		if(!file.exists())
		{
			Bukkit.getConsoleSender().sendMessage("§a[OpenVirtualBook] §c虚拟书籍存储箱子未设置！");
			worldName = "null";
			
			return;
		}
		
		config = load(file);
		x = config.getInt("Chest.X");
		y = config.getInt("Chest.Y");
		z = config.getInt("Chest.Z");
		worldName = config.getString("Chest.World");
	}
	
	public void saveConfig() 
	{
		File file=new File(getDataFolder(),"data.yml");
		FileConfiguration config;
		
		config = load(file);
		
		config.set("Chest.X", x);
		config.set("Chest.Y", y);
		config.set("Chest.Z", z);
		config.set("Chest.World", worldName);

		try 
		{
			config.save(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}
	
	public void initBooks()
	{
		if(worldName.equalsIgnoreCase("null"))
			return;
		Block chestBlock = Bukkit.getWorld(worldName).getBlockAt(x, y, z);
		
		books = getBooks(chestBlock);
	}
	
	public HashMap<String, ItemStack> getBooks(Block chestBlock)
	{
		Chest chest = (Chest)chestBlock.getState();
		
		Inventory inventory = chest.getInventory();

		return new HashMap<String, ItemStack>() {{
			for(ItemStack book:inventory.getContents())
			{
				if(book!=null)
				{
					if(book.getType().equals(Material.WRITTEN_BOOK))
					{
						BookMeta bookMeta = (BookMeta) book.getItemMeta();
						put(bookMeta.getTitle(), book);
					}
				}
			}
		}};
	}
	
	public FileConfiguration load(File file)
	{
        if (!(file.exists())) 
        { //假如文件不存在
        	try   //捕捉异常，因为有可能创建不成功
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
	
	public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args)  
	{
		if (cmd.getName().equalsIgnoreCase("ob"))
		{
			if(args.length==0)
			{
				if(sender.isOp())
				{
					sender.sendMessage("§9/ob open [书名] §6- 打开一本虚拟的成书");
					sender.sendMessage("§9/ob set §6- 设置一个存放成书的箱子");
					sender.sendMessage("§9/ob list §6- 查看所有书籍列表");
				}
				else
				{
					sender.sendMessage("§f未知的命令. 输入 \"help\" 查看帮助.");
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("list"))
			{
				if(sender.isOp())
				{
					initBooks();
					sender.sendMessage("§6==================");
					sender.sendMessage("§6编号      书名");
					if(!books.isEmpty())
					{
						int index = 1;
						for(ItemStack book:books.values())
						{
							BookMeta meta = (BookMeta)book.getItemMeta();
							String name = meta.getTitle();

							String result = String.format(" §e%2d  %8s", index,name);
							sender.sendMessage(result);

							index++;
						}
					}
					else
						sender.sendMessage("§a  目前没有书籍");
					sender.sendMessage("§6==================");

				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("set"))
			{
				if(sender.isOp())
				{
					if(sender instanceof Player)
					{
						Player p = (Player)sender;
						set = true;
						p.sendMessage("§6[虚拟书籍] §a请左键或右键一个箱子(确保这个箱子的安全)");
					}
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("open"))
			{
				if(sender instanceof Player)
				{
					Player p = (Player)sender;
					if(args.length==2)
					{
						if(books.containsKey(args[1]))
						{
							p.openBook(books.get(args[1]));
						}
						else
						{
							if(p.isOp())
								p.sendMessage("§6[虚拟书籍] §c书名错误!");
						}
					}
					else
					{
						if(p.isOp())
							p.sendMessage("§9/ob open [书名] §6- 打开一本虚拟的成书");
					}
				}
				return true;
			}
			return true;
		}
		return false;
		
	}
	
	
}


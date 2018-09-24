package privateFight;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class PrivateFightSetLocationListener implements Listener
{
	private PrivateFight plugin;

	public PrivateFightSetLocationListener(PrivateFight plugin)
	{
		this.plugin=plugin;
	}
	
	
	/**
	 *  Used to set Ready Sign.
	 * @param event
	 */
	@EventHandler
	private void onPlayerClickSign(PlayerInteractEvent event)
	{
		if(plugin.setLocationPlayer7.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			
			if(event.getClickedBlock().getType().equals(Material.WALL_SIGN))
			{
				String locationName = plugin.setLocationPlayer7.get(p.getName());
				
				HashMap<String, Location> allLocation = setSign(event, p, plugin.location.get(locationName), "player2Ready", locationName);

				plugin.location.put(locationName, allLocation);
				
				p.sendMessage("��a[����ϵͳ] ��3���2��׼������������");
				p.sendMessage("��a[����ϵͳ] ��3�������ء�c"+locationName+"��3���������");
				plugin.setLocationPlayer7.remove(p.getName());
			}
			
			else
			{
				p.sendMessage("��a[����ϵͳ] ��c������岻��һ������");
				p.sendMessage("��a[����ϵͳ] ��6���������2��׼������(���Ҽ�Ŀ������,����exit�˳�)");
				return;
			}
		}
		else if(plugin.setLocationPlayer6.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			
			if(event.getClickedBlock().getType().equals(Material.WALL_SIGN))
			{
				String locationName = plugin.setLocationPlayer6.get(p.getName());
				
				HashMap<String, Location> allLocation = setSign(event, p, plugin.location.get(locationName), "player1Ready", locationName);
				
				plugin.location.put(locationName, allLocation);
				
				plugin.setLocationPlayer7.put(p.getName(), locationName);
				p.sendMessage("��a[����ϵͳ] ��3���1��׼������������");
				p.sendMessage("��a[����ϵͳ] ��6���������2��׼������(���Ҽ�Ŀ������,����exit�˳�)");
				//p.sendMessage("��a[����ϵͳ] ��3�������ء�c"+plugin.setLocationPlayer3.get(p.getName())+"��3���������");
				plugin.setLocationPlayer6.remove(p.getName());
			}
			
			else
			{
				p.sendMessage("��a[����ϵͳ] ��c������岻��һ������");
				p.sendMessage("��a[����ϵͳ] ��6���������1��׼������(���Ҽ�Ŀ������,����exit�˳�)");
				return;
			}
		}
		
	}
	
	/**
	 *  Used to set players' lobby and fight location.
	 * @param event
	 */
	@EventHandler
	private void onPlayerChat(AsyncPlayerChatEvent event)
	{
		if(plugin.setLocationPlayer7.containsKey(event.getPlayer().getName()))
		{
			if(event.getMessage().equalsIgnoreCase("exit"))
			{
				event.setCancelled(true);
				Player p = event.getPlayer();
				plugin.location.remove(plugin.setLocationPlayer7.get(p.getName()));
				plugin.setLocationPlayer7.remove(p.getName());
				p.sendMessage("��a[����ϵͳ] ��c�ѽ�����ӳ�����Ϣ");
				return;
			}
		}
		else if(plugin.setLocationPlayer6.containsKey(event.getPlayer().getName()))
		{
			if(event.getMessage().equalsIgnoreCase("exit"))
			{
				event.setCancelled(true);
				Player p = event.getPlayer();
				plugin.location.remove(plugin.setLocationPlayer6.get(p.getName()));
				plugin.setLocationPlayer6.remove(p.getName());
				p.sendMessage("��a[����ϵͳ] ��c�ѽ�����ӳ�����Ϣ");
				return;
			}
		}
		else if(plugin.setLocationPlayer5.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			if(event.getMessage().equalsIgnoreCase("exit"))
			{
				plugin.location.remove(plugin.setLocationPlayer5.get(p.getName()));
				plugin.setLocationPlayer5.remove(p.getName());
				p.sendMessage("��a[����ϵͳ] ��c�ѽ�����ӳ�����Ϣ");
				return;
			}
			
			else if(event.getMessage().equalsIgnoreCase("add"))
			{
				String locationName = plugin.setLocationPlayer5.get(p.getName());
				
				HashMap<String, Location> allLocation = setLocation(event, p, plugin.location.get(locationName), "player2Fight");

				plugin.location.put(locationName, allLocation);
				
				plugin.setLocationPlayer6.put(p.getName(), locationName);
				p.sendMessage("��a[����ϵͳ] ��3���2ս��������������");
				p.sendMessage("��a[����ϵͳ] ��6���������1��׼������(���Ҽ�Ŀ������,����exit�˳�)");
				//p.sendMessage("��a[����ϵͳ] ��3�������ء�c"+plugin.setLocationPlayer3.get(p.getName())+"��3���������");
				plugin.setLocationPlayer5.remove(p.getName());
			}
			
			else
			{
				p.sendMessage("��a[����ϵͳ] ��6���������2ս��������,����add���(����exit�˳�)");
			}
		}
		else if(plugin.setLocationPlayer4.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			if(event.getMessage().equalsIgnoreCase("exit"))
			{
				plugin.location.remove(plugin.setLocationPlayer4.get(p.getName()));
				plugin.setLocationPlayer4.remove(p.getName());
				p.sendMessage("��a[����ϵͳ] ��c�ѽ�����ӳ�����Ϣ");
				return;
			}
			
			else if(event.getMessage().equalsIgnoreCase("add"))
			{
				String locationName = plugin.setLocationPlayer4.get(p.getName());
				HashMap<String, Location> allLocation = setLocation(event, p, plugin.location.get(locationName), "player1Fight");

				plugin.location.put(locationName, allLocation);
				
				plugin.setLocationPlayer5.put(p.getName(), locationName);
				p.sendMessage("��a[����ϵͳ] ��3���1ս��������������");
				p.sendMessage("��a[����ϵͳ] ��6���������2ս��������,����add���(����exit�˳�)");
				//p.sendMessage("��a[����ϵͳ] ��3�������ء�c"+plugin.setLocationPlayer3.get(p.getName())+"��3���������");
				plugin.setLocationPlayer4.remove(p.getName());
			}
			
			else
			{
				p.sendMessage("��a[����ϵͳ] ��6���������1ս��������,����add���(����exit�˳�)");
			}
		}
		else if(plugin.setLocationPlayer3.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			if(event.getMessage().equalsIgnoreCase("exit"))
			{
				plugin.location.remove(plugin.setLocationPlayer3.get(p.getName()));
				plugin.setLocationPlayer3.remove(p.getName());
				p.sendMessage("��a[����ϵͳ] ��c�ѽ�����ӳ�����Ϣ");
				return;
			}
			
			else if(event.getMessage().equalsIgnoreCase("add"))
			{
				String locationName = plugin.setLocationPlayer3.get(p.getName());
				HashMap<String, Location> allLocation = setLocation(event, p, plugin.location.get(locationName), "audienceLobby");

				plugin.location.put(locationName, allLocation);
				
				plugin.setLocationPlayer4.put(p.getName(), locationName);
				p.sendMessage("��a[����ϵͳ] ��3���ڳ���������");
				p.sendMessage("��a[����ϵͳ] ��6���������1ս��������,����add���(����exit�˳�):");
				//p.sendMessage("��a[����ϵͳ] ��3�������ء�c"+plugin.setLocationPlayer3.get(p.getName())+"��3���������");
				plugin.setLocationPlayer3.remove(p.getName());
			}
			
			else
			{
				p.sendMessage("��a[����ϵͳ] ��6��ǰ�����2�Ĵ��ͳ���,������add���(����exit�˳�)");
			}
			
		}
		else if(plugin.setLocationPlayer2.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			if(event.getMessage().equalsIgnoreCase("exit"))
			{
				plugin.location.remove(plugin.setLocationPlayer2.get(p.getName()));
				plugin.setLocationPlayer2.remove(p.getName());
				p.sendMessage("��a[����ϵͳ] ��c�ѽ�����ӳ�����Ϣ");
				return;
			}
			
			else if(event.getMessage().equalsIgnoreCase("add"))
			{
				String locationName = plugin.setLocationPlayer2.get(p.getName());
				HashMap<String, Location> allLocation = setLocation(event, p, plugin.location.get(locationName), "player2Lobby");

				plugin.location.put(locationName, allLocation);
				
				plugin.setLocationPlayer3.put(p.getName(), locationName);
				plugin.setLocationPlayer2.remove(p.getName());
				p.sendMessage("��a[����ϵͳ] ��3����2������");
				p.sendMessage("��a[����ϵͳ] ��6��ǰ�����ڵĴ��ͳ���,������add���(����exit�˳�)");
				
			}
			
			else
			{
				p.sendMessage("��a[����ϵͳ] ��6��ǰ�����2�Ĵ��ͳ���,������add���(����exit�˳�)");
			}
			
		}
		else if(plugin.setLocationPlayer1.containsKey(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			Player p = event.getPlayer();
			if(event.getMessage().equalsIgnoreCase("exit"))
			{
				plugin.location.remove(plugin.setLocationPlayer1.get(p.getName()));
				plugin.setLocationPlayer1.remove(p.getName());
				p.sendMessage("��a[����ϵͳ] ��c�ѽ�����ӳ�����Ϣ");
				return;
			}
			
			else if(event.getMessage().equalsIgnoreCase("add"))
			{
				String locationName = plugin.setLocationPlayer1.get(p.getName());
				HashMap<String, Location> allLocation = setLocation(event, p, "player1Lobby");

				plugin.location.put(locationName, allLocation);

				p.sendMessage("��a[����ϵͳ] ��3����1������");
				plugin.setLocationPlayer2.put(p.getName(), locationName);
				plugin.setLocationPlayer1.remove(p.getName());
				p.sendMessage("��a[����ϵͳ] ��6��ǰ�����2�Ĵ��ͳ���,������add���(����exit�˳�)");
			}
			
			else
			{
				p.sendMessage("��a[����ϵͳ] ��6��ǰ�����1�Ĵ��ͳ���,������add���(����exit�˳�)");
			}
			
		}
	}
	
	//========================================================================
	//=================================Method=================================
	//========================================================================
	
	public HashMap<String, Location> setSign(PlayerInteractEvent event, Player p, 
			HashMap<String, Location> previousLocation, String locationType, String locationName)
	{
		double x = event.getClickedBlock().getX();
		double y = event.getClickedBlock().getY();
		double z = event.getClickedBlock().getZ();

		Location l = new Location(p.getWorld(), x, y, z);
		
		Sign sign = (Sign)event.getClickedBlock().getState();
		sign.setLine(0, "��a["+locationType+"]");
		sign.setLine(1, "��3"+locationName);
		sign.setLine(2, "��5�Ҽ���׼��");
		sign.setLine(3, "��4��lδ׼��");
		sign.update();

		HashMap<String, Location> allLocation = previousLocation;
		allLocation.put(locationType, l);
		
		return allLocation;
	}
	
	public HashMap<String, Location> setLocation(AsyncPlayerChatEvent event, Player p, 
			HashMap<String, Location> previousLocation, String locationType)
	{
		double x = event.getPlayer().getLocation().getX();
		double y = event.getPlayer().getLocation().getY();
		double z = event.getPlayer().getLocation().getZ();
		
		double eyeX = event.getPlayer().getLocation().getDirection().getX();
		double eyeY = event.getPlayer().getLocation().getDirection().getY();
		double eyeZ = event.getPlayer().getLocation().getDirection().getZ();

		Location l = new Location(p.getWorld(), x, y, z);
		Vector v = new Vector(eyeX, eyeY, eyeZ);
		l.setDirection(v);
		HashMap<String, Location> allLocation = previousLocation;
		allLocation.put(locationType, l);
		
		return allLocation;
	}

	public HashMap<String, Location> setLocation(AsyncPlayerChatEvent event, Player p, String locationType)
	{
		double x = event.getPlayer().getLocation().getX();
		double y = event.getPlayer().getLocation().getY();
		double z = event.getPlayer().getLocation().getZ();
		
		double eyeX = event.getPlayer().getLocation().getDirection().getX();
		double eyeY = event.getPlayer().getLocation().getDirection().getY();
		double eyeZ = event.getPlayer().getLocation().getDirection().getZ();

		Location l = new Location(p.getWorld(), x, y, z);
		Vector v = new Vector(eyeX, eyeY, eyeZ);
		l.setDirection(v);
		HashMap<String, Location> allLocation = new HashMap<String, Location>();
		allLocation.put(locationType, l);
		
		return allLocation;
	}
}

package AmazingFishing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.Straiker123.TheAPI;
import me.Straiker123.TheAPI.SudoType;

public class Tournament {

	static List<String> legend = Arrays.asList("Length","MostCatch","Weight");
	public static enum Type{
		Length,
		MostCatch,
		Weight,
		Random;
	}
	static int count;
	static int save;
	public static boolean running() {
		return now != null;
	}
	public static void add(Player p, double record, double weight) {
		if(now != null) {
		if(now==Type.MostCatch) { 
			double i = stats.containsKey(p.getName()) ? stats.get(p.getName()): 0.0;
			++i;
			if(stats.containsKey(p.getName()))
				stats.replace(p.getName(),i);
			else
			stats.put(p.getName(), i);
		}
		if(now==Type.Length||now==Type.Weight) {
			if(stats.containsKey(p.getName())) {
				if(stats.get(p.getName())<record)
					stats.replace(p.getName(), record);
			}else
			stats.put(p.getName(), record);
		}
	}}
	static Type now;
	public static boolean inParticle(Player p) {
		return stats.containsKey(p.getName());
	}
	
	static int run;
	static HashMap<String, Double> stats = new HashMap<String, Double>();
	public static void stop(boolean rewards) {
		if(now==null)return;
		if(!rewards) {
			TheAPI.broadcastMessage(Loader.s("Stopped")
					.replace("%type%", Loader.c.getString("Tournaments."+now.toString()+".Name")).replace("%time%",
							TheAPI.getStringUtils().setTimeToString(Tournament.count)));
    	Bukkit.getServer().getScheduler().cancelTask(run);
    	count=0;
    	save=0;
        stats.clear();
    	now=null;
	}else{
		TheAPI.broadcastMessage(Loader.s("Stopped")
				.replace("%type%", Loader.c.getString("Tournaments."+now.toString()+".Name"))
				.replace("%time%", TheAPI.getStringUtils().setTimeToString(Tournament.count)));
    	Bukkit.getServer().getScheduler().cancelTask(run);
		for(String s:stats.keySet()) {if(TheAPI.getPlayer(s)==null)stats.remove(s);}
    	TheAPI.broadcastMessage(Loader.s("Winners")
				.replace("%type%", Loader.c.getString("Tournaments."+now.toString()+".Name")));
		 switch(now) {
 		case Weight:{
 			Ranking w = new Ranking(stats);
     		for (int i = 2; i >= 0; i--) {
     			String player = w.getPlayer(3-i);
     			TheAPI.broadcastMessage(Loader.c.getString("Tournaments."+now.toString()+".Positions")
         				.replace("%position%", 3-i+"")
         				.replace("%player%", player)
         				.replace("%playername%", p(player))
         				.replace("%weight%", String.format("%2.02f",TheAPI.getStringUtils().getDouble(w.getDouble(3-i)))).replace(",", "."));
     			if(!player.equalsIgnoreCase("-"))
        			for(String s:Loader.c.getStringList("Tournaments."+now.toString()+".Rewards."+(3-i)))
        				TheAPI.sudoConsole(SudoType.COMMAND, Color.c(s.replace("%position%", (3-i)+"")
             				.replace("%player%", player)
             				.replace("%playername%", p(player))
             				.replace("%weight%", String.format("%2.02f",TheAPI.getStringUtils().getDouble(w.getDouble(3-i))).replace(",", "."))));
     			Loader.me.set("Players."+player+".Stats.Tournaments", 1+Loader.me.getInt("Players."+player+".Stats.Tournaments"));
					Loader.me.set("Players."+player+".Stats.Top."+(3-i)+".Tournaments", 
							1+Loader.me.getInt("Players."+player+".Stats.Top."+(3-i)+".Tournaments"));
     		}
				Loader.saveChatMe();
		 }break;
     		case MostCatch:{
     			Ranking w = new Ranking(stats);
         		for (int i = 2; i >= 0; i--) {
         			String player = w.getPlayer(3-i);
         			TheAPI.broadcastMessage(Loader.c.getString("Tournaments."+now.toString()+".Positions")
             				.replace("%position%", 3-i+"")
             				.replace("%player%", player)
             				.replace("%playername%", p(player))
             				.replace("%fishes%", ((int)TheAPI.getStringUtils().getDouble(w.getDouble(3-i)))+""));
         			if(!player.equalsIgnoreCase("-"))
            			for(String s:Loader.c.getStringList("Tournaments."+now.toString()+".Rewards."+(3-i)))
            				TheAPI.sudoConsole(SudoType.COMMAND, Color.c(s.replace("%position%", (3-i)+"")
                 				.replace("%player%", player)
                 				.replace("%playername%", p(player))
                 				.replace("%fishes%", ((int)TheAPI.getStringUtils().getDouble(w.getDouble(3-i)))+"")));
         			Loader.me.set("Players."+player+".Stats.Tournaments", 1+Loader.me.getInt("Players."+player+".Stats.Tournaments"));
 					Loader.me.set("Players."+player+".Stats.Top."+(3-i)+".Tournaments", 
 							1+Loader.me.getInt("Players."+player+".Stats.Top."+(3-i)+".Tournaments"));
         		}
					Loader.saveChatMe();
		 }break;
     		case Length:{
     			Ranking w = new Ranking(stats);
         		for (int i = 2; i >= 0; i--) {
         			String player = w.getPlayer(3-i);
         			TheAPI.broadcastMessage(Loader.c.getString("Tournaments."+now.toString()+".Positions")
             				.replace("%position%", 3-i+"")
             				.replace("%player%", player)
             				.replace("%playername%", p(player))
             				.replace("%length%", String.format("%2.02f",TheAPI.getStringUtils().getDouble(w.getDouble(3-i)))).replace(",", "."));
         			if(!player.equalsIgnoreCase("-"))
            			for(String s:Loader.c.getStringList("Tournaments."+now.toString()+".Rewards."+(3-i)))
            				TheAPI.sudoConsole(SudoType.COMMAND, Color.c(s.replace("%position%", (3-i)+"")
                 				.replace("%player%", player)
                 				.replace("%playername%", p(player))
                 				.replace("%length%", String.format("%2.02f",TheAPI.getStringUtils().getDouble(w.getDouble(3-i))).replace(",", "."))));
         			Loader.me.set("Players."+player+".Stats.Tournaments", 1+Loader.me.getInt("Players."+player+".Stats.Tournaments"));
 					Loader.me.set("Players."+player+".Stats.Top."+(3-i)+".Tournaments", 
 							1+Loader.me.getInt("Players."+player+".Stats.Top."+(3-i)+".Tournaments"));
         		}
					Loader.saveChatMe();
     		}break;
     		default:
     			break;
          }
		 stats.clear();
    	now=null;
        count=0;
        save=0;
        return;
        }
	}
	public static void startType(Type type, int length) {
		if(type==Type.Random)type=Type.valueOf(TheAPI.getRandomFromList(legend).toString());
		now=type;
		count=length;
		if(count == 0)count = 600;
		save=count;
		 int as = (int) (save*0.80),bs = (int) (save*0.50), cs = (int) (save*0.30),ds = (int) (save*0.10);
         if(as<3)as=-20;
         if(bs<3)bs=-20;
         if(cs<3)cs=-20;
         if(ds<3)ds=-20;
         int a=as,b=bs,c=bs,d=ds;
		TheAPI.broadcastMessage(Loader.s("Started")
				.replace("%type%", Loader.c.getString("Tournaments."+type.toString()+".Name")).replace("%time%", TheAPI.getStringUtils().setTimeToString(Tournament.count)));
		run=Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Loader.plugin, 
				new Runnable(){
           public void run(){
				if(now==null) {
					Bukkit.getScheduler().cancelTask(run);
					return;
				}
                if(count > 0) {
                --count;
                }
    			for(String s:stats.keySet()) {if(TheAPI.getPlayer(s)==null)stats.remove(s);}
                if(Loader.c.getBoolean("Options.BossBar.Use"))
                for(Player p : Bukkit.getOnlinePlayers())
                	if(inParticle(p))
                		TheAPI.sendBossBar(p, Loader.c.getString("Options.BossBar.Running").replace("%time%", count+"").replace("%type%", now.toString()).replace("%time_formated%", TheAPI.getStringUtils().setTimeToString(count)),1,20);
               
                if(count == a || count == b || count == c || count == d) {
                	TheAPI.broadcastMessage(Loader.s("Running")
            				.replace("%type%", Loader.c.getString("Tournaments."+now.toString()+".Name")).replace("%time%", 
            						TheAPI.getStringUtils().setTimeToString(Tournament.count)));
            		switch(now) {
            		case Weight:{
            			Ranking w = new Ranking(stats);
            		for (int i = 2; i >= 0; i--) {
            			String player = w.getPlayer(3-i);
            			if(player==null)continue;
            			TheAPI.broadcastMessage(Loader.c.getString("Tournaments."+now.toString()+".Positions")
                				.replace("%position%", 3-i+"")
                				.replace("%player%", player)
                				.replace("%playername%", p(player))
                				.replace("%weight%", String.format("%2.02f",TheAPI.getStringUtils().getDouble(w.getDouble(3-i)))).replace(",", "."));
            		}
            		}break;
                		case MostCatch:{
                			Ranking w = new Ranking(stats);
                		for (int i = 2; i >= 0; i--) {
                			String player = w.getPlayer(3-i);
                			if(player==null)continue;
                			TheAPI.broadcastMessage(Loader.c.getString("Tournaments."+now.toString()+".Positions")
                    				.replace("%position%", 3-i+"")
                    				.replace("%player%", player)
                    				.replace("%playername%", p(player))
                    				.replace("%fishes%",((int)TheAPI.getStringUtils().getDouble(w.getDouble(3-i)))+""));
                		}
                		}break;
                		case Length:{
                			Ranking w = new Ranking(stats);
                    		for (int i = 2; i >= 0; i--) {

                    			String player = w.getPlayer(3-i);
                    			if(player==null)continue;
                    			TheAPI.broadcastMessage(Loader.c.getString("Tournaments."+now.toString()+".Positions")
                        				.replace("%position%", 3-i+"")
                        				.replace("%player%", player)
                        				.replace("%playername%", p(player))
                        				.replace("%length%", String.format("%2.02f",TheAPI.getStringUtils().getDouble(w.getDouble(3-i)))).replace(",", "."));
                    		}
                		}break;
                		default:
                			break;
                     }}
                if(count == 0) {
                	Bukkit.getServer().getScheduler().cancelTask(run);
                	TheAPI.broadcastMessage(Loader.s("Winners")
            				.replace("%type%", Loader.c.getString("Tournaments."+now.toString()+".Name")));
                switch(now) {
        		case Weight:{
        			Ranking w = new Ranking(stats);
            		for (int i = 2; i >= 0; i--) {
            			String player = w.getPlayer(3-i);
            			if(TheAPI.getPlayer(player)!=null) {
                    	if(inParticle(TheAPI.getPlayer(player)) && Loader.c.getBoolean("Options.BossBar.Use"))
                    		TheAPI.sendBossBar(TheAPI.getPlayer(player), Loader.c.getString("Options.BossBar.Win").replace("%type%", now.toString()).replace("%position%", 3-1+""), 1, 20);
            			}
            			TheAPI.broadcastMessage(Loader.c.getString("Tournaments."+now.toString()+".Positions")
                				.replace("%position%", 3-i+"")
                				.replace("%player%", player)
                				.replace("%playername%", p(player))
                				.replace("%weight%", String.format("%2.02f",TheAPI.getStringUtils().getDouble(w.getDouble(3-i)))).replace(",", "."));
            			if(!player.equalsIgnoreCase("-"))
                			for(String s:Loader.c.getStringList("Tournaments."+now.toString()+".Rewards."+(3-i)))
                				TheAPI.sudoConsole(SudoType.COMMAND, Color.c(s.replace("%position%", (3-i)+"")
                    				.replace("%player%", player)
                    				.replace("%playername%", p(player))
                    				.replace("%weight%", String.format("%2.02f",TheAPI.getStringUtils().getDouble(w.getDouble(3-i))).replace(",", "."))));
            			Loader.me.set("Players."+player+".Stats.Tournaments", 1+Loader.me.getInt("Players."+player+".Stats.Tournaments"));
    					Loader.me.set("Players."+player+".Stats.Top."+(3-i)+".Tournaments", 
    							1+Loader.me.getInt("Players."+player+".Stats.Top."+(3-i)+".Tournaments"));
            		}
					Loader.saveChatMe();
        		}break;
            		case MostCatch:{
            			Ranking w = new Ranking(stats);
                		for (int i = 2; i >= 0; i--) {
                			String player = w.getPlayer(3-i);
                			if(TheAPI.getPlayer(player)!=null) {
                            	if(inParticle(TheAPI.getPlayer(player)) && Loader.c.getBoolean("Options.BossBar.Use"))
                            		TheAPI.sendBossBar(TheAPI.getPlayer(player), Loader.c.getString("Options.BossBar.Win").replace("%type%", now.toString()).replace("%position%", 3-1+""), 1, 20);
                    			}
                			TheAPI.broadcastMessage(Loader.c.getString("Tournaments."+now.toString()+".Positions")
                    				.replace("%position%", 3-i+"")
                    				.replace("%player%", player)
                    				.replace("%playername%", p(player))
                    				.replace("%fishes%", ((int)TheAPI.getStringUtils().getDouble(w.getDouble(3-i)))+""));
                			if(!player.equalsIgnoreCase("-"))
                    			for(String s:Loader.c.getStringList("Tournaments."+now.toString()+".Rewards."+(3-i)))
                    				TheAPI.sudoConsole(SudoType.COMMAND, Color.c(s.replace("%position%", (3-i)+"")
                        				.replace("%player%", player)
                        				.replace("%playername%", p(player))
                        				.replace("%fishes%", ((int)TheAPI.getStringUtils().getDouble(w.getDouble(3-i)))+"")));
                			Loader.me.set("Players."+player+".Stats.Tournaments", 1+Loader.me.getInt("Players."+player+".Stats.Tournaments"));
        					Loader.me.set("Players."+player+".Stats.Top."+(3-i)+".Tournaments", 
        							1+Loader.me.getInt("Players."+player+".Stats.Top."+(3-i)+".Tournaments"));
                		}
    					Loader.saveChatMe();
            		}break;
            		case Length:{
            			Ranking w = new Ranking(stats);
                		for (int i = 2; i >= 0; i--) {
                			String player = w.getPlayer(3-i);
                			if(TheAPI.getPlayer(player)!=null) {
                            	if(inParticle(TheAPI.getPlayer(player)) && Loader.c.getBoolean("Options.BossBar.Use"))
                            		TheAPI.sendBossBar(TheAPI.getPlayer(player), Loader.c.getString("Options.BossBar.Win").replace("%type%", now.toString()).replace("%position%", 3-1+""), 1, 20);
                    			}
                			TheAPI.broadcastMessage(Loader.c.getString("Tournaments."+now.toString()+".Positions")
                    				.replace("%position%", 3-i+"")
                    				.replace("%player%", player)
                    				.replace("%playername%", p(player))
                    				.replace("%length%", String.format("%2.02f",TheAPI.getStringUtils().getDouble(w.getDouble(3-i)))).replace(",", "."));
                    		if(!player.equalsIgnoreCase("-"))
                			for(String s:Loader.c.getStringList("Tournaments."+now.toString()+".Rewards."+(3-i)))
                				TheAPI.sudoConsole(SudoType.COMMAND, Color.c(s.replace("%position%", (3-i)+"")
                        				.replace("%player%", player)
                        				.replace("%playername%", p(player))
                        				.replace("%length%", String.format("%2.02f",TheAPI.getStringUtils().getDouble(w.getDouble(3-i))).replace(",", "."))));
                			Loader.me.set("Players."+player+".Stats.Tournaments", 1+Loader.me.getInt("Players."+player+".Stats.Tournaments"));
        					Loader.me.set("Players."+player+".Stats.Top."+(3-i)+".Tournaments", 
        							1+Loader.me.getInt("Players."+player+".Stats.Top."+(3-i)+".Tournaments"));
                		}
    					Loader.saveChatMe();
            		}break;
            		default:
            			break;
                 }
                stats.clear();
            	now=null;
                count=0;
                save=0;
                }
           }
       }, 20,20);
	}
	static String p(String s) {
		try {
		if(TheAPI.getPlayer(s)!=null)return TheAPI.getPlayer(s).getDisplayName();
		return s;
		}catch(Exception e) {
			return "-";
		}
	}
}

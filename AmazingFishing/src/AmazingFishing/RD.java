package AmazingFishing;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.bekvon.bukkit.residence.api.ResidenceApi;

public class RD {
	private static boolean existRD() {
		return Bukkit.getPluginManager().getPlugin("Residence")!=null;
	}
	
    public static boolean isInResidence(Player p, String reg){
    	if(existRD()) {
        if(ResidenceApi.getResidenceManager().getByLoc(p.getLocation())!=null)return true;
        return false;
    	}
    	return false;
}
    public static String getResidence(Player p) {
    	String region = null;
    	if(existRD()) {
            if(ResidenceApi.getResidenceManager().getByLoc(p.getLocation()) != null)
            	region=ResidenceApi.getResidenceManager().getByLoc(p.getLocation()).getName();
         
    	}
    	return region;
    }
    
    public static enum Type{
    	Legendary,
    	Epic,
    	Rare,
    	Common;
    }
    public static boolean hasAccess(Player p, Type t) {
    	if(existRD()) {
    		if(Loader.c.getString("Options.Perms-Treasures."+t+".Residence") != null && Loader.c.getBoolean("Options.Perms-Treasures."+t+".UseResidence")) {
    			boolean bol = false;
    		for(String s:Loader.c.getStringList("Options.Perms-Treasures."+t+".Residence")) {
    			if(p.hasPermission("amazingfishing.treasures."+t.toString().toLowerCase()+"."+s))bol= true;
    			//amazingfishing.treasures.legendary.<residence name>
    		}
    		if(p.hasPermission("amazingfishing.treasures."+t.toString().toLowerCase()+".*"))bol=true;
    		return bol;
    		}
    		return true;
    	}
    	return true;
    }
}

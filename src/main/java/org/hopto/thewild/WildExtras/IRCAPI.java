package org.hopto.thewild.WildExtras;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import com.ensifera.animosity.craftirc.CraftIRC;
import com.ensifera.animosity.craftirc.EndPoint;
import com.ensifera.animosity.craftirc.RelayedMessage;
import com.ensifera.animosity.craftirc.EndPoint.Type;

	public class IRCAPI  implements EndPoint {
	    private final String exampletag = "WildExtras";
	    WildExtras WildExtras = org.hopto.thewild.WildExtras.WildExtras.plugin;
	    
	       
	    public RelayedMessage setupAPI() {
	        final Plugin plugin = WildExtras.getServer().getPluginManager().getPlugin("CraftIRC");
	        if ((plugin == null) || !plugin.isEnabled() || !(plugin instanceof CraftIRC)) {
	            WildExtras.getLogger().warning("no CraftIRC API!");
	            return null;
	        } else {
	            final CraftIRC craftirc = (CraftIRC) plugin;
	            craftirc.registerEndPoint(this.exampletag, this);
	            final RelayedMessage rm = craftirc.newMsg(this, null, "generic");
	            return rm;
	        }
	    }

	    
	    public void disableAPI() {
	        final Plugin plugin = WildExtras.getServer().getPluginManager().getPlugin("CraftIRC");
	        if (((plugin != null) && !plugin.isEnabled()) || (plugin instanceof CraftIRC)) {
	            ((CraftIRC) plugin).unregisterEndPoint(this.exampletag);
	        }
	    }

	   // @Override
	    public Type getType() {
	        return EndPoint.Type.MINECRAFT;
	    }

	    //@Override
	    public void messageIn(RelayedMessage msg) {
	        if (msg.getEvent().equals("join")) {
	           // WildExtras.getServer().broadcastMessage(msg.getField("sender") + ChatColor.RESET + " joined da game!");
	        }
	    }

	   // @Override
	    public boolean userMessageIn(String username, RelayedMessage msg) {
	        return false;
	    }

	   // @Override
	    public boolean adminMessageIn(RelayedMessage msg) {
	        return false;
	    }

	   // @Override
	    public List<String> listUsers() {
	        return null;
	    }

	    //@Override
	    public List<String> listDisplayUsers() {
	        return null;
	    }
	}
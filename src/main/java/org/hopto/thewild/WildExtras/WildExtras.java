package org.hopto.thewild.WildExtras;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


//todo - restructure classes

public final class WildExtras extends JavaPlugin {
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(new WEListeners(), this);
		getLogger().info("WildExtras Started");
	}
	public void onDisable(){
		getLogger().info("WildExtras Stopped");
	}
	
	
	
	//Arrow Clear Command
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
    if(cmd.getName().equalsIgnoreCase("arrowclear")){
    	if(sender.hasPermission("wildextras.arrowclear")) {
    		   //Do something
    		
		for(World w : getServer().getWorlds()) {
		    // Do something
		
			for (Arrow arrow : w.getEntitiesByClass(Arrow.class)) {
				//would be nice to check if its moving but i cant seem to do it right now					
					arrow.remove();
			}

		}

		}else{
    		   //Do something else - no perms
    		}
		
		return true;
	} //If this has happened the function will return true. 
        // If this hasn't happened the a value of false will be returned.
	return false; 
}
	     
		    

		    
}

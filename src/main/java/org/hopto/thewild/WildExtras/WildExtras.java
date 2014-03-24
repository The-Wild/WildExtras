package org.hopto.thewild.WildExtras;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;




public final class WildExtras extends JavaPlugin implements Listener {
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
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
	
	
	
//Dispenser blocking for old worlds
		    @EventHandler
		    public void onDispense(BlockDispenseEvent event){
		    if	(event.getItem().getType() == Material.WATER_BUCKET){
		            event.setCancelled(true);
		            }     
			if	(event.getItem().getType() == Material.LAVA_BUCKET){
		            event.setCancelled(true);
		            }    
		    if	(event.getBlock().getWorld().getName().equalsIgnoreCase("old_world")){
	            event.setCancelled(true);
	            }
		    if	(event.getBlock().getWorld().getName().equalsIgnoreCase("old_world_nether")){
	            event.setCancelled(true);
	            }
		    if	(event.getBlock().getWorld().getName().equalsIgnoreCase("old_world_the_end")){
	            event.setCancelled(true);
	            }  
		}
		    

			@EventHandler
		    public void onHangingPlace(HangingPlaceEvent e){ // block break event - or is it?
			    if	(e.getBlock().getWorld().getName().equalsIgnoreCase("old_world")){
		            e.setCancelled(true);
		            }
			    if	(e.getBlock().getWorld().getName().equalsIgnoreCase("old_world_nether")){
		            e.setCancelled(true);
		            }
			    if	(e.getBlock().getWorld().getName().equalsIgnoreCase("old_world_the_end")){
		            e.setCancelled(true);
		            }        

		    }
		    
		    @EventHandler
		    public void onHangingBreakByEntity(HangingBreakByEntityEvent ev){ // block break event - or is it?
			    if	(ev.getEntity().getWorld().getName().equalsIgnoreCase("old_world")){
		            ev.setCancelled(true);
		            }
			    if	(ev.getEntity().getWorld().getName().equalsIgnoreCase("old_world_nether")){
		            ev.setCancelled(true);
		            }
			    if	(ev.getEntity().getWorld().getName().equalsIgnoreCase("old_world_the_end")){
		            ev.setCancelled(true);
		            }           

		    }
		    
		    
		    
}

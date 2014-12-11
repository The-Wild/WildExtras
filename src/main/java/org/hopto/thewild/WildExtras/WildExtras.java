package org.hopto.thewild.WildExtras;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
	
	
	
	//Command Listener
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){

		//Arrow Clear Command
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
    		   //no perms - no message
    		}
		
		return true;
	} else if(cmd.getName().equalsIgnoreCase("spyon")){
		if(sender.hasPermission("wildextras.spy")) {
 		   //got perms
			if (args.length < 1) {
				return false;
			}
			//define our player and build some initial config file details for checks
			Player player = (sender instanceof Player) ? (Player) sender : null;
			String pname = player.getName();
			File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(), File.separator + "UserData");
			File f = new File(userdata, File.separator + pname + ".yml");
			//define our target player
			Player targetPlayer = getServer().getPlayer(args[0]);
			
			//Dont allow run on these conditions
			if (targetPlayer == null) {
				player.sendMessage("Player Not Found");
				return true;
		/*	}
				else if (targetPlayer == player) {
					player.sendMessage("Dummy!");
				return true; */
			}   else if(!f.exists()){
				player.sendMessage("You are already spying");		
				return true;
			}
			
			
			if (targetPlayer != null) {
			//Vanish player
				for(Player allPlayers : getServer().getOnlinePlayers()) {
				    player.hidePlayer(allPlayers);
				}
			//serialise inventory to string
			Inventory i = player.getInventory();
			String savedInventory = InventoryConvert.InventoryToString(i);
			//Turn location to string
			String savedLocation = player.getPlayer().getLocation().toString();
			
			
			//save inventory and location to file
			// This creates a file inside plugins data folder inside the folder "UserData"
			FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);   
			   if(!f.exists()){
			        try {
			            f.createNewFile();
			            } catch (IOException e) {
			 
			                e.printStackTrace();
			                    }
			 
			            try {
			                try {
			                    try {
			                        playerData.load(f);
			// Here you can set what you want to be in the player file by default
			                        playerData.set(savedInventory, savedLocation);
			                        playerData.save(f);   
			                    } catch (FileNotFoundException e) {
			                        // TODO Auto-generated catch block
			                        e.printStackTrace();
			                    } catch (IOException e) {
			                        // TODO Auto-generated catch block
			                        e.printStackTrace();
			                    }
			                } catch (InvalidConfigurationException e) {
			                    // TODO Auto-generated catch block
			                    e.printStackTrace();
			                }
			            } finally {
			            }
			   }       
			
			
			//prevent interaction////////////
			player.setGameMode(GameMode.ADVENTURE);
			//teleport to player
			player.teleport(targetPlayer);
			}

			
		
		

		}else{
 		   //no perms - no message
 		}
	} 
	return false; 
	
}
	
	
	

   
		    

		    
}


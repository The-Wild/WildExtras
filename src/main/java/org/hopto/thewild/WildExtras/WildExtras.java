package org.hopto.thewild.WildExtras;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

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
import org.hopto.thewild.WildExtras.InventoryConvert;
import org.hopto.thewild.WildExtras.LocationStringer;




//todo - restructure classes

public final class WildExtras extends JavaPlugin {

			
	public class InventoryConvert {

	}



	public void onEnable(){

		getServer().getPluginManager().registerEvents(new WEListeners(), this);
		getLogger().info("WildExtras Started");
	}
	public void onDisable(){
		getLogger().info("WildExtras Stopped");
	}
	
	public HashMap<String, String> VisitMap = new HashMap<String, String>();
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
		} else if(cmd.getName().equalsIgnoreCase("va")){
			//visit accept
			//get sender details
			Player player = (sender instanceof Player) ? (Player) sender : null;
			String pname = player.getName();
			if (VisitMap.get(pname) != null) {
			String moderatorName = VisitMap.get(pname);	
			Player moderator = getServer().getPlayer(moderatorName);
			VisitMap.remove(pname);	
				//save location & inventory to file
				String savedLocation = org.hopto.thewild.WildExtras.LocationStringer.toString(moderator.getPlayer().getLocation());
				String savedInventory = org.hopto.thewild.WildExtras.InventoryConvert.InventoryToString(moderator.getPlayer().getInventory());
				File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(), File.separator + "UserData");
				File f = new File(userdata, File.separator + moderatorName + "-visit.yml");
				FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);   
				   if(!f.exists()){
				        try {
				            f.createNewFile();
				            } catch (IOException e) {
				                 e.printStackTrace();
				                    }				 
				            try {
				                        playerData.load(f);
				                        playerData.set("location", savedLocation);
				                        playerData.set("inventory", savedInventory);
				                        playerData.save(f);   
				                        //request teleport
				                        moderator.teleport(player);
				                        //clear inventory
				                        moderator.getInventory().clear();
				                        moderator.updateInventory();
				                        moderator.setGameMode(GameMode.ADVENTURE);
				                        moderator.sendMessage("Visiting " + pname);
				                        player.sendMessage("Moderator [" + moderatorName + "] is here." );
				                        return true;
				                        
				                    } catch (FileNotFoundException e) {
				                        // TODO Auto-generated catch block
				                        e.printStackTrace();
				                    } catch (IOException e) {
				                        // TODO Auto-generated catch block
				                        e.printStackTrace();
				                } catch (InvalidConfigurationException e) {
				                    // TODO Auto-generated catch block
				                    e.printStackTrace();
				                
				            } finally {
				            }
				   } 		
				
				
								
			} else {
				player.sendMessage("No visit to accept.");
				return true;
			}			
			
			
		//request teleport - accept - teleport to, but disable pvp and inventory clicking
		} else if(cmd.getName().equalsIgnoreCase("visit")){
			if(sender.hasPermission("wildextras.visit")) {
		 		   //got perms
				if (args.length < 1) {
					return false;
				}
				//build player details
				//ACTUALLY - should put message to other player here then move all below code into VA
				
				Player player = (sender instanceof Player) ? (Player) sender : null;
				String pname = player.getName();
				File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(), File.separator + "UserData");
				File f = new File(userdata, File.separator + pname + "-visit.yml");
				Player targetPlayer = getServer().getPlayer(args[0]);
				String targetName = targetPlayer.getName().toString();
				//check conditions to quit
				if (targetPlayer == null) {
					player.sendMessage("Player Not Found");
					return true;
				}
					else if (targetPlayer == player) {
						player.sendMessage("Dummy!");
					return true; 
				}   else if(f.exists()){
					player.sendMessage("You are already visiting - use /endvisit");		
					return true;
				}
				VisitMap.put(pname, targetName);
				targetPlayer.sendMessage("Moderator [" + pname + "] would like to visit you. Type /va to accept.");
				player.sendMessage("Sent request to " + targetName);
				return true;
			}
	
			//end vist
		} else if(cmd.getName().equalsIgnoreCase("endvisit")){
			if(sender.hasPermission("wildextras.visit")) {
		 		   //got perms
					//define our player and build some initial config file details for checks
					Player player = (sender instanceof Player) ? (Player) sender : null;
					String pname = player.getName();
					File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(), File.separator + "UserData");
					File f = new File(userdata, File.separator + pname + "-visit.yml");
					//define our player and config files to use
					//Dont allow run on these conditions
					if(!f.exists()){
						player.sendMessage("You are not visiting anyone.");		
						return true;
					}
					
						for(Player allPlayers : getServer().getOnlinePlayers()) {
						    player.showPlayer(allPlayers);
						}
					//Turn location to string
					String savedLocation = player.getPlayer().getLocation().toString();
					
					
					//get userdata and give back inventory and tp to old location"
					FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);   
					   if(f.exists()){		
						   try {
					                        playerData.load(f);
					                        String newlocation = playerData.getString("location");
					                        String newinventory = playerData.getString("inventory");   
					                        Inventory inv1 = org.hopto.thewild.WildExtras.InventoryConvert.StringToInventory(newinventory);
					                        Location loc1 = org.hopto.thewild.WildExtras.LocationStringer.fromString(newlocation);
					                        player.teleport(loc1);
					                        player.getInventory().setContents(inv1.getContents());
					                        player.updateInventory();
					                        player.setGameMode(GameMode.SURVIVAL);
					                        f.delete();
					                        player.sendMessage("Visit Ended");
					                        return true;
						   } catch 
							   (FileNotFoundException e) {
			                        // TODO Auto-generated catch block
			                        e.printStackTrace();
			                        player.sendMessage("Huge error - may have lost your inventory!");  
						   } catch (IOException e) {
		                        // TODO Auto-generated catch block
		                        e.printStackTrace();
		                        player.sendMessage("Huge error - may have lost your inventory!");  
						   } catch (InvalidConfigurationException e) {
		                    // TODO Auto-generated catch block
		                    e.printStackTrace();
	                        player.sendMessage("Huge error - may have lost your inventory!");  
		            } finally {
		            }
					   }     
					
					//convert location and inventory back
					  
			
					
				}else{
		 		   //no perms - no message
		 		} 
				
	}	
		return false; 
	}   
}	    



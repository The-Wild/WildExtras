package org.hopto.thewild.WildExtras;


import java.io.File;
import java.io.FileFilter;
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

import com.earth2me.essentials.Essentials;




//todo - restructure classes

public final class WildExtras extends JavaPlugin {
	//public static Essentials essentials;
			
	public class InventoryConvert {

	}


        private WEListeners weListeners = null;
	public void onEnable(){
		
     /*   Plugin essentialsPlugin = Bukkit.getPluginManager().getPlugin("Essentials");

        if (essentialsPlugin.isEnabled() && (essentialsPlugin instanceof Essentials)) {
        	
            this.essentials = (Essentials) essentialsPlugin;
    } else {
           //could not hook
    	Bukkit.getPluginManager().disablePlugin(this);
    	
    }
        
        //Plugin plugin = Bukkit.getPluginManager().getPlugin("WildExtras"); */
                weListeners = new WEListeners(this);
		getServer().getPluginManager().registerEvents(weListeners, this);
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
	/*	} else if(cmd.getName().equalsIgnoreCase("mutelist")){
			if(sender.hasPermission("wildextras.visit")) {
				for eachuser in essentials.getUser() {
				essentials.getUser(eachuser)
				}				
				
			}
			//no perms
			return false;
			*/
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
				            	//load visit inventory data from file 		
								File visitFile = new File(userdata, File.separator + "visit-inventory.yml");
								FileConfiguration visitInv = YamlConfiguration.loadConfiguration(visitFile); 
								String visitInventory = visitInv.getString("inventory");
								Inventory inv1 = org.hopto.thewild.WildExtras.InventoryConvert.StringToInventory(visitInventory);
			                      
				                        playerData.load(f);
				                        playerData.set("location", savedLocation);
				                        playerData.set("inventory", savedInventory);
				                        playerData.save(f);   
				                        //request teleport
				                        moderator.teleport(player);
				                        //clear inventory
				                        moderator.getInventory().clear();
				                        //set to saved visit inventory
				                        moderator.getInventory().setContents(inv1.getContents());
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
			
			
		//create visit inventory - makes it easy to add stuff
		} else if(cmd.getName().equalsIgnoreCase("makevisitinv")){
			if(sender.hasPermission("wildextras.makevisitinv")) {
		 		   //got perms
				Player player = (sender instanceof Player) ? (Player) sender : null;
				File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(), File.separator + "UserData");
				File f = new File(userdata, File.separator + "visit-inventory.yml");
				String newvinv = org.hopto.thewild.WildExtras.InventoryConvert.InventoryToString(player.getPlayer().getInventory());
				FileConfiguration visitData = YamlConfiguration.loadConfiguration(f);   
				try {
				visitData.load(f);
                visitData.set("inventory", newvinv);
                visitData.save(f); 
				player.sendMessage("Set new inventory for visits");
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
	
			//end vist
			
		} else if(cmd.getName().equalsIgnoreCase("clearpvpcount")){
			if(sender.hasPermission("wildextras.clearpvpcount")) {
		 		   //got perms
				File protecteduserdata = new File(Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(), File.separator + "ProtectedUserData");
				org.hopto.thewild.WildExtras.WEListeners.deathmap.clear();
				for(File file: protecteduserdata.listFiles()) file.delete();
				return true;
        } return false;
		} else if(cmd.getName().equalsIgnoreCase("pvpon")){
			if(sender.hasPermission("wildextras.pvpon")) {
		 		   //got perms
				Player player = (sender instanceof Player) ? (Player) sender : null;
				String pname = player.getName().toString();
				File protecteduserdata = new File(Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(), File.separator + "ProtectedUserData");
				File f = new File(protecteduserdata, File.separator + pname + "-protected.yml");
				if (f.exists()){
				f.delete();
				org.hopto.thewild.WildExtras.WEListeners.deathmap.remove(pname);
				sender.sendMessage("You are no longer PvP Protected");
                                if (player != null) {
                                    weListeners.colorNick(player);
                                }
				return true;
				} else {
					sender.sendMessage("You are not PvP Protected");
					return true;
				}
        } return false;
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
				VisitMap.put(targetName, pname);
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
					
					/*	for(Player allPlayers : getServer().getOnlinePlayers()) {
						    player.showPlayer(allPlayers);
						}
					//Turn location to string
					String savedLocation = player.getPlayer().getLocation().toString();
					
					*/
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
					                        player.getInventory().clear();
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



package org.hopto.thewild.WildExtras;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.Inventory;
import nl.lolmewn.stats.api;

public class WEListeners implements Listener {
//old world config	
	//Dispenser blocking for old worlds
    @EventHandler
    public void onDispense(BlockDispenseEvent event){
    if	(event.getBlock().getWorld().getName().contains("old")){
        event.setCancelled(true);
        }
}
    
    //Stop painting placing
	@EventHandler
    public void onHangingPlace(HangingPlaceEvent e){ // block break event - or is it?
	    if	(e.getBlock().getWorld().getName().contains("old")){
            e.setCancelled(true);
            }     

    }

	
    
	//Stop item frame theft
    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent ev){ // block break event - or is it?
	    if	(ev.getEntity().getWorld().getName().contains("old")){
            ev.setCancelled(true);
            }
	          

    }
    
    //Stop horse inventory theft - old world
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
    	HumanEntity human =  event.getView().getPlayer();
	    if(human instanceof Player)
	    {
	        Player player = (Player)human;
	        if (player.getWorld().getName().contains("old")) {
		      Inventory inv = event.getInventory();
	            if (inv instanceof HorseInventory) {
		                          event.setCancelled(true);
		                          player.updateInventory();
	            }
	        }
	    }
    }
    
    //stop block breaking
    @EventHandler
    public void onBlockBreak(BlockBreakEvent ev){ // block break event - or is it?
	    if	(ev.getBlock().getWorld().getName().contains("old")){
            ev.setCancelled(true);
            }
    }
    
    
    //stop block placing
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent ev){ // block break event - or is it?
	    if	(ev.getBlock().getWorld().getName().contains("old")){
            ev.setCancelled(true);
            }
    }
    
    
    //stop block interaction - like chests - do this for visits too
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
    	Player player = e.getPlayer();
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(), File.separator + "UserData");
		File f = new File(userdata, File.separator + player.getName() + "-visit.yml");
    if(f.exists() || player.getWorld().getName().contains("old")){
    e.setCancelled(true);
        }
    }
    
 //end old world config
 
    //prevent pvp kill spamming - count deaths
    public static HashMap<String, Integer> deathmap = new HashMap<String, Integer>();
	File protecteduserdata = new File(Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(), File.separator + "ProtectedUserData");

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
          if(event.getEntity().getKiller() instanceof Player) {
    		int deathCount;
                  Player victim = event.getEntity();
                  String v = victim.getName().toString();
                  if (deathmap.get(v) != null) {
                	 deathCount = deathmap.get(v);
                  }  else {
                	 deathCount = 0; 
                  }       
     int newDeathCount=deathCount + 1;     
     deathmap.put(v,newDeathCount); 
     if (deathmap.get(v)>2) {
		File f = new File(protecteduserdata, File.separator + v + "-protected.yml");
		 if(!f.exists()){
		        try {
		            f.createNewFile();
		            } catch (IOException e) {
		                 e.printStackTrace();
		                    }	
		    	victim.sendMessage("You are now protected from PvP! Use /pvpon to disable.");
		 }
     }
    }
          return;
    }

    
    
    
    
    //prevent pvp damage on visits - and on pvp protect because of spam kills - also prevent protected person from pvping.
    @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true) 
    public void onEntityDamage(final EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        final Player player = (Player)
      	if (!(event.getEntity() instanceof Player && event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)event).getDamager() instanceof Player)) {
      	  final Player player=(Player)event.getEntity();
      		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(), File.separator + "UserData");
      		File f = new File(userdata, File.separator + player.getName() + "-visit.yml");
      	  if (f.exists()) {
      	    event.setCancelled(true);
      	    event.setDamage(0);
      	    return;
        	}
      	} else {
            final Player player2=(Player)event.getEntity();
            Entity killer = ((EntityDamageByEntityEvent)event).getDamager();
            File userdata2 = new File(Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(), File.separator + "UserData");
            File f2 = new File(userdata2, File.separator + player2.getName() + "-visit.yml");
            File p = new File(protecteduserdata, File.separator + player2.getName() + "-protected.yml");
            File pa = new File(protecteduserdata, File.separator + killer.getName() + "-protected.yml");
            if (f2.exists()) {
                event.setCancelled(true);
                event.setDamage(0);
                return;
            } else if (p.exists() || pa.exists()) {
                event.setCancelled(true);
                event.setDamage(0);
                killer.sendMessage(player2.getName() + " is PvP protected. Leave them alone!");
                return;
            } else if (pa.exists()) {
                event.setCancelled(true);
                event.setDamage(0);
                killer.sendMessage("You are PvP protected. Type /pvpon to disable");
                return;
            } else if (is_newbie()) {
                event.setCancelled(true);
                event.setDamage(0);
                killer.sendMessage(player2().getName() + " is a newbie, leave them alone!");
                return;
            }
        }
    }
       	
    	
    
    
    
    
    
    
	//prevent block pickup by moderators visiting. - need to add stop chest / enderchest open. Also need to stop damage.
	@EventHandler
    public void noPickup(PlayerPickupItemEvent e){
		Player player = e.getPlayer();
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(), File.separator + "UserData");
		File f = new File(userdata, File.separator + player.getName() + "-visit.yml");
		if(f.exists() || player.getName().contains("old")){
        if (e.isCancelled()) {
            return;
        }       
        e.setCancelled(true);
		}
    }	   
    
    
    //create a file for each user so that wildbot knows who to trust!
    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
             String playername = event.getPlayer().getName();
             File playerfile = new File("plugins/WildExtras/"+playername);
             if (!playerfile.exists()) {
            	 try {
                     playerfile.createNewFile();
                     event.getPlayer().sendMessage(
                             "Welcome!  Please read the welcome guide for "
                             + "useful info: http://the-wild.tk/welcome"
                    );
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }     

             // And if they're a newbie, colour their name
             if (is_newbie(playername)) {
                 event.getPlayer().setPlayerListName("&d" + playername);
                 event.getPlayer().setPlayerName("&d" + playername);
             }
          }
    
    private StatsAPI statsAPI;

    private boolean setupStatsAPI(){
        RegisteredServiceProvider<StatsAPI> stats = getServer().getServicesManager().getRegistration(nl.lolmewn.stats.api.StatsAPI.class);
        if (stats!= null) {
            statsAPI = stats.getProvider();
        }
        return (statsAPI != null);
    }

    public boolean is_newbie(String playername) {
        Int playtime_secs = StatsAPI.getPlaytime(playername, "world");
        return (playtime_secs < 60 * 60);
    }

}

package org.hopto.thewild.WildExtras;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
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
import org.bukkit.plugin.RegisteredServiceProvider;
import nl.lolmewn.stats.api.StatsAPI;

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
        if (isVisiting(player) || player.getWorld().getName().contains("old")) {
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
    public void onEntityDamage(final EntityDamageEvent event) {
        // If it's not a player being damaged, we don't care.
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player victim = (Player)event.getEntity();

        // If the attacker is not a player, then allow the damage unless it's a
        // visiting pmod being attacked:
        Entity attacker = ((EntityDamageByEntityEvent)event).getDamager();
        if (!(attacker instanceof Player)) {
            if (isVisiting(victim)) {
                event.setCancelled(true);
                event.setDamage(0);
            }
            return;
        } else {
            attacker = (Player) attacker;
        }

        Bukkit.getLogger().warning("Handling damage on " + victim + " named "
                + victim.getName() + " from " + attacker);

        // If the player is being show with a bow, though, the "damager" will be
        // an arrow, and we need to find out who fired that arrow:
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.getShooter() instanceof Player) {
                attacker = (Player) arrow.getShooter();
            }
        }

        // If the victim is a visiting pmod they should be immune to all damage,
        // or if the attacker is a visiting pmod they shouldn't be able to
        // damage other players, so if either is the case shortcut here
        if (isVisiting(victim) || isVisiting(attacker)) {
            event.setCancelled(true);
            event.setDamage(0);
            return;
        }

        // The remaining damage exemptions all only apply to PvP damage, so if
        // the attacker isn't a player, we don't care:
        if (!(attacker instanceof Player)) {
            return;
        }

        // Right, so we're going to nerf the damage - in some cases we want
        // to explain why - but only try to explain if the killer is a player
        if (isProtected(victim)) {
            attacker.sendMessage(victim.getName() + " is PvP protected. Leave them alone!");
        } else if (isProtected(attacker)) {
            attacker.sendMessage("You are PvP protected. Type /pvpon to disable");
        } else if (isNewbie(victim)) {
            attacker.sendMessage(victim.getName() + " is a newbie, leave them alone!");
        } else if (isNewbie(attacker)) {
            attacker.sendMessage(
                "You are still PvP protected as a new player, and thus cannot attack others yet"
            );
        }

        // Cancel the damage
        event.setCancelled(true);
        event.setDamage(0);
        return;
    }

    private boolean isProtected(final Entity player) {
        File userdata = new File(
            Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(),
            File.separator + "UserData"
        );
        File protected_file = new File(userdata, player.getName() + "-protected.yml");
        return protected_file.exists();
    }

    private boolean isVisiting(final Entity player) {
        File userdata = new File(
            Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(),
            File.separator + "UserData"
        );
        File visit_file = new File(userdata, player.getName() + "-visit.yml");
        return visit_file.exists();
    }
       	
    private boolean isNewbie(final Entity player) {
        if (statsAPI == null) {
            // Fail-safe if Stats API wasn't available for whatever reason
            return false;
        }
        double playtime_secs = statsAPI.getPlaytime(player.getName());
        return (playtime_secs < 60 * 60);
    }
    
    
    
    
    
    //prevent block pickup by moderators visiting. - need to add stop chest / enderchest open. Also need to stop damage.
    @EventHandler
    public void noPickup(PlayerPickupItemEvent e){
        Player player = e.getPlayer();
        if (isVisiting(player) || player.getName().contains("old")) {
            if (e.isCancelled()) {
                return;
            } else {
                e.setCancelled(true);
            }
        }
    }	   
    
    
    //create a file for each user so that wildbot knows who to trust!
    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playername = player.getName();
        File playerfile = new File("plugins/WildExtras/"+playername);
        if (!playerfile.exists()) {
            try {
                playerfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Welcome them, too
            event.getPlayer().sendMessage(
                "Welcome aboard!  Please see the welcome guide "
                + "http://the-wild.tk/welcome for helpful info!);
        } else {
            if (isNewbie(player)) {
            event.getPlayer().sendMessage(
               "Welcome back!  Remember, there's lots of useful info"
                + " in the welcome guide: http://the-wild.tk/welcome"
            );
        }     

        // And if they're a newbie, colour their name
        if (isNewbie(player)) {
            player.setPlayerListName("&d" + playername);
            player.setDisplayName("&d" + playername);
        }
    }
/*    
    private StatsAPI CachedStatsAPI;

    private StatsAPI getStatsAPI() {
        if (CachedStatsAPI != null) {
            return CachedStatsAPI;
        } else {
            StatsAPI stats 
                = this.getServer().getServicesManager().load(
                        nl.lolmewn.stats.api.StatsAPI.class
                );
            if (stats != null) {
                CachedStatsAPI = stats;
            }
            return CachedStatsAPI;
        }
    }
*/

private StatsAPI statsAPI;

private boolean setupStatsAPI(){
        RegisteredServiceProvider<StatsAPI> stats = Bukkit.getServer().getServicesManager().getRegistration(nl.lolmewn.stats.api.StatsAPI.class);
        
        if (stats!= null) {
            //statsAPI = stats.getProvider();
        }
        Bukkit.getLogger().warning("setupStatsAPI() called, result " + statsAPI);
        return (statsAPI != null);
    }
    // Set up the Stats API when we're enabled
    public void onEnable() {
        setupStatsAPI();
    }


}

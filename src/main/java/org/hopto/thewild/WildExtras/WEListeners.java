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
        Entity attacker = ((EntityDamageByEntityEvent)event).getDamager();

        // If the victim is a visiting pmod they should be immune to all damage,
        // or if the attacker is a visiting pmod they shouldn't be able to
        // damage other players, so if either is the case shortcut here
        if (isVisiting(player) || isVisiting(attacker)) {
            event.setCancelled(true);
            event.setDamage(0);
            return;
        }

        // The remaining damage exemptions all only apply to PvP damage, so if
        // the attacker isn't a player, we don't care:
        if (!attacker instanceof Player) {
            return;
        }

        // Right, so we're going to nerf the damage - in some cases we want
        // to explain why - but only try to explain if the killer is a player
        if (isProtected(victim)) {
            attacker.sendMessage(victim.getName() + " is PvP protected. Leave them alone!");
        } else if (isProtected(attacker)) {
            attacker.sendMessage("You are PvP protected. Type /pvpon to disable");
        } else if (isNewbie(victim)) {
            attacker.sendMessage(victim().getName() + " is a newbie, leave them alone!");
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

    private boolean isProtected(final Player player) {
        File userdata = new File(
            Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(),
            File.separator + "UserData"
        );
        protected_file = new File(userdata, player.getName() + "-protected.yml");
        return protected_file.exists();
    }

    private boolean isVisiting(final Player player) {
        File userdata = new File(
            Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(),
            File.separator + "UserData"
        );
        visit_file = new File(userdata, player.getName() + "-visit.yml");
        return visit_file.exists();
    }
       	
    private boolean isNewbie(final Player player) {
        StatsAPI statsAPI = getStatsAPI();
        if (statsAPI == null) {
            // Fail-safe if Stats API wasn't available for whatever reason
            return false;
        }
        Int playtime_secs = statsAPI.getPlaytime(player.getName(), "world");
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
        }     

        // And if they're a newbie, colour their name
        if (isNewbie(player)) {
            player.setPlayerListName("&d" + playername);
            player.setPlayerName("&d" + playername);
        }
    }
    
    private StatsAPI CachedStatsAPI;

    private StatsAPI getStatsAPI() {
        if (CachedstatsAPI) {
            return CachedstatsAPI;
        } else {
            RegisteredServiceProvider<StatsAPI> stats = getServer().getServicesManager().getRegistration(nl.lolmewn.stats.api.StatsAPI.class);
            if (stats == null) {
                CachedStatsAPI = stats.getProvider();
            }
            return CachedStatsAPI;
        }
    }


}

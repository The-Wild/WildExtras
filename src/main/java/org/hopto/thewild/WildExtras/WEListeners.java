package org.hopto.thewild.WildExtras;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.ChatColor;
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
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import nl.lolmewn.stats.api.StatsAPI;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;

public class WEListeners implements Listener {
    public Plugin plugin;

    WEListeners(Plugin caller_plugin) {
        plugin = caller_plugin;
    }

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
                        colorNick(victim);
		 }
     }
    }
          return;
    }

    
    
    
    
    //prevent pvp damage on visits - and on pvp protect because of spam kills - also prevent protected person from pvping.
    @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true) 
    public void onDamage(final EntityDamageEvent event) {
        // If it's not a player being damaged, we don't care.
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player victim = (Player)event.getEntity();


        // If the victim is in visit mode, they're immune to all damage, so nerf
        // it and go no further if so:
        if (isVisiting(victim)) {
            debugmsg(event + " damage to " + victim.getName()
                    + " blocked due to visit mode");
            event.setCancelled(true);
            event.setDamage(0);
            return;
        }

        // Work out who/what the attacker is.  How we do this differs depending
        // on the type of damage - direct attack or projectile.
        Entity attacker = null;

        if (event instanceof EntityDamageByEntityEvent) {
            attacker = ((EntityDamageByEntityEvent)event).getDamager();
            debugmsg("attacker class " + attacker.getClass());
            if (((EntityDamageByEntityEvent)event).getDamager() instanceof Arrow) {
                debugmsg("damager was instanceof Arrow");
                Arrow arrow = (Arrow) ((EntityDamageByEntityEvent)event).getDamager();
                if (arrow.getShooter() instanceof Player) {
                    attacker = (Player) arrow.getShooter();
                    debugmsg(
                        victim.getName() + " hit by arrow from "
                        + attacker.getName()
                    );
                } else {
                    debugmsg("Arrow fired by non-player " + attacker);
                }
            }
        } else {
            debugmsg("Unknown damage source " + event);
        }

        if (attacker == null) {
            debugmsg("Failed to determine attacker, allow damage");
            return;
        }

        // Remainder of checks concern player-to-player damage, so if we
        // determined it's non-player-caused, ignore.
            
        if (!(attacker instanceof Player)) {
            debugmsg("Damage from non-player " + attacker);
            return;
        }

        debugmsg("Handling damage on " + victim + " named "
                + victim.getName() + " from " + attacker);

        // If the player is being show with a bow, though, the "damager" will be
        // an arrow, and we need to find out who fired that arrow:
        if (event.getEntity() instanceof Arrow) {
            debugmsg(
                    victim.getName() + " got shot with an arrow"
            );
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.getShooter() instanceof Player) {
                attacker = (Player) arrow.getShooter();
                debugmsg(
                        "Shooter was " + attacker.getName()
                );
            } else {
                debugmsg(
                        "Shooter was not a player though"
                );
            }
        }

        // If the victim is a visiting pmod they should be immune to all damage,
        // or if the attacker is a visiting pmod they shouldn't be able to
        // damage other players, so if either is the case shortcut here
        if (isVisiting(victim) || isVisiting(attacker)) {
            debugmsg("Victim or attacker is visiting, damage blocked"); 
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
        boolean allowDamage = true;
        if (isProtected(victim)) {
            debugmsg("Protected player can't be attacked");
            attacker.sendMessage(victim.getName() + " is PvP protected. Leave them alone!");
            allowDamage = false;
        } else if (isProtected(attacker)) {
            debugmsg("Protected player can't attack others");
            attacker.sendMessage("You are PvP protected. Type /pvpon to disable");
            allowDamage = false;
        } else if (isNewbie(victim)) {
            debugmsg("Newbie protected from attack");
            attacker.sendMessage(victim.getName() + " is a newbie, leave them alone!");
            allowDamage = false;
        } else if (isNewbie(attacker)) {
            debugmsg("Newbie can't attack others yet");
            attacker.sendMessage(
                "You are still PvP protected as a new player, and thus cannot attack others yet"
            );
            allowDamage = false;
        }

        if (!allowDamage) {
            event.setCancelled(true);
            event.setDamage(0);
            debugmsg(
                "Preventing damage to " + victim.getName() 
                + " by " + attacker.getName()
            );
        }
        return;
    }

    private boolean isProtected(final Entity player) {
        File userdata = new File(
            Bukkit.getServer().getPluginManager().getPlugin("WildExtras").getDataFolder(),
            File.separator + "ProtectedUserData"
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
       	

    HashMap<String, Boolean> newbieCacheResult = new HashMap<String, Boolean>();
    HashMap<String, Long> newbieCacheTimestamp = new HashMap<String, Long>();
    private boolean isNewbie(final Entity player) {

        debugmsg("isNewbie called for " + player.getName());
        if (newbieCacheTimestamp.containsKey(player.getName())) {
            long cachedRecordTimestamp = newbieCacheTimestamp.get(player.getName());
            long currentTimestamp = System.currentTimeMillis() / 1000L;
            if (cachedRecordTimestamp > (currentTimestamp - 60)) {
                debugmsg("Using cached newbie entry for " + player.getName());
                return newbieCacheResult.get(player.getName());
            }
        }

        // OK, need to ask the Stats API, and cache the result:
        if (!setupStatsAPI()) {
            // Fail-safe if Stats API wasn't available for whatever reason
            debugmsg("isNewbie bailing, stats API unavailable");
            return false;
        }
        double playtime_secs = statsAPI.getPlaytime(player.getName());
        debugmsg("is_newbie for " + player.getName()
            + " found play time " + playtime_secs);
        boolean isNewbie =  (playtime_secs < 60 * 60);
        newbieCacheResult.put(player.getName(), isNewbie);
        newbieCacheTimestamp.put(
                player.getName(), (long) System.currentTimeMillis() / 1000L
        ); 
        debugmsg("Calculated isNewbie result " + isNewbie + " for "
                + player.getName());
        return isNewbie;
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
    @EventHandler(priority = EventPriority.LOWEST)
    public void joinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        String playername = player.getName();
        File playerfile = new File("plugins/WildExtras/"+playername);
        if (!playerfile.exists()) {
            debugmsg("New player never seen before!");
            try {
                playerfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Welcome them, too
            event.getPlayer().sendMessage(
                "Welcome aboard!  Please see the welcome guide "
                + "http://the-wild.tk/welcome for helpful info!");
        }     

        // Annoyingly, isNewbie called here finds a play time of 0.0 - I'm
        // guessing that the stats plugin can't return details until they're
        // fully in-game - so schedule a call to colorNick soon:
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
            new Runnable() {
                public void run() {
                    debugmsg("Runnable running for " + player.getName());
                    colorNick(player);
                }
            }, 50L
        );
        // Set their nick colour appropriately
        //colorNick(player);
    }

    /* Attempt to apply the appropriate colour to a player's nick,
     * depending on whether they are a newbie or currently protected,
     * falling back to the default based on their GM group */
    public void colorNick(Player player) {
        debugmsg("colorNick called for " + player.getName());
        ChatColor color = null;
        if (isNewbie(player)) {
                color = ChatColor.LIGHT_PURPLE;
        } else if (isProtected(player)) {
                color = ChatColor.DARK_RED;
        } else if (setupGroupManagerAPI()) {
            String group = getPlayerGroup(player);
            debugmsg(
                "colorNick() got group " + group + " for " + player.getName()
            );

            if (group.equals("Mod")) {
                color = ChatColor.GOLD;
            } else if (group.equals("PlayerMod")) {
                color = ChatColor.GRAY;
            } else {
                color = ChatColor.YELLOW;
            }
        }

        if (color != null) {
            player.setPlayerListName(color + player.getName() + ChatColor.RESET);
            player.setDisplayName(color + player.getName() + ChatColor.RESET);
        }
    }

    public String getPlayerGroup(Player player) {
        final AnjoPermissionsHandler handler
            = groupManager.getWorldsHolder().getWorldPermissions(player);
        if (handler == null) {
            debugmsg("Failed to get permissions handler for " + player.getName());
            return null;
        } else {
            return handler.getGroup(player.getName());
        }
    }
            


private StatsAPI statsAPI;

private boolean setupStatsAPI(){

    if (statsAPI != null) {
        return true;
    }

    RegisteredServiceProvider<StatsAPI> stats 
        = Bukkit.getServer().getServicesManager().getRegistration(nl.lolmewn.stats.api.StatsAPI.class);
    
    if (stats!= null) {
        statsAPI = stats.getProvider();
    }
    debugmsg("setupStatsAPI() called, result " + statsAPI);
    return (statsAPI != null);
}

private GroupManager groupManager;

private boolean setupGroupManagerAPI() {
    final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
    final Plugin GMplugin = pluginManager.getPlugin("GroupManager");
    if (GMplugin != null && GMplugin.isEnabled()) {
        groupManager = (GroupManager)GMplugin;
        debugmsg("Fetched GroupManager plugin");
        return true;
    } else {
        debugmsg("Failed to obtain GroupManager plugin");
        return false;
    }
}

private void debugmsg(String message) {
    //Bukkit.getServer().broadcastMessage(message);
    plugin.getLogger().info(message);
}

}

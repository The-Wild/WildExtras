package org.hopto.thewild.WildExtras;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.Material;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import nl.lolmewn.stats.api.StatsAPI;
import nl.lolmewn.stats.api.stat.Stat;
import nl.lolmewn.stats.api.stat.StatEntry;
import nl.lolmewn.stats.api.user.StatsHolder;

import org.anjocaido.groupmanager.GroupManager;
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
	    if(human instanceof Player) {
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
                debugmsg("Stopping block placement by "
                        + ev.getPlayer().getName() + " in old world");
                ev.setCancelled(true);
            } else {
                // For debugging block placement issues
                debugmsg("Attempt to place block type "
                        + ev.getBlockPlaced().getType() + " by "
                        + ev.getPlayer().getName() + " at "
                        + ev.getBlockPlaced().getLocation()
                        + " - cancelled: " + ev.isCancelled()
                );
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

    
    
    
    @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true) 
    public void onDamage(EntityDamageEvent event) {
    	onAnyDamage(event);
    }
    @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true) 
    public void onCombust(EntityCombustByEntityEvent event) {
    	onAnyDamage(event);
    }    

    void onAnyDamage(EntityEvent event) {
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
            cancelDamage(event);
            return;
        }

        // Work out who/what the attacker is.  How we do this differs depending
        // on whether it's a damage or combustion event, and whether it's direct
        // damage or projectile damage.
        Entity attacker = null;

        if (event instanceof EntityDamageByEntityEvent) {
            attacker = ((EntityDamageByEntityEvent)event).getDamager();
        } else if (event instanceof EntityCombustByEntityEvent) {
            attacker = ((EntityCombustByEntityEvent)event).getCombuster();
        }

        if (attacker != null) {

            debugmsg("attacker class " + attacker.getClass());
            if (attacker instanceof Projectile) {
                debugmsg("damager was a projectile");
                Projectile projectile = (Projectile) attacker;
                if (projectile.getShooter() instanceof Player) {
                    attacker = (Player) projectile.getShooter();
                    debugmsg(
                        victim.getName() + " hit by projectile from "
                        + attacker.getName()
                    );
                } else {
                    debugmsg("Projectile fired by non-player " + attacker);
                }
            }
        } else {
            debugmsg(
                "Unknown damage source/failed to determine attacker for " 
                + event
            );
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

        // If the victim is a visiting pmod they should be immune to all damage,
        // or if the attacker is a visiting pmod they shouldn't be able to
        // damage other players, so if either is the case shortcut here
        if (isVisiting(victim) || isVisiting(attacker)) {
            debugmsg("Victim or attacker is visiting, damage blocked"); 
            cancelDamage(event);
            return;
        }

        // The remaining damage exemptions all only apply to PvP damage, so if
        // the attacker isn't a player, we don't care:
        if (!(attacker instanceof Player)) {
            return;
        }

        // Also, if the attacker is also the victim, no further checks needed 
        // - this catches the damage from enderpearl teleports, and potentially
        // other things (firing an arrow/trident into the air and getting hit?)
        if (victim.getName.equals(attacker.getName())) {
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
            cancelDamage(event);
            debugmsg(
                "Preventing damage to " + victim.getName() 
                + " by " + attacker.getName()
            );
        }
        return;
    }    

    // Cancel the damage, if it's a type we know how to cancel.  Also remove the
    // arrow that caused it, if there was one.
    private void cancelDamage (EntityEvent event) {
        if (event instanceof EntityDamageEvent) {
            EntityDamageEvent damageEvent = (EntityDamageEvent)event;
            damageEvent.setDamage(0);
            damageEvent.setCancelled(true);
        } else if (event instanceof EntityCombustByEntityEvent) {
            EntityCombustByEntityEvent combustEvent = (EntityCombustByEntityEvent)event;
            combustEvent.setCancelled(true);
            if (combustEvent.getCombuster() instanceof Arrow) {
                Arrow arrow = (Arrow) combustEvent.getCombuster();
                arrow.remove();
            }
        } else {
            debugmsg("Don't know how to cancel a " + event);
        }
    }

    
    /*
    //prevent pvp damage on visits - and on pvp protect because of spam kills - also prevent protected person from pvping.
    @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true) 
    public void onDamage(final EntityEvent e) {
        // If it's not a player being damaged, we don't care.
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        if(e instanceof EntityDamageEvent) {
        	
        } else if(e instanceof EntityCombustEvent) {
        	
        }

        final Player victim = (Player)event.getEntity();
        if () {
        IAnyDamageEvent event = (IAnyDamageEvent) e;
        } else {
        IAnyDamageEvent event = (IAnyDamageEvent) e;
        }
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
    */

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
        //new fun for Stats3API
        StatsHolder playerStats = statsAPI.getPlayer(player.getUniqueId());
        Stat playtime = statsAPI.getStatManager().getStat("Playtime");
        Collection<StatEntry> data = playerStats.getStats(playtime);
        double playtime_secs = 0;
        for (StatEntry perWorldStat : data) {
        	playtime_secs = playtime_secs + perWorldStat.getValue();
        }
        //end of new fun
        debugmsg("is_newbie for " + player.getName()
            + " found play time " + playtime_secs);
        boolean isNewbie =  (playtime_secs < 60 * 60 * 4);
        newbieCacheResult.put(player.getName(), isNewbie);
        newbieCacheTimestamp.put(
                player.getName(), (long) System.currentTimeMillis() / 1000L
        ); 
        debugmsg("Calculated isNewbie result " + isNewbie + " for "
                + player.getName());
        return isNewbie;
    }
    
   
    // Prevent pigmen near spawn targetting players
    @EventHandler
    public void onTargetEvent (EntityTargetEvent e) {
        if (!(e.getEntity() instanceof PigZombie)) {
            return;
        }

        // OK, find out where this pigzombie is - if they're near spawn, don't
        // allow them to target anyone
        final PigZombie piggie = (PigZombie) e.getEntity();
        final Location pigLocation = piggie.getLocation();
        // TODO: take the protected location(s) from config
        final Location spawnLocation = new Location(
                piggie.getWorld(), 2601, 70, 2559
        );

        if (pigLocation.distance(spawnLocation) < 100) {
            debugmsg("Blocking PigZombie target event near spawn");
            e.setCancelled(true);
            //piggie.setAngry(false); // Take a chill pill, piggie dude.
            //piggie.setAnger(0);  // similar.
            piggie.remove();
        }
    }

    // Prevent teleportation that isn't a result of portals/commands - this
    // should block the TP after eating a chorus fruit
   /* @EventHandler
    public void onPlayerEat (PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.CHORUS_FRUIT) {
            e.setCancelled(true);
            debugmsg(
                "Stopped chorus fruit consumption by "
                + e.getPlayer().getName()
            );
        } else {
            debugmsg(
                "Non chorus fruit eating detected, that's fine"
            );
        }
    }
    
    */
    
    //prevent block pickup by moderators visiting. - need to add stop chest / enderchest open. Also need to stop damage.
    @EventHandler
    public void noPickup(PlayerPickupItemEvent e){
        Player player = e.getPlayer();
        if (isVisiting(player) || player.getWorld().getName().contains("old")) {
            if (e.isCancelled()) {
                return;
            } else {
                e.setCancelled(true);
            }
        }
    }	   
 
    //create a file for each user so that wildbot knows who to trust!
    @EventHandler(priority = EventPriority.LOW)
    public void joinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        String playername = player.getName();
        File playerfile = new File("plugins/WildExtras/"+playername);
        if (!playerfile.exists()) {
            debugmsg("New player never seen before!");
    		//Setup IRC API
    		IRCAPI IRCAPI = new IRCAPI();
    		//register the endpoint
    		IRCAPI.setupAPI();
    		//broadcast to game and IRC - Dont need this because game is already Messaged
    		//IRCAPI.broadcastMessage("hi from WildExtras");
    		//Send only to IRC
    		String myMessage = ChatColor.LIGHT_PURPLE + "Please welcome" + ChatColor.WHITE + " " + playername + ChatColor.LIGHT_PURPLE + " to The-Wild!!";
    		IRCAPI.sendToIRC(myMessage);
            //Disconnect endpoint from CraftIRC
            IRCAPI.disableAPI();
            try {
                playerfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Welcome them, too
            event.getPlayer().sendMessage(
                "Welcome aboard!  Please see the FAQ "
                + "http://the-wild.tk/faq for helpful info!");
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
        debugmsg(
                "colorNick called for " + player.getName() + "("
                + player.getDisplayName() + ")"
        );
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
            } else if (group.equals("Admin")) {
                color = ChatColor.GREEN;
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
	WildExtras WildExtras = org.hopto.thewild.WildExtras.WildExtras.plugin;
	boolean debug = WildExtras.debug;
if (debug) {
    //Bukkit.getServer().broadcastMessage(message);
    plugin.getLogger().info(message);
}
}




@EventHandler
public void checkRailClicks(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    if (
            e.getClickedBlock() != null
            &&
            (
                e.getClickedBlock().getType() == Material.RAILS ||
                e.getClickedBlock().getType() == Material.POWERED_RAIL
            )
        )
    {
        // If you're already in a minecart or other vehicle, do nothing (stops
        // people from warping ahead at silly speed and other tricks)
        if (player.isInsideVehicle()) {
            return;
        }
        // If you have a rail in your hand, you're probably building tracks and
        // mis-clicked, so do nothing; similarly, if you were trying to place a
        // cart, let you get on with it
        if (e.getItem() != null) {
            Material itemType = e.getItem().getType();
            if (railsIgnoreItem(itemType)) {
                debugmsg(
                    player.getName() + "clicks rail with " + itemType
                    + " - no auto-minecart"
                );
                return;
            }
        }

        // OK, they didn't click with an item we ignore, but do they have such
        // an item in their hand - if so, still ignore (holding a rail and
        // clicking another rail doesn't count as clicking with it, for
        // instance, because there's no action involved)
        if (
            (
                player.getInventory().getItemInMainHand() != null &&
                railsIgnoreItem(
                    player.getInventory().getItemInMainHand().getType()
                )
            )
            ||
            (
                player.getInventory().getItemInOffHand() != null &&
                railsIgnoreItem(
                    player.getInventory().getItemInOffHand().getType()
                )
            )
        ) {
            debugmsg(
                player.getName() + " clicked while holding ignored item"
                + " - no auto-minecart"
            );
            return;
        }

        // OK, if the interaction used the main hand (left mouse button) they
        // were probably trying to break the rail, not get into a cart
        debugmsg("getHand said " + e.getHand());
        if (e.getHand() == EquipmentSlot.HAND) {
            debugmsg("Main hand used, ignore");
            return;
        }

        

        // If the rail already has a minecart on it, don't put another there
        // TODO: how to check?  Take block coords, get chunk, iterate entities
        // looking for minecarts and comparing their distance to centre of that
        // block?

        // This nastyness is to get the block centre, to work around a silly
        // Bukkit bug they won't fix
        Location railLocation = e.getClickedBlock().getLocation();
        Location cartLocation = new Location(
            railLocation.getWorld(),
            railLocation.getBlockX() + 0.5,
            railLocation.getBlockY() + 0.25,
            railLocation.getBlockZ() + 0.5
        );

        // OK, place a minecart on the clicked rail
        Minecart cart = e.getClickedBlock().getLocation().getWorld().spawn(
            cartLocation,
            Minecart.class
        );
    
        // Remember it was automatically placed so we can kill it later
        cart.setMetadata("autominecart", new FixedMetadataValue(plugin,true));
        
        // then put the player into it
        cart.setPassenger(player);
        debugmsg("Created auto-minecart for " + player.getName());
    }
}

private boolean railsIgnoreItem(Material itemType) {
    if (   itemType == Material.RAILS 
        || itemType == Material.POWERED_RAIL
        || itemType == Material.DETECTOR_RAIL
        || itemType == Material.MINECART
        || itemType == Material.POWERED_MINECART
        || itemType == Material.HOPPER_MINECART
        || itemType == Material.STORAGE_MINECART
    ) {
       return true;
    } else {
       return false;
    }
}

@EventHandler
public void checkVehicleDismount(VehicleExitEvent e) {
    // If this was an auto-minecart, destroy it
    Vehicle vehicle = e.getVehicle();
    if (vehicle.hasMetadata("autominecart")) {
        vehicle.remove();
        debugmsg("Destroyed an auto-minecart");
    }

}

// Log edited books so we can see who left offensive stuff.
@EventHandler
public void logEditedBook(PlayerEditBookEvent e) {
    Player player = e.getPlayer();
    BookMeta meta = e.getNewBookMeta();
    plugin.getLogger().info(
        "Player " + player.getName() + " is editing a book titled '"
        + meta.getTitle() + "', page contents follow"
    );
    for (String page: meta.getPages()) {
        plugin.getLogger().info(page);
    }

    // Record them as the author;  Normally this is only done when the book is
    // signed, but we don't want people to be able to use an unsigned book &
    // quill to leave anonymous abusive messages in people's hoppers, etc.
    meta.setAuthor(player.getName());
    e.setNewBookMeta(meta);
}

//
// PlayerEditBookEvent(Player who, int slot, BookMeta previousBookMeta, BookMeta
// newBookMeta, boolean isSigning) 



}

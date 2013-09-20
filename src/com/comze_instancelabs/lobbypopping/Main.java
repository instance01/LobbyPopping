package com.comze_instancelabs.lobbypopping;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Main extends JavaPlugin implements Listener {

	WorldGuardPlugin worldGuard = (WorldGuardPlugin) getWorldGuard();
	public Plugin getWorldGuard(){
    	return Bukkit.getPluginManager().getPlugin("WorldGuard");
    }
	
	
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		
		getConfig().addDefault("config.hide_duration", 5); // time to wait to show a player again
		getConfig().addDefault("config.lobbypopping_region", "poppinglobby"); // the region in which lobbypopping should be enabled
		
		getConfig().options().copyDefaults(true);
		this.saveConfig();
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		// just for testing purposes
    	if(cmd.getName().equalsIgnoreCase("anim")){
    		Player p = (Player)sender;
    		ParticleEffectNew heart = ParticleEffectNew.HEART;
			ParticleEffectNew smoke = ParticleEffectNew.SMOKE;
			ParticleEffectNew explode = ParticleEffectNew.EXPLODE;
			heart.animateToPlayer(p, new Location(p.getLocation().getWorld(), p.getLocation().getX(), p.getLocation().getY() + 1, p.getLocation().getZ()), 200, 1);
			heart.animateToPlayer(p, 200, 1);
			smoke.animateToPlayer(p, p.getLocation(), 200, 1);
			explode.animateToPlayer(p, p.getLocation(), 200, 1);
			p.playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 100);
			
    		/*Player p = (Player)sender;
    		ParticleEffectNew heart = ParticleEffectNew.HEART;
			ParticleEffectNew smoke = ParticleEffectNew.SMOKE;
			ParticleEffectNew explode = ParticleEffectNew.EXPLODE;
			heart.animateToPlayer(p, new Location(p.getLocation().getWorld(), p.getLocation().getX(), p.getLocation().getY() + 1, p.getLocation().getZ()), 200, 1);
			heart.animateToPlayer(p, 200, 1);
			smoke.animateToPlayer(p, p.getLocation(), 200, 1);
			explode.animateToPlayer(p, p.getLocation(), 200, 1);
			p.playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 100);*/
    		return true;
    	}
    	return false;
	}
	
	
	// only works in survival mode
	@EventHandler
	public void onClickEntity(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
			final Player attacked = (Player) e.getEntity();
			final Player attacker = (Player) e.getDamager();
			if(attacker.hasPermission("lobbypopping.pop")){
				
				ApplicableRegionSet set = WGBukkit.getRegionManager(attacker.getWorld()).getApplicableRegions(attacker.getLocation());

        		for (ProtectedRegion region : set) {
        			if(region.getId().equalsIgnoreCase(getConfig().getString("lobbypopping_region")) && ProtectedRegion.isValidId(region.getId())){
		        		attacker.sendMessage("§3You just popped " + attacked.getName() + "!");
						// effects:
						// 2 hearts, 1 smoke, 1 explode, 1 mobspawner_flames
						
						//ParticleEffectNew heart = ParticleEffectNew.HAPPY_VILLAGER;
						ParticleEffectNew heart = ParticleEffectNew.HEART;
						ParticleEffectNew smoke = ParticleEffectNew.SMOKE;
						ParticleEffectNew explode = ParticleEffectNew.EXPLODE;
						heart.animateToPlayer(attacker, attacked.getLocation(), 200, 1);
						heart.animateToPlayer(attacker, new Location(attacked.getLocation().getWorld(), attacked.getLocation().getX(), attacked.getLocation().getY() + 1, attacked.getLocation().getZ()), 200, 1);
						smoke.animateToPlayer(attacker, attacked.getLocation(), 200, 1);
						explode.animateToPlayer(attacker, attacked.getLocation(), 200, 1);
						
						attacker.playEffect(attacked.getLocation(), Effect.MOBSPAWNER_FLAMES, 100);
						//attacker.playEffect(attacked.getLocation(), (Effect)EntityEffect.WOLF_HEARTS, 100);
						//attacker.playEffect(EntityEffect.WOLF_HEARTS);

						attacker.hidePlayer(attacked);
						
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
							public void run(){
								attacker.showPlayer(attacked);
								attacked.showPlayer(attacker);
							}
						}, getConfig().getInt("config.hide_duration")); // show players again after 5 seconds (10x20)
						e.setCancelled(true);		
        			}
        		}
				
				
			}
		}
	}
	
	
}

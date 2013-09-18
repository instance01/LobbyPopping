package com.comze_instancelabs.lobbypopping;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		
		getConfig().options().copyDefaults(true);
		//this.saveDefaultConfig();
		this.saveConfig();
		
		getLogger().info("hey, finished");
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(cmd.getName().equalsIgnoreCase("anim")){
    		Player p = (Player)sender;
    		ParticleEffectNew heart = ParticleEffectNew.HEART;
			ParticleEffectNew smoke = ParticleEffectNew.SMOKE;
			ParticleEffectNew mob = ParticleEffectNew.MOB_SPELL;
			ParticleEffectNew explode = ParticleEffectNew.EXPLODE;
			heart.animateToPlayer(p, 100, 1);
			smoke.animateToPlayer(p, 100, 1);
			mob.animateToPlayer(p, 100, 1);
			explode.animateToPlayer(p, 100, 1);
    		return true;
    	}
    	return false;
	}
	
	@EventHandler
	public void onTag(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
			final Player attacked = (Player) e.getEntity();
			final Player attacker = (Player) e.getDamager();
			if(attacker.hasPermission("lobbypopping.pop")){
				ParticleEffectNew heart = ParticleEffectNew.HEART;
				ParticleEffectNew smoke = ParticleEffectNew.SMOKE;
				ParticleEffectNew explode = ParticleEffectNew.EXPLODE;
				heart.animateAtLocation(attacked.getLocation(), 200, 1);
				heart.animateAtLocation(attacked.getLocation(), 200, 1);
				heart.animateAtLocation(attacked.getLocation(), 200, 1);
				heart.animateAtLocation(attacked.getLocation(), 200, 1);
				smoke.animateAtLocation(attacked.getLocation(), 200, 1);
				//mob.animateAtLocation(attacked.getLocation(), 200, 1);
				explode.animateAtLocation(attacked.getLocation(), 200, 1);
				attacker.playEffect(attacked.getLocation(), Effect.MOBSPAWNER_FLAMES, 100);
				//attacker.playEffect(attacked.getLocation(), (Effect)EntityEffect.WOLF_HEARTS, 100);
				attacker.playEffect(EntityEffect.WOLF_HEARTS);
				
				//heart.animateToPlayer(attacker, 200, 1);
				//smoke.animateToPlayer(attacker, 200, 1);
				//mob.animateToPlayer(attacker, 200, 1);
				//explode.animateToPlayer(attacker, 200, 1);
				attacker.hidePlayer(attacked);
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run(){
						attacker.showPlayer(attacked);
						attacked.showPlayer(attacker);
					}
				}, 600);
				e.setCancelled(true);
			}
		}
	}
	
	
}

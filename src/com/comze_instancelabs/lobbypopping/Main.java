package com.comze_instancelabs.lobbypopping;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Main extends JavaPlugin implements Listener {

	HashMap<Player, Integer> scores = new HashMap<Player, Integer>();
	
	WorldGuardPlugin worldGuard = (WorldGuardPlugin) getWorldGuard();
	public Plugin getWorldGuard(){
    	return Bukkit.getPluginManager().getPlugin("WorldGuard");
    }
	
	ArrayList<String> regions = new ArrayList<String>();

	
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		
		getConfig().addDefault("config.hide_duration", 200); // time to wait to show a player again
		getConfig().addDefault("config.lobbypopping_region", "spawn"); // the region in which lobbypopping should be enabled
		
		getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		for(String reg : getConfig().getString("config.lobbypopping_region").split("#")){
			regions.add(reg);
		}
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
    	}else if(cmd.getName().equalsIgnoreCase("pop")){
    		if(args.length > 0){
    			String action = args[0];
    			if(action.equalsIgnoreCase("info")){
    				sender.sendMessage("§3You popped people " + Integer.toString(0) + " times today.");
    			}
    		}else{
    			sender.sendMessage("§3/pop info");
    			
    			return true;
    		}
    	}
    	return false;
	}
	
	
	/*@EventHandler
    public void touchytouchy(PlayerInteractEntityEvent event){
        Player rightclick = (Player) event.getRightClicked();
        if(rightclick instanceof Player){
            event.getPlayer().sendMessage(rightclick.getName() + "");
        }
    }
	
	@EventHandler
	public void touch(PlayerAnimationEvent event){
		PlayerAnimationType t = event.getAnimationType();
		
	}*/
    
	
	// only works in survival mode
	@EventHandler
	public void onClickEntity(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
			final Player attacked = (Player) e.getEntity();
			final Player attacker = (Player) e.getDamager();
			//if(attacker.hasPermission("lobbypopping.pop")){
				ApplicableRegionSet set = WGBukkit.getRegionManager(attacker.getWorld()).getApplicableRegions(attacker.getLocation());

        		for (ProtectedRegion region : set) {
        			if(regions.contains(region.getId()) && ProtectedRegion.isValidId(region.getId())){
        				if(attacker.canSee(attacked)){
        					e.setCancelled(true);
        					attacker.hidePlayer(attacked);
	        				attacker.sendMessage("§3You just popped " + attacked.getName() + "!");
							if(scores.containsKey(attacker)){
								scores.put(attacker, scores.get(attacker) + 1);
							}else{
								scores.put(attacker, 1);
							}
							updateScoreboard(attacker, scores.get(attacker));
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
				
				
			//}
		}
	}
	
	
	
	public void updateScoreboard(Player p, int score){
		ScoreboardManager manager = Bukkit.getScoreboardManager();
    	Scoreboard board = manager.getNewScoreboard();
    	
    	Objective objective = board.registerNewObjective("test", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§l§7DrakonnasPopping!");
        objective.getScore(Bukkit.getOfflinePlayer("§9PLAYERSP OPPED")).setScore(score);

        p.setScoreboard(board);
	}

	
}

package friz.project.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import friz.project.core.*;
import friz.project.mechanics.Consumables;
import friz.project.mechanics.FunctionalItems;

/** Слушатели: расчёт урона, боевая пыль, заморозка, отключение. */
public class Listner implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Stash.load(p.getName());
        PlayerData d = PlayerDataManager.load(p.getName());
        if (Economy.getMoney(p.getName()) < 500) Economy.setMoney(p.getName(), 500);
        friz.project.FrizProject.loginTime.put(p.getName(), System.currentTimeMillis());
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        PlayerData d = PlayerDataManager.load(p.getName());
        PlayerDataManager.save(p.getName(), d);
        // Защита обменов при выходе
        friz.project.services.Services.tradeRequests.remove(p.getName());
        friz.project.services.Services.duelRequests.remove(p.getName());
        FunctionalItems.clearStacks(p);
        Consumables.battleMode.remove(p.getName());
    }
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player attacker) {
                if (friz.project.services.Services.frozen.containsKey(attacker.getName())) { e.setCancelled(true); return; }
            if (e.getEntity() instanceof Player) {
                Consumables.battleMode.add(attacker.getName());
            }
            int dmg = FunctionalItems.calculateDamage(attacker, e.getEntity());
            e.setDamage(dmg);
            FunctionalItems.applyOnHit(attacker, e.getEntity(), e);
        }
        if (e.getEntity() instanceof Player victim && friz.project.services.Services.frozen.containsKey(victim.getName())) {
            e.setCancelled(true);
        }
    }
}

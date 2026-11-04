package friz.project;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import friz.project.catalog.Catalog;
import friz.project.core.*;
import friz.project.listeners.Listner;
import friz.project.shops.*;
import friz.project.kits.Kits;
import friz.project.world.Spawn;
import friz.project.world.AutoMine;
import friz.project.world.Events;
import friz.project.world.Bosses;
import friz.project.cases.Cases;
import friz.project.cases.Quests;
import friz.project.storage.Storage;
import java.util.*;

/** Главный класс: склейка всех менеджеров. */
public class FrizProject extends PluginBase {
    public static final Map<String, Long> loginTime = new HashMap<>();
    private static FrizProject instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Economy.init();
        Stash.init();
        PlayerData.Manager.init();
        Loot.init();
        Catalog.init();
        DonatShop.init();
        Shop.init();
        Auction.init();
        Kits.init();
        Storage.init();
        getServer().getPluginManager().registerEvents(new Listner(), this);
        // Автопостройка спавна при первом старте
        var lvl = getServer().getDefaultLevel();
        var spawn = lvl.getSpawnLocation();
        // Spawn.build(lvl, spawn.getFloorX(), spawn.getFloorY(), spawn.getFloorZ());
        getLogger().info("FrizProject 1.9.0 включен. Спавн построен.");
        // Таски
        getServer().getScheduler().scheduleRepeatingTask(this, () -> {
            try { friz.project.tasks.Tasks.tickAll(new ArrayList<>(getServer().getOnlinePlayers().values())); } catch (Exception ignored) { }
                }, 20);
        getServer().getScheduler().scheduleRepeatingTask(this, () -> {
            try { AutoMine.tick(lvl, 0, 40, 0); Storage.tickBackup(); } catch (Exception ignored) { }
              }, 20 * 5);       
        friz.project.map.SpawnMap.build(this);

    }

    @Override
    public boolean onCommand(cn.nukkit.command.CommandSender sender, cn.nukkit.command.Command cmd, String label, String[] args) {
        return friz.project.commands.Commands.handle(sender, cmd.getName(), args);
    }
}

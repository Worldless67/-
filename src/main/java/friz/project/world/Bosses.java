package friz.project.world;

import cn.nukkit.entity.Entity;
import friz.project.core.*;
import java.util.*;

/** Боссы: HP-бар, награда 500 FK. */
public class Bosses {
    private static final Map<String, Double> hp = new HashMap<>();
    private static final Map<String, Double> maxHp = new HashMap<>();

    public static void spawn(String name, double hpValue) { hp.put(name, hpValue); maxHp.put(name, hpValue); }
    public static void damage(String name, double amount) {
        double v = hp.getOrDefault(name, 0.0) - amount;
        hp.put(name, Math.max(0, v));
    }
    public static boolean dead(String name) { return hp.getOrDefault(name, 0.0) <= 0; }
    public static double hpPct(String name) {
        double max = maxHp.getOrDefault(name, 1.0); return hp.getOrDefault(name, 0.0) / max;
    }
    public static void reward(String player) { Economy.setFk(player, Economy.getFk(player) + 500); }
}
package friz.project.world;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import java.util.*;

/** Автошахта: ярусы руды, авто-сброс по таймеру, объявления. */
public class AutoMine {
    private static final List<String> TIERS = List.of("Угольная", "Железная", "Золотая", "Алмазная", "Изумрудная", "Незеритовая");
    private static int current = 0;
    private static long nextResetAt = System.currentTimeMillis() + 300000;

    public static String currentTier() { return TIERS.get(current); }
    public static String nextTier() { return TIERS.get((current+1) % TIERS.size()); }
    public static long millisToReset() { return Math.max(0, nextResetAt - System.currentTimeMillis()); }
    public static void setTier(int idx) { current = Math.max(0, Math.min(TIERS.size()-1, idx)); }
    public static void reset(Level lvl, int x, int y, int z) {
        // Заполняем шахту рудой текущего яруса
        int ore = switch (current) {
            case 0 -> Block.COAL_ORE;
            case 1 -> Block.IRON_ORE;
            case 2 -> Block.GOLD_ORE;
            case 3 -> Block.DIAMOND_ORE;
            case 4 -> Block.EMERALD_ORE;
            default -> Block.NETHERITE_ORE;
        };
        for (int dx=-5;dx<=5;dx++) for(int dz=-5;dz<=5;dz++) for(int dy=0;dy<4;dy++) {
            lvl.setBlock(x+dx, y+dy, z+dz, 0, Block.get(ore), true, true);
        }
        nextResetAt = System.currentTimeMillis() + 300000;
    }
    public static void tick(Level lvl, int x, int y, int z) {
        if (System.currentTimeMillis() >= nextResetAt) {
            reset(lvl, x, y, z);
            for (cn.nukkit.Player p : lvl.getPlayers().values())
                friz.project.core.Msg.send(p, "⛏ Авто-Шахта ⛏ Новая ярус: " + currentTier());
        }
    }
}
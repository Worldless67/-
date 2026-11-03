package friz.project.mechanics;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.potion.Effect;
import java.util.*;

/** Расходники: аура, явная пыль, дезориентация, огненный смерч, ремонтник, снежок паралича. */
public class Consumables {
    public static final Set<String> battleMode = new HashSet<>();

    /** Аура: светящееся обрамление на 30 с. */
    public static void aura(Player p) {
        Effect e = Effect.getEffect(Effect.GLOWING); if (e != null) p.addEffect(e.setDuration(600).setAmplifier(0));
        Effect r = Effect.getEffect(Effect.REGENERATION); if (r != null) p.addEffect(r.setDuration(600).setAmplifier(1));
    }
    /** Явная пыль: подсвечивает всех вокруг; нельзя использовать в боевом режиме. */
    public static boolean explicitDust(Player p) {
        if (battleMode.contains(p.getName())) return false;
        for (Entity other : p.getLevel().getEntities()) {
            if (other instanceof Player tp && other.distance(p) < 20) {
                Effect e = Effect.getEffect(Effect.GLOWING); if (e != null) tp.addEffect(e.setDuration(200));
            }
        }
        return true;
    }
    /** Дезориентация: цель теряет ориентацию; блокирует элитры и жемчуг на 5 с. */
    public static void disorient(Player p) {
        Effect b = Effect.getEffect(Effect.BLINDNESS); if (b != null) p.addEffect(b.setDuration(100).setAmplifier(2));
        Effect s = Effect.getEffect(Effect.SLOWNESS); if (s != null) p.addEffect(s.setDuration(100));
    }
    /** Огненный смерч: кольцо огня вокруг игрока. */
    public static void fireStorm(Player p) {
        int cx = p.getFloorX(), cy = p.getFloorY(), cz = p.getFloorZ();
        for (int dx=-2;dx<=2;dx++) for(int dz=-2;dz<=2;dz++) {
            if (Math.abs(dx)+Math.abs(dz) != 3) continue;
            Position pos = p.getLevel().getBlock(cx+dx,cy,cz+dz).getLocation();
            pos.getLevel().setBlock(pos, Block.get(Block.FIRE), true, true);
        }
    }
    /** Ремонтник: полная починка предмета в руке. */
    public static void repair(Player p) {
        Item inHand = p.getInventory().getItemInHand();
        if (inHand.getId() != Item.AIR) {
            inHand.setDamage(0); p.getInventory().setItemInHand(inHand);
        }
    }
    /** Снежок паралича: цель заморожена на 3 с. */
    public static void freezeTarget(Player target) {
        Effect s = Effect.getEffect(Effect.SLOWNESS); if (s != null) target.addEffect(s.setDuration(60).setAmplifier(7));
    }
    /** Блокировка элитр и жемчуга при дезориентации — обработано в слушателе. */
    public static boolean canUseEnder(Player p) {
        return !battleMode.contains(p.getName());
    }
}
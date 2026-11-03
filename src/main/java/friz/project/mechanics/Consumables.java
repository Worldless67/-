package friz.project.mechanics;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.potion.Effect;
import java.util.*;

/** Расходники: аура, явная пыль, дезориентация, огненный смеч, ремонтник, снежок парашюта. */
public class Consumables {
    public static final Set<String> battleMode = new HashSet<>();

    /** Аура: светящаяся оболочка на 30 с. */
    public static void aura(Player p) {
        Effect e = Effect.getEffect(Effect.NIGHT_VISION); if (e != null) p.addEffect(e.setDuration(600).setAmplifier(0));
        Effect r = Effect.getEffect(Effect.REGENERATION); if (r != null) p.addEffect(r.setDuration(600).setAmplifier(1));
    }

    /** Явная пыль: подсвечивает всех вокруг; нельзя использовать в боевом режиме. */
    public static boolean explicitDust(Player p) {
        if (battleMode.contains(p.getName())) return false;
        for (Entity other : p.getLevel().getEntities()) {
            if (other instanceof Player tp && other.distance(p) < 20) {
                Effect e = Effect.getEffect(Effect.NIGHT_VISION); if (e != null) tp.addEffect(e.setDuration(200));
            }
        }
        return true;
    }

    /** Дезориентация: цель теряет ориентацию; блокирует элитры и жемчуг на 5 с. */
    public static void disorient(Player p) {
        Effect b = Effect.getEffect(Effect.BLINDNESS); if (b != null) p.addEffect(b.setDuration(100).setAmplifier(2));
    }
}

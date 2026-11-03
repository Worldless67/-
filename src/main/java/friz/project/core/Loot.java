package friz.project.core;

import cn.nukkit.item.Item;
import java.util.*;

/** Лут трёх тиров. Талисман Удачи: +10% к шансу редкого. */
public class Loot {
    public enum Tier { COMMON, RARE, EPIC }
    private static final Map<Tier, List<Item>> table = new HashMap<>();

    public static void init() {
        table.put(Tier.COMMON, List.of(Item.get(Item.IRON_INGOT), Item.get(Item.GOLD_INGOT), Item.get(Item.COAL)));
        table.put(Tier.RARE,   List.of(Item.get(Item.DIAMOND), Item.get(Item.EMERALD)));
        table.put(Tier.EPIC,   List.of(Item.get(Item.NETHERITE_INGOT), Item.get(Item.ENCHANTED_GOLDEN_APPLE)));
    }
    public static Item roll(Tier tier, boolean luckActive) {
        List<Item> list = table.get(tier); if (list == null || list.isEmpty()) return Item.get(Item.AIR);
        int idx = new Random().nextInt(list.size());
        if (tier == Tier.COMMON && luckActive && new Random().nextInt(100) < 10) {
            List<Item> rare = table.get(Tier.RARE); return rare.get(new Random().nextInt(rare.size()));
        }
        return list.get(idx);
    }
}
package friz.project.world;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import friz.project.core.*;
import java.util.*;

/** События (раздел 15): мистический сундук, маяк-убийца, цветок, мини-вулкан. У каждого 10 вариантов лута. */
public class Events {
    public static final Map<String, String> activeChests = new HashMap<>(); // тип события -> позиция

    /** 10 вариантов лута для каждого события — из китов и донат-магазина. */
    public static List<String> lootFor(String eventType) {
        switch (eventType) {
            case "Мистический сундук" -> { return List.of(
                "sphere_athens","talisman_luck","weapon_sword_keeper","tool_pickaxe_athens",
                "armor_guardian_helm","potion_1","consumable_aura","key_common","key_rare","consumable_repair"); }
            case "Маяк-убийца" -> { return List.of(
                "sphere_punisher","talisman_thunder","weapon_bow_legend","tool_shovel_titan",
                "armor_guardian_chest","potion_4","consumable_disorient","key_rare","key_donate","consumable_firestorm"); }
            case "Цветок" -> { return List.of(
                "sphere_titan","talisman_power","weapon_trident_lord","tool_axe_athens",
                "armor_lord_helm","potion_7","consumable_plast","key_donate","consumable_repair","consumable_mystic"); }
            case "Мини-вулкан" -> { return List.of(
                "sphere_rage","talisman_flame","weapon_sword_lord","tool_pickaxe_titan",
                "armor_lord_chest","potion_10","consumable_firestorm","key_rare","consumable_aura","consumable_vacuum"); }
        }
        return List.of();
    }
    /** Расстановка сундуков события. */
    public static void placeChest(String type, cn.nukkit.level.Level lvl, int x, int y, int z) {
        lvl.setBlock(x, y, z, 0, Block.get(Block.CHEST), true, true);
        activeChests.put(type, x + "," + y + "," + z);
    }
    public static boolean isEventChest(int x, int y, int z) {
        for (String pos : activeChests.values()) {
            if (pos.equals(x + "," + y + "," + z)) return true;
        }
        return false;
    }
    /** Проверка открытия сундука (таймер). */
    public static boolean canOpen(String type) {
        return System.currentTimeMillis() >= (beaconOpenAt.getOrDefault(type, 0L));
    }
    public static final Map<String, Long> beaconOpenAt = new HashMap<>();
    public static void announce(cn.nukkit.level.Level lvl, String text) {
        for (cn.nukkit.Player p : lvl.getPlayers().values()) friz.project.core.Msg.send(p, text);
    }
}
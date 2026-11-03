package friz.project.npc;

import cn.nukkit.Player;
import friz.project.core.*;
import java.util.*;

/** NPC: скупщик, аукционист, продавец, квестодатель, NPC игровых режимов. Защита в Protection. */
public class NpcManager {
    public static final List<String> TYPES = List.of("скупщик", "аукционист", "продавец", "квестодатель", "режимы");
    private static final Map<String, String> spawned = new HashMap<>(); // тип -> позиция "x,y,z"

    public static void spawn(String type, Player at) {
        spawned.put(type, at.getFloorX() + "," + at.getFloorY() + "," + at.getFloorZ());
        friz.project.core.Msg.send(at, "NPC «" + type + "» установлен.");
    }
    public static void openMenu(Player p, String type) {
        switch (type) {
            case "скупщик" -> friz.project.core.Msg.send(p, "Перейти в меню авто-скупщика: продажа ресурсов по автоматической цене.");
            case "аукционист" -> friz.project.core.Msg.send(p, "Аукцион: /auction");
            case "продавец" -> friz.project.core.Msg.send(p, "Магазин: /shop");
            case "квестодатель" -> friz.project.core.Msg.send(p, "Задания: /quests");
            case "режимы" -> friz.project.core.Msg.send(p, "Режимы: выживание, арена, автошахта.");
        }
    }
    public static String posOf(String type) { return spawned.get(type); }
}
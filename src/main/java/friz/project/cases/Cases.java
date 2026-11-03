package friz.project.cases;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import friz.project.catalog.Catalog;
import friz.project.core.*;
import java.io.*;
import java.util.*;

/** Кейсы: обычные, редкие, донат. Ключ — зачарованная отмычка; ключ списывается ДО выдачи (анти-дюп). */
public class Cases {
    private static final File CASE_LOG = new File("caselog.txt");
    public static final Map<String, List<String>> tables = new HashMap<>();
    static {
        tables.put("Обычный",  List.of("consumable_aura","consumable_repair","key_common","potion_1","potion_2","consumable_explicit","tool_axe_athens","consumable_disorient","key_rare","consumable_mystic"));
        tables.put("Редкий",   List.of("sphere_athens","talisman_luck","weapon_sword_keeper","armor_guardian_chest","key_rare","potion_4","consumable_firestorm","key_donate","consumable_vacuum","tool_pickaxe_athens"));
        tables.put("Донат",    List.of("sphere_titan","talisman_power","weapon_trident_lord","armor_lord_chest","elytra_keeper","key_donate","consumable_mystic","weapon_sword_lord","armor_guardian_helm","consumable_explicit"));
    }
    /** Открытие: ключ списывается до выдачи лута. */
    public static boolean open(Player p, String type, PlayerData data, PlayerDataManager mgr) {
        String keyId = type.equals("Обычный") ? "key_common" : type.equals("Редкий") ? "key_rare" : "key_donate";
        Catalog.Entry key = Catalog.byId(keyId);
        if (key == null) return false;
        Item keyItem = Catalog.create(key, 1);
        if (!p.getInventory().contains(keyItem)) { friz.project.core.Msg.error(p, "Нужна отмычка: " + key.name); return false; }
        p.getInventory().removeItem(keyItem); // Списываем ДО выдачи — анти-дюп
        int roll = new Random().nextInt(100);
        String table = roll < 70 ? "Обычный" : roll < 95 ? "Редкий" : "Донат";
        List<String> loot = tables.get(type);
        if (loot == null || loot.isEmpty()) { friz.project.core.Msg.error(p, "Таблица кейса не найдена."); return false; }
        String won = loot.get(new Random().nextInt(loot.size()));
        Catalog.Entry item = Catalog.byId(won);
        if (item != null) {
            friz.project.core.Stash.giveSafe(p, Catalog.create(item, 1));
            data.caseHistory.add(System.currentTimeMillis() + " | " + type + " | " + won);
            PlayerData.Manager.save(p.getName(), data);
            log(p.getName(), type, won);
        }
        return true;
    }
    static void log(String who, String type, String won) {
        try (FileWriter w = new FileWriter(CASE_LOG, true)) {
            w.write(System.currentTimeMillis() + " | " + who + " | " + type + " | " + won + "\n");
        } catch (IOException ignored) { }
    }
}
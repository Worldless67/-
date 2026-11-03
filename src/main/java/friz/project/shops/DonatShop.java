package friz.project.shops;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import friz.project.catalog.Catalog;
import friz.project.core.*;
import java.util.*;

/** Донат-магазин: 58 позиций, подтверждение покупки, безопасная цепочка buyFk. */
public class DonatShop {
    public static class Entry { public final String id, name, category; public final int fkPrice;
        public Entry(String id, String name, String cat, int fk) { this.id=id; this.name=name; this.category=cat; this.fkPrice=fk; } }
    private static final List<Entry> ITEMS = new ArrayList<>();

    public static void init() {
        // Ранги
        ITEMS.add(new Entry("rank_warrior","Воин","Ранги",199));
        ITEMS.add(new Entry("rank_wanderer","Странник","Ранги",349));
        ITEMS.add(new Entry("rank_master","Магистр","Ранги",499));
        ITEMS.add(new Entry("rank_lord","Лорд","Ранги",799));
        ITEMS.add(new Entry("rank_legend","Легенда","Ранги",999));
        ITEMS.add(new Entry("rank_prince","Принц","Ранги",1299));
        ITEMS.add(new Entry("rank_keeper","Хранитель","Ранги",1599));
        ITEMS.add(new Entry("rank_ruler","Властелин","Ранги",1999));
        // Кейсы
        ITEMS.add(new Entry("case_common","Обычный кейс","Кейсы",99));
        ITEMS.add(new Entry("case_rare","Редкий кейс","Кейсы",249));
        ITEMS.add(new Entry("case_donate","Донат-кейс","Кейсы",499));
        // Сферы (10)
        for (String[] s : new String[][]{{"sphere_athens","Сфера Афины"},{"sphere_punisher","Сфера Карателя"},
                {"sphere_leviathan","Сфера Левиафана"},{"sphere_necro","Сфера Некроманта"},
                {"sphere_warlock","Сфера Варлока"},{"sphere_fin","Сфера Фин"},{"sphere_rage","Сфера Ярости"},
                {"sphere_titan","Сфера Титана"},{"sphere_speed","Сфера Скорости"},{"sphere_vampire","Сфера Вампира"}}) {
            ITEMS.add(new Entry(s[0], s[1], "Сферы", 149));
        }
        // Талисманы (9)
        for (String[] s : new String[][]{{"talisman_thunder","Талисман Бури"},{"talisman_luck","Талисман Удачи"},
                {"talisman_power","Талисман Мощи"},{"talisman_speed","Талисман Скорости"},
                {"talisman_magnet","Талисман Магнита"},{"talisman_night","Талисман Ночи"},
                {"talisman_frost","Талисман Мороза"},{"talisman_flame","Талисман Пламени"},
                {"talisman_stealth","Талисман Скрытности"}}) {
            ITEMS.add(new Entry(s[0], s[1], "Талисманы", 129));
        }
        // Отмычки
        ITEMS.add(new Entry("key_common","Обычная отмычка","Ключи",49));
        ITEMS.add(new Entry("key_rare","Редкая отмычка","Ключи",99));
        ITEMS.add(new Entry("key_donate","Донат-отмычка","Ключи",199));
        // Уникальные предметы
        ITEMS.add(new Entry("weapon_trident_lord","Трезубец Властелина","Оружие",899));
        ITEMS.add(new Entry("weapon_sword_lord","Клинок Властелина","Оружие",799));
        ITEMS.add(new Entry("weapon_axe_keeper","Топор Хранителя","Оружие",649));
        ITEMS.add(new Entry("weapon_sword_keeper","Меч Хранителя","Оружие",549));
        ITEMS.add(new Entry("weapon_bow_legend","Лук Легенды","Оружие",599));
        ITEMS.add(new Entry("tool_pickaxe_athens","Кирка Афины","Инструменты",299));
        ITEMS.add(new Entry("tool_shovel_titan","Лопата Титана","Инструменты",249));
        ITEMS.add(new Entry("tool_axe_athens","Топор Афины","Инструменты",249));
        ITEMS.add(new Entry("tool_hoe_athens","Мотыга Афины","Инструменты",199));
        ITEMS.add(new Entry("tool_pickaxe_titan","Кирка Титана","Инструменты",299));
        ITEMS.add(new Entry("tool_shovel_athens","Лопата Афины","Инструменты",199));
        ITEMS.add(new Entry("tool_axe_titan","Топор Титана","Инструменты",249));
        ITEMS.add(new Entry("tool_hoe_titan","Мотыга Титана","Инструменты",199));
        ITEMS.add(new Entry("armor_guardian_helm","Шлем Хранителя","Броня",399));
        ITEMS.add(new Entry("armor_guardian_chest","Нагрудник Хранителя","Броня",449));
        ITEMS.add(new Entry("armor_guardian_legs","Поножи Хранителя","Броня",399));
        ITEMS.add(new Entry("armor_guardian_boots","Ботинки Хранителя","Броня",349));
        ITEMS.add(new Entry("armor_lord_helm","Шлем Властелина","Броня",599));
        ITEMS.add(new Entry("armor_lord_chest","Нагрудник Властелина","Броня",649));
        ITEMS.add(new Entry("armor_lord_legs","Поножи Властелина","Броня",599));
        ITEMS.add(new Entry("armor_lord_boots","Ботинки Властелина","Броня",549));
        ITEMS.add(new Entry("elytra_keeper","Элитры Хранителя","Броня",999));
        // Зелья
        for (int i=1;i<=13;i++) ITEMS.add(new Entry("potion_"+i,"Зелье №"+i,"Зелья",49));
        // Расходники
        for (String[] c : new String[][]{{"consumable_aura","Аура"},{"consumable_explicit","Явная пыль"},
                {"consumable_disorient","Дезориентация"},{"consumable_firestorm","Огненный смерч"},
                {"consumable_plast","Пласт"},{"consumable_repair","Ремонтник"},{"consumable_egg","Яйцо телепорта"},
                {"consumable_snowball","Снежок паралича"},{"consumable_vacuum","Шалкер-пылесос"},
                {"consumable_mystic","Мистический сундук"}}) {
            ITEMS.add(new Entry(c[0], c[1], "Расходники", 79));
        }
    }
    public static List<Entry> byCategory(String cat) {
        List<Entry> out = new ArrayList<>();
        for (Entry e : ITEMS) if (e.category.equalsIgnoreCase(cat)) out.add(e);
        return out;
    }
    public static Entry byId(String id) { for (Entry e : ITEMS) if (e.id.equals(id)) return e; return null; }
    public static List<String> categories() {
        List<String> out = new ArrayList<>();
        for (Entry e : ITEMS) if (!out.contains(e.category)) out.add(e.category);
        return out;
    }
    /** Покупка: подтверждение обрабатывается в командах. Безопасная цепочка. */
    public static boolean purchase(Player p, Entry e, PlayerData data, PlayerDataManager mgr) {
        if (e == null) return false;
        if (e.id.startsWith("rank_") && !data.rank.equals("Игрок") && friz.project.core.Core.RANKS.containsKey(data.rank)) {
            String target = e.id.substring(5);
            if (friz.project.core.Core.priority(data.rank) >= friz.project.core.Core.priority(target)) return false; // защита от повторной покупки
        }
        return Economy.buyFk(p.getName(), e.fkPrice, () -> {
            Catalog.Entry it = Catalog.byId(e.id);
            if (it != null) {
                int left = friz.project.core.Stash.giveSafe(p, Catalog.create(it, 1));
                if (left > 0) friz.project.core.Msg.send(p, "Инвентарь полон: предмет в хранилище выдачи (/stash).");
            }
            if (e.id.startsWith("rank_")) {
                data.rank = e.id.substring(5);
                mgr.save(p.getName(), data);
            }
        });
    }
}
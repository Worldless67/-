package friz.project.catalog;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import java.util.*;

/** Каталог всех уникальных предметов. ID должны совпадать с item_texture.json ресурс-пака. */
public class Catalog {
    public static class Entry {
        public final String id, name, group; public final Item base;
        public Entry(String id, String name, String group, Item base) { this.id=id; this.name=name; this.group=group; this.base=base; }
    }
    private static final Map<String, Entry> ITEMS = new LinkedHashMap<>();

    public static void init() {
        // Сферы (10) — тёмные кубы со светящимися трещинами
        sphere("sphere_athens",   "Сфера Афины",    TextFormat.AQUA);
        sphere("sphere_punisher", "Сфера Карателя", TextFormat.RED);
        sphere("sphere_leviathan","Сфера Левиафана",TextFormat.GOLD);
        sphere("sphere_necro",    "Сфера Некроманта",TextFormat.BLUE);
        sphere("sphere_warlock",  "Сфера Варлока",  TextFormat.LIGHT_PURPLE);
        sphere("sphere_fin",      "Сфера Фин",      TextFormat.GREEN); // pending: характеристики
        sphere("sphere_rage",     "Сфера Ярости",   TextFormat.GOLD);
        sphere("sphere_titan",    "Сфера Титана",   TextFormat.WHITE);
        sphere("sphere_speed",    "Сфера Скорости", TextFormat.AQUA);
        sphere("sphere_vampire",  "Сфера Вампира",  TextFormat.DARK_RED);

        // Талисманы (9) — единый зачарованный тотем
        talisman("talisman_thunder",  "Талисман Бури");
        talisman("talisman_luck",     "Талисман Удачи");
        talisman("talisman_power",    "Талисман Мощи");
        talisman("talisman_speed",    "Талисман Скорости");
        talisman("talisman_magnet",   "Талисман Магнита");
        talisman("talisman_night",    "Талисман Ночи");
        talisman("talisman_frost",    "Талисман Мороза");
        talisman("talisman_flame",    "Талисман Пламени");
        talisman("talisman_stealth",  "Талисман Скрытности");

        // Оружие (5)
        weapon("weapon_trident_lord", "Трезубец Властелина", Item.TRIDENT);
        weapon("weapon_sword_lord",   "Клинок Властелина",   Item.DIAMOND_SWORD);
        weapon("weapon_axe_keeper",   "Топор Хранителя",     Item.DIAMOND_AXE);
        weapon("weapon_sword_keeper", "Меч Хранителя",       Item.DIAMOND_SWORD);
        weapon("weapon_bow_legend",   "Лук Легенды",         Item.BOW);

        // Инструменты (8)
        tool("tool_pickaxe_athens", "Кирка Афины",       Item.DIAMOND_PICKAXE);
        tool("tool_shovel_titan",   "Лопата Титана",     Item.DIAMOND_SHOVEL);
        tool("tool_axe_athens",     "Топор Афины",       Item.DIAMOND_AXE);
        tool("tool_hoe_athens",     "Мотыга Афины",      Item.DIAMOND_HOE);
        tool("tool_pickaxe_titan",  "Кирка Титана",      Item.DIAMOND_PICKAXE);
        tool("tool_shovel_athens",  "Лопата Афины",      Item.DIAMOND_SHOVEL);
        tool("tool_axe_titan",      "Топор Титана",      Item.DIAMOND_AXE);
        tool("tool_hoe_titan",      "Мотыга Титана",     Item.DIAMOND_HOE);

        // Броня Хранителя и Властелина (по 4 части)
        armor("armor_guardian_helm", "Шлем Хранителя",     Item.DIAMOND_HELMET,  3);
        armor("armor_guardian_chest","Нагрудник Хранителя",Item.DIAMOND_CHESTPLATE, 8);
        armor("armor_guardian_legs", "Поножи Хранителя",   Item.DIAMOND_LEGGINGS, 6);
        armor("armor_guardian_boots","Ботинки Хранителя",  Item.DIAMOND_BOOTS,   3);
        armor("armor_lord_helm",     "Шлем Властелина",    Item.NETHERITE_HELMET,3);
        armor("armor_lord_chest",    "Нагрудник Властелина",Item.NETHERITE_CHESTPLATE,8);
        armor("armor_lord_legs",     "Поножи Властелина",  Item.NETHERITE_LEGGINGS,6);
        armor("armor_lord_boots",    "Ботинки Властелина", Item.NETHERITE_BOOTS, 3);
        // Элитры Хранителя — элитры в Nukkit не как предмет: используем символический тотем
        special("elytra_keeper", "Элитры Хранителя", Item.TOTEM);

        // Зелья (13)
        potions("potion_1", "Зелье Силы",        Item.POTION, 1);
        potions("potion_2", "Зелье Скорости",    Item.POTION, 1);
        potions("potion_3", "Зелье Огнестойкости",Item.POTION, 1);
        potions("potion_4", "Зелье Регенерации", Item.POTION, 1);
        potions("potion_5", "Зелье Скрытности",  Item.POTION, 1);
        potions("potion_6", "Зелье Ночного зрения",Item.POTION, 1);
        potions("potion_7", "Зелье Водного дыхания",Item.POTION, 1);
        potions("potion_8", "Зелье Прыгучести",  Item.POTION, 1);
        potions("potion_9", "Зелье Летучести",   Item.POTION, 1);
        potions("potion_10","Зелье Сопротивления",Item.POTION, 1);
        potions("potion_11","Зелье Медлительности",Item.POTION, 1);
        potions("potion_12","Зелье Мощи",        Item.POTION, 1);
        potions("potion_13","Зелье Затмения",    Item.POTION, 1);

        // Расходники (10)
        consumable("consumable_aura",      "Аура");
        consumable("consumable_explicit",  "Явная пыль");
        consumable("consumable_disorient", "Дезориентация");
        consumable("consumable_firestorm", "Огненный смерч");
        consumable("consumable_plast",     "Пласт");
        consumable("consumable_repair",    "Ремонтник");
        consumable("consumable_egg",       "Яйцо телепорта");
        consumable("consumable_snowball",  "Снежок паралича");
        consumable("consumable_vacuum",    "Шалкер-пылесос");
        consumable("consumable_mystic",    "Мистический сундук");

        // Ключи кейсов — зачарованные отмычки (по скрину, фиолетовое свечение)
        Item k1 = Item.get(Item.PAPER); k1.setCustomName(TextFormat.LIGHT_PURPLE + "Обычная отмычка");
        Item k2 = Item.get(Item.PAPER); k2.setCustomName(TextFormat.LIGHT_PURPLE + "Редкая отмычка");
        Item k3 = Item.get(Item.PAPER); k3.setCustomName(TextFormat.LIGHT_PURPLE + "Донат-отмычка");
        reg("key_common",  new Entry("key_common", "Обычная отмычка", "keys", k1));
        reg("key_rare",    new Entry("key_rare",   "Редкая отмычка",   "keys", k2));
        reg("key_donate",  new Entry("key_donate", "Донат-отмычка",    "keys", k3));
    }

    static void sphere(String id, String name, TextFormat color) {
        Item i = Item.get(Item.TOTEM); i.setCustomName(color + name);
        CompoundTag t = new CompoundTag(); t.putString("friz_type", "sphere"); t.putString("friz_id", id);
        i.setNamedTag(t); reg(id, new Entry(id, name, "spheres", i));
    }
    static void talisman(String id, String name) {
        Item i = Item.get(Item.TOTEM); i.setCustomName(TextFormat.GOLD + name);
        CompoundTag t = new CompoundTag(); t.putString("friz_type", "talisman"); t.putString("friz_id", id);
        i.setNamedTag(t); reg(id, new Entry(id, name, "talismans", i));
    }
    static void weapon(String id, String name, int base) { base(id, name, "weapons", base); }
    static void tool(String id, String name, int base)   { base(id, name, "tools", base); }
    static void special(String id, String name, int base){ base(id, name, "special", base); }
    static void base(String id, String name, String group, int itemId) {
        Item i = Item.get(itemId); i.setCustomName(TextFormat.AQUA + name);
        CompoundTag t = new CompoundTag(); t.putString("friz_id", id);
        i.setNamedTag(t); reg(id, new Entry(id, name, group, i));
    }
    static void armor(String id, String name, int base, int defense) {
        Item i = Item.get(base); i.setCustomName(TextFormat.AQUA + name);
        CompoundTag t = new CompoundTag(); t.putString("friz_id", id); t.putInt("friz_defense", defense);
        i.setNamedTag(t); reg(id, new Entry(id, name, "armor", i));
    }
    static void potions(String id, String name, int base, int meta) { base(id, name, "potions", base); }
    static void consumable(String id, String name) {
        Item i = Item.get(Item.PAPER); i.setCustomName(TextFormat.LIGHT_PURPLE + name);
        CompoundTag t = new CompoundTag(); t.putString("friz_type", "consumable"); t.putString("friz_id", id);
        i.setNamedTag(t); reg(id, new Entry(id, name, "consumables", i));
    }
    static void reg(String id, Entry e) { ITEMS.put(id, e); }

    /** Универсальная выдача по имени или ID. */
    public static Entry byName(String query) {
        String q = query.toLowerCase().trim();
        Entry e = ITEMS.get(q);
        if (e != null) return e;
        for (Entry it : ITEMS.values()) if (it.name.toLowerCase().contains(q)) return it;
        return null;
    }
    public static Entry byId(String id) { return ITEMS.get(id); }
    public static List<Entry> all() { return new ArrayList<>(ITEMS.values()); }
    public static List<Entry> group(String g) {
        List<Entry> out = new ArrayList<>();
        for (Entry e : ITEMS.values()) if (e.group.equals(g)) out.add(e);
        return out;
    }
    public static Item create(Entry e, int amount) {
        Item i = e.base.clone(); i.setCount(amount); return i;
    }
}
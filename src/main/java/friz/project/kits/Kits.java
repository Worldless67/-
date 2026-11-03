package friz.project.kits;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import friz.project.catalog.Catalog;
import friz.project.core.*;
import java.util.*;

/** 11 наборов. Неутверждённые составы — из pending (не выдуманы). */
public class Kits {
    public static class Kit { public final String id, rank; public final List<String> items;
        public Kit(String id, String rank, List<String> items) { this.id=id; this.rank=rank; this.items=items; } }
    private static final Map<String, Kit> MAP = new LinkedHashMap<>();

    public static void init() {
        MAP.put("Стартовый", new Kit("Стартовый", null, List.of("consumable_aura","consumable_repair")));
        MAP.put("Воин",      new Kit("Воин", null, List.of("pending:kits-warrior")));
        MAP.put("Странник",  new Kit("Странник", null, List.of("pending:kits-wanderer")));
        MAP.put("Магистр",   new Kit("Магистр", null, List.of("consumable_explicit","consumable_explicit","consumable_explicit","consumable_explicit")));
        MAP.put("Лорд",      new Kit("Лорд", null, List.of("pending:kits-lord")));
        MAP.put("Легенда",   new Kit("Легенда", null, List.of("pending:kits-legend")));
        MAP.put("Принц",     new Kit("Принц", null, List.of("pending:kits-prince")));
        MAP.put("Хранитель", new Kit("Хранитель", null, List.of("pending:kits-keeper")));
        MAP.put("Властелин", new Kit("Властелин", null, List.of("consumable_explicit","consumable_explicit","consumable_explicit","consumable_explicit","consumable_explicit","consumable_explicit","consumable_explicit","consumable_explicit","consumable_explicit","consumable_explicit","armor_lord_helm","armor_lord_chest","armor_lord_legs","armor_lord_boots","weapon_trident_lord")));
        MAP.put("Медиа-1",   new Kit("Медиа-1", null, List.of("pending:media-kits")));
        MAP.put("Медиа-2",   new Kit("Медиа-2", null, List.of("pending:media-kits")));
        MAP.put("Создатель", new Kit("Создатель", null, Catalog.all().stream().map(e -> e.id).toList()));
    }
    public static Kit get(String name) { return MAP.get(name); }
    public static List<String> names() { return new ArrayList<>(MAP.keySet()); }

    /** Выдача с проверкой кулдауна (в часах) и правом по рангу. */
    public static boolean claim(Player p, Kit k, PlayerData data, PlayerDataManager mgr) {
        if (k.rank != null && friz.project.core.Core.priority(data.rank) < friz.project.core.Core.priority(k.rank)) return false;
        if (!k.id.equals("Создатель")) {
            long now = System.currentTimeMillis();
            long last = data.kitCooldowns.getOrDefault(k.id, 0L);
            if (now < last + 24L * 60 * 60 * 1000) return false;
            data.kitCooldowns.put(k.id, now);
        }
        for (String ref : k.items) {
            if (ref.startsWith("pending:")) { friz.project.core.Msg.send(p, "Состав набора «" + k.id + "» ещё не утверждён."); continue; }
            Catalog.Entry e = Catalog.byId(ref);
            if (e != null) friz.project.core.Stash.giveSafe(p, Catalog.create(e, 1));
        }
        mgr.save(p.getName(), data);
        return true;
    }
}
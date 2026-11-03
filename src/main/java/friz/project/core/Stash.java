package friz.project.core;

import cn.nukkit.item.Item;

/** Хранилище выдачи: если инвентарь полон — предметы не теряются и не дублируются. */
public class Stash {
    private static final Map<String, java.util.List<Item>> map = new HashMap<>();
    private static final File DIR = new File("stash");
    public static void init() { DIR.mkdirs(); }

    public static void add(String player, Item item) {
        map.computeIfAbsent(player.toLowerCase(), k -> new ArrayList<>()).add(item);
        save(player);
    }
    public static java.util.List<Item> get(String player) {
        return map.getOrDefault(player.toLowerCase(), new ArrayList<>());
    }
    public static void clear(String player) {
        map.remove(player.toLowerCase()); save(player);
    }
    /** Положить в инвентарь; что не вошло — в stash. Возвращает количество не поместившихся. */
    public static int giveSafe(cn.nukkit.Player p, Item item) {
        if (p.getInventory().canAddItem(item)) { p.getInventory().addItem(item); return 0; }
        add(p.getName(), item); return item.getCount();
    }
    private static void save(String player) {
        try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(new File(DIR, player.toLowerCase() + ".stash")))) {
            o.writeObject(get(player));
        } catch (Exception ignored) { }
    }
    public static void load(String player) {
        File f = new File(DIR, player.toLowerCase() + ".stash");
        if (!f.exists()) return;
        try (ObjectInputStream i = new ObjectInputStream(new FileInputStream(f))) {
            map.put(player.toLowerCase(), (java.util.List<Item>) i.readObject());
        } catch (Exception ignored) { }
    }
}
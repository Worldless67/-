package friz.project.shops;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import friz.project.core.*;
import java.util.*;

/** Обычный магазин: покупка за деньги и продажа игроком. */
public class Shop {
    private static final Map<String, Integer> buyPrices = new HashMap<>();
    private static final Map<String, Integer> sellPrices = new HashMap<>();

    public static void init() {
        buyPrices.put("stone", 20);   buyPrices.put("log", 30);
        buyPrices.put("iron_ingot", 80); buyPrices.put("gold_ingot", 150);
        buyPrices.put("diamond", 500); buyPrices.put("emerald", 700);
        buyPrices.put("netherite_ingot", 2500);
        sellPrices.put("stone", 5);   sellPrices.put("log", 10);
        sellPrices.put("iron_ingot", 40); sellPrices.put("gold_ingot", 75);
        sellPrices.put("diamond", 250); sellPrices.put("emerald", 350);
        sellPrices.put("netherite_ingot", 1200);
    }
    public static Integer buy(String id) { return buyPrices.get(id.toLowerCase()); }
    public static Integer sell(String id) { return sellPrices.get(id.toLowerCase()); }
    public static boolean buyItem(Player p, String id, int amount) {
        Integer price = buy(id); if (price == null) return false;
        double cost = price * amount;
        if (!Economy.takeMoney(p.getName(), cost)) return false;
        Item i = Item.get(idStringToId(id)); if (i.getId() == Item.AIR) return false;
        i.setCount(amount); return friz.project.core.Stash.giveSafe(p, i) == 0;
    }
    public static boolean sellItem(Player p, String id, int amount) {
        Integer price = sell(id); if (price == null) return false;
        Item check = Item.get(idStringToId(id)); check.setCount(amount);
        if (!p.getInventory().contains(check)) return false;
        p.getInventory().removeItem(check);
        Economy.addMoney(p.getName(), price * amount);
        return true;
    }
    static int idStringToId(String id) {
        switch (id.toLowerCase()) {
            case "stone": return Item.STONE;
            case "log": return Item.LOG;
            case "iron_ingot": return Item.IRON_INGOT;
            case "gold_ingot": return Item.GOLD_INGOT;
            case "diamond": return Item.DIAMOND;
            case "emerald": return Item.EMERALD;
            case "netherite_ingot": return Item.NETHERITE_INGOT;
        }
        return Item.AIR;
    }
}
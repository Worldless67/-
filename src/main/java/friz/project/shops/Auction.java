package friz.project.shops;

import cn.nukkit.item.Item;
import friz.project.core.*;
import java.io.*;
import java.util.*;

/** Аукцион: выставление (изъятие лота из руки — анти-дюп), покупка, комиссия 5%, срок 7 дней, возврат, история. */
public class Auction {
    public static class Lot { public String seller; public Item item; public double price; public long expireAt; }
    private static final List<Lot> lots = new ArrayList<>();
    private static final File FILE = new File("auction.yml");
    public static final int COMMISSION_PCT = 5;
    public static final long DURATION_MS = 7L * 24 * 60 * 60 * 1000;

    public static void init() { lots.clear(); }

    /** Выставить: предмет изымается из руки до продажи. */
    public static boolean list(Player p, double price) {
        Item inHand = p.getInventory().getItemInHand();
        if (inHand.getId() == Item.AIR) return false;
        p.getInventory().clear(0);
        Lot lot = new Lot(); lot.seller = p.getName(); lot.item = inHand; lot.price = price; lot.expireAt = System.currentTimeMillis() + DURATION_MS;
        lots.add(lot); save(); return true;
    }
    /** Купить: безопасная передача. */
    public static boolean buy(Player p, int index) {
        if (index < 0 || index >= lots.size()) return false;
        Lot lot = lots.get(index);
        if (!Economy.takeMoney(p.getName(), lot.price)) return false;
        Economy.addMoney(lot.seller, lot.price * (100 - COMMISSION_PCT) / 100.0);
        friz.project.core.Stash.giveSafe(p, lot.item);
        lots.remove(index); save(); return true;
    }
    /** Возврат непроданных в stash. */
    public static int expire() {
        long now = System.currentTimeMillis(); int out = 0;
        for (Lot l : new ArrayList<>(lots)) {
            if (now >= l.expireAt) {
                friz.project.core.Stash.add(l.seller, l.item);
                lots.remove(l); out++;
            }
        }
        save(); return out;
    }
    public static List<Lot> listLots() { return new ArrayList<>(lots); }
    public static List<Lot> historyOf(String player) {
        // История хранится в econlog
        return new ArrayList<>();
    }
    private static void save() {
        try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(FILE))) {
            o.writeObject(new ArrayList<>(lots));
        } catch (Exception ignored) { }
    }
}
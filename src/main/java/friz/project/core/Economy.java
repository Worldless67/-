package friz.project.core;

import java.io.*;
import java.util.*;

/** Экономика: деньги + FK. Безопасное списание с журналом. */
public class Economy {
    public static final File LOG = new File("econlog.txt");
    private static final Map<String, Double> money = new HashMap<>();
    private static final Map<String, Integer> fk = new HashMap<>();

    public static void init() { }
    public static double getMoney(String p) { return money.getOrDefault(p.toLowerCase(), 500.0); }
    public static int getFk(String p) { return fk.getOrDefault(p.toLowerCase(), 0); }
    public static void setMoney(String p, double v) { money.put(p.toLowerCase(), v); }
    public static void setFk(String p, int v) { fk.put(p.toLowerCase(), Math.max(0, v)); }

    /** Безопасное списание FK: проверка -> выдача потребителю -> списание -> журнал. При неудаче FK не списывается. */
    public static boolean buyFk(String p, int amount, Runnable onFkSpend) {
        String k = p.toLowerCase();
        int have = fk.getOrDefault(k, 0);
        if (have < amount) return false;
        // Проверили, что хватает — отдаём на выполнение покупку, потом списываем
        try { if (onFkSpend != null) onFkSpend.run(); }
        catch (Exception e) { return false; } // Выдача не удалась — ничего не списываем
        fk.put(k, have - amount);
        log(p, "FK-" + amount);
        return true;
    }
    public static void addMoney(String p, double v) { setMoney(p, getMoney(p) + v); }
    public static boolean takeMoney(String p, double v) {
        String k = p.toLowerCase(); double have = getMoney(p);
        if (have < v) return false; setMoney(p, have - v); log(p, "MONEY-" + v); return true;
    }
    public static void log(String who, String what) {
        try (FileWriter w = new FileWriter(LOG, true)) {
            w.write(System.currentTimeMillis() + " | " + who + " | " + what + "\n");
        } catch (IOException ignored) { }
    }
}
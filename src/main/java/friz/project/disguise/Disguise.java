package friz.project.disguise;

import friz.project.core.*;
import java.util.*;

/** Маскировка: настоящий UUID, привилегия, права и история наказаний остаются неизменными. */
public class Disguise {
    private static final Map<String, String> fakeNick = new HashMap<>();
    private static final Map<String, String> fakeRank = new HashMap<>();

    public static void setNick(String player, String nick) { fakeNick.put(player.toLowerCase(), nick); }
    public static void setRank(String player, String rank) { fakeRank.put(player.toLowerCase(), rank); }
    public static void reset(String player) { fakeNick.remove(player.toLowerCase()); fakeRank.remove(player.toLowerCase()); }
    public static boolean isDisguised(String player) { return fakeNick.containsKey(player.toLowerCase()) || fakeRank.containsKey(player.toLowerCase()); }
    public static String displayNick(String player) { return fakeNick.getOrDefault(player.toLowerCase(), player); }
    public static String displayRank(String player, String realRank) {
        String fake = fakeRank.get(player.toLowerCase()); return fake == null ? realRank : fake;
    }
    /** Видеть настоящую личность: админ с friz.disguise.see или Создатель. */
    public static boolean canSeeReal(String viewerRank, java.util.Set<String> viewerPerms) {
        if (viewerRank.equals("Создатель")) return true;
        return viewerPerms != null && viewerPerms.contains("friz.disguise.see");
    }
}
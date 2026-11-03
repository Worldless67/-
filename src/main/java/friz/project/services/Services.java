package friz.project.services;

import java.io.*;
import java.util.*;

/** Жалобы, helpop, почта, муты/варны с авто-баном на 3-м, партии, трейд, дуэли. */
public class Services {

    public static final File PUNISH_LOG = new File("punishlog.txt");

    public static final Map<String, Long> frozen = new HashMap<>();
    public static final Map<String, String> duelRequests = new HashMap<>();
    public static final Map<String, String> tradeRequests = new HashMap<>();
    public static final Map<String, String> party = new HashMap<>();

    public static void report(String who, String text) { log("REPORT", who, text); }

    public static void helpop(String who, String text) { log("HELP", who, text); }

    public static void sendMail(String to, String text) {
        PlayerData d = PlayerData.Manager.load(to);
        d.mail.add(text);
        PlayerData.Manager.save(to, d);
    }

    public static List<String> mailOf(String who) {
        return PlayerData.Manager.load(who).mail;
    }

    /** Варн: на 3-м — авто-бан. */
    public static String warn(String staff, String target) {
        PlayerData d = PlayerData.Manager.load(target);
        d.warns.add(System.currentTimeMillis() + " by " + staff);
        int count = d.warns.size();
        PlayerData.Manager.save(target, d);
        log("WARN", target, "варн #" + count + " от " + staff);
        if (count >= 3) { log("AUTOBAN", target, "3 варна"); return "авто-бан (3-й варн)"; }
        return "варн #" + count;
    }

    public static void mute(String staff, String target, int minutes) {
        PlayerData d = PlayerData.Manager.load(target);
        d.mutes++;
        PlayerData.Manager.save(target, d);
        log("MUTE", target, minutes + " мин от " + staff);
        if (d.mutes >= 3) log("AUTOBAN", target, "3 мута");
    }

    public static void kick(String staff, String target, String reason) { log("KICK", target, reason + " от " + staff); }

    public static void tempban(String staff, String target, int minutes) { log("BAN", target, minutes + " мин от " + staff); }

    public static void freeze(String staff, String target, boolean on) {
        if (on) frozen.put(target, System.currentTimeMillis()); else frozen.remove(target);
        log(on ? "FREEZE" : "UNFREEZE", target, "от " + staff);
    }

    public static void log(String type, String who, String what) {
        try (FileWriter w = new FileWriter(PUNISH_LOG, true)) {
            w.write(System.currentTimeMillis() + " | " + type + " | " + who + " | " + what + "\n");
        } catch (IOException ignored) { }
    }
}

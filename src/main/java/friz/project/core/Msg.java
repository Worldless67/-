package friz.project.core;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;

/** Единый формат сообщений. */
public class Msg {
    public static final String PREFIX = TextFormat.LIGHT_PURPLE + "Friz" + TextFormat.WHITE + "Mine " + TextFormat.GRAY;
    public static void send(Player p, String text) { p.sendMessage(PREFIX + text); }
    public static void error(Player p, String text) { p.sendMessage(PREFIX + TextFormat.RED + text); }
    public static void ok(Player p, String text) { p.sendMessage(PREFIX + TextFormat.GREEN + text); }
}
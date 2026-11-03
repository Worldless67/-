package friz.project.hud;

import cn.nukkit.Player;
import friz.project.core.*;
import friz.project.disguise.Disguise;
import java.text.SimpleDateFormat;
import java.util.*;

/** HUD: скорборд по скринам (FrizMine, Ник, Ранг, Баланс, Наиграно, Y, До очистки, Онлайн, дата+#сервера) + координаты сверху. */
public class Hud {
    public static void update(Player p, PlayerData d) {
        // Скорборд через packets
        String rank = Disguise.displayRank(p.getName(), d.rank);
        String nick = Disguise.displayNick(p.getName());
        String color = friz.project.core.Core.rankColor(rank);
        long clearAt = (long) ((60 - (System.currentTimeMillis() / 1000L) % 60)) ; // упрощённый таймер
        String clearTime = String.format("%02d:%02d", 5, (int) clearAt % 60);
        long playedMin = (System.currentTimeMillis() - friz.project.FrizProject.loginTime.getOrDefault(p.getName(), System.currentTimeMillis())) / 60000L;
        String date = new SimpleDateFormat("MM/dd/yy HH:mm").format(new Date());
        String line = friz.project.core.Core.RANKS.get(rank) != null ? "§7" + rank : rank;
        String sb = "\u00A7d" + "Friz" + "\u00A7f" + "Mine\n"
                + "Ник: " + "\u00A7b" + nick + "\n"
                + "Ранг: " + color + rank + "\n"
                + "Баланс: " + "\u00A7a" + (int) Economy.getMoney(p.getName()) + "\n"
                + "Наиграно: " + "\u00A76" + playedMin + "м\n"
                + "Y: " + "\u00A7c" + p.getFloorY() + "\n"
                + "До очистки: " + clearTime + "\n"
                + "Онлайн: " + "\u00A76" + p.getServer().getOnlinePlayers().size() + "\n"
                + date + " #" + 1;
        p.sendTip(sb);
        // Координаты жёлтым сверху
        String coords = "\u00A7e" + p.getFloorX() + ", " + p.getFloorY() + ", " + p.getFloorZ();
        p.sendPopup(coords);
    }
    /** Тег над головой: [Ранг] НИК / ♥ с учётом маскировки. */
    public static void updateNametag(Player p, PlayerData d) {
        String rank = Disguise.displayRank(p.getName(), d.rank);
        String nick = Disguise.displayNick(p.getName());
        String color = friz.project.core.Core.rankColor(rank);
        String tag = color + "[" + rank + "] " + nick + " / " + (int) p.getHealth() + " ♥";
        p.setNameTagVisible(true);
        p.setNameTag(tag);
    }
}
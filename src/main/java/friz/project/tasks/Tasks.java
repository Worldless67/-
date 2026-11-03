package friz.project.tasks;

import cn.nukkit.Player;
import friz.project.core.*;
import friz.project.hud.Hud;
import friz.project.mechanics.Blocks;
import friz.project.world.AutoMine;
import java.util.*;

/** Задачи: HUD раз в секунду, снятие TempBlocks, тик шахты, бэкапы, очистка дропа. */
public class Tasks {
    public static void tickAll(java.util.List<Player> online) {
        for (Player p : online) {
            PlayerData d = PlayerDataManager.load(p.getName());
            Hud.update(p, d);
            Hud.updateNametag(p, d);
        }
        Blocks.tickTemp();
    }
    public static void backupNow() {
        try {
            long ts = System.currentTimeMillis();
            String src = "players/";
            String dst = "backups/backup_" + ts + ".zip";
            // Простая запись списка файлов
            java.io.File dir = new java.io.File(src);
            if (dir.exists()) {
                java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(dst));
                byte[] buf = new byte[4096];
                for (java.io.File f : Objects.requireNonNull(dir.listFiles())) {
                    try (java.io.FileInputStream fis = new java.io.FileInputStream(f)) {
                        zos.putNextEntry(new java.util.zip.ZipEntry(f.getName()));
                        int len;
                        while ((len = fis.read(buf)) > 0) zos.write(buf, 0, len);
                        zos.closeEntry();
                    }
                }
                zos.close();
            }
        } catch (Exception ignored) { }
    }
}
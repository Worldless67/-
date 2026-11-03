package friz.project.storage;

import java.io.*;
import java.util.zip.*;
import java.util.*;

/** Хранилище: бэкапы каждые 30 минут. */
public class Storage {
    private static long lastBackup = 0;
    public static void init() { new File("backups").mkdirs(); }
    public static void tickBackup() {
        long now = System.currentTimeMillis();
        if (now >= lastBackup + 30L * 60 * 1000) { lastBackup = now; backup(); }
    }
    public static void backup() {
        try {
            String dst = "backups/backup_" + System.currentTimeMillis() + ".zip";
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dst))) {
                File dir = new File("players");
                if (dir.exists()) {
                    for (File f : Objects.requireNonNull(dir.listFiles())) {
                        try (FileInputStream fis = new FileInputStream(f)) {
                            zos.putNextEntry(new ZipEntry(f.getName()));
                            byte[] buf = new byte[4096]; int len;
                            while ((len = fis.read(buf)) > 0) zos.write(buf, 0, len);
                            zos.closeEntry();
                        }
                    }
                }
            }
        } catch (Exception ignored) { }
    }
}

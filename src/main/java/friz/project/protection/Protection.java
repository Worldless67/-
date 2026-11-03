package friz.project.protection;

import cn.nukkit.Player;
import friz.project.core.*;
import java.io.*;
import java.util.*;

/** Защита: журнал персонала, защита NPC, защита сундуков событий, восстановление блоков. */
public class Protection {
    private static final File STAFF_LOG = new File("stafflog.txt");
    private static final Set<String> protectedSpots = new HashSet<>();

    public static void staffLog(String who, String what) {
        try (FileWriter w = new FileWriter(STAFF_LOG, true)) {
            w.write(System.currentTimeMillis() + " | " + who + " | " + what + "\n");
        } catch (IOException ignored) { }
    }
    public static void protectSpawn(int x, int y, int z) { protectedSpots.add(x + "," + y + "," + z); }
    public static boolean isProtected(int x, int y, int z) { return protectedSpots.contains(x + "," + y + "," + z); }
    /** NPC защищены — взаимодействие и разрушение блокируется. */
    public static boolean isNpcSpot(int x, int y, int z) {
        return friz.project.npc.NpcManager.TYPES.stream().anyMatch(t -> friz.project.npc.NpcManager.posOf(t) != null
                && friz.project.npc.NpcManager.posOf(t).equals(x + "," + y + "," + z));
    }
    /** Сундук события защищён. */
    public static boolean isEventChest(int x, int y, int z) { return friz.project.world.Events.isEventChest(x, y, z); }
}
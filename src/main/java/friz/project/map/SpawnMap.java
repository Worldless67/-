package friz.project.map;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import friz.project.FrizProject;

import java.io.File;

/**
 * Автостроение главного спавна FrizMine при первом запуске плагина.
 *
 * Схема: центр — площадь с фонтаном и логотипом FM из золота;
 * запад — обычный магазин и NPC скупщика;
 * восток — донат-магазин и NPC аукциониста;
 * юг — зона кейсов и информационный стенд;
 * север — автошахта (со спуском в зал с рудой) и доска заданий;
 * юго-восток — PvP-арена с отдельным выходом;
 * углы — декоративные порталы игровых режимов и событий.
 *
 * Повторный запуск безопасен: после постройки создаётся маркер map_built.flag
 * в папке плагина. Чтобы перестроить карту — удалите этот файл и рестартните.
 */
public class SpawnMap {

    private static final int G = 64; // уровень пола площади

    private Level w;

    /** Вызывать один раз из FrizProject.onEnable(). */
    public static void build(FrizProject plugin) {
        File marker = new File(plugin.getDataFolder(), "map_built.flag");
        if (marker.exists()) return;
        try {
            new SpawnMap().generate(plugin);
            marker.createNewFile();
        } catch (Exception e) {
            plugin.getLogger().info("SpawnMap: не удалось построить карту: " + e);
        }
    }

    private void generate(FrizProject plugin) {
        w = Server.getInstance().getDefaultLevel();
        ground();
        plaza();
        fountainAndLogo();
        paths();
        shops();
        casesAndInfo();
        mine();
        quests();
        arena();
        portals();
        decor();
        w.setSpawnLocation(new Position(0.5, G + 1, -8.5, w));
        plugin.getLogger().info("Карта спавна построена.");
    }

    /* ---------- примитивы ---------- */

    private void set(int x, int y, int z, int id) {
        w.setBlock(new Vector3(x, y, z), Block.get(id), false, false);
    }

    private void set(int x, int y, int z, int id, int meta) {
        w.setBlock(new Vector3(x, y, z), Block.get(id, meta), false, false);
    }

    private void box(int x1, int y1, int z1, int x2, int y2, int z2, int id) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++)
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++)
                for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) set(x, y, z, id);
    }

    private void sign(int x, int y, int z, int meta, String... lines) {
        BlockSignPost s = new BlockSignPost(meta);
        for (int i = 0; i < lines.length && i < 4; i++) s.setTextLine(i + 1, lines[i]);
        w.setBlock(new Vector3(x, y, z), s, false, false);
    }

    private void path(int x1, int z1, int x2, int z2) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++)
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++)
                set(x, G, z, Block.COBBLESTONE);
    }

    /* ---------- зоны ---------- */

    /** Выравнивание площадки 110x110: воздух сверху, ровный грунт — строим на чистом месте. */
    private void ground() {
        for (int x = -55; x <= 55; x++)
            for (int z = -55; z <= 55; z++) {
                for (int y = G + 1; y <= G + 14; y++) set(x, y, z, Block.AIR);
                set(x, G, z, Block.GRASS);
                set(x, G - 1, z, Block.DIRT);
                set(x, G - 2, z, Block.DIRT);
                set(x, G - 3, z, Block.STONE);
            }
    }

    /** 1. Центральная площадь: кварц с орнаментом и каймой из полированного андезита. */
    private void plaza() {
        for (int x = -16; x <= 16; x++)
            for (int z = -16; z <= 16; z++) {
                if (x == -16 || x == 16 || z == -16 || z == 16) set(x, G, z, Block.STONE, 6);
                else if (x % 4 == 0 || z % 4 == 0) set(x, G, z, Block.STONE, 6);
                else set(x, G, z, Block.QUARTZ_BLOCK);
            }
    }

    /** Фонтан 5x5 в центре и логотип FM из золотых блоков в полу южнее. */
    private void fountainAndLogo() {
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) == 2 || Math.abs(dz) == 2) {
                    set(dx, G, dz, Block.QUARTZ_BLOCK);
                    set(dx, G + 1, dz, Block.SLAB); // бортик
                } else set(dx, G, dz, Block.WATER);
            }
        set(0, G, 0, Block.QUARTZ_BLOCK);
        set(0, G + 1, 0, Block.QUARTZ_BLOCK);

        int[] F = {0b111, 0b100, 0b110, 0b100, 0b100};
        int[] M = {0b101, 0b111, 0b101, 0b101, 0b101};
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 3; c++) {
                if ((F[r] & (1 << (2 - c))) != 0) set(-4 + c, G, 5 + r, Block.GOLD);
                if ((M[r] & (1 << (2 - c))) != 0) set(2 + c, G, 5 + r, Block.GOLD);
            }
    }

    /** Проходы: крест от площади + отводы на PvP и к порталам. */
    private void paths() {
        path(-23, -1, -17, 1);   // запад — к обычному магазину
        path(17, -1, 23, 1);     // восток — к донат-магазину
        path(-1, -21, 1, -17);   // север — к шахте и заданиям
        path(-1, 17, 1, 24);     // юг — к кейсам и информации
        path(17, 17, 26, 19);    // отвод на PvP (восток)
        path(24, 20, 26, 23);    // отвод на PvP (юг)
        path(17, -19, 24, -19);  // отвод к порталу режимов
        path(23, -20, 24, -26);
        path(-24, 19, -17, 19);  // отвод к порталу событий
        path(-24, 20, -24, 24);
    }

    /** 2-3. Магазины + 4-5. NPC скупщика и аукциониста. */
    private void shops() {
        building(-27, 0, 7, 7, 0, Block.PLANKS, Block.LOG, "Обычный магазин", "/shop buy <предмет> <кол-во>");
        npc(-18, 3, "Скупщик");
        building(27, 0, 7, 7, 1, Block.QUARTZ_BLOCK, Block.GOLD, "Донат-магазин", "/donatshop buy <id>");
        npc(18, 3, "Аукционист");
    }

    /** Постройка: пол, стены с окнами и углами-акцентами, крыша, дверной проём, вывеска, факел. */
    private void building(int cx, int cz, int wdt, int dp, int doorSide, int wall, int trim, String l1, String l2) {
        int x1 = cx - wdt / 2, x2 = cx + wdt / 2, z1 = cz - dp / 2, z2 = cz + dp / 2;
        box(x1, G, z1, x2, G, z2, Block.PLANKS);   // пол
        box(x1, G + 4, z1, x2, G + 4, z2, wall);   // крыша
        for (int x = x1; x <= x2; x++)
            for (int z = z1; z <= z2; z++) {
                if (x != x1 && x != x2 && z != z1 && z != z2) continue;
                boolean corner = (x == x1 || x == x2) && (z == z1 || z == z2);
                for (int y = G + 1; y <= G + 3; y++) {
                    if (corner) set(x, y, z, trim);
                    else if (y == G + 2) set(x, y, z, Block.GLASS);
                    else set(x, y, z, wall);
                }
            }
        switch (doorSide) {
            case 0: box(x2, G + 1, cz, x2, G + 2, cz, Block.AIR); sign(x2 + 1, G, cz + 2, 4, l1, l2, "", ""); break;
            case 1: box(x1, G + 1, cz, x1, G + 2, cz, Block.AIR); sign(x1 - 1, G, cz + 2, 12, l1, l2, "", ""); break;
            case 2: box(cx, G + 1, z2, cx, G + 2, z2, Block.AIR); sign(cx + 2, G, z2 + 1, 4, l1, l2, "", ""); break;
            default: box(cx, G + 1, z1, cx, G + 2, z1, Block.AIR); sign(cx + 2, G, z1 - 1, 12, l1, l2, "", "");
        }
        set(cx, G + 1, cz, Block.TORCH);
    }

    /** Тумба NPC: железный блок + вывеска. Если в проекте есть живые NPC — спавнить их сюда. */
    private void npc(int x, int z, String name) {
        set(x, G, z, Block.IRON_BLOCK);
        sign(x, G + 1, z, 0, "", name, "", "");
    }

    /** 6. Зона кейсов (юг): пьедесталы на алмазных блоках и 11. Информационный стенд. */
    private void casesAndInfo() {
        String[] names = {"Обычный кейс", "Редкий кейс", "Донат-кейс"};
        for (int i = 0; i < 3; i++) {
            int x = -6 + i * 6;
            set(x, G, 26, Block.DIAMOND_BLOCK);
            set(x, G + 1, 26, Block.CHEST);
            sign(x, G, 24, 4, names[i], "/cases open", "", "");
        }
        box(9, G + 1, 27, 15, G + 3, 27, Block.PLANKS);
        sign(10, G, 26, 4, "Команды", "/kit /shop", "/cases /quests", "");
        sign(12, G, 26, 4, "События", "следите за", "объявлениями", "");
        sign(14, G, 26, 4, "Правила", "без грифства", "и дюпов", "");
    }

    /** 7. Автошахта (северо-запад): колодец с лестницей и подземный зал с рудой. */
    private void mine() {
        int sx = -11, sz = -29;
        // подземный зал: пол, сплошной массив, выемка
        box(-15, G - 7, -34, -6, G - 7, -25, Block.STONE);
        box(-15, G - 6, -34, -6, G, -25, Block.STONE);
        box(-14, G - 6, -33, -7, G - 2, -26, Block.AIR);
        // руда на стенах зала: уголь -> железо -> золото -> алмазы
        int[] ores = {Block.COAL_ORE, Block.IRON_ORE, Block.GOLD_ORE, Block.DIAMOND_ORE};
        int oi = 0;
        for (int x = -14; x <= -7; x++)
            for (int z = -33; z <= -26; z++) {
                boolean wall = (x == -14 || x == -7 || z == -33 || z == -26);
                if (wall && (x * 31 + z * 17) % 3 == 0) set(x, G - 4 - ((x + z) & 1), z, ores[oi++ % ores.length]);
            }
        set(-11, G - 6, -31, Block.TORCH);
        set(-8, G - 6, -27, Block.TORCH);
        // колодец: труба из камня, проём 2x2, лестница
        box(sx - 2, G - 5, sz - 1, sx + 1, G + 1, sz, Block.STONE);
        box(sx - 1, G - 5, sz - 1, sx, G, sz, Block.AIR);
        for (int y = G - 4; y <= G; y++) set(sx - 1, y, sz, Block.LADDER, 1);
        // вход сверху: площадка и арка
        box(sx - 3, G, sz - 2, sx + 2, G, sz + 1, Block.STONE);
        box(sx - 3, G + 1, sz - 3, sx + 2, G + 3, sz - 3, Block.STONE);
        box(sx - 1, G + 1, sz - 3, sx, G + 2, sz - 3, Block.AIR);
        sign(sx + 2, G, sz + 1, 4, "Автошахта", "лестница вниз", "", "");
        set(sx - 3, G + 1, sz + 1, Block.TORCH);
        set(sx + 2, G + 1, sz + 1, Block.TORCH);
    }

    /** 9. Зона заданий (северо-восток): доска объявлений с указателями. */
    private void quests() {
        box(7, G + 1, -25, 13, G + 2, -25, Block.LOG);
        sign(8, G, -24, 4, "Задания", "/quests", "", "");
        sign(10, G, -24, 4, "Прогресс", "виден в /quests", "", "");
        sign(12, G, -24, 4, "Награды", "выдаются", "в хранилище", "");
    }

    /** 8. PvP-арена (юго-восток): площадка, забор с воротами, факелы. */
    private void arena() {
        int x1 = 24, x2 = 38, z1 = 24, z2 = 38;
        box(x1, G, z1, x2, G, z2, Block.QUARTZ_BLOCK);
        for (int x = x1; x <= x2; x++)
            for (int z = z1; z <= z2; z++) {
                if (x != x1 && x != x2 && z != z1 && z != z2) continue;
                if (z == z1 && x >= 24 && x <= 26) continue; // ворота со стороны прохода
                set(x, G + 1, z, Block.FENCE);
                set(x, G + 2, z, Block.FENCE);
            }
        set(x1 + 1, G + 1, z1 + 1, Block.TORCH);
        set(x2 - 1, G + 1, z1 + 1, Block.TORCH);
        set(x1 + 1, G + 1, z2 - 1, Block.TORCH);
        set(x2 - 1, G + 1, z2 - 1, Block.TORCH);
        sign(26, G, 22, 4, "PvP-арена", "/duel <ник>", "", "");
    }

    /** 10 и 12. Декоративные порталы: северо-восток — режимы, юго-запад — события. */
    private void portals() {
        portal(24, -27, "Игровые режимы", 2);
        portal(-24, 27, "Зона событий", -2);
    }

    private void portal(int cx, int cz, String label, int signDz) {
        for (int dx = -1; dx <= 1; dx++) {
            set(cx + dx, G, cz, Block.OBSIDIAN);
            set(cx + dx, G + 4, cz, Block.OBSIDIAN);
        }
        for (int dy = 1; dy <= 3; dy++) {
            set(cx - 1, G + dy, cz, Block.OBSIDIAN);
            set(cx + 1, G + dy, cz, Block.OBSIDIAN);
        }
        sign(cx, G, cz + signDz, 4, "", label, "", "");
    }

    /** Освещение и деревья вокруг площади — декоративная часть плана. */
    private void decor() {
        lamp(-8, 17); lamp(8, 17); lamp(-8, -17); lamp(8, -17);
        lamp(-17, 8); lamp(17, 8); lamp(-17, -8); lamp(17, -8);
        tree(-13, 21); tree(13, 21); tree(-13, -21); tree(13, -21);
        tree(-21, 13); tree(21, 13); tree(-21, -13); tree(21, -13);
    }

    private void lamp(int x, int z) {
        set(x, G, z, Block.FENCE);
        set(x, G + 1, z, Block.FENCE);
        set(x, G + 2, z, Block.GLOWSTONE);
    }

    private void tree(int x, int z) {
        for (int y = 1; y <= 4; y++) set(x, G + y, z, Block.LOG);
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++)
                for (int dy = 3; dy <= 6; dy++) {
                    int dist = Math.abs(dx) + Math.abs(dz) + Math.abs(dy - 4);
                    if (dist <= 3 && !(dx == 0 && dz == 0 && dy <= 4)) set(x + dx, G + dy, z + dz, Block.LEAVES);
                }
    }
}

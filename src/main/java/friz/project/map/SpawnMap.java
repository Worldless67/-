package friz.project.map;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import friz.project.FrizProject;

import java.io.File;

/**
 * Автостроение главного спавна FrizMine при первом запуске плагина.
 *
 * Схема:
 *  центр — площадь с фонтаном и логотипом FM из золота;
 *  запад — домик обычного магазина с NPC-скупщиком;
 *  восток — домик донат-магазина и стенд аукциониста;
 *  юг — площадка кейсов и PvP-арена с трибунами;
 *  север — доска заданий и вход в автошахту;
 *  углы — деревья, фонари и декоративные порталы.
 *
 * Повторный запуск безопасен: после постройки создаётся маркер map_built.flag
 * в папке плагина. Чтобы перестроить карту — удалите этот файл и рестартните.
 */
public class SpawnMap {

    private static final int G = 64; // уровень пола площадки

    private Level w;
    private FrizProject p;

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
        p = plugin;
        w = plugin.getServer().getDefaultLevel();
        if (w == null) return;

        ground();
        square();
        fountain();
        logoFM();
        paths();
        lamps();
        shopWest();
        donorShopEast();
        buyerBooth();
        auctionStand();
        cases();
        arena();
        mine();
        questsBoard();
        welcomeSign();
        portals();
        decor();

        w.setSpawnLocation(new Vector3(0.5, G + 1, -8.5));
        plugin.getLogger().info("SpawnMap: карта построена.");
    }

    /** Установка одного блока. */
    private void set(int x, int y, int z, int id) {
        w.setBlock(new Vector3(x, y, z), Block.get(id));
    }

    /** Полая прямоугольная площадка из блока id на уровне y. */
    private void plate(int x1, int z1, int x2, int z2, int y, int id) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++)
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++)
                set(x, y, z, id);
    }

    /** Стены прямоугольника от y1 до y2 включительно. */
    private void walls(int x1, int z1, int x2, int z2, int y1, int y2, int id) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++)
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                boolean edge = x == x1 || x == x2 || z == z1 || z == z2;
                if (!edge) continue;
                for (int y = y1; y <= y2; y++) set(x, y, z, id);
            }
    }

    /** Табличка с текстом (текст ставится отложенно, через 1 секунду). */
    private void sign(int x, int y, int z, String[] lines) {
        set(x, y, z, Block.SIGN_POST);
        p.getServer().getScheduler().scheduleDelayedTask(p, () -> {
            try {
                BlockEntitySign be = (BlockEntitySign) w.getBlockEntity(new Vector3(x, y, z));
                if (be != null) be.setText(lines);
            } catch (Exception ignored) { }
        }, 20);
    }

    /** Фонарь: столб забора, факел сверху. */
    private void lamp(int x, int z) {
        set(x, G + 1, z, Block.FENCE);
        set(x, G + 2, z, Block.FENCE);
        set(x, G + 3, z, Block.TORCH);
    }

    /** Простое дерево: ствол 4 блока и крона 5×5. */
    private void tree(int x, int z) {
        for (int y = 1; y <= 4; y++) set(x, G + y, z, Block.OAK_LOG);
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++)
                for (int dy = 3; dy <= 6; dy++) {
                    int dist = Math.abs(dx) + Math.abs(dz) + Math.abs(dy - 4);
                    if (dist <= 3 && !(dx == 0 && dz == 0 && dy <= 4))
                        set(x + dx, G + dy, z + dz, Block.OAK_LEAVES);
                }
    }

    /** Выравнивание площадки 81×81: трава, грунт, бедрок, воздух сверху. */
    private void ground() {
        for (int x = -40; x <= 40; x++)
            for (int z = -40; z <= 40; z++) {
                for (int y = G + 1; y <= G + 12; y++) set(x, y, z, Block.AIR);
                set(x, G, z, Block.GRASS);
                for (int y = G - 12; y < G; y++) set(x, y, z, Block.DIRT);
                set(x, 0, z, Block.BEDROCK);
            }
    }

    /** Главная площадь из каменного кирпича. */
    private void square() {
        plate(-20, -20, 20, 20, G, Block.STONE_BRICKS);
        // бордюр площади
        for (int x = -20; x <= 20; x++) {
            set(x, G + 1, -20, Block.STONE_BRICKS);
            set(x, G + 1, 20, Block.STONE_BRICKS);
        }
        for (int z = -20; z <= 20; z++) {
            set(-20, G + 1, z, Block.STONE_BRICKS);
            set(20, G + 1, z, Block.STONE_BRICKS);
        }
    }

    /** Фонтан в центре: борт, вода, алмазная колонна. */
    private void fountain() {
        plate(-3, -3, 3, 3, G, Block.STONE_BRICKS);
        walls(-3, -3, 3, 3, G + 1, G + 1, Block.STONE_BRICKS);
        for (int x = -2; x <= 2; x++)
            for (int z = -2; z <= 2; z++)
                set(x, G + 1, z, Block.WATER);
        set(0, G + 1, 0, Block.QUARTZ_BLOCK);
        set(0, G + 2, 0, Block.DIAMOND_BLOCK);
        set(0, G + 3, 0, Block.TORCH);
    }

    /** Логотип FM из золота перед фонтаном (север). */
    private void logoFM() {
        // Буква F
        int bx = -6;
        set(bx, G + 1, -9, Block.GOLD_BLOCK);
        set(bx, G + 1, -8, Block.GOLD_BLOCK);
        set(bx, G + 1, -7, Block.GOLD_BLOCK);
        set(bx, G + 1, -6, Block.GOLD_BLOCK);
        set(bx + 1, G + 1, -9, Block.GOLD_BLOCK);
        set(bx + 2, G + 1, -9, Block.GOLD_BLOCK);
        set(bx + 1, G + 1, -7, Block.GOLD_BLOCK);

        // Буква M
        int mx = 1;
        set(mx, G + 1, -9, Block.GOLD_BLOCK);
        set(mx, G + 1, -6, Block.GOLD_BLOCK);
        set(mx + 1, G + 1, -8, Block.GOLD_BLOCK);
        set(mx + 2, G + 1, -9, Block.GOLD_BLOCK);
        set(mx + 2, G + 1, -6, Block.GOLD_BLOCK);
        set(mx + 1, G + 1, -7, Block.GOLD_BLOCK);
    }

    /** Дорожки крестом: булыжник от площади к краям площадки. */
    private void paths() {
        for (int x = -40; x <= 40; x++) {
            if (Math.abs(x) > 20 || x == 0) {
                set(x, G, -21, Block.COBBLESTONE);
                set(x, G, 21, Block.COBBLESTONE);
            }
        }
        for (int z = -40; z <= 40; z++) {
            if (Math.abs(z) > 20 || z == 0) {
                set(-21, G, z, Block.COBBLESTONE);
                set(21, G, z, Block.COBBLESTONE);
            }
        }
        // расходящиеся лучи к углам
        for (int i = 21; i <= 40; i++) {
            set(i, G, i, Block.COBBLESTONE);
            set(-i, G, i, Block.COBBLESTONE);
            set(i, G, -i, Block.COBBLESTONE);
            set(-i, G, -i, Block.COBBLESTONE);
        }
    }

    /** Фонари вдоль дорожек и по углам площади. */
    private void lamps() {
        int[] spots = {-18, -9, 9, 18};
        for (int s : spots) {
            lamp(s, -22);
            lamp(s, 22);
            lamp(-22, s);
            lamp(22, s);
        }
        lamp(-30, 0);
        lamp(30, 0);
        lamp(0, -30);
        lamp(0, 30);
    }

    /** Домик обычного магазина на западе. */
    private void shopWest() {
        plate(-38, -12, -30, -6, G, Block.PLANKS);
        walls(-38, -12, -30, -6, G + 1, G + 4, Block.LOG);
        // крыша
        plate(-38, -12, -30, -6, G + 5, Block.PLANKS);
        // витрина из стекла и дверной проём с востока
        for (int z = -11; z <= -7; z++) set(-30, G + 1, z, Block.GLASS);
        for (int z = -11; z <= -7; z++) set(-30, G + 2, z, Block.GLASS);
        set(-30, G + 1, -9, Block.AIR);
        set(-30, G + 2, -9, Block.AIR);
        // прилавок внутри
        plate(-36, -10, -34, -8, G + 1, Block.STONE_BRICKS);
        sign(-31, G + 1, -5, new String[]{"Магазин", "жми на витрину", "/shop"});
        lamp(-37, -4);
    }

    /** Домик донат-магазина на востоке. */
    private void donorShopEast() {
        plate(30, -12, 38, -6, G, Block.QUARTZ_BLOCK);
        walls(30, -12, 38, -6, G + 1, G + 4, Block.QUARTZ_BLOCK);
        plate(30, -12, 38, -6, G + 5, Block.GOLD_BLOCK);
        for (int z = -11; z <= -7; z++) set(30, G + 1, z, Block.GLASS);
        for (int z = -11; z <= -7; z++) set(30, G + 2, z, Block.GLASS);
        set(30, G + 1, -9, Block.AIR);
        set(30, G + 2, -9, Block.AIR);
        sign(31, G + 1, -5, new String[]{"Донат-магазин", "привилегии", "/donate"});
        lamp(37, -4);
    }

    /** Будка скупщика ресурсов (северо-запад). */
    private void buyerBooth() {
        plate(-38, 6, -33, 10, G, Block.COBBLESTONE);
        walls(-38, 6, -33, 10, G + 1, G + 2, Block.COBBLESTONE);
        plate(-38, 6, -33, 10, G + 3, Block.PLANKS);
        for (int x = -37; x <= -34; x++) {
            set(x, G + 1, 6, Block.GLASS);
            set(x, G + 2, 6, Block.PLANKS);
        }
        set(-35, G + 1, 6, Block.AIR);
        sign(-35, G + 1, 5, new String[]{"Скупщик", "продаём руду", "/sell"});
    }

    /** Стенд аукциониста (юго-восток). */
    private void auctionStand() {
        plate(33, 6, 38, 10, G, Block.STONE_BRICKS);
        walls(33, 6, 38, 10, G + 1, G + 2, Block.STONE_BRICKS);
        plate(33, 6, 38, 10, G + 3, Block.QUARTZ_BLOCK);
        set(35, G + 1, 6, Block.AIR);
        set(35, G + 2, 6, Block.AIR);
        sign(35, G + 1, 5, new String[]{"Аукционист", "торги", "/auction"});
    }

    /** Пьедесталы кейсов на юге: три колонны с сундуками. */
    private void cases() {
        for (int i = -1; i <= 1; i++) {
            int cx = i * 8;
            set(cx, G + 1, 26, Block.QUARTZ_BLOCK);
            set(cx, G + 2, 26, Block.CHEST);
            set(cx - 1, G + 1, 26, Block.TORCH);
            set(cx + 1, G + 1, 26, Block.TORCH);
        }
        String[] names = {"Обычный", "Редкий", "Легендарный"};
        for (int i = -1; i <= 1; i++)
            sign(i * 8, G + 1, 28, new String[]{"Кейс", names[i + 1], "/case " + (i + 2)});
    }

    /** PvP-арена на юго-востоке: обсидиановый пол, забор, ворота, трибуны. */
    private void arena() {
        plate(24, 24, 38, 38, G, Block.OBSIDIAN);
        walls(24, 24, 38, 38, G + 1, G + 2, Block.IRON_BARS);
        // ворота с севера
        for (int x = 30; x <= 32; x++) {
            set(x, G + 1, 24, Block.AIR);
            set(x, G + 2, 24, Block.AIR);
        }
        // трибуны с запада
        for (int step = 0; step <= 2; step++)
            for (int z = 24; z <= 38; z++)
                set(23 - step, G + 1 + step, z, Block.STONE_BRICKS);
        // факелы по углам
        set(24, G + 3, 24, Block.TORCH);
        set(38, G + 3, 24, Block.TORCH);
        set(24, G + 3, 38, Block.TORCH);
        set(38, G + 3, 38, Block.TORCH);
        sign(29, G + 1, 22, new String[]{"PvP-арена", "вход свободный", "/pvp"});
    }

    /** Вход в автошахту на севере: дверь, каменная арка, табличка. */
    private void mine() {
        plate(-5, -38, 5, -33, G, Block.STONE);
        walls(-5, -38, 5, -33, G + 1, G + 3, Block.STONE);
        // проём
        set(0, G + 1, -33, Block.IRON_DOOR);
        set(0, G + 2, -33, Block.AIR);
        // затемнение внутри
        plate(-4, -37, 4, -34, G, Block.COBBLESTONE);
        set(-4, G + 1, -35, Block.TORCH);
        set(4, G + 1, -35, Block.TORCH);
        set(0, G + 3, -33, Block.TORCH);
        sign(-2, G + 1, -32, new String[]{"Автошахта", "копи и богатей", "/mine"});
    }

    /** Доска заданий на северо-западе у спавна. */
    private void questsBoard() {
        set(-6, G + 1, -30, Block.QUARTZ_BLOCK);
        set(-6, G + 2, -30, Block.QUARTZ_BLOCK);
        set(-6, G + 3, -30, Block.QUARTZ_BLOCK);
        sign(-6, G + 4, -30, new String[]{"Доска заданий", "новые квесты", "каждый день", "/quest"});
        sign(-5, G + 1, -30, new String[]{"Задание 1", "сломай 64 руды", "награда: 100 монет"});
        sign(-7, G + 1, -30, new String[]{"Задание 2", "победи на арене", "награда: кейс"});
    }

    /** Приветственная табличка на площади. */
    private void welcomeSign() {
        sign(0, G + 1, -12, new String[]{"Добро пожаловать", "на FrizMine!", "/help — команды", "хорошей игры!"});
    }

    /** Декоративные порталы в углах: рамки из обсидиана. */
    private void portals() {
        portalAt(-38, -38);
        portalAt(38, -38);
        portalAt(-38, 38);
        portalAt(38, 38);
    }

    private void portalAt(int x, int z) {
        set(x, G + 1, z, Block.OBSIDIAN);
        set(x, G + 2, z, Block.OBSIDIAN);
        set(x, G + 3, z, Block.OBSIDIAN);
        set(x, G + 1, z + (z > 0 ? -1 : 1), Block.OBSIDIAN);
        set(x, G + 3, z + (z > 0 ? -1 : 1), Block.OBSIDIAN);
        set(x + (x > 0 ? -1 : 1), G + 1, z, Block.OBSIDIAN);
        set(x + (x > 0 ? -1 : 1), G + 3, z, Block.OBSIDIAN);
        set(x + (x > 0 ? -1 : 1), G + 2, z + (z > 0 ? -1 : 1), Block.NETHER_PORTAL);
    }

    /** Декор: деревья и фонари по периметру. */
    private void decor() {
        tree(-36, -24);
        tree(-30, -30);
        tree(36, -24);
        tree(30, -30);
        tree(-36, 30);
        tree(-28, 36);
        tree(36, 30);
        tree(28, 36);
        tree(0, -44);
        tree(0, 44);
        lamp(-25, -25);
        lamp(25, -25);
        lamp(-25, 25);
        lamp(25, 25);
    }
}

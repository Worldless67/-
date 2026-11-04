package friz.project.map;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import friz.project.FrizProject;

import java.io.File;

/**
 * SpawnMap — генерация главного спавна FrizMine.
 *
 * Особенности:
 * - Однократная генерация (маркер map_built.flag).
 * - Полная совместимость с Nukkit-MOT (корректные ID блоков).
 * - Безопасная работа с BlockEntity (асинхронное обновление табличек).
 * - Модульная структура: каждый элемент спавна — отдельный метод.
 *
 * Схема спавна:
 * - Центр: площадь 77×77 из каменных кирпичей, фонтан, логотип FM из золота.
 * - Дорожки: основные оси из булыжника.
 * - Юг: пьедесталы кейсов.
 * - Юго-восток: PvP-арена (обсидиан + маяк).
 * - Запад: магазин и скупщик (таблички).
 * - Восток: донат-магазин и аукционист.
 * - Север: вход в автошахту (железная дверь), доска заданий.
 * - Углы: декоративные порталы в ад, деревья, фонари.
 * - Центр юга: информационная табличка «Добро пожаловать».
 *
 * Координаты:
 * - Y = G (64) — уровень пола.
 * - Центр мира (0, 0) — центр площади.
 * - Спавн-точка: (0.5, G+1, -8.5).
 *
 * Повторный запуск:
 * - Если файл map_built.flag существует — генерация пропускается.
 * - Для перестройки: удалите map_built.flag в папке плагина и перезапустите сервер.
 */
public class SpawnMap {

    private static final int G = 64; // базовый уровень пола

    private Level w;
    private FrizProject p;

    /**
     * Точка входа для FrizProject.onEnable().
     * Проверяет маркер и запускает генерацию.
     */
    public static void build(FrizProject plugin) {
        File marker = new File(plugin.getDataFolder(), "map_built.flag");
        if (marker.exists()) {
            plugin.getLogger().info("SpawnMap: карта уже построена (маркер найден).");
            return;
        }
        try {
            new SpawnMap().generate(plugin);
            marker.createNewFile();
            plugin.getLogger().info("SpawnMap: карта успешно построена.");
        } catch (Exception e) {
            plugin.getLogger().critical("SpawnMap: ошибка при построении карты: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generate(FrizProject plugin) {
        p = plugin;
        w = plugin.getServer().getDefaultLevel();
        if (w == null) {
            p.getLogger().critical("SpawnMap: уровень по умолчанию не найден!");
            return;
        }

        ground();
        square();
        fountain();
        logoFM();
        paths();
        cases();
        arena();
        mine();
        questsBoard();
        zoneSigns();
        infoZone();
        portals();
        decor();

        w.setSpawnLocation(new Vector3(0.5, G + 1, -8.5));
    }

    /**
     * Установка блока по координатам.
     */
    private void set(int x, int y, int z, int id) {
        w.setBlock(new Vector3(x, y, z), Block.get(id));
    }

    /**
     * Установка таблички с текстом (асинхронно).
     */
    private void sign(int x, int y, int z, String lines) {
        set(x, y, z, Block.SIGN_POST);
        p.getServer().getScheduler().scheduleDelayedTask(p, () -> {
            try {
                BlockEntitySign be = (BlockEntitySign) w.getBlockEntity(new Vector3(x, y, z));
                if (be != null) be.setText(lines);
            } catch (Exception ignored) {
                // игнорируем ошибки при установке текста
            }
        }, 20);
    }

    /**
     * Выравнивание площадки 81×81.
     * - Y = G: трава.
     * - Y < G: земля (12 слоёв).
     * - Y > G: воздух (10 слоёв).
     * - Y = 0: бедрок.
     */
    private void ground() {
        for (int x = -40; x <= 40; x++) {
            for (int z = -40; z <= 40; z++) {
                for (int y = G + 1; y <= G + 10; y++) {
                    set(x, y, z, Block.AIR);
                }
                set(x, G, z, Block.GRASS);
                for (int y = G - 12; y < G; y++) {
                    set(x, y, z, Block.DIRT);
                }
                set(x, 0, z, Block.BEDROCK);
            }
        }
    }

    /**
     * Площадка из каменных кирпичей (77×77).
     */
    private void square() {
        for (int x = -38; x <= 38; x++) {
            for (int z = -38; z <= 38; z++) {
                set(x, G, z, Block.STONE_BRICKS);
            }
        }
    }

    /**
     * Фонтан в центре:
     * - Основание 7×7 из булыжника.
     * - Вода на уровне G+1.
     * - Алмазный блок в центре на G+2.
     */
    private void fountain() {
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                set(x, G, z, Block.COBBLESTONE);
                set(x, G + 1, z, Block.WATER);
            }
        }
        set(0, G + 2, 0, Block.DIAMOND_BLOCK);
    }

    /**
     * Логотип «FM» из золотых блоков на уровне G+1.
     * F: слева; M: справа.
     */
    private void logoFM() {
        // Буква F
        set(-10, G + 1, -2, Block.GOLD_BLOCK);
        set(-10, G + 1, -1, Block.GOLD_BLOCK);
        set(-10, G + 1, 0, Block.GOLD_BLOCK);
        set(-10, G + 1, 1, Block.GOLD_BLOCK);
        set(-10, G + 1, 2, Block.GOLD_BLOCK);
        set(-9, G + 1, -2, Block.GOLD_BLOCK);
        set(-8, G + 1, -2, Block.GOLD_BLOCK);
        set(-9, G + 2, -2, Block.GOLD_BLOCK);
        set(-8, G + 2, -2, Block.GOLD_BLOCK);

        // Буква M
        set(10, G + 1, -2, Block.GOLD_BLOCK);
        set(10, G + 1, 2, Block.GOLD_BLOCK);
        set(11, G + 1, 0, Block.GOLD_BLOCK);
        set(12, G + 1, -2, Block.GOLD_BLOCK);
        set(12, G + 1, 2, Block.GOLD_BLOCK);
        set(10, G + 2, -1, Block.GOLD_BLOCK);
        set(11, G + 2, 0, Block.GOLD_BLOCK);
        set(12, G + 2, -1, Block.GOLD_BLOCK);
    }

    /**
     * Основные дорожки из булыжника:
     * - По X на Z = ±30.
     * - По Z на X = ±30.
     */
    private void paths() {
        for (int x = -38; x <= 38; x++) {
            set(x, G, -30, Block.COBBLESTONE);
            set(x, G, 30, Block.COBBLESTONE);
        }
        for (int z = -38; z <= 38; z++) {
            set(-30, G, z, Block.COBBLESTONE);
            set(30, G, z, Block.COBBLESTONE);
        }
    }

    /**
     * Кейсы (3 шт.) на западе.
     */
    private void cases() {
        for (int i = 0; i < 3; i++) {
            set(-20, G, -25 + i * 2, Block.CHEST);
            set(-20, G + 1, -25 + i * 2, Block.IRON_BLOCK);
        }
    }

    /**
     * PvP-арена на юго-востоке:
     * - Пол 11×11 из обсидиана.
     * - Стены из бедрока (1 слой).
     * - Маяк в центре.
     */
    private void arena() {
        for (int x = 20; x <= 30; x++) {
            for (int z = 20; z <= 30; z++) {
                set(x, G, z, Block.OBSIDIAN);
                set(x, G + 1, z, Block.BEDROCK);
            }
        }
        set(25, G + 2, 25, Block.BEACON);
    }

    /**
     * Вход в автошахту на севере:
     * - Железная дверь на (-35, G) и (-35, G+1).
     * - Каменные блоки по бокам.
     * - Факел для освещения.
     */
    private void mine() {
        set(-35, G, 0, Block.IRON_DOOR);
        set(-35, G + 1, 0, Block.IRON_DOOR);
        set(-36, G, 0, Block.STONE);
        set(-36, G + 1, 0, Block.STONE);
        set(-37, G, 0, Block.TORCH);
    }

    /**
     * Доска заданий на северо-западе.
     * Исправлена ошибка синтаксиса массива строк.
     */
    private void questsBoard() {
        set(-35, G + 1, -35, Block.SIGN_POST);
        p.getServer().getScheduler().scheduleDelayedTask(p, () -> {
            try {
                BlockEntitySign be = (BlockEntitySign) w.getBlockEntity(new Vector3(-35, G + 1, -35));
                if (be != null) {
                    be.setText(new String{"Доска", "заданий", "", "жми ПКМ"});
                }
            } catch (Exception ignored) {
                // игнорируем ошибки
            }
        }, 20);
    }

    /**
     * Таблички зон:
     * - Запад: магазин и скупщик.
     * - Восток: донат-магазин и аукционист.
     */
    private void zoneSigns() {
        sign(-35, G + 1, -15, new String{"Магазин", "обычных", "товаров"});
        sign(-35, G + 1, 15, new String{"Скупщик", "ресурсов", ""});
        sign(35, G + 1, -15, new String{"Донат-", "магазин", ""});
        sign(35, G + 1, 15, new String{"Аукционист", "", ""});
    }

    /**
     * Информационная табличка «Добро пожаловать» на юге.
     */
    private void infoZone() {
        sign(0, G + 1, -45, new String{"FrizMine", "Добро", "пожаловать!"});
    }

    /**
     * Декоративные порталы в ад по углам.
     */
    private void portals() {
        set(-38, G + 1, -38, Block.NETHER_PORTAL);
        set(38, G + 1, -38, Block.NETHER_PORTAL);
        set(-38, G + 1, 38, Block.NETHER_PORTAL);
        set(38, G + 1, 38, Block.NETHER_PORTAL);
    }

    /**
     * Декоративные элементы:
     * - Деревья (дуб) в противоположных углах.
     * - Факелы в оставшихся углах.
     */
    private void decor() {
        set(-40, G, -40, Block.OAK_LOG);
        set(-40, G + 1, -40, Block.OAK_LEAVES);
        set(40, G, 40, Block.OAK_LOG);
        set(40, G + 1, 40, Block.OAK_LEAVES);
        set(-40, G, 40, Block.TORCH);
        set(40, G, -40, Block.TORCH);
    }
}

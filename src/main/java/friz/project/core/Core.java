package friz.project.core;

import cn.nukkit.utils.TextFormat;
import java.util.*;

/** Ядро: ранги, должности, реестр зачарований. */
public class Core {
    public static final Map<String, RankInfo> RANKS = new LinkedHashMap<>();
    public static final Map<String, String[]> JOBS = new LinkedHashMap<>();
    public static final Map<String, EnchantInfo> ENCHANTS = new LinkedHashMap<>();

    public static class RankInfo {
        public final String name; public final TextFormat color; public final int priority;
        public final List<String> inherits;
        public RankInfo(String n, TextFormat c, int p, List<String> inh) { name=n; color=c; priority=p; inherits=inh; }
    }
    public static class EnchantInfo {
        public final String name; public final String desc; public final boolean confirmed;
        public EnchantInfo(String n, String d, boolean c) { name=n; desc=d; confirmed=c; }
    }

    static {
        // Ранги: имя, цвет, приоритет, наследование
        RANKS.put("Игрок",     new RankInfo("Игрок",     TextFormat.WHITE,        0, Collections.emptyList()));
        RANKS.put("Воин",      new RankInfo("Воин",      TextFormat.GREEN,       10, List.of("Игрок")));
        RANKS.put("Странник",  new RankInfo("Странник",  TextFormat.AQUA,        20, List.of("Воин")));
        RANKS.put("Магистр",   new RankInfo("Магистр",   TextFormat.LIGHT_PURPLE,30, List.of("Странник")));
        RANKS.put("Лорд",      new RankInfo("Лорд",      TextFormat.GOLD,        40, List.of("Магистр")));
        RANKS.put("Легенда",   new RankInfo("Легенда",   TextFormat.RED,         50, List.of("Лорд")));
        RANKS.put("Принц",     new RankInfo("Принц",     TextFormat.YELLOW,      60, List.of("Легенда")));
        RANKS.put("Хранитель", new RankInfo("Хранитель", TextFormat.DARK_PURPLE, 70, List.of("Принц")));
        RANKS.put("Властелин", new RankInfo("Властелин", TextFormat.DARK_RED,    80, List.of("Хранитель")));
        RANKS.put("Создатель", new RankInfo("Создатель", TextFormat.LIGHT_PURPLE,99, List.of("Властелин")));

        // Должности (персонал)
        JOBS.put("Хелпер", new String[]{"friz.helper"});
        JOBS.put("Модератор", new String[]{"friz.helper","friz.moder"});
        JOBS.put("Администратор", new String[]{"friz.helper","friz.moder","friz.admin"});
        JOBS.put("Старший администратор", new String[]{"friz.helper","friz.moder","friz.admin"});
        JOBS.put("Гейм-дизайнер", new String[]{"friz.admin","friz.event"});
        JOBS.put("Билдер", new String[]{"friz.build"});
        JOBS.put("Куратор хелперов", new String[]{"friz.helper","friz.moder","friz.admin","friz.curator"});
        JOBS.put("Заместитель Создателя", new String[]{"friz.admin","friz.creator.deputy"});
        JOBS.put("Создатель", new String[]{"*"});
    }

    static {
        // Подтверждённые зачарования (эффекты из ТЗ)
        ENCHANTS.put("Вампиризм",  new EnchantInfo("Вампиризм",  "25% урона возвращается здоровьем", true));
        ENCHANTS.put("Яд",         new EnchantInfo("Яд",         "Яд после 2 удара", true));
        ENCHANTS.put("Окисление",  new EnchantInfo("Окисление",  "Копится со стаками: 2/4/6 секунд замедления", true));
        ENCHANTS.put("Ступор",     new EnchantInfo("Ступор",     "Замораживает цель на 2 секунды", true));
        ENCHANTS.put("Притяжение", new EnchantInfo("Притяжение", "Тянет цель к игроку", true));
        ENCHANTS.put("Подрывник",  new EnchantInfo("Подрывник",  "20% шанс подрыва без разрушения блоков", true));
        ENCHANTS.put("Дровосек",   new EnchantInfo("Дровосек",   "+30% добычи с дерева", true));
        ENCHANTS.put("Буря",       new EnchantInfo("Буря",       "Молния при ударе", true));
        ENCHANTS.put("Магнит",     new EnchantInfo("Магнит",     "Притягивает предметы", true));
        ENCHANTS.put("Удача",      new EnchantInfo("Удача",      "+10% к шансу лута", true));
        ENCHANTS.put("Разведчик",  new EnchantInfo("Разведчик",  "Показывает руды поблизости", true));
        // Не подтверждены — в конфиге pending (не выдуманы)
        ENCHANTS.put("Оживление",  new EnchantInfo("Оживление",  "Эффект уточнить (pending)", false));
        ENCHANTS.put("Снайпер",    new EnchantInfo("Снайпер",    "Эффект уточнить (pending)", false));
        ENCHANTS.put("Лингер",     new EnchantInfo("Лингер",     "Эффект уточнить (pending)", false));
        ENCHANTS.put("Вероятность III", new EnchantInfo("Вероятность III", "Точное название уточнить (pending)", false));
        ENCHANTS.put("Неразрушимость просы", new EnchantInfo("Неразрушимость просы", "Точное название уточнить (pending)", false));
    }

    public static String rankColor(String rank) {
        RankInfo r = RANKS.get(rank);
        return r == null ? "§f" : "§" + r.color.getChar();
    }
    /** Все наследованные ранги. */
    public static List<String> inherited(String rank) {
        List<String> out = new ArrayList<>(); List<String> cur = new ArrayList<>(List.of(rank));
        while (!cur.isEmpty()) {
            String r = cur.remove(0); if (out.contains(r)) continue; out.add(r);
            RankInfo info = RANKS.get(r); if (info != null) cur.addAll(info.inherits);
        }
        return out;
    }
    public static int priority(String rank) {
        RankInfo r = RANKS.get(rank); return r == null ? 0 : r.priority;
    }
}
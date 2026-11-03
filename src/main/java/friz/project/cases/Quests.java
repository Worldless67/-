package friz.project.cases;

import friz.project.core.*;
import java.util.*;

/** Задания: ежедневные, добыча, мобы, игровые. Награды FK и предметами. Прогресс в PlayerData. */
public class Quests {
    public static class Quest { public final String id, name, type; public final int target; public final int rewardFk;
        public Quest(String id, String name, String type, int target, int fk) { this.id=id; this.name=name; this.type=type; this.target=target; this.rewardFk=fk; } }
    private static final List<Quest> ALL = new ArrayList<>();
    static {
        ALL.add(new Quest("daily_mine","Ежедневная добыча","добыча",64,50));
        ALL.add(new Quest("daily_mobs","Ежедневное истребление","мобы",10,40));
        ALL.add(new Quest("daily_game","Ежедневная игровая задача","игровая",1,30));
        ALL.add(new Quest("mine_stone","Добыча камня","добыча",100,30));
        ALL.add(new Quest("mobs_kill","Убийство мобов","мобы",25,60));
        ALL.add(new Quest("game_duel","Дуэли","игровая",3,80));
    }
    public static List<Quest> byType(String type) {
        List<Quest> out = new ArrayList<>();
        for (Quest q : ALL) if (q.type.equals(type)) out.add(q);
        return out;
    }
    /** Прогресс задания. */
    public static int progress(PlayerData d, Quest q) { return d.questProgress.getOrDefault(q.id, 0); }
    public static void addProgress(String player, PlayerData d, Quest q, int amount, PlayerDataManager mgr) {
        int cur = progress(d, q) + amount;
        d.questProgress.put(q.id, Math.min(cur, q.target));
        PlayerData.Manager.save(player, d);
        if (d.questProgress.get(q.id) >= q.target && !d.caseHistory.contains("quest_done_" + q.id)) {
            Economy.setFk(player, Economy.getFk(player) + q.rewardFk);
        }
    }
    public static List<Quest> all() { return ALL; }
}
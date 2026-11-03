package friz.project.core;

import java.util.*;

/** Проверка прав: ранги с наследованием + права персонала. */
public class Perm {
    public interface Checkable { boolean has(String perm); }

    /** Проверить право игрока: по рангу или напрямую. */
    public static boolean check(String rank, Set<String> directPerms, String perm) {
        if (directPerms != null && directPerms.contains(perm)) return true;
        for (String r : Core.inherited(rank)) {
            if (r.equals("Создатель")) return true;
        }
        // Должности дают права — обрабатываются менеджером персонала
        return false;
    }
    public static boolean jobHas(String job, String perm) {
        String[] perms = Core.JOBS.get(job);
        if (perms == null) return false;
        for (String p : perms) { if (p.equals("*") || p.equals(perm)) return true; }
        return false;
    }
}
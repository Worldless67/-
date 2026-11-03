package friz.project.core;

/** Менеджер данных игроков: обёртка над PlayerData.Manager. */
public class PlayerDataManager {

    public static PlayerData load(String name) {
        return PlayerData.Manager.load(name);
    }

    public static void save(String name, PlayerData d) {
        PlayerData.Manager.save(name, d);
    }
}

package friz.project.core;

/** Менеджер данных игроков: обёртка над PlayerData.Manager. */
public class PlayerDataManager {

    public PlayerData load(String name) {
        return PlayerData.Manager.load(name);
    }

    public void save(String name, PlayerData d) {
        PlayerData.Manager.save(name, d);
    }
}

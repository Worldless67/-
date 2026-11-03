package friz.project.core;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.util.*;

/** Данные игрока: маскировка, кулдауны, прогресс заданий, ключи, журнал кейсов. */
public class PlayerData {
    public String rank = "Игрок";
    public String job = null;
    public Set<String> directPerms = new HashSet<>();
    public String nickColor = null;
    public String realNick = null;
    public String disguiseRank = null;
    public boolean disguiseOn = false;
    public Map<String, Long> kitCooldowns = new HashMap<>();
    public Map<String, Integer> questProgress = new HashMap<>();
    public List<String> keyItems = new ArrayList<>();
    public List<String> caseHistory = new ArrayList<>();
    public List<String> mail = new ArrayList<>();
    public List<String> warns = new ArrayList<>();
    public int mutes = 0;

    public static class Manager {
        public static Manager INSTANCE = new Manager();
        private static final Yaml yaml = new Yaml();
        private static final File DIR = new File("players");
        public static void init() { DIR.mkdirs(); }
        public static PlayerData load(String name) {
            File f = new File(DIR, name.toLowerCase() + ".yml");
            if (!f.exists()) return new PlayerData();
            try (Reader r = new FileReader(f)) { return yaml.loadAs(r, PlayerData.class); }
            catch (Exception e) { return new PlayerData(); }
        }
        public static void save(String name, PlayerData d) {
            try (Writer w = new FileWriter(new File(DIR, name.toLowerCase() + ".yml"))) {
                DumperOptions o = new DumperOptions(); o.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                new Yaml(o).dump(d, w);
            } catch (Exception ignored) { }
        }
    }
}

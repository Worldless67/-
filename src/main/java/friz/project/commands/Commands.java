package friz.project.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import friz.project.catalog.Catalog;
import friz.project.core.*;
import friz.project.disguise.Disguise;
import friz.project.kits.Kits;
import friz.project.mechanics.Consumables;
import friz.project.mechanics.Blocks;
import friz.project.services.Services;
import friz.project.shops.*;
import friz.project.cases.Cases;
import friz.project.cases.Quests;
import friz.project.world.*;
import java.util.*;

/** Все команды в одном регистрируемом классе (Nukkit регистрирует по plugin.yml). */
public class Commands {

    public static boolean handle(CommandSender sender, String cmd, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Только для игроков."); return true; }
        PlayerData d = PlayerDataManager.load(p.getName());
        String rank = d.rank;
        boolean isCreator = rank.equals("Создатель");
        boolean isAdmin = Perm.check(rank, d.directPerms, "friz.admin") || isCreator;
        boolean isModer = isAdmin || Perm.check(rank, d.directPerms, "friz.moder");
        boolean isHelper = isModer || Perm.check(rank, d.directPerms, "friz.helper");

        switch (cmd) {
            case "bal" -> Msg.send(p, "Баланс: " + (int) Economy.getMoney(p.getName()) + " | FK: " + Economy.getFk(p.getName()));
            case "pay" -> { if (args.length < 2) return true; Player t = p.getServer().getPlayer(args[0]);
                if (t == null) { Msg.error(p, "Игрок не найден."); return true; }
                try { double amt = Double.parseDouble(args[1]);
                    if (Economy.takeMoney(p.getName(), amt)) Economy.addMoney(t.getName(), amt); } catch (Exception ignored) {} }
            case "home" -> Msg.send(p, "Дома: /home set, /home go, /home del, /home list");
            case "tpa" -> { if (args.length < 1) return true; Msg.send(p, "Запрос телепорта отправлен."); }
            case "msg" -> { if (args.length < 2) return true; Player t = p.getServer().getPlayer(args[0]);
                if (t != null) t.sendMessage("[ЛС] " + p.getName() + ": " + String.join(" ", Arrays.copyOfRange(args,1,args.length))); }
            case "shop" -> Msg.send(p, "Магазин: /shop buy <предмет> <кол-во> | /shop sell <предмет> <кол-во>");
            case "donatshop" -> {
                StringBuilder sb = new StringBuilder("Донат-магазин:\n");
                for (String c : DonatShop.categories()) sb.append(c).append(", ");
                Msg.send(p, sb.toString());
                Msg.send(p, "Покупка: /donatshop buy <id>.");
            }
            case "auction" -> {
                if (args.length == 0) { Msg.send(p, "Аукцион: /auction list, /auction sell <цена>"); return true; }
                if (args[0].equalsIgnoreCase("list")) { int i = 0; for (Auction.Lot l : Auction.listLots()) { Msg.send(p, i + ". " + l.item.getCustomName() + " — " + l.price); i++; } }
                if (args[0].equalsIgnoreCase("sell") && args.length > 1) { try { double price = Double.parseDouble(args[1]); if (Auction.list(p, price)) Msg.ok(p, "Лот выставлен."); } catch (Exception ignored) {} }
            }
            case "kit" -> { if (args.length < 1) { Msg.send(p, "Наборы: " + String.join(", ", Kits.names())); return true; }
                Kits.Kit k = Kits.get(args[0]);
                if (k == null) { Msg.error(p, "Набор не найден."); return true; }
                if (Kits.claim(p, k, d, friz.project.core.PlayerData.Manager.load(p.getName()))) Msg.ok(p, "Набор выдан."); }
            case "cases" -> { if (args.length < 1) { Msg.send(p, "Кейсы: Обычный, Редкий, Донат. /cases open <тип>"); return true; }
                if (args[0].equalsIgnoreCase("open") && args.length > 1) { Cases.open(p, args[1], d, friz.project.core.PlayerData.Manager.load(p.getName())); } }
            case "quests" -> { StringBuilder sb = new StringBuilder("Задания:\n");
                for (Quests.Quest q : Quests.all()) sb.append(q.name).append(" (").append(Quests.progress(d, q)).append("/").append(q.target).append(")\n");
                Msg.send(p, sb.toString()); }
            case "stash" -> { for (Item i : Stash.get(p.getName())) { if (i.getId() != Item.AIR) p.getInventory().addItem(i); }
                Stash.clear(p.getName()); Msg.ok(p, "Хранилище выдачи выдано."); }
            case "vote" -> Msg.send(p, "Голосование: /vote <тема> — открыто.");
            case "report" -> { if (args.length < 1) return true; Services.report(p.getName(), String.join(" ", args)); Msg.ok(p, "Жалоба отправлена."); }
            case "helpop" -> { if (args.length < 1) return true; Services.helpop(p.getName(), String.join(" ", args)); Msg.ok(p, "Вопрос отправлен."); }
            case "mail" -> { for (String m : Services.mailOf(p.getName())) Msg.send(p, m); }
            case "party" -> Msg.send(p, "Партия: /party invite <ник>, /party leave");
            case "trade" -> { if (args.length < 1) return true; Services.tradeRequests.put(p.getName(), args[0]); Msg.send(p, "Запрос обмена отправлен."); }
            case "duel" -> { if (args.length < 1) return true; Services.duelRequests.put(p.getName(), args[0]); Msg.send(p, "Вызов на дуэль отправлен."); }
            case "spawn" -> p.teleport(p.getLevel().getSpawnLocation());
            case "nick" -> {
                if (args.length == 0) { Msg.send(p, "/nick <новый ник> | /nick reset | /nick color <цвет> | /nick status"); return true; }
                if (args[0].equalsIgnoreCase("reset")) { d.nickColor = null; Disguise.reset(p.getName()); PlayerDataManager.save(p.getName(), d); Msg.ok(p, "Сброшено."); return true; }
                if (args[0].equalsIgnoreCase("color")) { if (args.length > 1) { d.nickColor = args[1]; PlayerDataManager.save(p.getName(), d); Msg.ok(p, "Цвет ника изменён."); } return true; }
                if (args[0].equalsIgnoreCase("status")) { Msg.send(p, "Ник: " + Disguise.displayNick(p.getName()) + " | Ранг: " + Disguise.displayRank(p.getName(), d.rank)); return true; }
                Disguise.setNick(p.getName(), args[0]); Msg.ok(p, "Ник изменён.");
            }
            case "disguise" -> {
                if (!p.hasPermission("friz.donate")) { Msg.error(p, "Нужен донат-ранг."); return true; }
                if (args.length == 0) { Msg.send(p, "/disguise on|off|nick <ник>|rank <ранг>|reset|show"); return true; }
                switch (args[0].toLowerCase()) {
                    case "on" -> { d.disguiseOn = true; PlayerDataManager.save(p.getName(), d); Msg.ok(p, "Маскировка включена."); }
                    case "off" -> { d.disguiseOn = false; Disguise.reset(p.getName()); PlayerDataManager.save(p.getName(), d); Msg.ok(p, "Маскировка выключена."); }
                    case "nick" -> { if (args.length > 1) { Disguise.setNick(p.getName(), args[1]); Msg.ok(p, "Ник маскировки: " + args[1]); } }
                    case "rank" -> { if (args.length > 1) { Disguise.setRank(p.getName(), args[1]); Msg.ok(p, "Ранг маскировки: " + args[1]); } }
                    case "reset" -> { Disguise.reset(p.getName()); Msg.ok(p, "Сброшено."); }
                    case "show" -> { boolean canSee = isCreator || d.directPerms.contains("friz.disguise.see");
                        Msg.send(p, canSee ? "Настоящий ник: " + d.realNick + " | Ранг: " + d.rank : "Настоящую личность видят только админ и Создатель."); }
                }
            }
            case "realname" -> {
                if (!isHelper && !isCreator) return true;
                if (args.length < 1) return true;
                Msg.send(p, "Настоящий ник " + args[0] + ": " + (Disguise.isDisguised(args[0]) ? args[0] + " (маскировка)" : args[0]));
            }
            case "warn" -> { if (!isHelper || args.length < 1) return true;
                Msg.ok(p, Services.warn(p.getName(), args[0])); }
            case "mute" -> { if (!isHelper || args.length < 2) return true;
                try { Services.mute(p.getName(), args[0], Integer.parseInt(args[1])); Msg.ok(p, "Замучен."); } catch (Exception e) {} }
            case "kick" -> { if (!isHelper || args.length < 1) return true; Services.kick(p.getName(), args[0], args.length > 2 ? args[1] : "Нарушение");
                Player t = p.getServer().getPlayer(args[0]); if (t != null) t.kick("Кик: " + (args.length > 2 ? args[1] : "нарушение")); }
            case "tempban" -> { if (!isModer || args.length < 2) return true;
                try { Services.tempban(p.getName(), args[0], Integer.parseInt(args[1])); Msg.ok(p, "Забанен."); } catch (Exception e) {} }
            case "freeze" -> { if (!isModer || args.length < 1) return true;
                Services.freeze(p.getName(), args[0], !Services.frozen.containsKey(args[0]));
                Msg.ok(p, Services.frozen.containsKey(args[0]) ? "Заморожен." : "Разморожен."); }
            case "rank" -> { if (!isAdmin || args.length < 2) return true;
                d.rank = args[1]; PlayerDataManager.save(args[0], d); Msg.ok(p, "Ранг изменён."); }
            case "givemoney" -> { if (!isAdmin || args.length < 2) return true;
                try { Economy.addMoney(args[0], Double.parseDouble(args[1])); Msg.ok(p, "Выдано."); } catch (Exception e) {} }
            case "givefk" -> { if (!isAdmin || args.length < 2) return true;
                try { Economy.setFk(args[0], Economy.getFk(args[0]) + Integer.parseInt(args[1])); Msg.ok(p, "Выдано."); } catch (Exception e) {} }
            case "event" -> { if (!isAdmin) return true;
                Msg.send(p, "События: /event mystic, /event beacon, /event flower, /event volcano"); }
            case "mine" -> { if (!isAdmin) return true;
                Msg.send(p, "Шахта: ярус " + AutoMine.currentTier() + ", до сброса " + (AutoMine.millisToReset()/1000) + "с"); }
            case "pending" -> { if (!isAdmin) return true;
                Msg.send(p, "Неуточнённые данные: смотри config.yml блок pending."); }
            case "backup" -> { if (!isAdmin) return true; friz.project.tasks.Tasks.backupNow(); Msg.ok(p, "Бэкап создан."); }
            case "npc" -> { if (!isAdmin || args.length < 2) return true;
                if (args[0].equalsIgnoreCase("spawn")) { friz.project.npc.NpcManager.spawn(args[1], p); } }
            case "creator" -> creatorCommands(p, args, d, isCreator);
        }
        return true;
    }

    static void creatorCommands(Player p, String[] args, PlayerData d, boolean isCreator) {
        if (!isCreator) { Msg.error(p, "Только Создатель."); return; }
        if (args.length == 0) { Msg.send(p, "Справочник: /creator help"); return; }
        switch (args[0].toLowerCase()) {
            case "give" -> { // /creator give <ник> <предмет> <количество> — без кулдаунов и лимитов
                if (args.length < 3) { Msg.error(p, "/creator give <ник> <предмет> <кол-во>"); return; }
                String target = args[1], itemRef = args[2];
                int amount = 1; try { if (args.length > 3) amount = Integer.parseInt(args[3]); } catch (Exception e) {}
                Catalog.Entry e = Catalog.byId(itemRef); if (e == null) e = Catalog.byName(itemRef);
                if (e == null) { Msg.error(p, "Предмет не найден: " + itemRef); return; }
                Player t = p.getServer().getPlayer(target);
                if (t == null) { Msg.error(p, "Игрок не найден."); return; }
                int notFit = 0;
                for (int i=0;i<amount;i++) { if (friz.project.core.Stash.giveSafe(t, Catalog.create(e, 1)) > 0) notFit++; }
                Msg.ok(p, "Выдано: " + (amount - notFit) + " x " + e.name + (notFit > 0 ? " (остаток " + notFit + " в stash)" : ""));
            }
            case "giveall" -> {
                if (args.length < 2) { Msg.error(p, "/creator giveall <ник> [категория]"); return; }
                String target = args[1], cat = args.length > 2 ? args[2] : null;
                Player t = p.getServer().getPlayer(target);
                if (t == null) { Msg.error(p, "Игрок не найден."); return; }
                java.util.List<Catalog.Entry> list = cat == null ? Catalog.all() : Catalog.group(cat);
                for (Catalog.Entry e : list) friz.project.core.Stash.giveSafe(t, Catalog.create(e, 1));
                Msg.ok(p, "Выдано предметов: " + list.size());
            }
            case "sphere" -> { if (args.length < 3) { Msg.error(p, "/creator sphere give <ник> <название>"); return; }
                Catalog.Entry e = Catalog.byName(args[2]);
                Player t = p.getServer().getPlayer(args[1]);
                if (e != null && t != null) { friz.project.core.Stash.giveSafe(t, Catalog.create(e, 1)); Msg.ok(p, "Сфера выдана."); } }
            case "talisman" -> { if (args.length < 3) { Msg.error(p, "/creator talisman give <ник> <название>"); return; }
                Catalog.Entry e = Catalog.byName(args[2]);
                Player t = p.getServer().getPlayer(args[1]);
                if (e != null && t != null) { friz.project.core.Stash.giveSafe(t, Catalog.create(e, 1)); Msg.ok(p, "Талисман выдан."); } }
            case "iteminfo" -> { Item hand = p.getInventory().getItemInHand();
                if (hand.getId() == Item.AIR) { Msg.send(p, "В руке пусто."); return; }
                Msg.send(p, "ID: " + hand.getId() + ", метаданные: " + hand.getDamage() + ", NBT: " + hand.getNamedTag()); }
            case "itemedit" -> Msg.send(p, "Редактор: /creator itemedit <свойство> <значение> — применяет к предмету в руке.");
            case "help" -> Msg.send(p, "/creator give|giveall|sphere|talisman|iteminfo|itemedit — без кулдаунов.");
        }
    }
}

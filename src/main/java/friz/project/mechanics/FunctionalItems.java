package friz.project.mechanics;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Explosion;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import java.util.*;

/** Расчёт урона и эффекты уникальных зачарований. */
public class FunctionalItems {
    private static final Map<String, Integer> oxidationStacks = new HashMap<>();

    /** Итоговый урон = база + friz_damage + носитель в левой руке + острота. */
    public static int calculateDamage(Player attacker, Entity target) {
        Item main = attacker.getInventory().getItemInHand();
        CompoundTag tag = main.getNamedTag();
        int dmg = 5; // база
        if (tag != null && tag.contains("friz_damage")) dmg += tag.getInt("friz_damage");
        Item off = attacker.getOffhandInventory().getItem(0);
        if (off != null && off.getId() == Item.TOTEM) dmg += 2; // носитель в левой руке
        int sharp = main.getEnchantment(9) != null ? main.getEnchantment(9).getLevel() : 0; // 9 = SHARPNESS
        dmg += sharp; // 1 уровень остроты = 1 урон
        return dmg;
    }

    /** Эффекты зачарований после удара. */
    public static void applyOnHit(Player attacker, Entity target, EntityDamageByEntityEvent ev) {
        Item main = attacker.getInventory().getItemInHand();
        CompoundTag tag = main.getNamedTag();
        if (tag == null) return;
        // Вампиризм: 25% урона возвращается здоровьем
        if (tag.contains("ench_vampire")) {
            double heal = ev.getFinalDamage() * 0.25;
            attacker.setHealth((float) Math.min(attacker.getMaxHealth(), attacker.getHealth() + heal));
        }
        // Яд: после 2 удара подряд
        if (tag.contains("ench_poison")) {
            int hits = oxidationStacks.getOrDefault("poison_" + attacker.getName(), 0) + 1;
            oxidationStacks.put("poison_" + attacker.getName(), hits);
            if (hits >= 2 && target instanceof Player tp) {
                Effect e = Effect.getEffect(Effect.POISON); if (e != null) tp.addEffect(e.setDuration(60).setAmplifier(0));
                oxidationStacks.remove("poison_" + attacker.getName());
            }
        }
        // Окисление: 2/4/6 секунд замедления по стакам
        if (tag.contains("ench_oxidation")) {
            int stacks = oxidationStacks.getOrDefault("ox_" + target.getId(), 0) + 1;
            oxidationStacks.put("ox_" + target.getId(), Math.min(stacks, 3));
            int secs = stacks * 2;
            if (target instanceof Player tp) {
                Effect e = Effect.getEffect(Effect.SLOWNESS); if (e != null) tp.addEffect(e.setDuration(secs * 20));
            }
        }
        // Стан: заморозка цели на 2 с
        if (tag.contains("ench_stun") && target instanceof Player tp) {
            tp.setMotion(tp.getMotion().multiply(0));
            Effect e = Effect.getEffect(Effect.SLOWNESS);
            if (e != null) tp.addEffect(e.setDuration(40).setAmplifier(6));
        }
        // Притяжение: тянуть цель к игроку
        if (tag.contains("ench_pull") && target instanceof Player tp) {
            tp.setMotion(attacker.getDirectionVector().multiply(1.2));
        }
        // Подрывник: 20% шанс подрыва без разрушения блоков
        if (tag.contains("ench_bomb") && new Random().nextInt(100) < 20) {
            new Explosion(target, 2.0, attacker).explodeB(); // урон и отброс есть, блоки целы
        }
        // Буря: молния
        if (tag.contains("ench_storm")) {
            EntityLightning bolt = new EntityLightning(target.chunk, Entity.getDefaultNBT(target));
            bolt.spawnToAll();
        }
    }

    public static void clearStacks(Player p) { oxidationStacks.remove("poison_" + p.getName()); }
}

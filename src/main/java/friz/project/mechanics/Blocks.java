package friz.project.mechanics;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import java.util.*;

/** TempBlocks: восстановление, трапка 3x3x3 (25% телепорт), пласт (20с/60с). */
public class Blocks {
    public static class TempBlock { public final Position pos; public final int id; public final long removeAt;
        public TempBlock(Position p, int id, long t) { pos=p; this.id=id; removeAt=t; } }
    public static final List<TempBlock> tempBlocks = new ArrayList<>();

    public static void addTemp(Position pos, int id, int seconds) {
        tempBlocks.add(new TempBlock(pos, id, System.currentTimeMillis() + seconds * 1000L));
    }
    /** Снятие TempBlocks (вызывается из задач). */
    public static void tickTemp() {
        long now = System.currentTimeMillis();
        tempBlocks.removeIf(tb -> {
            if (now >= tb.removeAt) {
                tb.pos.getLevel().setBlock(tb.pos, Block.get(Block.AIR), true, true);
                return true;
            }
            return false;
        });
    }
    /** Трапка: стеклянная коробка 3x3x3 вокруг игрока; 25% шанс телепорта вверх. */
    public static void trap(Player p) {
        int cx = p.getFloorX(), cy = p.getFloorY(), cz = p.getFloorZ();
        for (int x=-1;x<=1;x++) for(int y=0;y<=2;y++) for(int z=-1;z<=1;z++) {
            Position pos = p.add(x,y,z).clone();
            pos.getLevel().setBlock(pos, Block.get(Block.GLASS), true, true);
            addTemp(pos, Block.GLASS, 20);
        }
        if (new Random().nextInt(100) < 25) p.setMotion(new Vector3(0, 1.4, 0)); // телепорт вверх
    }
    /** Пласт: вертикальный 20с или горизонтальный 60с. */
    public static void plast(Player p, boolean vertical) {
        int cx = p.getFloorX(), cy = p.getFloorY(), cz = p.getFloorZ();
        int secs = vertical ? 20 : 60;
        if (vertical) {
            for (int y=0; y<5; y++) { Position pos = p.add(0,y,0).clone(); pos.getLevel().setBlock(pos, Block.get(Block.STONE), true, true); addTemp(pos, Block.STONE, secs); }
        } else {
            for (int x=-2;x<=2;x++) for(int z=-2;z<=2;z++) {
                Position pos = p.add(x,0,z).clone();
                pos.getLevel().setBlock(pos, Block.get(Block.STONE), true, true);
                addTemp(pos, Block.STONE, secs);
            }
        }
    }
}
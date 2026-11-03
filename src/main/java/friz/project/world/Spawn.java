package friz.project.world;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import java.util.*;

/** Спавн-карта FM: платформа, логотип FM из золота, фонтан, здания, пьедесталы кейсов, арена. */
public class Spawn {
    public static void build(Level level, int cx, int cy, int cz) {
        // Платформа 21x21
        for (int x=-10;x<=10;x++) for(int z=-10;z<=10;z++) {
            level.setBlock(cx+x, cy-1, cz+z, 0, Block.get(Block.STONE_BRICKS), true, true);
        }
        // Логотип FM из золота
        int[][] fm = {{1,1,1,0,1,1,1},{1,0,0,0,1,0,1},{1,1,0,0,1,1,1},{1,0,0,0,1,0,0},{1,0,0,0,1,0,0}};
        for (int r=0;r<fm.length;r++) for(int c=0;c<fm[r].length;c++) {
            if (fm[r][c] == 1) level.setBlock(cx-3+c, cy, cz-2+r, 0, Block.get(Block.GOLD_BLOCK), true, true);
        }
        // Фонтан (круг диаметром 5)
        for (int dx=-2;dx<=2;dx++) for(int dz=-2;dz<=2;dz++) {
            if (dx*dx+dz*dz <= 5) level.setBlock(cx+dx, cy, cz+8+dz, 0, Block.get(Block.WATER), true, true);
        }
        for (int dx=-3;dx<=3;dx++) for(int dz=-3;dz<=3;dz++) {
            if (dx*dx+dz*dz == 9) level.setBlock(cx+dx, cy, cz+8+dz, 0, Block.get(Block.STONE_BRICKS), true, true);
        }
        // Здания (4 угла)
        int[][] shops = {{-8,-8},{8,-8},{-8,8},{8,8}};
        for (int[] s : shops) {
            for (int dx=0;dx<4;dx++) for(int dz=0;dz<4;dz++) for(int dy=0;dy<4;dy++) {
                boolean wall = dx==0 || dx==3 || dz==0 || dz==3 || dy==3;
                if (wall) level.setBlock(cx+s[0]+dx, cy+dy, cz+s[1]+dz, 0, Block.get(Block.BRICKS), true, true);
            }
        }
        // Пьедесталы кейсов: кейсы на даймонд-блоках (3 типа как на скрине)
        String[] caseIds = {"Обычный", "Редкий", "Донат"};
        for (int i=0;i<3;i++) {
            int px = cx - 4 + i * 4;
            level.setBlock(px, cy, cz-6, 0, Block.get(Block.DIAMOND_BLOCK), true, true);
            level.setBlock(px, cy+1, cz-6, 0, Block.get(Block.CHEST), true, true);
        }
        // Арена (8x8 площадка)
        for (int x=-4;x<=4;x++) for(int z=12;z<=20;z++) {
            level.setBlock(cx+x, cy-1, cz+z, 0, Block.get(Block.STONE_BRICKS), true, true);
        }
    }
}
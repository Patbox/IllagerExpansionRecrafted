package eu.pb4.illagerexpansion.util.spellutil;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;

import java.util.Random;

public class TeleportUtil {

    private boolean teleportTo(LivingEntity entity, double x, double y, double z) {
        boolean doTeleport = entity.teleport(x, y, z, false);
        return doTeleport;
    }

    private boolean randomTeleport(LivingEntity entity) {
        int randvaluex = -10 + new Random().nextInt(21);
        int randvaluey = -6 + new Random().nextInt(13);
        int randvaluez = -10 + new Random().nextInt(21);
        double x = entity.getX() + randvaluex;
        double y = entity.getY() + randvaluey;
        double z = entity.getZ() + randvaluez;
        while ((randvaluex <= 9 && randvaluex >= -9 && randvaluez <= 9 && randvaluez >= -9)) {
            randvaluex = -10 + new Random().nextInt(21);
            randvaluez = -10 + new Random().nextInt(21);
            x = entity.getX() + randvaluex;
            z = entity.getZ() + randvaluez;
        }
         return teleportTo(entity, x, y, z);
    }
    public boolean doRandomTeleport(LivingEntity entity) {
        for (int i = 0; i < 64; ++i) {
            if (!this.randomTeleport(entity)) continue;
            return true;
        }
        return false;
    }



    private boolean badBlock(Block block) {
        return block == Blocks.AIR || block == Blocks.LAVA || block == Blocks.FIRE;
    }

    public void tick() {
        System.out.println("TICKING TP!!!!___!_!_!_");
    }
}
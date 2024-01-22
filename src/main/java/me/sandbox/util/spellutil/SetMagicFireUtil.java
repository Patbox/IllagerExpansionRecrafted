package me.sandbox.util.spellutil;

import me.sandbox.block.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SetMagicFireUtil {


    public void setFire(LivingEntity entity, World world) {
        BlockPos entitylocation = entity.getBlockPos();
        BlockPos downLocation = entitylocation.down();
        Block blockBelow = world.getBlockState(downLocation).getBlock();
        if (goodBlock(blockBelow)) {
            return;
        }
        for (BlockPos blockradius : BlockPos.iterateOutwards(entitylocation, 1,1, 1)) {
            Block blockInRadius = world.getBlockState(blockradius).getBlock();
            if (goodBlock(blockInRadius)) {
                world.setBlockState(blockradius, BlockRegistry.MAGIC_FIRE.getDefaultState());
            }
        }

    }
    private boolean goodBlock(Block block) {
        return block == Blocks.AIR || block == Blocks.SHORT_GRASS || block == Blocks.FERN || block ==Blocks.TALL_GRASS;
    }
}

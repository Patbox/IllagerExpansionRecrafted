package eu.pb4.illagerexpansion.util.spellutil;

import eu.pb4.illagerexpansion.block.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class SetMagicFireUtil {


    public void setFire(LivingEntity entity, Level world) {
        BlockPos entitylocation = entity.blockPosition();
        BlockPos downLocation = entitylocation.below();
        Block blockBelow = world.getBlockState(downLocation).getBlock();
        if (goodBlock(blockBelow)) {
            return;
        }
        for (BlockPos blockradius : BlockPos.withinManhattan(entitylocation, 1,1, 1)) {
            Block blockInRadius = world.getBlockState(blockradius).getBlock();
            if (goodBlock(blockInRadius)) {
                world.setBlockAndUpdate(blockradius, BlockRegistry.MAGIC_FIRE.defaultBlockState());
            }
        }

    }
    private boolean goodBlock(Block block) {
        return block == Blocks.AIR || block == Blocks.SHORT_GRASS || block == Blocks.FERN || block ==Blocks.TALL_GRASS;
    }
}

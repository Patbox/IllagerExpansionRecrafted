package eu.pb4.illagerexpansion.block.custom;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class MagicFireBlock extends AbstractFireBlock implements PolymerBlock {
    public MagicFireBlock(Settings settings, float damage) {
        super(settings.nonOpaque(), 0.0f);
    }

    @Override
    protected MapCodec<? extends AbstractFireBlock> getCodec() {
        return null;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
            if (!(entity instanceof IllagerEntity || entity instanceof RavagerEntity) ) {
                entity.damage(world.getDamageSources().magic(), 3.0f);
            } else {
                return;
            }
            if (entity.getFireTicks() == 0) {
                entity.setFireTicks(0);
            }
        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.removeBlock(pos, false);
        world.scheduleBlockTick(pos, this, 180);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.scheduleBlockTick(pos, this, 180);
    }
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (this.canPlaceAt(state, world, pos)) {
            return this.getDefaultState();
        }
        return Blocks.AIR.getDefaultState();
    }
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, Direction.UP);
    }

    @Override
    protected boolean isFlammable(BlockState state) {
        return true;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.SOUL_FIRE.getDefaultState();
    }
}


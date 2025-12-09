package eu.pb4.illagerexpansion.block.custom;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.packettweaker.PacketContext;

public class MagicFireBlock extends BaseFireBlock implements PolymerBlock {
    public MagicFireBlock(Properties settings, float damage) {
        super(settings.noOcclusion(), 0.0f);
    }

    @Override
    protected MapCodec<? extends BaseFireBlock> codec() {
        return null;
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl) {
        if (!(entity instanceof AbstractIllager || entity instanceof Ravager) && world instanceof ServerLevel serverWorld ) {
            entity.hurtServer(serverWorld, world.damageSources().magic(), 3.0f);
        } else {
            return;
        }
        if (entity.getRemainingFireTicks() == 0) {
            entity.setRemainingFireTicks(0);
        }
        super.entityInside(state, world, pos, entity, handler, bl);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        world.removeBlock(pos, false);
        world.scheduleTick(pos, this, 180);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        world.scheduleTick(pos, this, 180);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (this.canSurvive(state, world, pos)) {
            return this.defaultBlockState();
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.below();
        return world.getBlockState(blockPos).isFaceSturdy(world, blockPos, Direction.UP);
    }

    @Override
    protected boolean canBurn(BlockState state) {
        return true;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return Blocks.SOUL_FIRE.defaultBlockState();
    }
}


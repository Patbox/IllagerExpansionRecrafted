package me.sandbox.block.custom;

import eu.pb4.polymer.api.block.PolymerHeadBlock;
import eu.pb4.polymer.api.utils.PolymerUtils;
import me.sandbox.gui.ImbuingTableScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ImbuingTableBlock extends Block implements PolymerHeadBlock {
    private static final Text TITLE = Text.literal("Imbue");

    public ImbuingTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (canActivate(pos, world)) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    public boolean canActivate(BlockPos pos, World world) {
        int i = 0;
        BlockPos blockPos = pos.down();
        for (BlockPos blockradius : BlockPos.iterateOutwards(blockPos, 1, 0, 1)) {
            Block blockInRadius = world.getBlockState(blockradius).getBlock();
            if (goodBlock(blockInRadius)) {
                i++;
            }
            if (i == 9) {
                return true;
            }
        }
        return false;
    }
        private boolean goodBlock(Block block) {
            return block == Blocks.COPPER_BLOCK || block == Blocks.CUT_COPPER || block == Blocks.WAXED_COPPER_BLOCK || block == Blocks.WAXED_CUT_COPPER;
        }
    @Override
    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> new ImbuingTableScreenHandler(syncId, inventory), TITLE);
    }
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (canActivate(pos, world)) {
            world.spawnParticles(ParticleTypes.ENCHANT, pos.getX(), pos.getY()+0.8, pos.getZ(), 3, 0.7D, 0.3D, 0.7D, 0.05);
        }
        world.createAndScheduleBlockTick(pos, this, 5);
    }
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.createAndScheduleBlockTick(pos, this, 5);
    }

    @Override
    public String getPolymerSkinValue(BlockState state) {
        return PolymerUtils.NO_TEXTURE_HEAD_VALUE;
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.PLAYER_HEAD;
    }
}
